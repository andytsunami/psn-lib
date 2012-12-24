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

package com.krobothsoftware.psn.internal;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.SAXException;

import com.krobothsoftware.commons.parse.HandlerXml;
import com.krobothsoftware.psn.model.FriendStatus;
import com.krobothsoftware.psn.model.PsnFriendData;

/**
 * 
 * @version 3.0.2
 * @since Nov 25 2012
 * @author Kyle Kroboth
 */
public final class HandlerXmlFriend extends HandlerXml {
	private static final String PSN_FRIEND = "psn_friend";
	private static final String ONLINEID = "onlineid";
	private static final String CURRENT_PRESENCE = "current_presence";
	private static final String CURRENT_GAME = "current_game";
	private static final String CURRENT_AVATAR = "current_avatar";
	private static final String COMMENT = "comment";
	private static final String PLAYSTATION_PLUS = "playstationplus";

	private static final String LEVEL = "level";
	private static final String PLATINUM = "platinum";
	private static final String GOLD = "gold";
	private static final String SILVER = "silver";
	private static final String BRONZE = "bronze";

	private final List<PsnFriendData> list;
	private final PsnFriendData.Builder builder;

	public HandlerXmlFriend() {
		list = new ArrayList<PsnFriendData>();
		builder = new PsnFriendData.Builder();
	}

	public List<PsnFriendData> getFriendList() {
		return list;
	}

	@Override
	public void endElement(final String uri, final String localName,
			final String qName) throws SAXException {

		if (qLocal(qName, localName).equalsIgnoreCase(PSN_FRIEND)) list
				.add(builder.build());

	}

	@Override
	public void characters(final char[] ch, final int start, final int length)
			throws SAXException {

		String str;

		if (calledStartElement) {
			if (startTag.equalsIgnoreCase(ONLINEID)) builder
					.setPsnId(new String(ch, start, length));
			else if (startTag.equalsIgnoreCase(CURRENT_PRESENCE)) builder
					.setPresence(getOnlineStatus(new String(ch, start, length)));
			else if (startTag.equalsIgnoreCase(CURRENT_GAME)) {
				str = new String(ch, start, length);
				builder.setGame(str.equals("null") ? null : str);
			} else if (startTag.equalsIgnoreCase(CURRENT_AVATAR)) builder
					.setAvatar(new String(ch, start, length));
			else if (startTag.equalsIgnoreCase(COMMENT)) {
				str = new String(ch, start, length);
				builder.setComment(str.equals("null") ? null : str);
			} else if (startTag.equalsIgnoreCase(PLAYSTATION_PLUS)) builder
					.setPP(Boolean.parseBoolean(new String(ch, start, length)));
			else if (startTag.equalsIgnoreCase(LEVEL)) builder.setLevel(Integer
					.parseInt(new String(ch, start, length)));
			else if (startTag.equalsIgnoreCase(PLATINUM)) builder
					.setPlatinum(Integer
							.parseInt(new String(ch, start, length)));
			else if (startTag.equalsIgnoreCase(GOLD)) builder.setGold(Integer
					.parseInt(new String(ch, start, length)));
			else if (startTag.equalsIgnoreCase(SILVER)) builder
					.setSilver(Integer.parseInt(new String(ch, start, length)));
			else if (startTag.equalsIgnoreCase(BRONZE)) builder
					.setBronze(Integer.parseInt(new String(ch, start, length)));
		}

		calledStartElement = false;

	}

	private FriendStatus getOnlineStatus(final String currentPresence) {
		if (currentPresence.equalsIgnoreCase("offline")) return FriendStatus.OFFLINE;
		else if (currentPresence.equalsIgnoreCase("online")
				|| currentPresence.equalsIgnoreCase("online-ingame")) return FriendStatus.ONLINE;
		else if (currentPresence.equalsIgnoreCase("online-away")
				|| currentPresence.equalsIgnoreCase("online-ingame-away")) return FriendStatus.AWAY;

		return FriendStatus.OFFLINE;
	}

}
