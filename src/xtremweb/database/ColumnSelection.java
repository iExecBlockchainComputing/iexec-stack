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

import xtremweb.database.SQLRequest;

/**
 * This helps to retrieve table columns from DB. This can be used to retrieve
 * either all columns, or UID only.
 * 
 * @author <A HREF="mailto:lodygens /at\ lal.in2p3.fr">Oleg Lodygensky </A>
 * @since 5.8.0
 */
public class ColumnSelection {

	/**
	 * This enumerates two selections : UID or *
	 */
	public enum SelectionType {
		/**
		 * This retreives UID column only
		 */
		UID,
		/**
		 * This retreives all columns
		 */
		ALL,
		/**
		 * This retreives row count
		 */
		COUNT;

		/**
		 * This retreives a string to select rows in SQL request
		 * 
		 * @see #selectionStrings
		 * @return a non formatted string to select rows in SQL request
		 */
		public String selectionString() {
			return selectionStrings[this.ordinal()];
		}
	};

	/**
	 * This are the strings to use in SQL request
	 */
	public static final String[] selectionStrings = {
			SQLRequest.MAINTABLEALIAS + ".uid as " + SQLRequest.UIDLABEL,
			SQLRequest.MAINTABLEALIAS + ".uid as " + SQLRequest.UIDLABEL + ", "
					+ SQLRequest.MAINTABLEALIAS + ".*", "count(*) as count" };

	private final SelectionType selection;

	/**
	 * This is used to retreive UID column only
	 */
	public static final ColumnSelection selectUID = new ColumnSelection(
			SelectionType.UID);
	/**
	 * This is used to retreive all columns
	 */
	public static final ColumnSelection selectAll = new ColumnSelection(
			SelectionType.ALL);
	/**
	 * This is used to retreive row count
	 */
	public static final ColumnSelection selectCount = new ColumnSelection(
			SelectionType.COUNT);

	/**
	 * This is the default constructor. This sets selection to SELECTIONTYPE.ALL
	 */
	public ColumnSelection() {
		selection = SelectionType.ALL;
	}

	/**
	 * This is constructor for the given selection
	 * 
	 * @param s
	 *            is the selection type (ALL or UID)
	 */
	public ColumnSelection(SelectionType s) {
		selection = s;
	}

	/**
	 * This retreives the selection type
	 */
	public SelectionType selectionType() {
		return selection;
	}

	/**
	 * This retreives the selection string
	 */
	@Override
	public String toString() {
		return selection.toString();
	}

	/**
	 * This retreives the selection string
	 * 
	 * @return unformatted selection string
	 */
	public String selectionString() {
		return selection.selectionString();
	}
}
