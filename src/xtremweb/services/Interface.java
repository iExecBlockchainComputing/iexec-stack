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

package xtremweb.services;

/**
 *
 * <p>
 * This interface aims to detect whether a service is implemented.<br />
 * Every service must overides and implements this interface, <b>using the same
 * <u>name</u></b><br />
 * The <b><code>xtremweb.services.rpc</code></b> package should be reffered as
 * example.
 * </p>
 *
 * <p>
 * Any implementation must comply to Class.newInstance(). In particular, it must
 * contain an empty constructor<br />
 * </p>
 *
 * @see xtremweb.services.rpc.Interface
 * @author <a href="mailto:lodygens  a/\t  lal.in2p3.fr">Oleg Lodygensky</a>
 * @since RPCXW
 */

public interface Interface {

	/**
	 * This is the service execution method
	 *
	 * @param cmdLine
	 *            is a String containing command line option, if any
	 * @param stdin
	 *            is a bytes array containing the stdin file, if any
	 * @param dirin
	 *            is a bytes array containing the dirin file, if any
	 * @return 0 on succes, error code otherwise
	 */
	public int exec(String cmdLine, byte[] stdin, byte[] dirin);

	/**
	 * This retreives the result content, when the service has been executed
	 *
	 * @return a bytes array, or null if there is no result
	 */
	public byte[] getResult();
}
