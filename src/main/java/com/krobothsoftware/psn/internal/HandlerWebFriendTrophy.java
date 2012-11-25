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
import static com.krobothsoftware.psn.TrophyType.HIDDEN;
import static com.krobothsoftware.psn.TrophyType.PLATINUM;
import static com.krobothsoftware.psn.TrophyType.SILVER;

import java.util.ArrayList;
import java.util.List;

import org.htmlcleaner.TagNode;

import com.krobothsoftware.commons.parse.HandlerHtml;
import com.krobothsoftware.psn.TrophyType;
import com.krobothsoftware.psn.model.PsnTrophyData;

/**
 * 
 * @version 3.0
 * @since Nov 25 2012
 * @author Kyle Kroboth
 */
public final class HandlerWebFriendTrophy extends HandlerHtml {
	private List<PsnTrophyData> psnTrophyList;
	private final String psnId;

	public HandlerWebFriendTrophy(final String psnId) {
		this.psnId = psnId;
	}

	public List<PsnTrophyData> getTrophyList() {
		return psnTrophyList;
	}

	@Override
	protected void parse(final TagNode rootTagNode) {
		// get game Id
		String gameId = rootTagNode
				.findElementByAttValue("class", "gameLogoImage", true, false)
				.findElementByName("IMG", false).getAttributeByName("src");
		gameId = gameId.substring(gameId.indexOf("trophy/np/") + 10,
				gameId.indexOf("_00_"));
		gameId += "_00";

		// get trophy details
		// @formatter:off
		/*
		@SuppressWarnings("unchecked")
		final List<TagNode> trophyDetails = rootTagNode
				.findElementByAttValue("class", "psnTrophyTable", true, false)
				.findElementByName("tbody", false)
				.getElementsByName("tr", false)[1].getElementsByName("td",
				false)[2].findElementByName("table", false)
				.findElementByName("tbody", false)
				.findElementByName("tr", false)
				.getElementListByName("td", false);

		final int bronze = Integer.parseInt(trophyDetails.get(0).getText()
				.toString());
		final int silver = Integer.parseInt(trophyDetails.get(1).getText()
				.toString());
		final int gold = Integer.parseInt(trophyDetails.get(2).getText()
				.toString());
		final int platinum = Integer.parseInt(trophyDetails.get(3).getText()
				.toString());
		*/
		// @formatter:on

		final TagNode gamelevelListingContainer = rootTagNode
				.findElementByAttValue("class", "gamelevelListingContainer",
						true, false);

		@SuppressWarnings("unchecked")
		final List<TagNode> trophyItems = gamelevelListingContainer
				.getElementListByAttValue("class", "gameLevelListRow", false,
						false);

		int trophyId = 0;
		for (final TagNode trophyItem : trophyItems) {
			trophyId++;
			boolean trophyHidden = false;
			String trophyDate = null;
			String trophyImage;
			String trophyTitle;
			String trophyDesc;
			TrophyType trophyType = null;
			trophyImage = trophyItem
					.findElementByAttValue("class", "gameLevelImage", true,
							false).findElementByName("IMG", false)
					.getAttributeByName("src");

			// check if locked
			if (trophyImage.contains("/portal/common/icon_trophy_default.gif")) trophyImage = "http://webassets.scea.com/playstation/img/trophy_locksmall.png";
			else
				trophyImage = "http://trophy01.np.community.playstation.net/trophy/np/"
						+ trophyImage.substring(trophyImage
								.indexOf("/trophy/np/") + 11);

			final TagNode gameLevelInfo = trophyItem.findElementByAttValue(
					"class", "gameLevelInfo", true, false);

			final String trophyTypeField = gameLevelInfo.findElementByName(
					"IMG", true).getAttributeByName("alt");

			if (trophyTypeField.equalsIgnoreCase("Platinum")) trophyType = PLATINUM;
			else if (trophyTypeField.equalsIgnoreCase("Gold")) trophyType = GOLD;
			else if (trophyTypeField.equalsIgnoreCase("Silver")) trophyType = SILVER;
			else if (trophyTypeField.equalsIgnoreCase("Bronze")) trophyType = BRONZE;
			else if (trophyTypeField.equalsIgnoreCase("Hidden")) {
				trophyType = HIDDEN;
				trophyHidden = true;
			}

			@SuppressWarnings("unchecked")
			final List<TagNode> levelDetails = trophyItem
					.findElementByAttValue("class", "gameLevelDetails", true,
							false).getElementListByName("p", false);

			if (levelDetails.size() == 3) {
				trophyTitle = levelDetails.get(0).getText().toString();
				trophyDate = levelDetails.get(1).getText().toString();
				trophyDate = trophyDate.substring(9);
				trophyDesc = levelDetails.get(2).getText().toString();
			} else {
				trophyTitle = levelDetails.get(0).getText().toString();
				trophyDesc = levelDetails.get(1).getText().toString();
			}

			if (psnTrophyList == null) psnTrophyList = new ArrayList<PsnTrophyData>();

			psnTrophyList.add(new PsnTrophyData.Builder(
					ModelType.UK_FRIEND_VERSION, psnId).setName(trophyTitle)
					.setImageUrl(trophyImage).setDescription(trophyDesc)
					.setHidden(trophyHidden).setTrophyId(trophyId)
					.setGameId(gameId).setDateEarned(trophyDate)
					.setTrophyType(trophyType)
					.setReceived(trophyDate == null ? false : true).build());

		}

	}

}
