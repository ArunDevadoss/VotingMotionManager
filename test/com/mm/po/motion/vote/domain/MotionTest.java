package com.mm.po.motion.vote.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.LocalDateTime;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mm.po.motion.vote.enums.MotionState;
import com.mm.po.motion.vote.enums.MotionStatus;
import com.mm.po.motion.vote.enums.VoteState;
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
public class MotionTest {

	@Before
	public void setUp() {
	}

	@After
	public void tearDown() {
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

		Motion motion = constructMotion(LocalDateTime.now().plusMinutes(10));

		motion.castVotingOnMotion(1, VoteState.Y, false);

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

		Motion motionOne = constructMotion(LocalDateTime.now().minusMinutes(30));

		motionOne.castVotingOnMotion(1, VoteState.Y, false);
		motionOne.castVotingOnMotion(2, VoteState.Y, false);
		motionOne.castVotingOnMotion(3, VoteState.Y, false);
		motionOne.castVotingOnMotion(4, VoteState.N, false);
		motionOne.closeVotingOnMotion();
		motionOne.setMotionState();
		assertEquals(MotionState.PASSED, motionOne.getMotionState());
		assertEquals(MotionStatus.CLOSED, motionOne.getMotionStatus());

		assertEquals(3, motionOne.getYesVoteCounts());
		assertEquals(1, motionOne.getNoVoteCounts());
		assertNotNull(motionOne.getOpenedTime());
		assertNotNull(motionOne.getClosedTime());
		motionOne.clearVoters();

		Motion motionTwo = constructMotion(LocalDateTime.now().minusMinutes(30));
		motionTwo.castVotingOnMotion(1, VoteState.Y, false);
		motionTwo.castVotingOnMotion(2, VoteState.N, false);
		motionTwo.castVotingOnMotion(3, VoteState.N, false);
		motionTwo.castVotingOnMotion(4, VoteState.N, false);
		motionTwo.closeVotingOnMotion();

		assertEquals(MotionStatus.CLOSED, motionTwo.getMotionStatus());
		motionTwo.setMotionState();
		assertEquals(MotionState.FAILED, motionTwo.getMotionState());

		assertEquals(1, motionTwo.getYesVoteCounts());
		assertEquals(3, motionTwo.getNoVoteCounts());
		assertNotNull(motionTwo.getOpenedTime());
		assertNotNull(motionTwo.getClosedTime());

	}

	/**
	 * Use Case 2 B: When a motion is closed for voting, a result is returned that
	 * describes o whether the motion passed or failed o the number of yes and no
	 * votes o the time that voting opened and closed
	 * 
	 * @throws MotionException
	 */

	// @Test(expected = MotionException.class)
	// public void get_Motion_Results() throws MotionException {
	//
	// motion.getMotionResult(1);
	//
	// }

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

		Motion motion = constructMotion(LocalDateTime.now().minusMinutes(10));

		motion.castVotingOnMotion(1, VoteState.Y, false);

		motion.closeVotingOnMotion();

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

		Motion motion = constructMotion(LocalDateTime.now().minusMinutes(16));

		motion.castVotingOnMotion(1, VoteState.Y, false);

		motion.closeVotingOnMotion();
		motion.closeVotingOnMotion();

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

		Motion motion = constructMotion(LocalDateTime.now().minusMinutes(16));

		motion.castVotingOnMotion(1, VoteState.Y, false);

		motion.closeVotingOnMotion();
		assertEquals(MotionStatus.CLOSED, motion.getMotionStatus());

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

		Motion motion = constructMotion(LocalDateTime.now().plusMinutes(50));

		motion.closeVotingOnMotion();

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

		Motion motion = constructMotion();
		motion.castVotingOnMotion(1, VoteState.Y, false);
		motion.castVotingOnMotion(1, VoteState.Y, false);

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

		Motion motion = constructMotion();

		motion.castVotingOnMotion(1, VoteState.Y, false);
		motion.castVotingOnMotion(2, VoteState.Y, false);
		motion.castVotingOnMotion(3, VoteState.Y, false);
		motion.castVotingOnMotion(4, VoteState.Y, false);
		motion.castVotingOnMotion(5, VoteState.Y, false);
		motion.castVotingOnMotion(6, VoteState.Y, false);
		motion.castVotingOnMotion(7, VoteState.Y, false);
		motion.castVotingOnMotion(8, VoteState.Y, false);
		motion.castVotingOnMotion(9, VoteState.Y, false);
		motion.castVotingOnMotion(10, VoteState.Y, false);

		motion.castVotingOnMotion(11, VoteState.Y, false);
		motion.castVotingOnMotion(12, VoteState.Y, false);
		motion.castVotingOnMotion(13, VoteState.Y, false);
		motion.castVotingOnMotion(14, VoteState.Y, false);
		motion.castVotingOnMotion(15, VoteState.Y, false);
		motion.castVotingOnMotion(16, VoteState.Y, false);
		motion.castVotingOnMotion(17, VoteState.Y, false);
		motion.castVotingOnMotion(18, VoteState.Y, false);
		motion.castVotingOnMotion(19, VoteState.Y, false);
		motion.castVotingOnMotion(20, VoteState.Y, false);

		motion.castVotingOnMotion(21, VoteState.Y, false);
		motion.castVotingOnMotion(22, VoteState.Y, false);
		motion.castVotingOnMotion(23, VoteState.Y, false);
		motion.castVotingOnMotion(24, VoteState.Y, false);
		motion.castVotingOnMotion(25, VoteState.Y, false);
		motion.castVotingOnMotion(26, VoteState.Y, false);
		motion.castVotingOnMotion(27, VoteState.Y, false);
		motion.castVotingOnMotion(28, VoteState.Y, false);
		motion.castVotingOnMotion(29, VoteState.Y, false);
		motion.castVotingOnMotion(30, VoteState.Y, false);

		motion.castVotingOnMotion(31, VoteState.Y, false);
		motion.castVotingOnMotion(32, VoteState.Y, false);
		motion.castVotingOnMotion(33, VoteState.Y, false);
		motion.castVotingOnMotion(34, VoteState.Y, false);
		motion.castVotingOnMotion(35, VoteState.Y, false);
		motion.castVotingOnMotion(36, VoteState.Y, false);
		motion.castVotingOnMotion(37, VoteState.Y, false);
		motion.castVotingOnMotion(38, VoteState.Y, false);
		motion.castVotingOnMotion(39, VoteState.Y, false);
		motion.castVotingOnMotion(40, VoteState.Y, false);

		motion.castVotingOnMotion(41, VoteState.Y, false);
		motion.castVotingOnMotion(42, VoteState.Y, false);
		motion.castVotingOnMotion(43, VoteState.Y, false);
		motion.castVotingOnMotion(44, VoteState.Y, false);
		motion.castVotingOnMotion(45, VoteState.Y, false);
		motion.castVotingOnMotion(46, VoteState.Y, false);
		motion.castVotingOnMotion(47, VoteState.Y, false);
		motion.castVotingOnMotion(48, VoteState.Y, false);
		motion.castVotingOnMotion(49, VoteState.Y, false);
		motion.castVotingOnMotion(50, VoteState.Y, false);

		motion.castVotingOnMotion(51, VoteState.Y, false);
		motion.castVotingOnMotion(52, VoteState.Y, false);
		motion.castVotingOnMotion(53, VoteState.Y, false);
		motion.castVotingOnMotion(54, VoteState.Y, false);
		motion.castVotingOnMotion(55, VoteState.Y, false);
		motion.castVotingOnMotion(56, VoteState.Y, false);
		motion.castVotingOnMotion(57, VoteState.Y, false);
		motion.castVotingOnMotion(58, VoteState.Y, false);
		motion.castVotingOnMotion(59, VoteState.Y, false);
		motion.castVotingOnMotion(60, VoteState.Y, false);

		motion.castVotingOnMotion(61, VoteState.Y, false);
		motion.castVotingOnMotion(62, VoteState.Y, false);
		motion.castVotingOnMotion(63, VoteState.Y, false);
		motion.castVotingOnMotion(64, VoteState.Y, false);
		motion.castVotingOnMotion(65, VoteState.Y, false);
		motion.castVotingOnMotion(66, VoteState.Y, false);
		motion.castVotingOnMotion(67, VoteState.Y, false);
		motion.castVotingOnMotion(68, VoteState.Y, false);
		motion.castVotingOnMotion(69, VoteState.Y, false);
		motion.castVotingOnMotion(70, VoteState.Y, false);

		motion.castVotingOnMotion(71, VoteState.Y, false);
		motion.castVotingOnMotion(72, VoteState.Y, false);
		motion.castVotingOnMotion(73, VoteState.Y, false);
		motion.castVotingOnMotion(74, VoteState.Y, false);
		motion.castVotingOnMotion(75, VoteState.Y, false);
		motion.castVotingOnMotion(76, VoteState.Y, false);
		motion.castVotingOnMotion(77, VoteState.Y, false);
		motion.castVotingOnMotion(78, VoteState.Y, false);
		motion.castVotingOnMotion(79, VoteState.Y, false);
		motion.castVotingOnMotion(80, VoteState.Y, false);

		motion.castVotingOnMotion(81, VoteState.Y, false);
		motion.castVotingOnMotion(82, VoteState.Y, false);
		motion.castVotingOnMotion(83, VoteState.Y, false);
		motion.castVotingOnMotion(84, VoteState.Y, false);
		motion.castVotingOnMotion(85, VoteState.Y, false);
		motion.castVotingOnMotion(86, VoteState.Y, false);
		motion.castVotingOnMotion(87, VoteState.Y, false);
		motion.castVotingOnMotion(88, VoteState.Y, false);
		motion.castVotingOnMotion(89, VoteState.Y, false);
		motion.castVotingOnMotion(90, VoteState.Y, false);

		motion.castVotingOnMotion(91, VoteState.N, false);
		motion.castVotingOnMotion(92, VoteState.N, false);
		motion.castVotingOnMotion(93, VoteState.N, false);
		motion.castVotingOnMotion(94, VoteState.N, false);
		motion.castVotingOnMotion(95, VoteState.Y, false);
		motion.castVotingOnMotion(96, VoteState.Y, false);
		motion.castVotingOnMotion(97, VoteState.Y, false);
		motion.castVotingOnMotion(98, VoteState.Y, false);
		motion.castVotingOnMotion(99, VoteState.Y, false);
		motion.castVotingOnMotion(100, VoteState.Y, false);

		motion.setMotionState();
		assertEquals(MotionState.PASSED, motion.getMotionState());

		motion.castVotingOnMotion(101, VoteState.Y, true);

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

		Motion motion = constructMotion();

		motion.castVotingOnMotion(1, VoteState.Y, false);
		motion.castVotingOnMotion(2, VoteState.Y, false);
		motion.castVotingOnMotion(3, VoteState.Y, false);
		motion.castVotingOnMotion(4, VoteState.Y, false);
		motion.castVotingOnMotion(5, VoteState.Y, false);
		motion.castVotingOnMotion(6, VoteState.Y, false);
		motion.castVotingOnMotion(7, VoteState.Y, false);
		motion.castVotingOnMotion(8, VoteState.Y, false);
		motion.castVotingOnMotion(9, VoteState.Y, false);
		motion.castVotingOnMotion(10, VoteState.Y, false);

		motion.castVotingOnMotion(11, VoteState.Y, false);
		motion.castVotingOnMotion(12, VoteState.Y, false);
		motion.castVotingOnMotion(13, VoteState.Y, false);
		motion.castVotingOnMotion(14, VoteState.Y, false);
		motion.castVotingOnMotion(15, VoteState.Y, false);
		motion.castVotingOnMotion(16, VoteState.Y, false);
		motion.castVotingOnMotion(17, VoteState.Y, false);
		motion.castVotingOnMotion(18, VoteState.Y, false);
		motion.castVotingOnMotion(19, VoteState.Y, false);
		motion.castVotingOnMotion(20, VoteState.Y, false);

		motion.castVotingOnMotion(21, VoteState.Y, false);
		motion.castVotingOnMotion(22, VoteState.Y, false);
		motion.castVotingOnMotion(23, VoteState.Y, false);
		motion.castVotingOnMotion(24, VoteState.Y, false);
		motion.castVotingOnMotion(25, VoteState.Y, false);
		motion.castVotingOnMotion(26, VoteState.Y, false);
		motion.castVotingOnMotion(27, VoteState.Y, false);
		motion.castVotingOnMotion(28, VoteState.Y, false);
		motion.castVotingOnMotion(29, VoteState.Y, false);
		motion.castVotingOnMotion(30, VoteState.Y, false);

		motion.castVotingOnMotion(31, VoteState.Y, false);
		motion.castVotingOnMotion(32, VoteState.Y, false);
		motion.castVotingOnMotion(33, VoteState.Y, false);
		motion.castVotingOnMotion(34, VoteState.Y, false);
		motion.castVotingOnMotion(35, VoteState.Y, false);
		motion.castVotingOnMotion(36, VoteState.Y, false);
		motion.castVotingOnMotion(37, VoteState.Y, false);
		motion.castVotingOnMotion(38, VoteState.Y, false);
		motion.castVotingOnMotion(39, VoteState.Y, false);
		motion.castVotingOnMotion(40, VoteState.Y, false);

		motion.castVotingOnMotion(41, VoteState.Y, false);
		motion.castVotingOnMotion(42, VoteState.Y, false);
		motion.castVotingOnMotion(43, VoteState.Y, false);
		motion.castVotingOnMotion(44, VoteState.Y, false);
		motion.castVotingOnMotion(45, VoteState.Y, false);
		motion.castVotingOnMotion(46, VoteState.Y, false);
		motion.castVotingOnMotion(47, VoteState.Y, false);
		motion.castVotingOnMotion(48, VoteState.Y, false);
		motion.castVotingOnMotion(49, VoteState.Y, false);
		motion.castVotingOnMotion(50, VoteState.Y, false);

		motion.castVotingOnMotion(51, VoteState.N, false);
		motion.castVotingOnMotion(52, VoteState.N, false);
		motion.castVotingOnMotion(53, VoteState.N, false);
		motion.castVotingOnMotion(54, VoteState.N, false);
		motion.castVotingOnMotion(55, VoteState.N, false);
		motion.castVotingOnMotion(56, VoteState.N, false);
		motion.castVotingOnMotion(57, VoteState.N, false);
		motion.castVotingOnMotion(58, VoteState.N, false);
		motion.castVotingOnMotion(59, VoteState.N, false);
		motion.castVotingOnMotion(60, VoteState.N, false);

		motion.castVotingOnMotion(61, VoteState.N, false);
		motion.castVotingOnMotion(62, VoteState.N, false);
		motion.castVotingOnMotion(63, VoteState.N, false);
		motion.castVotingOnMotion(64, VoteState.N, false);
		motion.castVotingOnMotion(65, VoteState.N, false);
		motion.castVotingOnMotion(66, VoteState.N, false);
		motion.castVotingOnMotion(67, VoteState.N, false);
		motion.castVotingOnMotion(68, VoteState.N, false);
		motion.castVotingOnMotion(69, VoteState.N, false);
		motion.castVotingOnMotion(70, VoteState.N, false);

		motion.castVotingOnMotion(71, VoteState.N, false);
		motion.castVotingOnMotion(72, VoteState.N, false);
		motion.castVotingOnMotion(73, VoteState.N, false);
		motion.castVotingOnMotion(74, VoteState.N, false);
		motion.castVotingOnMotion(75, VoteState.N, false);
		motion.castVotingOnMotion(76, VoteState.N, false);
		motion.castVotingOnMotion(77, VoteState.N, false);
		motion.castVotingOnMotion(78, VoteState.N, false);
		motion.castVotingOnMotion(79, VoteState.N, false);
		motion.castVotingOnMotion(80, VoteState.N, false);

		motion.castVotingOnMotion(81, VoteState.N, false);
		motion.castVotingOnMotion(82, VoteState.N, false);
		motion.castVotingOnMotion(83, VoteState.N, false);
		motion.castVotingOnMotion(84, VoteState.N, false);
		motion.castVotingOnMotion(85, VoteState.N, false);
		motion.castVotingOnMotion(86, VoteState.N, false);
		motion.castVotingOnMotion(87, VoteState.N, false);
		motion.castVotingOnMotion(88, VoteState.N, false);
		motion.castVotingOnMotion(89, VoteState.N, false);
		motion.castVotingOnMotion(90, VoteState.N, false);

		motion.castVotingOnMotion(91, VoteState.N, false);
		motion.castVotingOnMotion(92, VoteState.N, false);
		motion.castVotingOnMotion(93, VoteState.N, false);
		motion.castVotingOnMotion(94, VoteState.N, false);
		motion.castVotingOnMotion(95, VoteState.N, false);
		motion.castVotingOnMotion(96, VoteState.N, false);
		motion.castVotingOnMotion(97, VoteState.N, false);
		motion.castVotingOnMotion(98, VoteState.N, false);
		motion.castVotingOnMotion(99, VoteState.N, false);
		motion.castVotingOnMotion(100, VoteState.N, false);

		motion.setMotionState();
		assertEquals(MotionState.TIED, motion.getMotionState());

		motion.castVotingOnMotion(101, VoteState.Y, true);

	}

	/**
	 * 
	 * Use Case 5 C:The maximum votes that can be received on a motion is 101(100
	 * senators plus the Vicepresident). Vice President votes in Tied state 102
	 * votes
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

		Motion motion = constructMotion();

		motion.castVotingOnMotion(1, VoteState.Y, false);
		motion.castVotingOnMotion(2, VoteState.Y, false);
		motion.castVotingOnMotion(3, VoteState.Y, false);
		motion.castVotingOnMotion(4, VoteState.Y, false);
		motion.castVotingOnMotion(5, VoteState.Y, false);
		motion.castVotingOnMotion(6, VoteState.Y, false);
		motion.castVotingOnMotion(7, VoteState.Y, false);
		motion.castVotingOnMotion(8, VoteState.Y, false);
		motion.castVotingOnMotion(9, VoteState.Y, false);
		motion.castVotingOnMotion(10, VoteState.Y, false);

		motion.castVotingOnMotion(11, VoteState.Y, false);
		motion.castVotingOnMotion(12, VoteState.Y, false);
		motion.castVotingOnMotion(13, VoteState.Y, false);
		motion.castVotingOnMotion(14, VoteState.Y, false);
		motion.castVotingOnMotion(15, VoteState.Y, false);
		motion.castVotingOnMotion(16, VoteState.Y, false);
		motion.castVotingOnMotion(17, VoteState.Y, false);
		motion.castVotingOnMotion(18, VoteState.Y, false);
		motion.castVotingOnMotion(19, VoteState.Y, false);
		motion.castVotingOnMotion(20, VoteState.Y, false);

		motion.castVotingOnMotion(21, VoteState.Y, false);
		motion.castVotingOnMotion(22, VoteState.Y, false);
		motion.castVotingOnMotion(23, VoteState.Y, false);
		motion.castVotingOnMotion(24, VoteState.Y, false);
		motion.castVotingOnMotion(25, VoteState.Y, false);
		motion.castVotingOnMotion(26, VoteState.Y, false);
		motion.castVotingOnMotion(27, VoteState.Y, false);
		motion.castVotingOnMotion(28, VoteState.Y, false);
		motion.castVotingOnMotion(29, VoteState.Y, false);
		motion.castVotingOnMotion(30, VoteState.Y, false);

		motion.castVotingOnMotion(31, VoteState.Y, false);
		motion.castVotingOnMotion(32, VoteState.Y, false);
		motion.castVotingOnMotion(33, VoteState.Y, false);
		motion.castVotingOnMotion(34, VoteState.Y, false);
		motion.castVotingOnMotion(35, VoteState.Y, false);
		motion.castVotingOnMotion(36, VoteState.Y, false);
		motion.castVotingOnMotion(37, VoteState.Y, false);
		motion.castVotingOnMotion(38, VoteState.Y, false);
		motion.castVotingOnMotion(39, VoteState.Y, false);
		motion.castVotingOnMotion(40, VoteState.Y, false);

		motion.castVotingOnMotion(41, VoteState.Y, false);
		motion.castVotingOnMotion(42, VoteState.Y, false);
		motion.castVotingOnMotion(43, VoteState.Y, false);
		motion.castVotingOnMotion(44, VoteState.Y, false);
		motion.castVotingOnMotion(45, VoteState.Y, false);
		motion.castVotingOnMotion(46, VoteState.Y, false);
		motion.castVotingOnMotion(47, VoteState.Y, false);
		motion.castVotingOnMotion(48, VoteState.Y, false);
		motion.castVotingOnMotion(49, VoteState.Y, false);
		motion.castVotingOnMotion(50, VoteState.Y, false);

		motion.castVotingOnMotion(51, VoteState.N, false);
		motion.castVotingOnMotion(52, VoteState.N, false);
		motion.castVotingOnMotion(53, VoteState.N, false);
		motion.castVotingOnMotion(54, VoteState.N, false);
		motion.castVotingOnMotion(55, VoteState.N, false);
		motion.castVotingOnMotion(56, VoteState.N, false);
		motion.castVotingOnMotion(57, VoteState.N, false);
		motion.castVotingOnMotion(58, VoteState.N, false);
		motion.castVotingOnMotion(59, VoteState.N, false);
		motion.castVotingOnMotion(60, VoteState.N, false);

		motion.castVotingOnMotion(61, VoteState.N, false);
		motion.castVotingOnMotion(62, VoteState.N, false);
		motion.castVotingOnMotion(63, VoteState.N, false);
		motion.castVotingOnMotion(64, VoteState.N, false);
		motion.castVotingOnMotion(65, VoteState.N, false);
		motion.castVotingOnMotion(66, VoteState.N, false);
		motion.castVotingOnMotion(67, VoteState.N, false);
		motion.castVotingOnMotion(68, VoteState.N, false);
		motion.castVotingOnMotion(69, VoteState.N, false);
		motion.castVotingOnMotion(70, VoteState.N, false);

		motion.castVotingOnMotion(71, VoteState.N, false);
		motion.castVotingOnMotion(72, VoteState.N, false);
		motion.castVotingOnMotion(73, VoteState.N, false);
		motion.castVotingOnMotion(74, VoteState.N, false);
		motion.castVotingOnMotion(75, VoteState.N, false);
		motion.castVotingOnMotion(76, VoteState.N, false);
		motion.castVotingOnMotion(77, VoteState.N, false);
		motion.castVotingOnMotion(78, VoteState.N, false);
		motion.castVotingOnMotion(79, VoteState.N, false);
		motion.castVotingOnMotion(80, VoteState.N, false);

		motion.castVotingOnMotion(81, VoteState.N, false);
		motion.castVotingOnMotion(82, VoteState.N, false);
		motion.castVotingOnMotion(83, VoteState.N, false);
		motion.castVotingOnMotion(84, VoteState.N, false);
		motion.castVotingOnMotion(85, VoteState.N, false);
		motion.castVotingOnMotion(86, VoteState.N, false);
		motion.castVotingOnMotion(87, VoteState.N, false);
		motion.castVotingOnMotion(88, VoteState.N, false);
		motion.castVotingOnMotion(89, VoteState.N, false);
		motion.castVotingOnMotion(90, VoteState.N, false);

		motion.castVotingOnMotion(91, VoteState.N, false);
		motion.castVotingOnMotion(92, VoteState.N, false);
		motion.castVotingOnMotion(93, VoteState.N, false);
		motion.castVotingOnMotion(94, VoteState.N, false);
		motion.castVotingOnMotion(95, VoteState.N, false);
		motion.castVotingOnMotion(96, VoteState.N, false);
		motion.castVotingOnMotion(97, VoteState.N, false);
		motion.castVotingOnMotion(98, VoteState.N, false);
		motion.castVotingOnMotion(99, VoteState.N, false);
		motion.castVotingOnMotion(100, VoteState.N, false);

		motion.castVotingOnMotion(101, VoteState.Y, false);
		motion.castVotingOnMotion(102, VoteState.Y, false);

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

		Motion motion = constructMotion();

		motion.castVotingOnMotion(1, VoteState.Y, false);
		motion.castVotingOnMotion(2, VoteState.N, false);
		motion.castVotingOnMotion(3, VoteState.Y, false);
		motion.castVotingOnMotion(4, VoteState.N, false);

		motion.setMotionState();
		assertEquals(MotionState.TIED, motion.getMotionState());

		motion.castVotingOnMotion(5, VoteState.N, true);
		assertEquals(MotionStatus.CLOSED, motion.getMotionStatus());

		motion.setMotionState();
		assertEquals(MotionState.FAILED, motion.getMotionState());

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

		Motion motion = constructMotion();

		motion.castVotingOnMotion(1, VoteState.Y, false);
		motion.castVotingOnMotion(2, VoteState.N, false);
		motion.castVotingOnMotion(3, VoteState.Y, false);
		motion.castVotingOnMotion(4, VoteState.N, false);

		motion.setMotionState();
		assertEquals(MotionState.TIED, motion.getMotionState());

		motion.castVotingOnMotion(5, VoteState.Y, true);
		assertEquals(MotionStatus.CLOSED, motion.getMotionStatus());

		motion.setMotionState();
		assertEquals(MotionState.PASSED, motion.getMotionState());

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

		Motion motion = constructMotion();

		motion.castVotingOnMotion(1, VoteState.Y, false);
		motion.castVotingOnMotion(2, VoteState.Y, false);
		motion.castVotingOnMotion(3, VoteState.Y, false);
		motion.castVotingOnMotion(4, VoteState.N, false);

		motion.setMotionState();
		assertEquals(MotionState.PASSED, motion.getMotionState());

		motion.castVotingOnMotion(5, VoteState.Y, true);

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

		Motion motion = constructMotion();

		motion.castVotingOnMotion(1, VoteState.Y, false);
		motion.castVotingOnMotion(2, VoteState.N, false);
		motion.castVotingOnMotion(3, VoteState.Y, false);
		motion.castVotingOnMotion(4, VoteState.N, false);

		motion.setMotionState();
		assertEquals(MotionState.TIED, motion.getMotionState());

		motion.castVotingOnMotion(5, VoteState.Y, false);
		assertEquals(MotionStatus.CLOSED, motion.getMotionStatus());

		assertEquals(MotionState.FAILED, motion.getMotionState());

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

		Motion motion = constructMotion();

		motion.castVotingOnMotion(1, VoteState.Y, false);
		motion.castVotingOnMotion(2, VoteState.N, false);
		motion.castVotingOnMotion(3, VoteState.Y, false);
		motion.castVotingOnMotion(4, VoteState.N, false);

		motion.setMotionState();
		assertEquals(MotionState.TIED, motion.getMotionState());

	}

	// /**
	// *
	// * @throws MotionException
	// */
	//
	// @Test(expected = MotionException.class)
	// public void set_Motion_State_Exception() throws MotionException {
	//
	// motion.setMotionMap(motionMap);
	//
	// motion.setMotionState(1);
	//
	// }

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

		Motion motion = constructMotion(LocalDateTime.now().minusMinutes(30));

		motion.castVotingOnMotion(1, VoteState.Y, false);
		motion.castVotingOnMotion(2, VoteState.N, false);
		motion.castVotingOnMotion(3, VoteState.Y, false);
		motion.castVotingOnMotion(4, VoteState.N, false);

		motion.closeVotingOnMotion();

		motion.castVotingOnMotion(4, VoteState.N, false);
	}

	/**
	 * 
	 * 
	 * @throws MaximumVoteOnMotionException
	 * @throws MotionException
	 * @throws DuplicateVoteException
	 * @throws VicePresidentVoteException
	 */

	// @Test(expected = MotionException.class)
	// public void current_Motion_State_Exception()
	// throws MaximumVoteOnMotionException, MotionException, DuplicateVoteException,
	// VicePresidentVoteException {
	//
	// motion.setMotionMap(motionMap);
	//
	// motion.getMotionState(1);
	//
	// }

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

		Motion motion = constructMotion();

		motion.castVotingOnMotion(1, VoteState.Y, false);
		motion.castVotingOnMotion(2, VoteState.N, false);
		motion.castVotingOnMotion(3, VoteState.Y, false);

		motion.setMotionState();

		assertEquals(MotionState.PASSED, motion.getMotionState());

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

		Motion motion = constructMotion();

		motion.castVotingOnMotion(1, VoteState.Y, false);
		motion.castVotingOnMotion(2, VoteState.N, false);
		motion.castVotingOnMotion(3, VoteState.N, false);

		motion.setMotionState();

		assertEquals(MotionState.FAILED, motion.getMotionState());

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

		Motion motion = constructMotion();

		motion.castVotingOnMotion(1, VoteState.Y, false);
		motion.castVotingOnMotion(2, VoteState.N, false);
		motion.castVotingOnMotion(3, VoteState.Y, false);
		motion.castVotingOnMotion(4, VoteState.N, false);

		motion.setMotionState();

		assertEquals(MotionState.TIED, motion.getMotionState());

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

	// @Test
	// public void castVotingOnMotion_Three_Motions()
	// throws MaximumVoteOnMotionException, MotionException, DuplicateVoteException,
	// VicePresidentVoteException {
	//
	// constructMotion(1);
	//
	// constructMotion(2);
	//
	// constructMotion(3);
	//
	// motion.setMotionMap(motionMap);
	//
	// motion.castVotingOnMotion(1, 1, VoteState.Y, false);
	// motion.castVotingOnMotion(1, 2, VoteState.N, false);
	// motion.castVotingOnMotion(1, 3, VoteState.Y, false);
	//
	// motion.castVotingOnMotion(2, 1, VoteState.Y, false);
	// motion.castVotingOnMotion(2, 2, VoteState.N, false);
	// motion.castVotingOnMotion(2, 3, VoteState.N, false);
	//
	// motion.castVotingOnMotion(3, 1, VoteState.Y, false);
	// motion.castVotingOnMotion(3, 2, VoteState.N, false);
	// motion.castVotingOnMotion(3, 3, VoteState.Y, false);
	// motion.castVotingOnMotion(3, 4, VoteState.N, false);
	//
	// motion.setMotionState(1);
	// motion.setMotionState(2);
	// motion.setMotionState(3);
	//
	// assertEquals(MotionState.PASSED,
	// motion.getMotionMap().get(1).getMotionState());
	// assertEquals(MotionState.FAILED,
	// motion.getMotionMap().get(2).getMotionState());
	// assertEquals(MotionState.TIED,
	// motion.getMotionMap().get(3).getMotionState());
	//
	// }
	//
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

		Motion motion = constructMotion();

		motion.castVotingOnMotion(1, VoteState.Y, false);
		motion.castVotingOnMotion(2, VoteState.Y, false);
		motion.castVotingOnMotion(3, VoteState.Y, false);
		motion.castVotingOnMotion(4, VoteState.Y, false);
		motion.castVotingOnMotion(5, VoteState.Y, false);
		motion.castVotingOnMotion(6, VoteState.Y, false);
		motion.castVotingOnMotion(7, VoteState.Y, false);
		motion.castVotingOnMotion(8, VoteState.N, false);
		motion.castVotingOnMotion(9, VoteState.Y, false);
		motion.castVotingOnMotion(10, VoteState.Y, false);

		assertEquals(VoteState.N,
				motion.getVoters().stream().filter(m -> m.getVoterId() == 8).findAny().get().getVoteState());

	}

	/**
	 * TODO: Create Constructor in Motion
	 * 
	 * @param motionId
	 */

	private Motion constructMotion() {
		Motion motion = new Motion();
		motion.setOpenedTime(LocalDateTime.now());
		return motion;
	}

	/**
	 * 
	 * @param motionId
	 * @param time
	 */
	private Motion constructMotion(final LocalDateTime time) {
		Motion motion = new Motion();
		motion.setOpenedTime(time);
		return motion;
	}

}
