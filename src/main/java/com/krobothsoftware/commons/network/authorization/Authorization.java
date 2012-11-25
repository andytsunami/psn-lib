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

import org.slf4j.Logger;

import com.krobothsoftware.commons.network.NetworkHelper;
import com.krobothsoftware.commons.network.RequestBuilder;

/**
 * The Class Authorization is the base class for authorizations.
 * {@link AuthorizationManager} handles them.
 * 
 * @version 3.0
 * @since Nov 25 2012
 * @author Kyle Kroboth
 */
public abstract class Authorization {

	/** The network helper. */
	protected NetworkHelper networkHelper;

	/** The username. */
	protected String username;

	/** The password. */
	protected String password;

	/** The url. */
	protected URL url;

	/** The log. */
	protected Logger log;

	/**
	 * Instantiates a new authorization.
	 * 
	 * @param username
	 *            the username
	 * @param password
	 *            the password
	 */
	public Authorization(final String username, final String password) {
		this.username = username;
		this.password = password;
	}

	/**
	 * Called when connection needs to be set up for authorization
	 * 
	 * @param request
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public abstract void setup(RequestBuilder request) throws IOException;

	public abstract void reset();

	final void setURL(final URL url) {
		this.url = url;
	}

	void setNetworkHelper(final NetworkHelper networkHelper) {
		this.networkHelper = networkHelper;
	}

	void setLogger(final Logger logger) {
		log = logger;
	}

}
