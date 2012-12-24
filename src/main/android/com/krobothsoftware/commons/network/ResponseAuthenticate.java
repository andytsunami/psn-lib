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
