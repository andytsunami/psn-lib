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

package com.krobothsoftware.psn.internal;

import com.krobothsoftware.psn.model.PsnGame;

/**
 * 
 * @version 3.0
 * @since Nov 25 2012
 * @author Kyle Kroboth
 */
public abstract class BuilderGame<T extends PsnGame> {
	public String userId;
	public String gameId;
	public int trophies;
	public int platinum;
	public int gold;
	public int silver;
	public int bronze;
	public String trophyLinkId;

	public BuilderGame(final String userId) {
		this.userId = userId;
	}

	public BuilderGame<T> setGameId(final String gameId) {
		this.gameId = gameId;
		return this;
	}

	public BuilderGame<T> setTrophies(final int trophies) {
		this.trophies = trophies;
		return this;
	}

	public BuilderGame<T> setPlatinum(final int platinum) {
		this.platinum = platinum;
		return this;
	}

	public BuilderGame<T> setGold(final int gold) {
		this.gold = gold;
		return this;
	}

	public BuilderGame<T> setSilver(final int silver) {
		this.silver = silver;
		return this;
	}

	public BuilderGame<T> setBronze(final int bronze) {
		this.bronze = bronze;
		return this;
	}

	public BuilderGame<T> setTrophyLinkId(final String trophyLinkId) {
		this.trophyLinkId = trophyLinkId;
		return this;
	}

	public abstract T build();

}
