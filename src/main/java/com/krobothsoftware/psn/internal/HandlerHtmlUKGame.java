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

import com.krobothsoftware.commons.parse.Expression;
import com.krobothsoftware.commons.parse.ExpressionFilter;
import com.krobothsoftware.commons.parse.HandlerHtml;
import com.krobothsoftware.commons.parse.StopSAXException;
import com.krobothsoftware.psn.PsnUtils;
import com.krobothsoftware.psn.model.PsnGameData;

/**
 * 
 * @version 3.0.2
 * @since Dec 24 2012
 * @author Kyle Kroboth
 */
public final class HandlerHtmlUKGame extends HandlerHtml implements
		ExpressionFilter {
	private static final Expression expr = Expression
			.parse("/table[@class='psnTrophyTable']/tbody");
	private final List<PsnGameData> list;
	private final PsnGameData.Builder builder;
	private int td = -1;

	public HandlerHtmlUKGame(final String psnId) {
		list = new ArrayList<PsnGameData>();
		builder = new PsnGameData.Builder(ModelType.UK_VERSION, psnId);
	}

	public List<PsnGameData> getGameList() {
		return list;
	}

	@Override
	public Expression getExpression() {
		return expr;
	}

	@Override
	public void startElement(int expr, String uri, String localName,
			String qName, Attributes attributes) throws SAXException {

		String str;

		if (startTag.equals("td")) td++;
		switch (td) {
		case 0:
			if (startTag.equals("a")) {
				builder.setTitleLinkId((str = attributes.getValue("href"))
						.substring(str.indexOf("/detail/?title=") + 15));
			} else if (startTag.equals("img")) {
				str = attributes.getValue("src");
				builder.setName(attributes.getValue("alt"))
						.setImage(
								"http://trophy01.np.community.playstation.net/trophy/np/"
										+ str.substring(str
												.indexOf("/trophy/np/") + 11))
						.setGameId(PsnUtils.getGameIdOf(str));
			}
			break;
		case 2:

		}

	}

	@Override
	public void characters(int expr, char[] ch, int start, int length)
			throws SAXException {

		if (calledStartElement) {
			switch (td) {
			case 2:
				builder.setBronze(Integer
						.parseInt(new String(ch, start, length)));
				break;
			case 3:
				builder.setSilver(Integer
						.parseInt(new String(ch, start, length)));
				break;
			case 4:
				builder.setGold(Integer.parseInt(new String(ch, start, length)));
				break;
			case 5:
				builder.setPlatinum(Integer.parseInt(new String(ch, start,
						length)));
				break;
			case 8:
				String str = new String(ch, start, length);
				list.add(builder.setProgress(
						Integer.parseInt(str.substring(0, str.length() - 1)))
						.build());
				td = -1;
				break;
			}
		}

		calledStartElement = false;

	}

	@Override
	public boolean endElement(int expr, String uri, String localName,
			String qName) throws SAXException {
		if (qLocal(qName, localName).equals("table")) throw new StopSAXException();

		return true;
	}
}
