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
