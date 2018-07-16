package com.mm.po.motion.vote;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mm.po.motion.vote.domain.Motion;
import com.mm.po.motion.vote.enums.MotionState;
import com.mm.po.motion.vote.enums.MotionStatus;
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
public class VotingMotionManagerTest {

	private VotingMotionManager votingMotionManager;

	private Map<Integer, Motion> motionMap;

	@Before
	public void setUp() {
		votingMotionManager = new VotingMotionManager();
		motionMap = new HashMap<>();
	}

	@After
	public void tearDown() {
		votingMotionManager = null;
		motionMap = null;
	}

	/**
	 * Use case 1: A motion must not accept any votes until it is opened for voting.
	 * 
	 * @throws MaximumVoteOnMotionException
	 * @throws MotionException
	 * @throws DuplicateVoteException
	 * @throws VicePresidentVoteException
	 */
	@Test(expected = MotionException.class)
	public void castVotingOnMotion_MotionNotStarted()
			throws MaximumVoteOnMotionException, MotionException, DuplicateVoteException, VicePresidentVoteException {

		constructMotion(1, LocalDateTime.now().plusMinutes(10));

		votingMotionManager.setMotionMap(motionMap);
		votingMotionManager.castVotingOnMotion(1, 1, "Y", false);

	}

	/**
	 * Use Case 2 A: When a motion is closed for voting, a result is returned that
	 * describes o whether the motion passed or failed o the number of yes and no
	 * votes o the time that voting opened and closed
	 * 
	 * @throws MaximumVoteOnMotionException
	 * @throws MotionException
	 * @throws DuplicateVoteException
	 * @throws CloseVotingException
	 * @throws VicePresidentVoteException
	 */
	@Test
	public void get_Motion_results() throws MaximumVoteOnMotionException, MotionException, DuplicateVoteException,
			CloseVotingException, VicePresidentVoteException {

		constructMotion(1, LocalDateTime.now().minusMinutes(30));
		constructMotion(2, LocalDateTime.now().minusMinutes(30));

		votingMotionManager.setMotionMap(motionMap);

		votingMotionManager.castVotingOnMotion(1, 1, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 2, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 3, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 4, "N", false);

		votingMotionManager.castVotingOnMotion(2, 1, "Y", false);
		votingMotionManager.castVotingOnMotion(2, 2, "N", false);
		votingMotionManager.castVotingOnMotion(2, 3, "N", false);
		votingMotionManager.castVotingOnMotion(2, 4, "N", false);

		votingMotionManager.closeVotingOnMotion(1);
		votingMotionManager.closeVotingOnMotion(2);

		assertEquals(MotionStatus.CLOSED, votingMotionManager.getMotionMap().get(1).getMotionStatus());
		assertEquals(MotionStatus.CLOSED, votingMotionManager.getMotionMap().get(2).getMotionStatus());

		votingMotionManager.setMotionState(1);
		assertEquals(MotionState.PASSED, votingMotionManager.getMotionResult(1).getMotionState());
		assertEquals(3, votingMotionManager.getMotionResult(1).getYesVotecounts());
		assertEquals(1, votingMotionManager.getMotionResult(1).getNoVoteCounts());
		assertNotNull(votingMotionManager.getMotionResult(1).getVotingOpenedTime());
		assertNotNull(votingMotionManager.getMotionResult(1).getVotingClosedTime());

		votingMotionManager.setMotionState(2);
		assertEquals(MotionState.FAILED, votingMotionManager.getMotionResult(2).getMotionState());
		assertEquals(1, votingMotionManager.getMotionResult(2).getYesVotecounts());
		assertEquals(3, votingMotionManager.getMotionResult(2).getNoVoteCounts());
		assertNotNull(votingMotionManager.getMotionResult(2).getVotingOpenedTime());
		assertNotNull(votingMotionManager.getMotionResult(2).getVotingClosedTime());

	}

	/**
	 * Use Case 2 B: When a motion is closed for voting, a result is returned that
	 * describes o whether the motion passed or failed o the number of yes and no
	 * votes o the time that voting opened and closed
	 * 
	 * @throws MotionException
	 */
	@Test(expected = MotionException.class)
	public void get_Motion_Results() throws MotionException {

		votingMotionManager.setMotionMap(motionMap);

		votingMotionManager.getMotionResult(1);

	}

	/**
	 * Use case 3 A: A motion cannot be closed for voting less than 15 minutes after
	 * it was opened.
	 * 
	 * @throws MaximumVoteOnMotionException
	 * @throws MotionException
	 * @throws DuplicateVoteException
	 * @throws VicePresidentVoteException
	 * @throws CloseVotingException
	 */
	@Test(expected = CloseVotingException.class)
	public void closeVotingOnMotion_LessThan_15_Minutes() throws MaximumVoteOnMotionException, MotionException,
			DuplicateVoteException, VicePresidentVoteException, CloseVotingException {

		constructMotion(1, LocalDateTime.now().minusMinutes(10));

		votingMotionManager.setMotionMap(motionMap);
		votingMotionManager.castVotingOnMotion(1, 1, "Y", false);

		votingMotionManager.closeVotingOnMotion(1);

	}

	/**
	 * Use case 3 B: A motion can be closed for voting greater than 15 minutes after
	 * it was opened.
	 * 
	 * @throws MaximumVoteOnMotionException
	 * @throws MotionException
	 * @throws DuplicateVoteException
	 * @throws VicePresidentVoteException
	 * @throws CloseVotingException
	 */
	@Test(expected = MotionException.class)
	public void closeVotingOnMotion_Motion_Already_Closed_Exception() throws MaximumVoteOnMotionException,
			MotionException, DuplicateVoteException, VicePresidentVoteException, CloseVotingException {

		constructMotion(1, LocalDateTime.now().minusMinutes(16));

		votingMotionManager.setMotionMap(motionMap);
		votingMotionManager.castVotingOnMotion(1, 1, "Y", false);

		votingMotionManager.closeVotingOnMotion(1);
		votingMotionManager.closeVotingOnMotion(1);

	}

	/**
	 * Use case 3 C: A motion can be closed for voting greater than 15 minutes after
	 * it was opened.
	 * 
	 * @throws MaximumVoteOnMotionException
	 * @throws MotionException
	 * @throws DuplicateVoteException
	 * @throws VicePresidentVoteException
	 * @throws CloseVotingException
	 */
	@Test(expected = MotionException.class)
	public void closeVotingOnMotion_Motion_Not_Created_Exception() throws MaximumVoteOnMotionException, MotionException,
			DuplicateVoteException, VicePresidentVoteException, CloseVotingException {

		votingMotionManager.setMotionMap(motionMap);

		votingMotionManager.closeVotingOnMotion(1);

	}

	/**
	 * Use case 3 D: A motion can be closed for voting greater than 15 minutes after
	 * it was opened.
	 * 
	 * @throws MaximumVoteOnMotionException
	 * @throws MotionException
	 * @throws DuplicateVoteException
	 * @throws VicePresidentVoteException
	 * @throws CloseVotingException
	 */
	@Test
	public void closeVotingOnMotion_GreaterThan_15_Minutes() throws MaximumVoteOnMotionException, MotionException,
			DuplicateVoteException, VicePresidentVoteException, CloseVotingException {

		constructMotion(1, LocalDateTime.now().minusMinutes(16));

		votingMotionManager.setMotionMap(motionMap);
		votingMotionManager.castVotingOnMotion(1, 1, "Y", false);

		votingMotionManager.closeVotingOnMotion(1);
		assertEquals(MotionStatus.CLOSED, votingMotionManager.getMotionMap().get(1).getMotionStatus());

	}

	/**
	 * Use case 3 E: A motion cannot be closed for voting less than 15 minutes after
	 * it was opened.
	 * 
	 * @throws MaximumVoteOnMotionException
	 * @throws MotionException
	 * @throws DuplicateVoteException
	 * @throws VicePresidentVoteException
	 * @throws CloseVotingException
	 */
	@Test(expected = CloseVotingException.class)
	public void closeVotingOnMotion_MotionDidNot_Started() throws MaximumVoteOnMotionException, MotionException,
			DuplicateVoteException, VicePresidentVoteException, CloseVotingException {

		constructMotion(1, LocalDateTime.now().plusMinutes(50));

		votingMotionManager.setMotionMap(motionMap);

		votingMotionManager.closeVotingOnMotion(1);

	}

	/**
	 * Use Case 4: No voter can vote more than once on the same motion.
	 * 
	 * @throws MaximumVoteOnMotionException
	 * @throws MotionException
	 * @throws DuplicateVoteException
	 * @throws VicePresidentVoteException
	 */
	@Test(expected = DuplicateVoteException.class)
	public void castVotingOnMotion_Duplicate_Votes()
			throws MaximumVoteOnMotionException, MotionException, DuplicateVoteException, VicePresidentVoteException {

		constructMotion(1);
		votingMotionManager.setMotionMap(motionMap);
		votingMotionManager.castVotingOnMotion(1, 1, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 1, "Y", false);

	}

	/**
	 * Use Case 5 A: The maximum votes that can be received on a motion is 101 (100
	 * senators plus the Vicepresident).
	 * 
	 * @throws MaximumVoteOnMotionException
	 * @throws MotionException
	 * @throws DuplicateVoteException
	 * @throws VicePresidentVoteException
	 * @throws CloseVotingException
	 */
	@Test(expected = VicePresidentVoteException.class)
	public void castVotingOnMotion_Max_Votes_101_Motion_Passed() throws MaximumVoteOnMotionException, MotionException,
			DuplicateVoteException, VicePresidentVoteException, CloseVotingException {

		constructMotion(1);

		votingMotionManager.setMotionMap(motionMap);
		votingMotionManager.castVotingOnMotion(1, 1, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 2, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 3, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 4, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 5, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 6, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 7, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 8, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 9, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 10, "Y", false);

		votingMotionManager.castVotingOnMotion(1, 11, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 12, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 13, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 14, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 15, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 16, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 17, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 18, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 19, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 20, "Y", false);

		votingMotionManager.castVotingOnMotion(1, 21, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 22, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 23, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 24, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 25, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 26, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 27, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 28, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 29, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 30, "Y", false);

		votingMotionManager.castVotingOnMotion(1, 31, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 32, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 33, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 34, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 35, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 36, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 37, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 38, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 39, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 40, "Y", false);

		votingMotionManager.castVotingOnMotion(1, 41, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 42, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 43, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 44, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 45, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 46, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 47, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 48, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 49, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 50, "Y", false);

		votingMotionManager.castVotingOnMotion(1, 51, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 52, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 53, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 54, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 55, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 56, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 57, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 58, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 59, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 60, "Y", false);

		votingMotionManager.castVotingOnMotion(1, 61, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 62, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 63, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 64, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 65, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 66, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 67, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 68, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 69, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 70, "Y", false);

		votingMotionManager.castVotingOnMotion(1, 71, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 72, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 73, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 74, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 75, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 76, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 77, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 78, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 79, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 80, "Y", false);

		votingMotionManager.castVotingOnMotion(1, 81, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 82, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 83, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 84, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 85, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 86, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 87, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 88, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 89, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 90, "Y", false);

		votingMotionManager.castVotingOnMotion(1, 91, "N", false);
		votingMotionManager.castVotingOnMotion(1, 92, "N", false);
		votingMotionManager.castVotingOnMotion(1, 93, "N", false);
		votingMotionManager.castVotingOnMotion(1, 94, "N", false);
		votingMotionManager.castVotingOnMotion(1, 95, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 96, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 97, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 98, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 99, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 100, "Y", false);

		votingMotionManager.setMotionState(1);
		assertEquals(MotionState.PASSED, votingMotionManager.getMotionMap().get(1).getMotionState());

		votingMotionManager.castVotingOnMotion(1, 101, "Y", true);

	}

	/**
	 * Use Case 5 B: The maximum votes that can be received on a motion is 101 (100
	 * senators plus the Vicepresident). Vice President votes in Tied state
	 * 
	 * @throws MaximumVoteOnMotionException
	 * @throws MotionException
	 * @throws DuplicateVoteException
	 * @throws VicePresidentVoteException
	 * @throws CloseVotingException
	 */
	@Test
	public void castVotingOnMotion_Max_Votes_101_Motion_Tied() throws MaximumVoteOnMotionException, MotionException,
			DuplicateVoteException, VicePresidentVoteException, CloseVotingException {

		constructMotion(1);

		votingMotionManager.setMotionMap(motionMap);
		votingMotionManager.castVotingOnMotion(1, 1, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 2, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 3, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 4, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 5, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 6, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 7, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 8, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 9, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 10, "Y", false);

		votingMotionManager.castVotingOnMotion(1, 11, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 12, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 13, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 14, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 15, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 16, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 17, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 18, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 19, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 20, "Y", false);

		votingMotionManager.castVotingOnMotion(1, 21, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 22, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 23, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 24, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 25, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 26, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 27, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 28, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 29, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 30, "Y", false);

		votingMotionManager.castVotingOnMotion(1, 31, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 32, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 33, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 34, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 35, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 36, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 37, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 38, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 39, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 40, "Y", false);

		votingMotionManager.castVotingOnMotion(1, 41, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 42, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 43, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 44, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 45, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 46, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 47, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 48, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 49, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 50, "Y", false);

		votingMotionManager.castVotingOnMotion(1, 51, "N", false);
		votingMotionManager.castVotingOnMotion(1, 52, "N", false);
		votingMotionManager.castVotingOnMotion(1, 53, "N", false);
		votingMotionManager.castVotingOnMotion(1, 54, "N", false);
		votingMotionManager.castVotingOnMotion(1, 55, "N", false);
		votingMotionManager.castVotingOnMotion(1, 56, "N", false);
		votingMotionManager.castVotingOnMotion(1, 57, "N", false);
		votingMotionManager.castVotingOnMotion(1, 58, "N", false);
		votingMotionManager.castVotingOnMotion(1, 59, "N", false);
		votingMotionManager.castVotingOnMotion(1, 60, "N", false);

		votingMotionManager.castVotingOnMotion(1, 61, "N", false);
		votingMotionManager.castVotingOnMotion(1, 62, "N", false);
		votingMotionManager.castVotingOnMotion(1, 63, "N", false);
		votingMotionManager.castVotingOnMotion(1, 64, "N", false);
		votingMotionManager.castVotingOnMotion(1, 65, "N", false);
		votingMotionManager.castVotingOnMotion(1, 66, "N", false);
		votingMotionManager.castVotingOnMotion(1, 67, "N", false);
		votingMotionManager.castVotingOnMotion(1, 68, "N", false);
		votingMotionManager.castVotingOnMotion(1, 69, "N", false);
		votingMotionManager.castVotingOnMotion(1, 70, "N", false);

		votingMotionManager.castVotingOnMotion(1, 71, "N", false);
		votingMotionManager.castVotingOnMotion(1, 72, "N", false);
		votingMotionManager.castVotingOnMotion(1, 73, "N", false);
		votingMotionManager.castVotingOnMotion(1, 74, "N", false);
		votingMotionManager.castVotingOnMotion(1, 75, "N", false);
		votingMotionManager.castVotingOnMotion(1, 76, "N", false);
		votingMotionManager.castVotingOnMotion(1, 77, "N", false);
		votingMotionManager.castVotingOnMotion(1, 78, "N", false);
		votingMotionManager.castVotingOnMotion(1, 79, "N", false);
		votingMotionManager.castVotingOnMotion(1, 80, "N", false);

		votingMotionManager.castVotingOnMotion(1, 81, "N", false);
		votingMotionManager.castVotingOnMotion(1, 82, "N", false);
		votingMotionManager.castVotingOnMotion(1, 83, "N", false);
		votingMotionManager.castVotingOnMotion(1, 84, "N", false);
		votingMotionManager.castVotingOnMotion(1, 85, "N", false);
		votingMotionManager.castVotingOnMotion(1, 86, "N", false);
		votingMotionManager.castVotingOnMotion(1, 87, "N", false);
		votingMotionManager.castVotingOnMotion(1, 88, "N", false);
		votingMotionManager.castVotingOnMotion(1, 89, "N", false);
		votingMotionManager.castVotingOnMotion(1, 90, "N", false);

		votingMotionManager.castVotingOnMotion(1, 91, "N", false);
		votingMotionManager.castVotingOnMotion(1, 92, "N", false);
		votingMotionManager.castVotingOnMotion(1, 93, "N", false);
		votingMotionManager.castVotingOnMotion(1, 94, "N", false);
		votingMotionManager.castVotingOnMotion(1, 95, "N", false);
		votingMotionManager.castVotingOnMotion(1, 96, "N", false);
		votingMotionManager.castVotingOnMotion(1, 97, "N", false);
		votingMotionManager.castVotingOnMotion(1, 98, "N", false);
		votingMotionManager.castVotingOnMotion(1, 99, "N", false);
		votingMotionManager.castVotingOnMotion(1, 100, "N", false);

		votingMotionManager.setMotionState(1);
		assertEquals(MotionState.TIED, votingMotionManager.getMotionMap().get(1).getMotionState());

		votingMotionManager.castVotingOnMotion(1, 101, "Y", true);

	}

	/**
	 * Use Case 5 C: The maximum votes that can be received on a motion is 101 (100
	 * senators plus the Vicepresident). Vice President votes in Tied state
	 * 
	 * 102 votes
	 * 
	 * @throws MaximumVoteOnMotionException
	 * @throws MotionException
	 * @throws DuplicateVoteException
	 * @throws VicePresidentVoteException
	 * @throws CloseVotingException
	 */
	@Test(expected = MaximumVoteOnMotionException.class)
	public void castVotingOnMotion_Max_Votes_102() throws MaximumVoteOnMotionException, MotionException,
			DuplicateVoteException, VicePresidentVoteException, CloseVotingException {

		constructMotion(1);

		votingMotionManager.setMotionMap(motionMap);
		votingMotionManager.castVotingOnMotion(1, 1, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 2, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 3, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 4, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 5, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 6, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 7, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 8, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 9, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 10, "Y", false);

		votingMotionManager.castVotingOnMotion(1, 11, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 12, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 13, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 14, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 15, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 16, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 17, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 18, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 19, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 20, "Y", false);

		votingMotionManager.castVotingOnMotion(1, 21, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 22, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 23, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 24, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 25, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 26, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 27, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 28, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 29, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 30, "Y", false);

		votingMotionManager.castVotingOnMotion(1, 31, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 32, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 33, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 34, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 35, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 36, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 37, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 38, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 39, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 40, "Y", false);

		votingMotionManager.castVotingOnMotion(1, 41, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 42, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 43, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 44, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 45, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 46, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 47, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 48, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 49, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 50, "Y", false);

		votingMotionManager.castVotingOnMotion(1, 51, "N", false);
		votingMotionManager.castVotingOnMotion(1, 52, "N", false);
		votingMotionManager.castVotingOnMotion(1, 53, "N", false);
		votingMotionManager.castVotingOnMotion(1, 54, "N", false);
		votingMotionManager.castVotingOnMotion(1, 55, "N", false);
		votingMotionManager.castVotingOnMotion(1, 56, "N", false);
		votingMotionManager.castVotingOnMotion(1, 57, "N", false);
		votingMotionManager.castVotingOnMotion(1, 58, "N", false);
		votingMotionManager.castVotingOnMotion(1, 59, "N", false);
		votingMotionManager.castVotingOnMotion(1, 60, "N", false);

		votingMotionManager.castVotingOnMotion(1, 61, "N", false);
		votingMotionManager.castVotingOnMotion(1, 62, "N", false);
		votingMotionManager.castVotingOnMotion(1, 63, "N", false);
		votingMotionManager.castVotingOnMotion(1, 64, "N", false);
		votingMotionManager.castVotingOnMotion(1, 65, "N", false);
		votingMotionManager.castVotingOnMotion(1, 66, "N", false);
		votingMotionManager.castVotingOnMotion(1, 67, "N", false);
		votingMotionManager.castVotingOnMotion(1, 68, "N", false);
		votingMotionManager.castVotingOnMotion(1, 69, "N", false);
		votingMotionManager.castVotingOnMotion(1, 70, "N", false);

		votingMotionManager.castVotingOnMotion(1, 71, "N", false);
		votingMotionManager.castVotingOnMotion(1, 72, "N", false);
		votingMotionManager.castVotingOnMotion(1, 73, "N", false);
		votingMotionManager.castVotingOnMotion(1, 74, "N", false);
		votingMotionManager.castVotingOnMotion(1, 75, "N", false);
		votingMotionManager.castVotingOnMotion(1, 76, "N", false);
		votingMotionManager.castVotingOnMotion(1, 77, "N", false);
		votingMotionManager.castVotingOnMotion(1, 78, "N", false);
		votingMotionManager.castVotingOnMotion(1, 79, "N", false);
		votingMotionManager.castVotingOnMotion(1, 80, "N", false);

		votingMotionManager.castVotingOnMotion(1, 81, "N", false);
		votingMotionManager.castVotingOnMotion(1, 82, "N", false);
		votingMotionManager.castVotingOnMotion(1, 83, "N", false);
		votingMotionManager.castVotingOnMotion(1, 84, "N", false);
		votingMotionManager.castVotingOnMotion(1, 85, "N", false);
		votingMotionManager.castVotingOnMotion(1, 86, "N", false);
		votingMotionManager.castVotingOnMotion(1, 87, "N", false);
		votingMotionManager.castVotingOnMotion(1, 88, "N", false);
		votingMotionManager.castVotingOnMotion(1, 89, "N", false);
		votingMotionManager.castVotingOnMotion(1, 90, "N", false);

		votingMotionManager.castVotingOnMotion(1, 91, "N", false);
		votingMotionManager.castVotingOnMotion(1, 92, "N", false);
		votingMotionManager.castVotingOnMotion(1, 93, "N", false);
		votingMotionManager.castVotingOnMotion(1, 94, "N", false);
		votingMotionManager.castVotingOnMotion(1, 95, "N", false);
		votingMotionManager.castVotingOnMotion(1, 96, "N", false);
		votingMotionManager.castVotingOnMotion(1, 97, "N", false);
		votingMotionManager.castVotingOnMotion(1, 98, "N", false);
		votingMotionManager.castVotingOnMotion(1, 99, "N", false);
		votingMotionManager.castVotingOnMotion(1, 100, "N", false);

		votingMotionManager.castVotingOnMotion(1, 101, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 102, "Y", false);

	}

	/**
	 * Use Case 6 A: In the tied state, the Vice-president of the United States is
	 * the only person allowed to vote on the motion. Once the VP votes, the motion
	 * is automatically closed. Motion Failed, out of 5 votes , 2 Yes and 3 No(VP
	 * Votes 'N')
	 * 
	 * @throws MaximumVoteOnMotionException
	 * @throws MotionException
	 * @throws DuplicateVoteException
	 * @throws VicePresidentVoteException
	 */
	@Test
	public void castVotingOnMotion_Motion_Tied_VP_Votes_Motion_Closed_Failed()
			throws MaximumVoteOnMotionException, MotionException, DuplicateVoteException, VicePresidentVoteException {

		constructMotion(1);

		votingMotionManager.setMotionMap(motionMap);
		votingMotionManager.castVotingOnMotion(1, 1, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 2, "N", false);
		votingMotionManager.castVotingOnMotion(1, 3, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 4, "N", false);

		votingMotionManager.setMotionState(1);
		assertEquals(MotionState.TIED, votingMotionManager.getMotionMap().get(1).getMotionState());

		votingMotionManager.castVotingOnMotion(1, 5, "N", true);
		assertEquals(MotionStatus.CLOSED, votingMotionManager.getMotionMap().get(1).getMotionStatus());

		votingMotionManager.setMotionState(1);
		assertEquals(MotionState.FAILED, votingMotionManager.getMotionMap().get(1).getMotionState());

	}

	/**
	 * Use Case 6 B: In the tied state, the Vice-president of the United States is
	 * the only person allowed to vote on the motion. Once the VP votes, the motion
	 * is automatically closed. Motion Passed, out of 5 votes , 3 Yes and 2 No(VP
	 * Votes 'Y')
	 * 
	 * @throws MaximumVoteOnMotionException
	 * @throws MotionException
	 * @throws DuplicateVoteException
	 * @throws VicePresidentVoteException
	 */
	@Test
	public void castVotingOnMotion_Motion_Tied_VP_Votes_Motion_Closed_Passed()
			throws MaximumVoteOnMotionException, MotionException, DuplicateVoteException, VicePresidentVoteException {

		constructMotion(1);

		votingMotionManager.setMotionMap(motionMap);
		votingMotionManager.castVotingOnMotion(1, 1, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 2, "N", false);
		votingMotionManager.castVotingOnMotion(1, 3, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 4, "N", false);

		votingMotionManager.setMotionState(1);
		assertEquals(MotionState.TIED, votingMotionManager.getMotionMap().get(1).getMotionState());

		votingMotionManager.castVotingOnMotion(1, 5, "Y", true);
		assertEquals(MotionStatus.CLOSED, votingMotionManager.getMotionMap().get(1).getMotionStatus());

		votingMotionManager.setMotionState(1);
		assertEquals(MotionState.PASSED, votingMotionManager.getMotionMap().get(1).getMotionState());

	}

	/**
	 * Use Case 6 C: In the tied state, the Vice-president of the United States is
	 * the only person allowed to vote on the motion. Once the VP votes, the motion
	 * is automatically closed. Motion Passed, out of 5 votes , 3 Yes and 2 No(VP
	 * Votes 'Y')
	 * 
	 * @throws MaximumVoteOnMotionException
	 * @throws MotionException
	 * @throws DuplicateVoteException
	 * @throws VicePresidentVoteException
	 */
	@Test(expected = VicePresidentVoteException.class)
	public void castVotingOnMotion_Motion_Tied_VP_Votes_Negate_Tied()
			throws MaximumVoteOnMotionException, MotionException, DuplicateVoteException, VicePresidentVoteException {

		constructMotion(1);

		votingMotionManager.setMotionMap(motionMap);
		votingMotionManager.castVotingOnMotion(1, 1, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 2, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 3, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 4, "N", false);

		votingMotionManager.setMotionState(1);
		assertEquals(MotionState.PASSED, votingMotionManager.getMotionMap().get(1).getMotionState());

		votingMotionManager.castVotingOnMotion(1, 5, "Y", true);

	}

	/**
	 * Use Case 6 D: If the VP is not available to vote, then voting can be forced
	 * to the closed state which causes the motion to fail.
	 * 
	 * @throws MaximumVoteOnMotionException
	 * @throws MotionException
	 * @throws DuplicateVoteException
	 * @throws VicePresidentVoteException
	 */
	@Test
	public void castVotingOnMotion_Motion_Tied_VP_Not_Available_Motion_Closed_Failed()
			throws MaximumVoteOnMotionException, MotionException, DuplicateVoteException, VicePresidentVoteException {

		constructMotion(1);

		votingMotionManager.setMotionMap(motionMap);
		votingMotionManager.castVotingOnMotion(1, 1, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 2, "N", false);
		votingMotionManager.castVotingOnMotion(1, 3, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 4, "N", false);

		votingMotionManager.setMotionState(1);
		assertEquals(MotionState.TIED, votingMotionManager.getMotionMap().get(1).getMotionState());

		votingMotionManager.castVotingOnMotion(1, 5, "Y", false);
		assertEquals(MotionStatus.CLOSED, votingMotionManager.getMotionMap().get(1).getMotionStatus());

		assertEquals(MotionState.FAILED, votingMotionManager.getMotionMap().get(1).getMotionState());

	}

	/**
	 * Use Case 7: The code must support a query to discover the current state of a
	 * motion.
	 * 
	 * @throws MaximumVoteOnMotionException
	 * @throws MotionException
	 * @throws DuplicateVoteException
	 * @throws VicePresidentVoteException
	 */
	@Test
	public void current_Motion_State()
			throws MaximumVoteOnMotionException, MotionException, DuplicateVoteException, VicePresidentVoteException {

		constructMotion(1);

		votingMotionManager.setMotionMap(motionMap);
		votingMotionManager.castVotingOnMotion(1, 1, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 2, "N", false);
		votingMotionManager.castVotingOnMotion(1, 3, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 4, "N", false);

		votingMotionManager.setMotionState(1);
		assertEquals(MotionState.TIED.toString(), votingMotionManager.getMotionState(1));

	}

	/**
	 * 
	 * @throws MotionException
	 */
	@Test(expected = MotionException.class)
	public void set_Motion_State_Exception() throws MotionException {

		votingMotionManager.setMotionMap(motionMap);

		votingMotionManager.setMotionState(1);

	}

	/**
	 * 
	 * @throws MotionException
	 * @throws VicePresidentVoteException
	 * @throws DuplicateVoteException
	 * @throws MaximumVoteOnMotionException
	 */
	@Test(expected = MotionException.class)
	public void castVotingOnMotion_MotionException()
			throws MotionException, MaximumVoteOnMotionException, DuplicateVoteException, VicePresidentVoteException {

		votingMotionManager.setMotionMap(motionMap);
		votingMotionManager.castVotingOnMotion(1, 1, "Y", false);

	}

	/**
	 * 
	 * @throws MotionException
	 * @throws VicePresidentVoteException
	 * @throws DuplicateVoteException
	 * @throws MaximumVoteOnMotionException
	 * @throws CloseVotingException
	 */
	@Test(expected = MotionException.class)
	public void castVotingOnMotion_ClosedMotion_MotionException() throws MotionException, MaximumVoteOnMotionException,
			DuplicateVoteException, VicePresidentVoteException, CloseVotingException {

		constructMotion(1, LocalDateTime.now().minusMinutes(30));

		votingMotionManager.setMotionMap(motionMap);
		votingMotionManager.castVotingOnMotion(1, 1, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 2, "N", false);
		votingMotionManager.castVotingOnMotion(1, 3, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 4, "N", false);

		votingMotionManager.closeVotingOnMotion(1);

		votingMotionManager.castVotingOnMotion(1, 4, "N", false);
	}

	/**
	 * 
	 * 
	 * @throws MaximumVoteOnMotionException
	 * @throws MotionException
	 * @throws DuplicateVoteException
	 * @throws VicePresidentVoteException
	 */
	@Test(expected = MotionException.class)
	public void current_Motion_State_Exception()
			throws MaximumVoteOnMotionException, MotionException, DuplicateVoteException, VicePresidentVoteException {

		votingMotionManager.setMotionMap(motionMap);

		votingMotionManager.getMotionState(1);

	}

	/**
	 * Motion Passed , out of 3 votes , 2 Yes and 1 No.
	 * 
	 * @throws MaximumVoteOnMotionException
	 * @throws MotionException
	 * @throws DuplicateVoteException
	 * @throws VicePresidentVoteException
	 */
	@Test
	public void castVotingOnMotion_Motion_State_Pass()
			throws MaximumVoteOnMotionException, MotionException, DuplicateVoteException, VicePresidentVoteException {

		constructMotion(1);

		votingMotionManager.setMotionMap(motionMap);
		votingMotionManager.castVotingOnMotion(1, 1, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 2, "N", false);
		votingMotionManager.castVotingOnMotion(1, 3, "Y", false);

		votingMotionManager.setMotionState(1);

		assertEquals(MotionState.PASSED, votingMotionManager.getMotionMap().get(1).getMotionState());

	}

	/**
	 * Motion Failed , out of 3 votes , 1 Yes and 2 No.
	 * 
	 * @throws MaximumVoteOnMotionException
	 * @throws MotionException
	 * @throws DuplicateVoteException
	 * @throws VicePresidentVoteException
	 */
	@Test
	public void castVotingOnMotion_Motion_State_Failed()
			throws MaximumVoteOnMotionException, MotionException, DuplicateVoteException, VicePresidentVoteException {

		constructMotion(1);

		votingMotionManager.setMotionMap(motionMap);
		votingMotionManager.castVotingOnMotion(1, 1, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 2, "N", false);
		votingMotionManager.castVotingOnMotion(1, 3, "N", false);

		votingMotionManager.setMotionState(1);

		assertEquals(MotionState.FAILED, votingMotionManager.getMotionMap().get(1).getMotionState());

	}

	/**
	 * Motion Tied , out of 4 votes , 2 Yes and 2 No.
	 * 
	 * @throws MaximumVoteOnMotionException
	 * @throws MotionException
	 * @throws DuplicateVoteException
	 * @throws VicePresidentVoteException
	 */
	@Test
	public void castVotingOnMotion_Motion_State_Tied()
			throws MaximumVoteOnMotionException, MotionException, DuplicateVoteException, VicePresidentVoteException {

		constructMotion(1);

		votingMotionManager.setMotionMap(motionMap);
		votingMotionManager.castVotingOnMotion(1, 1, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 2, "N", false);
		votingMotionManager.castVotingOnMotion(1, 3, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 4, "N", false);

		votingMotionManager.setMotionState(1);

		assertEquals(MotionState.TIED, votingMotionManager.getMotionMap().get(1).getMotionState());

	}

	/**
	 * Motion 1 Passed , out of 3 votes , 2 Yes and 1 No. Motion 2 Failed , out of 3
	 * votes , 1 Yes and 2 No. Motion 3 Tied , out of 4 votes , 2 Yes and 2 No.
	 * 
	 * @throws MaximumVoteOnMotionException
	 * @throws MotionException
	 * @throws DuplicateVoteException
	 * @throws VicePresidentVoteException
	 */
	@Test
	public void castVotingOnMotion_Three_Motions()
			throws MaximumVoteOnMotionException, MotionException, DuplicateVoteException, VicePresidentVoteException {

		constructMotion(1);

		constructMotion(2);

		constructMotion(3);

		votingMotionManager.setMotionMap(motionMap);

		votingMotionManager.castVotingOnMotion(1, 1, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 2, "N", false);
		votingMotionManager.castVotingOnMotion(1, 3, "Y", false);

		votingMotionManager.castVotingOnMotion(2, 1, "Y", false);
		votingMotionManager.castVotingOnMotion(2, 2, "N", false);
		votingMotionManager.castVotingOnMotion(2, 3, "N", false);

		votingMotionManager.castVotingOnMotion(3, 1, "Y", false);
		votingMotionManager.castVotingOnMotion(3, 2, "N", false);
		votingMotionManager.castVotingOnMotion(3, 3, "Y", false);
		votingMotionManager.castVotingOnMotion(3, 4, "N", false);

		votingMotionManager.setMotionState(1);
		votingMotionManager.setMotionState(2);
		votingMotionManager.setMotionState(3);

		assertEquals(MotionState.PASSED, votingMotionManager.getMotionMap().get(1).getMotionState());
		assertEquals(MotionState.FAILED, votingMotionManager.getMotionMap().get(2).getMotionState());
		assertEquals(MotionState.TIED, votingMotionManager.getMotionMap().get(3).getMotionState());

	}

	/**
	 * Checking Voter State(Yes/No)
	 * 
	 * @throws MaximumVoteOnMotionException
	 * @throws MotionException
	 * @throws DuplicateVoteException
	 * @throws VicePresidentVoteException
	 */
	@Test
	public void castVotingOnMotion_Voter_State()
			throws MaximumVoteOnMotionException, MotionException, DuplicateVoteException, VicePresidentVoteException {

		constructMotion(1);

		votingMotionManager.setMotionMap(motionMap);
		votingMotionManager.castVotingOnMotion(1, 1, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 2, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 3, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 4, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 5, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 6, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 7, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 8, "N", false);
		votingMotionManager.castVotingOnMotion(1, 9, "Y", false);
		votingMotionManager.castVotingOnMotion(1, 10, "Y", false);

		assertEquals("N", votingMotionManager.getMotionMap().get(1).getVoters().stream()
				.filter(m -> m.getVoterId() == 8).findAny().get().getVoteState());

	}

	/**
	 * 
	 * @param motionId
	 */
	private void constructMotion(final int motionId) {
		Motion motion = new Motion();
		motion.setMotionId(motionId);
		motion.setOpenedTime(LocalDateTime.now());
		motionMap.put(motionId, motion);
	}

	/**
	 * 
	 * @param motionId
	 * @param time
	 */
	private void constructMotion(final int motionId, final LocalDateTime time) {
		Motion motion = new Motion();
		motion.setMotionId(motionId);
		motion.setOpenedTime(time);
		motionMap.put(motionId, motion);
	}

}
