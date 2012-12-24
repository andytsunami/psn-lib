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
