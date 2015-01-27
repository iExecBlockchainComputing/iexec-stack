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

/**
 * Date : March 13th, 2007<br />
 * @author Oleg Lodygensky (lodygens a t lal.in2p3.fr)
 *
 * This defines properties than can be read from config files.
 * @since 2.0.0
 */

package xtremweb.common;

public enum XWDBs {

	MYSQL, HSQLDB;

	/**
	 * This array defines possible database vendor names for config file. This
	 * is indexed by DB_* constants
	 */
	private static final String[] LABELS = { "mysql", "hsqldb" };

	public static final XWDBs DEFAULT = MYSQL;

	public static final String MEMENGINE = "mem";

	/**
	 * This converts database vendor to a String.
	 * 
	 * @param db
	 *            is the value to convert
	 * @return a string containing boolean value
	 */
	public static String toString(XWDBs db)
			throws ArrayIndexOutOfBoundsException {
		try {
			return LABELS[db.ordinal()];
		} catch (final Exception e) {
		}
		throw new ArrayIndexOutOfBoundsException("unknown database vendor : "
				+ db);
	}

	public static final String HSQLUSER = "sa";
	public static final String HSQLPASSWORD = "";

	/**
	 * This dumps enums to stdout
	 */
	public static void main(String[] argv) {
		for (final XWPropertyDefs i : XWPropertyDefs.values()) {
			System.out.println(i.toString() + " = " + i.ordinal()
					+ " valueOf() = " + XWPropertyDefs.valueOf(i.toString()));
		}
	}
}
