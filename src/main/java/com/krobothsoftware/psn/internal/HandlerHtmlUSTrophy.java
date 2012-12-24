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

import com.krobothsoftware.commons.parse.HandlerHtml;
import com.krobothsoftware.commons.util.CommonUtils;
import com.krobothsoftware.psn.PsnUtils;
import com.krobothsoftware.psn.TrophyType;
import com.krobothsoftware.psn.model.PsnTrophyData;
import com.krobothsoftware.psn.model.PsnTrophyData.Builder;

/**
 * 
 * @version 3.0.2
 * @since Dec 24 2012
 * @author Kyle Kroboth
 */
public final class HandlerHtmlUSTrophy extends HandlerHtml {
	private final List<PsnTrophyData> list;
	private final PsnTrophyData.Builder builder;
	private int responseId = -1;
	private int type = -1;
	private int trophy;

	public HandlerHtmlUSTrophy(final String psnId, final String gameId) {
		list = new ArrayList<PsnTrophyData>();
		builder = (Builder) new PsnTrophyData.Builder(ModelType.US_VERSION,
				psnId).setGameId(gameId);
	}

	public List<PsnTrophyData> getTrophies() {
		return list;
	}

	public int getResponseId() {
		return responseId;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);

		String str;

		if (startTag.equals("span")) {
			type++;
			if (attributes.getValue("class").equals("trophyTypeSortField")) type = 10;
		} else if (startTag.equals("img")) {
			// game image
			str = attributes.getValue("src");
			String alt = attributes.getValue("alt");
			builder.setImage(str);
			if (alt.equals("Earned Trophy")) builder.setGameId(PsnUtils
					.getGameIdOf(str));
			else if (alt.equals("Hidden Trophy")) {
				builder.setName("???").setDescription(null).setDateEarned(null);
			} else
				builder.setDateEarned(null);
		} else if (startTag.equals("script")) type = -2;
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {

	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {

		if (calledStartElement) {
			switch (type) {
			case 0:
				// name
				builder.setName(new String(ch, start, length));
				type++;
				break;
			case 2:
				// desc
				builder.setDescription(new String(ch, start, length));
				type++;
				break;
			case 4:
				// date
				builder.setDateEarned(new String(ch, start, length));
				break;
			case 10:
				// type
				responseId = 0;
				list.add(builder
						.setTrophyType(
								TrophyType.valueOf(CommonUtils.trim(new String(
										ch, start, length))))
						.setIndex(++trophy).build());
				type = -1;
				break;
			case -2:
				// error
				if (new String(ch, start, length).contains("login")) responseId = -2;
			}
		}

		calledStartElement = false;

	}
}
