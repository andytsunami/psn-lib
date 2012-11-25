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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.htmlcleaner.HtmlCleaner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Parser is used to parse XML and HTML data
 * 
 * @version 3.0
 * @since Nov 25 2012
 * @author Kyle Kroboth
 */
public class Parser {
	final SAXParser xmlParser;
	final HtmlCleaner htmlCleaner;
	final Logger log;

	public Parser() throws ParserConfigurationException, SAXException {
		log = LoggerFactory.getLogger(Parser.class);
		final SAXParserFactory factory = SAXParserFactory.newInstance();
		xmlParser = factory.newSAXParser();
		htmlCleaner = new HtmlCleaner();
	}

	public Logger getLogger() {
		return log;
	}

	/**
	 * Parses inputstream for {@link Handler}
	 * 
	 * @param inputStream
	 *            inputstream to be parsed
	 * @param handler
	 * @param charset
	 * @throws SAXException
	 *             the SAX exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void parse(final InputStream inputStream, final Handler handler,
			final String charset) throws SAXException, IOException {
		log.debug("Parsing {}", handler.getClass().getSimpleName());
		handler.setParser(this);
		if (handler instanceof HandlerXml) {
			final InputSource inputSource = new InputSource(inputStream);
			inputSource.setEncoding(charset);
			xmlParser.parse(inputStream, handler);
		} else if (handler instanceof HandlerHtml) {
			((HandlerHtml) handler).setHtmlCleaner(htmlCleaner);
			((HandlerHtml) handler).parse(htmlCleaner.clean(inputStream,
					charset));
		}
	}

	/**
	 * Parses String content for {@link Handler}
	 * 
	 * @param content
	 *            String content to be parsed
	 * @param handler
	 * @param charset
	 * @throws SAXException
	 *             the SAX exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void parse(final String content, final Handler handler,
			final String charset) throws SAXException, IOException {
		log.debug("Parsing {}", handler.getClass().getSimpleName());
		handler.setParser(this);
		if (handler instanceof HandlerXml) {
			final StringReader stringReader = new StringReader(content);
			final InputSource inputSource = new InputSource(stringReader);
			inputSource.setEncoding(charset);
			xmlParser.parse(inputSource, handler);
			stringReader.close();
		} else if (handler instanceof HandlerHtml) {
			((HandlerHtml) handler).setHtmlCleaner(htmlCleaner);
			((HandlerHtml) handler).parse(htmlCleaner.clean(content));
		}
	}

	/**
	 * Parses file for {@link Handler}
	 * 
	 * @param file
	 *            file to be parsed
	 * @param handler
	 * @throws SAXException
	 *             the SAX exception
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public void parse(final File file, final Handler handler)
			throws SAXException, IOException {
		log.debug("Parsing {}", handler.getClass().getSimpleName());
		handler.setParser(this);
		if (handler instanceof HandlerXml) {
			xmlParser.parse(file, handler);
		} else if (handler instanceof HandlerHtml) {
			((HandlerHtml) handler).setHtmlCleaner(htmlCleaner);
			((HandlerHtml) handler).parse(htmlCleaner.clean(file));
		}
	}

}
