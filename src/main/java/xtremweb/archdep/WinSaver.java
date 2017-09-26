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

public class WinSaver {
	/**
	 * Retreive whether the screen saver is running
	 *
	 * @return true if screen saver is running
	 */
	public static native boolean screenSaverRunning();

	/**
	 * Retreive whether the low power is enabled for this station
	 *
	 * @return true if low power is enabled
	 */
	public static native boolean lowPowerActive();

	/**
	 * Disable the low power for this station
	 */
	public static native void disableLowPower();

	/**
	 * Enable the low power for this station
	 */
	public static native void enableLowPower();

	/**
	 * Retreive whether the power off is enabled for this station
	 *
	 * @return true if power off is enabled
	 */
	public static native boolean powerOffActive();

	/**
	 * Disable the power off for this station
	 */
	public static native void disablePowerOff();

	/**
	 * Enable the power off for this station
	 */
	public static native void enablePowerOff();

	/**
	 * Retreive time out to low power
	 *
	 * @return the low power time out in seconds
	 */
	public static native int lowPowerTimeOut();

	/**
	 * Retreive time out to power off
	 *
	 * @return the power off time out in seconds
	 */
	public static native int powerOffTimeOut();
}
