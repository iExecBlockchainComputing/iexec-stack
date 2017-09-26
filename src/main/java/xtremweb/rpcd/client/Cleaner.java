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

package xtremweb.rpcd.client;

/**
 * Date    : Mar 25th, 2005
 * Project : RPCXW / RPCXW-C
 * File    : Cleaner.java
 *
 * @author <a href="mailto:lodygens_a_lal.in2p3.fr">Oleg Lodygensky</a>
 * @version
 */

import java.util.Vector;

import xtremweb.common.Logger;
import xtremweb.common.XWConfigurator;
import xtremweb.common.XWPropertyDefs;
import xtremweb.communications.CommClient;

/**
 * This class describes a thread which aims to clean jobs from XtremWeb server
 */
public class Cleaner extends Thread {

	Logger logger;
	/**
	 * Communication channel
	 */
	private CommClient comm;
	/**
	 * This is the callback to manage the jobs for
	 */
	protected Callback callback;

	/**
	 * This is the default consructor.
	 *
	 * @param c
	 *            is the XtremWeb config
	 * @param cb
	 *            is the callback to delete jobs for
	 */
	protected Cleaner(final XWConfigurator c, final Callback cb) {

		super("Cleaner");
		logger = new Logger(c.getLoggerLevel());

		callback = cb;

		try {
			comm = (CommClient) Class.forName(c.getProperty(XWPropertyDefs.COMMLAYER)).newInstance();
			CommClient.setConfig(c);
		} catch (final Exception e) {
			logger.fatal("Can't init comm :  " + e);
		}

		if (comm == null) {
			logger.fatal("Can't init comm");
		}
	}

	/**
	 * This removes jobs from server This does nothing
	 */
	@Override
	public void run() {

		final Vector uids = new Vector();

		while (true) {

			try {
				sleep(1000);
			} catch (final InterruptedException ce) {
				logger.error(ce.toString());
				System.exit(Client.ERR);
			}

			final Vector newuids = callback.getJobs();
			if (newuids == null) {
				logger.error("newuids is nul???");
				System.exit(Client.ERR);
			}

			for (int idx = 0; idx < newuids.size(); idx++) {
				final Object o = newuids.elementAt(idx);
				if (o == null) {
					continue;
				}
				if (uids.contains(o) == false) {
					uids.addElement(o);
				}
			}

			if (uids.size() < 10) {
				continue;
			}

			// System.out.println("Cleaning : removing " + uids.size ());

			try {
				comm.removeWorks(uids);
				// System.out.println("Cleaning : removed " + uids.size ());
				uids.clear();
			} catch (final Exception ce) {
				logger.error(ce.toString());
				System.exit(Client.ERRCONNECTION);
			}
		}
	}

}// Cleaner
