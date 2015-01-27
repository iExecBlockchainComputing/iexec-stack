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
 * This class does just nothing!! Its aim is only to provide a generic utility
 * class so that the worker runs in a minimal configuration but do nothing :(
 */
public class XWUtilDummy implements XWUtil {

	/**
	 * This does nothing
	 */
	public void raz() {
	}

	/**
	 * This does nothing
	 * 
	 * @return false
	 */
	public boolean isRunning(int pid) {
		return false;
	}

	/**
	 * This does nothing
	 * 
	 * @return 0
	 */
	public int getPid() {
		return 0;
	}

	/**
	 * This does nothing
	 * 
	 * @return 0
	 */
	public int getUid() {
		return 0;
	}

	/**
	 * This does nothing
	 * 
	 * @return 0
	 */
	public int getGid() {
		return 0;
	}

	/**
	 * This does nothing
	 * 
	 * @return 1
	 */
	public int getNumProc() {
		return 1;
	}

	/**
	 * This does nothing
	 * 
	 * @return 0
	 */
	public int getSpeedProc() {
		return 0;
	}

	/**
	 * This does nothing
	 * 
	 * @return null
	 */
	public String getProcModel() {
		return null;
	}

	/**
	 * This does nothing
	 * 
	 * @return 0
	 */
	public long getTotalMem() {
		return 0;
	}

	/**
	 * This does nothing
	 * 
	 * @return 0
	 */
	public long getTotalSwap() {
		return 0;
	}

	/**
	 * This does nothing
	 * 
	 * @return 100
	 */
	public int getCpuLoad() {
		return 100;
	}

	/**
	 * This does nothing
	 * 
	 * @return 0
	 */
	public int getProcessLoad() {
		return 0;
	}

}
