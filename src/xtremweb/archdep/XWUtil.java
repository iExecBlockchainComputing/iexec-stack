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

//java side for native functions 
//actual C code is in XWUtil.c
//some deprecated functions remain...

public interface XWUtil {

	/**
	 * This tells whether a process is running or not
	 * 
	 * @param pid
	 *            is the process id
	 * @return true is process is running false otherwise
	 */
	boolean isRunning(int pid);

	/**
	 * This determines the process id
	 * 
	 * @return an integer containing the process id
	 */
	int getPid();

	/**
	 * This retreives the group id of the current process
	 */
	int getGid();

	/**
	 * This retreives user id of the current process
	 */
	int getUid();

	/**
	 * This determines the main processor speed in MHz
	 * 
	 * @return the main processor speed in Mhz
	 */
	int getSpeedProc();

	/**
	 * This determines the main processor model
	 * 
	 * @return a String containing the main processor model name
	 */
	String getProcModel();

	/**
	 * This determines the total memory size
	 * 
	 * @return the total memory size in kB
	 */
	long getTotalMem();

	/**
	 * This determines the total memory swap size
	 * 
	 * @return the total memory swap size in kB
	 */
	long getTotalSwap();
	/**
	 * This determines this host cpu load
	 * 
	 * @return an integer containing the percentage of cpu load for this host
	 *         100 on error
	 */
	int getCpuLoad();

	/**
	 * This determines this process cpu load
	 * 
	 * @return an integer containing the percentage of cpu load for this process
	 *         0 on error
	 */
	int getProcessLoad();

	void raz();
}
