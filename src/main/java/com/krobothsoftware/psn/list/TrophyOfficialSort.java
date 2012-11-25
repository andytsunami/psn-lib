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

import com.krobothsoftware.psn.TrophyType;
import com.krobothsoftware.psn.model.PsnTrophyDataOfficial;

/**
 * Sort list with <code>PsnTrophyDataOfficial</code> type
 * 
 * @version 3.0
 * @since Nov 25 2012
 * @author Kyle Kroboth
 */
public enum TrophyOfficialSort implements Comparator<PsnTrophyDataOfficial> {

	TROPHY_TYPE {
		@Override
		public int compare(final PsnTrophyDataOfficial one,
				final PsnTrophyDataOfficial two) {
			return one.getTrophyType().compareTo(two.getTrophyType());
		}
	},

	PLATINUM {
		@Override
		public int compare(final PsnTrophyDataOfficial one,
				final PsnTrophyDataOfficial two) {
			return one.getTrophyType().compareTo(TrophyType.PLATINUM);
		}
	},

	GOLD {
		@Override
		public int compare(final PsnTrophyDataOfficial one,
				final PsnTrophyDataOfficial two) {
			return one.getTrophyType().compareTo(TrophyType.GOLD);
		}
	},

	SILVER {
		@Override
		public int compare(final PsnTrophyDataOfficial one,
				final PsnTrophyDataOfficial two) {
			return one.getTrophyType().compareTo(TrophyType.SILVER);
		}
	},

	BRONZE {
		@Override
		public int compare(final PsnTrophyDataOfficial one,
				final PsnTrophyDataOfficial two) {
			return one.getTrophyType().compareTo(TrophyType.BRONZE);
		}
	};

	public static Comparator<PsnTrophyDataOfficial> decending(
			final Comparator<PsnTrophyDataOfficial> other) {
		return new Comparator<PsnTrophyDataOfficial>() {
			@Override
			public int compare(final PsnTrophyDataOfficial o1,
					final PsnTrophyDataOfficial o2) {
				return -1 * other.compare(o1, o2);
			}
		};
	}

	public static Comparator<PsnTrophyDataOfficial> getComparator(
			final TrophyOfficialSort... multipleOptions) {
		return new Comparator<PsnTrophyDataOfficial>() {
			@Override
			public int compare(final PsnTrophyDataOfficial o1,
					final PsnTrophyDataOfficial o2) {
				for (final TrophyOfficialSort option : multipleOptions) {
					final int result = option.compare(o1, o2);
					if (result != 0) {
						return result;
					}
				}
				return 0;
			}
		};
	}

}
