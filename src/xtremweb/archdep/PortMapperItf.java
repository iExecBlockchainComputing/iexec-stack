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
 * Date    : Mar 25th, 2005<br />
 * Project : RPCXW / RPCXW-C<br />
 * File    : PortMapper.java<br />
 *
 * @author <a href="mailto:lodygens_a_lal.in2p3.fr">Oleg Lodygensky</a>
 * @version
 */

/**
 * <p>
 * This interface describes needed methods to reterive some system informations
 * <ul>
 * <li>Sun RPC tcp port
 * <li>Sun RPC udp port
 * <li>user id
 * <li>group id
 * </ul>
 * </p>
 * <p>
 * An object implementing this interface can be obtained with the
 * <code>portmap()</code> of <code>ArchDepFactory</code>
 * </p>
 * <p>
 * <code>xtremweb.services.rpc.rpc#udp(byte[])</code> uses this interface
 * </p>
 * 
 * @see xtremweb.archdep.ArchDepFactory
 * @see xtremweb.services.rpc.rpc#udp(byte[])
 */
public interface PortMapperItf {
	/**
	 * This retreives the RPC TCP port
	 * 
	 * @param prog
	 *            is the RPC prog number
	 * @param version
	 *            is the RPC prog version number
	 */
	public int gettcpport(int prog, int version);

	/**
	 * This retreives the RPC UDP port
	 * 
	 * @param prog
	 *            is the RPC prog number
	 * @param version
	 *            is the RPC prog version number
	 */
	public int getudpport(int prog, int version);
}
