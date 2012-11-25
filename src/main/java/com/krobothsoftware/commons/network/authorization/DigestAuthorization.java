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
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 * 
 */

package com.krobothsoftware.commons.network.authorization;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Locale;

import com.krobothsoftware.commons.network.RequestBuilder;
import com.krobothsoftware.commons.network.values.NameValuePair;
import com.krobothsoftware.commons.util.CommonUtils;

/**
 * Authorization with the digest scheme
 * 
 * @version 3.0
 * @since Nov 25 2012
 * @author Kyle Kroboth
 */
public class DigestAuthorization extends Authorization {
	private static final String DEFAULT_ELEMENT_CHARSET = "US-ASCII";

	private String nonce;
	private String realm;
	private String algorithm;
	private String qop;
	private String charset;
	private String nc;
	private int nonceCount;

	/**
	 * Hexa values used when creating 32 character long digest in HTTP
	 * DigestScheme in case of authentication. Credit goes to Apache Commons
	 * HttpClient
	 * 
	 * @see #encode(byte[])
	 */
	private static final char[] HEXADECIMAL = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	public DigestAuthorization(final String username, final String password) {
		super(username, password);

	}

	@Override
	public void setup(final RequestBuilder request) throws IOException {
		final String header = nonceUsable(request);

		if (header != null) {
			request.setHeader("Authorization", header);
			return;
		}

		final HttpURLConnection tmpUrlConnection = networkHelper
				.openConnection(request.getUrl());

		try {
			tmpUrlConnection.getInputStream();
		} catch (final IOException e) {
			if (tmpUrlConnection.getResponseCode() != HttpURLConnection.HTTP_UNAUTHORIZED) {
				throw e;
			}
		}

		nonceCount = 1;

		realm = getHeaderValueByType("realm",
				tmpUrlConnection.getHeaderField("WWW-Authenticate"));

		nonce = getHeaderValueByType("nonce",
				tmpUrlConnection.getHeaderField("WWW-Authenticate"));

		algorithm = getHeaderValueByType("algorithm",
				tmpUrlConnection.getHeaderField("WWW-Authenticate"));

		qop = getHeaderValueByType("qop",
				tmpUrlConnection.getHeaderField("WWW-Authenticate"));

		charset = getHeaderValueByType("charset",
				tmpUrlConnection.getHeaderField("WWW-Authenticate"));

		request.setHeader("Authorization", createHeader(request));

	}

	@Override
	public void reset() {
		nonce = null;
		realm = null;
		algorithm = null;
		qop = null;
		charset = null;
		nc = null;
		nonceCount = 0;

	}

	private String nonceUsable(final RequestBuilder request) {

		if (nonce == null) return null;
		nonceCount++;

		return createHeader(request);

	}

	private String createHeader(final RequestBuilder request) {
		MessageDigest messageDigest = null;
		StringBuffer buffer = null;

		try {
			messageDigest = MessageDigest.getInstance("MD5");

			final StringBuilder sb = new StringBuilder(256);
			final Formatter formatter = new Formatter(sb, Locale.US);
			formatter.format("%08x", nonceCount);
			formatter.close();
			nc = sb.toString();

			if (charset == null) {
				charset = DEFAULT_ELEMENT_CHARSET;
			}

			// generate client nonce
			final String cnonce = createCnonce();

			// make MD5 hash of username, realm, and password
			buffer = new StringBuffer();
			buffer.append(username).append(':').append(realm).append(':')
					.append(password);
			String hash1 = buffer.toString();
			hash1 = encode(messageDigest.digest(hash1.getBytes(charset)));

			// made MD5 hash of method name and url path
			buffer = new StringBuffer();
			buffer.append(request.getMethod()).append(':')
					.append(request.getUrl().getPath());
			String hash2 = buffer.toString();
			hash2 = encode(messageDigest.digest(hash2.getBytes(charset)));

			// make MD5 hash of hash1, nonce, nc, cnonce, qop, and hash2
			buffer = new StringBuffer();
			buffer.append(hash1).append(':').append(nonce).append(':')
					.append(nc).append(':').append(cnonce).append(':')
					.append(qop).append(':').append(hash2);
			String response = buffer.toString();
			response = encode(messageDigest.digest(response.getBytes(charset)));

			// setup header
			final ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new NameValuePair("username", username));
			params.add(new NameValuePair("realm", realm));
			params.add(new NameValuePair("nonce", nonce));
			params.add(new NameValuePair("uri", request.getUrl().getPath()));
			params.add(new NameValuePair("algorithm", algorithm));
			params.add(new NameValuePair("response", response));
			params.add(new NameValuePair("qop", qop));
			params.add(new NameValuePair("nc", nc));
			params.add(new NameValuePair("cnonce", cnonce));

			buffer = new StringBuffer();
			buffer.append("Digest ");

			for (final NameValuePair pair : params) {
				if (!pair.getName().equalsIgnoreCase("username")) buffer
						.append(", ");
				// check if need parentheses
				if (pair.getName().equalsIgnoreCase("nc")
						|| pair.getName().equals("qop")) {
					buffer.append(pair.getPair());
				} else {
					buffer.append(pair.getQuotedPair());
				}
			}

		} catch (final NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (final UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		log.info("Authorizing Digest[{}] {}", nonceCount, url);

		return buffer.toString();
	}

	private String getHeaderValueByType(final String type,
			final String headerText) {
		String header = headerText.replaceFirst("Digest ", "");
		header = header.replaceFirst("Basic ", "");
		final String[] values = header.split(",");

		for (final String value : values) {
			if (type.equalsIgnoreCase(value.substring(0, value.indexOf("="))
					.trim())) {
				return CommonUtils.trim(
						value.substring(value.indexOf("=") + 1), '"');
			}
		}

		return null;
	}

	/**
	 * Encodes the 128 bit (16 bytes) MD5 digest into a 32 characters long
	 * <CODE>String</CODE> according to RFC 2617. Credit goes to Apache Commons
	 * HttpClient
	 * 
	 * @param binaryData
	 *            array containing the digest
	 * @return encoded MD5, or <CODE>null</CODE> if encoding failed
	 */
	private static String encode(final byte[] binaryData) {
		final int n = binaryData.length;
		final char[] buffer = new char[n * 2];
		for (int i = 0; i < n; i++) {
			final int low = binaryData[i] & 0x0f;
			final int high = (binaryData[i] & 0xf0) >> 4;
			buffer[i * 2] = HEXADECIMAL[high];
			buffer[i * 2 + 1] = HEXADECIMAL[low];
		}

		return new String(buffer);
	}

	/**
	 * Creates a random cnonce value based on the current time. Credit goes to
	 * Apache Commons HttpClient
	 * 
	 * @return The cnonce value as String.
	 */
	public static String createCnonce() {
		final SecureRandom rnd = new SecureRandom();
		final byte[] tmp = new byte[8];
		rnd.nextBytes(tmp);
		return encode(tmp);
	}

}
