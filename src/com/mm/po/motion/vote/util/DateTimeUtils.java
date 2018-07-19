package com.mm.po.motion.vote.util;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 
 * @author Arun Devadoss
 *
 */
public class DateTimeUtils {

	/**
	 * Returns true, CurrentTime is after/equal OpenedTime. Otherwise false.
	 * 
	 * @param currentTime
	 * @param openedTime
	 * @return
	 */
	public static boolean checkTime(final LocalDateTime currentTime, final LocalDateTime openedTime) {
		return currentTime.isAfter(openedTime) || currentTime.isEqual(openedTime);
	}

	/**
	 * Returns Duration between two Times
	 * 
	 * @param time
	 * @param openedTime
	 * @return
	 */

	public static Duration durationBetween(final LocalDateTime time, final LocalDateTime openedTime) {
		return Duration.between(time, openedTime);
	}
}
