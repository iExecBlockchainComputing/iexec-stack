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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.AccessControlException;
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.net.SocketFactory;

import org.xml.sax.SAXException;

import xtremweb.common.AppInterface;
import xtremweb.common.AppTypeEnum;
import xtremweb.common.CPUEnum;
import xtremweb.common.DataInterface;
import xtremweb.common.DataTypeEnum;
import xtremweb.common.Logger;
import xtremweb.common.MD5;
import xtremweb.common.MileStone;
import xtremweb.common.OSEnum;
import xtremweb.common.StatusEnum;
import xtremweb.common.StreamIO;
import xtremweb.common.UID;
import xtremweb.common.XWPropertyDefs;
import xtremweb.common.XWTools;
import xtremweb.common.Zipper;
import xtremweb.communications.Connection;
import xtremweb.communications.SmartSocketsProxy;
import xtremweb.communications.URI;
import xtremweb.exec.Executor;
import xtremweb.exec.ExecutorLaunchException;

/**
 * ThreadWork.java Launch a java Work
 *
 * Created: Thu Jun 29 17:47:11 2000
 *
 * @author Gilles Fedak, V. Neri
 * @version %I% %G%
 */

public class ThreadWork extends Thread {

	/**
	 * This is the logger
	 */
	private final Logger logger;

	/**
	 * This tells whether the process has been killed If false, the process is
	 * has terminated just by itself(it reached its "normal" endpoint)
	 */
	private boolean killed = false;
	/**
	 * This is the name of the XWJOBUID env var which contains the job UID
	 *
	 * @since 8.0.0
	 */
	private static final String XWJOBUIDNAME = "XWJOBUID";
	/**
	 * This is the name of the XWLIBPATH env var which may contain the library
	 * bin path
	 *
	 * @since 8.0.0
	 */
	private static final String XWLIBPATHNAME = "XWLIBPATH";
	/**
	 * This is the name of the XWBINPATH env var which may contain the current
	 * bin path
	 *
	 * @since 8.0.0
	 */
	private static final String XWBINPATHNAME = "XWBINPATH";
	/**
	 * This is the name of the XWSCRATCH env var which may contain the job PWD
	 *
	 * @since 8.0.0
	 */
	private static final String XWSCRATCHNAME = "XWSCRATCHPATH";
	/**
	 * This is the name of the XWDIRINPATH env var which may contain the dirin
	 * path
	 *
	 * @since 8.0.0
	 */
	private static final String XWDIRINPATHNAME = "XWDIRINPATH";
	/**
	 * This is the name of the XWSTDINPATH env var which may contain the stdin
	 * path
	 *
	 * @since 8.0.0
	 */
	private static final String XWSTDINPATHNAME = "XWSTDINPATH";
	/**
	 * This is the name of the XWDISKSPACE env var which contains the expected
	 * disk space
	 *
	 * @since 8.0.0
	 */
	private static final String XWDISKSPACENAME = "XWDISKSPACE";
	/**
	 * This is the name of the XWRAMSIZE env var which contains the expected
	 * disk space
	 *
	 * @since 9.0.5
	 */
	private static final String XWRAMSIZENAME = "XWRAMSIZE";
	/**
	 * This is the name of the XWCPULOAD env var which contains the CPU usage
	 * limit
	 *
	 * @since 8.0.0
	 */
	private static final String XWCPULOADNAME = "XWCPULOAD";
	/**
	 * This is the name of the XWPORTS env var which contains a comma separated
	 * list of ports the process is listening
	 *
	 * @since 8.0.0
	 */
	private static final String XWLISTENINGPORTSNAME = "XWLISTENINGPORTS";
	/**
	 * This is the name of the XWPORTS env var which contains a comma separated
	 * list of ports the proxy is listening (to connect to client SmartSocket proxy)
	 *
	 * @since 10.6.0
	 */
	private static final String XWFORWARDINGPORTSNAME = "XWFORWARDINGPORTS";
	/**
	 * This is contains environment variables
	 *
	 * @since 8.0.0
	 */
	private Hashtable<String, String> envvars;
	/**
	 * This is the process work
	 */
	private Work currentWork;

	/**
	 * this retrieves the current work
	 *
	 * @return the current work
	 * @see #currentWork
	 * @since 8.2.0
	 */
	protected Work getCurrentWork() {
		return currentWork;
	}

	/**
	 * This is a vector of SmartSocket proxies since there may be several port
	 * per job
	 */
	private Collection<SmartSocketsProxy> smartSocketsProxies;

	/** This manages zip files */
	private final Zipper zipper;

	/** This contains processes return code */
	private int processReturnCode;

	/**
	 * This aims to display some time stamps
	 */
	private final MileStone mileStone;

	/**
	 * This manages processes
	 *
	 * @see xtremweb.exec.Executor
	 */
	private Executor exec;

	/**
	 * This is the local sandbox file, if any
	 */
	private File sandboxBinFile = null;

	/**
	 * This is the default and only constructor
	 */
	ThreadWork() {
		super("ThreadWork");

		logger = new Logger(this);
		smartSocketsProxies = null;
		mileStone = new MileStone(getClass());

		zipper = new Zipper();

		logger.warn("ThreadWork#ThreadWork() : RPCXW services disabled");

		sandboxBinFile = null;
		if (Worker.getConfig().getProperty(XWPropertyDefs.SANDBOXPATH) != null) {
			sandboxBinFile = new File(Worker.getConfig().getProperty(XWPropertyDefs.SANDBOXPATH).trim());
		}
		Runtime.getRuntime().addShutdownHook(new Thread("ThreadWorkCleaner") {
			@Override
			public void run() {
				try {
					shutDown();
					stopProcess();
				} catch (final Exception e) {
				}
			}
		});
	}

	private synchronized void waitForCompute() throws InterruptedException {
		if (ThreadLaunch.getInstance().getActivator() instanceof AlwaysActive) {
			logger.finest("is AlwaysActive");
			notifyAll();
			return;
		}

		while (!ThreadLaunch.getInstance().available()) {
			logger.info("ThreadWork : wait to compute");
			this.wait();
			logger.info("ThreadWork : compute allowed");
		}
		notifyAll();
	}

	private void dormir() {
		dormir(Long.parseLong(Worker.getConfig().getProperty(XWPropertyDefs.TIMEOUT)));
	}

	private void dormir(final long l) {
		try {
			sleep(l);
		} catch (final InterruptedException e) {
		}
	}

	public void wakeup() {
		this.interrupt();
	}

	/**
	 * This is the main loop
	 */
	@Override
	public void run() {

		StatusEnum status;

		while (true) {
			try {
				killed = false;

				if (Worker.getConfig().realTime() == false) {
					waitForCompute();
				}
				currentWork = CommManager.getInstance().getPoolWork().getNextWorkToCompute();

				if (currentWork == null) {
					dormir();
					continue;
				}

				try {
					final UID uid = currentWork.getUID();
					currentWork.setRunning();

					mileStone.println("<executejob uid='" + uid + "'>");

					addEnvVar(XWSCRATCHNAME, currentWork.getScratchDirName());

					final String jobuid = currentWork.getUID().toString();
					addEnvVar(XWJOBUIDNAME, jobuid);
					addEnvVar(XWCPULOADNAME, "" + Worker.getConfig().getHost().getCpuLoad());
					if (currentWork.getDiskSpace() > 0) {
						addEnvVar(XWDISKSPACENAME, "" + currentWork.getDiskSpace());
					}
					if (currentWork.getMinMemory() > 0) {
						addEnvVar(XWRAMSIZENAME, "" + currentWork.getMinMemory());
					}
					status = executeJob();
				} catch (final Throwable e) {
					killed = true;
					logger.exception("job launch error", new Exception(e));
					status = StatusEnum.ERROR;
					currentWork.clean();
					currentWork.setErrorMsg(e.getMessage());
					mileStone.println("<executeerror>" + e + "</executeerror>");
				}
				mileStone.println("</executejob>");

				currentWork.setStatus(status);

				if ((killed == false) && (status != StatusEnum.PENDING)) {
					logger.debug("Sending Result status = " + status);
					CommManager.getInstance().sendResult(currentWork);
				} else {
					try {
						CommManager.getInstance().getPoolWork().removeKilledWork(currentWork.getUID());
						logger.debug("killed == true ; " + currentWork.toXml());
						CommManager.getInstance().sendWork(currentWork);
					} catch (final Exception ioe) {
						logger.exception(ioe);
					}
					CommManager.getInstance().workRequest();
				}

				exec = null;
				currentWork = null;

				if (envvars != null) {
					envvars.clear();
				}
				envvars = null;
			} catch (final InterruptedException e) {
				logger.warn(e.toString());
			}
		}
	}

	public Work getWork() {
		return currentWork;
	}

	public void suspendProcess() {
		stopProcess();
	}

	/**
	 * This marks currentWork as aborted and send the to server
	 *
	 * @since 9.1.0
	 */
	protected void shutDown() {
		logger.debug("ThreadWork#shutDown()");

		if (currentWork != null) {
			currentWork.setStatus(StatusEnum.ABORTED);
			try {
				CommManager.getInstance().commClient().send(currentWork);
			} catch (final Exception e) {
			}
		}
	}

	/**
	 * This stops the running process, if any This first calls unload(), then
	 * exec.stop()
	 */
	protected void stopProcess() {
		logger.debug("stop process");
		if (exec != null) {
			if (exec.isRunning()) {
				try {
					unload();
					exec.stop();
					killed = true;
					exec = null;

					ThreadLaunch.getInstance().raz();
				} catch (final ExecutorLaunchException e) {
					logger.exception("ThreadWork.stopProcess() error ", e);
				} catch (InvalidKeyException e) {
					logger.exception("ThreadWork.stopProcess() error ", e);
				} catch (ClassNotFoundException e) {
					logger.exception("ThreadWork.stopProcess() error ", e);
				} catch (IOException e) {
					logger.exception("ThreadWork.stopProcess() error ", e);
				} catch (SAXException e) {
					logger.exception("ThreadWork.stopProcess() error ", e);
				} catch (URISyntaxException e) {
					logger.exception("ThreadWork.stopProcess() error ", e);
				}
			} else {
				logger.warn("ThreadWork.stopProcess() : nothing to stop");
			}
		}
	}

	/**
	 * This starts SmartSockets proxies as needed by the current work. This does
	 * nothing if the parameter is invalid (null or empty). This starts as many
	 * server proxies as ports found in currentWork.getListenPort() (a server
	 * proxy aims to forward incoming connection to current work). This also
	 * starts as many client proxies as ports found in
	 * currentWork.getSmartSocketClient() (a client proxy aims to forward
	 * outcoming connection from current work to the smart socket)
	 *
	 * @param hubAddr
	 *            is the SmartSockets hub
	 * @throws URISyntaxException
	 * @throws SAXException
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws UnknownHostException
	 * @throws ConnectException
	 * @throws AccessControlException
	 * @throws InvalidKeyException
	 * @since 8.0.0
	 */
	private void startProxy(final String hubAddr) throws InvalidKeyException,
			IOException, ClassNotFoundException, SAXException, URISyntaxException {

		if (Worker.getConfig().getBoolean(XWPropertyDefs.INCOMINGCONNECTIONS) == false) {
			logger.info("Incoming connections not allowed");
			return;
		}

		if ((hubAddr == null) || (hubAddr.length() < 1)) {
			return;
		}
		smartSocketsProxies = new Vector<SmartSocketsProxy>();
		//
		// Let 1st set SmartSockets proxies for server like job
		//
		final String sport = currentWork.getListenPort();
		if (sport != null) {
			final Collection<String> sports = XWTools.split(sport, ",");

			if (sports != null) {
				final Iterator<String> portenum = sports.iterator();
				String addresses = null;
				String ports = null;
				while (portenum.hasNext()) {
					int fport = Integer.parseInt(portenum.next());
					try {
						if (fport < Connection.PRIVILEGEDPORT) {
							logger.warn("Ignoring port lower than " + Connection.PRIVILEGEDPORT + " : " + fport);
							continue;
						}

						int portlimit;
						for (portlimit = 0; portlimit < 1000; portlimit++) {
							logger.debug("Checking port availability " + fport);
							if (XWTools.lockPort(fport)) {
								break;
							}
							fport++;
						}
						if (portlimit == 1000) {
							throw new IOException("can't find any available port");
						}
						logger.info("Starting new SmartSocket server proxy " + fport);
						final SmartSocketsProxy smartSocketsProxy = new SmartSocketsProxy(hubAddr, null, fport, true);
						smartSocketsProxies.add(smartSocketsProxy);
						smartSocketsProxy.start();
						if (addresses == null) {
							addresses = new String();
						} else {
							addresses += ",";
						}
						addresses += smartSocketsProxy.getLocalAddress().toString();

						if (ports == null) {
							ports = new String();
						} else {
							ports += ",";
						}
						ports += "" + Integer.toString(fport);
					} catch (final Exception e) {
						XWTools.releasePort(fport);
						logger.exception("Can't start new SmartSocket server proxy", e);
					}
				}

				addEnvVar(XWLISTENINGPORTSNAME, ports);

				currentWork.setSmartSocketAddr(addresses);
			}
		}

		//
		// Then let set SmartSockets proxies to connect to
		// server like application running on client side
		//
		final String sport2 = currentWork.getSmartSocketClient();
		if (sport2 != null) {

			final Hashtable<String, String> serverAddresses = (Hashtable<String, String>) XWTools.hash(sport2, ";", ",");
			if (serverAddresses != null) {
				final Enumeration<String> addressesenum = serverAddresses.keys();

				while (addressesenum.hasMoreElements()) {

					final String serverAddr = addressesenum.nextElement();

					SmartSocketsProxy smartSocketsProxy = null;
					try {
						final int lport = Integer.parseInt(serverAddresses.get(serverAddr));
						logger.info("Starting new SmartSocket client proxy " + serverAddr + " / " + lport);
						smartSocketsProxy = new SmartSocketsProxy(hubAddr, serverAddr, lport, false);
						smartSocketsProxies.add(smartSocketsProxy);
						smartSocketsProxy.start();
					} catch (final Exception e) {
						logger.exception("Can't start new SmartSocket client proxy", e);
					}
					finally {
						smartSocketsProxy = null;
					}
				}
			}
		}

		logger.debug(currentWork.toXml());
		CommManager.getInstance().sendWork(currentWork);
	}

	/**
	 * This stops the SmartSockets proxies, if any
	 *
	 * @since 8.0.0
	 */
	private void stopProxy() {

		if (smartSocketsProxies == null) {
			return;
		}
		logger.config("stopProxy");

		final Iterator<SmartSocketsProxy> proxiesEnum = smartSocketsProxies.iterator();

		while (proxiesEnum.hasNext()) {
			SmartSocketsProxy smartSocketsProxy = proxiesEnum.next();
			if (smartSocketsProxy == null) {
				continue;
			}
			smartSocketsProxy.setContinuer(false);
			//
			// we must open a last communications to unblock the socket.accept()
			//
			Socket s = null;
			int listenPort = -1;
			try {
				listenPort = smartSocketsProxy.getListenPort();
				if (listenPort < 0) {
					smartSocketsProxy = null;
					continue;
				}
				s = SocketFactory.getDefault().createSocket("localhost", listenPort);
				final OutputStream so = s.getOutputStream();
				so.write('\n'); // just write something to wake up the thread
				logger.info("SmartSocket proxy stopped");
			} catch (final Exception e) {
				logger.exception("Cant' stop SmartSocket proxy " + smartSocketsProxy.getListenPort(), e);
			} finally {
				XWTools.releasePort(listenPort);
				smartSocketsProxy = null;
				try {
					if (s != null) {
						s.close();
					}
				} catch (final IOException io) {
				}
				s = null;
			}
		}
		smartSocketsProxies.clear();
		smartSocketsProxies = null;
	}

	/**
	 * This execute unload script, if any This is called when process ends, or
	 * by stopProcess()
	 *
	 * @since 8.0.0
	 */
	private void unload()
			throws IOException, ClassNotFoundException, SAXException, URISyntaxException, InvalidKeyException {

		stopProxy();

		String command = getUnloadScriptPath();
		if (command == null) {
			return;
		}
		command += " " + currentWork.getCmdLine();

		logger.config("unload");

		try {
			final String[] envVars = getEnvVars();
			final File scratchDir = currentWork.getScratchDir();
			final FileOutputStream out = new FileOutputStream(new File(scratchDir, "unloadout.txt"));
			final FileOutputStream err = new FileOutputStream(new File(scratchDir, "unloaderr.txt"));
			final Executor unloader = new Executor(command, envVars, currentWork.getScratchDirName(), null, out, err,
					Long.parseLong(Worker.getConfig().getProperty(XWPropertyDefs.TIMEOUT)));
			try {
				unloader.startAndWait();
			} catch (final InterruptedException e) {
				logger.exception(e);
			} catch (ExecutorLaunchException e) {
				logger.exception(e);
			}

			try {
				out.flush();
				out.close();
			} catch (final IOException e) {
				logger.exception(e);
			}
			try {
				err.flush();
				err.close();
			} catch (final IOException e) {
				logger.exception(e);
			}
		} catch (final IOException e) {
			logger.exception("ThreadWork#unload()", e);
		} finally {
			command = null;
		}
	}

	@Override
	protected void finalize() throws Throwable {
		shutDown();
		stopProcess();
		super.finalize();
	}

	/**
	 * This restarts the current process, if any Mar 16th, 2005 : we don't
	 * restart a process which status is LONGFILE
	 *
	 * @see #executeJob()
	 */
	public synchronized void resumeProcess() {
		killed = false;

		if (currentWork != null) {
			try {
				currentWork.prepareDir();
			} catch (final Exception e) {
				logger.exception("can't prepare dir", e);
				try {
					notifyAll();
					this.finalize();
				} catch (final Throwable t) {
				}
			}
		}
	}

	/**
	 * This dispatches native(i.e. binary) and Java jobs
	 *
	 * @see #executeJavaJob(ArrayList)
	 * @see #executeNativeJob(ArrayList)
	 * @see #sendResult()
	 * @return mobile work status : - XWStatus.LONGFILE or - XWStatus.COMPLETED
	 *         Mar 16th, 2005 : when computing is done, we mark the job as
	 *         LONGFILE since zipping may be long stopProcess() followed by
	 *         resumeProcess() may be called while zipping/saving and we don't
	 *         want to restart a zipping/saving process
	 *
	 *         Not mentionning that resumeProcess() calls work.prepareDir()
	 *         which erases all dir structure, hence the zipping fails. The job
	 *         and the worker are then considered faulty : job will not be
	 *         reschedulled; the worker is deactivated. :(
	 *
	 *         resumeProcess() has been modified accordingly
	 *
	 *         This clearly means that CPU can not be released from
	 *         zipping/saving processing even if the user ask its CPU :(
	 *
	 * @see #resumeProcess()
	 */
	private StatusEnum executeJob() throws Exception {

		StatusEnum ret = StatusEnum.PENDING;

		zipper.setCreation(false);

		Collection<String> cmdLine = null;
		if (currentWork.isService() == false) {

			prepareWorkingDirectory();

			cmdLine = getCmdLine();

			// Oct 22th, 2003 : we sleep 250 millisec to be sure there will be a
			// difference in file.lastMofified()(see
			// src/common/Zipper.java::generate())
			if (Worker.getConfig().realTime() == false) {
				final int attente = 250;
				dormir(attente);
			}
		}

		currentWork.setCompStartDate(new Date());
		if (currentWork.isService()) {
			ret = executeService(cmdLine);
		} else {
			executeNativeJob(cmdLine);
		}

		currentWork.setCompEndDate(new Date());

		try {
			unload();
		} catch (final Exception e) {
			logger.exception("unload error", e);
		}

		ThreadLaunch.getInstance().raz();

		final UID workUID = currentWork.getUID();

		if (!killed) {
			try {
				if (currentWork.isService() == false) {
					if (currentWork.hasPackage() == false) {
						zipResult();
					} else {
						currentWork.setResult(null);
					}
				}
				ret = StatusEnum.COMPLETED;
			} catch (final IOException e) {
				ret = StatusEnum.ERROR;
				currentWork.clean();
				currentWork.setErrorMsg("Worker result error : " + e);
				logger.exception("Result error(" + workUID + ")", e);
			}
		} else {
			ret = StatusEnum.ABORTED;
			currentWork.clean();
			currentWork.setErrorMsg(
					"Aborted" + (currentWork.getErrorMsg() == null ? "" : " : " + currentWork.getErrorMsg()));
		}

		currentWork.clean(false);

		return ret;
	}

	/**
	 * @since 8.0.0
	 */
	private void addEnvVar(final String key, final String value) {
		if (envvars == null) {
			envvars = new Hashtable<String, String>();
		}
		envvars.put(key, value);
	}

	/**
	 * This retrieves environment variables, including: - CLASSPATH -
	 * DYLD_LIBRARY_PATH - JAVA_HOME - IFS - LD_LIBRARY_PATH - PATH - PERLLIB -
	 * PYTHONPATH
	 *
	 * @return an array of string containing default envvars, work env var (if
	 *         any) and app env var (if any)
	 * @throws URISyntaxException
	 * @throws SAXException
	 * @throws ClassNotFoundException
	 * @throws AccessControlException
	 * @throws InvalidKeyException
	 * @since 8.0.0 (FG)
	 */
	protected String[] getEnvVars() throws IOException, InvalidKeyException, AccessControlException,
			ClassNotFoundException, SAXException, URISyntaxException {

		final UID workApp = currentWork.getApplication();

		if (workApp == null) {
			throw new IOException("work has no application");
		}
		final AppInterface app = (AppInterface) CommManager.getInstance().commClient().get(workApp, false);

		if (app == null) {
			throw new IOException("can find application " + workApp);
		}

		// retrieve work env vars, if any
		final String envstrWork = currentWork.getEnvVars();
		final Hashtable<String, String> hwork = (Hashtable<String, String>) XWTools.hash(envstrWork, ",", "=");
		logger.debug("currentWork.getEnvVars().length = " + hwork.size());
		if (hwork != null) {
			envvars.putAll(hwork);
			hwork.clear();
		}

		// retrieve app env vars, if any
		final String envstrApp = app.getEnvVars();
		final Hashtable<String, String> happ = (Hashtable<String, String>) XWTools.hash(envstrApp, ",", "=");
		logger.debug("currentWork.app.getEnvVars().length = " + happ.size());
		if (happ != null) {
			envvars.putAll(happ);
			happ.clear();
		}

		final String[] ret = new String[envvars.size() + Worker.getConfig().getBaseEnvVars().length];

		int i = 0;

		logger.debug("Worker.getConfig().getBaseEnvVars().length = " + Worker.getConfig().getBaseEnvVars().length);
		for (int bevi = 0; bevi < Worker.getConfig().getBaseEnvVars().length; bevi++) {
			logger.finest("tuple[" + i + "] = " + Worker.getConfig().getBaseEnvVars()[bevi]);
			ret[i++] = Worker.getConfig().getBaseEnvVars()[bevi];
		}

		logger.debug("envvars.length = " + envvars.size());
		for (final Enumeration<String> keys = envvars.keys(); keys.hasMoreElements();) {
			final String key = keys.nextElement();
			final String value = envvars.get(key);
			final String tuple = new String(key + "=" + value);
			ret[i++] = tuple;
			logger.finest("tuple[" + i + "] = " + tuple);
		}

		return ret;
	}

	/**
	 * This retrieves the launch script path
	 *
	 * @return the work launch script, if any; if not, this returns the
	 *         application launch script, if any. This returns null otherwise
	 * @since 8.0.0 (FG)
	 */
	protected String getLaunchScriptPath()
			throws IOException, ClassNotFoundException, SAXException, URISyntaxException, InvalidKeyException {
		
		String ret = null;

		final UID workApp = currentWork.getApplication();

		if (workApp == null) {
			throw new IOException("work has no application");
		}
		final AppInterface app = (AppInterface) CommManager.getInstance().commClient().get(workApp, false);

		if (app == null) {
			throw new IOException("can`t find application " + workApp);
		}

		final URI scriptUri = app.getLaunchScript(Worker.getConfig().getHost().getOs());
		if (scriptUri == null) {
			return null;
		}

		CommManager.getInstance().commClient().get(scriptUri);

		final File scriptPath = CommManager.getInstance().commClient().getContentFile(scriptUri);

		if (scriptPath != null) {
			if (scriptPath.exists() == false) {
				throw new IOException("can find local script " + scriptPath);
			}
			ret = scriptPath.getCanonicalPath();
		}

		return ret;
	}

	/**
	 * This retrieves the unload script path
	 *
	 * @return the work launch script, if any; if not, this returns the
	 *         application launch script, if any. This returns null otherwise
	 * @since 8.0.0 (FG)
	 */
	protected String getUnloadScriptPath()
			throws IOException, ClassNotFoundException, SAXException, URISyntaxException, InvalidKeyException {

		String ret = null;

		final UID workApp = currentWork.getApplication();

		if (workApp == null) {
			throw new IOException("work has no application");
		}
		final AppInterface app = (AppInterface) CommManager.getInstance().commClient().get(workApp, false);

		if (app == null) {
			throw new IOException("can find application " + workApp);
		}

		final URI scriptUri = app.getUnloadScript(Worker.getConfig().getHost().getOs());
		if (scriptUri == null) {
			return null;
		}

		CommManager.getInstance().commClient().get(scriptUri);

		final File scriptPath = CommManager.getInstance().commClient().getContentFile(scriptUri);

		if (scriptPath != null) {
			if (scriptPath.exists() == false) {
				throw new IOException("can find local script " + scriptPath);
			}
			scriptPath.setExecutable(true);
			ret = scriptPath.getCanonicalPath();
		}

		return ret;
	}

	/**
	 * This retreives the current process binary path Since 6.0.0, it may happen
	 * that the Application has a file URI (e.g file:///path/to/bin) This is how
	 * we use locally predeployed applications (typically sandboxes)
	 *
	 * @throws AccessControlException
	 * @throws InvalidKeyException
	 */
	protected String getBinPath() throws IOException, ClassNotFoundException, SAXException, URISyntaxException,
			InvalidKeyException, AccessControlException {
		File sbBinPath = null;
		File appBinPath = null;
		String sbArgs = null;
		String ret = new String();
		String javajar = null;
		final UID workApp = currentWork.getApplication();

		if (workApp == null) {
			throw new IOException("work has no application");
		}

		final AppInterface app = (AppInterface) CommManager.getInstance().commClient().get(workApp, false);

		if (app == null) {
			throw new IOException("can find application " + workApp);
		}

		final AppTypeEnum at = app.getType();
		final String appName = app.getName();
		final String appType = (at != null ? at.toString() : null);

		if ((appType != null) && Worker.getConfig().getLocalApps().contains(appType)) {
			logger.debug("Applications " + appName + " (" + appType + ") is a shared app");
			String scriptPathName = getLaunchScriptPath();
			appBinPath = new File(scriptPathName);
			scriptPathName = null;
			if (appBinPath.exists() == false) {
				throw new IOException("local binary file not found");
			}
		} else {
			logger.debug("Applications " + appName + " (" + appType + ") is not a shared app");

			final URI binUri = app.getBinary(Worker.getConfig().getHost().getCpu(),
					Worker.getConfig().getHost().getOs());
			if (binUri == null) {
				throw new IOException("can find application " + workApp);
			}

			DataInterface bin = (DataInterface) CommManager.getInstance().commClient().get(binUri);

			if ((bin != null) && (bin.getType() == DataTypeEnum.JAVA)) {
				javajar = new String("java -jar");
			}
			appBinPath = CommManager.getInstance().commClient().getContentFile(binUri);

			if ((appBinPath != null) && (appBinPath.exists() == false)) {
				bin = null;
				appBinPath = null;
				throw new IOException("can find local binary " + appBinPath);
			}

			if (Worker.getConfig().getBoolean(XWPropertyDefs.SANDBOXENABLED) && (sandboxBinFile != null)
					&& sandboxBinFile.exists()) {

				sbBinPath = sandboxBinFile;

				if (Worker.getConfig().getProperty(XWPropertyDefs.SANDBOXSTARTARGS) != null) {
					sbArgs = Worker.getConfig().getProperty(XWPropertyDefs.SANDBOXSTARTARGS);
				}
			}
		}

		if (appBinPath != null) {
			appBinPath.setExecutable(true);
		}

		if (sbBinPath != null) {
			ret = sbBinPath.getCanonicalPath();
			if (sbArgs != null) {
				ret += " " + sbArgs;
			}
		}
		sbArgs = null;
		sbBinPath = null;

		if (javajar != null) {
			ret += " " + javajar;
		}
		javajar = null;

		final String path = appBinPath.getCanonicalPath();
		ret += " " + path;
		addEnvVar(XWBINPATHNAME, path);
		appBinPath = null;

		return ret;
	}

	/**
	 * This retrieves the current process command line
	 *
	 * @throws AccessControlException
	 * @throws InvalidKeyException
	 */
	protected Collection<String> getCmdLine() throws IOException, ClassNotFoundException, SAXException,
			URISyntaxException, InvalidKeyException, AccessControlException {

		Collection<String> ret = null;

		final String binPath = getBinPath();

		ret = XWTools.split(binPath);

		final String wcmdline = currentWork.getCmdLine();
		if (wcmdline != null) {
			final Collection<String> wcmdvector = XWTools.split(wcmdline);
			if (ret == null) {
				ret = wcmdvector;
			} else {
				ret.addAll(wcmdvector);
			}
		}
		return ret;
	}

	/**
	 * This installs data from an URI pass through file and call installFile()
	 * for each uri. This stops on the first error while trying to install URIs.
	 *
	 * @param throughUri
	 *            is the URI of the URI pass through file
	 * @param home
	 *            is the directory to install data content
	 * @return the directory of the last installed data
	 * @since 8.0.0
	 */
	protected File uriPassThrough(final URI throughUri, final File home) throws IOException {

		File ret = null;
		boolean islocked = false;
		BufferedReader reader = null;

		try {
			CommManager.getInstance().commClient().lock(throughUri);
			islocked = true;
			final File fData = CommManager.getInstance().commClient().getContentFile(throughUri);
			reader = new BufferedReader(new FileReader(fData));
			while (true) {
				String line = null;
				try {
					logger.debug("uriPassThrough 00 line = " + line);
					line = reader.readLine();
					logger.debug("uriPassThrough 01 line = " + line);
				} catch (final Exception e) {
				}
				if (line == null) {
					break;
				}
				logger.debug("uriPassThrough line = " + line);
				final URI uri = new URI(line);
				logger.debug("uriPassThrough uri = " + line);
				ret = installFile(uri, home);
			}
		} catch (final Exception e) {
			throw new IOException(e.getMessage());
		} finally {
			if (islocked) {
				CommManager.getInstance().commClient().unlock(throughUri);
			}
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (final IOException io) {
			}
			reader = null;
		}
		return ret;
	}

	/**
	 * This installs data from cache to home directory. If data content is a ZIP
	 * file, it is unzipped to home dir, otherwise it is copied
	 *
	 * @param uri
	 *            is the data URI
	 * @param home
	 *            is the directory to install data content
	 * @return the directory where the file has been unzipped; the file itself,
	 *         it not unzipped
	 */
	protected File installFile(final URI uri, final File home) throws IOException {

		File ret = null;
		File fData = null;
		DataInterface theData = null;
		boolean islocked = false;

		try {
			CommManager.getInstance().commClient().lock(uri);
			islocked = true;
			fData = CommManager.getInstance().commClient().getContentFile(uri);
			theData = (DataInterface) CommManager.getInstance().commClient(uri).get(uri, false);
		} catch (final Exception e) {
			if (islocked) {
				CommManager.getInstance().commClient().unlock(uri);
			}
			fData = null;
			theData = null;
			logger.exception(e);
			return null;
		}

		StreamIO io = null;

		try {
			logger.debug("installFile = " + fData);

			zipper.setFileName(fData.getCanonicalPath());
			boolean unzipped = false;
			try {
				unzipped = zipper.unzip(home.getCanonicalPath());
				ret = home;
			} catch (final Exception e) {
				logger.exception(e);
				unzipped = false;
			}

			if (unzipped == false) {
				// this is not a zip file
				// copy content from cache to pwd
				final File fout = new File(home,
						theData.getName() != null ? theData.getName() : theData.getUID().toString());
				XWTools.checkDir(fout.getParent());
				logger.debug("installFile = " + fData + " is not a zip file; just copy it to PWD : " + fout);
				ret = fout;
				final FileOutputStream fos = new FileOutputStream(fout);
				final DataOutputStream output = new DataOutputStream(fos);
				io = new StreamIO(output, null, 10240, Worker.getConfig().nio());
				io.writeFileContent(fData);
			}
		} finally {
			if (islocked) {
				CommManager.getInstance().commClient().unlock(uri);
			}
			if (io != null) {
				io.close();
			}
			io = null;
		}
		return ret;
	}

	/**
	 * This prepares the job environment (i.e. unzip/copy expected files) This
	 * unzip/copies:
	 * <ul>
	 * <li>app default dirin if job dirin is not defined
	 * <li>app default stdin if job stdin is not defined
	 * <li>job dirin
	 * <li>job stdin
	 * <li>app basedirin is installed at last because we want to ensure that its
	 * files are not overriden by job dirin or app default dirin files
	 * </ul>
	 *
	 * @exception IOException
	 *                is thrown on I/O error
	 */
	protected void prepareWorkingDirectory() throws IOException {

		logger.debug("prepareWorkingDirectory : scratchDir = " + currentWork.getScratchDir());

		final UID appUID = currentWork.getApplication();
		if (appUID == null) {
			logger.debug("no app uid ; certainly a sandbox?");
			return;
		}

		AppInterface app = null;
		try {
			app = (AppInterface) CommManager.getInstance().commClient().get(appUID, false);
		} catch (final Exception e) {
			logger.exception(e);
			app = null;
		}
		if (app == null) {
			throw new IOException("work defines now application");
		}
		logger.error("ThreadWork : can't use app library; please use executables");
		// URI uri = app.getLibrary(Worker.getConfig().getHost().getCpu(),
		// Worker.getConfig().getHost().getOs());
		// URI uri = null;
		// if (uri != null) {
		// logger.debug("prepareWorkingDirectory : using app library");
		// File libFile = installFile(uri, currentWork.getScratchDir());
		// addEnvVar(XWLIBPATHNAME, libFile.getCanonicalPath());
		// libFile = null;
		// }

		//
		// don't install app default dirin if job defined its own one
		//
		final URI dirinuri = currentWork.getDirin() != null ? currentWork.getDirin() : app.getDefaultDirin();
		if ((dirinuri != null) && (dirinuri.isNull() == false)) {
			File dirinFile = null;
			try {
				final DataInterface dirinData = (DataInterface) CommManager.getInstance().commClient(dirinuri)
						.get(dirinuri, false);
				final DataTypeEnum dirinType = (dirinData != null ? dirinData.getType() : null);
				logger.debug("dirinType = " + dirinType);
				if ((dirinType == null) || (dirinType != DataTypeEnum.URIPASSTHROUGH)) {
					dirinFile = installFile(dirinuri, currentWork.getScratchDir());
				} else {
					dirinFile = uriPassThrough(dirinuri, currentWork.getScratchDir());
				}
				addEnvVar(XWDIRINPATHNAME, dirinFile.getCanonicalPath());
			} catch (final Exception e) {
				logger.exception(e);
			} finally {
				dirinFile = null;
			}
		} else {
			logger.debug("prepareWorkingDirectory : job has no dirin");
		}
		//
		// don't install app default stdin if job defined its own one
		//
		final URI stdinuri = currentWork.getStdin() != null ? currentWork.getStdin() : app.getDefaultStdin();
		if ((stdinuri != null) && (stdinuri.isNull() == false)) {
			final File stdinFile = installFile(stdinuri, currentWork.getScratchDir());
			addEnvVar(XWSTDINPATHNAME, stdinFile.getCanonicalPath());
		} else {
			logger.debug("prepareWorkingDirectory : job has no stdin");
		}

		// If application basedirin is set, we must install it AFTER all others
		// to ensure basedirin files are not overriden

		final URI basedirinuri = app.getBaseDirin();
		if (basedirinuri != null) {
			logger.debug("prepareWorkingDirectory : using base app dirin");
			final File baseDirinFile = installFile(basedirinuri, currentWork.getScratchDir());
			addEnvVar(XWDIRINPATHNAME, baseDirinFile.getCanonicalPath());
		}

		zipper.resetFilesList();
		zipper.setFilesList(currentWork.getScratchDir());
	}

	/**
	 * This prepares the job result by zipping or copying the result
	 *
	 * @throws AccessControlException
	 * @throws InvalidKeyException
	 * @exception Exception
	 *                is thrown on I/O error
	 * @return the file containing the job result
	 */
	public synchronized File zipResult() throws IOException, ClassNotFoundException, SAXException, URISyntaxException,
			InvalidKeyException {

		boolean islocked = false;

		final UID workUID = currentWork.getUID();
		mileStone.println("<zipresult uid='" + workUID + "'>");

		DataInterface data = null;
		URI resulturi = currentWork.getResult();
		if (resulturi == null) {

			UID uid = new UID();
			resulturi = CommManager.getInstance().commClient().newURI(uid);
			logger.debug("work setting new result URI : " + resulturi);
			currentWork.setResult(resulturi);
			data = new DataInterface(uid);
			data.setURI(resulturi);
			uid = null;
			data.setCpu(CPUEnum.getCpu());
			data.setOs(OSEnum.getOs());
		} else {
			try {
				data = (DataInterface) CommManager.getInstance().commClient(resulturi).get(resulturi, false);
				logger.debug("work result URI was set : " + resulturi);
			} catch (final Exception e) {
			}
			if (data == null) {
				data = new DataInterface(resulturi.getUID());
				data.setURI(resulturi);
				data.setCpu(CPUEnum.getCpu());
				data.setOs(OSEnum.getOs());
			}
		}

		if ((data.getName() == null) || (data.getName().length() <= 0)) {
			if ((currentWork.getLabel() == null) || (currentWork.getLabel().length() <= 0)) {
				data.setName(DataTypeEnum.RESULTHEADER + currentWork.getUID());
			} else {
				data.setName(DataTypeEnum.RESULTHEADER + currentWork.getLabel());
			}
		}
		data.setOwner(currentWork.getOwner());

		File resultFile = null;
		try {
			// insure it is in the cache : put and get
			CommManager.getInstance().commClient().addToCache(data, resulturi);
			CommManager.getInstance().commClient().lock(resulturi);
			islocked = true;
			resultFile = CommManager.getInstance().commClient().getContentFile(resulturi);
			final String resultFilePath = resultFile.getCanonicalPath();
			logger.debug("ThreadWork#zipResult() " + resulturi + " " + "resultFile = " + resultFilePath);

			//
			// We don't keep stdout nor stderr if empty
			//
			final File out = new File(currentWork.getScratchDir(), XWTools.STDOUT);
			final File err = new File(currentWork.getScratchDir(), XWTools.STDERR);

			logger.debug("ThreadWork#zipResult : resultFile " + resultFilePath);

			if (out.exists() && (out.length() == 0)) {
				out.delete();
			}
			if (err.exists() && (err.length() == 0)) {
				err.delete();
			}

			final String[] resultDirName = new String[1];
			resultDirName[0] = new String(currentWork.getScratchDirName());

			zipper.setFileName(resultFilePath);

			data = (DataInterface) CommManager.getInstance().commClient().get(resulturi, false);

			if (zipper.zip(resultDirName, Worker.getConfig().getBoolean(XWPropertyDefs.OPTIMIZEZIP))) {
				data.setType(DataTypeEnum.ZIP);
			} else {
				data.setType(DataTypeEnum.NONE);

				if (zipper.getFileName() != null) {

					data.setName(zipper.getFileName().substring(currentWork.getScratchDirName().length() + 1));

					if (zipper.getFileName().endsWith(DataTypeEnum.TEXT.getFileExtension())) {
						data.setType(DataTypeEnum.TEXT);
					}

					File sourceResult = new File(zipper.getFileName());
					if (sourceResult.exists()) {
						XWTools.fileCopy(sourceResult, resultFile);
					}
					sourceResult = null;
				}
			}
			if (resultFile.exists()) {
				data.setMD5(MD5.asHex(MD5.getHash(resultFile)));
				data.setSize(resultFile.length());
			} else {
				logger.warn("ThreadWork#zipResult() resultFile does not exist");
			}
			data.setOwner(currentWork.getOwner());
		} finally {
			if (islocked) {
				CommManager.getInstance().commClient().unlock(currentWork.getResult());
			}
		}
		mileStone.println("</zipresult>");

		return resultFile;
	}

	/**
	 * This executes a native job
	 *
	 * @param cmdLine
	 *            command line for execution
	 * @throws URISyntaxException
	 * @throws SAXException
	 * @throws ClassNotFoundException
	 * @throws AccessControlException
	 * @throws InvalidKeyException
	 * @throws InterruptedException
	 * @throws ExecutorLaunchException
	 */
	protected void executeNativeJob(final Collection<String> cmdLine)
			throws IOException, InvalidKeyException, ClassNotFoundException, SAXException,
			URISyntaxException, ExecutorLaunchException, InterruptedException {

		final UID workUID = currentWork.getUID();

		mileStone.println("preparing execution", workUID);
		final String hubAddrStr = System.getProperty(XWPropertyDefs.SMARTSOCKETSHUBADDR.toString());
		startProxy(hubAddrStr);
		logger.debug("Execute Native Job " + workUID);
		String command = new String();
		for (final Iterator<String> iter = cmdLine.iterator(); iter.hasNext();) {
			command += iter.next() + " ";
		}

		logger.debug(workUID + " launches " + command);

		final File stdin = CommManager.getInstance().commClient().getContentFile(currentWork.getStdin());
		final File scratchDir = currentWork.getScratchDir();
		try {
			XWTools.checkDir(scratchDir);
		} catch (final Exception e) {
			logger.exception(e);
			command = null;
			throw new IOException(e.toString());
		}

		logger.debug("" + workUID + " executing on dir " + currentWork.getScratchDirName() + " stdin "
				+ (stdin == null ? "null" : stdin.getCanonicalPath()));

		final FileInputStream in = (stdin != null ? new FileInputStream(stdin) : null);
		final FileOutputStream out = new FileOutputStream(new File(scratchDir, XWTools.STDOUT));
		final FileOutputStream err = new FileOutputStream(new File(scratchDir, XWTools.STDERR));

		final String[] envvarsArray = getEnvVars();
		exec = new Executor(command, envvarsArray, currentWork.getScratchDirName(), in, out, err,
				Long.parseLong(Worker.getConfig().getProperty(XWPropertyDefs.TIMEOUT)));
		exec.setMaxWallClockTime(currentWork.getMaxWallClockTime());
		exec.setLoggerLevel(logger.getLoggerLevel());

		try {
			if ((Boolean.getBoolean(Worker.getConfig().getProperty(XWPropertyDefs.JAVARUNTIME))) && (stdin == null)) {
				mileStone.println("executing (Runtime)", workUID);
				final Runtime machine = Runtime.getRuntime();
				final Process process = machine.exec(command, null, scratchDir);
				process.waitFor();
				processReturnCode = process.exitValue();
			} else {
				mileStone.println("executing (Executor)", workUID);
				processReturnCode = exec.startAndWait();
			}
		} finally {
			try {
				if (out != null) {
					out.flush();
				}
			} catch (final IOException t) {
			}
			try {
				if (out != null) {
					out.close();
				}
			} catch (final IOException t) {
			}
			try {
				if (in != null) {
					in.close();
				}
			} catch (final IOException t) {
			}
			try {
				if (err != null) {
					err.flush();
				}
			} catch (final IOException t) {
			}
			try {
				if (err != null) {
					err.close();
				}
			} catch (final IOException t) {
			}

			exec = null;
			command = null;
		}

		mileStone.println("executed", workUID);

		logger.debug(workUID + " process exited with code " + processReturnCode);

		if (processReturnCode == 129) {
			killed = true;
		}
		if (!killed) {
			currentWork.setReturnCode(processReturnCode);
		}

		logger.debug("end of executeNativeJob() " + workUID);
	}

	/**
	 * This executes an embedded services
	 *
	 * @param cmdLine
	 *            command line for execution
	 * @exception WorkException
	 *                is thrown on execution error
	 */
	protected StatusEnum executeService(final Collection<String> cmdLine) throws IOException {

		throw new IOException("ThreadWork::executeService not implemented");
	}
}
