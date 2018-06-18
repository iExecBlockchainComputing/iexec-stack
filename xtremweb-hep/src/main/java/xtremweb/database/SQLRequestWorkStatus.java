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

import xtremweb.common.StatusEnum;
import xtremweb.common.TableColumns;
import xtremweb.common.UID;
import xtremweb.common.WorkInterface;

/**
 * This is a decorator pattern aiming to implement SQL requests used to retrieve
 * objects from DB. This implements the SQL request to retrieve works with the
 * given status from DB.<br />
 * This retrieves results from SQL statement:
 *
 * <pre>
 *   SELECT *
 *   FROM works
 *   WHERE status="&lt;aStatus&gt;"
 *     and works.isdeleted="false"
 *     and works.active="true"
 *   LIMIT 1000
 * </pre>
 *
 * @author <a href="mailto:lodygens /at\ lal.in2p3.fr">Oleg Lodygensky </a>
 * @since 5.8.0
 */
public class SQLRequestWorkStatus extends SQLRequest {

	/**
	 * This contains table names for the FROM part of the SQL request. There
	 * must not be a space after the comma
	 *
	 * @see DBConnPool#rowTableNames(String)
	 */
	private static final String TABLENAMES = WorkInterface.TABLENAME + " as " + MAINTABLEALIAS;

	private static final String CRITERIAS = MAINTABLEALIAS + "." + WorkInterface.Columns.STATUS + "='%s' AND "
			+ MAINTABLEALIAS + "." + WorkInterface.Columns.ACTIVE.toString() + "='" + Boolean.TRUE.toString() + "'";

	private StatusEnum status;

	/**
	 * This sets all to default values : select all rows, status=WAITING
	 */
	public SQLRequestWorkStatus() {
		super();
		setColumnSelection(ColumnSelection.selectAll);
		setTableNames(TABLENAMES);
		setTableName(TABLENAMES);
		setCriterias(CRITERIAS);
		status = StatusEnum.WAITING;
	}

	/**
	 * @param s
	 *            is the work status
	 */
	public SQLRequestWorkStatus(final StatusEnum s) throws IOException {
		this();
		status = s;
	}

	/**
	 * @param s
	 *            is the work status
	 * @param ctr
	 *            contains some more criteria
	 */
	public SQLRequestWorkStatus(final StatusEnum s, final String ctr) throws IOException {
		this();
		status = s;
		setMoreCriterias(ctr);
	}

	/**
	 * @param s
	 *            is the work status
	 * @param ownerUID
	 *            is the UID if the owner of the work
	 */
	public SQLRequestWorkStatus(final StatusEnum s, final UID ownerUID) throws IOException {
		this();
		status = s;
		setMoreCriterias(MAINTABLEALIAS + "." + TableColumns.OWNERUID.toString() + "='" + ownerUID + "'");
	}

	/**
	 * @param c
	 *            is the column selection
	 */
	public SQLRequestWorkStatus(final ColumnSelection c) throws IOException {
		this();
		setColumnSelection(c);
	}

	/**
	 * @param c
	 *            is the column selection
	 * @param ctr
	 *            contains some more criteria
	 */
	public SQLRequestWorkStatus(final ColumnSelection c, final String ctr) throws IOException {
		this();
		setColumnSelection(c);
		setMoreCriterias(ctr);
	}

	/**
	 * @param c
	 *            is the column selection
	 * @param s
	 *            is the work status
	 */
	public SQLRequestWorkStatus(final ColumnSelection c, final StatusEnum s) throws IOException {
		this();
		setColumnSelection(c);
		status = s;
	}

	/**
	 * @param c
	 *            is the column selection
	 * @param s
	 *            is the work status
	 * @param ctr
	 *            contains some more criteria
	 */
	public SQLRequestWorkStatus(final ColumnSelection c, final StatusEnum s, final String ctr) throws IOException {
		this();
		setColumnSelection(c);
		status = s;
		setMoreCriterias(ctr);
	}

	/**
	 * This retrieves this SQL criteria
	 *
	 * @return a String containing SQL criteria
	 */
	@Override
	public String getFullCriterias() throws IOException {
		final String mc = getMoreCriterias();
		return String.format(getCriterias(), status.toString()) + (mc == null ? "" : " AND " + mc);
	}
}
