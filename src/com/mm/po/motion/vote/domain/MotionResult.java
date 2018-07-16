package com.mm.po.motion.vote.domain;

import java.time.LocalDateTime;

import com.mm.po.motion.vote.enums.MotionState;

/**
 * 
 * @author Arun Devadoss
 *
 */
public class MotionResult {

	private Enum<MotionState> motionState;
	private long yesVotecounts;
	private long noVoteCounts;
	private LocalDateTime votingOpenedTime;
	private LocalDateTime votingClosedTime;

	/**
	 * @return the motionState
	 */
	public Enum<MotionState> getMotionState() {
		return motionState;
	}

	/**
	 * @param motionState
	 *            the motionState to set
	 */
	public void setMotionState(Enum<MotionState> motionState) {
		this.motionState = motionState;
	}

	/**
	 * @return the yesVotecounts
	 */
	public long getYesVotecounts() {
		return yesVotecounts;
	}

	/**
	 * @param yesVotecounts
	 *            the yesVotecounts to set
	 */
	public void setYesVotecounts(long yesVotecounts) {
		this.yesVotecounts = yesVotecounts;
	}

	/**
	 * @return the noVoteCounts
	 */
	public long getNoVoteCounts() {
		return noVoteCounts;
	}

	/**
	 * @param noVoteCounts
	 *            the noVoteCounts to set
	 */
	public void setNoVoteCounts(long noVoteCounts) {
		this.noVoteCounts = noVoteCounts;
	}

	/**
	 * @return the votingOpenedTime
	 */
	public LocalDateTime getVotingOpenedTime() {
		return votingOpenedTime;
	}

	/**
	 * @param votingOpenedTime
	 *            the votingOpenedTime to set
	 */
	public void setVotingOpenedTime(LocalDateTime votingOpenedTime) {
		this.votingOpenedTime = votingOpenedTime;
	}

	/**
	 * @return the votingClosedTime
	 */
	public LocalDateTime getVotingClosedTime() {
		return votingClosedTime;
	}

	/**
	 * @param votingClosedTime
	 *            the votingClosedTime to set
	 */
	public void setVotingClosedTime(LocalDateTime votingClosedTime) {
		this.votingClosedTime = votingClosedTime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MotionResult [motionState=" + motionState + ", yesVotecounts=" + yesVotecounts + ", noVoteCounts="
				+ noVoteCounts + ", votingOpenedTime=" + votingOpenedTime + ", votingClosedTime=" + votingClosedTime
				+ "]";
	}

}
