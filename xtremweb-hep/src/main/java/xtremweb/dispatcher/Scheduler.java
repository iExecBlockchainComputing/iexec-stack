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

package xtremweb.dispatcher;

import java.io.IOException;
import java.util.Collection;

import xtremweb.common.Logger;
import xtremweb.common.MileStone;
import xtremweb.common.WorkInterface;
import xtremweb.communications.XMLRPCCommandWorkRequest;

/**
 * This is the abstract class that defines XtremWeb scheduler main methods set.
 * <br />
 * Since XWHEP 5.8.0 this abstract class defines two responsabilities
 * <ul>
 * <li>retreive job from DB (this is the retreive() method)
 * <li>select a job for a worker (this is the select() method)
 * </ul>
 */
public abstract class Scheduler {

	protected Logger logger;

	protected Scheduler() {
		setLogger(new Logger(this));
		mileStone = new MileStone(this.getClass());
	}

	/**
	 * This aims to display some time stamps
	 */
	protected final MileStone mileStone;

	/**
	 * This retrieves waiting jobs from DB
	 *
	 * @return null if no work available; a MobileWork otherwise
	 * @since 5.8.0
	 */
	public abstract Collection<WorkInterface> retrieve() throws IOException;

	/**
	 * This tries to find a job that matches the given host and host owner. The
	 * found job must:
	 * <ul>
	 * <li>match the host definition (arch, OS etc)
	 * <li>the host owner must have the right to execute the job
	 * </ul>
	 *
	 * @return a Work matching host; null if no work matches this host -or no
	 *         pending work- found
	 */
	public abstract WorkInterface select(XMLRPCCommandWorkRequest command) throws IOException;

	/**
	 * @return the logger
	 */
	public Logger getLogger() {
		return logger;
	}

	/**
	 * @param logger
	 *            the logger to set
	 */
	public void setLogger(final Logger logger) {
		this.logger = logger;
	}

}
