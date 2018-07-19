package com.mm.po.motion.vote.domain;

import java.time.LocalDateTime;
import java.util.List;

import com.mm.po.motion.vote.domain.impl.Voter;
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
 *
 */
public interface Motion {

	/**
	 * 
	 * @param voterId
	 * @param voteState
	 * @param isVicePresedent
	 * @throws MaximumVoteOnMotionException
	 * @throws MotionException
	 * @throws DuplicateVoteException
	 * @throws VicePresidentVoteException
	 */
	public void castVotingOnMotion(final int voterId, final Enum<VoteState> voteState, final boolean isVicePresedent)
			throws MaximumVoteOnMotionException, MotionException, DuplicateVoteException, VicePresidentVoteException;

	/**
	 * 
	 * @throws MotionException
	 */
	public void setMotionState() throws MotionException;

	/**
	 * 
	 * @throws CloseVotingException
	 * @throws MotionException
	 */
	public void closeVotingOnMotion() throws CloseVotingException, MotionException;

	/**
	 * @return the openedTime
	 */
	public LocalDateTime getOpenedTime();

	/**
	 * @return the closedTime
	 */
	public LocalDateTime getClosedTime();

	/**
	 * Current State whether Motion Passed/Failed/Tied.
	 * 
	 * @return the motionStatus
	 */
	public Enum<MotionStatus> getMotionStatus();

	/**
	 * @return the motionState
	 */
	public Enum<MotionState> getMotionState();

	/**
	 * @return the yesVotecounts
	 */
	public long getYesVoteCounts();

	/**
	 * @return the noVoteCounts
	 */
	public long getNoVoteCounts();

	/**
	 * @return the voters
	 */
	public List<Voter> getVoters();
}
