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
import com.krobothsoftware.psn.model.PsnGameData;

/**
 * 
 * @version 3.0.2
 * @since Dec 24 2012
 * @author Kyle Kroboth
 */
public final class HandlerHtmlUSGame extends HandlerHtml {
	private final List<PsnGameData> list;
	private final PsnGameData.Builder builder;
	private int type = -1;

	public HandlerHtmlUSGame(final String psnId) {
		list = new ArrayList<PsnGameData>();
		builder = new PsnGameData.Builder(ModelType.US_VERSION, psnId);
	}

	public List<PsnGameData> getGames() {
		return list;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);

		String str;

		if (startTag.equals("div")
				&& attributes.getValue("class").equals("trophycontent")) type++;
		else if (startTag.equals("span")) type++;
		else if (type == -1 && startTag.equals("a")) {
			// title id
			str = attributes.getValue("href");
			type++;
			builder.setTitleLinkId(str.substring(str.indexOf("trophies/") + 9));
		} else if (type == 0 && startTag.equals("img")) {
			// image, gameId, and title
			str = attributes.getValue("src");
			builder.setImage(str).setName(attributes.getValue("title"))
					.setGameId(PsnUtils.getGameIdOf(str));
			type++;
		}

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
			case 3:
				builder.setProgress(Integer.parseInt(new String(ch, start,
						length)));
				type++;
				break;
			case 7:
				builder.setBronze(Integer.parseInt(CommonUtils.trim(new String(
						ch, start, length))));
				type++;
				break;
			case 9:
				builder.setSilver(Integer.parseInt(CommonUtils.trim(new String(
						ch, start, length))));
				type++;
				break;
			case 11:
				builder.setGold(Integer.parseInt(CommonUtils.trim(new String(
						ch, start, length))));
				type++;
				break;
			case 13:
				list.add(builder.setPlatinum(
						Integer.parseInt(CommonUtils.trim(new String(ch, start,
								length)))).build());
				type = -1;
				break;
			}
		}
		calledStartElement = false;

	}

}
