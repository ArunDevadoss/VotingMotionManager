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

	/**
	 * Motion Opened Time
	 */
	private LocalDateTime openedTime;
	/**
	 * Motion Closed Time
	 */
	private LocalDateTime closedTime;
	/**
	 * Motion Status - OPENED/CLOSED.
	 */
	private Enum<MotionStatus> motionStatus;
	/**
	 * Motion State - PASSED/FAILED/TIED.
	 */
	private Enum<MotionState> motionState;
	/**
	 * Number of 'YES' Vote Counts
	 */
	private long yesVoteCounts;
	/**
	 * Number of 'NO' vote Counts
	 */
	private long noVoteCounts;
	/**
	 * Voter information for the given Motion
	 */
	private List<Voter> voters = new ArrayList<>();

	/**
	 * 
	 * @param openedtime
	 */
	public VotingMotion(LocalDateTime openedtime) {
		this.openedTime = openedtime;
	}

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

	@Override
	public Enum<MotionStatus> getMotionStatus() {
		return motionStatus;
	}

	@Override
	public Enum<MotionState> getMotionState() {
		return motionState;
	}

	public List<Voter> getVoters() {
		return voters;
	}

	@Override
	public long getYesVoteCounts() {
		return yesVoteCounts;
	}

	@Override
	public long getNoVoteCounts() {
		return noVoteCounts;
	}

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

	@Override
	public void setMotionState() {

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
	 * @param voterId
	 *            Corresponds to one Senator who votes on Motion.
	 * @param voteState
	 *            YES/NO Votes on Motion.
	 * @param isVicePresident
	 *            is the Voter Vice President.
	 * @throws MaximumVoteOnMotionException
	 *             if maximum votes reaches 101.
	 * @throws MotionException
	 * @throws DuplicateVoteException
	 *             if the same Senator tries to vote for the second time on the same
	 *             Motion.
	 * @throws VicePresidentVoteException
	 *             if Vice President cast votes other than TIED state.
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
	 * 
	 * 
	 * @return <t>true</t> if motion is OPENED for voting by comparing Motion Opened
	 *         Time and Current Time.
	 */
	private boolean isMotionOpenedForVoting() {

		LocalDateTime currentTime = LocalDateTime.now();
		return DateTimeUtils.checkTime(currentTime, this.getOpenedTime());
	}

	/**
	 * Under Motion TIED VP casts votes
	 * 
	 * @param isVicePresedent
	 *            is the Voter Vice President.
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
	 * 
	 * @return <t>true</t> if MAXIMUM votes less than or equal to 101
	 */
	private boolean isMaximumVoteReceivedOnMotion() {
		return this.getVoters().size() < MotionVotingConstants.MAX_VOTES_ALLOWED;
	}

	/**
	 * 
	 * 
	 * @return<t>true</t> if Motion is past 15 minutes after OPENED
	 * 
	 * @throws CloseVotingException
	 *             if Motion is tries to CLOSE less than 15 minutes after opened.
	 */
	private boolean canMotionClose() throws CloseVotingException {

		if (DateTimeUtils.checkTime(LocalDateTime.now(), this.getOpenedTime())) {
			return DateTimeUtils.durationBetween(this.getOpenedTime(), LocalDateTime.now())
					.toMinutes() > MotionVotingConstants.MOTION_WINDOW;
		} else {

			throw new CloseVotingException(MotionVotingConstants.MOTION_CANNOT_CLOSED);

		}

	}

	/**
	 * 
	 * @author Arun Devadoss
	 *
	 */
	private class Voter {

		/**
		 * Voter Id
		 */
		private int voterId;
		/**
		 * Vote State - YES/NO
		 */
		private Enum<VoteState> voteState;

		/**
		 * 
		 * @param voterId
		 * @param voteState
		 */
		public Voter(int voterId, Enum<VoteState> voteState) {
			super();
			this.voterId = voterId;
			this.voteState = voteState;
		}

		/**
		 * @return the voterId
		 */
		public int getVoterId() {
			return voterId;
		}

		/**
		 * @return the voteState
		 */
		public Enum<VoteState> getVoteState() {
			return voteState;
		}

	}

}
