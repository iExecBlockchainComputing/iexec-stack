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
import xtremweb.common.UserRightEnum;
import xtremweb.security.XWAccessRights;

/**
 * This is a decorator pattern aiming to implement SQL requests used to retrieve
 * objects from DB. This implements the SQL request to retrieve objects from DB,
 * for a given user, according to objects access rights. This retrieves all
 * columns, or UID only.
 *
 * @author <A HREF="mailto:lodygens /at\ lal.in2p3.fr">Oleg Lodygensky </A>
 * @see ColumnSelection
 * @see xtremweb.security.XWAccessRights
 * @since 7.0.0
 */
public abstract class SQLRequestAccessible extends SQLRequest {
	/**
	 * This contains table names for the FROM part of the SQL request. There
	 * must not be a space after the comma
	 *
	 * @see DBConnPool#rowTableNames(String)
	 */
	private static final String TABLENAMES = "%s as " + MAINTABLEALIAS + "," + UserInterface.TABLENAME;
	private static final String TABLEUSERS = UserInterface.TABLENAME + " as " + MAINTABLEALIAS + ","
			+ UserInterface.TABLENAME;

	/**
	 * This contains criteria for the WHERE part of the SQL request.
	 */
	public static final String SQLCRITERIAS = UserInterface.TABLENAME + ".uid='%s'" + " AND users.isdeleted='false'"
			+ " AND (" + " users.uid=maintable.uid" // only for "users" table :
													// a user can retrieve
													// itself
			+ " OR users.uid=maintable.owneruid" // is the requester the owner?
			+ " OR users.userrightid >= " + UserRightEnum.SUPER_USER.ordinal()    // is this an admin

			// next : a VWORKER can access an object that has the sticky bit
			+ " OR (  (users.rights='" + UserRightEnum.VWORKER_USER + "'"
			+ "     OR users.userrightid >= " + UserRightEnum.ADVANCED_USER.ordinal() +")"    // is this a privileged user
			+ "  AND maintable.accessrights & " + XWAccessRights.STICKYBIT_INT + " = " + XWAccessRights.STICKYBIT_INT
			+ ")"

			// next :is the object accessible for all users
			+ " OR (maintable.accessrights & %d = %d)"
			+ " OR ("// finally, is the object defined in a group
			+ "     (maintable.accessrights & %d = %d)" + "     AND users.usergroupuid IN (" // if
																								// so,
																								// is
																								// the
																								// requester
																								// member
																								// of
																								// the
																								// object
																								// group
																								// ?
			+ "        SELECT usergroups.uid" + " FROM users,usergroups"
			+ "        WHERE usergroups.uid=users.usergroupuid" + " AND users.uid=maintable.owneruid" + "      )" // end
																													// IN
																													// "SELECT..."
			+ "    )" // end OR "is the object defined in a group"
			+ ")"; // end AND

	/**
	 * This is the equivalent of CRITERIAS for HyperSQL This is because HSQLDB
	 * does not accept '&amp;' as bitwise operator :(
	 *
	 * @see #SQLCRITERIAS
	 */
	public static final String CRITERIAS_HSQL = UserInterface.TABLENAME + ".uid='%s'" + " AND users.isdeleted='false'"
			+ " AND (" + " users.uid=maintable.uid" + " OR users.uid=" + "maintable.owneruid" + " OR users.rights='"
			+ UserRightEnum.SUPER_USER + "'" + " OR ((users.rights='" + UserRightEnum.VWORKER_USER + "' OR users.userrightid >= " + UserRightEnum.ADVANCED_USER.ordinal() +")" + " AND bitand("
			+ "maintable.accessrights, " + XWAccessRights.STICKYBIT_INT + ") = " + XWAccessRights.STICKYBIT_INT + ")"
			+ " OR (bitand(" + "maintable.accessrights, %d) = %d)" + " OR ((bitand("
			+ "maintable.accessrights, %d) = %d)" + " AND users.usergroupuid IN (" + " SELECT usergroups.uid" + " FROM "
			+ getDbName() + ".users," + getDbName() + ".usergroups" + " WHERE usergroups.uid=users.usergroupuid"
			+ " AND users.uid=" + MAINTABLEALIAS + ".owneruid" + ")))";

	private int otherAccess;
	private int groupAccess;

	/**
	 * This is the default constructor
	 */
	protected SQLRequestAccessible() {
		super();
		setColumnSelection(ColumnSelection.selectUID);
		setTableNames(TABLENAMES);
		if (getHsqldb()) {
			setCriterias(CRITERIAS_HSQL);
		} else {
			setCriterias(SQLCRITERIAS);
		}
		setOtherAccess(XWAccessRights.NONE_INT);
		setGroupAccess(XWAccessRights.NONE_INT);
	}

	/**
	 * @param u
	 *            is the requesting user
	 * @param t
	 *            is the table name
	 */
	public SQLRequestAccessible(final String t, final UserInterface u) throws IOException {
		this();
		setTableName(t);
		if (getTableName() == null) {
			throw new IOException("tableName is null ?!?!");
		}

		if (getTableName().compareToIgnoreCase(UserInterface.TABLENAME) == 0) {
			setTableNames(TABLEUSERS);
		}
		setUser(u);
	}

	/**
	 * @param u
	 *            is the requesting user
	 * @param t
	 *            is the table name
	 * @param uid
	 *            is the row UID
	 */
	public SQLRequestAccessible(final String t, final UserInterface u, final UID uid) throws IOException {
		this(t, u);
		if (uid != null) {
			setMoreCriterias(MAINTABLEALIAS + ".uid='" + uid + "'");
		}
	}

	/**
	 * @param u
	 *            is the requesting user
	 * @param t
	 *            is the table name
	 * @param c
	 *            contains more criterias
	 */
	public SQLRequestAccessible(final String t, final UserInterface u, final String c) throws IOException {
		this(t, u);
		setMoreCriterias(c);
	}

	/**
	 * @param u
	 *            is the requesting user
	 * @param t
	 *            is the table name
	 * @param s
	 *            is the column selection
	 */
	public SQLRequestAccessible(final String t, final UserInterface u, final ColumnSelection s) throws IOException {
		this(t, u);
		setColumnSelection(s);
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
	public SQLRequestAccessible(final String t, final UserInterface u, final ColumnSelection s, final UID uid)
			throws IOException {
		this(t, u, uid);
		setColumnSelection(s);
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
	public SQLRequestAccessible(final String t, final UserInterface u, final ColumnSelection s, final String c)
			throws IOException {
		this(t, u, c);
		setColumnSelection(s);
	}

	/**
	 * This retrieves this SQL criteria
	 *
	 * @return a String containing SQL criteria
	 */
	@Override
	public String getFullCriterias() throws IOException {

		final String ret = String.format(getCriterias(), getUser().getUID().toString(), getOtherAccess(),
				getOtherAccess(), getGroupAccess(), getGroupAccess());
		final String mc = getMoreCriterias();
		return ret + (mc == null ? "" : " AND " + mc);
	}

	/**
	 * @return the otherAccess
	 */
	public int getOtherAccess() {
		return otherAccess;
	}

	/**
	 * @param otherAccess
	 *            the otherAccess to set
	 */
	public final void setOtherAccess(final int otherAccess) {
		this.otherAccess = otherAccess;
	}

	/**
	 * @return the groupAccess
	 */
	public int getGroupAccess() {
		return groupAccess;
	}

	/**
	 * @param groupAccess
	 *            the groupAccess to set
	 */
	public final void setGroupAccess(final int groupAccess) {
		this.groupAccess = groupAccess;
	}
}
