package com.mm.po.motion.vote.constant;

/**
 * 
 * @author ArunDevadoss
 *
 */
public interface MotionVotingConstants {

	int MAX_VOTES_ALLOWED = 101;
	int MOTION_WINDOW = 15;
	String MOTION_NOT_CREATED = "Motion Is Not Yet Created";
	String MOTION_CLOSED = "Motion Already Closed";
	String MOTION_CANNOT_CLOSED_LESS_THAN_15 = "Motion Can't Be Closed Less Than 15 Minutes After It Was Opened";
	String VOTE_CANNOT_CAST = "Sorry Motion Closed, Can't Cast Voting";
	String MOTION_NOT_INITIALIZED = "Motions Are Not Yet Initialized";
	String MAX_VOTES = "Maximum Votes Allowed Is 101.";
	String VICE_PRESIDENT_VOTE_TIED_STATE = "Vice President Can Only Vote On Tied State";
	String DUPLICATE_VOTER = "Voter Can't Vote More Than Once";
	String MOTION_NOT_OPENED = "Motion Not Yet Opened, Opens At :";
	String MOTION_CANNOT_CLOSED = "Motion Can't Be Closed , As It Is Not Opened";

}
