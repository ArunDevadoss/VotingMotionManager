package com.mm.po.motion.vote.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.mm.po.motion.vote.enums.MotionState;
import com.mm.po.motion.vote.enums.MotionStatus;

/**
 * 
 * @author Arun Devadoss
 *
 */
public class Motion {

	private int motionId;
	private LocalDateTime openedTime;
	private LocalDateTime closedTime;
	private Enum<MotionStatus> motionStatus;
	private Enum<MotionState> motionState;
	private List<Voter> voters = new ArrayList<>();

	/**
	 * @return the motionId
	 */
	public int getMotionId() {
		return motionId;
	}

	/**
	 * @param motionId
	 *            the motionId to set
	 */
	public void setMotionId(int motionId) {
		this.motionId = motionId;
	}

	/**
	 * @return the openedTime
	 */
	public LocalDateTime getOpenedTime() {
		return openedTime;
	}

	/**
	 * @param openedTime
	 *            the openedTime to set
	 */
	public void setOpenedTime(LocalDateTime openedTime) {
		this.openedTime = openedTime;
	}

	/**
	 * @return the closedTime
	 */
	public LocalDateTime getClosedTime() {
		return closedTime;
	}

	/**
	 * @param closedTime
	 *            the closedTime to set
	 */
	public void setClosedTime(LocalDateTime closedTime) {
		this.closedTime = closedTime;
	}

	/**
	 * @return the motionStatus
	 */
	public Enum<MotionStatus> getMotionStatus() {
		return motionStatus;
	}

	/**
	 * @param motionStatus
	 *            the motionStatus to set
	 */
	public void setMotionStatus(Enum<MotionStatus> motionStatus) {
		this.motionStatus = motionStatus;
	}

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
	 * @return the voters
	 */
	public List<Voter> getVoters() {
		return voters;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Motion [motionId=" + motionId + ", openedTime=" + openedTime + ", closedTime=" + closedTime
				+ ", motionStatus=" + motionStatus + ", motionState=" + motionState + ", voters=" + voters + "]";
	}

}
