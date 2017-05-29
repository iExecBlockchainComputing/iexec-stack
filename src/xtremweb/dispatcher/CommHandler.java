/**
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

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.security.AccessControlException;
import java.security.InvalidKeyException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;

import xtremweb.common.AppInterface;
import xtremweb.common.CommonVersion;
import xtremweb.common.DataInterface;
import xtremweb.common.GroupInterface;
import xtremweb.common.HostInterface;
import xtremweb.common.Logger;
import xtremweb.common.MileStone;
import xtremweb.common.SessionInterface;
import xtremweb.common.StatusEnum;
import xtremweb.common.Table;
import xtremweb.common.TaskInterface;
import xtremweb.common.TraceInterface;
import xtremweb.common.UID;
import xtremweb.common.UserGroupInterface;
import xtremweb.common.UserInterface;
import xtremweb.common.UserRightEnum;
import xtremweb.common.Version;
import xtremweb.common.WorkInterface;
import xtremweb.common.WorkerParameters;
import xtremweb.common.XMLHashtable;
import xtremweb.common.XMLValue;
import xtremweb.common.XMLVector;
import xtremweb.common.XMLable;
import xtremweb.common.XWConfigurator;
import xtremweb.common.XWPropertyDefs;
import xtremweb.common.XWReturnCode;
import xtremweb.communications.AccessLogger;
import xtremweb.communications.CommServer;
import xtremweb.communications.Connection;
import xtremweb.communications.IdRpc;
import xtremweb.communications.URI;
import xtremweb.communications.XMLRPCCommand;
import xtremweb.communications.XMLRPCCommandActivateHost;
import xtremweb.communications.XMLRPCCommandChmod;
import xtremweb.communications.XMLRPCCommandDownloadData;
import xtremweb.communications.XMLRPCCommandGet;
import xtremweb.communications.XMLRPCCommandGetGroupWorks;
import xtremweb.communications.XMLRPCCommandGetUserByLogin;
import xtremweb.communications.XMLRPCCommandGetWorks;
import xtremweb.communications.XMLRPCCommandRemove;
import xtremweb.communications.XMLRPCCommandUploadData;
import xtremweb.communications.XMLRPCCommandWorkAliveByUID;
import xtremweb.communications.XMLRPCResult;
import xtremweb.communications.XWPostParams;
import xtremweb.security.XWAccessRights;

/**
 * CommHandler.java This Class launches several communication handler over
 * different "media" like RMI, UDP, SSL, custom, For now uses only RMI
 *
 * Created: Sun Jul 9 17:39:06 2000
 *
 * @author Gilles Fedak
 * @version %I% %G%
 */

public abstract class CommHandler extends Thread implements xtremweb.communications.CommHandler {

	private final Logger logger;
	private final String URINOTSET = "uri not set";

	/**
	 * @return the logger
	 */
	public Logger getLogger() {
		return logger;
	}

	/**
	 * This aims to display some time stamps
	 */
	private final MileStone mileStone;

	protected void mileStone(final String msg) {
		mileStone.println(msgWithRemoteAddresse(msg));
	}

	/** 10 sec */
	private int tracesResultDelay = 10000;
	/**
	 * equals 60 60 * tracesResultDelay = 10 mn
	 */
	private int tracesSendResultDelay = 60;

	private static final XMLVector NOANSWER = new XMLVector(new ArrayList());
	private static final Version CURRENTVERSION = CommonVersion.getCurrent();
	private static final String CURRENTVERSIONSTRING = CURRENTVERSION.toString();

	/**
	 * This is the server that received incoming communication This is used to
	 * push this communication handler back to server pool
	 */
	private CommServer commServer;

	/**
	 * This retrieves the communication server
	 *
	 * @return the commServer
	 */
	public CommServer getCommServer() {
		return commServer;
	}

	/**
	 * This sets the communication server
	 *
	 * @param s
	 *            the commServer to set
	 */
	@Override
	public void setCommServer(final CommServer s) {
		commServer = s;
	}

	/**
	 * This is the remote host name
	 */
	private String remoteName;

	/**
	 * This resets remote host name
	 *
	 * @since 8.2.0
	 */
	public void resetRemoteName() {
		remoteName = null;
	}

	/**
	 * This retrieves remote host name
	 *
	 * @return the remoteName
	 */
	public String getRemoteName() {
		return remoteName;
	}

	/**
	 * This sets the remote host name
	 *
	 * @param remoteName
	 *            the remoteName to set
	 */
	public void setRemoteName(final String remoteName) {
		this.remoteName = remoteName;
	}

	/**
	 * This is the client IP address
	 */
	private String remoteIP;

	/**
	 * This resets the remote host IP address
	 *
	 * @since 8.2.0
	 */
	public void resetRemoteIP() {
		remoteIP = null;
	}

	/**
	 * This retrieves the remote host IP address
	 *
	 * @return the remoteIP
	 */
	public String getRemoteIP() {
		return remoteIP;
	}

	/**
	 * This sets the remote host IP address
	 *
	 * @param remoteIP
	 *            the remoteIP to set
	 */
	public void setRemoteIP(final String remoteIP) {
		this.remoteIP = remoteIP;
	}

	/**
	 * This is the remote host port
	 */
	private int remotePort;

	/**
	 * This retrives the remote host port
	 *
	 * @return the remotePort
	 */
	public int getRemotePort() {
		return remotePort;
	}

	/**
	 * This sets the remote host port
	 *
	 * @param remotePort
	 *            the remotePort to set
	 */
	public void setRemotePort(final int remotePort) {
		this.remotePort = remotePort;
	}

	/**
	 * This resets the remote host port
	 *
	 * @since 8.2.0
	 */
	public void resetRemotePort() {
		remotePort = -1;
	}

	/**
	 * This has been constructed from config file
	 */
	private XWConfigurator config;

	/**
	 * @return the config
	 */
	public XWConfigurator getConfig() {
		return config;
	}

	/**
	 * This is the running semaphore
	 */
	private final boolean isRunning;

	/**
	 * constructor
	 */
	protected CommHandler(final String name) {
		super(name);
		logger = new Logger(this);
		logger.debug(name);
		mileStone = new MileStone(xtremweb.dispatcher.CommHandler.class);
		config = Dispatcher.getConfig();
		isRunning = false;
	}

	protected CommHandler(final String name, final XWConfigurator c) {
		this(name);
		config = c;
	}

	public String remoteAddresse() {
		return "{" + remoteName + "/" + remoteIP + ":" + remotePort + "}";
	}

	protected String msgWithRemoteAddresse(final String msg) {
		return remoteAddresse() + " : " + msg;
	}

	/**
	 * This prefixes the finest message with the remote host name and port
	 */
	public void finest(final String msg) {
		logger.finest(msgWithRemoteAddresse(msg));
	}

	/**
	 * This prefixes the debug message with the remote host name and port
	 */
	public void debug(final String msg) {
		logger.debug(msgWithRemoteAddresse(msg));
	}

	/**
	 * This prefixes the info message with the remote host name and port
	 */
	public void info(final String msg) {
		logger.info(msgWithRemoteAddresse(msg));
	}

	/**
	 * This prefixes the warning message with the remote host name and port
	 */
	public void warn(final String msg) {
		logger.warn(msgWithRemoteAddresse(msg));
	}

	/**
	 * This prefixes the error message with the remote host name and port
	 */
	public void error(final String msg) {
		logger.error(msgWithRemoteAddresse(msg));
	}

	/**
	 * This prefixes the error message with the remote host name and port
	 */
	public void error(final Exception e) {
		logger.exception(msgWithRemoteAddresse(""), e);
	}

	/**
	 * This prefixes the error message with the remote host name and port
	 */
	public void error(final String msg, final Exception e) {
		logger.exception(msgWithRemoteAddresse(msg), e);
	}

	/**
	 * This does nothing and must be overridden by any HTTP handler This is
	 * inherited from org.mortbay.jetty.Handler
	 */
	@Override
	public void handle(final String target, final Request baseRequest, final HttpServletRequest _request,
			final HttpServletResponse _response) throws IOException, ServletException {
	}

	/**
	 * This does nothing and must be overidden by any HTTP handler This is
	 * inherited from org.mortbay.jetty.Handler
	 */
	@Override
	public void setServer(final Server server) {
		warn("" + this + " CommHandler#serServer(server = " + server + ") does nothing ");
	}

	/**
	 * This does nothing and must be overidden by any HTTP handler This is
	 * inherited from org.mortbay.jetty.Handler
	 */
	@Override
	public Server getServer() {
		return null;
	}

	/**
	 * This does nothing and must be overidden by any HTTP handler This is
	 * inherited from org.mortbay.component.LifeCycle
	 *
	 * @return true
	 */
	@Override
	public boolean isFailed() {
		return true;
	}

	/**
	 * This does nothing and must be overidden by any HTTP handler This is
	 * inherited from org.mortbay.component.LifeCycle
	 *
	 * @return false
	 */
	@Override
	public boolean isRunning() {
		return isRunning;
	}

	/**
	 * This does nothing and must be overidden by any HTTP handler This is
	 * inherited from org.mortbay.component.LifeCycle
	 *
	 * @return false
	 */
	@Override
	public boolean isStarted() {
		return false;
	}

	/**
	 * This does nothing and must be overidden by any HTTP handler This is
	 * inherited from org.mortbay.component.LifeCycle
	 *
	 * @return false
	 */
	@Override
	public boolean isStarting() {
		return false;
	}

	/**
	 * This does nothing and must be overidden by any HTTP handler This is
	 * inherited from org.mortbay.component.LifeCycle
	 *
	 * @return true
	 */
	@Override
	public boolean isStopped() {
		return true;
	}

	@Override
	public void removeLifeCycleListener(final Listener l) {

	}

	@Override
	public void addLifeCycleListener(final Listener l) {

	}

	/**
	 * This does nothing and must be overidden by any HTTP handler This is
	 * inherited from org.mortbay.component.LifeCycle
	 *
	 * @return false
	 */
	@Override
	public boolean isStopping() {
		return false;
	}

	/**
	 * This write an object to output channel
	 */
	protected abstract void write(XMLable obj) throws IOException;

	/**
	 * This writes a file to output channel
	 */
	protected abstract void writeFile(File f) throws IOException;

	/**
	 * This read a file from input channel
	 */
	protected abstract void readFile(File f) throws IOException;

	/**
	 * @see xtremweb.communications.CommHandler#setSocket(Socket)
	 */
	@Override
	public synchronized void resetSockets() throws IOException, InvalidKeyException, AccessControlException {
		logger.error("resetSockets not implemented");
	}

	/**
	 * This is called by run() from inherited classes. This do not close
	 * communication channel. This pushes connection back to commserver channel
	 * stack
	 *
	 * @param command
	 *            is the command received from communication channel
	 */
	protected void run(final XMLRPCCommand command) {

		if (command == null) {
			error("CMD is null");
			return;
		}

		IdRpc idRpc = null;

		int accessStatus = 200;
		final int accessAnswerSize = 0;
		XMLable result = null;
		long resultSize = 0;
		final UserInterface user = command.getUser();

		try {

			idRpc = command.getIdRpc();

			final String newConnection = "<newConnection IDRPC='" + idRpc.toString() + "'>";
			debug(msgWithRemoteAddresse(newConnection) + " " + command.toXml());
			mileStone(newConnection);

			switch (idRpc) {
			case DISCONNECT:
				disconnect(user);
				result = NOANSWER;
				break;
			case SHUTDOWN:
				shutDown(user, command.getHost());
				result = NOANSWER;
				break;
			case REMOVE: {
				final URI uri = ((XMLRPCCommandRemove) command).getURI();
				remove(user, uri);
				result = NOANSWER;
				break;
			}
			case VERSION:
				result = CURRENTVERSION;
				break;
			case GET: {
				final URI uri = ((XMLRPCCommandGet) command).getURI();
				result = get(user, uri);
				break;
			}
			case GETTASK: {
				final URI uri = ((XMLRPCCommandGet) command).getURI();
				result = getTask(user, uri);
				break;
			}
			case GETAPPS:
				result = getApps(user);
				break;
			case SEND:
				if (command.getParameter() == null) {
					throw new IOException("Can't retrieve command parameter");
				}
				try {
					final WorkInterface witf = (WorkInterface) command.getParameter();
					final HostInterface hitf = command.getHost();
					sendWork(user, hitf, witf);
					break;
				} catch (final ClassCastException e) {
				}
				try {
					final DataInterface ditf = (DataInterface) command.getParameter();
					sendData(user, ditf);
					break;
				} catch (final ClassCastException e) {
				}
				try {
					final GroupInterface gitf = (GroupInterface) command.getParameter();
					sendGroup(user, gitf);
					break;
				} catch (final ClassCastException e) {
				}
				try {
					final SessionInterface sitf = (SessionInterface) command.getParameter();
					sendSession(user, sitf);
					break;
				} catch (final ClassCastException e) {
				}
				try {
					final AppInterface aitf = (AppInterface) command.getParameter();
					sendApp(user, aitf);
					break;
				} catch (final ClassCastException e) {
				}
				try {
					final UserInterface uitf = (UserInterface) command.getParameter();
					sendUser(user, uitf);
					break;
				} catch (final ClassCastException e) {
				}
				try {
					final UserGroupInterface ugitf = (UserGroupInterface) command.getParameter();
					sendUserGroup(user, ugitf);
					break;
				} catch (final ClassCastException e) {
				}
				try {
					final TraceInterface titf = (TraceInterface) command.getParameter();
					sendTrace(user, titf);
					break;
				} catch (final ClassCastException e) {
				}
				throw new IOException("Insertion error: bad object type");
			case SENDAPP:
				final AppInterface aitf = (AppInterface) command.getParameter();
				sendApp(user, aitf);
				break;
			case GETDATAS:
				result = getDatas(user);
				break;
			case SENDDATA:
				final DataInterface ditf = (DataInterface) command.getParameter();
				sendData(user, ditf);
				break;
			case UPLOADDATA: {
				final URI uri = ((XMLRPCCommandUploadData) command).getURI();
				resultSize = uploadData(user, uri);
				result = NOANSWER;
				break;
			}
			case DOWNLOADDATA: {
				final URI uri = ((XMLRPCCommandDownloadData) command).getURI();
				resultSize = downloadData(user, uri);
				result = NOANSWER;
				break;
			}
			case GETGROUPS:
				result = getGroups(user);
				break;
			case GETGROUPWORKS: {
				final URI uri = ((XMLRPCCommandGetGroupWorks) command).getURI();
				result = getGroupWorks(user, uri);
				break;
			}
			case SENDGROUP:
				final GroupInterface gitf = (GroupInterface) command.getParameter();
				sendGroup(user, gitf);
				break;
			case ACTIVATEHOST: {
				final URI uri = ((XMLRPCCommandActivateHost) command).getURI();
				activateWorker(user, uri, ((XMLRPCCommandActivateHost) command).getActivation());
				break;
			}
			case CHMOD: {
				final URI uri = ((XMLRPCCommandChmod) command).getURI();
				chmod(user, uri, ((XMLRPCCommandChmod) command).getModifier());
				break;
			}
			case GETHUBADDR:
				Hashtable<String, String> reth = getHubAddr(user);
				result = new XMLHashtable(reth);
				reth = null;
				break;
			case GETHOSTS:
				result = getWorkers(user);
				break;
			case GETSESSIONS:
				result = getSessions(user);
				break;
			case SENDSESSION:
				final SessionInterface sitf = (SessionInterface) command.getParameter();
				sendSession(user, sitf);
				break;
			case GETTASKS:
				result = getTasks(user);
				break;
			case GETTRACES:
				result = getTraces(user);
				break;
			case SENDTRACE:
				final TraceInterface titf = (TraceInterface) command.getParameter();
				sendTrace(user, titf);
				break;
			case SENDUSER:
				final UserInterface uitf = (UserInterface) command.getParameter();
				sendUser(user, uitf);
				break;
			case GETUSERBYLOGIN:
				String login = ((XMLRPCCommandGetUserByLogin) command).getLogin();
				result = getUser(user, login);
				login = null;
				break;
			case GETUSERS:
				result = getUsers(user);
				break;
			case GETUSERGROUPS:
				result = getUserGroups(user);
				break;
			case SENDUSERGROUP:
				final UserGroupInterface ugitf = (UserGroupInterface) command.getParameter();
				sendUserGroup(user, ugitf);
				break;
			case GETWORKS:
				final StatusEnum status = ((XMLRPCCommandGetWorks) command).getStatus();
				result = getWorks(user, status);
				break;
			case SENDWORK: {
				final WorkInterface witf = (WorkInterface) command.getParameter();
				final HostInterface hitf = command.getHost();
				sendWork(user, hitf, witf);
				break;
			}
			case BROADCASTWORK: {
				final WorkInterface witf = (WorkInterface) command.getParameter();
				broadcast(user, witf);
				result = NOANSWER;
				break;
			}
			case WORKREQUEST: {
				final HostInterface hitf = command.getHost();
				result = workRequest(user, hitf);
				break;
			}
			case WORKALIVEBYUID: {
				final URI uri = ((XMLRPCCommandWorkAliveByUID) command).getURI();
				final HostInterface hitf = command.getHost();
				final Hashtable hash = workAlive(user, hitf, uri);
				if (hitf == null) {
					throw new IOException("host is not set");
				}
				result = new XMLHashtable(hash);
				break;
			}
			case WORKALIVE:
				// here is a big hack to force "old" worker to update
				if (command.getCurrentVersion() == null) {
					warn("Forcing worker to update " + command.getHost().getUID() + " (" + command.getHost().getName()
							+ ")");
					final Hashtable h = new Hashtable();
					h.put("currentversion", CURRENTVERSIONSTRING);
					result = new XMLHashtable(h);
				} else {
					final HostInterface hitf = command.getHost();
					final Hashtable h2 = ((XMLHashtable) command.getParameter()).getHashtable();
					Hashtable ret = workAlive(user, hitf, h2);
					result = new XMLHashtable(ret);
					ret = null;
				}
				break;
			case PING:
				result = NOANSWER;
				break;

			default:
				error("BAD Id: " + idRpc);
				accessStatus = 404;
				break;
			}
		} catch (final EOFException e) {
			error(e);
		} catch (final SocketException e) {
			error(e);
		} catch (final IOException e) {
			error("Disk access error " + e);
			result = new XMLRPCResult(XWReturnCode.DISK, e.getMessage());
		} catch (final InvalidKeyException e) {
			error("Right access error " + e);
			result = new XMLRPCResult(XWReturnCode.AUTHENTICATION, e.getMessage());
			accessStatus = 401;
		} catch (final AccessControlException e) {
			error("Right access error " + e);
			result = new XMLRPCResult(XWReturnCode.AUTHORIZATION, e.getMessage());
			accessStatus = 403;
		} catch (final Exception e) {
			error("Cannot get io socket ", e);
		} finally {
			if (result == null) {
				result = NOANSWER;
			}
			String resultxml = result.toXml();
			resultxml = resultxml.substring(0, Math.min(resultxml.length(), 150));
			try {
				if (command.getCurrentVersion() == null) {
					result.resetCurrentVersion();
				} else {
					if (result.getCurrentVersion() == null) {
						result.setCurrentVersion();
					}
				}

				// if (idRpc != IdRpc.SEND) {
				debug("answer (" + idRpc.toString() + ") " + resultxml);
				// }
				write(result);
			} catch (final Exception e) {
				logger.exception("Can't write result", e);
			} finally {
				resultxml = null;
			}
			mileStone("</newConnection>");
		}

		try {
			String accessProto = null;
			if (this instanceof TCPHandler) {
				accessProto = "TCP";
			}
			if (this instanceof UDPHandler) {
				accessProto = "UDP";
			}
			if (this instanceof HTTPHandler) {
				accessProto = "HTTP";
			}

			final URI accessuri = command.getURI();
			if (accessuri != null) {
				final String accessPath = accessuri.toString();
				final String login = user != null ? user.getLogin() : "-";
				String os = null;
				try {
					os = command.getHost().getOs().toString();
				} catch (final Exception e) {
					os = "-";
				}
				AccessLogger.getInstance().println(accessPath, login, accessProto, accessStatus,
						result != NOANSWER ? result.toXml().length() : resultSize, remoteName, os, idRpc);
			}
		} catch (final Exception e) {
			error(e);
		}

		result = null;
		idRpc = null;

	}

	/**
	 * Set workers running parameters.
	 *
	 * @param client
	 *            is the client identifier
	 * @param nbWorkers
	 *            is the expected number of workers to activate.
	 * @param p
	 *            are the parameters to set
	 * @return the number of activated workers, -1 on error
	 */
	public synchronized int setWorkersParameters(final UserInterface client, final int nbWorkers,
			final WorkerParameters p) throws IOException, InvalidKeyException, AccessControlException {

		mileStone("<setWorkersParameters>");

		final int ret = DBInterface.getInstance().activateWorkers(client, nbWorkers);
		tracesSendResultDelay = p.getSendResultDelay();
		tracesResultDelay = p.getResultDelay();

		mileStone("</setWorkersParameters");

		notifyAll();
		return ret;
	}

	/**
	 * Get workers running parameters.
	 *
	 * @return workers parameters
	 */
	public WorkerParameters getWorkersParameters(final UserInterface client) {

		final WorkerParameters params = new WorkerParameters();
		params.setSendResultDelay(tracesSendResultDelay);
		params.setResultDelay(tracesResultDelay);
		return params;
	}

	/**
	 * Call to the scheduler to select a work
	 *
	 * @return a Description of the Work the server has to complete
	 */
	public synchronized WorkInterface workRequest(final UserInterface _user, final HostInterface _host)
			throws IOException, InvalidKeyException, AccessControlException {

		Exception excpt = null;
		WorkInterface itf = null;

		mileStone(("<workRequest host=" + _host) != null ? _host.getName() : "null" + ">");
		try {
			final UserInterface user = DBInterface.getInstance().checkClient(_user, UserRightEnum.GETJOB);

			_host.setIPAddr(remoteIP);
			final HostInterface host = DBInterface.getInstance().hostRegister(user, _host);

			if (host == null) {
				throw new IOException("can't register host");
			}
			if (host.isActive() && host.getVersion().equals(CURRENTVERSIONSTRING)) {
				itf = Dispatcher.getScheduler().select(host, user);
			}
		} catch (final Exception e) {
			excpt = e;
		} finally {
			if (excpt != null) {
				logger.exception("workRequest error", excpt);
			}

			if (excpt != null) {
				if (excpt instanceof InvalidKeyException) {
					throw (InvalidKeyException) excpt;
				} else {
					throw new IOException(excpt.getMessage());
				}
			}
			if (itf == null) {
				debug(("worker " + _host) != null ? _host.getName() : "null" + " gets nothing");
			}
			mileStone("</workRequest>");
			notifyAll();
		}
		return itf;
	}

	/**
	 */
	public void shutDown(final UserInterface client, final HostInterface host)
			throws IOException, InvalidKeyException, AccessControlException {
		throw new IOException("shutdown not implemented");
		// mileStone("<shutdown>");
		// DBInterface.getInstance().shutDown(client, host);
		// mileStone("</shutdown>");
	}

	/**
	 * This is sent by the worker for each job it has. This is not called if the
	 * worker has no job.
	 *
	 * This gets current job UID from worker. This sends a boolean back to
	 * worker wich tells whether to stop computing this jobs TRUE to continue
	 * and FALSE to stop
	 *
	 * @param uri
	 *            is the URI of the computing job on worker side
	 * @return a hashtable containing this job status so that worker continue or
	 *         stop computing it
	 * @since 5.8.0
	 */
	public Hashtable workAlive(final UserInterface _user, final HostInterface _host, final URI uri)
			throws IOException, InvalidKeyException, AccessControlException {
		if (uri == null) {
			throw new IOException(URINOTSET);
		}
		return workAlive(_user, _host, uri.getUID());
	}

	/**
	 * This is sent by the worker for each job it has. This is not called if the
	 * worker has no job.
	 *
	 * This gets current job UID from worker. This sends a boolean back to
	 * worker wich tells whether to stop computing this jobs TRUE to continue
	 * and FALSE to stop
	 *
	 * @param jobUID
	 *            is the UID od the computing job on worker side
	 * @return a hashtable containing this job status so that worker continue or
	 *         stop computing it
	 */
	public Hashtable workAlive(final UserInterface _user, final HostInterface _host, final UID jobUID)
			throws IOException, InvalidKeyException, AccessControlException {

		boolean keepWorking = false;

		mileStone(("<workAlive host=" + _host) != null ? _host.getName() : "null" + ">");
		final UserInterface user = DBInterface.getInstance().checkClient(_user, UserRightEnum.GETJOB);

		_host.setIPAddr(remoteIP);
		final HostInterface theHost = DBInterface.getInstance().hostRegister(user, _host);

		if (theHost == null) {
			throw new IOException("workAlive : can't find host ");
		}
		final boolean isActive = _host.isActive();

		logger.debug("retrieving current computing job");
		// theHost = DBInterface.getInstance().host(_host.getUID());
		final WorkInterface theWork = DBInterface.getInstance().work(jobUID);
		final TaskInterface theTask = DBInterface.getInstance().task(theWork, theHost);

		if (theTask != null) {
			if (!isActive) {
				theWork.unlockWork();
				theTask.setError();
				keepWorking = false;
			} else {
				try {
					keepWorking = theTask.setAlive(_host.getUID());
				} catch (final Exception e) {
					error(e);
				}
			}
			theTask.update();
		} else {
			logger.finest("work is not in the dispatcher pool. Send back 'abort'");
			keepWorking = false;
		}

		if (!keepWorking) {
			warn("workAlive(" + _host.getName() + "," + jobUID + ") stopping!");
		}
		final Hashtable result = new Hashtable();
		result.put("keepWorking", new Boolean(keepWorking));
		mileStone("</workAlive>");
		return result;

	}

	/**
	 * This is send by worker to tell it is still connected. Workers always try
	 * to sent this signal.
	 *
	 * This gets some informations from worker:
	 * <ul>
	 * <li>current stored job results UIDs, if any
	 * </ul>
	 *
	 * This sends some parameters back to worker:
	 * <ul>
	 * <li>the SmartSockets hub address
	 * <li>traces, a boolean, to collect traces (or not);
	 * <li>tracesSendResultDelay, an integer, contains traces results transfert
	 * to coordinator periodicity;
	 * <li>tracesResultDelay, an integer, contains traces collection
	 * periodicity.
	 * <li>savedTasks, a Vector, contains works saved by coordinator that worker
	 * can delete from local disk savedTasks also contains works that the
	 * coordinator is not able to save for any reason.
	 * <li>resultsExpected, a Vector, contains tasks the coordinator wants the
	 * worker sends again results for
	 * <li>this server current version
	 * </ul>
	 *
	 * @param _user
	 *            defines the calling client
	 * @param _host
	 *            defines the calling host
	 * @param rmiParams
	 *            is a Hashtable containing the worker local results, if any
	 * @return a hashtable containing new worker parameters.
	 */
	public Hashtable workAlive(final UserInterface _user, final HostInterface _host, final Hashtable rmiParams)
			throws IOException, InvalidKeyException, AccessControlException {

		mileStone(("<workAlive host=" + _host) != null ? _host.getName() : "null" + ">");
		final UserInterface user = DBInterface.getInstance().checkClient(_user, UserRightEnum.GETJOB);

		_host.setIPAddr(remoteIP);
		final HostInterface theHost = DBInterface.getInstance().hostRegister(user, _host);

		if (theHost == null) {
			throw new IOException("workAlive : can't find host ");
		}
		final Hashtable ret = new Hashtable();
		final boolean isTracing = theHost.isTracing();
		final boolean isActive = theHost.isActive();

		ret.put(Connection.HUBPNAME, System.getProperty(XWPropertyDefs.SMARTSOCKETSHUBADDR.toString()));
		ret.put(XWPostParams.TRACES.toString(), new Boolean(isTracing));
		ret.put(XWPostParams.TRACESSENDRESULTDELAY.toString(), new Integer(tracesSendResultDelay));
		ret.put(XWPostParams.TRACESRESULTDELAY.toString(), new Integer(tracesResultDelay));
		ret.put(XWPostParams.CURRENTVERSION.toString(), CURRENTVERSIONSTRING);

		logger.finest("retrieve saved tasks so that the worker cleans its local copy");
		final ArrayList<UID> finishedTasks = new ArrayList<>();
		final ArrayList<UID> resultsVector = new ArrayList<>();

		try {
			//
			// It finally checks whether worker should erase some results it
			// still
			// owns.
			// This may happen if worker has some deleted job results on its
			// disk.
			// (i.e. if client has submitted a job which has been dispatched and
			// computed on a worker while the client finally decided to delete
			// that
			// job)
			//
			final ArrayList jobResults = (ArrayList) rmiParams.get(XWPostParams.JOBRESULTS.toString());

			if (jobResults != null) {

				debug("workAlive (" + _host.getName() + ") : jobResults.size () = " + jobResults.size());

				final Iterator<XMLValue> vIterator = jobResults.iterator();

				if (vIterator != null) {
					while (vIterator.hasNext()) {

						final XMLValue v = vIterator.next();
						final UID workUID = (UID) v.getValue();

						if (workUID == null) {
							continue;
						}
						final WorkInterface w = DBInterface.getInstance().work(workUID);
						if (w == null) {
							// we don't know that job, remove result on worker
							// side
							// (maybe it has been removed by user)
							debug("workAlive (" + _host.getName() + ") : worker must stop " + workUID);
							finishedTasks.add(workUID);
						} else {
							final URI resultURI = w.getResult();
							final StatusEnum workStatus = w.getStatus();

							debug("resultURI = " + resultURI);
							if ((resultURI != null) && (resultURI.isXtremWeb())) {
								final UID resultUID = resultURI.getUID();
								final DataInterface workResult = DBInterface.getInstance().data(resultUID);
								debug("workResult (" + resultURI + ") = " + workResult);
								if (workResult != null) {
									final StatusEnum resultStatus = workResult.getStatus();
									if ((workStatus == StatusEnum.DATAREQUEST)
											|| (resultStatus == StatusEnum.DATAREQUEST)) {
										warn("workAlive (" + _host.getName() + ") : reasking result for " + workUID);
										resultsVector.add(workUID);
									}
								}
							}

							switch (workStatus) {
							case ERROR:
							case COMPLETED:
								debug("workAlive (" + _host.getName() + ") : worker can delete " + resultURI);
								finishedTasks.add(workUID);
								break;
							}
						}
					}
				}
			}

			ret.put(XWPostParams.FINISHEDTASKS.toString(), finishedTasks);

			ret.put(XWPostParams.RESULTEXPECTEDS.toString(), resultsVector);

			UID hostuid = theHost.getUID();
			String newServer = DBInterface.getInstance().getServer(hostuid);
			hostuid = null;

			if (newServer != null) {
				info("workAlive (" + theHost.getName() + ") : new server = " + newServer);
				ret.put(XWPostParams.NEWSERVER.toString(), newServer);
			}
			newServer = null;

			Integer aliveperiod = new Integer(config.getProperty(XWPropertyDefs.ALIVEPERIOD));
			debug("alivePeriod = " + aliveperiod);
			ret.put(XWPostParams.ALIVEPERIOD.toString(), aliveperiod);
			aliveperiod = null;
		} catch (final IOException e) {
			error(e);
		} finally {
			if (theHost != null) {
				try {
					theHost.update();
				} catch (final Exception e) {
					error("cant update host " + e);
				}
			}
		}

		String newKeystoreUriStr = config.getProperty(XWPropertyDefs.KEYSTOREURI);
		debug("\n\n\nnewKeystoreUriStr =  " + newKeystoreUriStr);
		if (newKeystoreUriStr != null) {
			ret.put(XWPostParams.KEYSTOREURI.toString(), newKeystoreUriStr);
		}
		newKeystoreUriStr = null;

		mileStone("</workAlive>");
		return ret;

	}

	/** Tracer */
	public void tactivityMonitor(final HostInterface host, final long start, final long end, final byte[] file)
			throws IOException, InvalidKeyException, AccessControlException {

		tactivityMonitor(host, new Long(start).toString(), new Long(end).toString(), file);
	}

	public synchronized void tactivityMonitor(final HostInterface host, final String start, final String end,
			final byte[] file) throws IOException, InvalidKeyException, AccessControlException {
		final long startValue = (new Long(start)).longValue();
		final long endValue = (new Long(end)).longValue();

		DBInterface.getInstance().writeStatFile(host, startValue, endValue, file);
		notifyAll();
	}

	/**
	 * This disconnects a client from server
	 *
	 * @param client
	 *            defines this client attributes, such as user ID, password etc.
	 */
	public void disconnect(final UserInterface client) throws IOException, InvalidKeyException, AccessControlException {

		mileStone("<disconnect>");
		DBInterface.getInstance().disconnect(client);
		mileStone("</disconnect>");
	}

	/**
	 * This creates or updates an application on server side This calls
	 * DBInterface::addApplication() to check whether client has the right to do
	 * so.
	 *
	 * @see DBInterface#addApp(UserInterface, AppInterface)
	 */
	public void sendApp(final UserInterface client, final AppInterface mapp)
			throws IOException, InvalidKeyException, AccessControlException {

		mileStone("<sendApp>");
		DBInterface.getInstance().addApp(client, mapp);
		DBInterface.getInstance().updateAppsPool();
		mileStone("</sendApp>");
	}

	/**
	 * This retrieves an object from server
	 */
	public Table get(final UserInterface client, final UID uid)
			throws IOException, InvalidKeyException, AccessControlException {
		return DBInterface.getInstance().get(client, uid);
	}

	/**
	 * This retrieves an object from server
	 *
	 * @since 5.8.0
	 */
	public Table get(final UserInterface client, final URI uri)
			throws IOException, InvalidKeyException, AccessControlException {
		if (uri == null) {
			throw new IOException(URINOTSET);
		}
		return get(client, uri.getUID());
	}

	/**
	 * This retrieves a task from server On Apr, 2012, we introduce GETTASK
	 * message to ease client usage and help to retrieve task from work uid This
	 * aims to retrieve task from either its UID **or** its WORKUID
	 */
	public Table getTask(final UserInterface client, final URI uri)
			throws IOException, InvalidKeyException, AccessControlException {
		if (uri == null) {
			throw new IOException(URINOTSET);
		}
		return DBInterface.getInstance().getTask(client, uri.getUID());
	}

	/**
	 * This retrieves an application from server
	 */
	public AppInterface getApp(final UserInterface client, final String name)
			throws IOException, InvalidKeyException, AccessControlException {
		return DBInterface.getInstance().getApplication(client, name);
	}

	/**
	 * This retrieves all applications from server
	 *
	 * @return a vector of UIDs
	 */
	public XMLVector getApps(final UserInterface client)
			throws IOException, InvalidKeyException, AccessControlException {

		mileStone("<getApps>");
		final ArrayList<UID> ret = (ArrayList<UID>) DBInterface.getInstance().getApplications(client);
		XMLVector v = null;
		if (ret != null) {
			v = new XMLVector((Collection) ret);
		}
		mileStone("</getApps>");
		return v;
	}

	/**
	 * This changes access rights of the given object with the given access
	 * rights modifier
	 *
	 * @param client
	 *            is the requesting client
	 * @param uri
	 *            is the URI of the object to change the access rights for
	 * @param modifier
	 *            is the access rights modifier
	 * @see xtremweb.security.XWAccessRights#chmod(String)
	 * @since 5.8.0
	 */
	public void chmod(final UserInterface client, final URI uri, final XWAccessRights modifier)
			throws IOException, InvalidKeyException, AccessControlException {
		try {
			mileStone("<chmod>");
			DBInterface.getInstance().chmod(client, uri, modifier.toHexString());
			mileStone("</chmod>");
		} catch (final ParseException e) {
			throw new IOException(e.getMessage());
		}
	}

	/**
	 * This retrieves the known SmartSockets hub address
	 *
	 * @param _user
	 *            is the caller credentials
	 * @return a hashtable containing the SmartSockets hub address
	 * @since 8.0.0
	 */
	public Hashtable<String, String> getHubAddr(final UserInterface _user)
			throws IOException, InvalidKeyException, AccessControlException {

		mileStone("<getHubAddr>");
		try {
			DBInterface.getInstance().checkClient(_user, UserRightEnum.GETJOB);
		} catch (final Exception e) {
			throw new InvalidKeyException("unknown user " + _user.getLogin());
		}

		final Hashtable<String, String> ret = new Hashtable<>();
		logger.debug("hub addr = " + System.getProperty(XWPropertyDefs.SMARTSOCKETSHUBADDR.toString()));
		ret.put(Connection.HUBPNAME, System.getProperty(XWPropertyDefs.SMARTSOCKETSHUBADDR.toString()));
		mileStone("</getHubAddr>");
		return ret;
	}

	/**
	 * This removes an application from server
	 *
	 * @param client
	 *            defines the client
	 * @param uid
	 *            is the UID of the object to remove
	 */
	public void remove(final UserInterface client, final UID uid)
			throws IOException, InvalidKeyException, AccessControlException {

		mileStone("<remove>");
		if (!DBInterface.getInstance().remove(client, uid)) {
			mileStone("<error method='remove' msg='object not found' />");
			mileStone("</remove>");
			throw new IOException("Object not found");
		}
		mileStone("</remove>");
	}

	/**
	 * This removes an application from server
	 *
	 * @param client
	 *            defines the client
	 * @param uri
	 *            is the URI of the object to remove
	 * @since 5.8.0
	 */
	public void remove(final UserInterface client, final URI uri)
			throws IOException, InvalidKeyException, AccessControlException {
		if (uri == null) {
			throw new IOException(URINOTSET);
		}
		remove(client, uri.getUID());
	}

	/**
	 * This creates or updates data on server side
	 *
	 * @param client
	 *            is the caller attributes
	 * @param data
	 *            is the data to create
	 */
	public void sendData(final UserInterface client, final DataInterface data)
			throws IOException, InvalidKeyException, AccessControlException {

		mileStone("<sendData>");
		DBInterface.getInstance().addData(client, data);
		mileStone("</sendData>");
	}

	/**
	 * This retrieves a data from server
	 */
	public XMLVector getDatas(final UserInterface client)
			throws IOException, InvalidKeyException, AccessControlException {

		mileStone("<getDatas>");
		final ArrayList<UID> ret = (ArrayList<UID>) DBInterface.getInstance().getDatas(client);
		XMLVector v = null;
		if (ret != null) {
			v = new XMLVector((Collection) ret);
		}
		mileStone("</getDatas>");
		return v;
	}

	/**
	 * This uploads a data to server<br />
	 * Data must be defined on server side (i.e. sendData() must be called
	 * first)
	 *
	 * @param client
	 *            is the caller attributes
	 * @param uri
	 *            is the URI of the data to upload
	 * @see #sendData(UserInterface, DataInterface)
	 * @return the size of the uploaded data
	 * @since 5.8.0
	 */
	public long uploadData(final UserInterface client, final URI uri)
			throws IOException, InvalidKeyException, AccessControlException {
		if (uri == null) {
			throw new IOException(URINOTSET);
		}
		return uploadData(client, uri.getUID());
	}

	/**
	 * This uploads a data to server<br />
	 * Data must be defined on server side (i.e. sendData() must be called
	 * first)
	 *
	 * @param client
	 *            is the caller attributes
	 * @param uid
	 *            is the UID of the data to upload
	 * @see #sendData(UserInterface, DataInterface)
	 * @return the size of the uploaded data
	 */
	public long uploadData(final UserInterface client, final UID uid)
			throws IOException, InvalidKeyException, AccessControlException {

		long ret = 0;

		mileStone("<uploadData>");
		DataInterface theData = null;
		File dFile = null;
		Date date = new Date();
		StatusEnum dataStatus = StatusEnum.AVAILABLE;

		try {
			theData = DBInterface.getInstance().getData(client, uid);
			if (theData == null) {
				throw new IOException("uploadData(" + uid + ") data not found");
			}
			dFile = theData.getPath();
			readFile(dFile);
		} catch (final InvalidKeyException e) {
			mileStone("<error method='uploadData' msg='" + e.getMessage() + "' />");
			throw e;
		} catch (final AccessControlException e) {
			mileStone("<error method='uploadData' msg='" + e.getMessage() + "' />");
			throw e;
		} catch (final IOException ioe) {
			mileStone("<error method='uploadData' msg='" + ioe.getMessage() + "' />");
			if (dFile != null) {
				dFile.delete();
			}
			logger.exception(ioe);
			dataStatus = StatusEnum.DATAREQUEST;
			throw new IOException("uploadData(" + uid + ") IOerror on server side");
		} catch (final Exception e) {
			mileStone("<error method='uploadData' msg='" + e.getMessage() + "' />");
			logger.exception(e);
			dataStatus = StatusEnum.ERROR;
			throw new IOException(e.getMessage());
		} finally {
			try {
				if (theData != null) {
					theData.setStatus(dataStatus);
					theData.update();
				}
			} catch (final Exception e) {
				logger.exception(e);
			}
			if (dFile != null) {
				ret = dFile.length();
			}
			theData = null;
			dFile = null;
			date = null;
			mileStone("</uploadData>");
		}
		return ret;
	}

	/**
	 * This downloads a data from server
	 *
	 * @param client
	 *            is the caller attributes
	 * @param uri
	 *            is the URI of the data to download
	 * @since 5.8.0
	 */
	public long downloadData(final UserInterface client, final URI uri)
			throws IOException, InvalidKeyException, AccessControlException {
		if (uri == null) {
			throw new IOException(URINOTSET);
		}
		return downloadData(client, uri.getUID());
	}

	/**
	 * This downloads a data from server
	 *
	 * @param client
	 *            is the caller attributes
	 * @param uid
	 *            is the UID of the data to download
	 */
	public long downloadData(final UserInterface client, final UID uid)
			throws IOException, InvalidKeyException, AccessControlException {

		long ret = 0;
		DataInterface theData = null;
		File dFile = null;
		final Date date = new Date();

		mileStone("<downloadData>");
		try {
			theData = DBInterface.getInstance().getData(client, uid);
			if (theData == null) {
				throw new IOException("downloadData(" + uid + ") data not found");
			}

			try {
				theData = DBInterface.getInstance().data(uid);
				dFile = theData.getPath();

				if ((theData.getSize() > 0) && (theData.getMD5() == null)) {
					error("downloadData setstatus ERROR : size=" + theData.getSize() + ", md5=" + theData.getMD5());
					theData.setStatus(StatusEnum.ERROR);
					theData.update();
					mileStone("<error method='downloadData' msg='MD5 error' />");
					mileStone("</downloadData>");
					throw new IOException("downloadData(" + uid + ") MD5 should not be null");
				}
			} catch (final Exception e) {
				theData = null;
				dFile = null;
			}

			writeFile(dFile);
			ret = 0;
			if (dFile != null) {
				ret = dFile.length();
			} else {
				throw new IOException("downloadData(" + uid + ") file not found");
			}
			if (theData != null) {
				theData.update();
			} else {
				throw new IOException("downloadData(" + uid + ") data error");
			}
		} catch (final InvalidKeyException e) {
			mileStone("<error method='downloadData' msg='" + e.getMessage() + "' />");
			throw e;
		} catch (final AccessControlException e) {
			mileStone("<error method='downloadData' msg='" + e.getMessage() + "' />");
			throw e;
		} catch (final Exception e) {
			mileStone("<error method='downloadData' msg='" + e.getMessage() + "' />");
			error(e);
			if (theData != null) {
				error("downloadData setstatus ERROR : " + e);
				theData.setStatus(StatusEnum.ERROR);
				theData.update();
			}
			throw new IOException(e.toString());
		} finally {
			theData = null;
			dFile = null;
			mileStone("</downloadData>");
		}
		return ret;
	}

	/**
	 * This creates or updates a group on server side
	 *
	 * @param client
	 *            contains user id/password
	 * @param group
	 *            is the group to send
	 */
	public void sendGroup(final UserInterface client, final GroupInterface group)
			throws IOException, InvalidKeyException, AccessControlException {

		mileStone("<createGroup>");
		DBInterface.getInstance().addGroup(client, group);
		mileStone("</createGroup>");
	}

	/**
	 * This retrieves this client groups
	 *
	 * @param client
	 *            defines this client attributes, such as user ID, password etc.
	 */
	public XMLVector getGroups(final UserInterface client)
			throws IOException, InvalidKeyException, AccessControlException {

		try {
			mileStone("<getGroups>");
			final ArrayList<UID> ret = (ArrayList<UID>) DBInterface.getInstance().getGroups(client);
			XMLVector v = null;
			if (ret != null) {
				v = new XMLVector((Collection) ret);
			}
			return v;
		} finally {
			mileStone("</getGroups>");
		}
	}

	/**
	 * This retrieves all works for the given group
	 *
	 * @param client
	 *            defines this client attributes, such as user ID, password etc.
	 * @param uri
	 *            is the URI of the group to retrieve works for
	 * @since 5.8.0
	 */
	public XMLVector getGroupWorks(final UserInterface client, final URI uri)
			throws IOException, InvalidKeyException, AccessControlException {
		if (uri == null) {
			throw new IOException(URINOTSET);
		}
		return getGroupWorks(client, uri.getUID());
	}

	/**
	 * This retrieves all works for the given group
	 *
	 * @param client
	 *            defines this client attributes, such as user ID, password etc.
	 * @param group
	 *            is the group UID to retrieve works for
	 */
	public XMLVector getGroupWorks(final UserInterface client, final UID group)
			throws IOException, InvalidKeyException, AccessControlException {

		try {
			mileStone("<getGroupWorks>");
			final ArrayList<UID> ret = (ArrayList<UID>) DBInterface.getInstance().getGroupJobs(client, group);
			XMLVector v = null;
			if (ret != null) {
				v = new XMLVector((Collection) ret);
			}
			return v;
		} finally {
			mileStone("<getGroupWorks>");
		}
	}

	/**
	 * This creates or updates an session on server side
	 */
	public void sendSession(final UserInterface client, final SessionInterface session)
			throws IOException, InvalidKeyException, AccessControlException {

		DBInterface.getInstance().addSession(client, session);
	}

	/**
	 * This retrieves all sessions from server
	 *
	 * @return a vector of UIDs
	 */
	public XMLVector getSessions(final UserInterface client)
			throws IOException, InvalidKeyException, AccessControlException {

		try {
			mileStone("<getSessions>");
			final ArrayList<UID> ret = (ArrayList<UID>) DBInterface.getInstance().getSessions(client);
			XMLVector v = null;
			if (ret != null) {
				v = new XMLVector((Collection) ret);
			}
			return v;
		} finally {
			mileStone("</getSessions>");
		}
	}

	/**
	 * This retrieves all works for the given session
	 *
	 * @param client
	 *            defines this client attributes, such as user ID, password etc.
	 * @param session
	 *            is the session UID to retrieve works for
	 */
	public XMLVector getSessionWorks(final UserInterface client, final UID session)
			throws IOException, InvalidKeyException, AccessControlException {

		try {
			mileStone("<getSessionWorks>");
			final ArrayList<UID> ret = (ArrayList<UID>) DBInterface.getInstance().getSessionJobs(client, session);
			XMLVector v = null;
			if (ret != null) {
				v = new XMLVector((Collection) ret);
			}
			return v;
		} finally {
			mileStone("</getSessionWorks>");
		}
	}

	/**
	 * This creates or updates an work on server side This calls
	 * DBInterface#insertWork()
	 *
	 * @see DBInterface#addWork(UserInterface, HostInterface, WorkInterface)
	 */
	public void sendWork(final UserInterface client, final HostInterface host, final WorkInterface work)
			throws IOException, InvalidKeyException, AccessControlException {

		mileStone("<sendWork>");
		DBInterface.getInstance().addWork(client, host, work);
		mileStone("</sendWork>");
	}

	/**
	 * @deprecated since 1.9.0 this is deprecated ; sendWork() should be used
	 *             instead stdin and dirin must be sent using sendData
	 * @see #sendWork(UserInterface, HostInterface, WorkInterface)
	 * @see #sendData(UserInterface, DataInterface)
	 */
	@Deprecated
	public void submit(final UserInterface client, final WorkInterface job)
			throws IOException, InvalidKeyException, AccessControlException {
	}

	/**
	 * This retrieves an work from server, including all associated files
	 *
	 * @param client
	 *            is the caller attributes
	 * @param uid
	 *            is the work UID
	 * @return a WorkInterface object
	 */
	public WorkInterface loadWork(final UserInterface client, final UID uid)
			throws IOException, InvalidKeyException, AccessControlException {
		throw new IOException("TCPHandler::loadWork() not implemented yet");
	}

	/**
	 * This retrieves all works from server
	 *
	 * @return a vector of UIDs
	 */
	public XMLVector getWorks(final UserInterface client)
			throws IOException, InvalidKeyException, AccessControlException {
		return getWorks(client, null);
	}

	/**
	 * This retrieves all works from server
	 *
	 * @return a vector of UIDs
	 */
	public XMLVector getWorks(final UserInterface client, final StatusEnum s)
			throws IOException, InvalidKeyException, AccessControlException {

		try {
			mileStone("<getWorks>");
			final ArrayList<UID> ret = (ArrayList<UID>) DBInterface.getInstance().getAllJobs(client, s);
			XMLVector v = null;
			if (ret != null) {
				v = new XMLVector((Collection) ret);
			}
			return v;
		} finally {
			mileStone("</getWorks>");
		}
	}

	/**
	 * This broadcasts a new work to all workers
	 *
	 * @param client
	 *            defines this client attributes, such as user ID, password etc.
	 * @param work
	 *            defines the work to broadcast
	 * @return a Vector of String containing submitted work UIDs
	 */
	public XMLVector broadcast(final UserInterface client, final WorkInterface work)
			throws IOException, InvalidKeyException, AccessControlException {

		try {
			mileStone("<broadcast>");
			final ArrayList<UID> ret = (ArrayList<UID>) DBInterface.getInstance().broadcast(client, work);
			XMLVector v = null;
			if (ret != null) {
				v = new XMLVector((Collection) ret);
			}
			return v;
		} finally {
			mileStone("</broadcast>");
		}
	}

	/**
	 * This always throws an exception
	 *
	 * @deprecated since 1.9.0
	 * @see #sendData(UserInterface, DataInterface)
	 */
	@Deprecated
	public DataInterface getResult(final UserInterface client, final UID uid)
			throws IOException, InvalidKeyException, AccessControlException {
		throw new IOException("GETRESULT is deprecated; please use GETDATA");
	}

	/**
	 * This always throws an exception
	 *
	 * @deprecated since 1.9.0
	 * @see #sendData(UserInterface, DataInterface)
	 */
	@Deprecated
	public void sendResult(final UserInterface client, final DataInterface result)
			throws IOException, InvalidKeyException, AccessControlException {
		throw new IOException("SENDRESULT is deprecated; please use SENDDATA");
	}

	/**
	 * This creates or updates an worker on server side
	 */
	public void sendWorker(final UserInterface client, final HostInterface worker)
			throws IOException, InvalidKeyException, AccessControlException {
		throw new IOException("TCPHandler::sendWorker TCP not implemented yet");
	}

	/**
	 * This retrieves all workers from server
	 *
	 * @return a vector of UIDs
	 */
	public XMLVector getWorkers(final UserInterface client)
			throws IOException, InvalidKeyException, AccessControlException {

		try {
			mileStone("<getWorkers>");
			final ArrayList<UID> ret = (ArrayList<UID>) DBInterface.getInstance().getAliveWorkers(client);
			XMLVector v = null;
			if (ret != null) {
				v = new XMLVector((Collection) ret);
			}
			return v;
		} finally {
			mileStone("</getWorkers>");
		}
	}

	/**
	 * Set worker active flag.
	 *
	 * @param client
	 *            contains client parameters
	 * @param uri
	 *            is the worker uri
	 * @param flag
	 *            is the active flag
	 * @since 5.8.0
	 */
	public void activateWorker(final UserInterface client, final URI uri, final boolean flag)
			throws IOException, InvalidKeyException, AccessControlException {
		if (uri == null) {
			throw new IOException(URINOTSET);
		}
		activateWorker(client, uri.getUID(), flag);
	}

	/**
	 * Set worker active flag.
	 *
	 * @param client
	 *            contains client parameters
	 * @param uid
	 *            is the worker uid
	 * @param flag
	 *            is the active flag
	 */
	public void activateWorker(final UserInterface client, final UID uid, final boolean flag)
			throws IOException, InvalidKeyException, AccessControlException {
		mileStone("<activateWorkers>");
		DBInterface.getInstance().activateWorker(client, uid, flag);
		mileStone("</activateWorkers>");
	}

	/**
	 * This set the expected number of workers
	 *
	 * @param client
	 *            contains client parameters
	 * @return the number of activated workers, -1 on error
	 */
	public int setWorkersNb(final UserInterface client, final int nb)
			throws IOException, InvalidKeyException, AccessControlException {
		return DBInterface.getInstance().activateWorkers(client, nb);
	}

	/**
	 * This adds a new user.
	 *
	 * @param client
	 *            contains client parameters
	 * @param user
	 *            describes new user informations
	 */
	public void sendUser(final UserInterface client, final UserInterface user)
			throws IOException, InvalidKeyException, AccessControlException {
		mileStone("<sendUser>");
		DBInterface.getInstance().addUser(client, user);
		mileStone("</sendUser>");
	}

	/**
	 * This adds/updates an user group. This forces group.project to true
	 */
	public void sendUserGroup(final UserInterface client, final UserGroupInterface group)
			throws IOException, InvalidKeyException, AccessControlException {

		mileStone("<sendUserGroup>");
		group.setProject(true);
		DBInterface.getInstance().addUserGroup(client, group);
		mileStone("</sendUserGroup>");
	}

	/**
	 * This retrieves all usergroups from server
	 *
	 * @return a vector of UIDs
	 */
	public XMLVector getUserGroups(final UserInterface client)
			throws IOException, InvalidKeyException, AccessControlException {

		try {
			mileStone("<getUserGroups>");
			final ArrayList<UID> ret = (ArrayList<UID>) DBInterface.getInstance().getUserGroups(client);
			XMLVector v = null;
			if (ret != null) {
				v = new XMLVector((Collection) ret);
			}
			return v;
		} finally {
			mileStone("</getUserGroups>");
		}
	}

	/**
	 * This retrieves an user from server
	 */
	public UserInterface getUser(final UserInterface client, final String login)
			throws IOException, InvalidKeyException, AccessControlException {

		return DBInterface.getInstance().getUser(client, login);
	}

	/**
	 * This retrieves all users from server
	 *
	 * @return a vector of UIDs
	 */
	public XMLVector getUsers(final UserInterface client)
			throws IOException, InvalidKeyException, AccessControlException {

		try {
			mileStone("<getUsers>");
			final ArrayList<UID> ret = (ArrayList<UID>) DBInterface.getInstance().getUsers(client);
			XMLVector v = null;
			if (ret != null) {
				v = new XMLVector((Collection) ret);
			}
			return v;
		} finally {
			mileStone("</getUsers>");
		}
	}

	/**
	 * This retrieves all tasks from server
	 *
	 * @return a vector of UIDs
	 */
	public XMLVector getTasks(final UserInterface client)
			throws IOException, InvalidKeyException, AccessControlException {

		try {
			mileStone("<getTasks>");
			final ArrayList<UID> ret = (ArrayList<UID>) DBInterface.getInstance().tasksUID(client);
			XMLVector v = null;
			if (ret != null) {
				v = new XMLVector((Collection) ret);
			}
			return v;
		} finally {
			mileStone("</getTasks>");
		}
	}

	/**
	 * This creates or updates an trace on server side
	 */
	public void sendTrace(final UserInterface client, final TraceInterface trace)
			throws IOException, InvalidKeyException, AccessControlException {
		throw new IOException("sendTrace not implemented yet");
	}

	/**
	 * This retrieves all traces from server
	 *
	 * @return a vector of UIDs
	 */
	public XMLVector getTraces(final UserInterface client)
			throws IOException, InvalidKeyException, AccessControlException {
		throw new IOException("getTraces not implemented yet");
	}

	/**
	 * Get all known traces.
	 *
	 * @return an vector of TraceInterface
	 */
	public XMLVector getTraces(final UserInterface client, final Date since, final Date before)
			throws IOException, InvalidKeyException, AccessControlException {
		throw new IOException("getTraces not implemented yet");
	}

	/**
	 * Get traces files path.
	 *
	 * @return a string containing traces files path.
	 */
	public String getTracesPath() throws IOException, InvalidKeyException, AccessControlException {
		throw new IOException("getTracesPath not implemented yet");
	}

	/**
	 * Get the path of traces files.
	 *
	 * @return a string describing path to traces files.
	 */
	public String getTracesPath(final UserInterface client)
			throws IOException, InvalidKeyException, AccessControlException {
		throw new IOException("getTracesPath not implemented yet");
	}

	/**
	 * Get trusted addresses
	 *
	 * @return a string containing trused ip addresses separated by a white
	 *         space.
	 */
	public String getTrustedAddresses(final UserInterface client)
			throws IOException, InvalidKeyException, AccessControlException {
		return null;
	}

	/**
	 * Add a trusted address
	 *
	 * @param ip
	 *            new trusted IP
	 */
	public void addTrustedAddress(final UserInterface client, final String ip)
			throws IOException, InvalidKeyException, AccessControlException {
	}

	/**
	 * this is not implemented
	 *
	 * @param ip
	 *            trusted IP to remove
	 */
	public void removeTrustedAddress(final UserInterface client, final String ip)
			throws IOException, InvalidKeyException, AccessControlException {
		throw new IOException("removeTrustedAddress is not implemented");
	}

	/**
	 * Set workers trace flag.
	 *
	 * @param hosts
	 *            is a hashtable which contains host name as key and their
	 *            dedicated trace flag as value.
	 */
	public void traceWorkers(final UserInterface client, final Hashtable hosts)
			throws IOException, InvalidKeyException, AccessControlException {
		mileStone("<traceWorkers>");
		DBInterface.getInstance().traceWorkers(client, hosts);
		mileStone("</traceWorkers>");
	}

}
