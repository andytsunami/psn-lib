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
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.krobothsoftware.commons.network.NetworkHelper.Method;
import com.krobothsoftware.commons.network.authorization.Authorization;
import com.krobothsoftware.commons.network.values.Cookie;
import com.krobothsoftware.commons.network.values.NameValuePair;

/**
 * Builder for requesting HTTP connections. Use
 * {@link NetworkHelper#setupMethod(Method, URL)} to initiate the builder.
 * 
 * 
 * @version 3.0
 * @since Nov 25 2012
 * @author Kyle Kroboth
 */
public final class RequestBuilder {
	final URL url;
	final Method method;
	boolean ignoreErrorChecks;
	boolean followRedirects = false;
	int connectTimeout = -1;
	int readTimeout = -1;
	boolean useCookies = true;
	final List<Cookie> cookies;
	boolean useAuth = true;
	Authorization auth;
	byte[] payload;
	final Map<String, String> headerMap;

	/**
	 * Instantiates new builder
	 * 
	 * @param method
	 *            HTTP method
	 * @param url
	 *            web url
	 */
	public RequestBuilder(final Method method, final URL url) {
		this.method = method;
		this.url = url;
		cookies = new ArrayList<Cookie>();
		headerMap = new HashMap<String, String>();
	}

	public URL getUrl() {
		return url;
	}

	public Method getMethod() {
		return method;
	}

	/**
	 * Ignores IOException if it occurs when {@link #execute(NetworkHelper)} is
	 * called. Otherwise it is thrown.
	 * 
	 * @param ignoreErrorChecks
	 *            error check
	 * @return request builder
	 */
	public RequestBuilder ignoreErrorChecks(final boolean ignoreErrorChecks) {
		this.ignoreErrorChecks = ignoreErrorChecks;
		return this;
	}

	public RequestBuilder setFollowRedirects(final boolean followRedirects) {
		this.followRedirects = followRedirects;
		return this;
	}

	public RequestBuilder setConnectTimeout(final int connectionTimeout) {
		connectTimeout = connectionTimeout;
		return this;
	}

	public RequestBuilder setReadTimeout(final int readTimeout) {
		this.readTimeout = readTimeout;
		return this;
	}

	public RequestBuilder useCookies(final boolean useCookies) {
		this.useCookies = useCookies;
		return this;
	}

	public RequestBuilder putCookie(final Cookie cookie) {
		cookies.add(cookie);
		return this;
	}

	public RequestBuilder putCookieList(final List<Cookie> cookies) {
		this.cookies.addAll(cookies);
		return this;
	}

	public RequestBuilder useAuthorizationIfNeeded(final boolean useAuth) {
		this.useAuth = useAuth;
		return this;
	}

	public RequestBuilder setAuthorization(final Authorization auth) {
		this.auth = auth;
		return this;
	}

	/**
	 * Sets the payload for POST and PUT {@link Method}
	 * 
	 * @param params
	 *            post params
	 * @param charset
	 *            the charset
	 * @return request builder
	 * @throws UnsupportedEncodingException
	 *             the unsupported encoding exception
	 */
	public RequestBuilder setPayload(final List<NameValuePair> params,
			final String charset) throws UnsupportedEncodingException {
		final StringBuilder builder = new StringBuilder();
		for (final NameValuePair pair : params) {
			builder.append('&').append(pair.getEncodedPair());
		}
		payload = builder.substring(1).getBytes(charset);
		return this;
	}

	/**
	 * Sets the payload for POST and PUT {@link Method}
	 * 
	 * @param payload
	 *            raw bytes
	 * @return request builder
	 */
	public RequestBuilder setPayload(final byte[] payload) {
		this.payload = payload;
		return this;
	}

	public RequestBuilder setHeader(final String name, final String value) {
		headerMap.put(name, value);
		return this;
	}

	/**
	 * Sends HTTP request based on request builder. Connection is not closed.
	 * 
	 * @param networkHelper
	 *            network helper
	 * @return response
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public Response execute(final NetworkHelper networkHelper)
			throws IOException {
		HttpURLConnection urlConnection;
		urlConnection = networkHelper.openConnection(url);
		urlConnection.setRequestMethod(method.name());
		urlConnection.setConnectTimeout(connectTimeout > -1 ? connectTimeout
				: networkHelper.defaultConnectTimeout);
		urlConnection.setReadTimeout(readTimeout > -1 ? readTimeout
				: networkHelper.defaultReadTimout);

		if (!cookies.isEmpty()) CookieManager
				.setCookies(urlConnection, cookies);
		if (useCookies) networkHelper.cookieManager.setupCookies(urlConnection);

		urlConnection.setInstanceFollowRedirects(followRedirects);
		if (auth != null) networkHelper.authManager.authorizeConnection(this,
				auth);
		if (useAuth) networkHelper.authManager.authorizeConnection(this);

		setupHeaders(urlConnection, networkHelper.defaultHeaderMap);
		setupHeaders(urlConnection, headerMap);

		switch (method) {
		case POST:
		case PUT:
			if (urlConnection.getRequestProperty("Content-Type") == null) {
				urlConnection.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded; charset=UTF-8");
			}
			if (payload == null) break;
			urlConnection.setDoOutput(true);
			urlConnection.setFixedLengthStreamingMode(payload.length);
			final OutputStream output = urlConnection.getOutputStream();
			output.write(payload);
			output.close();
			break;
		default:
			break;
		}

		InputStream inputStream = null;
		try {
			final URL url = urlConnection.getURL();
			networkHelper.log.info("Request {}:{}://{}{}", method,
					url.getProtocol(), url.getAuthority(), url.getPath());
			urlConnection.connect();
			inputStream = NetworkHelper.getInputStream(urlConnection);
			networkHelper.log.info("Response {}",
					urlConnection.getResponseMessage());
		} catch (final IOException e) {
			final int statusCode = urlConnection.getResponseCode();
			if (!ignoreErrorChecks && statusCode >= 400) {
				networkHelper.log.info("Response {}",
						urlConnection.getResponseMessage());
				inputStream = NetworkHelper.getErrorStream(urlConnection);
			} else
				throw e;
		} finally {
			if (useCookies) networkHelper.cookieManager
					.putCookieList(CookieManager.getCookies(urlConnection));
		}

		return new Response(urlConnection, inputStream,
				urlConnection.getResponseCode(),
				NetworkHelper.getCharset(urlConnection));

	}

	private void setupHeaders(final HttpURLConnection urlConnection,
			final Map<String, String> headers) {
		final Iterator<Entry<String, String>> iterator = headers.entrySet()
				.iterator();
		Entry<String, String> entry;
		while (iterator.hasNext()) {
			entry = iterator.next();
			urlConnection.setRequestProperty(entry.getKey(), entry.getValue());
		}
	}
}
