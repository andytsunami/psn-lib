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

import com.krobothsoftware.psn.TrophyType;
import com.krobothsoftware.psn.model.PsnTrophy;

/**
 * 
 * @version 3.0.2
 * @since Nov 25 2012
 * @author Kyle Kroboth
 */
public abstract class BuilderTrophy<T extends PsnTrophy> {
	public String userId;
	public int index;
	public String gameId;
	public String dateEarned;
	public TrophyType type;

	public BuilderTrophy(final String userId) {
		this.userId = userId;
	}

	public BuilderTrophy<T> setIndex(final int index) {
		this.index = index;
		return this;
	}

	public BuilderTrophy<T> setGameId(final String gameId) {
		this.gameId = gameId;
		return this;
	}

	public BuilderTrophy<T> setDateEarned(final String dateEarned) {
		this.dateEarned = dateEarned;
		return this;
	}

	public BuilderTrophy<T> setTrophyType(final TrophyType trophieType) {
		this.type = trophieType;
		return this;
	}

	public abstract T build();

}
