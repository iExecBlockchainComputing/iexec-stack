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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.naming.ConfigurationException;

import xtremweb.common.CommandLineOptions;
import xtremweb.common.CommandLineParser;
import xtremweb.common.Logger;
import xtremweb.common.UID;
import xtremweb.common.UserGroupInterface;
import xtremweb.common.XWConfigurator;
import xtremweb.common.XWPropertyDefs;
import xtremweb.common.XWTools;
import xtremweb.communications.HTTPServer;

/**
 * This class describes the XtremWeb Worker. It has a single infinite loop,
 * where it creates a new <CODE>ThreadLaunch</CODE>, starts it and waits for it
 * to die.
 * 
 * @author Gilles Fedak, Oleg Lodygensky
 */

public class Worker {

	private final Logger logger;

	/**
	 * This stores and tests the command line parameters
	 */
	private CommandLineParser args;
	/**
	 * The <CODE>config</CODE> variable implements the environment as read from
	 * disk.
	 */
	private static XWConfigurator config;
	private static int NEEDS_INIT = 1;
	private static int NEEDS_CHECK_ALONE = 2;
	private int status = NEEDS_INIT;

	/**
	 * This is the default constructor
	 */
	public Worker() {
		if (System.getProperty(XWPropertyDefs.CACHEDIR.toString()) == null) {
			System.setProperty(XWPropertyDefs.CACHEDIR.toString(), (new File(
					"cache")).getAbsolutePath());
		}
		logger = new Logger(this);
	}

	/**
	 * This shows application usage
	 * 
	 * @see xtremweb.common.CommandLineOptions#usage()
	 */
	private void usage() {
		CommandLineParser.usage("XWHEP computing element.",
				CommandLineOptions.CONFIG);
	}

	public void initialize(String[] a) {
		final String[] argv = a.clone();

		try {
			args = new CommandLineParser(argv);
		} catch (final Exception e) {
			usage();
		}

		try {
			XWTools.checkDir(System.getProperty(XWPropertyDefs.CACHEDIR
					.toString()));
			XWTools.checkDir(System.getProperty(XWPropertyDefs.CACHEDIR
					.toString()) + "/logs");
		} catch (final IOException e) {
			logger.fatal("Worker::initialize () : unrecoverable exception "
					+ e.toString());
		}
		if (System.getProperty(XWPropertyDefs.ALONE.toString()) == null) {
			status |= NEEDS_CHECK_ALONE;
		}
	}

	/**
	 * The <CODE>main</CODE> method contains a infinite loop to start and wait
	 * for a single <CODE>ThreadLaunch</CODE>.
	 */
	public static void main(String[] argv) {
		final Worker worker = new Worker();
		worker.initialize(argv);
		worker.run();
	}

	public void run() {

		if ((status & NEEDS_INIT) != 0) {

			final Properties defaults = new Properties();

			try {

				final InputStream def = this.getClass().getClassLoader()
						.getResourceAsStream("data/config.defaults");

				if (def == null) {

					throw new IOException();
				}
				defaults.load(def);
			} catch (final IOException e) {
			}

			try {
				setConfig((XWConfigurator) args
						.getOption(CommandLineOptions.CONFIG));
				getConfig().store();
			} catch (final Exception e) {
				logger.fatal("can retreive config file");
			}
		}

		getConfig().dump(System.out, "XWHEP Worker started ");

		final CommManager commThread = new CommManager();

		boolean configerror = false;
		String configerrmsg = null;
		String project = getConfig().getHost().getProject().trim();
		while (true) {
			configerror = false;
			configerrmsg = null;
			try {
				final String pass = getConfig().getUser().getPassword();
				getConfig().setUser(
						commThread.commClient().getUser(
								getConfig().getUser().getLogin()));
				getConfig().getUser().setPassword(pass);
				getConfig().getUser().setCertificate(null);

				if ((project != null) && (project.length() > 0)) {
					final UID groupuid = getConfig().getUser().getGroup();
					configerror = (groupuid == null);

					if (configerror) {
						throw new ConfigurationException(
								"worker is outside any group;"
										+ " can't manage jobs for \"" + project
										+ "\"");
					}

					final UserGroupInterface group = (UserGroupInterface) commThread
							.commClient().get(groupuid);
					configerror = (group == null);
					if (configerror) {
						throw new ConfigurationException(
								"can't retrieve worker group " + groupuid);
					}

					configerror = (project.compareTo(group.getLabel()) != 0);

					if (configerror) {
						throw new ConfigurationException(
								"worker is in group \"" + group.getLabel()
										+ "\";" + " can't manage jobs for \""
										+ project + "\"");
					}
				}
				break;
			} catch (final ConfigurationException e0) {
				configerror = true;
				configerrmsg = e0.getMessage();
				break;
			} catch (final Exception e) {
				configerror = true;
				configerrmsg = e.getMessage();
				logger.exception(e);
				try {
					Thread.sleep(5000);
				} catch (final Exception es) {
				}
			}
		}
		if (configerror) {
			logger.fatal("Configuration error : can not compute jobs for the project \""
					+ project
					+ "\""
					+ (configerrmsg != null ? " (" + configerrmsg + ")" : ""));
		}

		configerrmsg = null;
		project = null;

		commThread.setDaemon(true);
		commThread.start();

		ThreadLaunch launch = null;
		try {
			launch = new ThreadLaunch();
		} catch (final Exception ex) {
			logger.fatal("Worker::run () uncaught exception" + ex);
		}

		final ThreadAlive aliveThread = new ThreadAlive(getConfig());
		aliveThread.setDaemon(true);
		aliveThread.start();

		if (getConfig().http()) {
			//
			// This creates an HTTP server that helps the resource owner
			// to manage its worker
			//
			try {
				final HTTPServer httpServer = new HTTPServer();
				httpServer.initComm(getConfig(), new HTTPHandler());
				httpServer.addHandler(new HTTPStatHandler());
				httpServer.addHandler("/bitdew", new HTTPBitdewHandler());
				httpServer.start();
			} catch (final Exception ex) {
				logger.exception(
						"Worker::run () can't init HTTP Server for the worker page",
						ex);
			}
		}

		if (getConfig().getHost().isTracing()) {
			final XWTracer tracerThread = new XWTracer(true);
			tracerThread.setDaemon(true);
			tracerThread.start();
		}

		while (true) {
			try {

				if (launch == null) {
					try {
						launch = new ThreadLaunch();
					} catch (final Exception ex) {
						logger.fatal("Worker::run () uncaught exception " + ex);
					}
				}
				launch.start();
				launch.join();
			} catch (final InterruptedException e) {
			}

		}

	}

	/**
	 * @return the config
	 */
	public static XWConfigurator getConfig() {
		return config;
	}

	/**
	 * @param config
	 *            the config to set
	 */
	public static void setConfig(XWConfigurator config) {
		Worker.config = config;
	}
}
