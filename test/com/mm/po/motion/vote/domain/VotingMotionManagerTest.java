//package com.mm.po.motion.vote.domain;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//
//import java.time.LocalDateTime;
//import java.util.HashMap;
//import java.util.Map;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//
//import com.mm.po.motion.vote.VotingMotionManager;
//import com.mm.po.motion.vote.domain.Motion;
//import com.mm.po.motion.vote.enums.MotionState;
//import com.mm.po.motion.vote.enums.MotionStatus;
//import com.mm.po.motion.vote.enums.VoteState;
//import com.mm.po.motion.vote.exception.CloseVotingException;
//import com.mm.po.motion.vote.exception.DuplicateVoteException;
//import com.mm.po.motion.vote.exception.MaximumVoteOnMotionException;
//import com.mm.po.motion.vote.exception.MotionException;
//import com.mm.po.motion.vote.exception.VicePresidentVoteException;
//
///**
// * 
// * @author Arun Devadoss
// *
// */
//public class VotingMotionManagerTest {
//
//	private VotingMotionManager votingMotionManager;
//
//	private Map<Integer, Motion> motionMap;
//
//	@Before
//	public void setUp() {
//		votingMotionManager = new VotingMotionManager();
//		motionMap = new HashMap<>();
//	}
//
//	@After
//	public void tearDown() {
//		votingMotionManager = null;
//		motionMap = null;
//	}
//
//	/**
//	 * Use case 1: A motion must not accept any votes until it is opened for voting.
//	 * 
//	 * @throws MaximumVoteOnMotionException
//	 * @throws MotionException
//	 * @throws DuplicateVoteException
//	 * @throws VicePresidentVoteException
//	 */
//	@Test(expected = MotionException.class)
//	public void castVotingOnMotion_MotionNotStarted()
//			throws MaximumVoteOnMotionException, MotionException, DuplicateVoteException, VicePresidentVoteException {
//
//		constructMotion(1, LocalDateTime.now().plusMinutes(10));
//
//		votingMotionManager.setMotionMap(motionMap);
//		votingMotionManager.castVotingOnMotion(1, 1, VoteState.Y, false);
//
//	}
//
//	/**
//	 * Use Case 2 A: When a motion is closed for voting, a result is returned that
//	 * describes o whether the motion passed or failed o the number of yes and no
//	 * votes o the time that voting opened and closed
//	 * 
//	 * @throws MaximumVoteOnMotionException
//	 * @throws MotionException
//	 * @throws DuplicateVoteException
//	 * @throws CloseVotingException
//	 * @throws VicePresidentVoteException
//	 */
//	@Test
//	public void get_Motion_results() throws MaximumVoteOnMotionException, MotionException, DuplicateVoteException,
//			CloseVotingException, VicePresidentVoteException {
//
//		constructMotion(1, LocalDateTime.now().minusMinutes(30));
//		constructMotion(2, LocalDateTime.now().minusMinutes(30));
//
//		votingMotionManager.setMotionMap(motionMap);
//
//		votingMotionManager.castVotingOnMotion(1, 1, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 2, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 3, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 4, VoteState.N, false);
//
//		votingMotionManager.castVotingOnMotion(2, 1, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(2, 2, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(2, 3, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(2, 4, VoteState.N, false);
//
//		votingMotionManager.closeVotingOnMotion(1);
//		votingMotionManager.closeVotingOnMotion(2);
//
//		assertEquals(MotionStatus.CLOSED, votingMotionManager.getMotionMap().get(1).getMotionStatus());
//		assertEquals(MotionStatus.CLOSED, votingMotionManager.getMotionMap().get(2).getMotionStatus());
//
//		votingMotionManager.setMotionState(1);
//		assertEquals(MotionState.PASSED, votingMotionManager.getMotionResult(1).getMotionState());
//		assertEquals(3, votingMotionManager.getMotionResult(1).getYesVotecounts());
//		assertEquals(1, votingMotionManager.getMotionResult(1).getNoVoteCounts());
//		assertNotNull(votingMotionManager.getMotionResult(1).getVotingOpenedTime());
//		assertNotNull(votingMotionManager.getMotionResult(1).getVotingClosedTime());
//
//		votingMotionManager.setMotionState(2);
//		assertEquals(MotionState.FAILED, votingMotionManager.getMotionResult(2).getMotionState());
//		assertEquals(1, votingMotionManager.getMotionResult(2).getYesVotecounts());
//		assertEquals(3, votingMotionManager.getMotionResult(2).getNoVoteCounts());
//		assertNotNull(votingMotionManager.getMotionResult(2).getVotingOpenedTime());
//		assertNotNull(votingMotionManager.getMotionResult(2).getVotingClosedTime());
//
//	}
//
//	/**
//	 * Use Case 2 B: When a motion is closed for voting, a result is returned that
//	 * describes o whether the motion passed or failed o the number of yes and no
//	 * votes o the time that voting opened and closed
//	 * 
//	 * @throws MotionException
//	 */
//	@Test(expected = MotionException.class)
//	public void get_Motion_Results() throws MotionException {
//
//		votingMotionManager.setMotionMap(motionMap);
//
//		votingMotionManager.getMotionResult(1);
//
//	}
//
//	/**
//	 * Use case 3 A: A motion cannot be closed for voting less than 15 minutes after
//	 * it was opened.
//	 * 
//	 * @throws MaximumVoteOnMotionException
//	 * @throws MotionException
//	 * @throws DuplicateVoteException
//	 * @throws VicePresidentVoteException
//	 * @throws CloseVotingException
//	 */
//	@Test(expected = CloseVotingException.class)
//	public void closeVotingOnMotion_LessThan_15_Minutes() throws MaximumVoteOnMotionException, MotionException,
//			DuplicateVoteException, VicePresidentVoteException, CloseVotingException {
//
//		constructMotion(1, LocalDateTime.now().minusMinutes(10));
//
//		votingMotionManager.setMotionMap(motionMap);
//		votingMotionManager.castVotingOnMotion(1, 1, VoteState.Y, false);
//
//		votingMotionManager.closeVotingOnMotion(1);
//
//	}
//
//	/**
//	 * Use case 3 B: A motion can be closed for voting greater than 15 minutes after
//	 * it was opened.
//	 * 
//	 * @throws MaximumVoteOnMotionException
//	 * @throws MotionException
//	 * @throws DuplicateVoteException
//	 * @throws VicePresidentVoteException
//	 * @throws CloseVotingException
//	 */
//	@Test(expected = MotionException.class)
//	public void closeVotingOnMotion_Motion_Already_Closed_Exception() throws MaximumVoteOnMotionException,
//			MotionException, DuplicateVoteException, VicePresidentVoteException, CloseVotingException {
//
//		constructMotion(1, LocalDateTime.now().minusMinutes(16));
//
//		votingMotionManager.setMotionMap(motionMap);
//		votingMotionManager.castVotingOnMotion(1, 1, VoteState.Y, false);
//
//		votingMotionManager.closeVotingOnMotion(1);
//		votingMotionManager.closeVotingOnMotion(1);
//
//	}
//
//	/**
//	 * Use case 3 C: A motion can be closed for voting greater than 15 minutes after
//	 * it was opened.
//	 * 
//	 * @throws MaximumVoteOnMotionException
//	 * @throws MotionException
//	 * @throws DuplicateVoteException
//	 * @throws VicePresidentVoteException
//	 * @throws CloseVotingException
//	 */
//	@Test(expected = MotionException.class)
//	public void closeVotingOnMotion_Motion_Not_Created_Exception() throws MaximumVoteOnMotionException, MotionException,
//			DuplicateVoteException, VicePresidentVoteException, CloseVotingException {
//
//		votingMotionManager.setMotionMap(motionMap);
//
//		votingMotionManager.closeVotingOnMotion(1);
//
//	}
//
//	/**
//	 * Use case 3 D: A motion can be closed for voting greater than 15 minutes after
//	 * it was opened.
//	 * 
//	 * @throws MaximumVoteOnMotionException
//	 * @throws MotionException
//	 * @throws DuplicateVoteException
//	 * @throws VicePresidentVoteException
//	 * @throws CloseVotingException
//	 */
//	@Test
//	public void closeVotingOnMotion_GreaterThan_15_Minutes() throws MaximumVoteOnMotionException, MotionException,
//			DuplicateVoteException, VicePresidentVoteException, CloseVotingException {
//
//		constructMotion(1, LocalDateTime.now().minusMinutes(16));
//
//		votingMotionManager.setMotionMap(motionMap);
//		votingMotionManager.castVotingOnMotion(1, 1, VoteState.Y, false);
//
//		votingMotionManager.closeVotingOnMotion(1);
//		assertEquals(MotionStatus.CLOSED, votingMotionManager.getMotionMap().get(1).getMotionStatus());
//
//	}
//
//	/**
//	 * Use case 3 E: A motion cannot be closed for voting less than 15 minutes after
//	 * it was opened.
//	 * 
//	 * @throws MaximumVoteOnMotionException
//	 * @throws MotionException
//	 * @throws DuplicateVoteException
//	 * @throws VicePresidentVoteException
//	 * @throws CloseVotingException
//	 */
//	@Test(expected = CloseVotingException.class)
//	public void closeVotingOnMotion_MotionDidNot_Started() throws MaximumVoteOnMotionException, MotionException,
//			DuplicateVoteException, VicePresidentVoteException, CloseVotingException {
//
//		constructMotion(1, LocalDateTime.now().plusMinutes(50));
//
//		votingMotionManager.setMotionMap(motionMap);
//
//		votingMotionManager.closeVotingOnMotion(1);
//
//	}
//
//	/**
//	 * Use Case 4: No voter can vote more than once on the same motion.
//	 * 
//	 * @throws MaximumVoteOnMotionException
//	 * @throws MotionException
//	 * @throws DuplicateVoteException
//	 * @throws VicePresidentVoteException
//	 */
//	@Test(expected = DuplicateVoteException.class)
//	public void castVotingOnMotion_Duplicate_Votes()
//			throws MaximumVoteOnMotionException, MotionException, DuplicateVoteException, VicePresidentVoteException {
//
//		constructMotion(1);
//		votingMotionManager.setMotionMap(motionMap);
//		votingMotionManager.castVotingOnMotion(1, 1, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 1, VoteState.Y, false);
//
//	}
//
//	/**
//	 * Use Case 5 A: The maximum votes that can be received on a motion is 101 (100
//	 * senators plus the Vicepresident).
//	 * 
//	 * @throws MaximumVoteOnMotionException
//	 * @throws MotionException
//	 * @throws DuplicateVoteException
//	 * @throws VicePresidentVoteException
//	 * @throws CloseVotingException
//	 */
//	@Test(expected = VicePresidentVoteException.class)
//	public void castVotingOnMotion_Max_Votes_101_Motion_Passed() throws MaximumVoteOnMotionException, MotionException,
//			DuplicateVoteException, VicePresidentVoteException, CloseVotingException {
//
//		constructMotion(1);
//
//		votingMotionManager.setMotionMap(motionMap);
//		votingMotionManager.castVotingOnMotion(1, 1, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 2, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 3, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 4, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 5, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 6, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 7, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 8, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 9, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 10, VoteState.Y, false);
//
//		votingMotionManager.castVotingOnMotion(1, 11, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 12, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 13, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 14, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 15, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 16, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 17, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 18, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 19, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 20, VoteState.Y, false);
//
//		votingMotionManager.castVotingOnMotion(1, 21, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 22, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 23, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 24, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 25, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 26, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 27, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 28, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 29, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 30, VoteState.Y, false);
//
//		votingMotionManager.castVotingOnMotion(1, 31, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 32, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 33, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 34, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 35, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 36, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 37, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 38, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 39, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 40, VoteState.Y, false);
//
//		votingMotionManager.castVotingOnMotion(1, 41, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 42, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 43, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 44, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 45, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 46, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 47, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 48, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 49, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 50, VoteState.Y, false);
//
//		votingMotionManager.castVotingOnMotion(1, 51, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 52, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 53, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 54, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 55, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 56, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 57, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 58, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 59, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 60, VoteState.Y, false);
//
//		votingMotionManager.castVotingOnMotion(1, 61, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 62, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 63, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 64, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 65, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 66, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 67, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 68, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 69, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 70, VoteState.Y, false);
//
//		votingMotionManager.castVotingOnMotion(1, 71, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 72, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 73, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 74, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 75, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 76, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 77, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 78, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 79, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 80, VoteState.Y, false);
//
//		votingMotionManager.castVotingOnMotion(1, 81, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 82, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 83, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 84, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 85, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 86, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 87, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 88, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 89, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 90, VoteState.Y, false);
//
//		votingMotionManager.castVotingOnMotion(1, 91, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 92, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 93, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 94, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 95, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 96, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 97, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 98, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 99, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 100, VoteState.Y, false);
//
//		votingMotionManager.setMotionState(1);
//		assertEquals(MotionState.PASSED, votingMotionManager.getMotionMap().get(1).getMotionState());
//
//		votingMotionManager.castVotingOnMotion(1, 101, VoteState.Y, true);
//
//	}
//
//	/**
//	 * Use Case 5 B: The maximum votes that can be received on a motion is 101 (100
//	 * senators plus the Vicepresident). Vice President votes in Tied state
//	 * 
//	 * @throws MaximumVoteOnMotionException
//	 * @throws MotionException
//	 * @throws DuplicateVoteException
//	 * @throws VicePresidentVoteException
//	 * @throws CloseVotingException
//	 */
//	@Test
//	public void castVotingOnMotion_Max_Votes_101_Motion_Tied() throws MaximumVoteOnMotionException, MotionException,
//			DuplicateVoteException, VicePresidentVoteException, CloseVotingException {
//
//		constructMotion(1);
//
//		votingMotionManager.setMotionMap(motionMap);
//		votingMotionManager.castVotingOnMotion(1, 1, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 2, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 3, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 4, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 5, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 6, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 7, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 8, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 9, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 10, VoteState.Y, false);
//
//		votingMotionManager.castVotingOnMotion(1, 11, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 12, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 13, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 14, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 15, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 16, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 17, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 18, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 19, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 20, VoteState.Y, false);
//
//		votingMotionManager.castVotingOnMotion(1, 21, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 22, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 23, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 24, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 25, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 26, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 27, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 28, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 29, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 30, VoteState.Y, false);
//
//		votingMotionManager.castVotingOnMotion(1, 31, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 32, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 33, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 34, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 35, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 36, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 37, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 38, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 39, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 40, VoteState.Y, false);
//
//		votingMotionManager.castVotingOnMotion(1, 41, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 42, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 43, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 44, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 45, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 46, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 47, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 48, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 49, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 50, VoteState.Y, false);
//
//		votingMotionManager.castVotingOnMotion(1, 51, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 52, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 53, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 54, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 55, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 56, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 57, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 58, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 59, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 60, VoteState.N, false);
//
//		votingMotionManager.castVotingOnMotion(1, 61, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 62, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 63, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 64, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 65, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 66, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 67, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 68, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 69, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 70, VoteState.N, false);
//
//		votingMotionManager.castVotingOnMotion(1, 71, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 72, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 73, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 74, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 75, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 76, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 77, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 78, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 79, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 80, VoteState.N, false);
//
//		votingMotionManager.castVotingOnMotion(1, 81, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 82, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 83, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 84, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 85, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 86, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 87, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 88, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 89, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 90, VoteState.N, false);
//
//		votingMotionManager.castVotingOnMotion(1, 91, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 92, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 93, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 94, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 95, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 96, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 97, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 98, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 99, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 100, VoteState.N, false);
//
//		votingMotionManager.setMotionState(1);
//		assertEquals(MotionState.TIED, votingMotionManager.getMotionMap().get(1).getMotionState());
//
//		votingMotionManager.castVotingOnMotion(1, 101, VoteState.Y, true);
//
//	}
//
//	/**
//	 * Use Case 5 C: The maximum votes that can be received on a motion is 101 (100
//	 * senators plus the Vicepresident). Vice President votes in Tied state
//	 * 
//	 * 102 votes
//	 * 
//	 * @throws MaximumVoteOnMotionException
//	 * @throws MotionException
//	 * @throws DuplicateVoteException
//	 * @throws VicePresidentVoteException
//	 * @throws CloseVotingException
//	 */
//	@Test(expected = MaximumVoteOnMotionException.class)
//	public void castVotingOnMotion_Max_Votes_102() throws MaximumVoteOnMotionException, MotionException,
//			DuplicateVoteException, VicePresidentVoteException, CloseVotingException {
//
//		constructMotion(1);
//
//		votingMotionManager.setMotionMap(motionMap);
//		votingMotionManager.castVotingOnMotion(1, 1, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 2, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 3, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 4, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 5, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 6, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 7, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 8, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 9, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 10, VoteState.Y, false);
//
//		votingMotionManager.castVotingOnMotion(1, 11, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 12, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 13, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 14, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 15, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 16, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 17, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 18, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 19, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 20, VoteState.Y, false);
//
//		votingMotionManager.castVotingOnMotion(1, 21, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 22, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 23, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 24, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 25, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 26, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 27, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 28, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 29, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 30, VoteState.Y, false);
//
//		votingMotionManager.castVotingOnMotion(1, 31, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 32, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 33, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 34, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 35, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 36, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 37, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 38, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 39, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 40, VoteState.Y, false);
//
//		votingMotionManager.castVotingOnMotion(1, 41, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 42, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 43, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 44, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 45, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 46, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 47, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 48, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 49, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 50, VoteState.Y, false);
//
//		votingMotionManager.castVotingOnMotion(1, 51, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 52, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 53, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 54, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 55, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 56, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 57, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 58, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 59, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 60, VoteState.N, false);
//
//		votingMotionManager.castVotingOnMotion(1, 61, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 62, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 63, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 64, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 65, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 66, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 67, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 68, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 69, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 70, VoteState.N, false);
//
//		votingMotionManager.castVotingOnMotion(1, 71, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 72, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 73, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 74, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 75, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 76, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 77, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 78, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 79, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 80, VoteState.N, false);
//
//		votingMotionManager.castVotingOnMotion(1, 81, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 82, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 83, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 84, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 85, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 86, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 87, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 88, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 89, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 90, VoteState.N, false);
//
//		votingMotionManager.castVotingOnMotion(1, 91, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 92, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 93, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 94, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 95, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 96, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 97, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 98, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 99, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 100, VoteState.N, false);
//
//		votingMotionManager.castVotingOnMotion(1, 101, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 102, VoteState.Y, false);
//
//	}
//
//	/**
//	 * Use Case 6 A: In the tied state, the Vice-president of the United States is
//	 * the only person allowed to vote on the motion. Once the VP votes, the motion
//	 * is automatically closed. Motion Failed, out of 5 votes , 2 Yes and 3 No(VP
//	 * Votes 'N')
//	 * 
//	 * @throws MaximumVoteOnMotionException
//	 * @throws MotionException
//	 * @throws DuplicateVoteException
//	 * @throws VicePresidentVoteException
//	 */
//	@Test
//	public void castVotingOnMotion_Motion_Tied_VP_Votes_Motion_Closed_Failed()
//			throws MaximumVoteOnMotionException, MotionException, DuplicateVoteException, VicePresidentVoteException {
//
//		constructMotion(1);
//
//		votingMotionManager.setMotionMap(motionMap);
//		votingMotionManager.castVotingOnMotion(1, 1, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 2, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 3, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 4, VoteState.N, false);
//
//		votingMotionManager.setMotionState(1);
//		assertEquals(MotionState.TIED, votingMotionManager.getMotionMap().get(1).getMotionState());
//
//		votingMotionManager.castVotingOnMotion(1, 5, VoteState.N, true);
//		assertEquals(MotionStatus.CLOSED, votingMotionManager.getMotionMap().get(1).getMotionStatus());
//
//		votingMotionManager.setMotionState(1);
//		assertEquals(MotionState.FAILED, votingMotionManager.getMotionMap().get(1).getMotionState());
//
//	}
//
//	/**
//	 * Use Case 6 B: In the tied state, the Vice-president of the United States is
//	 * the only person allowed to vote on the motion. Once the VP votes, the motion
//	 * is automatically closed. Motion Passed, out of 5 votes , 3 Yes and 2 No(VP
//	 * Votes 'Y')
//	 * 
//	 * @throws MaximumVoteOnMotionException
//	 * @throws MotionException
//	 * @throws DuplicateVoteException
//	 * @throws VicePresidentVoteException
//	 */
//	@Test
//	public void castVotingOnMotion_Motion_Tied_VP_Votes_Motion_Closed_Passed()
//			throws MaximumVoteOnMotionException, MotionException, DuplicateVoteException, VicePresidentVoteException {
//
//		constructMotion(1);
//
//		votingMotionManager.setMotionMap(motionMap);
//		votingMotionManager.castVotingOnMotion(1, 1, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 2, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 3, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 4, VoteState.N, false);
//
//		votingMotionManager.setMotionState(1);
//		assertEquals(MotionState.TIED, votingMotionManager.getMotionMap().get(1).getMotionState());
//
//		votingMotionManager.castVotingOnMotion(1, 5, VoteState.Y, true);
//		assertEquals(MotionStatus.CLOSED, votingMotionManager.getMotionMap().get(1).getMotionStatus());
//
//		votingMotionManager.setMotionState(1);
//		assertEquals(MotionState.PASSED, votingMotionManager.getMotionMap().get(1).getMotionState());
//
//	}
//
//	/**
//	 * Use Case 6 C: In the tied state, the Vice-president of the United States is
//	 * the only person allowed to vote on the motion. Once the VP votes, the motion
//	 * is automatically closed. Motion Passed, out of 5 votes , 3 Yes and 2 No(VP
//	 * Votes 'Y')
//	 * 
//	 * @throws MaximumVoteOnMotionException
//	 * @throws MotionException
//	 * @throws DuplicateVoteException
//	 * @throws VicePresidentVoteException
//	 */
//	@Test(expected = VicePresidentVoteException.class)
//	public void castVotingOnMotion_Motion_Tied_VP_Votes_Negate_Tied()
//			throws MaximumVoteOnMotionException, MotionException, DuplicateVoteException, VicePresidentVoteException {
//
//		constructMotion(1);
//
//		votingMotionManager.setMotionMap(motionMap);
//		votingMotionManager.castVotingOnMotion(1, 1, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 2, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 3, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 4, VoteState.N, false);
//
//		votingMotionManager.setMotionState(1);
//		assertEquals(MotionState.PASSED, votingMotionManager.getMotionMap().get(1).getMotionState());
//
//		votingMotionManager.castVotingOnMotion(1, 5, VoteState.Y, true);
//
//	}
//
//	/**
//	 * Use Case 6 D: If the VP is not available to vote, then voting can be forced
//	 * to the closed state which causes the motion to fail.
//	 * 
//	 * @throws MaximumVoteOnMotionException
//	 * @throws MotionException
//	 * @throws DuplicateVoteException
//	 * @throws VicePresidentVoteException
//	 */
//	@Test
//	public void castVotingOnMotion_Motion_Tied_VP_Not_Available_Motion_Closed_Failed()
//			throws MaximumVoteOnMotionException, MotionException, DuplicateVoteException, VicePresidentVoteException {
//
//		constructMotion(1);
//
//		votingMotionManager.setMotionMap(motionMap);
//		votingMotionManager.castVotingOnMotion(1, 1, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 2, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 3, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 4, VoteState.N, false);
//
//		votingMotionManager.setMotionState(1);
//		assertEquals(MotionState.TIED, votingMotionManager.getMotionMap().get(1).getMotionState());
//
//		votingMotionManager.castVotingOnMotion(1, 5, VoteState.Y, false);
//		assertEquals(MotionStatus.CLOSED, votingMotionManager.getMotionMap().get(1).getMotionStatus());
//
//		assertEquals(MotionState.FAILED, votingMotionManager.getMotionMap().get(1).getMotionState());
//
//	}
//
//	/**
//	 * Use Case 7: The code must support a query to discover the current state of a
//	 * motion.
//	 * 
//	 * @throws MaximumVoteOnMotionException
//	 * @throws MotionException
//	 * @throws DuplicateVoteException
//	 * @throws VicePresidentVoteException
//	 */
//	@Test
//	public void current_Motion_State()
//			throws MaximumVoteOnMotionException, MotionException, DuplicateVoteException, VicePresidentVoteException {
//
//		constructMotion(1);
//
//		votingMotionManager.setMotionMap(motionMap);
//		votingMotionManager.castVotingOnMotion(1, 1, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 2, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 3, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 4, VoteState.N, false);
//
//		votingMotionManager.setMotionState(1);
//		assertEquals(MotionState.TIED.toString(), votingMotionManager.getMotionState(1));
//
//	}
//
//	/**
//	 * 
//	 * @throws MotionException
//	 */
//	@Test(expected = MotionException.class)
//	public void set_Motion_State_Exception() throws MotionException {
//
//		votingMotionManager.setMotionMap(motionMap);
//
//		votingMotionManager.setMotionState(1);
//
//	}
//
//	/**
//	 * 
//	 * @throws MotionException
//	 * @throws VicePresidentVoteException
//	 * @throws DuplicateVoteException
//	 * @throws MaximumVoteOnMotionException
//	 * @throws CloseVotingException
//	 */
//	@Test(expected = MotionException.class)
//	public void castVotingOnMotion_ClosedMotion_MotionException() throws MotionException, MaximumVoteOnMotionException,
//			DuplicateVoteException, VicePresidentVoteException, CloseVotingException {
//
//		constructMotion(1, LocalDateTime.now().minusMinutes(30));
//
//		votingMotionManager.setMotionMap(motionMap);
//		votingMotionManager.castVotingOnMotion(1, 1, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 2, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 3, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 4, VoteState.N, false);
//
//		votingMotionManager.closeVotingOnMotion(1);
//
//		votingMotionManager.castVotingOnMotion(1, 4, VoteState.N, false);
//	}
//
//	/**
//	 * 
//	 * 
//	 * @throws MaximumVoteOnMotionException
//	 * @throws MotionException
//	 * @throws DuplicateVoteException
//	 * @throws VicePresidentVoteException
//	 */
//	@Test(expected = MotionException.class)
//	public void current_Motion_State_Exception()
//			throws MaximumVoteOnMotionException, MotionException, DuplicateVoteException, VicePresidentVoteException {
//
//		votingMotionManager.setMotionMap(motionMap);
//
//		votingMotionManager.getMotionState(1);
//
//	}
//
//	/**
//	 * Motion Passed , out of 3 votes , 2 Yes and 1 No.
//	 * 
//	 * @throws MaximumVoteOnMotionException
//	 * @throws MotionException
//	 * @throws DuplicateVoteException
//	 * @throws VicePresidentVoteException
//	 */
//	@Test
//	public void castVotingOnMotion_Motion_State_Pass()
//			throws MaximumVoteOnMotionException, MotionException, DuplicateVoteException, VicePresidentVoteException {
//
//		constructMotion(1);
//
//		votingMotionManager.setMotionMap(motionMap);
//		votingMotionManager.castVotingOnMotion(1, 1, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 2, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 3, VoteState.Y, false);
//
//		votingMotionManager.setMotionState(1);
//
//		assertEquals(MotionState.PASSED, votingMotionManager.getMotionMap().get(1).getMotionState());
//
//	}
//
//	/**
//	 * Motion Failed , out of 3 votes , 1 Yes and 2 No.
//	 * 
//	 * @throws MaximumVoteOnMotionException
//	 * @throws MotionException
//	 * @throws DuplicateVoteException
//	 * @throws VicePresidentVoteException
//	 */
//	@Test
//	public void castVotingOnMotion_Motion_State_Failed()
//			throws MaximumVoteOnMotionException, MotionException, DuplicateVoteException, VicePresidentVoteException {
//
//		constructMotion(1);
//
//		votingMotionManager.setMotionMap(motionMap);
//		votingMotionManager.castVotingOnMotion(1, 1, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 2, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 3, VoteState.N, false);
//
//		votingMotionManager.setMotionState(1);
//
//		assertEquals(MotionState.FAILED, votingMotionManager.getMotionMap().get(1).getMotionState());
//
//	}
//
//	/**
//	 * Motion Tied , out of 4 votes , 2 Yes and 2 No.
//	 * 
//	 * @throws MaximumVoteOnMotionException
//	 * @throws MotionException
//	 * @throws DuplicateVoteException
//	 * @throws VicePresidentVoteException
//	 */
//	@Test
//	public void castVotingOnMotion_Motion_State_Tied()
//			throws MaximumVoteOnMotionException, MotionException, DuplicateVoteException, VicePresidentVoteException {
//
//		constructMotion(1);
//
//		votingMotionManager.setMotionMap(motionMap);
//		votingMotionManager.castVotingOnMotion(1, 1, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 2, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 3, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 4, VoteState.N, false);
//
//		votingMotionManager.setMotionState(1);
//
//		assertEquals(MotionState.TIED, votingMotionManager.getMotionMap().get(1).getMotionState());
//
//	}
//
//	/**
//	 * Motion 1 Passed , out of 3 votes , 2 Yes and 1 No. Motion 2 Failed , out of 3
//	 * votes , 1 Yes and 2 No. Motion 3 Tied , out of 4 votes , 2 Yes and 2 No.
//	 * 
//	 * @throws MaximumVoteOnMotionException
//	 * @throws MotionException
//	 * @throws DuplicateVoteException
//	 * @throws VicePresidentVoteException
//	 */
//	@Test
//	public void castVotingOnMotion_Three_Motions()
//			throws MaximumVoteOnMotionException, MotionException, DuplicateVoteException, VicePresidentVoteException {
//
//		constructMotion(1);
//
//		constructMotion(2);
//
//		constructMotion(3);
//
//		votingMotionManager.setMotionMap(motionMap);
//
//		votingMotionManager.castVotingOnMotion(1, 1, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 2, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 3, VoteState.Y, false);
//
//		votingMotionManager.castVotingOnMotion(2, 1, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(2, 2, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(2, 3, VoteState.N, false);
//
//		votingMotionManager.castVotingOnMotion(3, 1, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(3, 2, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(3, 3, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(3, 4, VoteState.N, false);
//
//		votingMotionManager.setMotionState(1);
//		votingMotionManager.setMotionState(2);
//		votingMotionManager.setMotionState(3);
//
//		assertEquals(MotionState.PASSED, votingMotionManager.getMotionMap().get(1).getMotionState());
//		assertEquals(MotionState.FAILED, votingMotionManager.getMotionMap().get(2).getMotionState());
//		assertEquals(MotionState.TIED, votingMotionManager.getMotionMap().get(3).getMotionState());
//
//	}
//
//	/**
//	 * Checking Voter State(Yes/No)
//	 * 
//	 * @throws MaximumVoteOnMotionException
//	 * @throws MotionException
//	 * @throws DuplicateVoteException
//	 * @throws VicePresidentVoteException
//	 */
//	@Test
//	public void castVotingOnMotion_Voter_State()
//			throws MaximumVoteOnMotionException, MotionException, DuplicateVoteException, VicePresidentVoteException {
//
//		constructMotion(1);
//
//		votingMotionManager.setMotionMap(motionMap);
//		votingMotionManager.castVotingOnMotion(1, 1, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 2, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 3, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 4, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 5, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 6, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 7, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 8, VoteState.N, false);
//		votingMotionManager.castVotingOnMotion(1, 9, VoteState.Y, false);
//		votingMotionManager.castVotingOnMotion(1, 10, VoteState.Y, false);
//
//		assertEquals(VoteState.N, votingMotionManager.getMotionMap().get(1).getVoters().stream()
//				.filter(m -> m.getVoterId() == 8).findAny().get().getVoteState());
//
//	}
//
//	/**
//	 * 
//	 * @param motionId
//	 */
//	private void constructMotion(final int motionId) {
//		Motion motion = new Motion();
//		motion.setMotionId(motionId);
//		motion.setOpenedTime(LocalDateTime.now());
//		motionMap.put(motionId, motion);
//	}
//
//	/**
//	 * 
//	 * @param motionId
//	 * @param time
//	 */
//	private void constructMotion(final int motionId, final LocalDateTime time) {
//		Motion motion = new Motion();
//		motion.setMotionId(motionId);
//		motion.setOpenedTime(time);
//		motionMap.put(motionId, motion);
//	}
//
//}
