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
import com.krobothsoftware.psn.PsnUtils;
import com.krobothsoftware.psn.TrophyType;
import com.krobothsoftware.psn.model.PsnTrophyData;

/**
 * 
 * @version 3.0
 * @since Nov 25 2012
 * @author Kyle Kroboth
 */
public final class HandlerWebUSTrophy extends HandlerHtml {
	private boolean getGameId;
	private int responseId;
	private String gameId;
	private String psnId;
	private List<PsnTrophyData> psnTrophyList;

	public HandlerWebUSTrophy(final String psnId, final String gameId) {
		this.psnId = psnId;
		this.gameId = gameId;
	}

	public HandlerWebUSTrophy(final boolean getGameId) {
		this.getGameId = getGameId;
	}

	public String getGameId() {
		return gameId;
	}

	public List<PsnTrophyData> getTrophies() {
		return psnTrophyList;
	}

	public int getResponseId() {
		return responseId;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void parse(final TagNode rootTagNode) {
		if (getGameId) {
			final TagNode progressTag = rootTagNode.findElementByAttValue("id",
					"levelprogress", true, false);
			String imageSrc = progressTag.findElementByName("IMG", true)
					.getAttributeByName("src");
			imageSrc = imageSrc.substring(imageSrc
					.indexOf("/uwps/GetAvtarImage?avtar=") + 26);
			gameId = PsnUtils.getGameIdOf(imageSrc);
			return;
		} else {
			psnTrophyList = new ArrayList<PsnTrophyData>();
			// final List<TagNode> trophyDivs = rootTagNode
			// .findElementByName("iframe", true)
			// .findElementByName("body", true)
			// .getElementListHavingAttribute("class", false);
			final List<TagNode> trophyDivs = rootTagNode.findElementByName(
					"body", true).getElementListByName("div", false);
			if (trophyDivs.isEmpty()) {
				// check if login is required
				TagNode tag = rootTagNode.findElementByName("body", true)
						.findElementByName("script", false);
				if (tag != null
						&& tag.getText().toString().contains("loginWindow")) {
					responseId = -2;
					return;
				} else if ((tag = rootTagNode.findElementByName("body", true)
						.findElementByName("trophyresponse", false)) != null) {
					responseId = Integer.parseInt(tag
							.getAttributeByName("status"));
				} else
					responseId = -1;

				return;
			}
			int trophyId = 0;
			for (final TagNode trophyDiv : trophyDivs) {
				trophyId++;
				boolean trophyHidden = false;
				String trophyDate = null;
				String trophyImage;
				String trophyTitle;
				String trophyDesc;
				TrophyType trophyType = null;
				final TagNode img = trophyDiv.findElementByName("IMG", true);
				// final String imgTitle = img.getAttributeByName("title");
				if (trophyDiv.getAttributeByName("class").equalsIgnoreCase(
						"slot  hiddenTrophy")) trophyHidden = true;

				trophyImage = img.getAttributeByName("src");
				if (trophyDiv.findElementByName("div", false)
						.getAttributeByName("class")
						.contains("showTrophyDetail")) {
					trophyTitle = trophyDiv
							.findElementByAttValue("class",
									"trophyTitleSortField", true, false)
							.getText().toString().trim();
					trophyDesc = trophyDiv
							.findElementByAttValue("class", "subtext", true,
									false).getText().toString().trim();
					final TagNode dateField = trophyDiv.findElementByAttValue(
							"class", "dateEarnedSortField", true, false);
					if (dateField != null) trophyDate = dateField.getText()
							.toString();

					final String trophyTypeField = trophyDiv
							.findElementByAttValue("class",
									"trophyTypeSortField", true, false)
							.getText().toString().trim();

					if (trophyTypeField.equalsIgnoreCase("PLATINUM")) {
						trophyType = PLATINUM;
					} else if (trophyTypeField.equalsIgnoreCase("GOLD")) {
						trophyType = GOLD;
					} else if (trophyTypeField.equalsIgnoreCase("SILVER")) {
						trophyType = SILVER;
					} else if (trophyTypeField.equalsIgnoreCase("BRONZE")) {
						trophyType = BRONZE;
					} else if (trophyTypeField.equalsIgnoreCase("HIDDEN")) trophyType = HIDDEN;
				} else {
					trophyTitle = "???";
					trophyDesc = null;
					trophyDate = null;
					trophyType = HIDDEN;
				}

				psnTrophyList
						.add(new PsnTrophyData.Builder(ModelType.US_VERSION,
								psnId).setName(trophyTitle)
								.setImageUrl(trophyImage)
								.setDescription(trophyDesc)
								.setHidden(trophyHidden).setTrophyId(trophyId)
								.setGameId(gameId).setDateEarned(trophyDate)
								.setTrophyType(trophyType)
								.setReceived(trophyDate == null ? false : true)
								.build());

			}

		}

	}
}
