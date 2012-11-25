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

import static com.krobothsoftware.psn.TrophyType.BRONZE;
import static com.krobothsoftware.psn.TrophyType.GOLD;
import static com.krobothsoftware.psn.TrophyType.PLATINUM;
import static com.krobothsoftware.psn.TrophyType.SILVER;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.krobothsoftware.commons.parse.HandlerXml;
import com.krobothsoftware.psn.PlatformType;
import com.krobothsoftware.psn.TrophyType;
import com.krobothsoftware.psn.model.PsnTrophyDataOfficial;

/**
 * 
 * @version 3.0
 * @since Nov 25 2012
 * @author Kyle Kroboth
 */
public final class HandlerXmlTrophy extends HandlerXml {

	private static final String INFO = "info";
	private static final String NPTROPHY = "nptrophy";
	private static final String NPCOMMID = "npcommid";
	private static final String PF = "pf";
	private static final String TROPHY = "trophy";
	private static final String TROPHY_ID = "id";
	private static final String TROPHY_TYPE = "type";

	private List<PsnTrophyDataOfficial> psnTrophyList;

	private String result;
	private String gameId;

	private PlatformType pf;
	private int trophyId;
	private String dateEarned;
	private TrophyType trophyType;

	private final String userId;

	public HandlerXmlTrophy(final String userId) {
		this.userId = userId;
	}

	public List<PsnTrophyDataOfficial> getTrophies() {
		return psnTrophyList != null ? psnTrophyList
				: new ArrayList<PsnTrophyDataOfficial>();
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
			gameId = attributes.getValue(NPCOMMID);
			pf = PlatformType.getPlatform(attributes.getValue(PF));
		} else if (startTag.equalsIgnoreCase(TROPHY)) {
			trophyId = Integer.parseInt(attributes.getValue(TROPHY_ID));
			if (attributes.getIndex(NPCOMMID) != -1) gameId = attributes
					.getValue(NPCOMMID);
			switch (Integer.parseInt(attributes.getValue(TROPHY_TYPE))) {
			case 0:
				trophyType = BRONZE;
				break;
			case 1:
				trophyType = SILVER;
				break;
			case 2:
				trophyType = GOLD;
				break;
			case 3:
				trophyType = PLATINUM;
				break;
			}
			if (attributes.getIndex(PF) != -1) pf = PlatformType
					.getPlatform(attributes.getValue(PF));

		}
	}

	@Override
	public void endElement(final String uri, final String localName,
			final String qName) throws SAXException {
		final String qLocal = qLocal(qName, localName);

		if (qLocal.equalsIgnoreCase(TROPHY)) {
			if (psnTrophyList == null) psnTrophyList = new ArrayList<PsnTrophyDataOfficial>();
			psnTrophyList.add(new PsnTrophyDataOfficial.Builder(userId)
					.setPlatform(pf).setTrophyId(trophyId).setGameId(gameId)
					.setDateEarned(dateEarned).setTrophyType(trophyType)
					.build());
		}

	}

	@Override
	public void characters(final char[] ch, final int start, final int length)
			throws SAXException {
		if (calledStartElement) if (startTag.equalsIgnoreCase(TROPHY)) dateEarned = new String(
				ch, start, length);

		calledStartElement = false;

	}

}
