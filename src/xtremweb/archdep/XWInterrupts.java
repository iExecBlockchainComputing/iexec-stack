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

/**
 * <p>
 * This interface describes needed methods to read the number of keyboard and
 * mouse interrupts that occured since system startup.
 * </p>
 * <p>
 * An object implementing this interface can be obtained with the
 * <code>xwinterrupts()</code> of <code>ArchDepFactory</code>
 * </p>
 * <p>
 * <code>xtremweb.worker.MouseKbdActivator</code> uses this intereface
 * </p>
 * 
 * @see xtremweb.archdep.ArchDepFactory
 * @see xtremweb.worker.MouseKbdActivator
 */

public interface XWInterrupts {
	/**
	 * This initializes IRQ counter
	 * 
	 * @return true on success false otherwise
	 */
	boolean initialize();

	/**
	 * <p>
	 * Read keyboard interrupts occurences since last reboot
	 * </p>
	 * <p>
	 * <i>Actually neither this method nor <code>readMouse()</code> needs to
	 * return the exact number of interrupts occurences since last reboot. It
	 * must return an integer that change between two calls if an interrupts
	 * occured.</i>
	 * </p>
	 */
	int readKey();

	/**
	 * Read mouse interrupts occurences since last reboot
	 */
	int readMouse();
}
