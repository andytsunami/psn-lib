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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.krobothsoftware.commons.parse.HandlerXml;
import com.krobothsoftware.psn.model.PsnProfileData;

/**
 * 
 * @version 3.0.2
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
	private int backgroundColor;

	private final PsnProfileData.Builder builder;

	public HandlerXmlProfile() {
		builder = new PsnProfileData.Builder();
	}

	public PsnProfileData getProfile() {
		if (id == null) return null;
		if (backgroundColor == 0) builder.setBackgroundColor(DEFAULT_COLOR);

		return builder.setBackgroundColor(backgroundColor).setJid(id).build();

	}

	@Override
	public void startElement(final String uri, final String localName,
			final String qName, final Attributes attributes)
			throws SAXException {
		super.startElement(uri, localName, qName, attributes);

		if (startTag.equalsIgnoreCase(LEVEL)) {
			builder.setLevelFloor(Integer.parseInt(attributes
					.getValue(LEVEL_BASE)));
			builder.setLevelCeiling(Integer.parseInt(attributes
					.getValue(LEVEL_NEXT)));
			builder.setProgress(Integer.parseInt(attributes
					.getValue(LEVEL_PROGRESS)));
		} else if (startTag.equalsIgnoreCase(TYPES)) {
			builder.setPlatinum(Integer.parseInt(attributes
					.getValue(TYPES_PLATINUM)));
			builder.setGold(Integer.parseInt(attributes.getValue(TYPES_GOLD)));
			builder.setSilver(Integer.parseInt(attributes
					.getValue(TYPES_SILVER)));
			builder.setBronze(Integer.parseInt(attributes
					.getValue(TYPES_BRONZE)));
		} else if (startTag.equalsIgnoreCase(PANEL)) builder
				.setPanelBackgroundColor(Integer.parseInt(
						attributes.getValue(PANEL_BGC), 16));

	}

	@Override
	public void characters(final char[] ch, final int start, final int length)
			throws SAXException {

		String str;

		if (calledStartElement) {
			if (startTag.equalsIgnoreCase(ONLINE_NAME)) id = new String(ch,
					start, length);
			else if (startTag.equalsIgnoreCase(AVATAR)) builder
					.setAvatar(new String(ch, start, length));
			else if (startTag.equalsIgnoreCase(ABOUT_ME)) builder
					.setAboutMe(new String(ch, start, length));
			else if (startTag.equalsIgnoreCase(COUNTRY)) builder
					.setCountry(PsnProfileData.CULTURE_MAP.get(new String(ch,
							start, length).toUpperCase()));
			else if (startTag.equalsIgnoreCase(PLUS)) builder.setPP(new String(
					ch, start, length).equals("0") ? false : true);
			else if (startTag.equals(COLOR)) {
				str = new String(ch, start, length);
				str = str.substring(8, str.length() - 2);
				backgroundColor = Integer.parseInt(str, 16);
			} else if (startTag.equalsIgnoreCase(POINT)) builder
					.setPoints(Integer.parseInt(new String(ch, start, length)));
			else if (startTag.equalsIgnoreCase(LEVEL)) builder.setLevel(Integer
					.parseInt(new String(ch, start, length)));
			else if (startTag.equalsIgnoreCase(PANEL)) builder
					.setPanel(new String(ch, start, length));

		}

		calledStartElement = false;

	}
}
