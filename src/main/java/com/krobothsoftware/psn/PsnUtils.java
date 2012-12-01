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

package com.krobothsoftware.psn;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.krobothsoftware.commons.network.values.Cookie;
import com.krobothsoftware.commons.network.values.Cookie.Builder;
import com.krobothsoftware.commons.util.Base64;
import com.krobothsoftware.commons.util.CommonUtils;

/**
 * Utils for creating cookies, checking Ids, and other psn realated methods
 * 
 * @version 3.0
 * @since Nov 25 2012
 * @author Kyle Kroboth
 */
public final class PsnUtils {

	private PsnUtils() {

	}

	/**
	 * Generate <code>TICKET</code> cookie for US site
	 * 
	 * @param psnId
	 *            psn id
	 * @return TICKET cookie
	 */
	public static Cookie createLoginCookieTicket(final String psnId) {
		String user;
		try {
			user = CommonUtils.rightPad(
					Base64.encodeBytes(psnId.getBytes(), Base64.URL_SAFE)
							.replaceAll("=", ""), 40, 'A');
		} catch (final IOException e) {
			e.printStackTrace();
			return null;
		}

		final Builder builder = new Builder();
		builder.setName("TICKET")
				.setDomain(".playstation.com")
				.setValue(
						"MQAAAAAAAQcwAAC7AAgAFMAzj73%2FkeHsYc7s%2F2mIW0yw8KbaAAEABAAAAQAABwAIAAAB"
								+ "OjiuhlEA%0ABwAIAAABOj3U36AAAgAIMWuXSWuxj08ABAAg"
								+ user
								+ "%0AAAAACAAEdXMAAQAEAARiNgAAAAgAGFVQOTAwMi1OUFdBMDAwMzVfMDAAAAAAADARAAQHwgwfAAEA"
								+ "%0ABBkAAgAwEAAPBlNUUkhXSwAAATPOWLloAAAAADACAEQACAAE2%2B8LsgAIADgwNgIZAOCdX0tizuh0%0A"
								+ "aKpHe%2BtLap6jQMmNHw4pnAIZAJnz9fvKhnuM9uUbR5MHrq3i4ALOvWlUoA%3D%3D");

		return builder.build();

	}

	/**
	 * Generate <code>PSNS2STICKET</code> Cookie for US site
	 * 
	 * @param psnId
	 *            psn id
	 * @return PSNS2TICKET Cookie
	 */
	public static Cookie createLoginCookiePsnTicket(final String psnId) {
		String user;
		try {
			user = CommonUtils.rightPad(
					Base64.encodeBytes(psnId.getBytes(), Base64.URL_SAFE)
							.replaceAll("=", ""), 40, 'A');
		} catch (final IOException e) {
			e.printStackTrace();
			return null;
		}

		final Builder builder = new Builder();
		builder.setName("PSNS2STICKET")
				.setDomain(".playstation.com")
				.setValue(
						"MQAAAAAAAPgwAACsAAgAFBBkz19DwtiLL2zo5owu%2FGjZ4OihAAEABAAAAQAABwAIAAABOjiuhx"
								+ "EA%0ABwAIAAABOj3U36AAAgAIMWuXSWuxj08ABAAg"
								+ user
								+ "%0AAAAACAAEdXMAAQAEAARiNgAAAAgAGElWMDAwMS1OUFhTMDEwMDRfMDAAAAAAADARAAQHwgwfAAEA%0AB"
								+ "BkAAgAwEAAAAAAAADACAEQACAAEyS7rGwAIADgwNQIYC0htxjeTFvBo7nPpSPJCwAWjRtzfVa5f%0AAhkAwjEz"
								+ "sDCC0XZBjPz%2FKko5ogByHFzFXnx%2FAA%3D%3D");

		return builder.build();

	}

	/**
	 * Creates <code>PDC4_COOKIE</code> cookie for UK site
	 * 
	 * @param value
	 *            cookie value
	 * @return cookie
	 */
	public static Cookie createLoginCookiePdc4(final String value) {
		return new Cookie.Builder().setName("PDC4_COOKIE").setValue(value)
				.setDomain(".playstation.com").setHttpOnly(true).build();
	}

	/**
	 * Creates <code>s_token</code> cookie for UK site
	 * 
	 * @param value
	 *            cookie value
	 * @return cookie
	 */
	public static Cookie createLoginCookieToken(final String value) {
		return new Cookie.Builder().setName("s_token").setValue(value)
				.setDomain(".secure.eu.playstation.com").setSecure(true)
				.setHttpOnly(true).build();
	}

	/**
	 * Extract psn id from jid.
	 * 
	 * @param jid
	 * @return psnId
	 */
	public static String getPsnIdFromJid(final String jid) {
		if (isValidJid(jid)) return jid.substring(0, jid.indexOf("@"));
		else
			return jid;
	}

	/**
	 * Checks if JID is valid
	 * 
	 * @param jid
	 * @return true, if is valid
	 */
	public static boolean isValidJid(final String jid) {
		return jid.matches("(\\S+@\\S+)");
	}

	/**
	 * Checks if game Id is <i>official</i>
	 * 
	 * @param gameId
	 * @return true, if is valid game id
	 */
	public static boolean isValidGameId(final String gameId) {
		return gameId.length() == 12 && gameId.endsWith("_00");
	}

	/**
	 * Extracts game Id of valid trophy image link
	 * 
	 * @param trophyImageLink
	 * 
	 * @return official game Id
	 */
	public static String getGameIdOf(final String trophyImageLink) {
		final int index = trophyImageLink.indexOf("/trophy/np/") + 11;
		return trophyImageLink.substring(index, index + 12);
	}

	/**
	 * Gets the official date format used by <i>Official</i> methods
	 * 
	 * @param date
	 * @return official date format
	 */
	public static String getOfficialDateFormat(final Date date, Locale locale) {
		return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", locale)
				.format(date);
	}
}
