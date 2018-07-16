package com.mm.po.motion.vote.domain;

/**
 * 
 * @author Arun devadoss
 *
 */
public class Voter {

	private int voterId;
	private String voteState;

	public int getVoterId() {
		return voterId;
	}

	public void setVoterId(int voterId) {
		this.voterId = voterId;
	}

	public String getVoteState() {
		return voteState;
	}

	public void setVoteState(String voteState) {
		this.voteState = voteState;
	}

	@Override
	public String toString() {
		return "Voter [voterId=" + voterId + ", voteState=" + voteState + "]";
	}

}
