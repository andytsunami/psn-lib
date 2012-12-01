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
public final class HandlerWebUKGame extends HandlerHtml {
	private final List<PsnGameData> psnGameList;
	private final String psnId;

	public HandlerWebUKGame(final String psnId) {
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

		for (final TagNode gameRow : gameRows) {
			@SuppressWarnings("unchecked")
			final List<TagNode> gameData = gameRow.getElementListByName("td",
					false);
			final TagNode imgData = gameData.get(0).findElementByName("A",
					false);
			String titleLinkId = imgData.getAttributeByName("href");
			titleLinkId = titleLinkId.substring(titleLinkId
					.indexOf("/detail/?title=") + 15);
			String gameImage = imgData.findElementByName("IMG", false)
					.getAttributeByName("src");
			gameImage = "http://trophy01.np.community.playstation.net/trophy/np/"
					+ gameImage
							.substring(gameImage.indexOf("/trophy/np/") + 11);
			String npCommid = PsnUtils.getGameIdOf(gameImage);
			final String gameTitle = gameData.get(1)
					.findElementByName("STRONG", true).getText().toString();
			final int bronze = Integer.parseInt(gameData.get(2).getText()
					.toString());
			final int silver = Integer.parseInt(gameData.get(3).getText()
					.toString());
			final int gold = Integer.parseInt(gameData.get(4).getText()
					.toString());
			final int platinum = Integer.parseInt(gameData.get(5).getText()
					.toString());
			final String progressText = gameData.get(8).getText().toString();
			final int progress = Integer.parseInt(progressText.substring(0,
					progressText.length() - 1));
			psnGameList
					.add(new PsnGameData.Builder(ModelType.UK_VERSION, psnId)
							.setName(gameTitle).setGameImage(gameImage)
							.setProgress(progress).setGameId(npCommid)
							.setPlatinum(platinum).setGold(gold)
							.setSilver(silver).setBronze(bronze)
							.setTitleLinkId(titleLinkId).build());
		}

	}
}
