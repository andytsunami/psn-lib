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

package com.krobothsoftware.commons.network.values;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * The Class NameValuePair for holding all name-value combinations.
 * 
 * @version 3.0
 * @since Nov 25 2012
 * @author Kyle Kroboth
 */
public class NameValuePair implements Serializable {
	private static final long serialVersionUID = -1049649389075000405L;
	private final String name;
	private final String value;

	public NameValuePair(final String name, final String value) {
		this.name = name;
		this.value = value;
	}

	public String getPair() {
		return name + "=" + value;
	}

	/**
	 * Gets the encoded pair using UTF-8.
	 * 
	 * @return encoded pair
	 */
	public String getEncodedPair() {
		return getEncodedPair("UTF-8");
	}

	/**
	 * Gets the encoded pair from specified charset
	 * 
	 * @param charSet
	 *            for encoding
	 * @return encoded pair
	 */
	public String getEncodedPair(final String charSet) {
		try {
			return name + "=" + URLEncoder.encode(value, charSet);
		} catch (final UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Gets pair where value is in quotes
	 * 
	 * @return quoted pair
	 */
	public String getQuotedPair() {
		return name + "=\"" + value + "\"";
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	@Override
	public final String toString() {
		return "NameValuePair [name=" + name + ", value=" + value + "]";
	}

	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (name == null ? 0 : name.hashCode());
		result = prime * result + (value == null ? 0 : value.hashCode());
		return result;
	}

	@Override
	public final boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof NameValuePair)) return false;
		final NameValuePair other = (NameValuePair) obj;
		if (name == null) {
			if (other.name != null) return false;
		} else if (!name.equals(other.name)) return false;
		if (value == null) {
			if (other.value != null) return false;
		} else if (!value.equals(other.value)) return false;
		return true;
	}

}
