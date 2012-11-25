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

import java.util.Locale;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.krobothsoftware.commons.parse.HandlerXml;
import com.krobothsoftware.psn.model.PsnProfileData;

/**
 * 
 * @version 3.0
 * @since Nov 25 2012
 * @author Kyle Kroboth
 */
public final class HandlerXmlProfile extends HandlerXml {
	private static final String ONLINE_NAME = "onlinename";
	private static final String COUNTRY = "country";
	private static final String ABOUT_ME = "aboutme";
	private static final String AVATAR = "avatarurl";
	private static final String COLOR = "ucbgp";
	private static final String PLUS = "plusicon";

	private static final String POINT = "point";
	private static final String LEVEL = "level";
	private static final String LEVEL_BASE = "base";
	private static final String LEVEL_NEXT = "next";
	private static final String LEVEL_PROGRESS = "progress";
	private static final String TYPES = "types";
	private static final String TYPES_PLATINUM = "platinum";
	private static final String TYPES_GOLD = "gold";
	private static final String TYPES_SILVER = "silver";
	private static final String TYPES_BRONZE = "bronze";
	private static final String PANEL = "panelurl";
	private static final String PANEL_BGC = "bgc";

	private static final int DEFAULT_COLOR = 0x989898;

	private String id;
	private String avatar;
	private String aboutMe;
	private Locale countryCulture;
	private boolean isPlus;
	private int backgroundColor;
	private String panel;
	private int panelBackgroundColor;

	private int points;
	private int level;
	private int levelFloor;
	private int levelCeiling;
	private int progress;
	private int platinum;
	private int gold;
	private int silver;
	private int bronze;

	public PsnProfileData getProfile() {
		if (id == null) return null;
		if (backgroundColor == 0) {
			// set default
			backgroundColor = DEFAULT_COLOR;
		}

		return new PsnProfileData.Builder(id).setAvatar(avatar)
				.setAboutMe(aboutMe).setCountryCulture(countryCulture)
				.setPlus(isPlus).setBackgroundColor(backgroundColor)
				.setPoints(points).setLevel(level).setLevelFloor(levelFloor)
				.setLevelCeiling(levelCeiling).setProgress(progress)
				.setPlatinum(platinum).setGold(gold).setSilver(silver)
				.setBronze(bronze).setPanel(panel)
				.setPanelBackgroundColor(panelBackgroundColor)
				.setTrophyCount(bronze + silver + gold + platinum).build();

	}

	@Override
	public void startElement(final String uri, final String localName,
			final String qName, final Attributes attributes)
			throws SAXException {

		startTag = qLocal(qName, localName);
		calledStartElement = true;

		if (startTag.equalsIgnoreCase(LEVEL)) {
			levelFloor = Integer.parseInt(attributes.getValue(LEVEL_BASE));
			levelCeiling = Integer.parseInt(attributes.getValue(LEVEL_NEXT));
			progress = Integer.parseInt(attributes.getValue(LEVEL_PROGRESS));
		} else if (startTag.equalsIgnoreCase(TYPES)) {
			platinum = Integer.parseInt(attributes.getValue(TYPES_PLATINUM));
			gold = Integer.parseInt(attributes.getValue(TYPES_GOLD));
			silver = Integer.parseInt(attributes.getValue(TYPES_SILVER));
			bronze = Integer.parseInt(attributes.getValue(TYPES_BRONZE));
		} else if (startTag.equalsIgnoreCase(PANEL)) {
			panelBackgroundColor = Integer.parseInt(
					attributes.getValue(PANEL_BGC), 16);
		}
	}

	@Override
	public void characters(final char[] ch, final int start, final int length)
			throws SAXException {

		if (calledStartElement) {
			if (startTag.equalsIgnoreCase(ONLINE_NAME)) {
				id = new String(ch, start, length);
			} else if (startTag.equalsIgnoreCase(AVATAR)) {
				avatar = new String(ch, start, length);
			} else if (startTag.equalsIgnoreCase(ABOUT_ME)) {
				aboutMe = new String(ch, start, length);
			} else if (startTag.equalsIgnoreCase(COUNTRY)) {
				countryCulture = PsnProfileData.CULTURE_MAP.get(new String(ch,
						start, length).toUpperCase());
			} else if (startTag.equalsIgnoreCase(PLUS)) {
				final String tmp = new String(ch, start, length);
				if (tmp.equals("0")) isPlus = false;
				else
					isPlus = true;
			} else if (startTag.equals(COLOR)) {
				String tmp = new String(ch, start, length);
				tmp = tmp.substring(8, tmp.length() - 2);
				backgroundColor = Integer.parseInt(tmp, 16);
			}

			if (startTag.equalsIgnoreCase(POINT)) {
				points = Integer.parseInt(new String(ch, start, length));
			} else if (startTag.equalsIgnoreCase(LEVEL)) {
				level = Integer.parseInt(new String(ch, start, length));
			} else if (startTag.equalsIgnoreCase(PANEL)) {
				panel = new String(ch, start, length);
			}
		}

		calledStartElement = false;

	}

}
