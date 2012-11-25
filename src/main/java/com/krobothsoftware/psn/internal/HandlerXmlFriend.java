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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.krobothsoftware.commons.parse.HandlerXml;
import com.krobothsoftware.psn.model.FriendStatus;
import com.krobothsoftware.psn.model.PsnFriendData;

/**
 * 
 * @version 3.0
 * @since Nov 25 2012
 * @author Kyle Kroboth
 */
public final class HandlerXmlFriend extends HandlerXml {
	@SuppressWarnings("unused")
	private static final String FRIEND_LIST = "friend-list";
	private static final String PSN_FRIEND = "psn_friend";
	private static final String ONLINEID = "onlineid";
	private static final String QUICK_FRIEND = "quick_friend";
	private static final String CURRENT_PRESENCE = "current_presence";
	private static final String CURRENT_GAME = "current_game";
	private static final String CURRENT_AVATAR = "current_avatar";
	private static final String COMMENT = "comment";
	private static final String PLAYSTATION_PLUS = "playstationplus";

	@SuppressWarnings("unused")
	private static final String TROPHY = "trophy";
	private static final String LEVEL = "level";
	private static final String PLATINUM = "platinum";
	private static final String GOLD = "gold";
	private static final String SILVER = "silver";
	private static final String BRONZE = "bronze";

	private final List<PsnFriendData> list;

	// friend
	private String onlineId;
	private boolean isQuickFriend;
	private FriendStatus presence;
	private String game;
	private String avatar;
	private String comment;
	private boolean plus;

	// trophy
	private int level;
	private int platinum;
	private int gold;
	private int silver;
	private int bronze;

	public HandlerXmlFriend() {
		list = new ArrayList<PsnFriendData>();
	}

	public List<PsnFriendData> getFriendList() {
		return list;
	}

	@Override
	public void startElement(final String uri, final String localName,
			final String qName, final Attributes attributes)
			throws SAXException {
		final String qLocal = qLocal(qName, localName);
		calledStartElement = true;
		startTag = qLocal;

	}

	@Override
	public void endElement(final String uri, final String localName,
			final String qName) throws SAXException {
		final String qLocal = qLocal(qName, localName);

		if (qLocal.equalsIgnoreCase(PSN_FRIEND)) {
			list.add(new PsnFriendData.Builder(onlineId)
					.setQuickFriend(isQuickFriend).setCurrentPresence(presence)
					.setCurrentGame(game.equals("null") ? null : game)
					.setCurrentAvatar(avatar)
					.setComment(comment.equals("null") ? null : comment)
					.setPlaystationPlus(plus).setLevel(level)
					.setPlatinum(platinum).setGold(gold).setSilver(silver)
					.setBronze(bronze).build());
		}

	}

	@Override
	public void characters(final char[] ch, final int start, final int length)
			throws SAXException {
		final String qLocal = new String(ch, start, length);

		if (calledStartElement) if (startTag.equalsIgnoreCase(ONLINEID)) onlineId = qLocal;
		else if (startTag.equalsIgnoreCase(QUICK_FRIEND)) isQuickFriend = Boolean
				.parseBoolean(qLocal);
		else if (startTag.equalsIgnoreCase(CURRENT_PRESENCE)) presence = getOnlineStatus(qLocal);
		else if (startTag.equalsIgnoreCase(CURRENT_GAME)) game = qLocal;
		else if (startTag.equalsIgnoreCase(CURRENT_AVATAR)) avatar = qLocal;
		else if (startTag.equalsIgnoreCase(COMMENT)) comment = qLocal;
		else if (startTag.equalsIgnoreCase(PLAYSTATION_PLUS)) plus = Boolean
				.parseBoolean(qLocal);
		else if (startTag.equalsIgnoreCase(LEVEL)) level = Integer
				.parseInt(qLocal);
		else if (startTag.equalsIgnoreCase(PLATINUM)) platinum = Integer
				.parseInt(qLocal);
		else if (startTag.equalsIgnoreCase(GOLD)) gold = Integer
				.parseInt(qLocal);
		else if (startTag.equalsIgnoreCase(SILVER)) silver = Integer
				.parseInt(qLocal);
		else if (startTag.equalsIgnoreCase(BRONZE)) bronze = Integer
				.parseInt(qLocal);

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
