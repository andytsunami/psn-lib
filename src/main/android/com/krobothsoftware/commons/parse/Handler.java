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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import android.os.Build;

import com.krobothsoftware.commons.progress.ProgressHelper;

/**
 * Handler to parse data
 * 
 * @see Parser#parse(java.io.InputStream, Handler, String)
 * 
 * @version 3.0.2
 * @since Nov 25 2012
 * @author Kyle Kroboth
 */
public abstract class Handler extends DefaultHandler {
	protected ProgressHelper progressHelper;
	protected Parser parser;
	protected String startTag;
	protected boolean calledStartElement;
	private static int SDK_VERSION = -1;

	public Handler(final ProgressHelper progressHelper) {
		this.progressHelper = progressHelper;
	}

	public Handler() {

	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		startTag = qLocal(qName, localName);
		calledStartElement = true;
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		calledStartElement = false;
	}

	@Override
	public void error(SAXParseException e) throws SAXException {
		parser.log.error("Handler error [{}]", e.toString());
	}

	@Override
	public void warning(SAXParseException e) throws SAXException {
		parser.log.warn("Handler warning [{}]", e.toString());
	}

	@Override
	public void fatalError(SAXParseException e) throws SAXException {
		parser.log.error("Handler warning [{}]", e.toString());
	}

	void setParser(final Parser defaultParser) {
		parser = defaultParser;
	}

	/**
	 * Gets correct qlocal from XML Handler
	 * 
	 * @param qName
	 *            qname
	 * @param localname
	 *            localname
	 * @return correct qLocal
	 */
	protected final String qLocal(final String qName, final String localname) {
		String retValue = "";

		if (SDK_VERSION != -1) {

			if (SDK_VERSION <= 7) retValue = localname;
			else
				retValue = qName;
		} else
			retValue = qName;
		return retValue;
	}

	static {
		// Android version's below 2.1 switches qName and qLocal values,
		// so this is to check if android exist and what version it is
		SDK_VERSION = Build.VERSION.SDK_INT;
	}

}
