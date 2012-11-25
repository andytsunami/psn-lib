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

package com.krobothsoftware.commons.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Common Util class
 * 
 * @version 3.0
 * @since Nov 25 2012
 * @author Kyle Kroboth
 */
public final class CommonUtils {

	private CommonUtils() {

	}

	public static String getContentFromInputStream(
			final InputStream inputStream, final String charset)
			throws IOException {
		final BufferedReader in = new BufferedReader(new InputStreamReader(
				inputStream, charset));

		final StringBuilder builder = new StringBuilder();
		String inputLine;

		while ((inputLine = in.readLine()) != null)
			builder.append(inputLine);
		in.close();
		inputStream.close();

		return builder.toString();
	}

	/**
	 * Trim off all the whitespace. {@link java.lang.String#trim()} does not
	 * take off certain whitespace characters.
	 * 
	 * @param text
	 *            text to trim
	 * @return modified string
	 */
	public static String trim(final String text) {
		StringBuilder buffer = new StringBuilder(text);

		for (int i = 0; i < buffer.length(); i++) {
			if (Character.isWhitespace(buffer.charAt(i))) {
				buffer.deleteCharAt(i);
			} else {
				break;
			}
		}

		buffer = new StringBuilder(buffer.toString());

		for (int i = buffer.length() - 1; i > 0; --i) {
			if (Character.isWhitespace(buffer.charAt(i))) {
				buffer.deleteCharAt(i);
			} else {
				break;
			}
		}

		return buffer.toString();
	}

	/**
	 * Trim off with character
	 * 
	 * @param text
	 *            text to trim
	 * @param trimCharacter
	 *            trim character
	 * @return modified string
	 */
	public static String trim(final String text, final char trimCharacter) {
		StringBuilder buffer = new StringBuilder(text);

		for (int i = 0; i < buffer.length(); i++) {
			if (trimCharacter == buffer.charAt(i)) {
				buffer.deleteCharAt(i);
			} else {
				break;
			}
		}

		buffer = new StringBuilder(buffer.toString());

		for (int i = buffer.length() - 1; i > 0; --i) {
			if (trimCharacter == buffer.charAt(i)) {
				buffer.deleteCharAt(i);
			} else {
				break;
			}
		}

		return buffer.toString();
	}

	/**
	 * Trims the first character for first part of string and second character
	 * for last part of string.
	 * 
	 * @param text
	 *            text to trim
	 * @param firstTrim
	 *            first trim character
	 * @param secondTrim
	 *            second trim character
	 * @return modified string
	 */
	public static String trim(final String text, final char firstTrim,
			final char secondTrim) {
		StringBuffer buffer = new StringBuffer(text);

		for (int i = 0; i < buffer.length(); i++) {
			if (firstTrim == buffer.charAt(i)) {
				buffer.deleteCharAt(i);
			} else {
				break;
			}
		}

		buffer = new StringBuffer(buffer.toString());

		for (int i = buffer.length() - 1; i > 0; --i) {
			if (secondTrim == buffer.charAt(i)) {
				buffer.deleteCharAt(i);
			} else {
				break;
			}
		}

		return buffer.toString();
	}

	/**
	 * Right pad String
	 * 
	 * @param str
	 *            <code>String</code> to pad
	 * @param size
	 *            padding size
	 * @param padChar
	 *            pad char
	 * @return padded string
	 */
	public static String rightPad(final String str, final int size,
			final char padChar) {
		final int pads = size - str.length();
		final StringBuilder builder = new StringBuilder(str);

		for (int i = 0; i < pads; i++) {
			builder.append(padChar);
		}

		return builder.toString();
	}

	/**
	 * Checks if inputstream has the <code>contains</code> string
	 * 
	 * @param input
	 *            inputstream to be checked
	 * @param contains
	 *            contains string
	 * @param charset
	 *            encoding charset
	 * @return true, if successful
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static boolean streamingContains(final InputStream input,
			final String contains, final String charset) throws IOException {
		BufferedReader in = null;
		int count = 0;
		final char[] content = contains.toCharArray();
		try {
			in = new BufferedReader(new InputStreamReader(input, charset));

			for (;;) {
				final char c = (char) in.read();
				if (c == content[count]) {
					count++;
				} else
					count = 0;

				if (count == contains.length()) return true;
			}

		} catch (final UnsupportedEncodingException e) {
			e.printStackTrace();
			return false;
		} finally {
			if (in != null) in.close();
		}
	}

	// public static
}
