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

package xtremweb.client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;

import xtremweb.common.Logger;
import xtremweb.common.StreamIO;
import xtremweb.communications.Connection;
import xtremweb.communications.SmartSocketsProxy;
import xtremweb.communications.XMLRPCCommand;

/**
 * This class implements a simple TCP server that waits for incoming
 * connections. This is not multi threaded and can manage one connection at a
 * time only. <br />
 * Incoming connection must send a valid XMLRPCCommand <br />
 * Created: Sept 14th, 2010
 * 
 * @author Oleg Lodygensky
 * @since 7.0.0
 */

public class Shell extends Thread {

	private final Logger logger;
	/**
	 * This is this thread name
	 */
	private static final String NAME = "Shell";
	/**
	 * This is the secured socket server
	 */
	private ServerSocket socketServer = null;

	private int port;
	private Client client;

	/**
	 * This constructs a new instance
	 * 
	 * @param label
	 *            is this thread label
	 */
	public Shell(String label) {
		super(label);
		logger = new Logger(this);
		port = -1;
	}

	/**
	 * This constructs a new instance
	 */
	public Shell() {
		this(NAME);
	}

	/**
	 * This initializes communications
	 */
	public void initComm(Client c) {

		client = c;

		try {
			final String proptxt = System.getProperty(Connection.XMLRPCPORT
					.toString());
			if (proptxt != null) {
				port = new Integer(proptxt.trim()).intValue();
			}

			socketServer = new ServerSocket(port);

			Runtime.getRuntime().addShutdownHook(
					new Thread(getName() + "Cleaner") {
						@Override
						public void run() {
							cleanup();
						}
					});
		} catch (final Exception e) {
			logger.fatal(getName() + ": could not listen on port " + port
					+ " : " + e);
		}
	}

	/**
	 * This indefinitely waits for incoming connections<br />
	 */
	@Override
	public void run() {

		logger.info("started, listening on port : " + port);

		int loop = 0;
		while (true) {

			Socket socket = null;
			String localip = null;
			String remoteip = null;
			try {
				if ((++loop % 1000) == 0) {
					loop = 0;
					System.gc();
				}
				logger.debug("Connection management : accepting");
				socket = socketServer.accept();

				process(socket);

			} catch (final Exception e) {
				logger.exception(e);
			} finally {
				try {
					socket.close();
				} catch (final Exception e) {
					logger.exception(e);
				} finally {
					socket = null;
					localip = null;
					remoteip = null;
				}
			}
		}

	}

	/**
	 * This reads from socket input stream
	 */
	private void process(Socket socket) throws ParseException, IOException,
			FileNotFoundException {

		InputStreamReader isreader = null;
		BufferedReader breader = null;
		PrintStream printStream = null;
		StreamIO io = null;
		try {
			isreader = new InputStreamReader(socket.getInputStream());
			breader = new BufferedReader(isreader);
			printStream = new PrintStream(socket.getOutputStream());
			client.setPrintStream(printStream);

			io = new StreamIO(new DataOutputStream(socket.getOutputStream()),
					new DataInputStream(socket.getInputStream()),
					socket.getSendBufferSize(), false);
			final XMLRPCCommand cmd = XMLRPCCommand.newCommand(io);
			if (cmd.getUser() == null) {
				printStream.println("ERROR : user must be set");
			} else {
				client.sendCommand(cmd, true);
			}

		} catch (final ClassNotFoundException e) {
			try {
				if (printStream != null) {
					printStream
							.println("ERROR : object or not found (or access denied)");
				}
			} catch (final Exception e2) {
			}

			logger.exception(e);
		} catch (final Exception e) {
			try {
				if (printStream != null) {
					printStream.println("ERROR : " + e);
				}
			} catch (final Exception e2) {
			}

			logger.exception(e);
		} finally {
			io = null;
			isreader = null;
			breader = null;
			printStream = null;
		}
	}

	/**
	 * This is called on program termination (CTRL+C) This deletes session from
	 * server
	 */
	protected void cleanup() {
		try {
			logger.debug("cleanup");
			socketServer.close();
		} catch (final Exception e) {
			logger.error("can't clean up");
		}
	}

	/*
	 * This is for testing only <br /> Usage : java xtremweb.worker.ThreadProxy
	 * <server address> <port>
	 */
	public static void main(String[] args) throws Exception {
		SmartSocketsProxy proxy = null;
		try {
			proxy = new SmartSocketsProxy(args[0], args[1],
					Integer.parseInt(args[2]), Boolean.parseBoolean(args[3]));
		} catch (final ArrayIndexOutOfBoundsException e) {
			System.out
					.println("Usage : java xtremweb.worker.ThreadProxy <hub address> <server address> <port> <true|false>");
			System.exit(1);
		}
		proxy.start();
	}
}
