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

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import com.krobothsoftware.commons.parse.Expression.Node;

/**
 * 
 * @version 3.0.2
 * @since Dec 24 2012
 * @author Kyle Kroboth
 */
class ExpressionHandler extends DefaultHandler {
	private Handler delegate;
	private ExpressionFilter filter;
	private Expression expr;
	private int index = 0;
	private boolean cont;
	private Node node;
	private boolean reached;
	private boolean hasAttrib;
	private boolean hasAttribV;
	private int count = 1;

	public ExpressionHandler(Handler delegate) {
		this.delegate = delegate;
		this.filter = (ExpressionFilter) delegate;
		expr = filter.getExpression();
		expr.reset();
		popNode();
	}

	@Override
	public InputSource resolveEntity(String publicId, String systemId)
			throws IOException, SAXException {
		return delegate.resolveEntity(publicId, systemId);
	}

	@Override
	public void notationDecl(String name, String publicId, String systemId)
			throws SAXException {
		delegate.notationDecl(name, publicId, systemId);
	}

	@Override
	public void unparsedEntityDecl(String name, String publicId,
			String systemId, String notationName) throws SAXException {
		delegate.unparsedEntityDecl(name, publicId, systemId, notationName);
	}

	@Override
	public void setDocumentLocator(Locator locator) {
		delegate.setDocumentLocator(locator);
	}

	@Override
	public void startDocument() throws SAXException {
		delegate.startDocument();
	}

	@Override
	public void endDocument() throws SAXException {
		delegate.endDocument();
	}

	@Override
	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
		delegate.startPrefixMapping(prefix, uri);
	}

	@Override
	public void endPrefixMapping(String prefix) throws SAXException {
		delegate.endPrefixMapping(prefix);
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		delegate.startElement(uri, localName, qName, attributes);
		if (reached) {
			if (node != null && cont) {
				popNode();
				index++;
				reached = false;
				cont = false;
			} else
				filter.startElement(index, uri, localName, qName, attributes);
			return;
		}
		if (!node.tag.equals(delegate.startTag)) return;
		if (hasAttrib) {
			int index = attributes.getIndex(node.attrib);
			if (index == -1) return;
			if (hasAttribV && !attributes.getValue(index).equals(node.attribV)) return;
		}
		if (count++ != node.index) return;
		popNode();
		if (reached) filter.startElement(index, uri, localName, qName,
				attributes);

	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		delegate.endElement(uri, localName, qName);
		if (reached) cont = filter.endElement(index, uri, localName, qName);
	}

	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (reached) filter.characters(index, ch, start, length);
		delegate.characters(ch, start, length);
	}

	@Override
	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
		delegate.ignorableWhitespace(ch, start, length);
	}

	@Override
	public void processingInstruction(String target, String data)
			throws SAXException {
		delegate.processingInstruction(target, data);
	}

	@Override
	public void skippedEntity(String name) throws SAXException {
		delegate.skippedEntity(name);
	}

	@Override
	public void warning(SAXParseException e) throws SAXException {
		delegate.warning(e);
	}

	@Override
	public void error(SAXParseException e) throws SAXException {
		delegate.error(e);
	}

	@Override
	public void fatalError(SAXParseException e) throws SAXException {
		delegate.fatalError(e);
	}

	private void popNode() {
		count = 1;
		node = expr.popNode();
		if (node == null) {
			reached = true;
			return;
		} else if (node.tag.equals("&&")) {
			cont = false;
			reached = true;
			return;
		}
		hasAttrib = node.attrib != null;
		hasAttribV = node.attribV != null;
	}

}
