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
 * XWReturnCode.java
 *
 * Created: Mar 8th, 2007
 *
 * @author <a href="mailto:lodygens /at\ .in2p3.fr>Oleg Lodygensky</a>
 * @version %I%, %G%
 */

/**
 * This defines XtremWeb return codes
 */

public enum XWReturnCode {

	/**
	 * This defines the success return code
	 */
	SUCCESS,
	/**
	 * This defines the fatal return code
	 */
	FATAL,
	/**
	 * This defines the error return code
	 */
	ERROR,
	/**
	 * This defines the warning return code
	 */
	WARNING,
	/**
	 * This defines the connection error return code This means that the
	 * XtremWeb server is not reacheable
	 */
	CONNECTION,
	/**
	 * This defines the SSL handshake error return code This means that the user
	 * doesn't have the valid server public key
	 */
	HANDSHAKE,
	/**
	 * This defines the authentication error return code This means that the
	 * user doesn't have a valid user credential (login/password, certificate)
	 */
	AUTHENTICATION,
	/**
	 * This defines the authorization error return code This means that the user
	 * doesn't have access to the requested object
	 *
	 * @since 7.5.0
	 */
	AUTHORIZATION,
	/**
	 * This defines the parsing error return code This means there's an error on
	 * command line option
	 */
	PARSING,
	/**
	 * This defines the disk error return code This means there's an I/O error
	 */
	DISK,
	/**
	 * This defines the restart return code
	 */
	RESTART,
	/**
	 * This defines the object not found error return code This means there's no
	 * such an object on server side
	 *
	 * @since 5.8.0
	 */
	NOTFOUND,
	/**
	 * This defines the wall clock time error return code This means the job has
	 * reached its wall clock time
	 *
	 * @since 8.2.0
	 */
	WALLCLOCKTIME,
	/**
	 * This defines the restart return code
	 */
	UNKNOWN;

	/**
	 * This retreives an Columns from its integer value
	 *
	 * @param v
	 *            is the integer value of the Columns
	 * @return an Columns
	 */
	public static XWReturnCode fromInt(final int v) {
		for (final XWReturnCode c : XWReturnCode.values()) {
			if (c.ordinal() == v) {
				return c;
			}
		}
		return UNKNOWN;
	}

}
