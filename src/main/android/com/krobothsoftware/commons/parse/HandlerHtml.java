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

package com.krobothsoftware.commons.parse;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import com.krobothsoftware.commons.progress.ProgressHelper;

/**
 * Base Handler for HTML data
 * 
 * @version 3.0
 * @since Nov 25 2012
 * @author Kyle Kroboth
 */
public abstract class HandlerHtml extends Handler {
	protected HtmlCleaner cleaner;

	public HandlerHtml(final ProgressHelper progressHelper) {
		super(progressHelper);
	}

	public HandlerHtml() {

	}

	protected abstract void parse(TagNode rootTagNode);

	void setHtmlCleaner(final HtmlCleaner cleaner) {
		this.cleaner = cleaner;
	}

}
