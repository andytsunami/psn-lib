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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.krobothsoftware.psn.client.PlayStationNetworkClient;
import com.krobothsoftware.psn.internal.ModelType;

/**
 * Holds profile data used by <i>Official</i> methods.
 * 
 * @see PlayStationNetworkClient#getOfficialProfile(String)
 * 
 * @version 3.0
 * @since Nov 25 2012
 * @author Kyle Kroboth
 */
public class PsnProfileData extends PsnModel implements Jid {
	private static final long serialVersionUID = 8271705791064135500L;
	private final String jid;
	private final String avatar;
	private final String aboutMe;
	private final Locale countryCulture;
	private final boolean isPlus;
	private final int backgroundColor;
	private final String panel;
	private final int panelBackgroundColor;

	private final int points;
	private final int level;
	private final int levelFloor;
	private final int levelCeiling;
	private final int progress;
	private final int trophyCount;
	private final int platinum;
	private final int gold;
	private final int silver;
	private final int bronze;

	public transient final static Map<String, Locale> CULTURE_MAP;

	static {
		CULTURE_MAP = new HashMap<String, Locale>();

		for (final Locale locale : Locale.getAvailableLocales()) {
			if (locale.getCountry().length() > 1) CULTURE_MAP.put(
					locale.getCountry(), locale);
		}
	}

	private PsnProfileData(final Builder builder) {
		super(ModelType.OFFICIAL_VERSION);
		jid = builder.jid;
		avatar = builder.avatar;
		aboutMe = builder.aboutMe;
		countryCulture = builder.countryCulture;
		isPlus = builder.isPlus;
		backgroundColor = builder.backgroundColor;
		points = builder.points;
		level = builder.level;
		levelFloor = builder.levelFloor;
		levelCeiling = builder.levelCeiling;
		progress = builder.progress;
		trophyCount = builder.trophyCount;
		platinum = builder.platinum;
		gold = builder.gold;
		silver = builder.silver;
		bronze = builder.bronze;
		panel = builder.panel;
		panelBackgroundColor = builder.panelBackgroundColor;
	}

	@Override
	public String getJid() {
		return jid;
	}

	public String getAvatar() {
		return avatar;
	}

	public String getAboutMe() {
		return aboutMe;
	}

	public Locale getCountryCulture() {
		return countryCulture;
	}

	public boolean hasPlayStationPlus() {
		return isPlus;
	}

	public int getBackgroundColor() {
		return backgroundColor;
	}

	public int getPoints() {
		return points;
	}

	public int getLevel() {
		return level;
	}

	public int getLevelFloor() {
		return levelFloor;
	}

	public int getLevelCeiling() {
		return levelCeiling;
	}

	public int getProgress() {
		return progress;
	}

	public int getTrophyCount() {
		return trophyCount;
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

	public String getPanel() {
		return panel;
	}

	public int getPanelBackgroundColor() {
		return panelBackgroundColor;
	}

	@Override
	public String toString() {
		return String
				.format("PsnProfileData [aboutMe=%s, countryCulture=%s, level=%s, progress=%s, psnId=%s]",
						aboutMe, countryCulture, level, progress, jid);
	}

	public static class Builder {
		String jid;
		String avatar;
		String aboutMe;
		Locale countryCulture;
		boolean isPlus;
		int backgroundColor;
		String panel;
		int panelBackgroundColor;

		int points;
		int level;
		int levelFloor;
		int levelCeiling;
		int progress;
		int trophyCount;
		int platinum;
		int gold;
		int silver;
		int bronze;

		public Builder(final String jid) {
			this.jid = jid;
		}

		public Builder setAvatar(final String avatar) {
			this.avatar = avatar;
			return this;
		}

		public Builder setAboutMe(final String aboutMe) {
			this.aboutMe = aboutMe;
			return this;
		}

		public Builder setCountryCulture(final Locale countryCulture) {
			this.countryCulture = countryCulture;
			return this;
		}

		public Builder setPlus(final boolean isPlus) {
			this.isPlus = isPlus;
			return this;
		}

		public Builder setBackgroundColor(final int backgroundColor) {
			this.backgroundColor = backgroundColor;
			return this;
		}

		public Builder setPoints(final int points) {
			this.points = points;
			return this;
		}

		public Builder setLevel(final int level) {
			this.level = level;
			return this;
		}

		public Builder setLevelFloor(final int levelFloor) {
			this.levelFloor = levelFloor;
			return this;
		}

		public Builder setLevelCeiling(final int levelCeiling) {
			this.levelCeiling = levelCeiling;
			return this;
		}

		public Builder setProgress(final int progress) {
			this.progress = progress;
			return this;
		}

		public Builder setTrophyCount(final int trophyCount) {
			this.trophyCount = trophyCount;
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

		public Builder setPanel(final String panel) {
			this.panel = panel;
			return this;
		}

		public Builder setPanelBackgroundColor(final int panelBackgroundColor) {
			this.panelBackgroundColor = panelBackgroundColor;
			return this;
		}

		public PsnProfileData build() {
			return new PsnProfileData(this);
		}

	}

}
