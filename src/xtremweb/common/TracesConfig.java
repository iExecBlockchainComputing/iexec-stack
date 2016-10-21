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
 * File    : TracesConfig.java
 *
 * Date   : March 2002
 * By     : Oleg Lodygensky
 * e-mail : lodygens /at\ .in2p3.fr
 */

package xtremweb.common;

public class TracesConfig {
	/**
	 * @return the hostName
	 */
	public String getHostName() {
		return hostName;
	}

	/**
	 * @param hostName
	 *            the hostName to set
	 */
	public void setHostName(final String hostName) {
		this.hostName = hostName;
	}

	/**
	 * @return the timeZone
	 */
	public String getTimeZone() {
		return timeZone;
	}

	/**
	 * @param timeZone
	 *            the timeZone to set
	 */
	public void setTimeZone(final String timeZone) {
		this.timeZone = timeZone;
	}

	/**
	 * @return the ipAddr
	 */
	public String getIpAddr() {
		return ipAddr;
	}

	/**
	 * @param ipAddr
	 *            the ipAddr to set
	 */
	public void setIpAddr(final String ipAddr) {
		this.ipAddr = ipAddr;
	}

	/**
	 * @return the hwAddr
	 */
	public String getHwAddr() {
		return hwAddr;
	}

	/**
	 * @param hwAddr
	 *            the hwAddr to set
	 */
	public void setHwAddr(final String hwAddr) {
		this.hwAddr = hwAddr;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public void setVersion(final String version) {
		this.version = version;
	}

	/**
	 * @return the resultDelay
	 */
	public int getResultDelay() {
		return resultDelay;
	}

	/**
	 * @param resultDelay
	 *            the resultDelay to set
	 */
	public void setResultDelay(final int resultDelay) {
		this.resultDelay = resultDelay;
	}

	/**
	 * @return the sendResultDelay
	 */
	public int getSendResultDelay() {
		return sendResultDelay;
	}

	/**
	 * @param sendResultDelay
	 *            the sendResultDelay to set
	 */
	public void setSendResultDelay(final int sendResultDelay) {
		this.sendResultDelay = sendResultDelay;
	}

	private String hostName;
	private String timeZone;
	private String ipAddr;
	private String hwAddr;
	private String version;
	private int resultDelay;
	private int sendResultDelay;
}
