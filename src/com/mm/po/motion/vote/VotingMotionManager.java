//package com.mm.po.motion.vote;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import com.mm.po.motion.vote.constant.MotionVotingConstants;
//import com.mm.po.motion.vote.domain.Motion;
//import com.mm.po.motion.vote.domain.MotionResult;
//import com.mm.po.motion.vote.domain.Voter;
//import com.mm.po.motion.vote.enums.MotionState;
//import com.mm.po.motion.vote.enums.MotionStatus;
//import com.mm.po.motion.vote.enums.VoteState;
//import com.mm.po.motion.vote.exception.CloseVotingException;
//import com.mm.po.motion.vote.exception.DuplicateVoteException;
//import com.mm.po.motion.vote.exception.MaximumVoteOnMotionException;
//import com.mm.po.motion.vote.exception.MotionException;
//import com.mm.po.motion.vote.exception.VicePresidentVoteException;
//import com.mm.po.motion.vote.util.DateTimeUtils;
//
///**
// * 
// * @author Arun Devadoss
// *
// */
//public class VotingMotionManager {
//
//	private Map<Integer, Motion> motionMap = new HashMap<>();
//
//	/**
//	 * Current State whether Motion Passed/Failed/Tied.
//	 * 
//	 * @param motionId
//	 * @return
//	 * @throws MotionException
//	 */
//	public String getMotionState(final int motionId) throws MotionException {
//
//		Enum<MotionState> motionState = null;
//
//		// Get corresponding Motion
//		Motion motion = motionMap.get(motionId);
//
//		if (null != motion) {
//			motionState = motion.getMotionState();
//		} else {
//			throw new MotionException(MotionVotingConstants.MOTION_NOT_CREATED);
//		}
//
//		return motionState.toString();
//	}
//
//	/**
//	 * Close Motion on voting once done.
//	 * 
//	 * @param motionId
//	 * @throws MotionNotCreatedException
//	 * @throws CloseVotingException
//	 * @throws MotionException
//	 */
//	public void closeVotingOnMotion(final int motionId) throws CloseVotingException, MotionException {
//
//		Motion motion = motionMap.get(motionId);
//
//		if (null != motion) {
//
//			if (MotionStatus.CLOSED.equals(motion.getMotionStatus())) {
//				throw new MotionException(MotionVotingConstants.MOTION_CLOSED);
//			} else {
//				// A motion cannot be closed for voting less than 15 minutes after it was
//				// opened.
//				if (canMotionClose(motion)) {
//					motion.setMotionStatus(MotionStatus.CLOSED);
//					motion.setClosedTime(LocalDateTime.now());
//
//				} else {
//					throw new CloseVotingException(MotionVotingConstants.MOTION_CANNOT_CLOSED_LESS_THAN_15);
//				}
//			}
//
//		} else {
//			throw new MotionException(MotionVotingConstants.MOTION_NOT_CREATED);
//		}
//
//	}
//
//	/**
//	 * When a motion is closed for voting, a result is returned that describes 1)
//	 * whether the motion passed or failed 2) the number of yes and no votes 3) the
//	 * time that voting opened and closed
//	 * 
//	 * @param motionId
//	 * @throws MotionException
//	 */
//	public MotionResult getMotionResult(final int motionId) throws MotionException {
//
//		Motion motion = motionMap.get(motionId);
//		MotionResult motionResult = new MotionResult();
//
//		if (null != motion) {
//			motionResult.setMotionState(motion.getMotionState());
//			motionResult.setVotingOpenedTime(motion.getOpenedTime());
//			motionResult.setVotingClosedTime(motion.getClosedTime());
//			motionResult.setYesVotecounts(
//					motion.getVoters().stream().filter(m -> VoteState.Y.equals(m.getVoteState())).count());
//			motionResult.setNoVoteCounts(
//					motion.getVoters().stream().filter(m -> VoteState.N.equals(m.getVoteState())).count());
//		} else {
//			throw new MotionException(MotionVotingConstants.MOTION_NOT_CREATED);
//		}
//
//		return motionResult;
//
//	}
//
//	/**
//	 * castVotingOnMotion, performs core voting on motions logic for the given
//	 * requirements.
//	 * 
//	 * @param motionId
//	 * @param voterId
//	 * @param voteState
//	 * @param isVicePresedent
//	 * @throws MaximumVoteOnMotionException
//	 * @throws MotionException
//	 * @throws DuplicateVoteException
//	 * @throws VicePresidentVoteException
//	 */
//	public void castVotingOnMotion(final int motionId, final int voterId, final Enum<VoteState> voteState,
//			final boolean isVicePresedent)
//			throws MaximumVoteOnMotionException, MotionException, DuplicateVoteException, VicePresidentVoteException {
//
//		List<Voter> voters = new ArrayList<>();
//		if (motionMap.containsKey(motionId)) {
//			Motion motion = motionMap.get(motionId);
//			// Motion Not Closed, then perform core voting on motion logic.
//			if (!MotionStatus.CLOSED.equals(motion.getMotionStatus())) {
//				checkVotingConditions(motionId, voterId, voteState, isVicePresedent, voters, motion);
//			} else {
//				// Motion already closed, throw exception
//				throw new MotionException(MotionVotingConstants.VOTE_CANNOT_CAST);
//			}
//
//		}
//
//	}
//
//	/**
//	 * Checks various voting on Motion conditions.
//	 * 
//	 * Checks for Motion Opened for Voting, Duplicate Voters, Maximum number of
//	 * Votes, VP voting conditions
//	 * 
//	 * @param motionId
//	 * @param voterId
//	 * @param voteState
//	 * @param isVicePresedent
//	 * @param voters
//	 * @param motion
//	 * @throws MaximumVoteOnMotionException
//	 * @throws MotionException
//	 * @throws DuplicateVoteException
//	 * @throws VicePresidentVoteException
//	 */
//	private void checkVotingConditions(final int motionId, final int voterId, final Enum<VoteState> voteState,
//			final boolean isVicePresedent, final List<Voter> voters, final Motion motion)
//			throws MaximumVoteOnMotionException, MotionException, DuplicateVoteException, VicePresidentVoteException {
//
//		// A motion must not accept any votes until it is opened for voting. Throw
//		// Motion Not Yet Started.
//		if (isMotionOpenedForVoting(motionId)) {
//
//			long voterIds = motion.getVoters().stream().filter(m -> m.getVoterId() == voterId).count();
//
//			// To check duplicate votes, when count more than 0, then the same voter votes
//			// for the second time . Throw DuplicateVoteException.
//			if (voterIds == 0) {
//
//				motion.setMotionStatus(MotionStatus.OPENED);
//				// Cast Votes other than Motion TIED state, as VP can vote only when Motion goes
//				// TIED.
//				if (!MotionState.TIED.equals(motion.getMotionState())) {
//					// Throw VicePresidentVoteException, when VP cast votes other than TIED Motion
//					// State. VP can only vote during TIED Motion State.
//					if (!isVicePresedent) {
//						// Check whether maximum number of votes casted, Throw
//						// MaximumVoteOnMotionException when number of votes greater than 101.
//						if (isMaximumVoteReceivedOnMotion(motion)) {
//							constructVoter(voterId, voteState, voters, motion);
//
//							motionMap.put(motionId, motion);
//						} else {
//							throw new MaximumVoteOnMotionException(MotionVotingConstants.MAX_VOTES);
//						}
//					} else {
//						throw new VicePresidentVoteException(MotionVotingConstants.VICE_PRESIDENT_VOTE_TIED_STATE);
//					}
//
//				} else {
//					// When Motion is "TIED", VP is allowed to vote and Motion CLOSED automatically
//					constructVoter(voterId, voteState, voters, motion);
//
//					closeMotionOnVPVotes(isVicePresedent, motion);
//					motionMap.put(motionId, motion);
//
//				}
//			} else {
//				throw new DuplicateVoteException(MotionVotingConstants.DUPLICATE_VOTER);
//			}
//		} else {
//			throw new MotionException(MotionVotingConstants.MOTION_NOT_OPENED + motion.getOpenedTime());
//		}
//
//	}
//
//	/**
//	 * Construct Voter Object for casting votes on a Motion
//	 * 
//	 * @param voterId
//	 * @param voteState
//	 * @param voters
//	 * @param motion
//	 */
//	private void constructVoter(final int voterId, final Enum<VoteState> voteState, final List<Voter> voters,
//			final Motion motion) {
//		Voter voter = new Voter();
//		voter.setVoterId(voterId);
//		voter.setVoteState(voteState);
//
//		voters.add(voter);
//		motion.getVoters().addAll(voters);
//	}
//
//	/**
//	 * Sets Motion state to PASSED/FAILED/TIED Number of Yes votes greater than No
//	 * votes, Motion is PASSED. Number of Yes votes less than No votes, Motion is
//	 * FAILED. Equal number of Yes and No votes, Motion is Tied
//	 * 
//	 * @param motionId
//	 * @throws MotionException
//	 */
//	public void setMotionState(final int motionId) throws MotionException {
//
//		Motion motion = motionMap.get(motionId);
//		if (null != motion) {
//
//			long yesVoteCounts = motion.getVoters().stream().filter(m -> VoteState.Y.equals(m.getVoteState())).count();
//			long noVoteCounts = motion.getVoters().stream().filter(m -> VoteState.N.equals(m.getVoteState())).count();
//			if (yesVoteCounts > noVoteCounts) {
//				motion.setMotionState(MotionState.PASSED);
//			} else if (yesVoteCounts == noVoteCounts) {
//				motion.setMotionState(MotionState.TIED);
//			} else {
//				motion.setMotionState(MotionState.FAILED);
//			}
//
//		} else {
//
//			throw new MotionException(MotionVotingConstants.MOTION_NOT_CREATED);
//
//		}
//
//	}
//
//	/**
//	 * true, motion is opened for voting by comparing motion Opened Time and current
//	 * time else false.
//	 * 
//	 * @param motionId
//	 * @return
//	 */
//	private boolean isMotionOpenedForVoting(final int motionId) {
//
//		LocalDateTime currentTime = LocalDateTime.now();
//		return DateTimeUtils.checkTime(currentTime, motionMap.get(motionId).getOpenedTime());
//	}
//
//	/**
//	 * Check maximum number of votes casted on a motion. Maximum number of votes is
//	 * 101(100 senators plus the Vicepresident).
//	 * 
//	 * @param motion
//	 * @return
//	 */
//	private boolean isMaximumVoteReceivedOnMotion(final Motion motion) {
//		return motion.getVoters().size() < MotionVotingConstants.MAX_VOTES_ALLOWED;
//	}
//
//	/**
//	 * Check to close a Motion , when motion is past 15 minutes after opened. Throw
//	 * CloseVotingException , on an already CLOSED Motion.
//	 * 
//	 * @param motion
//	 * @return
//	 * @throws CloseVotingException
//	 */
//	private boolean canMotionClose(final Motion motion) throws CloseVotingException {
//
//		if (DateTimeUtils.checkTime(LocalDateTime.now(), motion.getOpenedTime())) {
//			return DateTimeUtils.durationBetween(motion.getOpenedTime(), LocalDateTime.now())
//					.toMinutes() > MotionVotingConstants.MOTION_WINDOW;
//		} else {
//
//			throw new CloseVotingException(MotionVotingConstants.MOTION_CANNOT_CLOSED);
//
//		}
//
//	}
//
//	/**
//	 * Under Motion TIED VP casts votes
//	 * 
//	 * @param isVicePresedent
//	 * @param motion
//	 * @throws MotionException
//	 */
//	private void closeMotionOnVPVotes(final boolean isVicePresedent, final Motion motion) throws MotionException {
//		// VP votes Y, Motion PASSED and closed automatically
//		// VP votes N, Motion FAILED and closed automatically
//		if (isVicePresedent) {
//			setMotionState(motion.getMotionId());
//			motion.setMotionStatus(MotionStatus.CLOSED);
//		} else {
//			// VP not available ,Motion FAILED and closed automatically.
//			motion.setMotionState(MotionState.FAILED);
//			motion.setMotionStatus(MotionStatus.CLOSED);
//		}
//
//	}
//
//	public Map<Integer, Motion> getMotionMap() {
//		return motionMap;
//	}
//
//	public void setMotionMap(Map<Integer, Motion> motionMap) {
//		this.motionMap = motionMap;
//	}
//
//}
