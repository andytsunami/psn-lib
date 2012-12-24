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
import org.xml.sax.helpers.DefaultHandler;

/**
 * Filter applied to {@link Handler} for {@link Expression} evaluation.
 * 
 * @version 3.0.2
 * @since Dec 24 2012
 * @author Kyle Kroboth
 */
public interface ExpressionFilter {

	/**
	 * Gets expression to evaluate.
	 * 
	 * @return expression
	 */
	Expression getExpression();

	/**
	 * Called when the expression's path is reached. If there is more than one
	 * path, <code>expr</code> will represent the path's index.
	 * 
	 * @see DefaultHandler#startElement(String, String, String, Attributes)
	 * 
	 * @param expr
	 *            current path; starts at 0
	 * @param uri
	 *            the uri
	 * @param localName
	 * @param qName
	 * @param attributes
	 * @throws SAXException
	 */
	void startElement(int expr, String uri, String localName, String qName,
			Attributes attributes) throws SAXException;

	/**
	 * Called when the expression's path is reached. If there is more than one
	 * path, <code>expr</code> will represent the path's index.
	 * 
	 * @see DefaultHandler#endElement(String, String, String)
	 * 
	 * @param expr
	 *            current path; starts at 0
	 * 
	 * @param expr
	 *             current path; starts at 0
	 * @param uri
	 * @param localName
	 * @param qName
	 * @return
	 * @throws SAXException
	 * 
	 */
	boolean endElement(int expr, String uri, String localName, String qName)
			throws SAXException;

	/**
	 * Called when the expression's path is reached. If there is more than one
	 * path, <code>expr</code> will represent the path's index.
	 * 
	 * @see DefaultHandler#characters(char[], int, int)
	 * 
	 * @param expr
	 *            current path; starts at 0
	 * @param ch
	 * @param start
	 * @param length
	 * @throws SAXException
	 * 
	 */
	void characters(int expr, char[] ch, int start, int length)
			throws SAXException;

}
