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

import com.krobothsoftware.psn.PlatformType;
import com.krobothsoftware.psn.client.PlayStationNetworkClient;
import com.krobothsoftware.psn.internal.BuilderGame;
import com.krobothsoftware.psn.internal.ModelType;

/**
 * Holds game data for <i>Official</i> methods.
 * 
 * @see PlayStationNetworkClient#getOfficialGameList(String, int, int,
 *      PlatformType...)
 * 
 * @version 3.0
 * @since Nov 25 2012
 * @author Kyle Kroboth
 */
public class PsnGameDataOfficial extends PsnModel implements PsnGame, Jid {
	private static final long serialVersionUID = -2913100475832848476L;
	private final String jid;
	private final String npCommid;
	private final PlatformType pf;
	private final int platinum;
	private final int gold;
	private final int silver;
	private final int bronze;
	private final String lastUpdated;

	private PsnGameDataOfficial(final Builder builder) {
		super(ModelType.OFFICIAL_VERSION);
		jid = builder.userId;
		npCommid = builder.gameId;
		pf = builder.pf;
		platinum = builder.platinum;
		gold = builder.gold;
		silver = builder.silver;
		bronze = builder.bronze;
		lastUpdated = builder.lastUpdated;
	}

	@Override
	public String getJid() {
		return jid;
	}

	@Override
	public String getGameId() {
		return npCommid;
	}

	public PlatformType getPlatform() {
		return pf;
	}

	@Override
	public String getTitleLinkId() {
		return null;
	}

	@Override
	public int getPlatinum() {
		return platinum;
	}

	@Override
	public int getGold() {
		return gold;
	}

	@Override
	public int getSilver() {
		return silver;
	}

	@Override
	public int getBronze() {
		return bronze;
	}

	public String getLastUpdated() {
		return lastUpdated;
	}

	@Override
	public String toString() {
		return String
				.format("PsnGameDataOfficial [npCommid=%s, platinum=%s, gold=%s, silver=%s, bronze=%s, lastUpdated=%s]",
						npCommid, platinum, gold, silver, bronze, lastUpdated);
	}

	public static class Builder extends BuilderGame<PsnGameDataOfficial> {
		String lastUpdated;
		PlatformType pf;

		public Builder(final String userId) {
			super(userId);

		}

		public Builder setPlatform(final PlatformType platform) {
			pf = platform;
			return this;
		}

		public Builder setLastUpdated(final String lastUpdated) {
			this.lastUpdated = lastUpdated;
			return this;
		}

		@Override
		public PsnGameDataOfficial build() {
			return new PsnGameDataOfficial(this);
		}

	}

}
