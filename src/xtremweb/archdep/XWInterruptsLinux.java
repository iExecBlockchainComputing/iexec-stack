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

package xtremweb.archdep;

// XWInterruptsLinux.java
// Created: Thu Jun 29 17:47:11 2000 by Gilles Fedak
//
// Tue May 28 2002 -- Samuel Heriard
//    - isActive() method moved to xtremweb.worker.MouseKbdActivator

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * <p>
 * Linux implementation of <code>XWInterrupts</code>
 * </p>
 * <p>
 * This implementation directly parse <code>/proc/interrupts</code>
 * </p>
 *
 * @author Gilles Fedak
 * @version %I% %G%
 */

public class XWInterruptsLinux implements XWInterrupts {

	/**
	 * This does nothing since there is no initialization need here.
	 *
	 * @return always true
	 */
	@Override
	public boolean initialize() {
		return true;
	}

	/** Read /proc/interrupts to get the number binded to keyboard */
	@Override
	public int readKey() {

		try {
			final BufferedReader bufferFile = new BufferedReader(new FileReader("/proc/interrupts"));
			String l = "";

			while (l != null) {
				if ((l.toLowerCase().indexOf("keyboard") > -1)
						|| ((l.toLowerCase().indexOf(" 1:") > -1) && (l.toLowerCase().indexOf("i8042") > -1))) {
					l = l.substring(l.indexOf(':') + 1, l.length());
					l = l.trim();
					l = l.substring(0, l.indexOf(' '));
					bufferFile.close();
					return (new Integer(l)).intValue();
				}
				l = bufferFile.readLine();
			}

			bufferFile.close();
		} catch (final Exception e) {
			System.err.println("Unrecoverable exception " + e);
			System.exit(1);
		}

		return 0;

	}

	/** Read /proc/interrupts to get the number binded to mouse */
	@Override
	public int readMouse() {
		try {
			final BufferedReader bufferFile = new BufferedReader(new FileReader("/proc/interrupts"));
			String l = "";

			while (l != null) {
				if (l.toLowerCase().indexOf("mouse") > -1) {
					l = l.substring(l.indexOf(':') + 1, l.length());
					l = l.trim();
					l = l.substring(0, l.indexOf(' '));
					bufferFile.close();
					return (new Integer(l)).intValue();
				}
				l = bufferFile.readLine();
			}
			bufferFile.close();
		} catch (final Exception e) {
			System.err.println("Unrecoverable exception" + e);
			System.exit(1);
		}

		return 0;
	}
}
