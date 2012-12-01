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
import java.net.URL;
import java.util.StringTokenizer;

import com.krobothsoftware.commons.network.values.Cookie;
import com.krobothsoftware.commons.util.CommonUtils;

/**
 * Cookie information holder.
 * 
 * 
 * @version 3.0
 * @since Nov 25 2012
 * @author Kyle Kroboth
 */
public class Cookie implements Serializable {
	private static final long serialVersionUID = -7856076968023905033L;
	private final String name;
	private String value;
	private String domain;
	private String path;
	private String expires;
	private boolean isSecure;
	private boolean isHttpOnly;

	public Cookie(final String name, final String value) {
		this.name = name;
		this.value = value;
		domain = null;
		path = null;
		expires = null;
		isSecure = false;
		isHttpOnly = false;
	}

	private Cookie(final Builder builder) {
		name = builder.name;
		value = builder.value;
		domain = builder.domain;
		path = builder.path;
		expires = builder.expires;
		isSecure = builder.isSecure;
		isHttpOnly = builder.isHttpOnly;
	}

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(final String value) {
		this.value = value;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(final String domain) {
		this.domain = domain;
	}

	public String getPath() {
		return path;
	}

	public void setPath(final String path) {
		this.path = path;
	}

	public String getExpires() {
		return expires;
	}

	public void setExpired(final String expires) {
		this.expires = expires;
	}

	public boolean isSecure() {
		return isSecure;
	}

	public void setSecure(final boolean isSecure) {
		this.isSecure = isSecure;
	}

	public boolean isHttpOnly() {
		return isHttpOnly;
	}

	public void setHttpOnly(final boolean isHttpOnly) {
		this.isHttpOnly = isHttpOnly;
	}

	public String getCookieString() {
		return name + "=" + value;
	}

	@Override
	public final String toString() {
		return String.format("%s %s", name, domain);
	}

	@Override
	public final int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (domain == null ? 0 : domain.hashCode());
		result = prime * result + (name == null ? 0 : name.hashCode());
		result = prime * result + (path == null ? 0 : path.hashCode());
		return result;
	}

	@Override
	public final boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof Cookie)) return false;
		final Cookie other = (Cookie) obj;
		if (domain == null) {
			if (other.domain != null) return false;
		} else if (!domain.equals(other.domain)) return false;
		if (name == null) {
			if (other.name != null) return false;
		} else if (!name.equals(other.name)) return false;
		if (path == null) {
			if (other.path != null) return false;
		} else if (!path.equals(other.path)) return false;
		return true;
	}

	/**
	 * Build and parse cookie
	 * 
	 * @param cookieString
	 *            cookie header string
	 * @return built cookie
	 */
	public static Cookie parseCookie(final URL url, final String cookieString) {
		final Cookie.Builder builder = new Cookie.Builder();

		final StringTokenizer tokenizer = new StringTokenizer(cookieString, ";");

		// get name and value
		String token = tokenizer.nextToken();
		token.trim();
		builder.setName(token.substring(0, token.indexOf("=")));
		builder.setValue(token.substring(token.indexOf("=") + 1));

		while (tokenizer.hasMoreTokens()) {
			token = tokenizer.nextToken();
			token = CommonUtils.trim(token);

			// check if its a boolean
			if (token.contains("=")) {
				final String name = token.split("=")[0];
				final String value = token.split("=")[1];

				if (name.equalsIgnoreCase("Domain")) {
					builder.setDomain(value);
				} else if (name.equalsIgnoreCase("Path")) {
					builder.setPath(value);
				} else if (name.equalsIgnoreCase("Expires")) {
					builder.setExpires(value);
				}
			} else {
				if (token.equalsIgnoreCase("Secure")) {
					builder.setSecure(true);
				} else if (token.equalsIgnoreCase("HttpOnly")) {
					builder.setHttpOnly(true);
				}
			}

		}

		if (builder.domain == null) builder.domain = url.getHost();

		if (builder.path == null) builder.path = "/";

		return builder.build();
	}

	public static class Builder {
		private String name;
		private String value;
		private String domain;
		private String path;
		private String expires;
		private boolean isSecure;
		private boolean isHttpOnly;

		public Builder(final String data) {
			name = data.substring(0, data.indexOf("="));
			value = data.substring(data.indexOf("=") + 1, data.length() - 1);
		}

		public Builder() {

		}

		public Builder setName(final String name) {
			this.name = name;
			return this;
		}

		public Builder setValue(final String value) {
			this.value = value;
			return this;
		}

		public Builder setDomain(final String domain) {
			this.domain = domain;
			return this;
		}

		public Builder setPath(final String path) {
			this.path = path;
			return this;
		}

		public Builder setExpires(final String expires) {
			this.expires = expires;
			return this;
		}

		public Builder setSecure(final boolean isSecure) {
			this.isSecure = isSecure;
			return this;
		}

		public Builder setHttpOnly(final boolean isHttpOnly) {
			this.isHttpOnly = isHttpOnly;
			return this;
		}

		public Cookie build() {
			if (domain == null) throw new IllegalStateException(
					"'domain' can not be null");
			return new Cookie(this);
		}
	}

}
