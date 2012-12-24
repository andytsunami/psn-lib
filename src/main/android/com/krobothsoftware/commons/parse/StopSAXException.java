package com.krobothsoftware.commons.parse;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 
 * SAX Exception for stopping parsing in {@link DefaultHandler} which is caught
 * internally.
 * 
 * @version 3.0.2
 * @since Dec 24 2012
 * @author Kyle Kroboth
 */
public final class StopSAXException extends SAXException {
	private static final long serialVersionUID = 5964448482034581273L;

}
