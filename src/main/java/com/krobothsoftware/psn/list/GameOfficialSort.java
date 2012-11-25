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

package com.krobothsoftware.psn.list;

import java.util.Comparator;

import com.krobothsoftware.psn.model.PsnGameData;
import com.krobothsoftware.psn.model.PsnGameDataOfficial;

/**
 * Sort list with <code>PsnGameDataOfficial</code> type
 * 
 * @version 3.0
 * @since Nov 25 2012
 * @author Kyle Kroboth
 */
public enum GameOfficialSort implements Comparator<PsnGameDataOfficial> {

	TROPHY_PLATINUM_SORT {
		@Override
		public int compare(final PsnGameDataOfficial one,
				final PsnGameDataOfficial two) {
			return Integer.valueOf(one.getPlatinum()).compareTo(
					two.getPlatinum());
		}
	},

	TROPHY_GOLD_SORT {
		@Override
		public int compare(final PsnGameDataOfficial one,
				final PsnGameDataOfficial two) {
			return Integer.valueOf(one.getGold()).compareTo(two.getGold());
		}
	},

	TROPHY_SILVER_SORT {
		@Override
		public int compare(final PsnGameDataOfficial one,
				final PsnGameDataOfficial two) {
			return Integer.valueOf(one.getSilver()).compareTo(two.getSilver());
		}
	},

	TROPHY_BRONZE_SORT {
		@Override
		public int compare(final PsnGameDataOfficial one,
				final PsnGameDataOfficial two) {
			return Integer.valueOf(one.getBronze()).compareTo(two.getBronze());
		}
	};

	public static Comparator<PsnGameData> decending(
			final Comparator<PsnGameData> other) {
		return new Comparator<PsnGameData>() {
			@Override
			public int compare(final PsnGameData o1, final PsnGameData o2) {
				return -1 * other.compare(o1, o2);
			}
		};
	}

}