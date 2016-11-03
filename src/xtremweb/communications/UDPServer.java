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

package xtremweb.communications;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.rmi.RemoteException;

import org.eclipse.jetty.server.Handler;

import xtremweb.common.BytePacket;
import xtremweb.common.XWConfigurator;
import xtremweb.common.XWPropertyDefs;
import xtremweb.common.XWTools;

/**
 * <p>
 * This class implements a generic UDP server<br />
 * This instanciates a new CommHandler on each new connection.
 * </p>
 *
 * <p>
 * Created: Jun 7th, 2005
 * </p>
 *
 * @see CommHandler
 * @author Oleg Lodygensky
 * @since RPCXW
 */

public class UDPServer extends CommServer {

	/**
	 * This is this thread name
	 */
	private static final String NAME = "UDPServer";
	/**
	 * This is the NIO server
	 */
	private DatagramChannel nioServer;
	/**
	 * This is the standard IO server
	 */
	private DatagramSocket ioServer = null;
	/**
	 * This tells whether to use NIO or not This contains value is read from
	 * config file
	 */
	private boolean nio;

	/**
	 * This constructs a new Thread to server UDP communications
	 */
	public UDPServer() {
		super(NAME);
	}

	/**
	 * This initializes communications
	 *
	 * @see CommServer#initComm(XWConfigurator, Handler)
	 */
	@Override
	public void initComm(final XWConfigurator prop, final Handler h) throws RemoteException {
		super.initComm(prop, h);

		setPort(Connection.UDPPORT.defaultPortValue());

		try {
			String proptxt = prop.getProperty(Connection.UDPPORT.toString());
			if (proptxt != null) {
				setPort(new Integer(proptxt.trim()).intValue());
			}

			setHandler(h);

			proptxt = prop.getProperty(XWPropertyDefs.JAVANIO);
			nio = true;
			if (proptxt != null) {
				nio = (proptxt.compareToIgnoreCase("true") == 0);
			}

			if (nio) {
				nioServer = DatagramChannel.open();
				nioServer.configureBlocking(false);
				nioServer.socket().bind(new InetSocketAddress(getPort()));
			} else {
				ioServer = new DatagramSocket(getPort());
			}

			setName(NAME + (nio ? "NIO" : ""));

			Runtime.getRuntime().addShutdownHook(new Thread(NAME + "Cleaner") {
				@Override
				public void run() {
					cleanup();
				}
			});
		} catch (final Exception e) {
			getLogger().fatal("UDPServer: Could not listen on port " + getPort() + " : " + e);
		}
	}

	/**
	 * This indefinitly waits for incoming connections. This uses the
	 * CommHandler to handle connections. Packet size is set to util#PACKETSIZE
	 *
	 * @see xtremweb.common.XWTools#PACKETSIZE
	 * @see CommServer#handler
	 */
	@Override
	public void run() {

		getLogger().debug("UDPServer started, listen on port: " + getPort());

		while (true) {

			try {
				if (nio) {
					final BytePacket buffer = new BytePacket();

					while (true) {
						buffer.getBuffer().clear();
						final SocketAddress remote = nioServer.receive(buffer.getBuffer());

						if (remote == null) {
							try {
								Thread.sleep(50);
							} catch (final InterruptedException e) {
							}

							continue;
						}

						buffer.getBuffer().clear();
						final CommHandler handler = (CommHandler) getHandler();
						handler.setPacket(nioServer, remote, buffer);
						handler.run();
					}
				} else {
					final byte[] buf = new byte[XWTools.PACKETSIZE];
					final DatagramPacket packet = new DatagramPacket(buf, buf.length);
					ioServer.receive(packet);
					boolean done = false;
					int doing = 0;
					while (done == false) {
						try {
							final CommHandler h = popConnection();
							h.setPacket(ioServer, packet);
							h.start();
							done = true;
						} catch (final OutOfMemoryError ome) {
							if (doing++ > 1000) {
								getLogger().fatal("UDPServer memory error ; can't do more");
							}

							getLogger().error("Still answering (" + doing + ") : " + ome);
							sleep(100);
						}
					}
				}
			} catch (final Exception e) {
				getLogger().fatal("UDPServer error " + e);
			}
		}
	} // run()

	/**
	 * This is called on program termination (CTRL+C) This deletes session from
	 * server
	 */
	private void cleanup() {
		try {
			getLogger().debug("UDPServer cleanup");
			if (nio) {
				nioServer.close();
			} else {
				ioServer.close();
			}
		} catch (final Exception e) {
			getLogger().error("can't clean up");
		}
	}

}
