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

package com.krobothsoftware.commons.network.authorization;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.krobothsoftware.commons.network.NetworkHelper;
import com.krobothsoftware.commons.network.RequestBuilder;

/**
 * Manager for authorizing connections.
 * 
 * @see Authorization
 * 
 * @version 3.0
 * @since Nov 25 2012
 * @author Kyle Kroboth
 */
public class AuthorizationManager {
	private final List<Authorization> authorizationList;
	private final NetworkHelper networkHelper;
	private final Logger log;

	public AuthorizationManager(final NetworkHelper networkHelper) {
		log = LoggerFactory.getLogger(AuthorizationManager.class);
		this.networkHelper = networkHelper;
		authorizationList = new ArrayList<Authorization>();
	}

	/**
	 * Adds the authorization to list
	 * 
	 * @param uri
	 *            web url for authorization
	 * @param auth
	 *            authorization
	 */
	public void addAuthorization(final URL url, final Authorization auth) {
		if (!authorizationList.contains(auth)) {
			auth.setNetworkHelper(networkHelper);
			auth.setURL(url);
			auth.setLogger(log);
			authorizationList.add(auth);
		}
	}

	/**
	 * Gets authorization from url
	 * 
	 * @param uri
	 *            web url
	 * @return authorization if found, otherwise null
	 */
	public Authorization getAuthorization(final URL url) {
		for (final Authorization auth : authorizationList) {
			if (url.getHost().equals(auth.url.getHost())) return auth;
		}

		return null;
	}

	/**
	 * Authorize connection for request if found
	 * 
	 * @param request
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void authorizeConnection(final RequestBuilder request)
			throws IOException {
		for (final Authorization auth : authorizationList) {
			if (request.getUrl().getHost().equals(auth.url.getHost())) {
				auth.setup(request);
				break;
			}
		}
	}

	/**
	 * Authorize connection for request
	 * 
	 * @param request
	 * @param auth
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void authorizeConnection(final RequestBuilder request,
			final Authorization auth) throws IOException {
		auth.setLogger(log);
		auth.setNetworkHelper(networkHelper);
		auth.setURL(request.getUrl());
		auth.setup(request);
	}

	public void clearAuthorizations() {
		authorizationList.clear();
	}
}
