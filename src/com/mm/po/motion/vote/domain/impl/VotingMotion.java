package com.mm.po.motion.vote.domain.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.mm.po.motion.vote.constant.MotionVotingConstants;
import com.mm.po.motion.vote.domain.Motion;
import com.mm.po.motion.vote.enums.MotionState;
import com.mm.po.motion.vote.enums.MotionStatus;
import com.mm.po.motion.vote.enums.VoteState;
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
public class VotingMotion implements Motion {

	private LocalDateTime openedTime;
	private LocalDateTime closedTime;
	private Enum<MotionStatus> motionStatus;
	private Enum<MotionState> motionState;
	private long yesVoteCounts;
	private long noVoteCounts;
	private List<Voter> voters = new ArrayList<>();

	/**
	 * 
	 * @param openedtime
	 */
	public VotingMotion(LocalDateTime openedtime) {
		this.openedTime = openedtime;
	}

	/**
	 * @return the openedTime
	 */
	@Override
	public LocalDateTime getOpenedTime() {
		return openedTime;
	}

	/**
	 * @return the closedTime
	 */
	@Override
	public LocalDateTime getClosedTime() {
		return closedTime;
	}

	/**
	 * Current State whether Motion Passed/Failed/Tied.
	 * 
	 * @return the motionStatus
	 */
	@Override
	public Enum<MotionStatus> getMotionStatus() {
		return motionStatus;
	}

	/**
	 * @return the motionState
	 */
	@Override
	public Enum<MotionState> getMotionState() {
		return motionState;
	}

	/**
	 * @return the voters
	 */
	@Override
	public List<Voter> getVoters() {
		return voters;
	}

	/**
	 * @return the yesVotecounts
	 */
	@Override
	public long getYesVoteCounts() {
		return yesVoteCounts;
	}

	/**
	 * @return the noVoteCounts
	 */
	@Override
	public long getNoVoteCounts() {
		return noVoteCounts;
	}

	/**
	 * castVotingOnMotion, performs core voting on motions logic for the given
	 * requirements.
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
	@Override
	public void castVotingOnMotion(final int voterId, final Enum<VoteState> voteState, final boolean isVicePresedent)
			throws MaximumVoteOnMotionException, MotionException, DuplicateVoteException, VicePresidentVoteException {

		// Motion Not Closed, then perform core voting on motion logic.
		if (!MotionStatus.CLOSED.equals(this.getMotionStatus())) {
			checkVotingConditions(voterId, voteState, isVicePresedent);
		} else {
			// Motion already closed, throw exception
			throw new MotionException(MotionVotingConstants.VOTE_CANNOT_CAST);
		}

	}

	/**
	 * Sets Motion state to PASSED/FAILED/TIED Number of Yes votes greater than No
	 * votes, Motion is PASSED. Number of Yes votes less than No votes, Motion is
	 * FAILED. Equal number of Yes and No votes, Motion is Tied
	 * 
	 * @param motionId
	 * @throws MotionException
	 */
	@Override
	public void setMotionState() throws MotionException {

		this.yesVoteCounts = this.getVoters().stream().filter(m -> VoteState.Y.equals(m.getVoteState())).count();
		this.noVoteCounts = this.getVoters().stream().filter(m -> VoteState.N.equals(m.getVoteState())).count();
		if (this.yesVoteCounts > this.noVoteCounts) {
			this.motionState = MotionState.PASSED;
		} else if (this.yesVoteCounts == this.noVoteCounts) {
			this.motionState = MotionState.TIED;
		} else {
			this.motionState = MotionState.FAILED;
		}

	}

	/**
	 * Close Motion on voting once done.
	 * 
	 * @param motionId
	 * @throws MotionNotCreatedException
	 * @throws CloseVotingException
	 * @throws MotionException
	 */
	@Override
	public void closeVotingOnMotion() throws CloseVotingException, MotionException {

		if (MotionStatus.CLOSED.equals(this.getMotionStatus())) {
			throw new MotionException(MotionVotingConstants.MOTION_CLOSED);
		} else {
			// A motion cannot be closed for voting less than 15 minutes after it was
			// opened.
			if (canMotionClose()) {
				this.motionStatus = MotionStatus.CLOSED;
				this.closedTime = LocalDateTime.now();

			} else {
				throw new CloseVotingException(MotionVotingConstants.MOTION_CANNOT_CLOSED_LESS_THAN_15);
			}
		}

	}

	/**
	 * Checks various voting on Motion conditions.
	 * 
	 * Checks for Motion Opened for Voting, Duplicate Voters, Maximum number of
	 * Votes, VP voting conditions
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
	private void checkVotingConditions(final int voterId, final Enum<VoteState> voteState,
			final boolean isVicePresedent)
			throws MaximumVoteOnMotionException, MotionException, DuplicateVoteException, VicePresidentVoteException {

		// A motion must not accept any votes until it is opened for voting. Throw
		// Motion Not Yet Started.
		if (isMotionOpenedForVoting()) {

			long voterIds = this.getVoters().stream().filter(m -> m.getVoterId() == voterId).count();

			// To check duplicate votes, when count more than 0, then the same voter votes
			// for the second time . Throw DuplicateVoteException.
			if (voterIds == 0) {

				this.motionStatus = MotionStatus.OPENED;
				// Cast Votes other than Motion TIED state, as VP can vote only when Motion goes
				// TIED.
				if (!MotionState.TIED.equals(this.getMotionState())) {
					// Throw VicePresidentVoteException, when VP cast votes other than TIED Motion
					// State. VP can only vote during TIED Motion State.
					if (!isVicePresedent) {
						// Check whether maximum number of votes casted, Throw
						// MaximumVoteOnMotionException when number of votes greater than 101.
						if (isMaximumVoteReceivedOnMotion()) {

							Voter voter = new Voter(voterId, voteState);
							voters.add(voter);

						} else {
							throw new MaximumVoteOnMotionException(MotionVotingConstants.MAX_VOTES);
						}
					} else {
						throw new VicePresidentVoteException(MotionVotingConstants.VICE_PRESIDENT_VOTE_TIED_STATE);
					}

				} else {
					// When Motion is "TIED", VP is allowed to vote and Motion CLOSED automatically
					Voter voter = new Voter(voterId, voteState);
					voters.add(voter);

					closeMotionOnVPVotes(isVicePresedent);

				}
			} else {
				throw new DuplicateVoteException(MotionVotingConstants.DUPLICATE_VOTER);
			}
		} else {
			throw new MotionException(MotionVotingConstants.MOTION_NOT_OPENED + this.getOpenedTime());
		}

	}

	/**
	 * true, motion is opened for voting by comparing motion Opened Time and current
	 * time else false.
	 * 
	 * @param motionId
	 * @return
	 */
	private boolean isMotionOpenedForVoting() {

		LocalDateTime currentTime = LocalDateTime.now();
		return DateTimeUtils.checkTime(currentTime, this.getOpenedTime());
	}

	/**
	 * Under Motion TIED VP casts votes
	 * 
	 * @param isVicePresedent
	 * @param motion
	 * @throws MotionException
	 */
	private void closeMotionOnVPVotes(final boolean isVicePresedent) throws MotionException {
		// VP votes Y, Motion PASSED and closed automatically
		// VP votes N, Motion FAILED and closed automatically
		if (isVicePresedent) {
			setMotionState();
			this.motionStatus = MotionStatus.CLOSED;
		} else {
			// VP not available ,Motion FAILED and closed automatically.
			this.motionState = MotionState.FAILED;
			this.motionStatus = MotionStatus.CLOSED;
		}

	}

	/**
	 * Check maximum number of votes casted on a motion. Maximum number of votes is
	 * 101(100 senators plus the Vicepresident).
	 * 
	 * @param motion
	 * @return
	 */
	private boolean isMaximumVoteReceivedOnMotion() {
		return this.getVoters().size() < MotionVotingConstants.MAX_VOTES_ALLOWED;
	}

	/**
	 * Check to close a Motion , when motion is past 15 minutes after opened. Throw
	 * CloseVotingException , on an already CLOSED Motion.
	 * 
	 * @param motion
	 * @return
	 * @throws CloseVotingException
	 */
	private boolean canMotionClose() throws CloseVotingException {

		if (DateTimeUtils.checkTime(LocalDateTime.now(), this.getOpenedTime())) {
			return DateTimeUtils.durationBetween(this.getOpenedTime(), LocalDateTime.now())
					.toMinutes() > MotionVotingConstants.MOTION_WINDOW;
		} else {

			throw new CloseVotingException(MotionVotingConstants.MOTION_CANNOT_CLOSED);

		}

	}

}
