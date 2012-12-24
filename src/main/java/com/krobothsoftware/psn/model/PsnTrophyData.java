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

import com.krobothsoftware.psn.TrophyType;
import com.krobothsoftware.psn.client.PlayStationNetworkClient;
import com.krobothsoftware.psn.internal.BuilderTrophy;
import com.krobothsoftware.psn.internal.ModelType;

/**
 * Holds trophy data used by <i>US</i> and <i>UK</i> methods.
 * 
 * @see PlayStationNetworkClient#getClientTrophyList(String)
 * @see PlayStationNetworkClient#getPublicTrophyList(String, String, boolean)
 * 
 * @version 3.0.2
 * @since Nov 25 2012
 * @author Kyle Kroboth
 */
public class PsnTrophyData extends PsnModel implements PsnTrophy, PsnId {
	private static final long serialVersionUID = -2664313320588242916L;
	private final String psnId;
	private final int index;
	private final String gameId;
	private final String name;
	private final String image;
	private final String description;
	private final String dateEarned;
	private final TrophyType type;
	private final boolean isHidden;
	private final boolean isReceived;

	private PsnTrophyData(final Builder builder) {
		super(builder.version);
		psnId = builder.userId;
		index = builder.index;
		gameId = builder.gameId;
		name = builder.name;
		image = builder.image;
		description = builder.description;
		dateEarned = builder.dateEarned;
		type = builder.type;
		isHidden = builder.isHidden;
		isReceived = (dateEarned != null) ? true : false;
	}

	@Override
	public String getPsnId() {
		return psnId;
	}

	public String getName() {
		return name;
	}

	public String getImage() {
		return image;
	}

	public String getDescription() {
		return description;
	}

	public boolean isHidden() {
		return isHidden;
	}

	@Override
	public String getGameId() {
		return gameId;
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public String getDateEarned() {
		return dateEarned;
	}

	@Override
	public TrophyType getType() {
		return type;
	}

	@Override
	public boolean isReceived() {
		return isReceived;
	}

	@Override
	public String toString() {
		return String
				.format("ModelTrophyImpl [trophyId=%s, name=%s, dateEarned=%s, trophyType=%s, isReceived=%s]",
						index, name, dateEarned, type, isReceived);
	}

	public static class Builder extends BuilderTrophy<PsnTrophyData> {
		ModelType version;
		String name;
		String image;
		String description;
		boolean isHidden;

		public Builder(final ModelType version, final String userId) {
			super(userId);
			this.version = version;
		}

		public Builder setName(final String name) {
			this.name = name;
			return this;
		}

		public Builder setImage(final String image) {
			this.image = image;
			return this;
		}

		public Builder setDescription(final String description) {
			this.description = description;
			return this;
		}

		public Builder setHidden(final boolean isHidden) {
			this.isHidden = isHidden;
			return this;
		}

		@Override
		public PsnTrophyData build() {
			return new PsnTrophyData(this);
		}

	}

}
