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
import com.krobothsoftware.psn.model.PsnGameData;

/**
 * 
 * @version 3.0
 * @since Nov 25 2012
 * @author Kyle Kroboth
 */
public final class HandlerWebUSGame extends HandlerHtml {
	private final List<PsnGameData> psnGameList;
	private final String psnId;

	public HandlerWebUSGame(final String psnId) {
		psnGameList = new ArrayList<PsnGameData>();
		this.psnId = psnId;
	}

	public List<PsnGameData> getGames() {
		return psnGameList;
	}

	@Override
	protected void parse(final TagNode rootTagNode) {
		@SuppressWarnings("unchecked")
		final List<TagNode> gameDivs = rootTagNode.getElementListByAttValue(
				"class", "slot", true, false);

		for (final TagNode gameTag : gameDivs) {
			String trophyLinkId = gameTag.findElementByName("A", true)
					.getAttributeByName("HREF");
			trophyLinkId = trophyLinkId.substring(
					trophyLinkId.indexOf("trophies/") + 9,
					trophyLinkId.length());
			final String gameImage = gameTag.findElementByName("IMG", true)
					.getAttributeByName("SRC");
			String npCommid = gameImage.substring(
					gameImage.indexOf("trophy/np/") + 10,
					gameImage.indexOf("_00_"));
			npCommid += "_00";
			final String gameTitle = gameTag
					.findElementByAttValue("class", "gameTitleSortField", true,
							false).getText().toString();
			final int progress = Integer.parseInt(gameTag
					.findElementByAttValue("class", "gameProgressSortField",
							true, false).getText().toString().trim());
			@SuppressWarnings("unchecked")
			final List<TagNode> trophyTags = gameTag.getElementListByAttValue(
					"class", "trophycontent", true, false);
			final int bronze = Integer.parseInt(trophyTags.get(1).getText()
					.toString().trim());
			final int silver = Integer.parseInt(trophyTags.get(2).getText()
					.toString().trim());
			final int gold = Integer.parseInt(trophyTags.get(3).getText()
					.toString().trim());
			final int platinum = Integer.parseInt(trophyTags.get(4).getText()
					.toString().trim());

			psnGameList
					.add(new PsnGameData.Builder(ModelType.US_VERSION, psnId)
							.setName(gameTitle).setGameImage(gameImage)
							.setProgress(progress).setGameId(npCommid)
							.setPlatinum(platinum).setGold(gold)
							.setSilver(silver).setBronze(bronze)
							.setTrophies(platinum + gold + silver + bronze)
							.setTrophyLinkId(trophyLinkId).build());

		}

	}

}
