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

package com.krobothsoftware.psn.client;

import static com.krobothsoftware.commons.network.NetworkHelper.Method.GET;
import static com.krobothsoftware.commons.network.NetworkHelper.Method.POST;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.krobothsoftware.commons.network.CookieManager;
import com.krobothsoftware.commons.network.NetworkHelper;
import com.krobothsoftware.commons.network.NetworkHelper.Method;
import com.krobothsoftware.commons.network.Response;
import com.krobothsoftware.commons.network.authorization.AuthorizationManager;
import com.krobothsoftware.commons.network.authorization.DigestAuthorization;
import com.krobothsoftware.commons.network.values.Cookie;
import com.krobothsoftware.commons.network.values.NameValuePair;
import com.krobothsoftware.commons.parse.Parser;
import com.krobothsoftware.commons.progress.ProgressHelper;
import com.krobothsoftware.commons.progress.ProgressListener;
import com.krobothsoftware.commons.util.CommonUtils;
import com.krobothsoftware.psn.PlatformType;
import com.krobothsoftware.psn.PlayStationNetworkException;
import com.krobothsoftware.psn.PlayStationNetworkLoginException;
import com.krobothsoftware.psn.PsnUtils;
import com.krobothsoftware.psn.internal.HandlerWebFriendGame;
import com.krobothsoftware.psn.internal.HandlerWebFriendTrophy;
import com.krobothsoftware.psn.internal.HandlerWebUKGame;
import com.krobothsoftware.psn.internal.HandlerWebUKTrophy;
import com.krobothsoftware.psn.internal.HandlerWebUSGame;
import com.krobothsoftware.psn.internal.HandlerWebUSTrophy;
import com.krobothsoftware.psn.internal.HandlerXmlFriend;
import com.krobothsoftware.psn.internal.HandlerXmlGame;
import com.krobothsoftware.psn.internal.HandlerXmlProfile;
import com.krobothsoftware.psn.internal.HandlerXmlTrophy;
import com.krobothsoftware.psn.model.PsnFriendData;
import com.krobothsoftware.psn.model.PsnGameData;
import com.krobothsoftware.psn.model.PsnGameDataOfficial;
import com.krobothsoftware.psn.model.PsnProfileData;
import com.krobothsoftware.psn.model.PsnTrophyData;
import com.krobothsoftware.psn.model.PsnTrophyDataOfficial;
import com.krobothsoftware.psn.model.PsnUserInfo;

/**
 * The PlayStationNetwork Client is used for all psn network related methods and
 * info. There are three main types of data being retrieved,
 * 
 * <ul>
 * <li>Client - prefix for all methods that retrieved data from <a href=
 * "http://uk.playstation.com/" >UK Site.</a></li>
 * <li>Public - prefix for all methods that retrieve data from <a href=
 * "http://us.playstation.com/" >US Site</a></li>
 * <li>Official - prefix for all methods that retrieved data from Sony's
 * official servers</li>
 * </ul>
 * 
 * @version 3.0
 * @since Nov 25 2012
 * @author Kyle Kroboth
 * 
 */
public class PlayStationNetworkClient {

	/** Agent used for getting profile and jid data in <i>Official</i> methods. */
	public static final String AGENT_PS3_COMMUNITY = "PS3Community-agent/1.0.0 libhttp/1.0.0";

	/** Agent used for getting trophy and game data in <i>Official</i> methods. */
	public static final String AGENT_PS3_APPLICATION = "PS3Application libhttp/3.5.5-000 (CellOS)";

	/** PS3 Update Agent. */
	public static final String AGENT_PS3_UPDATE = "PS3Update-agent/1.0.0 libhttp/1.0.0";

	/** Agent used in the console's browser. */
	public static final String AGENT_PS3_BROWSER = "Mozilla/5.0 (PLAYSTATION 3; 1.00)";

	/**
	 * Seems to be the agent used for checking for game and app updates. Don't
	 * know for sure.
	 */
	public static final String AGENT_PS3_HTTPCLIENT = "Sony-HTTPClient/1.0 [PS3 test]";

	/** Vita's main user agent. */
	public static final String AGENT_VITA_LIBHTTP = "libhttp/1.66 (PS Vita)";

	/** Agent used for vita's browser. */
	public static final String AGENT_VITA_BROWSER = "Mozilla/5.0 (Playstation Vita 1.50) AppleWebKit/531.22.8 (KHTML, like Gecko)﻿ Silk/3.2﻿";

	/** Agent used for PSP's browser. */
	public static final String AGENT_PSP_BROWSER = "Mozilla/4.0 (PSP (PlayStation Portable); 2.00)";

	/** PSP Update Agent. */
	public static final String AGENT_PSP_UPDATE = "PSPUpdate-agent/1.0.0 libhttp/1.0.0";

	/** Current PS3 firmware version as of 12/23/2012 */
	public static String PS3_FIRMWARE_VERSION = "4.31";

	private String clientJid;
	private String clientSessionId;
	private boolean clientLoggedIn;
	private PsnUserInfo clientUserInfo;
	private final NetworkHelper networkHelper;
	private final Parser parser;
	private final Logger log;

	/**
	 * Creates new client. Call {@link #init()} to initialize the client for
	 * use.
	 */
	public PlayStationNetworkClient() {
		log = LoggerFactory.getLogger(PlayStationNetworkClient.class);
		networkHelper = new NetworkHelper();
		parser = _getParser();

	}

	private Parser _getParser() {
		try {
			return new Parser();
		} catch (final ParserConfigurationException e) {
			log.error("PlaystationNetwork failed to intialize", e);
		} catch (final SAXException e) {
			log.error("PlaystationNetwork failed to initialize", e);
		}

		throw new RuntimeException("Parser is null");
	}

	/**
	 * Initialize the client and sets up authorizations.
	 */
	public void init() {
		log.info("Initializing client");
		try {
			final AuthorizationManager authManager = networkHelper
					.getAuthorizationManager();
			authManager.addAuthorization(new URL(
					"http://searchjid.usa.np.community.playstation.net"),
					new DigestAuthorization("c7y-basic01",
							"A9QTbosh0W0D^{7467l-n_>2Y%JG^v>o"));
			authManager.addAuthorization(new URL(
					"http://getprof.us.np.community.playstation.net"),
					new DigestAuthorization("c7y-basic01",
							"A9QTbosh0W0D^{7467l-n_>2Y%JG^v>o"));
			authManager.addAuthorization(new URL(
					"http://trophy.ww.np.community.playstation.net"),
					new DigestAuthorization("c7y-trophy01",
							"jhlWmT0|:0!nC:b:#x/uihx'Y74b5Ycx"));
		} catch (final MalformedURLException e) {
			log.error("PlayStationNetworkClient failed to init", e);
		}
	}

	/**
	 * Cleans up and resets client and network helper.
	 */
	public void cleanup() {
		log.info("Cleaning up client");
		clientSessionId = null;
		clientJid = null;
		clientLoggedIn = false;
		networkHelper.reset();
	}

	public NetworkHelper getNetworkHelper() {
		return networkHelper;
	}

	public Parser getParser() {
		return parser;
	}

	/**
	 * Must call {@link #clientLogin(ProgressListener, String, String)}
	 * beforehand.
	 * 
	 * @return client jid
	 */
	public String getClientJid() {
		return clientJid;
	}

	/**
	 * Must call {@link #clientLogin(ProgressListener, String, String)}
	 * beforehand.
	 * 
	 * @return client sessionId
	 */
	public String getClientSessionId() {
		return clientSessionId;
	}

	/**
	 * Must call {@link #clientLogin(ProgressListener, String, String)}
	 * beforehand.
	 * 
	 * @return user info
	 */
	public PsnUserInfo getUserInfo() {
		return clientUserInfo;
	}

	/**
	 * Checks if is client logged in.
	 * 
	 * @return true, if is client logged in
	 */
	public boolean isClientLoggedIn() {
		return clientLoggedIn;
	}

	/**
	 * Logs into account from credentials. First logs into UK account and uses
	 * the data in return to quietly log into US account.
	 * <p>
	 * Following data is stored
	 * </p>
	 * 
	 * <pre>
	 *  <ul>
	 *  <li>UK Cookies
	 *       - PDC4_COOKIE (login cookie)
	 *       - s_token (login cookie)
	 *  </li>
	 *  <li>US Cookies
	 *       - userinfo (contains {@link PsnUserInfo} data)
	 *       - TICKET (login cookie)
	 *       - PSNS2STICKET (login cookie)
	 * </li>
	 *  <li>PsnId (returned in method)</li>
	 *  <li>Jid ({@link #getOfficialJid(String)} used throughout the whole library as the main identifier)</li>
	 *  <li>SessionId</li>
	 *  </ul>
	 * </pre>
	 * 
	 * <p>
	 * Connections made, in order. Some may not be called if data isn't found
	 * </p>
	 * 
	 * <pre>
	 * https://secure.eu.playstation.com/sign-in/confirmation/
	 * https://store.playstation.com/j_acegi_external_security_check?target=/external/loginDefault.action
	 * https://store.playstation.com/external/loginDefault.action (;jsessionid=****) is appended at end if not set
	 * https://secure.eu.playstation.com/sign-in/confirmation/ (?sessionId=****)
	 * http://us.playstation.com/uwps/HandleIFrameRequests (?sessionId=****)
	 * http://us.playstation.com/uwps/PSNLoginCookie?cookieName=userinfo&id= (random number)
	 * </pre>
	 * 
	 * <p>
	 * Methods that need UK login
	 * </p>
	 * {@link #getClientFriendList()} </br> {@link #getClientGameList()} </br>
	 * {@link #getClientTrophyList(String)} </br>
	 * {@link #getClientFriendGameList(String)} </br>
	 * {@link #getClientFriendTrophyList(String, String)}
	 * 
	 * @param progressListener
	 *            progress listener used to monitor login process
	 * @param username
	 *            username which is email
	 * @param password
	 *            password
	 * @return PsnId from account
	 * @throws IllegalArgumentException
	 *             thrown if username or password is null
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws PlayStationNetworkException
	 *             thrown if sign in was unsuccessful. Not to be confused with
	 *             username or password not correct
	 * @throws PlayStationNetworkLoginException
	 *             thrown if username or password is not valid
	 */
	public String clientLogin(final ProgressListener progressListener,
			final String username, final String password) throws IOException,
			PlayStationNetworkException, PlayStationNetworkLoginException {
		Response response = null;
		log.debug("clientLogin - Entering");

		if (username == null || password == null) throw new IllegalArgumentException(
				"username and password can not be null");

		final ProgressHelper progressHelper = ProgressHelper
				.newInstance(progressListener);
		progressHelper.setup(6);
		String returnPsnId = null;

		final ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new NameValuePair("j_username", username));
		params.add(new NameValuePair("j_password", password));
		params.add(new NameValuePair("returnURL",
				"https://secure.eu.playstation.com/sign-in/confirmation/"));

		try {

			try {
				progressHelper.update("Logging in");
				response = networkHelper
						.setupMethod(
								POST,
								new URL(
										"https://store.playstation.com/j_acegi_external_security_check?target=/external/loginDefault.action"))
						.setPayload(params, "UTF-8").execute(networkHelper);
				response.disconnect();
			} catch (final IOException e) {
				throw e;
			}

			switch (response.getStatusCode()) {
			case HttpURLConnection.HTTP_MOVED_TEMP:
				String urlLocation = response.getConnection().getHeaderField(
						"Location");

				progressHelper.update("Authenticating");
				isLoginValid(response = networkHelper.setupMethod(GET,
						new URL(urlLocation)).execute(networkHelper));
				response.disconnect();

				// get session id and location
				urlLocation = response.getConnection().getHeaderField(
						"Location");
				clientSessionId = urlLocation.substring(urlLocation
						.indexOf("?sessionId=") + 11);

				// get additional cookies
				progressHelper.update("Authenticating");
				response = networkHelper.setupMethod(GET, new URL(urlLocation))
						.execute(networkHelper);
				response.disconnect();

				// get psn id

				// old way using UK site

				// @formatter:off
				/*urlConnection = networkHelper
						.openConnection("https://secure.eu.playstation.com/psn/mypsn/");

				progressHelper
						.updateProgress("Signing into Playstation Network");
				clientUserId = getPsnId(Utils.getContentFromInputStream(
						networkHelper.sendGet(urlConnection),
						NetworkHelper.getCharset(urlConnection)));
				urlConnection.disconnect();*/
				// @formatter:on

				// US method
				progressHelper.update("Retrieving PsnId");
				response = networkHelper
						.setupMethod(
								GET,
								new URL(
										String.format(
												"http://us.playstation.com/uwps/HandleIFrameRequests?sessionId=%s",
												clientSessionId)))
						.useCookies(false).execute(networkHelper);
				response.disconnect();
				Cookie userinfoCookie = null;

				for (final Cookie cookie : CookieManager.getCookies(response
						.getConnection())) {
					final String name = cookie.getName();
					if (name.equalsIgnoreCase("ph")) returnPsnId = cookie
							.getValue();
					else if (name.equalsIgnoreCase("userinfo")) {
						userinfoCookie = cookie;
						networkHelper.getCookieManager().putCookie(cookie);
					} else if (name.equalsIgnoreCase("TICKET")) networkHelper
							.getCookieManager().putCookie(cookie);
					else if (name.equalsIgnoreCase("PSNS2STICKET")) networkHelper
							.getCookieManager().putCookie(cookie);
				}

				// if returnPsnId is null proceed with second method
				if (userinfoCookie != null) {
					progressHelper.addIncrement(1);
					progressHelper.update("Retrieving userinfo");
					response = networkHelper
							.setupMethod(
									GET,
									new URL(
											String.format(
													"http://us.playstation.com/uwps/PSNLoginCookie?cookieName=%s&id=%f",
													"userinfo", Math.random())))
							.setHeader("X-Requested-With", "XMLHttpRequest")
							.setHeader("Cookie",
									userinfoCookie.getCookieString())
							.useCookies(false).execute(networkHelper);
					clientUserInfo = PsnUserInfo.newInstance(CommonUtils
							.getContentFromInputStream(response.getStream(),
									response.getCharset()));
					response.disconnect();
				}

				if (returnPsnId == null) throw new PlayStationNetworkLoginException(
						"Sign-In unsuccessful");

				// get Jid
				progressHelper.update("Retrieving Jid");
				clientJid = getOfficialJid(returnPsnId);

				progressHelper.finish("Successfully logged in");
				clientLoggedIn = true;
				break;
			case HttpURLConnection.HTTP_UNAVAILABLE:
				throw new PlayStationNetworkException(
						"PlayStation Network is unavailable");
			default:
				throw new PlayStationNetworkLoginException(
						"Error when logging in: " + response.getStatusCode());
			}

			return returnPsnId;
		} catch (final MalformedURLException e) {
			throw new PlayStationNetworkLoginException("Sign-In unsuccessful",
					e);
		} catch (final IllegalStateException e) {
			throw new PlayStationNetworkLoginException("Sign-In unsuccessful",
					e);
		} finally {
			if (response != null) response.disconnect();
			log.debug("clientLogin - Exiting");
		}

	}

	/**
	 * Retrieves client Friend List.
	 * 
	 * <p>
	 * Requirements
	 * </p>
	 * 
	 * <pre>
	 *  <li>UK Cookies
	 *     - PDC4_COOKIE
	 *     - s_token
	 * </li>
	 * </pre>
	 * 
	 * <p>
	 * Connections Made
	 * </p>
	 * 
	 * <pre>
	 * https://secure.eu.playstation.com/ajax/mypsn/friend/presence/
	 * </pre>
	 * 
	 * @see PsnUtils#createLoginCookiePdc4(String)
	 * @see PsnUtils#createLoginCookieToken(String)
	 * 
	 * @return friend list
	 * @throws PlayStationNetworkException
	 *             thrown if parse error is encountered
	 * @throws PlayStationNetworkLoginException
	 *             thrown if login cookies were invalid or not found
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public List<PsnFriendData> getClientFriendList()
			throws PlayStationNetworkException,
			PlayStationNetworkLoginException, IOException {
		Response response = null;
		log.debug("getFriendList - Entering");

		try {
			response = networkHelper
					.setupMethod(
							Method.GET,
							new URL(
									"https://secure.eu.playstation.com/ajax/mypsn/friend/presence/"))
					.setReadTimeout(0).execute(networkHelper);
			if (response.getStatusCode() == HttpURLConnection.HTTP_MOVED_TEMP) throw new PlayStationNetworkLoginException(
					"login cookies invalid, expired, or not found");
			final HandlerXmlFriend friendHandler = new HandlerXmlFriend();
			parser.parse(response.getStream(), friendHandler,
					response.getCharset());
			return friendHandler.getFriendList();
		} catch (final SAXException e) {
			throw new PlayStationNetworkException(
					"Unexpected error occurred while parsing xml", e);
		} finally {
			if (response != null) response.disconnect();
			log.debug("getFriendList - Exiting");
		}

	}

	/**
	 * Retrieves client game list.
	 * 
	 * <p>
	 * Requirements
	 * </p>
	 * 
	 * <pre>
	 *  <li>UK Cookies
	 *     - PDC4_COOKIE
	 *     - s_token
	 * </li>
	 * </pre>
	 * 
	 * <p>
	 * Connections Made
	 * </p>
	 * 
	 * <pre>
	 * http://uk.playstation.com/psn/mypsn/trophies/
	 * </pre>
	 * 
	 * @see PsnUtils#createLoginCookiePdc4(String)
	 * @see PsnUtils#createLoginCookieToken(String)
	 * 
	 * @return client game list
	 * @throws PlayStationNetworkException
	 *             thrown if parse error is encountered
	 * @throws PlayStationNetworkLoginException
	 *             thrown if login cookies were invalid or not found
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public List<PsnGameData> getClientGameList() throws IOException,
			PlayStationNetworkException, PlayStationNetworkLoginException {
		Response response = null;
		log.debug("getClientGameList - Entering");

		try {
			response = networkHelper.setupMethod(GET,
					new URL("http://uk.playstation.com/psn/mypsn/trophies/"))
					.execute(networkHelper);

			if (response.getStatusCode() == HttpURLConnection.HTTP_MOVED_TEMP) throw new PlayStationNetworkLoginException(
					"login cookies invalid, expired, or not found");

			final HandlerWebUKGame gameHandler = new HandlerWebUKGame(clientJid);
			parser.parse(response.getStream(), gameHandler,
					response.getCharset());
			return gameHandler.getGameList();
		} catch (final SAXException e) {
			throw new PlayStationNetworkException(
					"Unexpected error occurred while parsing xml", e);
		} finally {
			if (response != null) response.disconnect();
			log.debug("getClientGameList - Exiting");
		}
	}

	/**
	 * Retrieves client trophy list. Will return all types of trophies for given
	 * game
	 * 
	 * <p>
	 * Requirements
	 * </p>
	 * 
	 * <pre>
	 *  <li>UK Cookies
	 *     - PDC4_COOKIE
	 *     - s_token
	 * </li>
	 * </pre>
	 * 
	 * <p>
	 * Connections Made
	 * </p>
	 * 
	 * <pre>
	 * http://uk.playstation.com/psn/mypsn/trophies/detail/?title= (Trophy Link Id)
	 * </pre>
	 * 
	 * @see PsnUtils#createLoginCookiePdc4(String)
	 * @see PsnUtils#createLoginCookieToken(String)
	 * 
	 * @param trophyLinkId
	 *            trophy link Id
	 * @return client trophy list
	 * @throws IllegalArgumentException
	 *             thrown if invalid trophy link Id
	 * @throws PlayStationNetworkException
	 *             thrown if parse error is encountered
	 * @throws PlayStationNetworkLoginException
	 *             thrown if login cookies were invalid or not found
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public List<PsnTrophyData> getClientTrophyList(final String trophyLinkId)
			throws PlayStationNetworkException,
			PlayStationNetworkLoginException, IOException {
		Response response = null;
		log.debug("getClientTrophyList [{}] - Entering", trophyLinkId);

		if (PsnUtils.isValidGameId(trophyLinkId)) throw new IllegalArgumentException(
				"invalid trophyLinkId: " + trophyLinkId);

		try {
			response = networkHelper
					.setupMethod(
							GET,
							new URL(
									String.format(
											"http://uk.playstation.com/psn/mypsn/trophies/detail/?title=%s",
											trophyLinkId)))
					.setHeader("Referer",
							"http://uk.playstation.com/psn/mypsn/trophies/")
					.execute(networkHelper);
			if (response.getStatusCode() == HttpURLConnection.HTTP_MOVED_TEMP) throw new PlayStationNetworkLoginException(
					"login cookies invalid, expired, or not found");

			final HandlerWebUKTrophy trophyHandler = new HandlerWebUKTrophy(
					clientJid);
			parser.parse(response.getStream(), trophyHandler,
					response.getCharset());
			return trophyHandler.getTrophyList();
		} catch (final SAXException e) {
			throw new PlayStationNetworkException(
					"Unexpected error occurred while parsing xml", e);
		} finally {
			if (response != null) response.disconnect();
			log.debug("getClientTrophyList - Exiting");
		}
	}

	/**
	 * Retrieves client friend game list. <code>friendPsnId</code> must be a
	 * valid friend.
	 * 
	 * 
	 * <p>
	 * Requirements
	 * </p>
	 * 
	 * <pre>
	 *  <li>UK Cookies
	 *     - PDC4_COOKIE
	 *     - s_token
	 * </li>
	 * </pre>
	 * 
	 * <p>
	 * Connections Made
	 * </p>
	 * 
	 * <pre>
	 * http://uk.playstation.com/psn/mypsn/trophies-compare/?friend= (friend psnId) &mode=FRIENDS
	 * </pre>
	 * 
	 * @see PsnUtils#createLoginCookiePdc4(String)
	 * @see PsnUtils#createLoginCookieToken(String)
	 * 
	 * @param friendPsnId
	 *            friend psn id
	 * @return client friend game list
	 * @throws PlayStationNetworkException
	 *             thrown if parse error is encountered
	 * @throws PlayStationNetworkLoginException
	 *             thrown if login cookies were invalid or not found
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public List<PsnGameData> getClientFriendGameList(final String friendPsnId)
			throws IOException, PlayStationNetworkException,
			PlayStationNetworkLoginException {
		Response response = null;
		log.debug("getClientFriendGameList [{}] - Entering", friendPsnId);

		try {
			response = networkHelper
					.setupMethod(
							GET,
							new URL(
									String.format(
											"http://uk.playstation.com/psn/mypsn/trophies-compare/?friend=%s&mode=FRIENDS",
											friendPsnId)))
					.setHeader("Referer",
							"http://uk.playstation.com/psn/mypsn/friends/")
					.execute(networkHelper);
			if (response.getStatusCode() == HttpURLConnection.HTTP_MOVED_TEMP) throw new PlayStationNetworkLoginException(
					"login cookies invalid, expired, or not found");

			final HandlerWebFriendGame handler = new HandlerWebFriendGame(
					friendPsnId);
			parser.parse(response.getStream(), handler, response.getCharset());
			return handler.getGameList();
		} catch (final SAXException e) {
			throw new PlayStationNetworkLoginException(
					"Unexpected error occurred while parsing xml", e);
		} finally {
			if (response != null) response.disconnect();
			log.debug("getClientFriendGameList - Exiting");
		}
	}

	/**
	 * Retrieves client friend trophy list. <code>friendPsnId</code> must be a
	 * valid friend.
	 * 
	 * <p>
	 * Requirements
	 * </p>
	 * 
	 * <pre>
	 *  <li>UK Cookies
	 *     - PDC4_COOKIE
	 *     - s_token
	 * </li>
	 * </pre>
	 * 
	 * <p>
	 * Connections Made
	 * </p>
	 * 
	 * <pre>
	 * http://uk.playstation.com/psn/mypsn/trophies-compare/detail/?title= (trophy linkId) &friend= (friend psnId) &sortBy=game
	 * </pre>
	 * 
	 * @see PsnUtils#createLoginCookiePdc4(String)
	 * @see PsnUtils#createLoginCookieToken(String)
	 * 
	 * @param friendPsnId
	 *            friend psn id
	 * @param trophyLinkId
	 *            UK trophy link id
	 * @return client friend trophy list
	 * @throws PlayStationNetworkException
	 *             thrown if parse error is encountered
	 * @throws PlayStationNetworkLoginException
	 *             thrown if login cookies were invalid or not found
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public List<PsnTrophyData> getClientFriendTrophyList(
			final String friendPsnId, final String trophyLinkId)
			throws IOException, PlayStationNetworkException,
			PlayStationNetworkLoginException {
		Response response = null;
		log.debug("getClientFriendTrophyList [{}, {}] - Entering", friendPsnId,
				trophyLinkId);
		try {
			response = networkHelper
					.setupMethod(
							GET,
							new URL(
									String.format(
											"http://uk.playstation.com/psn/mypsn/trophies-compare/detail/?title=%s&friend=%s&sortBy=game",
											trophyLinkId, friendPsnId)))
					.setHeader(
							"Referer",
							"http://uk.playstation.com/psn/mypsn/trophies-compare/?friend="
									+ friendPsnId + "&mode=FRIENDS")
					.execute(networkHelper);
			if (response.getStatusCode() == HttpURLConnection.HTTP_MOVED_TEMP) throw new PlayStationNetworkLoginException(
					"login cookies invalid, expired, or not found");

			final HandlerWebFriendTrophy handler = new HandlerWebFriendTrophy(
					friendPsnId);
			parser.parse(response.getStream(), handler, response.getCharset());
			return handler.getTrophyList();
		} catch (final SAXException e) {
			throw new PlayStationNetworkLoginException(
					"Unexpected error occurred while parsing xml", e);
		} finally {
			if (response != null) response.disconnect();
			log.debug("getClientFriendTrophyList - Exiting");
		}

	}

	/**
	 * Retrieve the public game list from US site. Note that the web site
	 * happens not to get all games for given user. Use <i>Official</i> or
	 * <i>Client</i> methods if needed.
	 * 
	 * <p>
	 * Requirements
	 * </p>
	 * 
	 * <pre>
	 *  <li>US Cookies
	 *     - TICKET
	 *     - PSNS2STICKET
	 * </li>
	 * </pre>
	 * 
	 * <p>
	 * Connections Made
	 * </p>
	 * 
	 * <pre>
	 * http://us.playstation.com/playstation/psn/profile/ (psnId) /get_ordered_trophies_data
	 * </pre>
	 * 
	 * @see PsnUtils#createLoginCookieTicket(String)
	 * @see PsnUtils#createLoginCookiePsnTicket(String)
	 * 
	 * @param psnId
	 *            psn id
	 * @return public game list
	 * @throws PlayStationNetworkException
	 *             thrown if parse error is encountered
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public List<PsnGameData> getPublicGameList(final String psnId)
			throws PlayStationNetworkException, IOException {
		Response response = null;
		log.debug("getPublicGameList [{}] - Entering", psnId);

		try {
			response = networkHelper
					.setupMethod(
							GET,
							new URL(
									String.format(
											"http://us.playstation.com/playstation/psn/profile/%s/get_ordered_trophies_data",
											psnId)))
					.setHeader("Referer", "http://us.playstation.com")
					.setHeader("X-Requested-With", "XMLHttpRequest")
					.setHeader("Accept", "text/html").useCookies(false)
					.execute(networkHelper);

			final HandlerWebUSGame gameHandler = new HandlerWebUSGame(psnId);
			parser.parse(response.getStream(), gameHandler,
					response.getCharset());
			return gameHandler.getGames();
		} catch (final SAXException e) {
			throw new PlayStationNetworkException(
					"Unexpected error occurred while parsing xml", e);
		} finally {
			if (response != null) response.disconnect();
			log.debug("getPublicGameList - Exiting");
		}
	}

	/**
	 * Retrieve public trophy list.
	 * 
	 * <p>
	 * Requirements
	 * </p>
	 * 
	 * <pre>
	 *  <li>US Cookies
	 *     - TICKET
	 *     - PSNS2STICKET
	 * </li>
	 * </pre>
	 * 
	 * <p>
	 * Connections Made
	 * </p>
	 * 
	 * <pre>
	 * (getGameId == true) http://us.playstation.com/playstation/psn/profiles/ (psnId) /trophies/ (trophy linkId)
	 * http://us.playstation.com/playstation/psn/profile/ (psnId) /get_ordered_title_details_data
	 * </pre>
	 * 
	 * @see PsnUtils#createLoginCookieTicket(String)
	 * @see PsnUtils#createLoginCookiePsnTicket(String)
	 * 
	 * @param psnId
	 *            psn id
	 * @param trophyLinkId
	 *            US trophy link id
	 * @param getGameId
	 *            whether to retrieve <i>Official</i> game Id
	 * @return public trophy list
	 * @throws IllegalArgumentException
	 *             thrown if US trophy link Id isn't in correct format
	 * @throws PlayStationNetworkLoginException
	 *             thrown if login cookies were invalid or not found
	 * @throws PlayStationNetworkException
	 *             thrown if parse error is encountered
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public List<PsnTrophyData> getPublicTrophyList(final String psnId,
			final String trophyLinkId, final boolean getGameId)
			throws PlayStationNetworkException,
			PlayStationNetworkLoginException, IOException {
		return getPublicTrophyListInternal(psnId, trophyLinkId, getGameId);
	}

	/**
	 * Retrieve public trophy list.
	 * 
	 * <p>
	 * Requirements
	 * </p>
	 * 
	 * <pre>
	 *  <li>US Cookies
	 *     - TICKET
	 *     - PSNS2STICKET
	 * </li>
	 * </pre>
	 * 
	 * <p>
	 * Connections Made
	 * </p>
	 * 
	 * <pre>
	 * http://us.playstation.com/playstation/psn/profile/ (psnId) /get_ordered_title_details_data
	 * </pre>
	 * 
	 * @see PsnUtils#createLoginCookieTicket(String)
	 * @see PsnUtils#createLoginCookiePsnTicket(String)
	 * 
	 * @param psnId
	 *            psn id
	 * @param trophyLinkId
	 *            US trophy link id
	 * @param gameId
	 *            <i>Official</i> game Id
	 * @return public trophy list
	 * @throws IllegalArgumentException
	 *             thrown if US trophy link Id isn't right format
	 * @throws PlayStationNetworkException
	 *             thrown if parse error is encountered, psnId invalid, or
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public List<PsnTrophyData> getPublicTrophyList(final String psnId,
			final String trophyLinkId, final String gameId)
			throws PlayStationNetworkException,
			PlayStationNetworkLoginException, IOException {
		return getPublicTrophyListInternal(psnId, trophyLinkId, gameId);
	}

	private List<PsnTrophyData> getPublicTrophyListInternal(final String psnId,
			final String trophyLinkId, final Object option) throws IOException,
			PlayStationNetworkException, PlayStationNetworkLoginException {
		Response response = null;
		String methodGameId = null;
		HandlerWebUSTrophy trophyHandler = null;
		log.debug("getPublicTrophyListInternal [{}, {}, {}] - Entering", psnId,
				trophyLinkId, option);

		if (PsnUtils.isValidGameId(trophyLinkId)) throw new IllegalArgumentException(
				"invalid trophyLinkId: " + trophyLinkId);

		try {

			if (option instanceof Boolean && option == Boolean.TRUE) {

				response = networkHelper
						.setupMethod(
								GET,
								new URL(
										String.format(
												"http://us.playstation.com/playstation/psn/profiles/%s/trophies/%s",
												psnId, trophyLinkId)))
						.setHeader("Referer",
								"http://us.playstation.com/playstation/psn/profiles/")
						.setHeader("Accept", "text/html")
						.execute(networkHelper);
				trophyHandler = new HandlerWebUSTrophy(true);
				if (response.getStatusCode() == HttpURLConnection.HTTP_NOT_FOUND) throw new PlayStationNetworkException(
						"PsnId or gameId invalid");
				parser.parse(response.getStream(), trophyHandler,
						response.getCharset());
				response.disconnect();
				methodGameId = trophyHandler.getGameId();
			}

			final ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new NameValuePair("sortBy", "id_asc"));
			params.add(new NameValuePair("titleId", trophyLinkId));

			response = networkHelper
					.setupMethod(
							POST,
							new URL(
									String.format(
											"http://us.playstation.com/playstation/psn/profile/%s/get_ordered_title_details_data",
											psnId)))
					.setHeader("Referer", "http://us.playstation.com")
					.setHeader("X-Requested-With", "XMLHttpRequest")
					.setHeader("Accept", "text/html")
					.setPayload(params, "UTF-8").execute(networkHelper);

			trophyHandler = new HandlerWebUSTrophy(psnId, methodGameId);
			parser.parse(response.getStream(), trophyHandler,
					response.getCharset());
			response.disconnect();
			switch (trophyHandler.getResponseId()) {
			case 1:
				throw new PlayStationNetworkException("psnId invalid, empty");
			case 2:
				throw new PlayStationNetworkException("psnId invaid");
			case -1:
				new PlayStationNetworkException("TrophyLinkId or psnId invalid")
						.printStackTrace();
				return null;
			case -2:
				throw new PlayStationNetworkLoginException(
						"TICKET and PSN2STICKET cookies were not present, invalid, or expired");

			}
			return trophyHandler.getTrophies();
		} catch (final SAXException e) {
			throw new PlayStationNetworkException(e);
		} finally {
			if (response != null) response.disconnect();
			log.debug("getPublicTrophyListInternal - Exiting");
		}

	}

	/**
	 * Retrieves official profile.
	 * 
	 * <p>
	 * Requirements
	 * </p>
	 * 
	 * <pre>
	 *  <li>Authorization
	 *     - c7y-basic01 : A9QTbosh0W0D^{7467l-n_>2Y%JG^v>o
	 * </li>
	 * Should already be set by calling {@link #init()}
	 * </pre>
	 * 
	 * <p>
	 * Connections Made
	 * </p>
	 * 
	 * <pre>
	 * http://getprof.us.np.community.playstation.net/basic_view/func/get_profile
	 * http://trophy.ww.np.community.playstation.net/trophy/func/get_user_info
	 * </pre>
	 * 
	 * @param jid
	 *            jid
	 * @return if found, profile, otherwise null
	 * @throws PlayStationNetworkException
	 *             thrown if parse error is encountered
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public PsnProfileData getOfficialProfile(final String jid)
			throws PlayStationNetworkException, IOException {
		Response response = null;
		final HandlerXmlProfile handlerXmlProfile = new HandlerXmlProfile();
		log.debug("getProfile [{}] - Entering", jid);
		try {

			String xmlPost = String.format(
					"<profile platform='ps3' sv='%s'><jid>%s</jid></profile>",
					PS3_FIRMWARE_VERSION, jid);

			response = networkHelper
					.setupMethod(
							POST,
							new URL(
									"http://getprof.us.np.community.playstation.net/basic_view/func/get_profile"))
					.setHeader("Content-Type", "text/xml; charset=UTF-8")
					.setHeader("Accept-Encoding", "identity")
					.setHeader("User-Agent", AGENT_PS3_COMMUNITY)
					.setPayload(xmlPost.getBytes("UTF-8"))
					.execute(networkHelper);

			parser.parse(response.getStream(), handlerXmlProfile,
					response.getCharset());
			response.disconnect();
			if (handlerXmlProfile.getProfile() == null) return null;

			xmlPost = String
					.format("<nptrophy platform='ps3' sv='%s'><jid>%s</jid></nptrophy>",
							PS3_FIRMWARE_VERSION, jid);

			response = networkHelper
					.setupMethod(
							POST,
							new URL(
									"http://trophy.ww.np.community.playstation.net/trophy/func/get_user_info"))
					.setHeader("Content-Type", "text/xml; charset=UTF-8")
					.setHeader("Accept-Encoding", "identity")
					.setHeader("User-Agent", AGENT_PS3_COMMUNITY)
					.setPayload(xmlPost.getBytes("UTF-8"))
					.execute(networkHelper);

			parser.parse(response.getStream(), handlerXmlProfile,
					response.getCharset());
			return handlerXmlProfile.getProfile();
		} catch (final SAXException e) {
			throw new PlayStationNetworkException(
					"Unexpected error occurred while parsing xml", e);
		} finally {
			if (response != null) response.disconnect();
			log.debug("getProfile - Exiting");
		}
	}

	/**
	 * Retrieves official firmware version of platform type.
	 * 
	 * <p>
	 * Connections Made
	 * </p>
	 * 
	 * <pre>
	 * (PS3) http://fus01.ps3.update.playstation.net/update/ps3/list/us/ps3-updatelist.txt
	 * (VITA) http://fus01.psp2.update.playstation.net/update/psp2/list/us/psp2-updatelist.xml
	 * (PSP) http://fu01.psp.update.playstation.org/update/psp/list2/us/psp-updatelist.txt
	 * </pre>
	 * 
	 * @param platform
	 *            platform to check firmware
	 * @return firmware version
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public String getOfficialFirmwareVersion(final PlatformType platform)
			throws IOException {
		Response response = null;
		String version = null;
		try {
			String userAgent = null;
			switch (platform) {
			case PS3:
				userAgent = AGENT_PS3_UPDATE;
				break;
			case VITA:
				userAgent = AGENT_VITA_LIBHTTP;
				break;
			case PSP:
				userAgent = AGENT_PSP_UPDATE;
				break;
			}
			response = networkHelper
					.setupMethod(
							GET,
							new URL(
									String.format(
											"http://%s.%s.update.playstation.%s/update/%s/%s/us/%s-updatelist.%s",
											platform == PlatformType.PSP ? "fu01"
													: "fus01",
											platform.getTypeString(),
											platform == PlatformType.PSP ? "org"
													: "net",
											platform.getTypeString(),
											platform == PlatformType.PSP ? "list2"
													: "list",
											platform.getTypeString(),
											platform == PlatformType.VITA ? "xml"
													: "txt")))
					.setHeader("User-Agent", userAgent)
					.setHeader("Accept-Encoding", "identity")
					.execute(networkHelper);

			final String data = CommonUtils.getContentFromInputStream(
					response.getStream(), response.getCharset());

			switch (platform) {
			case PS3:
				final String[] dataArray = data.split(";");

				for (final String part : dataArray)
					if (part.subSequence(0, part.indexOf("=")).toString()
							.equalsIgnoreCase("SystemSoftwareVersion")) {
						version = PS3_FIRMWARE_VERSION = part.substring(
								part.indexOf("=") + 1, part.length() - 2);
						break;
					}

				break;
			case VITA:
				final Matcher matcher = Pattern.compile("label=\"\\S+\">")
						.matcher(data);
				matcher.find();
				final String find = matcher.group();
				version = find.substring(find.indexOf("label=\"") + 7,
						find.lastIndexOf("\">"));
				break;
			case PSP:
				final int start = data.indexOf("#SystemSoftwareVersion=");
				version = data.substring(start + 23,
						data.indexOf(";", start + 23));
				break;
			}

			return version;
		} finally {
			if (response != null) response.disconnect();
		}
	}

	/**
	 * Retrieves Jid for giving PsnId. Every psnId should be on the US server no
	 * matter what country.
	 * 
	 * <p>
	 * Requirements
	 * </p>
	 * 
	 * <pre>
	 *  <li>Authorization
	 *     - c7y-basic01 : A9QTbosh0W0D^{7467l-n_>2Y%JG^v>o
	 * </li>
	 * Should already be set by calling {@link #init()}
	 * </pre>
	 * 
	 * <p>
	 * Connections Made
	 * </p>
	 * 
	 * <pre>
	 * http://searchjid.usa.np.community.playstation.net/basic_view/func/search_jid
	 * </pre>
	 * 
	 * @param psnId
	 *            psn id
	 * @return jid
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public String getOfficialJid(final String psnId) throws IOException {
		log.debug("getJid [{}] - Entering", psnId);
		Response response = null;

		final String xmlPost = String
				.format("<?xml version='1.0' encoding='utf-8'?><searchjid platform='ps3' sv='%s'><online-id>%s</online-id></searchjid>",
						PS3_FIRMWARE_VERSION, psnId);

		try {

			// searchjid.usa should work with all countries
			response = networkHelper
					.setupMethod(
							POST,
							new URL(
									"http://searchjid.usa.np.community.playstation.net/basic_view/func/search_jid"))
					.setPayload(xmlPost.getBytes("UTF-8"))
					.setHeader("User-Agent", AGENT_PS3_COMMUNITY)
					.execute(networkHelper);
			final String data = CommonUtils.getContentFromInputStream(
					response.getStream(), response.getCharset());

			String jid = null;
			final int start = data.indexOf("<jid>") + 5;
			if (start != -1) jid = data
					.substring(start, data.indexOf("</jid>"));

			log.debug("getJid - Exiting");
			return jid;
		} catch (final UnsupportedEncodingException e) {
			throw e;
		} finally {
			if (response != null) response.disconnect();
		}
	}

	/**
	 * Retrieves game list for Jid. Logger will show warning if max goes over 64
	 * since it is the default limit. If no platform is given, PS3 will be used
	 * as default.
	 * 
	 * <p>
	 * Requirements
	 * </p>
	 * 
	 * <pre>
	 *  <li>Authorization
	 *     - c7y-trophy01 : jhlWmT0|:0!nC:b:#x/uihx'Y74b5Ycx
	 * </li>
	 * Should already be set by calling {@link #init()}
	 * </pre>
	 * 
	 * <p>
	 * Connections Made
	 * </p>
	 * 
	 * <pre>
	 * http://trophy.ww.np.community.playstation.net/trophy/func/get_title_list
	 * </pre>
	 * 
	 * @param jid
	 *            jid
	 * @param start
	 *            start index. Has to be one or greater.
	 * @param max
	 *            max games
	 * @param platforms
	 *            platforms for games
	 * @return official game list
	 * @throws IllegalArgumentException
	 *             thrown if start index is zero or less
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws PlayStationNetworkException
	 *             thrown if parse error is encountered or invalid jid
	 */
	public List<PsnGameDataOfficial> getOfficialGameList(final String jid,
			final int start, final int max, final PlatformType... platforms)
			throws IOException, PlayStationNetworkException {
		Response response = null;
		log.debug("getOfficialGameList [{}, {}, {}, {}] - Entering", jid,
				start, max, platforms);

		try {
			if (start <= 0) throw new IllegalArgumentException(
					"start index must be greater than 0");

			if (max > 64) log.warn("max index is greater than 64");

			final String xmlPost = String
					.format("<nptrophy platform='ps3' sv='%s'><jid>%s</jid><start>%d</start><max>%d</max>%s</nptrophy>",
							PS3_FIRMWARE_VERSION, jid, start, max,
							getPlatformString(platforms));

			response = networkHelper
					.setupMethod(
							POST,
							new URL(
									"http://trophy.ww.np.community.playstation.net/trophy/func/get_title_list"))
					.setHeader("User-Agent", AGENT_PS3_APPLICATION)
					.setHeader("Content-Type", "text/xml; charset=UTF-8")
					.setHeader("Accept-Encoding", "identity")
					.setPayload(xmlPost.getBytes("UTF-8"))
					.execute(networkHelper);

			final HandlerXmlGame gameHandler = new HandlerXmlGame(jid);
			parser.parse(response.getStream(), gameHandler,
					response.getCharset());
			if (gameHandler.getResult().equals("05")) throw new PlayStationNetworkException(
					"jid invalid");
			return gameHandler.getGames();
		} catch (final SAXException e) {
			throw new PlayStationNetworkException(
					"Unexpected error occurred while parsing xml", e);
		} finally {
			if (response != null) response.disconnect();
			log.debug("getOfficialGameList - Exiting");
		}

	}

	/**
	 * Retrieves official trophy list from jid and <i>Official</i> game Id. If
	 * game Id is invalid a blank game list will be returned.
	 * 
	 * <p>
	 * Requirements
	 * </p>
	 * 
	 * <pre>
	 *  <li>Authorization
	 *     - c7y-trophy01 : jhlWmT0|:0!nC:b:#x/uihx'Y74b5Ycx
	 * </li>
	 * Should already be set by calling {@link #init()}
	 * </pre>
	 * 
	 * <p>
	 * Connections Made
	 * </p>
	 * 
	 * <pre>
	 * http://trophy.ww.np.community.playstation.net/trophy/func/get_trophies
	 * </pre>
	 * 
	 * @param jid
	 *            jid
	 * @param gameId
	 *            <i>Official</i> game id
	 * @return official trophy list
	 * @throws IllegalArgumentException
	 *             thrown if game id isn't in <i>Official</i> format
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws PlayStationNetworkException
	 *             thrown if parse error is encountered or invalid id
	 */
	public List<PsnTrophyDataOfficial> getOfficialTrophyList(final String jid,
			final String gameId) throws IOException,
			PlayStationNetworkException {
		Response response = null;
		log.debug("getOfficialTrophyList [{}, {}] - Entering", jid, gameId);

		if (!PsnUtils.isValidGameId(gameId)) throw new IllegalArgumentException(
				"Must be a valid PsnGame Id");

		try {
			final String xmlPost = String
					.format("<nptrophy platform='ps3' sv='%s'><jid>%s</jid><list><info npcommid='%s'><target>FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF</target></info></list></nptrophy>",
							PS3_FIRMWARE_VERSION, jid, gameId);

			response = networkHelper
					.setupMethod(
							POST,
							new URL(
									"http://trophy.ww.np.community.playstation.net/trophy/func/get_trophies"))
					.setHeader("User-Agent", AGENT_PS3_APPLICATION)
					.setHeader("Content-Type", "text/xml; charset=UTF-8")
					.setHeader("Accept-Encoding", "identity")
					.setPayload(xmlPost.getBytes("UTF-8"))
					.execute(networkHelper);

			final HandlerXmlTrophy trophyHandler = new HandlerXmlTrophy(jid);
			parser.parse(response.getStream(), trophyHandler,
					response.getCharset());
			if (trophyHandler.getResult().equals("05")) throw new PlayStationNetworkException(
					"jid invalid");
			return trophyHandler.getTrophies();
		} catch (final SAXException e) {
			throw new PlayStationNetworkException(
					"Unexpected error occurred while parsing xml", e);
		} finally {
			if (response != null) response.disconnect();
			log.debug("getOfficialTrophyList - Exiting");
		}

	}

	/**
	 * Retrieves official latest trophy list. Logger will show warning if max
	 * goes over 64 since it is the default limit. If no platform is given, PS3
	 * will be used as default.
	 * 
	 * <p>
	 * Requirements
	 * </p>
	 * 
	 * <pre>
	 *  <li>Authorization
	 *     - c7y-trophy01 : jhlWmT0|:0!nC:b:#x/uihx'Y74b5Ycx
	 * </li>
	 * Should already be set by calling {@link #init()}
	 * </pre>
	 * 
	 * <p>
	 * Connections Made
	 * </p>
	 * 
	 * <pre>
	 * http://trophy.ww.np.community.playstation.net/trophy/func/get_latest_trophies
	 * </pre>
	 * 
	 * 
	 * @param jid
	 *            jid
	 * @param max
	 *            max amount of trophies
	 * @param platforms
	 *            platforms for trophies
	 * @return official trophy list
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws PlayStationNetworkException
	 *             thrown if parse error is encountered or invalid id
	 */
	public List<PsnTrophyDataOfficial> getOfficialLatestTrophyList(
			final String jid, final int max, final PlatformType... platforms)
			throws PlayStationNetworkException, IOException {
		Response response = null;
		log.debug("getOfficialLatestTrophyList [{}, {}, {}] - Entering", jid,
				max, platforms);

		if (max > 64) log.warn("max index is greater than 64");

		try {
			final String xmlPost = String
					.format("<nptrophy platform='ps3' sv='%s'><jid>%s</jid><max>%d</max>%s</nptrophy>",
							PS3_FIRMWARE_VERSION, jid, max,
							getPlatformString(platforms));

			response = networkHelper
					.setupMethod(
							POST,
							new URL(
									"http://trophy.ww.np.community.playstation.net/trophy/func/get_latest_trophies"))
					.setHeader("User-Agent", AGENT_PS3_APPLICATION)
					.setHeader("Content-Type", "text/xml; charset=UTF-8")
					.setHeader("Accept-Encoding", "identity")
					.setPayload(xmlPost.getBytes("UTF-8"))
					.execute(networkHelper);

			final HandlerXmlTrophy trophyHandler = new HandlerXmlTrophy(jid);
			parser.parse(response.getStream(), trophyHandler,
					response.getCharset());
			if (trophyHandler.getResult().equals("05")) throw new PlayStationNetworkException(
					"Id invalid");
			return trophyHandler.getTrophies();
		} catch (final SAXException e) {
			throw new PlayStationNetworkException(
					"Unexpected error occurred while parsing xml", e);
		} finally {
			if (response != null) response.disconnect();
			log.debug("getOfficialLatestTrophyList - Exiting");
		}

	}

	/**
	 * Retrieves official trophy list from date. Logger will show warning if max
	 * goes over 64 since it is the default limit. If no platform is given, PS3
	 * will be used as default.
	 * 
	 * <p>
	 * Requirements
	 * </p>
	 * 
	 * <pre>
	 *  <li>Authorization
	 *     - c7y-trophy01 : jhlWmT0|:0!nC:b:#x/uihx'Y74b5Ycx
	 * </li>
	 * Should already be set by calling {@link #init()}
	 * </pre>
	 * 
	 * <p>
	 * Connections Made
	 * </p>
	 * 
	 * <pre>
	 * http://trophy.ww.np.community.playstation.net/trophy/func/get_latest_trophies
	 * </pre>
	 * 
	 * @see PsnUtils#getOfficialDateFormat(java.util.Date)
	 * 
	 * @param jid
	 *            jid
	 * @param max
	 *            max amount of trophies
	 * @param since
	 *            date in format <code>yyyy-MM-dd'T'HH:mm:ss.SSSZ</code>
	 * @param platforms
	 *            platforms for trophies
	 * @return the official trophy list since
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws PlayStationNetworkException
	 *             thrown if parse error is encountered or invalid id
	 */
	public List<PsnTrophyDataOfficial> getOfficialTrophyListSince(
			final String jid, final int max, final String since,
			final PlatformType... platforms) throws IOException,
			PlayStationNetworkException {
		Response response = null;
		if (max > 64) log.warn("max index is greater than 64");

		log.debug("getOfficialTrophyListSince [{}, {}, {}, {}] - Entering",
				jid, max, since, platforms);

		try {
			final String xmlPost = String
					.format("<nptrophy platform='ps3' sv='%s'><jid>%s</jid><max>%d</max><since>%s</since>%s</nptrophy>",
							PS3_FIRMWARE_VERSION, jid, max, since,
							getPlatformString(platforms));
			response = networkHelper
					.setupMethod(
							Method.POST,
							new URL(
									"http://trophy.ww.np.community.playstation.net/trophy/func/get_latest_trophies"))
					.setHeader("User-Agent", AGENT_PS3_APPLICATION)
					.setHeader("Content-Type", "text/xml; charset=UTF-8")
					.setHeader("Accept-Encoding", "identity")
					.setPayload(xmlPost.getBytes("UTF-8"))
					.execute(networkHelper);

			final HandlerXmlTrophy trophyHandler = new HandlerXmlTrophy(jid);
			parser.parse(response.getStream(), trophyHandler,
					response.getCharset());
			if (trophyHandler.getResult().equals("05")) throw new PlayStationNetworkException(
					"jid invalid");
			return trophyHandler.getTrophies();

		} catch (final SAXException e) {
			throw new PlayStationNetworkException(
					"Unexpected error occurred while parsing xml", e);
		} finally {
			if (response != null) response.disconnect();
			log.debug("getOfficialTrophyListSince - Exiting");
		}
	}

	/**
	 * Logs into UK account from credentials. This method only logs into UK
	 * account and that is all. Use this if login cookies are invalid and need
	 * new ones.
	 * 
	 * <p>
	 * Following data is stored
	 * </p>
	 * 
	 * <pre>
	 *  <ul>
	 *  <li>UK Cookies
	 *       - PDC4_COOKIE (login cookie)
	 *       - s_token (login cookie)
	 *  </li>
	 *  <li>SessionId</li>
	 *  </ul>
	 * </pre>
	 * 
	 * <p>
	 * Connections made, in order.
	 * </p>
	 * 
	 * <pre>
	 * https://store.playstation.com/j_acegi_external_security_check?target=/external/loginDefault.action
	 * https://store.playstation.com/external/loginDefault.action (;jsessionid=****) is appended at end if not set
	 * https://secure.eu.playstation.com/sign-in/confirmation/ (?sessionId=****)
	 * </pre>
	 * 
	 * @see #clientLogin(ProgressListener, String, String)
	 * 
	 * @param progressListener
	 *            progress listener
	 * @param username
	 *            username which is email
	 * @param password
	 *            password
	 * @throws IllegalArgumentException
	 *             thrown if username or password is null
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws PlayStationNetworkException
	 *             thrown if sign in was unsuccessful. Not to be confused with
	 *             username or password not correct
	 * @throws PlayStationNetworkLoginException
	 *             thrown if username or password is not valid
	 */
	public void loginUK(final ProgressListener progressListener,
			final String username, final String password) throws IOException,
			PlayStationNetworkException, PlayStationNetworkLoginException {
		Response response = null;
		log.debug("loginUK - Entering");

		if (username == null || password == null) throw new IllegalArgumentException(
				"username and password can not be null");

		final ProgressHelper progressHelper = ProgressHelper
				.newInstance(progressListener);
		progressHelper.setup(4);

		final ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new NameValuePair("j_username", username));
		params.add(new NameValuePair("j_password", password));
		params.add(new NameValuePair("returnURL",
				"https://secure.eu.playstation.com/sign-in/confirmation/"));

		try {

			try {
				progressHelper.update("Logging in");
				response = networkHelper
						.setupMethod(
								POST,
								new URL(
										"https://store.playstation.com/j_acegi_external_security_check?target=/external/loginDefault.action"))
						.setPayload(params, "UTF-8").execute(networkHelper);
				response.disconnect();
			} catch (final IOException e) {
				throw e;
			}

			switch (response.getStatusCode()) {
			case HttpURLConnection.HTTP_MOVED_TEMP:
				String urlLocation = response.getConnection().getHeaderField(
						"Location");

				progressHelper.update("Authenticating");
				isLoginValid(response = networkHelper.setupMethod(GET,
						new URL(urlLocation)).execute(networkHelper));
				response.disconnect();

				// get session id and location
				urlLocation = response.getConnection().getHeaderField(
						"Location");
				clientSessionId = urlLocation.substring(urlLocation
						.indexOf("?sessionId=") + 11);

				// get additional cookies
				progressHelper.update("Authenticating");
				response = networkHelper.setupMethod(GET, new URL(urlLocation))
						.execute(networkHelper);
				response.disconnect();
				progressHelper.finish("Successfully logged in");
				break;
			case HttpURLConnection.HTTP_UNAVAILABLE:
				throw new PlayStationNetworkException(
						"PlayStation Network is unavailable due to maintenance");
			default:
				throw new PlayStationNetworkLoginException(
						"Error when logging in: " + response.getStatusCode());
			}

		} catch (final MalformedURLException e) {
			throw new PlayStationNetworkLoginException("Sign-In unsuccessful",
					e);
		} catch (final IllegalStateException e) {
			e.printStackTrace();
			throw new PlayStationNetworkLoginException("Sign-In unsuccessful",
					e);
		} finally {
			if (response != null) response.disconnect();
			log.debug("loginUK - Exiting");
		}

	}

	/**
	 * Logs into UK account from credentials. This method only logs into US
	 * account and that is all. In the client this method is useless since
	 * {@link #clientLogin(ProgressListener, String, String)} retrieves US login
	 * cookies. Plus there are methods to generate them as well. It can be used
	 * to retrieve client session Id and user info as well.
	 * 
	 * 
	 * <p>
	 * Following data is stored
	 * </p>
	 * 
	 * <pre>
	 *  <ul>
	 *  <li>US Cookies
	 *       - userinfo (contains {@link PsnUserInfo} data)
	 *       - TICKET (login cookie)
	 *       - PSNS2STICKET (login cookie)
	 * </li>
	 *  <li>SessionId</li>
	 *  </ul>
	 * </pre>
	 * 
	 * <p>
	 * Connections made, in order. Some may not be called if data isn't found
	 * </p>
	 * 
	 * <pre>
	 * https://account.sonyentertainmentnetwork.com/external/auth/login!authenticate.action
	 * https://us.playstation.com/uwps/PSNTicketRetrievalGenericServlet (?sessionId=****)
	 * http://us.playstation.com/uwps/HandleIFrameRequests (?sessionId=****)
	 * </pre>
	 * 
	 * @see #clientLogin(ProgressListener, String, String)
	 * @see PsnUtils#createLoginCookieTicket(String)
	 * @see PsnUtils#createLoginCookiePsnTicket(String)
	 * 
	 * @param progressListener
	 *            progress listener
	 * @param username
	 *            username which is email
	 * @param password
	 *            password
	 * @throws IllegalArgumentException
	 *             thrown if username or password is null
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 * @throws PlayStationNetworkException
	 *             thrown if sign in was unsuccessful. Not to be confused with
	 *             username or password not correct
	 * @throws PlayStationNetworkLoginException
	 *             thrown if username or password is not valid
	 */
	public void loginUS(final ProgressListener progressListener,
			final String username, final String password) throws IOException,
			PlayStationNetworkException, PlayStationNetworkLoginException {
		Response response = null;
		log.debug("loginUS - Entering");

		if (username == null || password == null) throw new IllegalArgumentException(
				"username and password can not be null");

		final ProgressHelper progressHelper = ProgressHelper
				.newInstance(progressListener);
		progressHelper.setup(4);

		try {
			final ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new NameValuePair("j_username", username));
			params.add(new NameValuePair("j_password", password));
			params.add(new NameValuePair("returnURL",
					"https://us.playstation.com/uwps/PSNTicketRetrievalGenericServlet"));
			params.add(new NameValuePair("service-entity", "psn"));

			progressHelper.update("Logging in");
			response = networkHelper
					.setupMethod(
							POST,
							new URL(
									"https://account.sonyentertainmentnetwork.com/external/auth/login!authenticate.action"))
					.setHeader(
							"Referer",
							"https://account.sonyentertainmentnetwork.com/external/auth/login.action?request_locale=en_US&service-entity=psn&returnURL=https://us.playstation.com/uwps/PSNTicketRetrievalGenericServlet")
					.setPayload(params, "UTF-8").execute(networkHelper);
			response.disconnect();
			switch (response.getStatusCode()) {
			case HttpURLConnection.HTTP_MOVED_TEMP:
				final String urlLocation = response.getConnection()
						.getHeaderField("Location");
				progressHelper.update("Authenticating");
				response = networkHelper.setupMethod(GET, new URL(urlLocation))
						.setHeader("Referer", "https://us.playstation.com")
						.execute(networkHelper);
				response.disconnect();

				// get session id and location
				clientSessionId = urlLocation.substring(urlLocation
						.indexOf("?sessionId=") + 11);

				progressHelper.update("Authenticating");
				response = networkHelper
						.setupMethod(
								Method.GET,
								new URL(
										String.format(
												"http://us.playstation.com/uwps/HandleIFrameRequests?sessionId=%s",
												clientSessionId)))
						.useCookies(false).execute(networkHelper);
				response.disconnect();
				progressHelper.finish("Successfully logged in");
				break;
			case HttpURLConnection.HTTP_UNAVAILABLE:
				throw new PlayStationNetworkException(
						"PlayStation Network is unavailable due to maintenance");
			default:
				throw new PlayStationNetworkLoginException(
						"Error when logging in: " + response.getStatusCode());
			}

		} catch (final MalformedURLException e) {
			throw new PlayStationNetworkLoginException("Sign-In unsuccessful",
					e);
		} finally {
			if (response != null) response.disconnect();
			log.debug("loginUS - Exiting");
		}
	}

	private boolean isLoginValid(final Response response) throws IOException,
			PlayStationNetworkLoginException {
		if (response.getStatusCode() == HttpURLConnection.HTTP_MOVED_TEMP) return true;

		if (CommonUtils.streamingContains(response.getStream(), "Incorrect",
				response.getCharset())) {
			throw new PlayStationNetworkLoginException(
					"incorrect username or password");
		}

		throw new PlayStationNetworkLoginException("Login Failed");
	}

	private String getPlatformString(final PlatformType[] platforms) {
		if (platforms == null) return "";
		String platformString = "";
		for (final PlatformType platform : platforms) {
			platformString += String.format("<pf>%s</pf>",
					platform.getTypeString());
		}

		return platformString;
	}
}
