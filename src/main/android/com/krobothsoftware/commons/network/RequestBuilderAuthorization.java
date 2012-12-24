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
import java.net.URL;

import com.krobothsoftware.commons.network.NetworkHelper.Method;
import com.krobothsoftware.commons.network.authorization.Authorization;
import com.krobothsoftware.commons.network.authorization.AuthorizationManager;

/**
 * Builder for authorization HTTP connections. If <code>authorization</code> is
 * null, will check {@link AuthorizationManager}
 * 
 * @version 3.0.2
 * @since Dec 24 2012
 * @author Kyle Kroboth
 */
public class RequestBuilderAuthorization extends RequestBuilder {
	Authorization auth;

	public RequestBuilderAuthorization(Method method, URL url,
			Authorization auth) {
		super(method, url);
	}

	public RequestBuilderAuthorization(Method method, URL url) {
		super(method, url);
	}

	/**
	 * Sends HTTP request based on request builder with authorization.
	 * Connection is not closed.
	 * 
	 * @param networkHelper
	 *            network helper
	 * @return response
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	@Override
	public Response execute(NetworkHelper networkHelper) throws IOException {
		if (auth != null) networkHelper.authManager.authorizeConnection(this,
				auth);
		else
			networkHelper.authManager.authorizeConnection(this);
		return super.execute(networkHelper);
	}

}
