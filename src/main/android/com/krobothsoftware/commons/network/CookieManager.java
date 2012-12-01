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

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.krobothsoftware.commons.network.values.Cookie;
import com.krobothsoftware.commons.util.CommonUtils;

/**
 * CookieManager stores, and sends HTTP Cookies through
 * {@link java.net.HttpURLConnection}.
 * 
 * @version 3.0
 * @since Nov 25 2012
 * @author Kyle Kroboth
 */
public class CookieManager {
	private final Map<String, List<Cookie>> cookieMap;
	private final List<String> domainList;
	private final Logger log;

	public CookieManager() {
		log = LoggerFactory.getLogger(CookieManager.class);
		cookieMap = new HashMap<String, List<Cookie>>();
		domainList = new ArrayList<String>();
	}

	/**
	 * Stores cookies which are giving in a list. Any duplicate cookies
	 * <b>will</b> be replaced.
	 * 
	 * @param cookieList
	 *            {@link Cookie} list
	 */
	public void putCookieList(final List<Cookie> cookieList) {
		for (final Cookie cookie : cookieList) {
			putCookie(cookie);
		}
	}

	/**
	 * Stores an individual cookie. Will replace if is duplicate.
	 * 
	 * @param cookie
	 *            {@link Cookie}
	 */
	public void putCookie(final Cookie cookie) {
		final String domain = cookie.getDomain();
		log.debug("Storing [{}]", cookie);

		if (cookieMap.get(domain) == null) {
			final ArrayList<Cookie> list = new ArrayList<Cookie>();
			list.add(cookie);
			cookieMap.put(domain, list);
			domainList.add(domain);
		} else {
			final List<Cookie> cookieList = cookieMap.get(domain);
			cookieList.remove(cookie);
			cookieList.add(cookie);
		}
	}

	/**
	 * Gets each domain with its list of cookies.
	 * 
	 * @return cookie map
	 */
	public Map<String, List<Cookie>> getCookieMap() {
		return cookieMap;
	}

	/**
	 * Sets up cookies based on <code>urlConnection</code> domain.
	 * 
	 * @param urlConnection
	 *            connection for cookies
	 * 
	 */
	public void setupCookies(final HttpURLConnection urlConnection) {
		// check for domains
		final String host = urlConnection.getURL().getHost();
		final List<Cookie> cookieList = new ArrayList<Cookie>();
		for (final String domain : domainList) {
			if (host.contains(domain)) {
				cookieList.addAll(cookieMap.get(domain));
			}
		}
		if (!cookieList.isEmpty()) {
			setupCookies(urlConnection, cookieList);
		}
	}

	public List<Cookie> getCookieList(final String domain) {
		return cookieMap.get(domain);
	}

	/**
	 * Gets the cookie.
	 * 
	 * @param url
	 *            the url
	 * @param name
	 *            the name
	 * @return the cookie
	 */
	public Cookie getCookie(final URL url, final String name) {
		return getCookie(url.getHost(), name);
	}

	public Cookie getCookie(final String domain, final String name) {
		final List<Cookie> cookieList = cookieMap.get(domain);

		if (cookieList == null) return null;

		for (final Cookie cookie : cookieList) {
			if (cookie.getName().equalsIgnoreCase(name)) return cookie;
		}

		return null;
	}

	/**
	 * Searches all domains for cookie
	 * 
	 * @param name
	 *            the name
	 * @return the cookie
	 */
	public Cookie getCookie(final String name) {
		for (final Map.Entry<String, List<Cookie>> entry : cookieMap.entrySet()) {
			for (final Cookie cookie : entry.getValue()) {
				if (cookie.getName().equalsIgnoreCase(name)) return cookie;
			}
		}

		return null;
	}

	/**
	 * Searches all domains and removes cookie
	 * 
	 * @param name
	 *            the name
	 * @return true, if successful
	 */
	public boolean removeCookie(final String name) {
		for (final Map.Entry<String, List<Cookie>> entry : cookieMap.entrySet()) {
			for (final Cookie cookie : entry.getValue()) {
				if (cookie.getName().equalsIgnoreCase(name)) {
					entry.getValue().remove(cookie);
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Clear cookies.
	 */
	public void clearCookies() {
		cookieMap.clear();
		domainList.clear();
	}

	/**
	 * Gets the cookies.
	 * 
	 * @param urlConnection
	 *            the url connection
	 * @return the cookies
	 */
	public static List<Cookie> getCookies(final HttpURLConnection urlConnection) {
		final ArrayList<Cookie> cookieList = new ArrayList<Cookie>();

		int i = 1;
		String header;
		while ((header = urlConnection.getHeaderFieldKey(i)) != null) {
			if (header.equals("Set-Cookie")) {
				cookieList.add(Cookie.parseCookie(urlConnection.getURL(),
						urlConnection.getHeaderField(i)));

			}
			i++;
		}

		return cookieList;
	}

	public static void setCookies(final HttpURLConnection urlConnection,
			final List<Cookie> cookies) {
		final StringBuilder builder = new StringBuilder();
		for (final Cookie cookie : cookies) {
			builder.append(';').append(' ').append(cookie.getCookieString());
		}
		urlConnection.setRequestProperty("Cookie",
				CommonUtils.trim(builder.substring(1)));
	}

	private void setupCookies(final HttpURLConnection urlConnection,
			final List<Cookie> cookieList) {
		if (cookieList == null || cookieList.isEmpty()) return;

		StringBuilder builder;

		if (urlConnection.getRequestProperty("Cookie") != null) {
			builder = new StringBuilder(
					urlConnection.getRequestProperty("Cookie"));
		} else {
			builder = new StringBuilder();
		}

		for (final Cookie cookie : cookieList) {
			builder.append(';').append(' ').append(cookie.getCookieString());
		}
		String cookieString;
		if (urlConnection.getRequestProperty("Cookie") == null) cookieString = builder
				.substring(1);
		else
			cookieString = builder.toString();
		cookieString = CommonUtils.trim(cookieString);

		urlConnection.setRequestProperty("Cookie", cookieString);
	}
}
