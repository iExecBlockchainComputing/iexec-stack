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
 *  File : MacSaver.java
 *  Purpose : MacOSX activator implementation based on screen saver
 *  Author : Oleg Lodygensky
 *  Date : August, 12th 2004
 */

package xtremweb.worker;

import xtremweb.archdep.MacSaver;

/**
 * Mac OS X activator that checks if the screensaver is running to decide if the
 * worker can be started
 * </p>
 *
 * @author Oleg Lodygensky
 */

public class MacSaverActivator extends PollingActivator {

	/**
	 * This tells whether the worker can start computing, accordingly to the
	 * local activation policy
	 *
	 * @return true if the work can start computing
	 */
	@Override
	protected boolean canStart() {
		final int running = MacSaver.running();
		System.out.println("MacSaver = " + running);
		return running <= 0;
	}

	/**
	 * This tells whether the worker must stop computing, accordingly to the
	 * local activation policy
	 *
	 * @return true if the work must stop computing
	 */
	@Override
	protected boolean mustStop() {
		return !canStart();
	}
}
