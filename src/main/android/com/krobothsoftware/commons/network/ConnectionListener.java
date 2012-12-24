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
import java.net.URL;

/**
 * Listener for connections
 * 
 * @version 3.0.1
 * @since Dec 1 2012
 * @author Kyle Kroboth
 * 
 */
public interface ConnectionListener {

	/**
	 * Called right when connection is being set up.
	 * 
	 * @param builder
	 */
	void onRequest(final RequestBuilder builder);

	/**
	 * Called after connection a finished whether or not an error occurred.
	 * 
	 * @param url
	 * @param connection
	 */
	void onFinish(final URL url, final HttpURLConnection connection);

}
