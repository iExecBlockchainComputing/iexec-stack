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

import java.sql.ResultSet;
import java.text.ParseException;
import java.util.Date;

import xtremweb.security.XWAccessRights;

/**
 * This class describes the base columns for all manageable objects.
 * 
 * Created: November 22th, 2012
 * 
 * @author <a href="mailto:oleg.lodygensky /at\ .in2p3.fr>Oleg Lodygensky</a>
 * @version %I%, %G%
 * @since 8.2.0
 */

public enum TableColumns implements XWBaseColumn {

	/**
	 * This is the column index of the UNIQUE ID.<br />
	 * It <b>must</b> be the first attribute since we use it as primary key.
	 * 
	 * @see Table
	 */
	UID {
		/**
		 * This creates a new UID from string
		 */
		@Override
		public UID fromString(String v) {
			return new UID(v);
		}
	},
	/**
	 * This is the column index of the UID of the owner.<br />
	 * 
	 * @since 5.8.0
	 */
	OWNERUID {
		@Override
		public UID fromString(String v) {
			return new UID(v);
		}
	},
	/**
	 * This is the column index of this work access rights if any
	 * 
	 * @since 5.8.0
	 */
	ACCESSRIGHTS {
		@Override
		public XWAccessRights fromString(String v) throws ParseException {
			return new XWAccessRights(v);
		}
	},
	/**
	 * This is the column index of the error message, if any
	 * 
	 * @since 9.0.0
	 */
	ERRORMSG {
		/**
		 * This creates an object from String representation for this column
		 * value This cleans the parameter to ensure SQL compliance
		 * 
		 * @param v
		 *            the String representation
		 * @return a Boolean representing the column value
		 * @throws Exception
		 *             is thrown on instantiation error
		 */
		@Override
		public String fromString(final String v) {
			final String val = v;
			return val.replaceAll("[\\n\\s\'\"]+", "_");
		}
	},
	/**
	 * This is the column index of the last update date. This has a timestamp
	 * SQL type which is automatically updated
	 * 
	 * @link 
	 *       http://dev.mysql.com/doc/refman/5.0/en/timestamp-initialization.html
	 * @since 9.0.0
	 */
	MTIME {
		/**
		 * This creates an object from String representation for this column
		 * value
		 * 
		 * @param v
		 *            the String representation
		 * @return a Boolean representing the column value
		 * @throws Exception
		 *             is thrown on instantiation error
		 */
		@Override
		public Date fromString(String v) {
			return XWTools.getSQLDateTime(v);
		}
	};

	public static final int SIZE = TableColumns.values().length;

	/**
	 * This retrieves the ordinal
	 * 
	 * @return the ordinal
	 * @since 8.2.0
	 */
	public int getOrdinal() {
		return this.ordinal();
	}

	/**
	 * This retrieves an Columns from its integer value
	 * 
	 * @param v
	 *            is the integer value of the Columns
	 * @return an Columns
	 */
	public static TableColumns fromInt(int v) throws IndexOutOfBoundsException {
		for (final TableColumns c : TableColumns.values()) {
			if (c.ordinal() == v) {
				return c;
			}
		}
		throw new IndexOutOfBoundsException("unvalid Columns value " + v);
	}

	/**
	 * This instantiates a representation of the enumeration
	 * 
	 * @param r
	 *            is the String representation
	 * @return this enumeration representation
	 * @throws Exception
	 *             is thrown on error
	 */
	public abstract Object fromString(String r) throws Exception;

	/**
	 * This creates a new UID from DB result set
	 * 
	 * @param rs
	 *            is the DB result set
	 * @return this enumeration representation
	 * @throws Exception
	 *             is thrown on error
	 */
	public final Object fromResultSet(ResultSet rs) throws Exception {
		return this.fromString(rs.getString(this.toString()));
	}
}
