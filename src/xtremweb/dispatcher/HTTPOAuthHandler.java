/*
 * Copyrights     : CNRS
 * Author         : Oleg Lodygensky
 * Acknowledgment : XtremWeb-HEP is based on XtremWeb 1.8.0 by inria : http://www.xtremweb.net/
 * Web            : http://www.xtremweb-hep.org
 *
 *      This file is part of XtremWeb-HEP.
 *
 *    XtremWeb-HEP is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    XtremWeb-HEP is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with XtremWeb-HEP.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package xtremweb.dispatcher;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.expressme.openid.Authentication;
import com.github.scribejava.apis.FacebookApi;
import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.apis.YahooApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.Token;
import com.github.scribejava.core.oauth.OAuth10aService;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.github.scribejava.core.oauth.OAuthService;

import xtremweb.common.Logger;
import xtremweb.common.LoggerLevel;
import xtremweb.common.MD5;
import xtremweb.common.XWPropertyDefs;
import xtremweb.common.XWTools;
import xtremweb.communications.Connection;
import xtremweb.communications.XWPostParams;

/**
 * This handles HTTP request to /oauth2/
 *
 * Created: 19 fevrier 2013
 *
 * @author Oleg Lodygensky
 * @since 9.1.1
 */

public class HTTPOAuthHandler extends Thread implements org.eclipse.jetty.server.Handler {

	public class OAuthException extends Exception {

		public OAuthException(final String str) {
			super(str);
		}

	}

	public enum Operator {
		FACEBOOK {
			@Override
			public Class getScribeClass() {
				return FacebookApi.class;
			}
		},
		GOOGLE {
			@Override
			public Class getScribeClass() {
				return GoogleApi20.class;
			}
		},
		TWITTER {
			@Override
			public Class getScribeClass() {
				return TwitterApi.class;
			}
		},
		YAHOO {
			@Override
			public Class getScribeClass() {
				return YahooApi.class;
			}
		};
		/**
		 * This is the application id
		 */
		protected String appId;
		/**
		 * This is the application secrete key
		 */
		protected String appKey;
		/**
		 * This is the OAuth scope
		 */
		protected String scope;
		/**
		 * This is the callback url
		 */
		protected String callbackUrl;
		/**
		 * This is the OAuth uri
		 * @since 10.5.0
		 */
		protected String authUri;
		/**
		 * This is the server address
		 * @since 10.5.0
		 */
		protected String serverAddress;
		/**
		 * This is the service
		 */
		private OAuthService service;

		/**
		 * This retrieves the operator class from scribe library
		 *
		 * @return the operator class
		 */
		public abstract Class getScribeClass();

		/**
		 * This sets the String representing the application ID
		 */
		public void setAppId(final String id) {
			this.appId = id;
		}

		/**
		 * This retrieves the String representing the application ID
		 *
		 * @return the application identifier
		 */
		public String getAppId() {
			return this.appId;
		}

		/**
		 * This sets the String representing the application key
		 */
		public void setAppKey(final String key) {
			this.appKey = key;
		}

		/**
		 * This retrieves the String representing the application key
		 *
		 * @return the application identifier
		 */
		public String getAppKey() {
			return this.appKey;
		}
		/**
		 * This sets the OAuth service scope
		 */
		public void setScope(final String key) {
			this.scope = key;
		}

		/**
		 * This retrieves the service scope
		 *
		 * @return the application identifier
		 */
		public String getScope() {
			return this.scope;
		}
		/**
		 * This sets the OAuth callback url
		 */
		public void setCallbackUrl(final String key) {
			this.callbackUrl = key;
		}

		/**
		 * This retrieves the callback url
		 *
		 * @return the application identifier
		 */
		public String getCallbackUrl() {
			return this.callbackUrl;
		}

		/**
		 * This sets the OAuth URI
		 * @since 10.5.0
		 */
		public void setOAuthUri(final String key) {
			this.authUri = key;
		}

		/**
		 * This retrieves the OAuth URI
		 * @since 10.5.0
		 * @return the auth URI
		 */
		public String getOAuthUri() {
			return this.authUri;
		}

		/**
		 * This sets the server address
		 * @since 10.5.0
		 */
		public void setServerAddress(final String key) {
			this.serverAddress = key;
		}

		/**
		 * This retrieves the server address
		 * @since 10.5.0
		 * @return the auth URI
		 */
		public String getServerAddress() {
			return this.serverAddress;
		}
		/**
		 * This sets the service
		 * @since 10.5.0
		 */
		public void setService(final OAuthService key) {
			this.service = key;
		}

		/**
		 * This retrieves the service
		 * @since 10.5.0
		 * @return the auth URI
		 */
		public OAuthService getService() {
			return this.service;
		}
		
		/**
		 * This retrieves the operator from its name
		 *
		 * @return the operator
		 */
		public static Operator fromString(final String opname) {
			for (final Operator o : Operator.values()) {
				if (o.toString().compareToIgnoreCase(opname) == 0) {
					return o;
				}
			}
			throw new IndexOutOfBoundsException("unvalid Operator value " + opname);
		}
	}

	private Logger logger;

	static final long ONE_HOUR = 3600000L;
	static final long TWO_HOUR = ONE_HOUR * 2L;

	/**
	 * This is the path handled by this handler
	 */
	public static final String handlerPath = "/oauth2";
	/**
	 * This is the facebook operator name
	 */
	// https://www.facebook.com/dialog/oauth?
	// client_id=YOUR_APP_ID
	// &redirect_uri=YOUR_REDIRECT_URI
	// &state=SOME_ARBITRARY_BUT_UNIQUE_STRING
	public static final String OP_FACEBOOK = "Facebook";
	public static final String FACEBOOK_SERVER_ADDR = "www.facebook.com";

	/**
	 * These are the google informations
	 */
	// https://accounts.google.com/o/oauth2/auth?scope=https://www.googleapis.com/auth/userinfo.email&response_type=code&client_id=1078690626706-rb5lpg7bhofsbmklqtmgjcvfjch64s8r.apps.googleusercontent.com&redirect_uri=https://xwservpub.lal.in2p3.fr:4324/oauth
	public static final String OP_GOOGLE = "Google";
	public static final String GOOGLE_SERVER_ADDR = "www.google.com";

	/**
	 * These are the yahoo informations
	 */
	public static final String OP_YAHOO = "Yahoo";
	public static final String YAHOO_SERVER_ADDR = "www.yahoo.com";

	/**
	 * These are the twitter informations
	 */
	public static final String OP_TWITTER = "Twitter";
	public static final String TWITTER_SERVER_ADDR = "www.twitter.com";

	/**
	 * This is the client host name; for debug purposes only
	 */
	private String remoteName;
	/**
	 * This is the client IP addr; for debug purposes only
	 */
	private String remoteIP;
	/**
	 * This is the client port; for debug purposes only
	 */
	private int remotePort;

	/** this contains this server URL */
	private URL localRootUrl;

	private static HTTPOAuthHandler instance;

	/**
	 * @return the instance
	 */
	public static HTTPOAuthHandler getInstance() {
		return instance;
	}

	/**
	 * This is the default constructor which only calls super("HTTPStatHandler")
	 */
	public HTTPOAuthHandler() {
		super("HTTPOAuthHandler");
		if (instance != null) {
			return;
		}
		logger = new Logger(this);
		try {
			localRootUrl = new URL(Connection.HTTPSSLSCHEME + "://" + XWTools.getLocalHostName() + ":"
					+ Dispatcher.getConfig().getPort(Connection.HTTPSPORT));
		} catch (final MalformedURLException e) {
			XWTools.fatal(e.getMessage());
		}
		instance = this;

		Operator.FACEBOOK.setAppId(Dispatcher.getConfig().getProperty(XWPropertyDefs.FACEBOOKAPPID));
		Operator.FACEBOOK.setAppKey(Dispatcher.getConfig().getProperty(XWPropertyDefs.FACEBOOKAPPKEY));
		Operator.FACEBOOK.setOAuthUri(Dispatcher.getConfig().getProperty(XWPropertyDefs.FACEBOOKAUTHURI));
		Operator.FACEBOOK.setScope(Dispatcher.getConfig().getProperty(XWPropertyDefs.FACEBOOKSCOPE));
		Operator.FACEBOOK.setServerAddress(FACEBOOK_SERVER_ADDR);
		Operator.FACEBOOK.setCallbackUrl(Dispatcher.getConfig().getProperty(XWPropertyDefs.FACEBOOKCALLBACKURL));
		try {
			Operator.FACEBOOK.setService(new ServiceBuilder()
					.apiKey(Operator.FACEBOOK.getAppId())
					.apiSecret(Operator.FACEBOOK.getAppKey())
	                .callback(Operator.FACEBOOK.getCallbackUrl())
	                .scope(Operator.FACEBOOK.getScope())
	                .build(FacebookApi.instance()));
		}
		catch(final Exception e) {
			logger.warn(Operator.FACEBOOK.toString() + " : " + e.toString());
		}
		Operator.GOOGLE.setAppId(Dispatcher.getConfig().getProperty(XWPropertyDefs.GOOGLEAPPID));
		Operator.GOOGLE.setAppKey(Dispatcher.getConfig().getProperty(XWPropertyDefs.GOOGLEAPPKEY));
		Operator.GOOGLE.setOAuthUri(Dispatcher.getConfig().getProperty(XWPropertyDefs.GOOGLEAUTHURI));
		Operator.GOOGLE.setScope(Dispatcher.getConfig().getProperty(XWPropertyDefs.GOOGLESCOPE));
		Operator.GOOGLE.setServerAddress(GOOGLE_SERVER_ADDR);
		Operator.GOOGLE.setCallbackUrl(Dispatcher.getConfig().getProperty(XWPropertyDefs.GOOGLECALLBACKURL));
		try {
			Operator.GOOGLE.setService(new ServiceBuilder()
                .apiKey(Operator.GOOGLE.getAppId())
                .apiSecret(Operator.GOOGLE.getAppKey())
                .callback(Operator.GOOGLE.getCallbackUrl())
                .scope(Operator.GOOGLE.getScope())
                .build(GoogleApi20.instance()));
		}
		catch(final Exception e) {
			logger.warn(Operator.GOOGLE.toString() + " : " + e.toString());
		}

		Operator.TWITTER.setAppId(Dispatcher.getConfig().getProperty(XWPropertyDefs.TWITTERAPPID));
		Operator.TWITTER.setAppKey(Dispatcher.getConfig().getProperty(XWPropertyDefs.TWITTERAPPKEY));
		Operator.TWITTER.setOAuthUri(Dispatcher.getConfig().getProperty(XWPropertyDefs.TWITTERAUTHURI));
		Operator.TWITTER.setScope(Dispatcher.getConfig().getProperty(XWPropertyDefs.TWITTERSCOPE));
		Operator.TWITTER.setServerAddress(TWITTER_SERVER_ADDR);
		Operator.TWITTER.setCallbackUrl(Dispatcher.getConfig().getProperty(XWPropertyDefs.TWITTERCALLBACKURL));
		try {
			Operator.TWITTER.setService(new ServiceBuilder()
                .apiKey(Operator.TWITTER.getAppId())
                .apiSecret(Operator.TWITTER.getAppKey())
                .callback(Operator.TWITTER.getCallbackUrl())
                .scope(Operator.TWITTER.getScope())
                .build(TwitterApi.instance()));
		}
		catch(final Exception e) {
			logger.warn(Operator.TWITTER.toString() + " : " + e.toString());
		}

		Operator.YAHOO.setAppId(Dispatcher.getConfig().getProperty(XWPropertyDefs.YAHOOAPPID));
		Operator.YAHOO.setAppKey(Dispatcher.getConfig().getProperty(XWPropertyDefs.YAHOOAPPKEY));
		Operator.YAHOO.setOAuthUri(Dispatcher.getConfig().getProperty(XWPropertyDefs.YAHOOAUTHURI));
		Operator.YAHOO.setScope(Dispatcher.getConfig().getProperty(XWPropertyDefs.YAHOOSCOPE));
		Operator.YAHOO.setServerAddress(YAHOO_SERVER_ADDR);
		Operator.YAHOO.setCallbackUrl(Dispatcher.getConfig().getProperty(XWPropertyDefs.YAHOOCALLBACKURL));
		try {
			Operator.YAHOO.setService(new ServiceBuilder()
                .apiKey(Operator.YAHOO.getAppId())
                .apiSecret(Operator.YAHOO.getAppKey())
                .callback(Operator.YAHOO.getCallbackUrl())
                .scope(Operator.YAHOO.getScope())
                .build(YahooApi.instance()));
		}
		catch(final Exception e) {
			logger.warn(Operator.YAHOO.toString() + " : " + e.toString());
		}

		for (final Operator op : Operator.values()) {
			if (op.getService() == null) { 
				continue;
			}
			logger.info("" + op + " server   = " + op.getServerAddress());
			logger.info("" + op + " auth uri = " + op.getOAuthUri());
			logger.info("" + op + " scope    = " + op.getScope());
			logger.info("" + op + " app id   = " + op.getAppId());
			logger.info("" + op + " app key  = " + op.getAppKey());
			logger.info("" + op + " service  = " + op.getService());
		}
	}

	/**
	 * This constructor call the default constructor and sets the logger level
	 *
	 * @param l
	 *            is the logger level
	 */
	public HTTPOAuthHandler(final LoggerLevel l) {
		this();
		logger.setLoggerLevel(l);
	}

	/**
	 * This does nothing and must be overridden by any HTTP handler This is
	 * inherited from org.mortbay.jetty.Handler
	 */
	@Override
	public void setServer(final Server server) {
	}

	/**
	 * This does nothing and must be overridden by any HTTP handler This is
	 * inherited from org.mortbay.jetty.Handler
	 */
	@Override
	public Server getServer() {
		return null;
	}

	/**
	 * This does nothing and must be overridden by any HTTP handler This is
	 * inherited from org.mortbay.component.LifeCycle
	 *
	 * @return true
	 */
	@Override
	public boolean isFailed() {
		return true;
	}

	/**
	 * This does nothing and must be overridden by any HTTP handler This is
	 * inherited from org.mortbay.component.LifeCycle
	 *
	 * @return false
	 */
	@Override
	public boolean isRunning() {
		return false;
	}

	/**
	 * This does nothing and must be overridden by any HTTP handler This is
	 * inherited from org.mortbay.component.LifeCycle
	 *
	 * @return false
	 */
	@Override
	public boolean isStarted() {
		return false;
	}

	/**
	 * This does nothing and must be overridden by any HTTP handler This is
	 * inherited from org.mortbay.component.LifeCycle
	 *
	 * @return false
	 */
	@Override
	public boolean isStarting() {
		return false;
	}

	/**
	 * This does nothing and must be overridden by any HTTP handler This is
	 * inherited from org.mortbay.component.LifeCycle
	 *
	 * @return true
	 */
	@Override
	public boolean isStopped() {
		return true;
	}

	/**
	 * This does nothing and must be overridden by any HTTP handler This is
	 * inherited from org.mortbay.component.LifeCycle
	 *
	 * @return false
	 */
	@Override
	public boolean isStopping() {
		return false;
	}

	@Override
	public void removeLifeCycleListener(final Listener l) {

	}

	@Override
	public void addLifeCycleListener(final Listener l) {

	}

	/**
	 * This does nothing and must be overridden by any HTTP handler This is
	 * inherited from org.mortbay.component.LifeCycle
	 */
	@Override
	public void start() {
	}

	/**
	 * This cleans and closes communications
	 */
	public void close() {
		logger.debug("close");
	}

	/**
	 * This handles incoming connections. This is inherited from
	 * org.mortbay.jetty.Handler.
	 *
	 * @see xtremweb.communications.XWPostParams
	 */
	@Override
	public void handle(final String target, final Request baseRequest, final HttpServletRequest request,
			final HttpServletResponse response) throws IOException, ServletException {

		logger.debug("new connection");

		final String path = request.getPathInfo();
		try {
			logger.debug("Handling path info      = " + path);
			logger.debug("Handling target         = " + target);
			logger.debug("Handling request        = " + request.getContentLength() + " " + request.getContentType());
			logger.debug("Handling parameter size = " + request.getParameterMap().size());
			logger.debug("Handling query string   = " + request.getQueryString());
			logger.debug("Handling method         = " + request.getMethod());

			remoteName = request.getRemoteHost();
			remoteIP = request.getRemoteAddr();
			remotePort = request.getRemotePort();

			for (final Enumeration<String> e = request.getParameterNames(); e.hasMoreElements();) {
				final String pname = e.nextElement();
				logger.debug("parameter name " + pname);
				logger.debug("parameter value " + request.getParameter(pname));
			}
			for (final Enumeration<String> e = request.getHeaderNames(); e.hasMoreElements();) {
				logger.debug("header " + e.nextElement());
			}

			if (request.getParameterMap().size() > 0) {
				oauthRequest(request, response);
			}

			baseRequest.setHandled(true);
		} catch (final Exception e) {
			logger.exception(e);
		}
		response.getWriter().flush();
	}

	/**
	 * This handles XMLHTTPRequest
	 */
	private void oauthRequest(final HttpServletRequest request, final HttpServletResponse response)
			throws IOException, OAuthException {

		final String op = request.getParameter(XWPostParams.AUTH_OPERATOR.toString());
		if (op == null) {
			final String state = request.getParameter(XWPostParams.AUTH_STATE.toString());
			checkState(state);
			final Authentication auth = getAuthentication(request);
			final String url = localRootUrl + "?" + XWPostParams.AUTH_STATE + "=" + state
					+ request.getParameter(XWPostParams.AUTH_STATE.toString()) + "&" + XWPostParams.AUTH_EMAIL + "="
					+ auth.getEmail() + "&" + XWPostParams.AUTH_IDENTITY + "=" + auth.getIdentity();
			response.sendRedirect(url);
			return;
		}

		try {
			final Operator operator = Operator.fromString(op);
			logger.debug("operator = " + op);
			oauthRequestForOPerator(request, operator, response);
		} catch (final Exception e) {
			throw new OAuthException("Unsupported OP: " + op + "; " + e);
		}
	}

	/**
	 * This handles XMLHTTPRequest for Google delegation
	 *
	 * @param request
	 *            is the HTTP request
	 * @param operator
	 *            is the OAuth operator (google, facebook etc.)
	 * @since 9.1.1
	 */
	private void oauthRequestForOPerator(final HttpServletRequest request, final Operator operator,
			final HttpServletResponse response) throws IOException, OAuthException {
		if (operator == null) {
			throw new OAuthException("operator is null");
		}

		if (operator.getService() == null) {
			throw new OAuthException(operator.toString() + " : auth uri is null");
		}

		final String newState = newState(operator);
		logger.debug("newState = " + newState);

		final String oauthUrl = operator.getOAuthUri();
		logger.debug("oathUrl = " + oauthUrl);

		final HttpSession session = request.getSession(true);
		session.setAttribute(XWPostParams.AUTH_STATE.toString(), newState);
		session.setAttribute(XWPostParams.AUTH_OPERATOR.toString(), operator.toString());

//		response.sendRedirect(oauthUrl);
		if (operator.getService().getClass() == OAuth10aService.class) {
			response.sendRedirect(((OAuth10aService)operator.getService()).getAuthorizationUrl(null));
		}
		if (operator.getService().getClass() == OAuth20Service.class) {
			response.sendRedirect(((OAuth20Service)operator.getService()).getAuthorizationUrl(null));
		}
	}

	/**
	 * This generates a new state (a random string) and stores it in stateDb
	 *
	 * @return the new generated state
	 */
	private String newState(final Operator op) {
		final MD5 md5 = new MD5(op.getAppId() + System.currentTimeMillis() + Math.random());
		final String ret = md5.asHex();
		storeState(ret);
		return ret;
	}

	/**
	 * This retrieves authentication from openid server response
	 *
	 * @param request
	 *            is the HTTP request
	 * @return the authentication if found; null otherwise
	 */
	private Authentication getAuthentication(final HttpServletRequest request) throws OAuthException {
		final HttpSession session = request.getSession(false);

		String email = request.getParameter(XWPostParams.AUTH_EMAIL.toString());
		String id = request.getParameter(XWPostParams.AUTH_IDENTITY.toString());

		if ((email == null) && (session != null)) {
			email = (String) session.getAttribute(XWPostParams.AUTH_EMAIL.toString());
		}
		if ((id == null) && (session != null)) {
			id = (String) session.getAttribute(XWPostParams.AUTH_IDENTITY.toString());
		}

		if ((email == null) || (id == null)) {
			throw new OAuthException("can't retrieve authentication informations");
		}
		final Authentication authentication = new Authentication();
		authentication.setEmail(email);
		authentication.setIdentity(id);
		return authentication;
	}

	/**
	 * This simulates a database that store all states:
	 */
	private final Set<String> stateDb = new HashSet<String>();

	/**
	 * This tests if state exist in database:
	 *
	 * @param state
	 * @throws OAuthException
	 *             is thrown if provided state does not exist
	 */
	void checkState(final String state) throws OAuthException {
		if (stateDb.contains(state) == false) {
			throw new OAuthException("invalid state");
		}
	}

	/**
	 * This tests if state exist in database:
	 *
	 * @param state
	 * @return true if state exists
	 */
	boolean stateExists(final String state) {
		return stateDb.contains(state);
	}

	/**
	 * This stores state in database, if not already stored
	 *
	 * @param state
	 *            is the state to store
	 */
	void storeState(final String state) {
		if (stateDb.contains(state) == false) {
			stateDb.add(state);
		}
	}

	/**
	 * This inserts all known CA certificates to the provided keystore
	 *
	 * @param store
	 *            is the keystore to add certificate to
	 * @return this returns null, if parameter is null; else this returns the
	 *         keystore filled with some new entries
	 * @since 8.0.2
	 */
	public static KeyStore setCACertificateEntries(final KeyStore store) {
		if (store == null) {
			return null;
		}
		final Logger logger = new Logger();
		try {
			for (final Operator op : Operator.values()) {
				final X509Certificate[] gcerts = XWTools.retrieveCertificates(op.getServerAddress(), false);
				for (int i = 0; i < gcerts.length; i++) {
					final X509Certificate cert = gcerts[i];
					try {
						final String alias = cert.getSubjectDN().toString();
						logger.finest("KeyStore set entry= " + alias + "; KeyStore.size = " + store.size());
						store.setCertificateEntry(alias, cert);
					} catch (final Exception e) {
						logger.exception("Can't add new entry to keystore", e);
					}
				}
			}
		} catch (final Exception e) {
			logger.exception("Can't add new entry to keystore", e);
		}
		return store;
	}

	/**
	 * This is for testing only
	 *
	 * @param args
	 * @throws Exception
	 */
	public static void main(final String[] args) throws Exception {
		int i = 0;
		final boolean sav = (args.length <= 0 ? false : (args[0].compareToIgnoreCase("--sav") == 0));
		if (sav) {
			i = 1;
		}
		for (; i < args.length; i++) {
			XWTools.retrieveCertificates(args[i], sav);
		}
	}
}
