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
