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
import com.krobothsoftware.psn.TrophyType;
import com.krobothsoftware.psn.client.PlayStationNetworkClient;
import com.krobothsoftware.psn.internal.BuilderTrophy;
import com.krobothsoftware.psn.internal.ModelType;

/**
 * Holds trophy data for <i>Official</i> methods.
 * 
 * @see PlayStationNetworkClient#getOfficialTrophyList(String, String)
 * 
 * @version 3.0
 * @since Nov 25 2012
 * @author Kyle Kroboth
 */
public class PsnTrophyDataOfficial extends PsnModel implements PsnTrophy, Jid {
	private static final long serialVersionUID = -5311787059197244617L;
	private final String jid;
	private final PlatformType pf;
	private final int trophyId;
	private final String gameId;
	private final String dateEarned;
	private final TrophyType trophyType;
	private final boolean isReceived;

	private PsnTrophyDataOfficial(final Builder builder) {
		super(ModelType.OFFICIAL_VERSION);
		jid = builder.userId;
		trophyId = builder.trophyId;
		gameId = builder.gameId;
		dateEarned = builder.dateEarned;
		trophyType = builder.trophyType;
		isReceived = builder.isReceived;
		pf = builder.pf;
	}

	@Override
	public String getJid() {
		return jid;
	}

	public PlatformType getPlatform() {
		return pf;
	}

	@Override
	public int getTrophyId() {
		return trophyId;
	}

	@Override
	public String getGameId() {
		return gameId;
	}

	@Override
	public String getDateEarned() {
		return dateEarned;
	}

	@Override
	public TrophyType getTrophyType() {
		return trophyType;
	}

	@Override
	public boolean isReceived() {
		return isReceived;
	}

	@Override
	public String toString() {
		return String
				.format("ModelTrophyOfficialImpl [trophyId=%s, gameId=%s, dateEarned=%s, trophyType=%s]",
						trophyId, gameId, dateEarned, trophyType);
	}

	public static class Builder extends BuilderTrophy<PsnTrophyDataOfficial> {
		PlatformType pf;

		public Builder(final String userId) {
			super(userId);
		}

		public Builder setPlatform(final PlatformType pf) {
			this.pf = pf;
			return this;
		}

		@Override
		public PsnTrophyDataOfficial build() {
			return new PsnTrophyDataOfficial(this);
		}

	}

}
