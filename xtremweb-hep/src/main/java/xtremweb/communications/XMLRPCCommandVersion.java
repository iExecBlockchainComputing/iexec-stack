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

import org.xml.sax.SAXException;

import xtremweb.common.UID;
import xtremweb.common.UserInterface;
import xtremweb.common.XMLReader;
import xtremweb.common.XMLable;
import xtremweb.common.XWConfigurator;

/**
 * XMLRPCCommandVersion.java
 *
 * Created: March 2nd, 2009
 *
 * @author <a href="mailto:lodygens /a|t\ lal.in2p3.fr>Oleg Lodygensky</a>
 * @since 5.6.1
 * @deprecated since 5.8.0 introduces the XWHEP XML root element
 * @see #xtremweb.common.XMLable#xmlRootElement(boolean)
 */

/**
 * This class defines the XMLRPCCommand to retreive object definition This does
 * not retreive Tasks from server
 */
public class XMLRPCCommandVersion extends XMLRPCCommand {

	/**
	 * This is the RPC id
	 */
	public static final IdRpc IDRPC = IdRpc.VERSION;
	/**
	 * This is the XML tag
	 */
	public static final String THISTAG = IDRPC.toString();

	/**
	 * This constructs a new command
	 */
	public XMLRPCCommandVersion() throws IOException {
		super(null, IDRPC);
	}

	/**
	 * This constructs a new command
	 */
	protected XMLRPCCommandVersion(final URI uri, final IdRpc id) throws IOException {
		super(uri, id);
	}

	/**
	 * This constructs a new command
	 *
	 * @param uri
	 *            is the URI of the group to retreive
	 */
	public XMLRPCCommandVersion(final URI uri) throws IOException {
		super(uri, IDRPC);
	}

	/**
	 * This constructs a new command
	 *
	 * @param uri
	 *            is the URI of the group to retreive
	 */
	public XMLRPCCommandVersion(final URI uri, final UserInterface u) throws IOException {
		this(uri);
		setUser(u);
	}

	/**
	 * This constructs a new object from XML attributes received from input
	 * stream
	 *
	 * @param input
	 *            is the input stream
	 * @throws IOException
	 *             on XML error
	 * @throws InvalidKeyException
	 * @see xtremweb.common.XMLReader#read(InputStream)
	 */
	public XMLRPCCommandVersion(final InputStream input) throws IOException, SAXException, InvalidKeyException {
		this();
		final XMLReader reader = new XMLReader(this);
		reader.read(input);
	}

	/**
	 * This sends this command to server and returns answer
	 *
	 * @param comm
	 *            is the communication channel
	 * @return an object depeding on URI (an app, a data...)
	 * @throws AccessControlException
	 * @throws InvalidKeyException
	 * @exception RemoteException
	 *                is thrown on comm error
	 */
	@Override
	public XMLable exec(final CommClient comm)
			throws IOException, SAXException, InvalidKeyException, AccessControlException {
		return comm.version(this);
	}

	/**
	 * This is for testing only. The first argument must be a valid client
	 * configuration file. Without a second argument, this dumps an
	 * XMLRPCCommandVersion object. If the second argument is an XML file
	 * containing a description of an XMLRPCCommandVersion this creates an
	 * object from XML description and dumps it. <br />
	 * Usage : java -cp xtremweb.jar
	 * xtremweb.communications.XMLRPCCommandVersion aConfigFile
	 * [anXMLDescriptionFile]
	 */
	public static void main(final String[] argv) {
		try {
			final XWConfigurator config = new XWConfigurator(argv[0], false);
			final XMLRPCCommandVersion cmd = new XMLRPCCommandVersion(new URI(config.getCurrentDispatcher(), new UID()),
					config.getUser());
			cmd.test(argv);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}
