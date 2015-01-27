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
 * File    : TracerConfig.java
 *
 * Date   : March 2002
 * By     : Oleg Lodygensky
 * e-mail : lodygens /at\ .in2p3.fr
 */

package xtremweb.common;

public class TracerConfig {
	/**
	 * @return the cpuNum
	 */
	public short getCpuNum() {
		return cpuNum;
	}

	/**
	 * @param cpuNum
	 *            the cpuNum to set
	 */
	public void setCpuNum(short cpuNum) {
		this.cpuNum = cpuNum;
	}

	/**
	 * @return the cpuSpeed
	 */
	public short getCpuSpeed() {
		return cpuSpeed;
	}

	/**
	 * @param cpuSpeed
	 *            the cpuSpeed to set
	 */
	public void setCpuSpeed(short cpuSpeed) {
		this.cpuSpeed = cpuSpeed;
	}

	/**
	 * @return the memTotal
	 */
	public int getMemTotal() {
		return memTotal;
	}

	/**
	 * @param memTotal
	 *            the memTotal to set
	 */
	public void setMemTotal(int memTotal) {
		this.memTotal = memTotal;
	}

	/**
	 * @return the swapTotal
	 */
	public int getSwapTotal() {
		return swapTotal;
	}

	/**
	 * @param swapTotal
	 *            the swapTotal to set
	 */
	public void setSwapTotal(int swapTotal) {
		this.swapTotal = swapTotal;
	}

	/**
	 * @return the boottime
	 */
	public int getBoottime() {
		return boottime;
	}

	/**
	 * @param boottime
	 *            the boottime to set
	 */
	public void setBoottime(int boottime) {
		this.boottime = boottime;
	}

	/**
	 * @return the kernel
	 */
	public byte[] getKernel() {
		return kernel;
	}

	/**
	 * @param kernel
	 *            the kernel to set
	 */
	public void setKernel(byte[] kernel) {
		this.kernel = kernel.clone();
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
	public void setTime(int time) {
		this.time = time;
	}

	public static final int LENGTH = 36;

	private short cpuNum;
	private short cpuSpeed;
	private int memTotal;
	private int swapTotal;
	private int boottime;
	private byte kernel[];
	private int time;

}
