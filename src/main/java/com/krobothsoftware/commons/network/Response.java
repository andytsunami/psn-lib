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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

/**
 * Response holder from {@link RequestBuilder#execute(NetworkHelper)}. Make sure
 * to call {@link #disconnect()} to ensure connection is closed.
 * 
 * 
 * @version 3.0
 * @since Nov 25 2012
 * @author Kyle Kroboth
 */
public final class Response {
	private final HttpURLConnection urlConnection;
	private final InputStream responseStream;
	private final int responseCode;
	private final String responseCharset;

	public Response(final HttpURLConnection conn, final InputStream in,
			final int respCode, final String respChar) {
		urlConnection = conn;
		responseStream = in;
		responseCode = respCode;
		responseCharset = respChar;
	}

	public HttpURLConnection getConnection() {
		return urlConnection;
	}

	public InputStream getStream() {
		return responseStream;
	}

	public int getStatusCode() {
		return responseCode;
	}

	public String getCharset() {
		return responseCharset;
	}

	public void disconnect() throws IOException {
		urlConnection.disconnect();
		if (responseStream != null) responseStream.close();
	}
}
