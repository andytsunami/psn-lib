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
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.krobothsoftware.commons.network.NetworkHelper.Method;
import com.krobothsoftware.commons.network.values.Cookie;
import com.krobothsoftware.commons.network.values.NameValuePair;

/**
 * Builder for requesting HTTP connections.
 * 
 * 
 * @version 3.0.2
 * @since Nov 25 2012
 * @author Kyle Kroboth
 */
public class RequestBuilder {
	final URL url;
	final Method method;
	Proxy proxy;
	boolean ignoreErrorChecks;
	boolean followRedirects = false;
	int connectTimeout = -1;
	int readTimeout = -1;
	boolean useCookies = true;
	final List<Cookie> cookies;
	byte[] payload;
	final Map<String, String> headerMap;

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
	 * Sets proxy for request. If <code>NetworkHelper</code> has a proxy, it
	 * will use request's proxy instead.
	 * 
	 * @param proxy
	 */
	public void setProxy(Proxy proxy) {
		this.proxy = proxy;
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

	public RequestBuilder followRedirects(final boolean followRedirects) {
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
		networkHelper.connListener.onRequest(this);
		HttpURLConnection urlConnection;
		if (proxy != null) urlConnection = networkHelper.openConnection(url,
				proxy);
		else
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

		Map<String, String> headers = new HashMap<String, String>(
				networkHelper.defaultHeaderMap);
		headers.putAll(headerMap);
		setupHeaders(urlConnection, headers);

		switch (method) {
		case POST:
		case PUT:
			if (urlConnection.getRequestProperty("Content-Type") == null) {
				urlConnection.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
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
			networkHelper.connListener.onFinish(url, urlConnection);
		}

		return getResponse(urlConnection, inputStream);

	}

	@Override
	public String toString() {
		return method + ":" + url.toString();
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

	private Response getResponse(HttpURLConnection urlConnection,
			InputStream inputStream) throws IOException {
		int code = urlConnection.getResponseCode();
		@SuppressWarnings("resource")
		UnclosableInputStream stream = (inputStream != null) ? new UnclosableInputStream(
				inputStream) : null;
		String charset = NetworkHelper.getCharset(urlConnection);
		switch (code) {
		case HttpURLConnection.HTTP_MOVED_TEMP:
			return new ResponseRedirect(urlConnection, stream, code, charset);
		case HttpURLConnection.HTTP_UNAUTHORIZED:
			return new ResponseAuthenticate(urlConnection, stream, code,
					charset);
		default:
			return new Response(urlConnection, stream, code, charset);
		}
	}
}
