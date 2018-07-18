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
