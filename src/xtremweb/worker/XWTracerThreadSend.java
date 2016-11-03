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
 * Project : XTremWeb
 * File    : XWTracerThreadSend.java
 *
 * Initial revision : July 2001
 * By               : Anna Lawer
 *
 * New revision : January 2002
 * By           : Oleg Lodygensky
 * e-mail       : lodygens /at\ lal.in2p3.fr
 */

package xtremweb.worker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import xtremweb.common.Logger;

/**
 * The <CODE>XWTracerThreadSend</CODE> class describes the XtremWeb thread which
 * sends traces to server.
 */

public class XWTracerThreadSend extends Thread {

	private final Logger logger;

	/**
	 * This variable stores date when current traces has been started.
	 */
	private final long dateStart;
	/**
	 * This variable stores date when current traces has been ended.
	 */
	private final long dateEnd;
	/**
	 * This variable stores traces file name.
	 */
	private final String fileName;

	/**
	 * This is the only constructor.
	 *
	 * @param start
	 *            contains date when current traces has been started.
	 * @param end
	 *            contains date when current traces has been ended.
	 */
	XWTracerThreadSend(final String fName, final long start, final long end) {

		super("XWTracerThreadSend");
		logger = new Logger(this);
		fileName = fName;
		dateStart = start;
		dateEnd = end;
	}

	/**
	 * This is the main method. It sends traces to server and exits.
	 */
	@Override
	public void run() {

		File f;
		FileInputStream fInpS;
		byte[] file;

		f = new File(fileName);
		file = new byte[(int) f.length()];

		try {
			fInpS = new FileInputStream(f);

			try {
				fInpS.read(file, 0, (int) f.length());
				fInpS.close();
			} catch (final IOException e) {
				logger.error("XWTracerThreadSend : can't read input file; " + e);
			}
		} catch (final FileNotFoundException ff) {
			logger.error("XWTracerThreadSend : input file not found; " + ff);
		}

		try {
			CommManager.getInstance().tactivityMonitor(Worker.getConfig().getHost().getName(),
					Worker.getConfig().getUser().getLogin(), dateStart, dateEnd, file);

			logger.info("Trace results sent");
		} catch (final Exception e) {
			logger.error("Send trace error : " + e);
		}
	}

}
