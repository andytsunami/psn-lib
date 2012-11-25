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
import com.krobothsoftware.psn.internal.BuilderGame;
import com.krobothsoftware.psn.internal.ModelType;

/**
 * Holds game data used by <i>UK</i> and <i>US</i> methods.
 * 
 * @see PlayStationNetworkClient#getClientGameList()
 * @see PlayStationNetworkClient#getPublicGameList(String)
 * 
 * @version 3.0
 * @since Nov 25 2012
 * @author Kyle Kroboth
 */
public class PsnGameData extends PsnModel implements PsnGame, PsnId {
	private static final long serialVersionUID = 7368966773723281525L;
	private final String psnId;
	private final String gameId;
	private final String name;
	private final String gameImage;
	private final int progress;
	private final int trophies;
	private final int platinum;
	private final int gold;
	private final int silver;
	private final int bronze;
	private final String trophyLinkId;

	private PsnGameData(final Builder builder) {
		super(builder.version);
		psnId = builder.userId;
		gameId = builder.gameId;
		name = builder.name;
		gameImage = builder.gameImage;
		progress = builder.progress;
		trophies = builder.trophies;
		platinum = builder.platinum;
		gold = builder.gold;
		silver = builder.silver;
		bronze = builder.bronze;
		trophyLinkId = builder.trophyLinkId;

	}

	@Override
	public String getPsnId() {
		return psnId;
	}

	@Override
	public String getGameId() {
		return gameId;
	}

	public String getName() {
		return name;
	}

	public String getGameImage() {
		return gameImage;
	}

	public int getProgress() {
		return progress;
	}

	@Override
	public int getTrophyCount() {
		return trophies;
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

	@Override
	public String getTrophyLinkId() {
		return trophyLinkId;
	}

	@Override
	public String toString() {
		return String.format("PsnGameData [gameId=%s, name=%s, progress=%s]",
				gameId, name, progress);
	}

	public static class Builder extends BuilderGame<PsnGameData> {
		ModelType version;
		String name;
		String gameImage;
		int progress;

		public Builder(final ModelType version, final String userId) {
			super(userId);
			this.version = version;

		}

		public Builder setName(final String name) {
			this.name = name;
			return this;
		}

		public Builder setGameImage(final String gameImage) {
			this.gameImage = gameImage;
			return this;
		}

		public Builder setProgress(final int progress) {
			this.progress = progress;
			return this;
		}

		@Override
		public PsnGameData build() {
			return new PsnGameData(this);
		}

	}

}
