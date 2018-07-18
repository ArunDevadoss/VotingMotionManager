package com.mm.po.motion.vote.domain;

import com.mm.po.motion.vote.enums.VoteState;

/**
 * 
 * @author Arun devadoss
 *
 */
public class Voter {

	private int voterId;
	private Enum<VoteState> voteState;

	/**
	 * 
	 */
	public Voter() {

	}

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
	 * @param voterId
	 *            the voterId to set
	 */
	public void setVoterId(int voterId) {
		this.voterId = voterId;
	}

	/**
	 * @return the voteState
	 */
	public Enum<VoteState> getVoteState() {
		return voteState;
	}

	/**
	 * @param voteState
	 *            the voteState to set
	 */
	public void setVoteState(Enum<VoteState> voteState) {
		this.voteState = voteState;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Voter [voterId=" + voterId + ", voteState=" + voteState + "]";
	}

}
