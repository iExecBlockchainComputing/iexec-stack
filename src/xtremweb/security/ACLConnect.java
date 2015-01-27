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

package xtremweb.security;

import java.security.Principal;
import java.security.acl.Acl;
import java.security.acl.AclEntry;
import java.security.acl.NotOwnerException;
import java.security.acl.Permission;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.regex.Pattern;

import sun.security.acl.AclEntryImpl;
import sun.security.acl.AclImpl;
import sun.security.acl.PermissionImpl;
import sun.security.acl.PrincipalImpl;
import xtremweb.common.XWTools;

/**
 * This manages connection access control list (ACL) from IP and resolved names.<br />
 * This constructs ACL list from a comma separated regular expressions (regexp)<br />
 * 
 * Regexp examples :
 * <ul>
 * <li>accept all incoming connection : .*
 * <li>accept incoming connection from '168.192.*.*' : 168\.192\..*
 * <li>accept incoming connection from '*.in2P3.fr' : .*\.in2p3\.fr
 * <li>reject incoming connection from '168.192.*.*' : -168\.192\..*
 * <li>reject incoming connection from '*.in2P3.fr' : -.*\.in2p3\.fr
 * </ul>
 */
public class ACLConnect {

	private final String _MOINS = "-";
	private final String _PLUS = "+";

	private final String _ipregexp = "\\b((25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3})\\b";
	private final Pattern _IPPattern = Pattern.compile(_ipregexp);
	private final String _hostnameregexp = "^(www\\.|)(([a-zA-Z0-9])+\\.){1,4}[a-z]+$";
	private final Permission _connect = new PermissionImpl("CONNECT");
	private final Principal _owner = new PrincipalImpl("localhost");
	private Acl _acl = new AclImpl(_owner, "ConnectACL");

	/**
	 * This constructs a new object with ACL as provided as a comma separated
	 * string
	 * 
	 * @param acls
	 *            contains comma separated ACLs
	 */
	public ACLConnect(String acls) {

		final Collection<String> acl_regexps = XWTools.split(acls, ",");

		_acl = new AclImpl(_owner, "ConnectACL");
		final AclEntry allowConnections = new AclEntryImpl(_owner);
		allowConnections.addPermission(_connect);
		try {
			_acl.addEntry(_owner, allowConnections);
		} catch (final NotOwnerException e) {
			e.printStackTrace();
		}

		final Iterator<String> aclenum = acl_regexps.iterator();

		for (; aclenum.hasNext();) {

			String regexp = aclenum.next();

			final boolean negative = regexp.startsWith(_MOINS);
			if (negative) {
				regexp = regexp.substring(1);
			}

			if (regexp.startsWith(_PLUS)) {
				regexp = regexp.substring(1);
			}

			final Principal prince = new PrincipalImpl(regexp);
			final AclEntry entry = new AclEntryImpl(prince);
			entry.addPermission(_connect);
			if (negative) {
				entry.setNegativePermissions();
			}

			try {
				_acl.addEntry(_owner, entry);
			} catch (final NotOwnerException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * This checks whether rhost is a valid host IP or name
	 * 
	 * @param rhost
	 *            is a String containing IP or name to check
	 * @return true if rhost is a valid host IP or name
	 */
	public boolean isValidHost(String rhost) {
		return ((_IPPattern.matcher(rhost).matches() == false) || (rhost
				.matches(_hostnameregexp) == false));
	}

	/**
	 * This checks whether rhost can connect accordingly to ACLs
	 * 
	 * @param rhost
	 *            is a String containing IP or name
	 * @return true if rhost can connect
	 */
	public boolean canConnect(String rhost) {

		boolean ret = false;

		if (!isValidHost(rhost)) {
			return false;
		}
		final Enumeration<AclEntry> entries = _acl.entries();
		while (entries.hasMoreElements() && !ret) {
			final AclEntry entry = entries.nextElement();
			final Principal p = entry.getPrincipal();
			if (rhost.matches(p.getName()) && entry.checkPermission(_connect)) {
				ret = (entry.isNegative() == false);
			}
		}

		return ret;
	}

	/**
	 * This is for debug purposes only
	 */
	public static void main(String[] argv) {

		final ACLConnect aclregexp = new ACLConnect(argv[0]);

		final String rhost = argv[1];

		System.out.println("rhost = " + rhost);

		if (!aclregexp.isValidHost(rhost)) {
			System.out.println(rhost + " is not a valid address");
			return;
		}

		System.out.println(rhost + " can connect = "
				+ aclregexp.canConnect(rhost));
	}

}
