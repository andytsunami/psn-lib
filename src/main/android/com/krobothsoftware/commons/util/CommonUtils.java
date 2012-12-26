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
import java.util.Scanner;

/**
 * 
 * @version 3.0.2
 * @since Nov 25 2012
 * @author Kyle Kroboth
 */
public final class CommonUtils {

	private CommonUtils() {

	}

	public static String getContentFromInputStream(
			final InputStream inputStream, final String charset)
			throws IOException {
		Scanner s = new Scanner(inputStream, charset);
		s.useDelimiter("\\A");
		String text = s.hasNext() ? s.next() : "";
		s.close();
		return text;
	}

	/**
	 * Trims all whitespace off including special characters and non breaking
	 * space.
	 * 
	 * @param str
	 *            text to trim
	 * @return trimmed string
	 */
	public static String trim(final String str) {
		int count = str.length();
		int len = str.length();
		int st = 0;
		int off = 0;
		char[] val = str.toCharArray();

		while ((st < len) && (isWhiteSpace(val[off + st])))
			st++;
		while ((st < len) && (isWhiteSpace(val[off + len - 1])))
			len--;

		return ((st > 0) || (len < count)) ? str.substring(st, len) : str;
	}

	/**
	 * Trims string for character
	 * 
	 * @param str
	 *            text to trim
	 * @param ch
	 *            trim character
	 * @return trimmed string
	 */
	public static String trim(final String str, final char ch) {
		int count = str.length();
		int len = str.length();
		int st = 0;
		int off = 0;
		char[] val = str.toCharArray();

		while ((st < len) && (val[off + st] <= ch)) {
			st++;
		}
		while ((st < len) && (val[off + len - 1] <= ch)) {
			len--;
		}

		return ((st > 0) || (len < count)) ? str.substring(st, len) : str;
	}

	/**
	 * Trims string with first and last characters
	 * 
	 * @param str
	 *            text to trim
	 * @param chF
	 *            first trim character
	 * @param chL
	 *            last trim character
	 * @return trimmed string
	 */
	public static String trim(final String str, char chF, char chL) {
		int count = str.length();
		int len = str.length();
		int st = 0;
		int off = 0;
		char[] val = str.toCharArray();

		while ((st < len) && (val[off + st] <= chF)) {
			st++;
		}
		while ((st < len) && (val[off + len - 1] <= chL)) {
			len--;
		}

		return ((st > 0) || (len < count)) ? str.substring(st, len) : str;
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

			while (true) {
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

	private static boolean isWhiteSpace(char ch) {
		if ('\u00A0' == ch) return true;
		else if ('\u2007' == ch) return true;
		else if ('\u202F' == ch) return true;
		else if (Character.isWhitespace(ch)) return true;
		else
			return false;
	}
}
