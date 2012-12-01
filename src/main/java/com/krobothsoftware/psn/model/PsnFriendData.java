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

package com.krobothsoftware.psn.model;

import com.krobothsoftware.psn.client.PlayStationNetworkClient;
import com.krobothsoftware.psn.internal.ModelType;

/**
 * 
 * Holds friend data used by <i>UK</i> methods.
 * 
 * @see PlayStationNetworkClient#getClientFriendList()
 * 
 * @version 3.0
 * @since Nov 25 2012
 * @author Kyle Kroboth
 */
public class PsnFriendData extends PsnModel implements PsnId {
	private static final long serialVersionUID = -2725063559049803762L;
	private final String psnId;
	private final FriendStatus currentPresence;
	private final String currentGame;
	private final String avatar;
	private final String comment;
	private final boolean isPlayStationPlus;
	private final int level;
	private final int platinum;
	private final int gold;
	private final int silver;
	private final int bronze;

	private PsnFriendData(final Builder builder) {
		super(ModelType.UK_FRIEND_VERSION);
		psnId = builder.psnId;
		currentPresence = builder.currentPresence;
		currentGame = builder.currentGame;
		avatar = builder.avatar;
		comment = builder.comment;
		isPlayStationPlus = builder.isPlayStationPlus;
		level = builder.level;
		platinum = builder.platinum;
		gold = builder.gold;
		silver = builder.silver;
		bronze = builder.bronze;
	}

	@Override
	public String getPsnId() {
		return psnId;
	}

	public FriendStatus getCurrentStatus() {
		return currentPresence;
	}

	public String getCurrentGame() {
		return currentGame;
	}

	/**
	 * Gets UK avatar link
	 * 
	 * <pre>
	 * http://secure.eu.playstation.com (Avatar)
	 * </pre>
	 * 
	 * @return avatar link
	 */
	public String getAvatar() {
		return "http://secure.eu.playstation.com" + avatar;
	}

	public String getComment() {
		return comment;
	}

	public boolean isPlayStationPlus() {
		return isPlayStationPlus;
	}

	public int getLevel() {
		return level;
	}

	public int getPlatinum() {
		return platinum;
	}

	public int getGold() {
		return gold;

	}

	public int getSilver() {
		return silver;

	}

	public int getBronze() {
		return bronze;

	}

	@Override
	public String toString() {
		return "PsnFriendData [onlineId=" + psnId + ", currentPresence="
				+ currentPresence + ", currentGame=" + currentGame + "]";
	}

	public static class Builder {
		String psnId;
		FriendStatus currentPresence;
		String currentGame;
		String avatar;
		String comment;
		boolean isPlayStationPlus;
		int level;
		int platinum;
		int gold;
		int silver;
		int bronze;

		public Builder(final String psnId) {
			this.psnId = psnId;
		}

		public Builder setPsnId(final String psnId) {
			this.psnId = psnId;
			return this;
		}

		public Builder setCurrentPresence(final FriendStatus currentPresence) {
			this.currentPresence = currentPresence;
			return this;
		}

		public Builder setCurrentGame(final String currentGame) {
			this.currentGame = currentGame;
			return this;
		}

		public Builder setCurrentAvatar(final String currentAvatar) {
			avatar = currentAvatar;
			return this;
		}

		public Builder setComment(final String comment) {
			this.comment = comment;
			return this;
		}

		public Builder setPlaystationPlus(final boolean isPlaystationPlus) {
			isPlayStationPlus = isPlaystationPlus;
			return this;
		}

		public Builder setLevel(final int level) {
			this.level = level;
			return this;
		}

		public Builder setPlatinum(final int platinum) {
			this.platinum = platinum;
			return this;
		}

		public Builder setGold(final int gold) {
			this.gold = gold;
			return this;
		}

		public Builder setSilver(final int silver) {
			this.silver = silver;
			return this;
		}

		public Builder setBronze(final int bronze) {
			this.bronze = bronze;
			return this;
		}

		public PsnFriendData build() {
			return new PsnFriendData(this);
		}

	}

}
