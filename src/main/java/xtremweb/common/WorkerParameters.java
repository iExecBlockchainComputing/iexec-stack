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

package xtremweb.common;

/**
 * XWMAPanel.java
 *
 * Purpose : Created : Nov 30 2001
 *
 * @author <a href="mailto:lodygens /at\ .in2p3.fr>Oleg Lodygensky</a>
 * @version %I%, %G%
 */

public class WorkerParameters implements java.io.Serializable {
	private int sendResultDelay;
	private int resultDelay;

	public int getSendResultDelay() {
		return sendResultDelay;
	}

	public void setSendResultDelay(final int sendResultDelay) {
		this.sendResultDelay = sendResultDelay;
	}

	public int getResultDelay() {
		return resultDelay;
	}

	public void setResultDelay(final int resultDelay) {
		this.resultDelay = resultDelay;
	}

	public WorkerParameters() {
	}

}
