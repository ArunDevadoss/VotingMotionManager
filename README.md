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

  	o In the tied state, the Vice-president of the United States is the only person allowed to vote on the motion. Once the VP votes, the motion is automatically closed.
  
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
	
	
	

## High Level Programme Design



## Domain Objects

  Motion  - Holds information about Motion(Motion Id, Motion State(PASSED/FAILED/TIED), Motion Status(CLOSED/OPENED) & Voter)
  
  Methods to accomodate all the scenarios/rquirements for Motion Voting Programme.


  	1) String getMotionState(final int motionId) throws MotionException
	
		o Returns Current State of a Motion - Passed/Failed/Tied.
    		
  	2) void closeVotingOnMotion(final int motionId) throws CloseVotingException, MotionException
	
		o CLOSE Motion for voting once done. Checks Motion is eligible for closing. If you try to close Motion within 15 minutes after it was opened , exception will be thrown
  
  	3) MotionResult getMotionResult(final int motionId) throws MotionException
	
		When a motion is CLOSED for voting, a result is returned that describes
			o whether the motion passed or failed
			o the number of yes and no votes
			o the time that voting opened and closed
  
  	4) void castVotingOnMotion(final int motionId, final int voterId, final Enum<VoteState> voteState,final boolean isVicePresedent) throws MaximumVoteOnMotionException, MotionException, DuplicateVoteException, VicePresidentVoteException
	
		o Checks various voting on Motion conditions.
	 
	  	o Checks for Motion OPENED for Voting, Duplicate Voters, Maximum number of Votes, VP voting conditions along with changing Motion State & Motion Status.
			
			
  	5) void setMotionState(final int motionId) throws MotionException
	
		o Sets Motion state to PASSED/FAILED/TIED.
		
		o Number of "Y" (Yes) votes greater than "N" (No) votes, Motion is PASSED. 
		
		o Number of "Y" (Yes) votes less than "N" No votes, Motion is FAILED. 
		
		o Equal number of "Y" Yes and "N" No votes, Motion is TIED.
  
  Voter  - Holds information about Voter and it's state (Yes/No)
  
  MotionResult - When a motion is closed for voting, this object will provide the following information
  
  	o whether the motion passed or failed
  
  	o the number of yes and no votes
  
  	o the time that voting opened and closed
	
	
## Note: As I don't want to expose domain object Motion , converting domain object into MotionResult to provide motion information.

## Enums

Motion and Vote specific constants are maintained in Enums

	1) MotionState - PASSED/FAILED/TIED
	
	2) MotionStatus  - OPENED/CLOSED
	
	3) VoteState - Y/N

## Exceptions

User Defined Exceptions are below 

  	1) CloseVotingException
  
  	2) DuplicateVoteException
  
  	3) MaximumVoteOnMotionException
  
  	4) MotionException
  
  	5) VicePresidentVoteException
  
## Constants

Programme specific constants, Exception messages are added in Constants

	1) MotionVotingConstants

## Utils

DateTime related methods are added in this Utils

  	1) DateTimeUtils
  

## Assumptions In Design

	1) As per the requirement , all the scenarios/requirements are covered in VotingMotionManager.java and it has been tested completely using JUnit.
	
	2) This programme is not a standalone code, hence Motion & Voter informations are not captured through command lines.
	
	3) All scenarios are tested with JUnit. Mocked Motion & Voter informations according to the different scenarios and assertained. Has 21 test cases with boundry conditions, exception cases, happy path, negative conditions with 95.9% of code coverage. 
	
	4) Some of the Motion information such as Name, Description etc are not captured as part of the design/coding, as the requirment  doesn't talk about it.
	
	5) Some of the Voter information such as Voter First Name & Last Name are not captured as part of the design/coding, as the requirement doesn't talk about it.

