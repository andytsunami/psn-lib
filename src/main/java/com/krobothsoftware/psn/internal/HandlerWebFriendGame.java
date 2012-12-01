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

import org.htmlcleaner.TagNode;

import com.krobothsoftware.commons.parse.HandlerHtml;
import com.krobothsoftware.psn.PsnUtils;
import com.krobothsoftware.psn.model.PsnGameData;

/**
 * 
 * @version 3.0
 * @since Nov 25 2012
 * @author Kyle Kroboth
 */
public final class HandlerWebFriendGame extends HandlerHtml {
	private final List<PsnGameData> psnGameList;
	private final String psnId;

	public HandlerWebFriendGame(final String psnId) {
		psnGameList = new ArrayList<PsnGameData>();
		this.psnId = psnId;
	}

	public List<PsnGameData> getGameList() {
		return psnGameList;
	}

	@Override
	protected void parse(final TagNode rootTagNode) {
		final TagNode gameTableBody = rootTagNode.findElementByAttValue(
				"class", "psnTrophyTable", true, false).findElementByName(
				"tbody", true);
		@SuppressWarnings("unchecked")
		final List<TagNode> gameRows = gameTableBody.getElementListByName("tr",
				false);

		for (int i = 0; i < gameRows.size(); i += 3) {
			final TagNode gameRow1 = gameRows.get(i);
			// TagNode gameRow2 = gameRows.get(i + 1);
			final TagNode gameRow3 = gameRows.get(i + 2);

			final TagNode imageTag = gameRow1.findElementByName("IMG", true);

			String titleLinkId = imageTag.getParent()
					.getAttributeByName("href");
			titleLinkId = titleLinkId.substring(
					titleLinkId.indexOf("/detail/?title=") + 15,
					titleLinkId.indexOf("&friend="));

			String gameImage = imageTag.getAttributeByName("src");
			gameImage = "http://trophy01.np.community.playstation.net/trophy/np/"
					+ gameImage
							.substring(gameImage.indexOf("/trophy/np/") + 11);
			final String npCommid = PsnUtils.getGameIdOf(gameImage);
			final String gameTitle = imageTag.getAttributeByName("alt");

			@SuppressWarnings("unchecked")
			final List<TagNode> td = gameRow3.getElementListByName("td", false);
			final int bronze = Integer.parseInt(td.get(2).getText().toString());
			final int silver = Integer.parseInt(td.get(3).getText().toString());
			final int gold = Integer.parseInt(td.get(4).getText().toString());
			final int platinum = Integer.parseInt(td.get(5).getText()
					.toString());
			final String progressText = td.get(8).getText().toString();
			final int progress = Integer.parseInt(progressText.substring(0,
					progressText.length() - 1));

			psnGameList.add(new PsnGameData.Builder(
					ModelType.UK_FRIEND_VERSION, psnId).setName(gameTitle)
					.setGameImage(gameImage).setProgress(progress)
					.setGameId(npCommid).setPlatinum(platinum).setGold(gold)
					.setSilver(silver).setBronze(bronze)
					.setTitleLinkId(titleLinkId).build());
		}
	}

}
