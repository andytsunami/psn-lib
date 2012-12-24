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
import com.krobothsoftware.psn.PlatformType;
import com.krobothsoftware.psn.model.PsnGameDataOfficial;

/**
 * 
 * @version 3.0.2
 * @since Nov 25 2012
 * @author Kyle Kroboth
 */
public final class HandlerXmlGame extends HandlerXml {
	private static final String TITLE = "title";
	private static final String NPTROPHY = "nptrophy";
	private static final String NPCOMMID = "npcommid";
	private static final String PF = "pf";
	private static final String INFO = "info";
	private static final String TYPES = "types";
	private static final String TYPES_PLATINUM = "platinum";
	private static final String TYPES_GOLD = "gold";
	private static final String TYPES_SILVER = "silver";
	private static final String TYPES_BRONZE = "bronze";
	private static final String LAST_UPDATED = "last-updated";

	private String result;
	private int numberOfGames;

	private List<PsnGameDataOfficial> list;
	private final PsnGameDataOfficial.Builder builder;

	public HandlerXmlGame(final String jid) {
		builder = new PsnGameDataOfficial.Builder(jid);
	}

	public List<PsnGameDataOfficial> getGames() {
		return list != null ? list : new ArrayList<PsnGameDataOfficial>(
				numberOfGames);
	}

	public String getResult() {
		return result;
	}

	@Override
	public void startElement(final String uri, final String localName,
			final String qName, final Attributes attributes)
			throws SAXException {
		super.startElement(uri, localName, qName, attributes);

		if (startTag.equalsIgnoreCase(NPTROPHY)) {
			result = attributes.getValue("result");
		} else if (startTag.equalsIgnoreCase(INFO)) {
			builder.setGameId(attributes.getValue(NPCOMMID));
			builder.setPlatform(PlatformType.getPlatform(attributes
					.getValue(PF)));
		} else if (startTag.equalsIgnoreCase(TYPES)) {
			builder.setPlatinum(Integer.parseInt(attributes
					.getValue(TYPES_PLATINUM)));
			builder.setGold(Integer.parseInt(attributes.getValue(TYPES_GOLD)));
			builder.setSilver(Integer.parseInt(attributes
					.getValue(TYPES_SILVER)));
			builder.setBronze(Integer.parseInt(attributes
					.getValue(TYPES_BRONZE)));
		}

	}

	@Override
	public void endElement(final String uri, final String localName,
			final String qName) throws SAXException {

		if (qLocal(qName, localName).equalsIgnoreCase(INFO)) {

			if (list == null) list = new ArrayList<PsnGameDataOfficial>(
					numberOfGames);
			list.add(builder.build());
		}

	}

	@Override
	public void characters(final char[] ch, final int start, final int length)
			throws SAXException {

		if (calledStartElement) {
			if (startTag.equalsIgnoreCase(TITLE)) numberOfGames = Integer
					.parseInt(new String(ch, start, length));
			else if (startTag.equalsIgnoreCase(LAST_UPDATED)) builder
					.setLastUpdated(new String(ch, start, length));
		}

		calledStartElement = false;

	}

}
