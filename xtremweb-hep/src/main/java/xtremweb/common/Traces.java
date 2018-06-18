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
 * File    : Traces.java
 *
 * Date   : March 2002
 * By     : Oleg Lodygensky
 * e-mail : lodygens /at\ .in2p3.fr
 */

package xtremweb.common;

public class Traces {
	/**
	 * These help to work with masks e.g. if (masks[i] & CPUUSER) then states[i]
	 * contains CPUUSER data
	 */
	public static final short CPUUSER = 0x0001;
	public static final short CPUNICE = 0x0002;
	public static final short CPUSYSTEM = 0x0004;
	public static final short CPUIDLE = 0x0008;
	public static final short CPUAIDLE = 0x0010;
	public static final short LOADONE = 0x0020;
	public static final short LOADFIVE = 0x0040;
	public static final short LOADFIFTEEN = 0x0080;
	public static final short PROCRUN = 0x0100;
	public static final short PROCTOTAL = 0x0200;
	public static final short MEMFREE = 0x0400;
	public static final short MEMSHARED = 0x0800;
	public static final short MEMBUFFERS = 0x1000;
	public static final short MEMCACHED = 0x2000;
	public static final short SWAPFREE = 0x4000;
	public static final short TIME = (short) 0x8000;

	/**
	 * @return the t_config
	 */
	public TracesConfig getT_config() {
		return t_config;
	}

	/**
	 * @param t_config
	 *            the t_config to set
	 */
	public void setT_config(final TracesConfig t_config) {
		this.t_config = t_config;
	}

	/**
	 * @return the masks
	 */
	public short[] getMasks() {
		return masks;
	}

	/**
	 * @param m
	 *            the masks to set
	 */
	public void setMasks(final short[] m) {
		this.masks = m.clone();
	}

	/**
	 * @return the configs
	 */
	public TracerConfig[] getConfigs() {
		return configs;
	}

	/**
	 * @param c
	 *            the configs to set
	 */
	public void setConfigs(final TracerConfig[] c) {
		this.configs = c.clone();
	}

	/**
	 * @return the states
	 */
	public TracerState[] getStates() {
		return states;
	}

	/**
	 * @param s
	 *            the states to set
	 */
	public void setStates(final TracerState[] s) {
		this.states = s.clone();
	}

	private TracesConfig t_config;
	private short[] masks;
	private TracerConfig[] configs;
	private TracerState[] states;

	public class Data {
		private final double timeStamp;
		private final double value;

		public Data(final double t, final double v) {
			timeStamp = t;
			value = v;
		}
	}

	/**
	 * This tests whether traces includes some values since they have only been
	 * stored if they changed between two data acquisitions e.g. if mask[index]
	 * & CPUUSER then TracerState[index] contains a CPUUSER data
	 *
	 * @param index
	 *            to address mask array
	 * @param dataType
	 *            to test masks[index]
	 * @return a boolean as test result
	 */
	private boolean testMask(final int index, final int dataType) {

		return ((masks[index] & dataType) != 0);
	}

	/**
	 * This counts how many traces are effectivly stored
	 *
	 * @param dataType
	 *            determines interesting values
	 * @return a boolean as test result
	 */
	private int maskLength(final int dataType) {
		int ret = 0;
		for (int loop = 0; loop < masks.length; loop++) {
			if (testMask(loop, dataType)) {
				ret++;
			}
		}
		return ret;
	}

	/**
	 * This extracts processes informations
	 *
	 * @param total
	 *            is a boolean to determine whether to collect total or running
	 *            processes
	 * @return an hashtable, as as getCPU ()
	 */
	private Data[] processes(final boolean total) {
		final int dataType = PROCRUN | PROCTOTAL;
		final Data[] ret = new Data[maskLength(dataType)];
		double pProcs = 0;
		int index = 0;

		for (int loop = 0; loop < masks.length; loop++) {
			if (testMask(loop, dataType)) {

				double dTime = (states[loop].getTime());
				dTime *= 100.0;
				final long prun = states[loop].getProcRun();
				final long ptotal = states[loop].getProcTotal();
				if (total) {
					pProcs = ptotal;
				} else {
					if (ptotal > 0) {
						pProcs = (double) prun / (double) ptotal;
					} else {
						pProcs = 0;
					}
				}

				ret[index++] = new Data(dTime, pProcs);
			}
		}

		return ret;

	}

	/**
	 * This extracts running procs
	 *
	 * @return an hashtable, as as getCPU ()
	 */
	public Data[] runningProcesses() {
		return processes(false);
	}

	/**
	 * This extracts total procs
	 *
	 * @return an hashtable, as as getCPU ()
	 */
	public Data[] totalProcesses() {
		return processes(true);
	}

	/**
	 * This extracts memory informations
	 *
	 * @param used
	 *            is a boolean to determine whether to collect used or free
	 *            memory
	 * @return an hashtable, as as getCPU ()
	 */
	private Data[] memory(final boolean used) {
		final int dataType = MEMFREE;
		final Data[] ret = new Data[maskLength(dataType)];
		double pMem = 0;
		int index = 0;

		for (int loop = 0; loop < masks.length; loop++) {
			if (testMask(loop, dataType)) {

				double dTime = (states[loop].getTime());

				// * 100 because time is in hundreds of seconde
				dTime *= 100.0;

				if (configs[0].getMemTotal() > 0) {
					pMem = (double) states[loop].getMemFree() / (double) configs[0].getMemTotal();
				} else {
					pMem = 0;
				}

				pMem *= 100;
				if (used) {
					pMem = 100 - pMem;
				}

				ret[index++] = new Data(dTime, pMem);
			}
		}

		return ret;
	}

	/**
	 * This extracts used memory
	 *
	 * @return an hashtable, as as getCPU ()
	 */
	public Data[] memoryUsed() {
		return memory(true);
	}

	/**
	 * This extracts free memory
	 *
	 * @return an hashtable, as as getCPU ()
	 */
	public Data[] memoryFree() {
		return memory(false);
	}

	/**
	 * This extracts swap informations
	 *
	 * @param used
	 *            is a boolean to determine whether to collect used or free swap
	 * @return an hashtable, as as getCPU ()
	 */
	private Data[] swap(final boolean used) {
		final int dataType = SWAPFREE;
		final Data[] ret = new Data[maskLength(dataType)];
		double pSwap = 0;
		int index = 0;

		for (int loop = 0; loop < masks.length; loop++) {
			if (testMask(loop, dataType)) {

				double dTime = (states[loop].getTime());
				dTime *= 100.0;
				if (configs[0].getSwapTotal() > 0) {
					pSwap = (double) states[loop].getSwapFree() / (double) configs[0].getSwapTotal();
				} else {
					pSwap = 0;
				}

				pSwap *= 100;
				if (used) {
					pSwap = 100 - pSwap;
				}

				ret[index++] = new Data(dTime, pSwap);
			}
		}

		return ret;

	}

	/**
	 * This extracts used swap
	 *
	 * @return an hashtable, as as getCPU ()
	 */
	public Data[] swapUsed() {
		return swap(true);
	}

	/**
	 * This extracts free swap
	 *
	 * @return an hashtable, as as getCPU ()
	 */
	public Data[] getSwapFree() {
		return swap(false);
	}

	/**
	 * This extracts data accordingly to param dataType
	 *
	 * @param dataType
	 *            to determine which data to work on
	 * @return a hastable containing time stamps as keys, and data as values
	 */
	private Data[] getCPU(final int dataType) {
		final Data[] ret = new Data[maskLength(dataType)];
		double pCPU = 0;
		int index = 0;

		for (int loop = 0; loop < masks.length; loop++) {
			if (testMask(loop, dataType)) {

				final double dTime = (states[loop].getTime());

				long totalUsedCPU = 0;

				if (testMask(loop, CPUUSER)) {
					totalUsedCPU += states[loop].getCpuUser();
				}
				if (testMask(loop, CPUNICE)) {
					totalUsedCPU += states[loop].getCpuNice();
				}
				if (testMask(loop, CPUSYSTEM)) {
					totalUsedCPU += states[loop].getCpuSystem();
				}

				final long totalCPU = (states[loop].getCpuUser() + states[loop].getCpuNice()
						+ states[loop].getCpuSystem() + states[loop].getCpuIdle());

				if (totalCPU > 0) {
					pCPU = (double) totalUsedCPU / (double) totalCPU;
				} else {
					pCPU = 0;
				}

				pCPU *= 100;

				ret[index++] = new Data(dTime, pCPU);

			}
		}

		return ret;
	}

	/**
	 * This extracts used CPU percentage, including all but idle returns: see
	 * getCPU ()
	 */
	public Data[] usedCPU() {
		final int dataType = CPUUSER | CPUNICE | CPUSYSTEM;
		return getCPU(dataType);
	}

	/**
	 * This extracts idle CPU percentage returns: see getCPU ()
	 */
	public Data[] idleCPU() {
		final int dataType = CPUIDLE;
		return getCPU(dataType);
	}

	/**
	 * This extracts nice CPU percentage returns: see getCPU ()
	 */
	public Data[] niceCPU() {
		final int dataType = CPUNICE;
		return getCPU(dataType);
	}

	/**
	 * This extracts user CPU percentage returns: see getCPU ()
	 */
	public Data[] userCPU() {
		final int dataType = CPUUSER;
		return getCPU(dataType);
	}

	/**
	 * This extracts system CPU percentage returns: see getCPU ()
	 */
	public Data[] systemCPU() {
		final int dataType = CPUSYSTEM;
		return getCPU(dataType);
	}

}
