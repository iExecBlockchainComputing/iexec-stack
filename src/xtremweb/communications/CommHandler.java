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

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.DatagramChannel;
import java.security.AccessControlException;
import java.security.InvalidKeyException;

import javax.net.ssl.SSLSocket;

import org.eclipse.jetty.server.Handler;

import xtremweb.common.BytePacket;

/**
 * This is just an alias to ServerAPI This should not have been a class because
 * RMIHandler extends UnicastRemoteObject
 *
 * @author Oleg Lodygensky
 * @since RPCXW
 */
public interface CommHandler extends Handler {

	/**
	 * This is only needed for handlers that extends java.lang.Thread Such
	 * handlers have the start() method; others (i.e. not extending
	 * java.lang.Thread) must implements an empty method Thanks to this
	 * declaration we can call java.lang.Thread#start()
	 */
	@Override
	void start();

	/**
	 * This is only needed for handlers that extends java.lang.Thread Such
	 * handlers have the start() method; others (i.e. not extending
	 * java.lang.Thread) must implements an empty method Thanks to this
	 * declaration we can call java.lang.Thread#run()
	 */
	void run();

	/**
	 * This closes communication channels
	 */
	void close();

	/**
	 * This sets the current server. This is used to pushConnection back to
	 * server
	 */
	void setCommServer(CommServer s);

	/**
	 * This resets the communication channel
	 */
	void resetSockets() throws IOException, InvalidKeyException, AccessControlException;

	/**
	 * This sets the communication socket for TCP comms
	 *
	 * @param s
	 *            is the socket
	 */
	void setSocket(Socket s) throws IOException, InvalidKeyException, AccessControlException;

	/**
	 * This sets the secured communication socket for TCP comms
	 *
	 * @param s
	 *            is the socket
	 */
	void setSocket(SSLSocket s) throws IOException, InvalidKeyException, AccessControlException;

	/**
	 * This sets the datagram packet and socket for UDP comms
	 *
	 * @param s
	 *            is the socket
	 * @param p
	 *            is the packet
	 */
	void setPacket(DatagramSocket s, DatagramPacket p) throws IOException, InvalidKeyException, AccessControlException;

	/**
	 * This sets the server addr and byte packet
	 *
	 * @param c
	 *            is the channel to server
	 * @param r
	 *            is the client address
	 * @param p
	 *            is the packet
	 */
	void setPacket(DatagramChannel c, SocketAddress r, BytePacket p)
			throws IOException, InvalidKeyException, AccessControlException;
}
