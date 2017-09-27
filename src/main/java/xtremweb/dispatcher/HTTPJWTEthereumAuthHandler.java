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
import java.io.UnsupportedEncodingException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import org.eclipse.jetty.server.Request;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import xtremweb.common.LoggerLevel;
import xtremweb.communications.XWPostParams;

/**
 * This handles HTTP request to /ethauth/. This accepts and verifies ethereum
 * public address
 *
 * @author Oleg Lodygensky
 * @since XWHEP 11.0.0
 */

public class HTTPJWTEthereumAuthHandler extends HTTPJWTHandler {

	public static final String handlerPath = "/ethauth";
	/**
	 * This is the name of the cookie containing the JWT
	 */
	private static final String COOKIE_NAME = "ethauthtoken";
	/**
	 * This is the JWT claim containing the ethereum address
	 */
	private static final String CLAIM_NAME = "blockchainaddr";

	/**
	 * This is the default constructor which only calls super("HTTPStatHandler")
	 * 
	 * @throws UnsupportedEncodingException
	 * @throws IllegalArgumentException
	 */
	public HTTPJWTEthereumAuthHandler() throws IllegalArgumentException, UnsupportedEncodingException {
		super("HTTPEthereumAuthHandler");
	}

	/**
	 * This constructor call the default constructor and sets the logger level
	 *
	 * @param l
	 *            is the logger level
	 * @throws UnsupportedEncodingException
	 * @throws IllegalArgumentException
	 */
	public HTTPJWTEthereumAuthHandler(LoggerLevel l) throws IllegalArgumentException, UnsupportedEncodingException {
		this();
		logger.setLoggerLevel(l);
	}

	/**
	 * This handles XMLHTTPRequest
	 * 
	 * @throws JWTVerificationException
	 * @throws IllegalArgumentException
	 * @throws IOException
	 */
	@Override
	final protected void jwtRequest(Request baseRequest)
			throws IllegalArgumentException, JWTVerificationException, IOException {

		final HttpSession session = request.getSession(true);
		logger.debug("session = " + session.getId());
		final Cookie cookie = getCookie(COOKIE_NAME);
		String strState = (String) session.getAttribute(XWPostParams.AUTH_STATE.toString());
		final DecodedJWT token = cookie != null ? getToken(cookie) : getToken(getState(strState));

		if (token == null) {
			baseRequest.setHandled(false);
			throw new JWTVerificationException("JWT not found");
		}
		logger.debug("ADDR = " + getEthereumAddress(token));

		if (cookie != null) {
			final String newState = newState(cookie);
			logger.debug("newState = " + newState);
			session.setAttribute(XWPostParams.AUTH_STATE.toString(), newState);
			storeState(newState, cookie);
			strState = newState;
		}

		final String url = localRootUrl + "?" + XWPostParams.AUTH_STATE + "=" + strState;
		logger.debug("sendRedirectUrm = " + url);
		response.sendRedirect(url);

		baseRequest.setHandled(true);
	}

	public String getEthereumAddress(final DecodedJWT token) {
		return token.getClaim(CLAIM_NAME).asString();
	}

	/**
	 * This is for testing only
	 *
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
	}
}
