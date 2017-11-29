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
import java.security.AccessControlException;
import java.security.InvalidKeyException;
import java.util.Collection;

import xtremweb.common.CommonVersion;
import xtremweb.common.HostInterface;
import xtremweb.common.MileStone;
import xtremweb.common.StatusEnum;
import xtremweb.common.TaskInterface;
import xtremweb.common.UserInterface;
import xtremweb.common.UserRightEnum;
import xtremweb.common.Version;
import xtremweb.common.WorkInterface;
import xtremweb.communications.XMLRPCCommandWorkRequest;
import xtremweb.database.SQLRequestWorkRequest;

/**
 * This is a simple scheduler
 *
 * @author Gille Fedak
 */

public class SimpleScheduler extends Scheduler {
	/**
	 * This aims to display some time stamps
	 */
	private MileStone mileStone;

	/**
	 * This does nothing
	 */
	public SimpleScheduler() {
		super();
		setMileStone(new MileStone(this.getClass()));
	}

	/**
	 * This retrieves waiting jobs from DB in FIFO mode
	 *
	 * @return null if no work available; a vector of works otherwise
	 * @since 5.8.0
	 * @see DBInterface#works(StatusEnum)
	 */
	@Override
	public Collection<WorkInterface> retrieve() throws IOException {
		return DBInterface.getInstance().works(StatusEnum.WAITING);
	}

	private static final Version CURRENTVERSION = CommonVersion.getCurrent();
	private static final String CURRENTVERSIONSTRING = CURRENTVERSION.toString();

	/**
	 * Get the first task that is pending
	 *
	 * @param host
	 *            is the worker definition
	 * @param user
	 *            is not used here (see MatchingScheduler)
	 * @return a Work matching host; null if no work matches this host -or no
	 *         pending work- found
	 * @exception IOException
	 *                is thrown on error
	 */
	@Override
	public synchronized WorkInterface select(final XMLRPCCommandWorkRequest command) throws IOException {

		if(command == null) {
			throw new IOException("command is null");
		}
		try {
			return DBInterface.getInstance().work(command);
		} catch (InvalidKeyException | AccessControlException e) {
			throw new IOException(e);
		}
	}

	/**
	 * @return the mileStone
	 */
	public MileStone getMileStone() {
		return mileStone;
	}

	/**
	 * @param mileStone
	 *            the mileStone to set
	 */
	public final void setMileStone(final MileStone mileStone) {
		this.mileStone = mileStone;
	}
}
