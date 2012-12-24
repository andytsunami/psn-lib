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
 * response code is 302(Temporary Redirect). Make sure to call
 * {@link #disconnect()} to ensure connection is closed.
 * 
 * @version 3.0.2
 * @since Dec 24 2012
 * @author Kyle Kroboth
 */
public class ResponseRedirect extends Response {
	private final String redirectUrl;

	public ResponseRedirect(HttpURLConnection conn, UnclosableInputStream in,
			int respCode, String respChar) {
		super(conn, in, respCode, respChar);
		redirectUrl = conn.getHeaderField("Location");
	}

	/**
	 * Gets header <code>Location</code>
	 * 
	 * @return location header
	 */
	public String getRedirectUrl() {
		return redirectUrl;
	}

}
