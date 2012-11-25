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
 * Trophy Types used with PSN
 * 
 * @version 3.0
 * @since Nov 25 2012
 * @author Kyle Kroboth
 */
public enum TrophyType {

	PLATINUM {
		@Override
		public String getTrophyName() {
			return STRING_PLATINUM;
		}
	},
	GOLD {
		@Override
		public String getTrophyName() {
			return STRING_GOLD;
		}
	},
	SILVER {
		@Override
		public String getTrophyName() {
			return STRING_SILVER;
		}
	},
	BRONZE {
		@Override
		public String getTrophyName() {
			return STRING_BRONZE;
		}
	},
	HIDDEN {
		@Override
		public String getTrophyName() {
			return STRING_HIDDEN;
		}
	};

	private static final String STRING_PLATINUM = "Platinum";
	private static final String STRING_GOLD = "Gold";
	private static final String STRING_SILVER = "Silver";
	private static final String STRING_BRONZE = "Bronze";
	private static final String STRING_HIDDEN = "Hidden";

	/**
	 * Gets the trophy name. Not same as {@link Enum#toString()}.
	 * 
	 * @return trophy name
	 */
	public abstract String getTrophyName();

}
