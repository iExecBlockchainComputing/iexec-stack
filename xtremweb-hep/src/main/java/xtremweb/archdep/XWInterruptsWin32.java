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
 * Win32 XWInterrupts implementation, all methods are native.
 * </p>
 *
 * @author Oleg Lodygensky
 */

public class XWInterruptsWin32 implements XWInterrupts {
	/**
	 * This initializes IRQ counter
	 *
	 * @return true on success false otherwise
	 */
	@Override
	public native boolean initialize();

	/**
	 * Read keyboard interrupts occurences
	 */
	@Override
	public native int readKey();

	/**
	 * Read mouse interrupts occurences
	 */
	@Override
	public native int readMouse();

}
