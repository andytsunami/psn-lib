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
 * @version 3.0
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
	private String npCommid;
	private PlatformType pf;
	private int platinum;
	private int gold;
	private int silver;
	private int bronze;
	private String lastUpdated;
	private int numberOfGames;

	private List<PsnGameDataOfficial> psnGameList;
	private final String userId;

	public HandlerXmlGame(final String userId) {
		this.userId = userId;
	}

	public List<PsnGameDataOfficial> getGames() {
		return psnGameList != null ? psnGameList
				: new ArrayList<PsnGameDataOfficial>(numberOfGames);
	}

	public String getResult() {
		return result;
	}

	@Override
	public void startElement(final String uri, final String localName,
			final String qName, final Attributes attributes)
			throws SAXException {
		startTag = qLocal(qName, localName);
		calledStartElement = true;

		if (startTag.equalsIgnoreCase(NPTROPHY)) {
			result = attributes.getValue("result");
		} else if (startTag.equalsIgnoreCase(INFO)) {
			npCommid = attributes.getValue(NPCOMMID);
			pf = PlatformType.getPlatform(attributes.getValue(PF));
		} else if (startTag.equalsIgnoreCase(TYPES)) {
			platinum = Integer.parseInt(attributes.getValue(TYPES_PLATINUM));
			gold = Integer.parseInt(attributes.getValue(TYPES_GOLD));
			silver = Integer.parseInt(attributes.getValue(TYPES_SILVER));
			bronze = Integer.parseInt(attributes.getValue(TYPES_BRONZE));
		}

	}

	@Override
	public void endElement(final String uri, final String localName,
			final String qName) throws SAXException {
		final String qLocal = qLocal(qName, localName);

		if (qLocal.equalsIgnoreCase(INFO)) {

			if (psnGameList == null) psnGameList = new ArrayList<PsnGameDataOfficial>(
					numberOfGames);

			psnGameList.add(new PsnGameDataOfficial.Builder(userId)
					.setPlatform(pf).setLastUpdated(lastUpdated)
					.setGameId(npCommid).setPlatinum(platinum).setGold(gold)
					.setSilver(silver).setBronze(bronze)
					.setTrophies(platinum + gold + silver + bronze).build());
		}

	}

	@Override
	public void characters(final char[] ch, final int start, final int length)
			throws SAXException {

		if (calledStartElement) if (startTag.equalsIgnoreCase(TITLE)) numberOfGames = Integer
				.parseInt(new String(ch, start, length));
		else if (startTag.equalsIgnoreCase(LAST_UPDATED)) lastUpdated = new String(
				ch, start, length);

		calledStartElement = false;

	}

}
