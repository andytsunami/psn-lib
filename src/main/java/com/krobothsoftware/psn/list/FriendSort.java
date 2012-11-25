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

import static com.krobothsoftware.psn.model.FriendStatus.AWAY;
import static com.krobothsoftware.psn.model.FriendStatus.OFFLINE;
import static com.krobothsoftware.psn.model.FriendStatus.ONLINE;
import static com.krobothsoftware.psn.model.FriendStatus.PENDING_RESPONSE;

import java.util.Comparator;

import com.krobothsoftware.psn.model.PsnFriendData;

/**
 * Sort list with <code>PsnFriendData</code> type
 * 
 * @version 3.0
 * @since Nov 25 2012
 * @author Kyle Kroboth
 */
public enum FriendSort implements Comparator<PsnFriendData> {

	ID_SORT {

		@Override
		public int compare(final PsnFriendData one, final PsnFriendData two) {
			return one.getPsnId().compareTo(two.getPsnId());
		}

	},
	ONLINE_SORT {

		@Override
		public int compare(final PsnFriendData one, final PsnFriendData two) {
			return Boolean.valueOf(one.getCurrentStatus().equals(ONLINE))
					.compareTo(two.getCurrentStatus().equals(ONLINE));
		}

	},
	OFFLINE_SORT {

		@Override
		public int compare(final PsnFriendData one, final PsnFriendData two) {
			return Boolean.valueOf(one.getCurrentStatus().equals(OFFLINE))
					.compareTo(two.getCurrentStatus().equals(OFFLINE));
		}

	},
	AWAY_SORT {

		@Override
		public int compare(final PsnFriendData one, final PsnFriendData two) {
			return Boolean.valueOf(one.getCurrentStatus().equals(AWAY))
					.compareTo(two.getCurrentStatus().equals(AWAY));
		}

	},
	PENDING_RESPONSE_SORT {

		@Override
		public int compare(final PsnFriendData one, final PsnFriendData two) {
			return Boolean.valueOf(
					one.getCurrentStatus().equals(PENDING_RESPONSE)).compareTo(
					two.getCurrentStatus().equals(PENDING_RESPONSE));
		}

	},
	CURRENT_GAME_SORT {

		@Override
		public int compare(final PsnFriendData one, final PsnFriendData two) {
			return one.getCurrentGame().compareTo(two.getCurrentGame());
		}

	};

	public static Comparator<PsnFriendData> decending(
			final Comparator<PsnFriendData> other) {
		return new Comparator<PsnFriendData>() {
			@Override
			public int compare(final PsnFriendData o1, final PsnFriendData o2) {
				return -1 * other.compare(o1, o2);
			}
		};
	}

	public static Comparator<PsnFriendData> getComparator(
			final FriendSort... multipleOptions) {
		return new Comparator<PsnFriendData>() {
			@Override
			public int compare(final PsnFriendData o1, final PsnFriendData o2) {
				for (final FriendSort option : multipleOptions) {
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
