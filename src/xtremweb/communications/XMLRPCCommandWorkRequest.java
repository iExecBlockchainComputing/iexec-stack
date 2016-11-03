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
import java.io.InputStream;
import java.rmi.RemoteException;
import java.security.AccessControlException;
import java.security.InvalidKeyException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import xtremweb.common.HostInterface;
import xtremweb.common.UID;
import xtremweb.common.XMLReader;
import xtremweb.common.XMLable;
import xtremweb.common.XWConfigurator;

/**
 * XMLRPCCommandWorkRequest.java
 *
 * Created: Nov 16th, 2006
 *
 * @author <a href="mailto:lodygens /a|t\ lal.in2p3.fr>Oleg Lodygensky</a>
 * @since 1.9.0
 */

/**
 * This class defines the XMLRPCCommand to send work definition
 */
public class XMLRPCCommandWorkRequest extends XMLRPCCommand {

	/**
	 * This is the RPC id
	 */
	public static final IdRpc IDRPC = IdRpc.WORKREQUEST;
	/**
	 * This is the XML tag
	 */
	public static final String THISTAG = IDRPC.toString();

	/**
	 * This constructs a new commadn
	 */
	public XMLRPCCommandWorkRequest() throws IOException {
		super(null, IDRPC);
	}

	/**
	 * This constructs a new command
	 *
	 * @param uri
	 *            is the URI to connect to
	 * @param h
	 *            defines the worker making this call
	 */
	public XMLRPCCommandWorkRequest(final URI uri, final HostInterface h) throws IOException {
		super(uri, IDRPC);
		setHost(h);
	}

	/**
	 * This constructs a new object from XML attributes received from input
	 * stream
	 *
	 * @param input
	 *            is the input stream
	 * @see xtremweb.common.XMLReader#read(InputStream)
	 */
	public XMLRPCCommandWorkRequest(final InputStream input) throws IOException, SAXException, InvalidKeyException {
		this();
		final XMLReader reader = new XMLReader(this);
		reader.read(input);
	}

	/**
	 * This is called to decode XML elements
	 *
	 * @see xtremweb.common.XMLReader#read(InputStream)
	 */
	@Override
	public void xmlElementStart(final String uri, final String tag, final String qname, final Attributes attrs)
			throws SAXException {
		xmlElementStartCheckUserAndHost(uri, tag, qname, attrs);
	}

	/**
	 * This sends this command to server and returns answer
	 *
	 * @param comm
	 *            is the communication channel
	 * @return always null
	 * @throws AccessControlException
	 * @throws InvalidKeyException
	 * @exception RemoteException
	 *                is thrown on comm error
	 */
	@Override
	public XMLable exec(final CommClient comm)
			throws IOException, ClassNotFoundException, SAXException, InvalidKeyException, AccessControlException {
		return comm.workRequest(this);
	}

	/**
	 * This is for testing only
	 */
	public static void main(final String[] argv) {
		try {
			final XWConfigurator config = new XWConfigurator(argv[0], false);
			final XMLRPCCommandWorkRequest cmd = new XMLRPCCommandWorkRequest(
					new URI(config.getCurrentDispatcher(), new UID()), config.getHost());
			cmd.test(argv);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

}
