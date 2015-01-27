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

package xtremweb.database;

import java.io.IOException;

import xtremweb.common.UID;
import xtremweb.common.UserInterface;
import xtremweb.security.XWAccessRights;

/**
 * This extends SQLRequestAccessible so that SQL request retrieves all readable
 * and executable rows
 * 
 * @author <A HREF="mailto:lodygens /at\ lal.in2p3.fr">Oleg Lodygensky </A>
 * @see xtremweb.security.XWAccessRights
 * @since 7.0.0
 */
public class SQLRequestExecutable extends SQLRequestAccessible {

	/**
	 * This is the default constructor
	 */
	public SQLRequestExecutable() {
		super();
		setAccesses();
	}

	/**
	 * This sets other and group accesses
	 */
	private void setAccesses() {
		setOtherAccess(XWAccessRights.OTHERREAD_INT
				| XWAccessRights.OTHEREXEC_INT);
		setGroupAccess(XWAccessRights.GROUPREAD_INT
				| XWAccessRights.GROUPEXEC_INT);
	}

	/**
	 * @param u
	 *            is the requesting user
	 * @param t
	 *            is the table name
	 */
	public SQLRequestExecutable(String t, UserInterface u) throws IOException {
		super(t, u);
		setAccesses();
	}

	/**
	 * @param u
	 *            is the requesting user
	 * @param t
	 *            is the table name
	 * @param uid
	 *            is the row UID
	 */
	public SQLRequestExecutable(String t, UserInterface u, UID uid) throws IOException {
		super(t, u, uid);
		setAccesses();
	}

	/**
	 * @param u
	 *            is the requesting user
	 * @param t
	 *            is the table name
	 * @param c
	 *            contains more criteria
	 */
	public SQLRequestExecutable(String t, UserInterface u, String c) throws IOException {
		super(t, u, c);
		setAccesses();
	}

	/**
	 * @param u
	 *            is the requesting user
	 * @param t
	 *            is the table name
	 * @param s
	 *            is the column selection
	 */
	public SQLRequestExecutable(String t, UserInterface u, ColumnSelection s)
			throws IOException {
		super(t, u, s);
		setAccesses();
	}

	/**
	 * @param u
	 *            is the requesting user
	 * @param t
	 *            is the table name
	 * @param s
	 *            is the column selection
	 * @param uid
	 *            is the UID of the object to retrieve
	 */
	public SQLRequestExecutable(String t, UserInterface u, ColumnSelection s, UID uid)
			throws IOException {
		super(t, u, s, uid);
		setAccesses();
	}

	/**
	 * @param u
	 *            is the requesting user
	 * @param t
	 *            is the table name
	 * @param s
	 *            is the column selection
	 * @param c
	 *            contains more criteria
	 */
	public SQLRequestExecutable(String t, UserInterface u, ColumnSelection s, String c)
			throws IOException {
		super(t, u, s, c);
		setAccesses();
	}
}
