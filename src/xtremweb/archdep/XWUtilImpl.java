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

//
//  XWUtilImpl.java
//  Samuel Heriard
//  
//  Generic implementation of XWUtil
//  all methods are native.

public class XWUtilImpl implements XWUtil {
	public native boolean isRunning(int pid);

	/** this retrieves process id */
	public native int getPid();

	/** in Mhz */
	public native int getSpeedProc();

	public native String getProcModel();

	/** in Kb */
	public native long getTotalMem();

	/** in Kb */
	public native long getTotalSwap();

	/** this retrieves this host average cpu load */
	public native int getCpuLoad();

	/** this retrieves this process average cpu load */
	public native int getProcessLoad();

	public native void raz();

	/**
	 * This retrieves the group id of the current process
	 */
	public native int getGid();

	/**
	 * This retrieves user id of the current process
	 */
	public native int getUid();
}
