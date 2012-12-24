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
 * @version 3.0.2
 * @since Nov 25 2012
 * @author Kyle Kroboth
 */
public class PsnFriendData extends PsnModel implements PsnId {
	private static final long serialVersionUID = -2725063559049803762L;
	private final String psnId;
	private final FriendStatus presence;
	private final String game;
	private final String avatar;
	private final String comment;
	private final boolean isPP;
	private final int level;
	private final int platinum;
	private final int gold;
	private final int silver;
	private final int bronze;

	private PsnFriendData(final Builder builder) {
		super(ModelType.UK_FRIEND_VERSION);
		psnId = builder.psnId;
		presence = builder.presence;
		game = builder.game;
		avatar = builder.avatar;
		comment = builder.comment;
		isPP = builder.isPP;
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
		return presence;
	}

	public String getCurrentGame() {
		return game;
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
		return isPP;
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
		return "PsnFriendData [onlineId=" + psnId + ", presence=" + presence
				+ ", game=" + game + "]";
	}

	public static class Builder {
		String psnId;
		FriendStatus presence;
		String game;
		String avatar;
		String comment;
		boolean isPP;
		int level;
		int platinum;
		int gold;
		int silver;
		int bronze;

		public Builder setPsnId(final String psnId) {
			this.psnId = psnId;
			return this;
		}

		public Builder setPresence(final FriendStatus presence) {
			this.presence = presence;
			return this;
		}

		public Builder setGame(final String game) {
			this.game = game;
			return this;
		}

		public Builder setAvatar(final String avatar) {
			this.avatar = avatar;
			return this;
		}

		public Builder setComment(final String comment) {
			this.comment = comment;
			return this;
		}

		public Builder setPP(final boolean isPP) {
			this.isPP = isPP;
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
