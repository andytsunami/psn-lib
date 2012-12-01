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

package com.krobothsoftware.psn.model;

import java.util.StringTokenizer;

import com.krobothsoftware.psn.client.PlayStationNetworkClient;
import com.krobothsoftware.psn.internal.ModelType;

/**
 * 
 * Holds all personal Info in an US account. This info is from the Playstation
 * Account Settings but gets it from a <code>userinfo</code> cookie on the US
 * Site.
 * 
 * @see PlayStationNetworkClient#getClientUserInfo()
 * 
 * @version 3.0
 * @since Nov 25 2012
 * @author Kyle Kroboth
 */
public class PsnUserInfo extends PsnModel implements PsnId {
	private static final long serialVersionUID = 5112139106790377237L;
	private final String psnId;
	private final int psuId;
	private final String dob;
	private final int age;
	private final String aboutMe;
	private final String email;
	private final String firstName;
	private final String lastName;
	private final String gender;
	private final String location;
	private final String city;
	private final String stateInitial;
	private final String countryInitial;
	private final String avatar;

	private PsnUserInfo(final Builder builder) {
		super(ModelType.US_VERSION);
		psnId = builder.psnId;
		psuId = builder.psuId;
		dob = builder.dob;
		age = builder.age;
		aboutMe = builder.aboutMe;
		email = builder.email;
		firstName = builder.firstName;
		lastName = builder.lastName;
		gender = builder.gender;
		location = builder.location;
		city = builder.city;
		stateInitial = builder.stateInitial;
		countryInitial = builder.countryInitial;
		avatar = builder.avatar;
	}

	@Override
	public String getPsnId() {
		return psnId;
	}

	/**
	 * See <a href="http://www.psu.com/">PSU website</a>
	 * 
	 * @return PlayStation Universe Id
	 */
	public int getPsuId() {
		return psuId;
	}

	/**
	 * 
	 * @return date of birth
	 */
	public String getDob() {
		return dob;
	}

	public int getAge() {
		return age;
	}

	public String getAboutMe() {
		return aboutMe;
	}

	public String getEmail() {
		return email;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getGender() {
		return gender;
	}

	public String getLocation() {
		return location;
	}

	public String getCity() {
		return city;
	}

	public String getStateInitial() {
		return stateInitial;
	}

	public String getCountryInitial() {
		return countryInitial;
	}

	public String getAvatar() {
		return avatar;
	}

	@Override
	public String toString() {
		return String.format("PsnUserInfo [psnId=%s]", psnId);
	}

	/**
	 * Returns a new PsnPersonalInfo Instance.
	 * 
	 * @param userinfo
	 *            the cookie data
	 * @return the personal info
	 */
	public static PsnUserInfo newInstance(final String userinfo) {
		final Builder builder = new Builder();
		final StringTokenizer tokens = new StringTokenizer(userinfo, ",");
		while (tokens.hasMoreTokens()) {
			final String token = tokens.nextToken();
			final String name = token.split("=")[0];
			if ((name + "=").length() == token.length()) continue;
			final String value = token.split("=")[1];

			if (name.equalsIgnoreCase("handle")) builder.setUserId(value);
			else if (name.equalsIgnoreCase("birthDate")) builder.setDob(value);
			else if (name.equalsIgnoreCase("aboutMe")) builder
					.setAboutMe(value);
			else if (name.equalsIgnoreCase("email")) builder.setEmail(value);
			else if (name.equalsIgnoreCase("firstName")) builder
					.setFirstName(value);
			else if (name.equalsIgnoreCase("age")) builder.setAge(Integer
					.parseInt(value));
			else if (name.equalsIgnoreCase("avatar")) builder.setAvatar(value);
			else if (name.equalsIgnoreCase("location")) builder
					.setLocation(value);
			else if (name.equalsIgnoreCase("psuId")) builder.setPsuId(Integer
					.parseInt(value));
			else if (name.equalsIgnoreCase("gender")) builder.setGender(value
					.equals("M") ? "Male" : "Female");
			else if (name.equalsIgnoreCase("lastName")) builder
					.setLastName(value);
			else if (name.equalsIgnoreCase("city")) builder.setCity(value);
			else if (name.equalsIgnoreCase("state")) builder
					.setStateInitial(value);
			else if (name.equalsIgnoreCase("country")) builder
					.setCountryInitial(value);
		}

		return builder.build();
	}

	public static class Builder {
		String psnId;
		int psuId;
		String dob;
		int age;
		String aboutMe;
		String email;
		String firstName;
		String lastName;
		String gender;
		String location;
		String city;
		String stateInitial;
		String countryInitial;
		String avatar;

		public Builder setUserId(final String psnId) {
			this.psnId = psnId;
			return this;
		}

		public Builder setPsuId(final int psuId) {
			this.psuId = psuId;
			return this;
		}

		public Builder setDob(final String dob) {
			this.dob = dob;
			return this;
		}

		public Builder setAge(final int age) {
			this.age = age;
			return this;
		}

		public Builder setAboutMe(final String aboutMe) {
			this.aboutMe = aboutMe;
			return this;
		}

		public Builder setEmail(final String email) {
			this.email = email;
			return this;
		}

		public Builder setFirstName(final String firstName) {
			this.firstName = firstName;
			return this;
		}

		public Builder setLastName(final String lastName) {
			this.lastName = lastName;
			return this;
		}

		public Builder setGender(final String gender) {
			this.gender = gender;
			return this;
		}

		public Builder setLocation(final String location) {
			this.location = location;
			return this;
		}

		public Builder setCity(final String city) {
			this.city = city;
			return this;
		}

		public Builder setStateInitial(final String stateInitial) {
			this.stateInitial = stateInitial;
			return this;
		}

		public Builder setCountryInitial(final String countryInitial) {
			this.countryInitial = countryInitial;
			return this;
		}

		public Builder setAvatar(final String avatar) {
			this.avatar = avatar;
			return this;
		}

		public PsnUserInfo build() {
			return new PsnUserInfo(this);
		}

	}

}
