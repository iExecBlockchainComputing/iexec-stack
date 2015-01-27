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

package xtremweb.common;

/**
 * UserRights.java
 *
 * Created: Nov 3rd, 2003
 *
 * @author <a href="mailto:lodygens /at\ .in2p3.fr>Oleg Lodygensky</a>
 * @version %I%, %G%
 */

/**
 * This class describes static variables that define user right levels
 */

public enum UserRightEnum {

	/**
	 * no right
	 */
	NONE,
	/**
	 * retrieve jobs list
	 */
	LISTJOB,
	/**
	 * submit/update job
	 */
	INSERTJOB,
	/**
	 * retrieve job
	 */
	GETJOB,
	/**
	 * delete job
	 */
	DELETEJOB,
	/**
	 * retrieve datas list
	 */
	LISTDATA,
	/**
	 * send/update data
	 */
	INSERTDATA,
	/**
	 * retrieve data
	 */
	GETDATA,
	/**
	 * delete data
	 */
	DELETEDATA,
	/**
	 * retrieve groups list
	 */
	LISTGROUP,
	/**
	 * send/update group
	 */
	INSERTGROUP,
	/**
	 * retrieve group
	 */
	GETGROUP,
	/**
	 * delete group
	 */
	DELETEGROUP,
	/**
	 * retrieve sessions list
	 */
	LISTSESSION,
	/**
	 * send/update session
	 */
	INSERTSESSION,
	/**
	 * retrieve session
	 */
	GETSESSION,
	/**
	 * delete session
	 */
	DELETESESSION,
	/**
	 * retrieve hosts list
	 */
	LISTHOST,
	/**
	 * retrieve host
	 */
	GETHOST,
	/**
	 * Retrieve users list
	 */
	LISTUSER,
	/**
	 * retrieve user
	 */
	GETUSER,
	/**
	 * retrieve user groups list
	 */
	LISTUSERGROUP,
	/**
	 * retrieve user group
	 */
	GETUSERGROUP,
	/**
	 * send/update app
	 */
	INSERTAPP,
	/**
	 * delete app
	 */
	DELETEAPP,
	/**
	 * retrieve applications list
	 */
	LISTAPP,
	/**
	 * retrieve application
	 */
	GETAPP,
	/**
	 * this is the default user right
	 */
	STANDARD_USER,
	/**
	 * This allows user rights delegation to workers e.g. workers must be able
	 * to update works and change works status (among others)
	 */
	UPDATEWORK,
	/**
	 * this is the user rights needed by workers
	 */
	WORKER_USER,
	/**
	 * This denotes virtualized worker user rights. This aims to allow access
	 * objects that has the sticky bit.
	 * 
	 * @since 8.0.0
	 * @see xtremweb.security.XWAccessRights#STICKYBIT
	 */
	VWORKER_USER,
	/**
	 * this is a advanced rights : send/update a user
	 */
	INSERTUSER,
	/**
	 * this is a advanced rights : delete user
	 */
	DELETEUSER,
	/**
	 * this is the advanced right
	 */
	ADVANCED_USER,
	/**
	 * this is a privileged right : send/update host
	 */
	INSERTHOST,
	/**
	 * this is a privileged right : delete host
	 */
	DELETEHOST,
	/**
	 * this is a privileged right : send/update user group
	 */
	INSERTUSERGROUP,
	/**
	 * this is a privileged right : delete user group
	 */
	DELETEUSERGROUP,
	/**
	 * all rights
	 */
	SUPER_USER;

	public static final UserRightEnum LAST = SUPER_USER;
	public static final int SIZE = LAST.ordinal() + 1;

	/**
	 * This tests whether this is higher than the provided parameter
	 * 
	 * @param c
	 *            is the value to compare
	 * @return true if this is higher than the parameter, false otherwise
	 */
	public boolean higherThan(UserRightEnum c) {
		return this.ordinal() > c.ordinal();
	}

	/**
	 * This tests whether this is higher or equal than the provided parameter
	 * 
	 * @param c
	 *            is the value to compare
	 * @return true if this is higher or equal than the parameter, false
	 *         otherwise
	 */
	public boolean higherOrEquals(UserRightEnum c) {
		return this.ordinal() >= c.ordinal();
	}

	/**
	 * This tests whether this is lower than the provided parameter
	 * 
	 * @param c
	 *            is the value to compare
	 * @return true if this is lower than the parameter, false otherwise
	 */
	public boolean lowerThan(UserRightEnum c) {
		return this.ordinal() < c.ordinal();
	}

	/**
	 * This tests whether this is lower or equal than the provided parameter
	 * 
	 * @param c
	 *            is the value to compare
	 * @return true if this is lower or equal than the parameter, false
	 *         otherwise
	 */
	public boolean lowerOrEquals(UserRightEnum c) {
		return this.ordinal() <= c.ordinal();
	}

	/**
	 * This tests whether this equals the provided parameter
	 * 
	 * @param c
	 *            is the value to compare
	 * @return true if this equals the parameter, false otherwise
	 */
	public boolean doesEqual(UserRightEnum c) {
		return this.ordinal() == c.ordinal();
	}

	/**
	 * This retrieves an Columns from its integer value
	 * 
	 * @param v
	 *            is the integer value of the Columns
	 * @return an Columns
	 */
	public static UserRightEnum fromInt(int v) throws IndexOutOfBoundsException {
		for (final UserRightEnum c : UserRightEnum.values()) {
			if (c.ordinal() == v) {
				return c;
			}
		}
		throw new IndexOutOfBoundsException("unvalid status " + v);
	}

	public static void main(String[] argv) {
		for (final UserRightEnum r : UserRightEnum.values()) {
			System.out.println(r.toString() + " = " + r.ordinal());
		}
	}

}
