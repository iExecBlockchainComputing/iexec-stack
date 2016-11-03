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

/**
 * Created: Jun 2nd, 2005<br />
 *
 * This class implements the UDP client part to connect to the dispatcher
 * This class is not thread safe!
 *
 * @see xtremweb.dispatcher.TCPHandler
 * @author Oleg Lodygensky
 * @since RPCXW
 */

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.security.AccessControlException;
import java.security.InvalidKeyException;

import javax.net.ssl.SSLHandshakeException;

import org.xml.sax.SAXException;

import xtremweb.common.AppInterface;
import xtremweb.common.BytePacket;
import xtremweb.common.DataInterface;
import xtremweb.common.GroupInterface;
import xtremweb.common.HostInterface;
import xtremweb.common.SessionInterface;
import xtremweb.common.StreamIO;
import xtremweb.common.Table;
import xtremweb.common.TaskInterface;
import xtremweb.common.TraceInterface;
import xtremweb.common.UserGroupInterface;
import xtremweb.common.UserInterface;
import xtremweb.common.Version;
import xtremweb.common.WorkInterface;
import xtremweb.common.XMLHashtable;
import xtremweb.common.XMLVector;
import xtremweb.common.XWConfigurator;
import xtremweb.common.XWPropertyDefs;
import xtremweb.common.XWTools;

public class UDPClient extends CommClient {

	/**
	 * This manages the packet byte array
	 */
	private Selector selector;
	private int timeOut;
	private InetSocketAddress serverAddr;

	/**
	 * This is the channel used for I/O
	 */
	private DatagramChannel nioServer;
	/**
	 * This aims to determine communication time out
	 */
	private final SocketTimeoutException soTimeout;
	private BytePacket packet;

	/**
	 * This is the server host to send client requests to
	 */
	private InetAddress serverHost;
	/**
	 * This is the datagram packet used for I/O
	 */
	private DatagramPacket serverPacket;
	/**
	 * This is the datagram socket used for I/O
	 */
	private DatagramSocket serverSocket;

	private boolean waitAnswer;
	private boolean nio;

	/**
	 * This is the default constructor; this only calls super()
	 */
	public UDPClient() {
		super();
		nioServer = null;
		serverHost = null;
		serverPacket = null;
		serverSocket = null;
		soTimeout = new SocketTimeoutException();
	}

	/**
	 * This retreives this client port number
	 *
	 * @since 5.9.0
	 */
	@Override
	public int getPort() {
		if (getConfig() == null) {
			return -1;
		}
		return getConfig().getPort(Connection.UDPPORT);
	}

	/**
	 * This only sets the server name to connect to. This does not effectivly
	 * connects to server
	 *
	 * @param uri
	 *            is the URI to reach
	 */
	@Override
	public void open(final URI uri) throws UnknownHostException, NoRouteToHostException, SSLHandshakeException,
			SocketTimeoutException, IOException {

		final XWConfigurator config = getConfig();
		waitAnswer = false;
		String serverName = null;
		serverName = XWTools.getHostName(uri.getHost());
		int serverPort = uri.getPort();
		if (serverPort == -1) {
			serverPort = config.getPort(Connection.UDPPORT);
		}

		String proxyName = config.getProperty(XWPropertyDefs.PROXYSERVER);
		if ((proxyName != null) && (proxyName.trim().length() > 0)) {
			serverName = null;
			serverName = XWTools.getHostName(proxyName);
		}
		proxyName = null;

		String porttxt = config.getProperty(XWPropertyDefs.PROXYPORT);
		if ((porttxt != null) && (porttxt.trim().length() > 0)) {
			final int proxyPort = config.getPort(Connection.PROXYPORT);
			if (proxyPort > 0) {
				serverPort = proxyPort;
			}
		}
		porttxt = null;

		nio = config.nio();

		if (nio) {
			nioServer = DatagramChannel.open();
			nioServer.configureBlocking(false);
			serverAddr = new InetSocketAddress(serverName, serverPort);
			packet = new BytePacket();

			selector = Selector.open();
			nioServer.register(selector, SelectionKey.OP_READ);
			timeOut = config.getInt(XWPropertyDefs.SOTIMEOUT);
		} else {
			serverSocket = new DatagramSocket();
			serverSocket.setSoTimeout(config.getInt(XWPropertyDefs.SOTIMEOUT));

			serverSocket.setTrafficClass(0x08); // maximize throughput

			serverHost = InetAddress.getByName(serverName);
			packet = new BytePacket();

			final byte[] d = packet.getData();
			serverPacket = new DatagramPacket(d, d.length, serverHost, serverPort);
		}
		packet.reset();
	}

	/**
	 * This does nothing
	 */
	@Override
	public void close() {
	}

	/**
	 * This waits for a packet
	 *
	 * @see xtremweb.common.ByteStack#pack()
	 */
	protected void receive() throws IOException {
		if (waitAnswer == false) {
			throw new IOException("we can only receive answers...");
		}
		serverPacket.setData(packet.getData());
		serverSocket.receive(serverPacket);
		packet.setData(serverPacket.getData());
		waitAnswer = false;
	}

	/**
	 * This first packs the packet then sends it
	 *
	 * @see xtremweb.common.ByteStack#pack()
	 */
	protected void send() throws IOException {
		packet.pack();
		final byte[] buf = packet.getData();
		serverPacket.setData(buf);
		serverSocket.send(serverPacket);
		waitAnswer = true;
	}

	/**
	 * This puts the UserInterface and the IrRpc code
	 */
	@Override
	protected void write(final XMLRPCCommand cmd) throws IOException {
		packet.putObject(cmd);
		send();
	}

	/**
	 * This creates an object from channel
	 */
	@Override
	protected Table newTableInterface() throws InvalidKeyException, AccessControlException, IOException, SAXException {
		return super.newTableInterface(StreamIO.stream(packet.getString()));
	}

	/**
	 * This creates an object from channel
	 */
	@Override
	protected AppInterface newAppInterface()
			throws InvalidKeyException, AccessControlException, IOException, SAXException {
		return super.newAppInterface(StreamIO.stream(packet.getString()));
	}

	/**
	 * This creates an object from channel
	 */
	@Override
	protected DataInterface newDataInterface()
			throws InvalidKeyException, AccessControlException, IOException, SAXException {
		return super.newDataInterface(StreamIO.stream(packet.getString()));
	}

	/**
	 * This creates an object from channel
	 */
	@Override
	protected GroupInterface newGroupInterface()
			throws InvalidKeyException, AccessControlException, IOException, SAXException {
		return super.newGroupInterface(StreamIO.stream(packet.getString()));
	}

	/**
	 * This creates an object from channel
	 */
	@Override
	protected HostInterface newHostInterface()
			throws InvalidKeyException, AccessControlException, IOException, SAXException {
		return super.newHostInterface(StreamIO.stream(packet.getString()));
	}

	/**
	 * This creates an object from channel
	 */
	@Override
	protected SessionInterface newSessionInterface()
			throws InvalidKeyException, AccessControlException, IOException, SAXException {
		return super.newSessionInterface(StreamIO.stream(packet.getString()));
	}

	/**
	 * This creates an object from channel
	 */
	@Override
	protected TaskInterface newTaskInterface()
			throws InvalidKeyException, AccessControlException, IOException, SAXException {
		return super.newTaskInterface(StreamIO.stream(packet.getString()));
	}

	/**
	 * This creates an object from channel
	 */
	@Override
	protected TraceInterface newTraceInterface()
			throws InvalidKeyException, AccessControlException, IOException, SAXException {
		return super.newTraceInterface(StreamIO.stream(packet.getString()));
	}

	/**
	 * This creates an object from channel
	 */
	@Override
	protected UserInterface newUserInterface()
			throws InvalidKeyException, AccessControlException, IOException, SAXException {
		return super.newUserInterface(StreamIO.stream(packet.getString()));
	}

	/**
	 * This creates an object from channel
	 */
	@Override
	protected UserGroupInterface newUserGroupInterface()
			throws InvalidKeyException, AccessControlException, IOException, SAXException {
		return super.newUserGroupInterface(StreamIO.stream(packet.getString()));
	}

	/**
	 * This creates an object from channel
	 */
	@Override
	protected WorkInterface newWorkInterface()
			throws InvalidKeyException, AccessControlException, IOException, SAXException {
		return super.newWorkInterface(StreamIO.stream(packet.getString()));
	}

	/**
	 * This creates an object from channel
	 */
	@Override
	protected XMLVector newXMLVector() throws InvalidKeyException, AccessControlException, IOException, SAXException {
		return super.newXMLVector(StreamIO.stream(packet.getString()));
	}

	/**
	 * This creates an object from channel
	 */
	@Override
	protected Version newXMLVersion() throws InvalidKeyException, AccessControlException, IOException, SAXException {
		return super.newXMLVersion(StreamIO.stream(packet.getString()));
	}

	/**
	 * This creates an object from channel
	 */
	@Override
	protected XMLHashtable newXMLHashtable()
			throws InvalidKeyException, AccessControlException, IOException, SAXException {
		return super.newXMLHashtable(StreamIO.stream(packet.getString()));
	}

	/**
	 * This writes a file to socket
	 *
	 * @param f
	 *            is the file to send
	 */
	@Override
	public void writeFile(final File f) throws IOException {
		throw new IOException("UDP does not implement writeFile");
	}

	/**
	 * This reads a file from socket This is typically needed after a
	 * workRequest to get stdin and/or dirin files
	 *
	 * @param f
	 *            is the file to store received bytes
	 */
	@Override
	public void readFile(final File f) throws IOException {
		throw new IOException("UDP does not implement readFile");
	}

}
