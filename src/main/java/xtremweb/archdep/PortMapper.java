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
//  PortMapperImpl.java
//
//  Generic implementation of PortMapper
//  all methods are native.

public class PortMapper implements PortMapperItf {
	/**
	 * This retreives the RPC TCP port
	 *
	 * @param prog
	 *            is the RPC prog number
	 * @param version
	 *            is the RPC prog version number
	 */
	@Override
	public native int gettcpport(int prog, int version);

	/**
	 * This retreives the RPC UDP port
	 *
	 * @param prog
	 *            is the RPC prog number
	 * @param version
	 *            is the RPC prog version number
	 */
	@Override
	public native int getudpport(int prog, int version);

	/**
	 * This retreives the group id of the current process
	 */
	public native int getGid();

	/**
	 * This retreives user id of the current process
	 */
	public native int getUid();
}
