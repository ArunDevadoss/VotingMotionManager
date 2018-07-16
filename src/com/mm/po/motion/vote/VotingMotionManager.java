package com.mm.po.motion.vote;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mm.po.motion.vote.constant.MotionVotingConstants;
import com.mm.po.motion.vote.domain.Motion;
import com.mm.po.motion.vote.domain.MotionResult;
import com.mm.po.motion.vote.domain.Voter;
import com.mm.po.motion.vote.enums.MotionState;
import com.mm.po.motion.vote.enums.MotionStatus;
import com.mm.po.motion.vote.exception.CloseVotingException;
import com.mm.po.motion.vote.exception.DuplicateVoteException;
import com.mm.po.motion.vote.exception.MaximumVoteOnMotionException;
import com.mm.po.motion.vote.exception.MotionException;
import com.mm.po.motion.vote.exception.VicePresidentVoteException;
import com.mm.po.motion.vote.util.DateTimeUtils;

/**
 * 
 * @author Arun Devadoss
 *
 */
public class VotingMotionManager {

	private Map<Integer, Motion> motionMap = new HashMap<>();

	/**
	 * Current State (Passed/Failed/Tied) on a given Motion
	 * 
	 * @param motionId
	 * @return
	 * @throws MotionException
	 */
	public String getMotionState(final int motionId) throws MotionException {

		Enum<MotionState> motionState = null;

		Motion motion = motionMap.get(motionId);

		if (null != motion) {
			motionState = motion.getMotionState();
		} else {
			throw new MotionException(MotionVotingConstants.MOTION_NOT_CREATED);
		}

		return motionState.toString();
	}

	/**
	 * 
	 * @param motionId
	 * @throws MotionNotCreatedException
	 * @throws CloseVotingException
	 * @throws MotionException
	 */
	public void closeVotingOnMotion(final int motionId) throws CloseVotingException, MotionException {

		Motion motion = motionMap.get(motionId);

		if (null != motion) {

			if (MotionStatus.CLOSED.equals(motion.getMotionStatus())) {
				throw new MotionException(MotionVotingConstants.MOTION_CLOSED);
			} else {
				if (canMotionClosed(motion)) {
					motion.setMotionStatus(MotionStatus.CLOSED);
					motion.setClosedTime(LocalDateTime.now());

				} else {
					throw new CloseVotingException(MotionVotingConstants.MOTION_CANNOT_CLOSED_LESS_THAN_15);
				}
			}

		} else {
			throw new MotionException(MotionVotingConstants.MOTION_NOT_CREATED);
		}

	}

	/**
	 * 
	 * @param motionId
	 * @throws MotionException
	 */
	public MotionResult getMotionResult(final int motionId) throws MotionException {

		Motion motion = motionMap.get(motionId);
		MotionResult motionResult = new MotionResult();

		if (null != motion) {
			motionResult.setMotionState(motion.getMotionState());
			motionResult.setVotingOpenedTime(motion.getOpenedTime());
			motionResult.setVotingClosedTime(motion.getClosedTime());
			motionResult
					.setYesVotecounts(motion.getVoters().stream().filter(m -> m.getVoteState().equals("Y")).count());
			motionResult.setNoVoteCounts(motion.getVoters().stream().filter(m -> m.getVoteState().equals("N")).count());
		} else {
			throw new MotionException(MotionVotingConstants.MOTION_NOT_CREATED);
		}

		return motionResult;

	}

	/**
	 * 
	 * @param motionId
	 * @param voterId
	 * @param voteState
	 * @param isVicePresedent
	 * @throws MaximumVoteOnMotionException
	 * @throws MotionException
	 * @throws DuplicateVoteException
	 * @throws VicePresidentVoteException
	 */
	public void castVotingOnMotion(final int motionId, final int voterId, final String voteState,
			final boolean isVicePresedent)
			throws MaximumVoteOnMotionException, MotionException, DuplicateVoteException, VicePresidentVoteException {

		List<Voter> voters = new ArrayList<>();
		if (!motionMap.isEmpty() && motionMap.size() > 0) {
			if (motionMap.containsKey(motionId)) {
				Motion motion = motionMap.get(motionId);

				if (!MotionStatus.CLOSED.equals(motion.getMotionStatus())) {
					checkVotingConditions(motionId, voterId, voteState, isVicePresedent, voters, motion);
				} else {
					throw new MotionException(MotionVotingConstants.VOTE_CANNOT_CAST);
				}

			}
		} else {

			throw new MotionException(MotionVotingConstants.MOTION_NOT_INITIALIZED);

		}

	}

	/**
	 * 
	 * @param motionId
	 * @param voterId
	 * @param voteState
	 * @param isVicePresedent
	 * @param voters
	 * @param motion
	 * @throws MaximumVoteOnMotionException
	 * @throws MotionException
	 * @throws DuplicateVoteException
	 * @throws VicePresidentVoteException
	 */
	private void checkVotingConditions(final int motionId, final int voterId, final String voteState,
			final boolean isVicePresedent, final List<Voter> voters, final Motion motion)
			throws MaximumVoteOnMotionException, MotionException, DuplicateVoteException, VicePresidentVoteException {

		long voterIds = motion.getVoters().stream().filter(m -> m.getVoterId() == voterId).count();
		if (voterIds == 0) {
			if (isMotionOpenedForVoting(motionId)) {
				motion.setMotionStatus(MotionStatus.OPENED);
				if (!MotionState.TIED.equals(motion.getMotionState())) {
					if (!isVicePresedent) {
						if (isMaximumVoteReceivedOnMotion(motion)) {
							constructVoter(voterId, voteState, voters, motion);

							motionMap.put(motionId, motion);
						} else {
							throw new MaximumVoteOnMotionException(MotionVotingConstants.MAX_VOTES);
						}
					} else {
						throw new VicePresidentVoteException(MotionVotingConstants.VICE_PRESIDENT_VOTE_TIED_STATE);
					}

				} else {

					constructVoter(voterId, voteState, voters, motion);

					closeMotionOnVPVotes(isVicePresedent, motion);
					motionMap.put(motionId, motion);

				}
			} else {
				throw new MotionException(MotionVotingConstants.MOTION_NOT_OPENED + motion.getOpenedTime());
			}

		} else {
			throw new DuplicateVoteException(MotionVotingConstants.DUPLICATE_VOTER);
		}
	}

	/**
	 * 
	 * @param voterId
	 * @param voteState
	 * @param voters
	 * @param motion
	 */
	private void constructVoter(final int voterId, final String voteState, final List<Voter> voters,
			final Motion motion) {
		Voter voter = new Voter();
		voter.setVoterId(voterId);
		voter.setVoteState(voteState);

		voters.add(voter);
		motion.getVoters().addAll(voters);
	}

	/**
	 * 
	 * @param motionId
	 * @throws MotionException
	 */
	public void setMotionState(final int motionId) throws MotionException {

		Motion motion = motionMap.get(motionId);
		if (null != motion) {

			long yesVoteCounts = motion.getVoters().stream().filter(m -> m.getVoteState().equals("Y")).count();
			long noVoteCounts = motion.getVoters().stream().filter(m -> m.getVoteState().equals("N")).count();
			if (yesVoteCounts > noVoteCounts) {
				motion.setMotionState(MotionState.PASSED);
			} else if (yesVoteCounts == noVoteCounts) {
				motion.setMotionState(MotionState.TIED);
			} else {
				motion.setMotionState(MotionState.FAILED);
			}

		} else {

			throw new MotionException(MotionVotingConstants.MOTION_NOT_CREATED);

		}

	}

	/**
	 * 
	 * @param motionId
	 * @return
	 */
	private boolean isMotionOpenedForVoting(final int motionId) {

		LocalDateTime currentTime = LocalDateTime.now();
		return DateTimeUtils.checkTime(currentTime, motionMap.get(motionId).getOpenedTime());
	}

	/**
	 * 
	 * @param motion
	 * @return
	 */
	private boolean isMaximumVoteReceivedOnMotion(final Motion motion) {
		return motion.getVoters().size() < MotionVotingConstants.MAX_VOTES_ALLOWED;
	}

	/**
	 * 
	 * @param motion
	 * @return
	 * @throws CloseVotingException
	 */
	private boolean canMotionClosed(final Motion motion) throws CloseVotingException {

		if (DateTimeUtils.checkTime(LocalDateTime.now(), motion.getOpenedTime())) {
			return DateTimeUtils.durationBetween(motion.getOpenedTime(), LocalDateTime.now())
					.toMinutes() > MotionVotingConstants.MOTION_WINDOW;
		} else {

			throw new CloseVotingException(MotionVotingConstants.MOTION_CANNOT_CLOSED);

		}

	}

	/**
	 * 
	 * @param isVicePresedent
	 * @param motion
	 */
	private void closeMotionOnVPVotes(final boolean isVicePresedent, final Motion motion) {

		if (isVicePresedent) {
			motion.setMotionStatus(MotionStatus.CLOSED);
		} else {
			motion.setMotionState(MotionState.FAILED);
			motion.setMotionStatus(MotionStatus.CLOSED);
		}

	}

	public Map<Integer, Motion> getMotionMap() {
		return motionMap;
	}

	public void setMotionMap(Map<Integer, Motion> motionMap) {
		this.motionMap = motionMap;
	}

}
