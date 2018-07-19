package com.mm.po.motion.vote.domain;

import java.time.LocalDateTime;

import com.mm.po.motion.vote.domain.impl.VotingMotion;
import com.mm.po.motion.vote.enums.MotionState;
import com.mm.po.motion.vote.enums.MotionStatus;
import com.mm.po.motion.vote.enums.VoteState;
import com.mm.po.motion.vote.exception.CloseVotingException;
import com.mm.po.motion.vote.exception.DuplicateVoteException;
import com.mm.po.motion.vote.exception.MaximumVoteOnMotionException;
import com.mm.po.motion.vote.exception.MotionException;
import com.mm.po.motion.vote.exception.VicePresidentVoteException;

/**
 * 
 * @author Arun Devadoss
 * @see VotingMotion
 *
 */
public interface Motion {

	/**
	 * Cast voting on Motion, performs core voting on motions scenarios for the
	 * given Motion.
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
	void castVotingOnMotion(final int voterId, final Enum<VoteState> voteState, final boolean isVicePresident)
			throws MaximumVoteOnMotionException, MotionException, DuplicateVoteException, VicePresidentVoteException;

	/**
	 * Sets Motion state to PASSED/FAILED/TIED .
	 * 
	 * Number of Yes votes greater than No votes, Motion is PASSED.
	 * 
	 * Number of Yes votes less than No votes, Motion is FAILED.
	 * 
	 * Equal number of Yes and No votes, Motion is Tied.
	 * 
	 * @throws MotionException
	 */
	void setMotionState();

	/**
	 * Close Motion on voting once done.
	 * 
	 * Validates when Motion can be CLOSED,changes the Motion Status and
	 * MotionClosedTime.
	 * 
	 * @throws CloseVotingException
	 *             if Motion is tried to CLOSE less then 15 minutes after it is
	 *             opened.
	 * @throws MotionException
	 *             if we try to close already CLOSED Motion.
	 */
	void closeVotingOnMotion() throws CloseVotingException, MotionException;

	/**
	 * Motion Opened Time.
	 * 
	 * @return the openedTime
	 */
	LocalDateTime getOpenedTime();

	/**
	 * Motion Closed Time.
	 * 
	 * @return the closedTime
	 */
	LocalDateTime getClosedTime();

	/**
	 * Motion Status OPENED/CLOSED.
	 * 
	 * @return the motionStatus
	 */
	Enum<MotionStatus> getMotionStatus();

	/**
	 * Motion State PASSES/FAILED/TIED.
	 * 
	 * @return the motionState
	 */
	Enum<MotionState> getMotionState();

	/**
	 * Number of Yes Vote Counts by Senators.
	 * 
	 * @return the yesVotecounts
	 */
	long getYesVoteCounts();

	/**
	 * Number of No Vote Counts by Senators.
	 * 
	 * @return the noVoteCounts
	 */
	long getNoVoteCounts();

}
