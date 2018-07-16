# VotingMotionManager

Java solution for managing the voting on motions once debate has concluded on the senate floor.


The code you create must accept yes and no votes on various motions and determine whether each
motion passes or fails.


• A motion must not accept any votes until it is opened for voting.

• When a motion is closed for voting, a result is returned that describes
 
 	o whether the motion passed or failed
  
  	o the number of yes and no votes
  
  	o the time that voting opened and closed
  
• A motion cannot be closed for voting less than 15 minutes after it was opened.

• No voter can vote more than once on the same motion.

• The maximum votes that can be received on a motion is 101 (100 senators plus the Vicepresident).

• If voting is a tie, then an attempt to close the motion for voting will cause it to enter a special “tied” state.

  	o In the tied state, the Vice-president of the United States is the only person allowed to vote on the motion. Once the VP 		votes, the motion is automatically closed.
  
 	o The VP is only allowed to vote on motions that are in the tied state.
  
  	o If the VP is not available to vote, then voting can be forced to the closed state which causes the motion to fail.
  
• The code must support a query to discover the current state of a motion.


## Instructions


To successfully complete the assignment, follow these instructions:

	1. Write Java classes that meet all requirements.
	
	2. Write JUnit tests to verify all requirements.
	
	3. DO NOT write any UI code or service code, only domain code.
	
	4. DO NOT write any persistence code or authentication code.
	
	5. Avoid use of any framework or open source library other than JUnit.
	
	6. Readability and maintainability of your code is key.
	
	
	

## High Level Programme Design:



## Domain Objects:

  Motion  - Holds information about Motion(Motion Id, Motion State(PASSED/FAILED/TIED), Motion Status(CLOSED/OPENED) & Voter)
  
  Voter  - Holds information about Voter and it's state (Yes/No)
  
  MotionResult - When a motion is closed for voting, this object will provide the following information
  
  	o whether the motion passed or failed
  
  	o the number of yes and no votes
  
  	o the time that voting opened and closed
	
	
### Note: As I don't want to expose domain object Motion , converting domain object into MotionResult to provide motion information.



## VotingMotionManager: 


Has independent methods for the given scenarios/requirement and they are all tested using Junit(As per requirement)

Methods to accomodate all the scenarios/rquirements for Motion Voting Programme.


  String getMotionState(final int motionId)
  
  void closeVotingOnMotion(final int motionId)
  
  MotionResult getMotionResult(final int motionId)
  
  void castVotingOnMotion(final int motionId, final int voterId, final String voteState,
			final boolean isVicePresedent)
			
  void setMotionState(final int motionId)


## Exceptions:


  CloseVotingException
  
  DuplicateVoteException
  
  MaximumVoteOnMotionException
  
  MotionException
  
  VicePresidentVoteException
  

## Utils:

  DateTimeUtils
