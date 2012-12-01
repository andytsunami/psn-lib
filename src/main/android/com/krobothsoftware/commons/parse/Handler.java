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

import org.xml.sax.helpers.DefaultHandler;

import com.krobothsoftware.commons.progress.ProgressHelper;

/**
 * Handler to parse data
 * 
 * @see Parser#parse(java.io.InputStream, Handler, String)
 * 
 * @version 3.0
 * @since Nov 25 2012
 * @author Kyle Kroboth
 */
public abstract class Handler extends DefaultHandler {
	protected ProgressHelper progressHelper;
	protected Parser parser;

	public Handler(final ProgressHelper progressHelper) {
		this.progressHelper = progressHelper;
	}

	public Handler() {

	}

	void setParser(final Parser defaultParser) {
		parser = defaultParser;
	}

}
