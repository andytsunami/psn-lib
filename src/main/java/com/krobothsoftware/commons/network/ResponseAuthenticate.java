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

package com.krobothsoftware.commons.network;

import java.net.HttpURLConnection;

/**
 * Response holder from {@link RequestBuilder#execute(NetworkHelper)} if
 * response code is 401(Unauthorized). Make sure to call {@link #disconnect()}
 * to ensure connection is closed.
 * 
 * @version 3.0.2
 * @since Dec 24 2012
 * @author Kyle Kroboth
 */
public class ResponseAuthenticate extends Response {
	private final String authenticate;

	public ResponseAuthenticate(HttpURLConnection conn,
			UnclosableInputStream in, int respCode, String respChar) {
		super(conn, in, respCode, respChar);
		authenticate = conn.getHeaderField("WWW-Authenticate");
	}

	/**
	 * Gets header <code>WWW-Authenticate</code>
	 * 
	 * @return authenticate header
	 */
	public String getAuthentication() {
		return authenticate;
	}

}
