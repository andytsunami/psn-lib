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

/**
 * Friend status enum for <code>PsnFriendData</code>
 * 
 * 
 * @version 3.0
 * @since Nov 25 2012
 * @author Kyle Kroboth
 */
public enum FriendStatus {

	ONLINE {
		@Override
		public String getStringName() {
			return "Online";
		}

	},
	OFFLINE {
		@Override
		public String getStringName() {
			return "Offline";
		}

	},
	AWAY {
		@Override
		public String getStringName() {
			return "Away";
		}

	},
	PENDING_RESPONSE {
		@Override
		public String getStringName() {
			return "Pending Response";
		}

	};

	/**
	 * Gets status name. Not same as {@link Enum#toString()}.
	 * 
	 * @return status name
	 */
	public abstract String getStringName();

}
