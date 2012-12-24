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

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Parser is used to parse XML and HTML data. Xml is parsed by SAX and Html by
 * TagSoup(SAX).
 * 
 * @version 3.0.2
 * @since Nov 25 2012
 * @author Kyle Kroboth
 */
public class Parser {
	final SAXParser xmlParser;
	final SAXParser htmlParser;
	final Logger log;

	public Parser() throws ParserConfigurationException, SAXException {
		log = LoggerFactory.getLogger(Parser.class);
		xmlParser = SAXParserFactory.newInstance().newSAXParser();
		htmlParser = SAXParserFactory.newInstance(
				"org.ccil.cowan.tagsoup.jaxp.SAXFactoryImpl",
				Parser.class.getClassLoader()).newSAXParser();
	}

	/**
	 * Parses inputstream for {@link Handler}
	 * 
	 * @param inputStream
	 *            inputstream to be parsed
	 * @param handler
	 * @param charset
	 * @throws ParseException
	 */
	public void parse(final InputStream inputStream, final Handler handler,
			final String charset) throws ParseException {
		log.debug("Parsing {}", handler.getClass().getSimpleName());
		handler.setParser(this);
		final InputSource inputSource = new InputSource(inputStream);
		inputSource.setEncoding(charset);
		try {
			if (handler instanceof HandlerXml) {
				xmlParser
						.parse(inputStream,
								(handler instanceof ExpressionFilter) ? new ExpressionHandler(
										handler) : handler);
			} else if (handler instanceof HandlerHtml) {
				htmlParser
						.parse(inputSource,
								(handler instanceof ExpressionFilter) ? new ExpressionHandler(
										handler) : handler);
			} else
				throw new ParseException(String.format(
						"Unsupported Handler [%s]", handler.getClass()));
		} catch (SAXException e) {
			if (e instanceof StopSAXException) return;
			throw new ParseException(e.getMessage(), e);
		} catch (IOException e) {
			throw new ParseException(e.getMessage(), e);
		}
	}

}
