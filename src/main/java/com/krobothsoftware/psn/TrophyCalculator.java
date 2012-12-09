/* ===================================================
 * Copyright 2012 Kroboth Software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ========================================================== 
 */

package com.krobothsoftware.psn;

/**
 * Used for calculating psn trophy progress, points, and levels.
 * 
 * Special Thanks for morphingryno's guide <a href=
 * "http://www.ps3trophies.org/forum/games/18347-trophy-points-guide.html"
 * >Trophy Guide</a>
 * 
 * 
 * @version 3.0
 * @since Nov 25 2012
 * @author Kyle Kroboth
 */
public final class TrophyCalculator {
	public static final int PLATINUM_POINTS = 180;
	public static final int GOLD_POINTS = 90;
	public static final int SILVER_POINTS = 30;
	public static final int BRONZE_POINTS = 15;

	public static final double LVL_2 = 200D;
	public static final double LVL_3 = 600D;
	public static final double LVL_4 = 1200D;
	public static final double LVL_5 = 2400D;
	public static final double LVL_6 = 4000D;
	public static final double LVL_12 = 16000D;
	public static final double LVL_13 = 24000D;
	public static final double LVL_27 = 138000D;

	public static final double INCR_12 = 2000D;
	public static final double INCR_27 = 8000D;
	public static final double INCR_43 = 10000D;

	private static final double PROGRESS = 100D;

	private TrophyCalculator() {

	}

	/**
	 * Gets the level from giving <code>points</code>. Because this method
	 * returns an integer, the level is ceiled. i.e.,
	 * 
	 * <pre>
	 * 13.589 = 14 level
	 * </pre>
	 * 
	 * @param points
	 * @return level
	 */
	public static int getLevel(final int points) {
		if (points < LVL_2) return 1;
		if (points < LVL_3) return 2;
		if (points < LVL_4) return 3;
		if (points < LVL_5) return 4;
		if (points < LVL_6) return 5;

		// level 12
		if (points < LVL_12) return (int) (Math
				.ceil(correctLevel((points - LVL_6) / INCR_12)) + 5D);

		// level 27
		if (points < LVL_27) return (int) (Math
				.ceil(correctLevel((points - LVL_13) / INCR_27)) + 12D);

		// level 43+
		return (int) (Math.ceil(correctLevel((points - LVL_27) / INCR_43)) + 26D);

	}

	/**
	 * Gets the progress from giving <code>points</code>. This gets the progress
	 * for current level of points.
	 * 
	 * @param points
	 * @return progress in percentage
	 */
	public static double getProgress(final int points) {
		final double dpoints = points;
		if (points < LVL_2) return dpoints / LVL_2 * PROGRESS;
		if (points < LVL_3) return (dpoints - LVL_2) / (LVL_3 - LVL_2)
				* PROGRESS;
		if (points < LVL_4) return (dpoints - LVL_3) / (LVL_4 - LVL_3)
				* PROGRESS;
		if (points < LVL_5) return (dpoints - LVL_4) / (LVL_5 - LVL_4)
				* PROGRESS;
		if (points < LVL_6) return (dpoints - LVL_5) / (LVL_6 - LVL_5)
				* PROGRESS;
		if (points < LVL_12) return (dpoints - LVL_6) / INCR_12 % 1D * PROGRESS;
		if (points < LVL_27) return (dpoints - LVL_13) / INCR_27 % 1D
				* PROGRESS;
		return (dpoints - LVL_27) / INCR_43 % 1D * PROGRESS;

	}

	/**
	 * Gets the game progress rom <code>achievedPoints</code> and
	 * <code>totalPoints</code>. Normally it's
	 * 
	 * <pre>
	 * A / T * 100
	 * </pre>
	 * 
	 * to get the progress but 180(Platinum) must be subtracted from T first
	 * 
	 * <pre>
	 * A / (T - 180) * 100
	 * correct expression
	 * </pre>
	 * 
	 * @param achievedPoints
	 *            the achieved points
	 * @param totalPoints
	 *            the total points
	 * @return the game progress
	 */
	public static double getGameProgress(final int achievedPoints,
			final int totalPoints) {
		return achievedPoints / ((double) totalPoints - PLATINUM_POINTS)
				* PROGRESS;
	}

	/**
	 * Calculates the trophy points for given trophy types
	 * <p>
	 * Trophy Points
	 * </p>
	 * 
	 * <pre>
	 * Platinum: 180</br> Gold: 90</br> Silver: 30</br> Bronze: 15
	 * </pre>
	 * 
	 * @param bronze
	 * @param silver
	 * @param gold
	 * @param platinum
	 * @return points
	 */
	public static int getTrophyPoints(final int bronze, final int silver,
			final int gold, final int platinum) {
		return platinum * PLATINUM_POINTS + gold * GOLD_POINTS + silver
				* SILVER_POINTS + bronze * BRONZE_POINTS;
	}

	/**
	 * Gets base points for given level
	 * 
	 * @param level
	 * @return points from level
	 */
	public static int getLevelPoints(final int level) {
		switch (level) {
		case 0:
		case 1:
			return 0;
		case 2:
			return (int) LVL_2;
		case 3:
			return (int) LVL_3;
		case 4:
			return (int) LVL_4;
		case 5:
			return (int) LVL_5;
		case 6:
			return (int) LVL_6;
		default:
			if (level < 12) return (int) ((level - 6) * INCR_12 + LVL_6);
			if (level < 27) return (int) ((level - 12) * INCR_27 + LVL_12);
			return (int) ((level - 27) * INCR_43 + LVL_27);

		}
	}

	/**
	 * Gets the points from level and progress
	 * 
	 * <pre>
	 * *LVL* are points for level
	 * (NEXT_LVL - LVL) * 100 + LVL
	 * </pre>
	 * 
	 * @param level
	 * @param progress
	 * @return points
	 */
	public static int getProgressPoints(final int level, final int progress) {
		final double p = progress / 100D;
		switch (level) {
		case 0:
		case 1:
			return (int) (p * LVL_2);
		case 2:
			return (int) (p * (LVL_3 - LVL_2) + LVL_2);
		case 3:
			return (int) (p * (LVL_4 - LVL_3) + LVL_3);
		case 4:
			return (int) (p * (LVL_5 - LVL_4) + LVL_4);
		case 5:
			return (int) (p * (LVL_6 - LVL_5) + LVL_5);
		default:
			if (level < 12) return (int) ((level - 6) * INCR_12 + LVL_6 + p
					* INCR_12);
			if (level < 27) return (int) ((level - 12) * INCR_27 + LVL_12 + p
					* INCR_27);
			return (int) ((level - 27) * INCR_43 + LVL_27 + p * INCR_43);
		}

	}

	/**
	 * Since a new level starts at 0.0, it needs to be incremented. If not it
	 * stays the same
	 */
	private static double correctLevel(double level) {
		return level % 1 > 0.0D ? level : ++level;
	}
}
