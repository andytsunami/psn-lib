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

import java.lang.reflect.Field;

import javax.xml.parsers.SAXParser;

import com.krobothsoftware.commons.progress.ProgressHelper;

/**
 * Base Handler for XML data
 * 
 * @version 3.0
 * @since Nov 25 2012
 * @author Kyle Kroboth
 */
public abstract class HandlerXml extends Handler {
	protected SAXParser xmlParser;
	protected String startTag;
	protected boolean calledStartElement;
	private static int SDK_VERSION = -1;

	static {
		// Android version's below 2.1 switches qName and qLocal values,
		// so this is to check if android exist and what version it is
		try {
			final Class<?> clazz = Class.forName("android.os.Build$VERSION");
			final Field field = clazz.getField("SDK_INT");
			SDK_VERSION = field.getInt(null);
		} catch (final ClassNotFoundException e) {

		} catch (final SecurityException e) {

		} catch (final IllegalArgumentException e) {

		} catch (final NoSuchFieldException e) {

		} catch (final IllegalAccessException e) {

		}
	}

	void setParser(final SAXParser xmlParser) {
		this.xmlParser = xmlParser;
	}

	public HandlerXml(final ProgressHelper progressHelper) {
		super(progressHelper);
	}

	public HandlerXml() {

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

}
