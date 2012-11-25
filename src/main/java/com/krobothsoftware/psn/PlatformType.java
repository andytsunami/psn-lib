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

package com.krobothsoftware.psn;

/**
 * Current Platforms from Sony
 * 
 * @version 3.0
 * @since Nov 25 2012
 * @author Kyle Kroboth
 */
public enum PlatformType {

	PS3 {
		@Override
		public String getTypeString() {
			return "ps3";
		}
	},
	VITA {
		@Override
		public String getTypeString() {
			return "psp2";
		}
	},
	PSP {

		@Override
		public String getTypeString() {
			return "psp";
		}

	};

	/**
	 * Gets the type string used by <i>Official</i> methods
	 * 
	 * @return type string
	 */
	public abstract String getTypeString();

	/**
	 * Gets the platform from type string
	 * 
	 * @param typeString
	 *            official platform type string
	 * @return platform
	 */
	public static PlatformType getPlatform(final String typeString) {
		if (typeString.equalsIgnoreCase("ps3")) return PS3;
		else if (typeString.equalsIgnoreCase("psp2")) return VITA;
		else if (typeString.equalsIgnoreCase("psp")) return PSP;

		return null;
	}

}
