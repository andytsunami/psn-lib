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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.krobothsoftware.commons.network.authorization.AuthorizationManager;
import com.krobothsoftware.commons.network.values.NameValuePair;

/**
 * Helper class for creating, and retrieving data from
 * {@link java.net.HttpURLConnection}
 * 
 * 
 * @see HttpURLConnection
 * @version 3.0
 * @since Nov 25 2012
 * @author Kyle Kroboth
 */
public class NetworkHelper {
	protected final CookieManager cookieManager;
	protected final AuthorizationManager authManager;
	protected final Logger log;

	Proxy proxy;
	final Map<String, String> defaultHeaderMap;
	int defaultConnectTimeout;
	int defaultReadTimout;

	public NetworkHelper() {
		log = LoggerFactory.getLogger(NetworkHelper.class);
		cookieManager = new CookieManager();
		authManager = new AuthorizationManager(this);
		defaultHeaderMap = new HashMap<String, String>(2);
		setupHeaders();
	}

	/**
	 * Cleans up cookies, authorizations, sets default headers, and removes
	 * proxy.
	 */
	public void reset() {
		log.info("Cleaning up");
		defaultConnectTimeout = 0;
		defaultReadTimout = 0;
		proxy = null;
		cookieManager.clearCookies();
		authManager.clearAuthorizations();
		defaultHeaderMap.clear();
		setupHeaders();
	}

	private void setupHeaders() {
		defaultHeaderMap.put("Accept-Encoding", "gzip, deflate");
		defaultHeaderMap.put("Accept-Charset", "UTF-8");
		defaultHeaderMap.put("Connection", "close");
	}

	/**
	 * Sets the proxy used for opening up connections.
	 * 
	 * @param proxy
	 *            the new proxy
	 * @see #openConnection(URL)
	 */
	public void setProxy(final Proxy proxy) {
		this.proxy = proxy;
	}

	public Logger getLogger() {
		return log;
	}

	/**
	 * Sets default header for connections. If the connection has the same
	 * header, the default one will <b>not</b> be used.
	 * 
	 * @param name
	 *            header name
	 * @param value
	 *            header value
	 */
	public void setDefaultHeader(final String name, final String value) {
		defaultHeaderMap.put(name, value);
	}

	/**
	 * Removes default header used for connections.
	 * 
	 * @param name
	 *            header name
	 * @return header value, null if not found
	 */
	public String removeDefaultHeader(final String name) {
		return defaultHeaderMap.remove(name);
	}

	/**
	 * Sets default connect timeout for connections. If the connection already
	 * has a connect timeout set, the default one will <b>not</b> be used.
	 * 
	 * @param connectTimeout
	 *            new default connect timout
	 */
	public void setDefaultConnectTimeout(final int connectTimeout) {
		defaultConnectTimeout = connectTimeout;
	}

	/**
	 * Sets default read timeout for connections. If the connection already has
	 * a connect read set, the default one will <b>not</b> be used.
	 * 
	 * @param readTimeout
	 *            new default read timeout
	 */
	public void setDefaultReadTimeout(final int readTimeout) {
		defaultReadTimout = readTimeout;
	}

	public AuthorizationManager getAuthorizationManager() {
		return authManager;
	}

	public CookieManager getCookieManager() {
		return cookieManager;
	}

	/**
	 * Opens connection and sets proxy if not null.
	 * 
	 * @param url
	 *            url for connection
	 * @return {@link java.net.HttpURLConnection}
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @see #setProxy(Proxy)
	 */
	public HttpURLConnection openConnection(final URL url) throws IOException {
		HttpURLConnection urlConnection = null;
		if (proxy != null) urlConnection = (HttpURLConnection) url
				.openConnection(proxy);
		else
			urlConnection = (HttpURLConnection) url.openConnection();

		return urlConnection;

	}

	/**
	 * Sets up request builder from <code>method</code> and <code>url</code>.
	 * 
	 * @param method
	 *            HTTP method
	 * @param url
	 *            web url
	 * @return {@link RequestBuilder}
	 */
	public final RequestBuilder setupMethod(final Method method, final URL url) {
		return new RequestBuilder(method, url);
	}

	/**
	 * Sets up request builder form <code>method</code>, <code>url</code> and
	 * appends <code>query</code> list.
	 * 
	 * @param method
	 *            HTTP method
	 * @param url
	 *            web url
	 * @param query
	 *            url query
	 * @return request builder
	 * @throws MalformedURLException
	 *             the malformed url exception
	 */
	public final RequestBuilder setupMethod(final Method method, final URL url,
			final List<NameValuePair> query) throws MalformedURLException {
		final StringBuilder builder = new StringBuilder();
		builder.append('?');
		for (final NameValuePair pair : query) {
			builder.append('&');
			builder.append(pair.getEncodedPair());
		}
		return new RequestBuilder(method, new URL(url, builder.substring(1)));
	}

	/**
	 * Retrieves url query and puts it in a {@link NameValuePair} List.
	 * 
	 * @param url
	 *            web url
	 * @return query params
	 */
	public static List<NameValuePair> getUrlQueryList(final URL url) {
		final String query = url.getQuery();
		if (query == null) return new ArrayList<NameValuePair>();
		final String[] params = query.split("&");
		final ArrayList<NameValuePair> listParams = new ArrayList<NameValuePair>(
				params.length);

		String[] value;
		for (final String param : params) {
			value = param.split("=");
			listParams.add(new NameValuePair(value[0], value[1]));
		}

		return listParams;
	}

	/**
	 * Gets the charset from <code>urlConnection</code>. If none found, will
	 * return the default UTF-8.
	 * 
	 * @param urlConnection
	 *            connection
	 * @return charset
	 */
	public static String getCharset(final HttpURLConnection urlConnection) {
		final String[] values = urlConnection.getContentType().split(";");

		String charset = null;

		for (String value : values) {
			value = value.trim();

			if (value.toLowerCase().startsWith("charset=")) {
				charset = value.substring("charset=".length());
			}
		}

		if (charset == null) {
			charset = "UTF-8";
		}

		return charset;

	}

	/**
	 * Gets the correct {@link java.io.InputStream} based on
	 * <code>urlConnection</code> encoding.
	 * 
	 * @param urlConnection
	 *            connection
	 * @return inputstream from encoding. If not supported returns normal
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 *             {@link java.io.InputStream}
	 */
	public static InputStream getInputStream(
			final HttpURLConnection urlConnection) throws IOException {
		if (urlConnection.getRequestMethod().equals("HEAD")) return null;
		final String encoding = urlConnection.getContentEncoding();
		if (encoding != null && encoding.equalsIgnoreCase("gzip")) {
			return new GZIPInputStream(urlConnection.getInputStream());
		} else if (encoding != null && encoding.equalsIgnoreCase("deflate")) {
			return new InflaterInputStream(urlConnection.getInputStream(),
					new Inflater(true));
		} else {
			return urlConnection.getInputStream();
		}
	}

	/**
	 * Gets the correct {@link java.io.InputStream} based on
	 * <code>urlConnection</code> encoding.
	 * 
	 * @param urlConnection
	 *            connection
	 * @return errorstream from encoding. If not supported returns normal
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 *             {@link java.io.InputStream}
	 */
	public static InputStream getErrorStream(
			final HttpURLConnection urlConnection) throws IOException {
		if (urlConnection.getRequestMethod().equals("HEAD")) return null;
		final String encoding = urlConnection.getContentEncoding();
		if (encoding != null && encoding.equalsIgnoreCase("gzip")) {
			return new GZIPInputStream(urlConnection.getErrorStream());
		} else if (encoding != null && encoding.equalsIgnoreCase("deflate")) {
			return new InflaterInputStream(urlConnection.getErrorStream(),
					new Inflater(true));
		} else {
			return urlConnection.getErrorStream();
		}
	}

	/**
	 * HTTP Methods used for connections
	 */
	public enum Method {

		GET, POST, HEAD, PUT;
	}
}
