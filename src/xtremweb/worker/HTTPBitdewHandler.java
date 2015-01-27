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

package xtremweb.worker;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;

import xtremweb.common.Logger;
import xtremweb.common.LoggerLevel;
import xtremweb.common.Table;
import xtremweb.common.UID;
import xtremweb.communications.CommClient;
import xtremweb.security.XWAccessRights;

/**
 * This handles incoming communications through HTTP. This class aims to help
 * the worker owner to configure its worker. This owner can configure its worker
 * via a web browser.<br />
 * 
 * Created: Octobre 2007
 * 
 * @author Oleg Lodygensky
 * @version XWHEP 1.0.0
 */

public class HTTPBitdewHandler extends Thread implements
		Handler {

	private Logger logger;

	private HttpServletRequest request;
	private HttpServletResponse response;

	/**
	 *  This is the HTML parameter to set this worker shared package name
	 * @since 10.0.0 
	 */
	private final static String DATAPACKAGENAME = "datapackagename";
	/** 
	 * This is the HTML parameter to set the shared package path
	 * @since 10.0.0 
	 */
	private final static String DATAPACKAGEPATH = "datapackagepath";
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

	/**
	 * This is the default constructor which only calls super("HTTPStatHandler")
	 */
	public HTTPBitdewHandler() {
		super("HTTPBitdewHandler");
		logger = new Logger(this);
	}

	/**
	 * This constructor call the default constructor and sets the logger level
	 * 
	 * @param l
	 *            is the logger level
	 */
	public HTTPBitdewHandler(LoggerLevel l) {
		this();
		logger.setLoggerLevel(l);
	}

	/**
	 * This does nothing and must be overidden by any HTTP handler This is
	 * inherited from org.mortbay.jetty.Handler
	 */
	public void setServer(Server server) {
	}

	/**
	 * This does nothing and must be overidden by any HTTP handler This is
	 * inherited from org.mortbay.jetty.Handler
	 */
	public Server getServer() {
		return null;
	}

	/**
	 * This does nothing and must be overidden by any HTTP handler This is
	 * inherited from org.mortbay.component.LifeCycle
	 * 
	 * @return true
	 */
	public boolean isFailed() {
		return true;
	}

	/**
	 * This does nothing and must be overidden by any HTTP handler This is
	 * inherited from org.mortbay.component.LifeCycle
	 * 
	 * @return false
	 */
	public boolean isRunning() {
		return false;
	}

	/**
	 * This does nothing and must be overidden by any HTTP handler This is
	 * inherited from org.mortbay.component.LifeCycle
	 * 
	 * @return false
	 */
	public boolean isStarted() {
		return false;
	}

	/**
	 * This does nothing and must be overidden by any HTTP handler This is
	 * inherited from org.mortbay.component.LifeCycle
	 * 
	 * @return false
	 */
	public boolean isStarting() {
		return false;
	}

	/**
	 * This does nothing and must be overidden by any HTTP handler This is
	 * inherited from org.mortbay.component.LifeCycle
	 * 
	 * @return true
	 */
	public boolean isStopped() {
		return true;
	}

	/**
	 * This does nothing and must be overidden by any HTTP handler This is
	 * inherited from org.mortbay.component.LifeCycle
	 * 
	 * @return false
	 */
	public boolean isStopping() {
		return false;
	}

	/**
	 * This does nothing and must be overidden by any HTTP handler This is
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

	public void removeLifeCycleListener(Listener l) {
		
	}
	public void addLifeCycleListener(Listener l) {
		
	}

	/**
	 * This handles incoming connections. This is inherited from
	 * org.mortbay.jetty.Handler.
	 * 
	 * @see xtremweb.communications.XWPostParams
	 */
	public void handle(String target,
            Request baseRequest,
            HttpServletRequest _request,
            HttpServletResponse _response) throws IOException,
			ServletException {

		request = _request;
		response = _response;

		final String path = request.getPathInfo();
		try {
			logger.debug("Handling target         = " + target);
			logger.debug("Handling request        = "
					+ request.getContentLength() + " "
					+ request.getContentType());
			logger.debug("Handling parameter size = "
					+ request.getParameterMap().size());
			logger.debug("Handling query string   = "
					+ request.getQueryString());
			logger.debug("Handling path info      = " + path);
			logger.debug("Handling method         = "
					+ request.getMethod());

			logger.info("HTTPBitdewHandler new connection " + path);

			remoteName = request.getRemoteHost();
			remoteIP = request.getRemoteAddr();
			remotePort = request.getRemotePort();

			if (request.getParameterMap().size() > 0) {
				bitdewRequest();
				baseRequest.setHandled(true);
				return;
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
	private void bitdewRequest() throws IOException {
		try {
			final String pkgName = request.getParameter(DATAPACKAGENAME);
			if (pkgName == null) {
				return;
			}
			Worker.getConfig().getHost().setSharedPackages(pkgName);
			final String pkgPath = request.getParameter(DATAPACKAGEPATH);
			if (pkgPath == null) {
				return;
			}
			Worker.getConfig().setPackageDir(pkgName, pkgPath);
		} catch (final Exception e) {
			logger.exception("Can't manage data package", e);
		}
	}
}
