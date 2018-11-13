/*
 * Copyrights     : CNRS
 * Author         : Oleg Lodygensky
 * Acknowledgment : XtremWeb-HEP is based on XtremWeb 1.8.0 by inria : http://www.xtremweb.net/
 * Web            : http://www.xtremweb-hep.org
 *
 *      This file is part of XtremWeb-HEP.
 *
 *    XtremWeb-HEP is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General private License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    XtremWeb-HEP is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General private License for more details.
 *
 *    You should have received a copy of the GNU General private License
 *    along with XtremWeb-HEP.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

/**
 * Project : XTremWeb
 * File    : TracerState.java
 *
 * Date   : March 2002
 * By     : Oleg Lodygensky
 * e-mail : lodygens /at\ .in2p3.fr
 */

package xtremweb.common;

public class TracerState {
	private int cpuUser;

	/**
	 * @return the cpuUser
	 */
	public int getCpuUser() {
		return cpuUser;
	}

	/**
	 * @param cpuUser
	 *            the cpuUser to set
	 */
	public void setCpuUser(final int cpuUser) {
		this.cpuUser = cpuUser;
	}

	/**
	 * @return the cpuNice
	 */
	public int getCpuNice() {
		return cpuNice;
	}

	/**
	 * @param cpuNice
	 *            the cpuNice to set
	 */
	public void setCpuNice(final int cpuNice) {
		this.cpuNice = cpuNice;
	}

	/**
	 * @return the cpuSystem
	 */
	public int getCpuSystem() {
		return cpuSystem;
	}

	/**
	 * @param cpuSystem
	 *            the cpuSystem to set
	 */
	public void setCpuSystem(final int cpuSystem) {
		this.cpuSystem = cpuSystem;
	}

	/**
	 * @return the cpuIdle
	 */
	public int getCpuIdle() {
		return cpuIdle;
	}

	/**
	 * @param cpuIdle
	 *            the cpuIdle to set
	 */
	public void setCpuIdle(final int cpuIdle) {
		this.cpuIdle = cpuIdle;
	}

	/**
	 * @return the cpuAidle
	 */
	public int getCpuAidle() {
		return cpuAidle;
	}

	/**
	 * @param cpuAidle
	 *            the cpuAidle to set
	 */
	public void setCpuAidle(final int cpuAidle) {
		this.cpuAidle = cpuAidle;
	}

	/**
	 * @return the loadOne
	 */
	public short getLoadOne() {
		return loadOne;
	}

	/**
	 * @param loadOne
	 *            the loadOne to set
	 */
	public void setLoadOne(final short loadOne) {
		this.loadOne = loadOne;
	}

	/**
	 * @return the loadFive
	 */
	public short getLoadFive() {
		return loadFive;
	}

	/**
	 * @param loadFive
	 *            the loadFive to set
	 */
	public void setLoadFive(final short loadFive) {
		this.loadFive = loadFive;
	}

	/**
	 * @return the loadFifteen
	 */
	public short getLoadFifteen() {
		return loadFifteen;
	}

	/**
	 * @param loadFifteen
	 *            the loadFifteen to set
	 */
	public void setLoadFifteen(final short loadFifteen) {
		this.loadFifteen = loadFifteen;
	}

	/**
	 * @return the procRun
	 */
	public short getProcRun() {
		return procRun;
	}

	/**
	 * @param procRun
	 *            the procRun to set
	 */
	public void setProcRun(final short procRun) {
		this.procRun = procRun;
	}

	/**
	 * @return the procTotal
	 */
	public short getProcTotal() {
		return procTotal;
	}

	/**
	 * @param procTotal
	 *            the procTotal to set
	 */
	public void setProcTotal(final short procTotal) {
		this.procTotal = procTotal;
	}

	/**
	 * @return the memFree
	 */
	public int getMemFree() {
		return memFree;
	}

	/**
	 * @param memFree
	 *            the memFree to set
	 */
	public void setMemFree(final int memFree) {
		this.memFree = memFree;
	}

	/**
	 * @return the memShared
	 */
	public int getMemShared() {
		return memShared;
	}

	/**
	 * @param memShared
	 *            the memShared to set
	 */
	public void setMemShared(final int memShared) {
		this.memShared = memShared;
	}

	/**
	 * @return the memBuffers
	 */
	public int getMemBuffers() {
		return memBuffers;
	}

	/**
	 * @param memBuffers
	 *            the memBuffers to set
	 */
	public void setMemBuffers(final int memBuffers) {
		this.memBuffers = memBuffers;
	}

	/**
	 * @return the memCached
	 */
	public int getMemCached() {
		return memCached;
	}

	/**
	 * @param memCached
	 *            the memCached to set
	 */
	public void setMemCached(final int memCached) {
		this.memCached = memCached;
	}

	/**
	 * @return the swapFree
	 */
	public int getSwapFree() {
		return swapFree;
	}

	/**
	 * @param swapFree
	 *            the swapFree to set
	 */
	public void setSwapFree(final int swapFree) {
		this.swapFree = swapFree;
	}

	/**
	 * @return the time
	 */
	public int getTime() {
		return time;
	}

	/**
	 * @param time
	 *            the time to set
	 */
	public void setTime(final int time) {
		this.time = time;
	}

	private int cpuNice;
	private int cpuSystem;
	private int cpuIdle;
	private int cpuAidle;
	private short loadOne;
	private short loadFive;
	private short loadFifteen;
	private short procRun;
	private short procTotal;
	private int memFree;
	private int memShared;
	private int memBuffers;
	private int memCached;
	private int swapFree;
	private int time;
}
