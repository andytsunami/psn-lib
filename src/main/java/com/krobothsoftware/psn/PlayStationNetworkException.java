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
 * Signals that an playstation network exception has occurred.
 * 
 * @version 3.0
 * @since Nov 25 2012
 * @author Kyle Kroboth
 */
public class PlayStationNetworkException extends Exception {
	private static final long serialVersionUID = -6794987639270369775L;

	public PlayStationNetworkException(final String error) {
		super(error);
	}

	public PlayStationNetworkException(final Throwable throwable) {
		super(throwable);
	}

	public PlayStationNetworkException(final String message,
			final Throwable throwable) {
		super(message, throwable);
	}

}
