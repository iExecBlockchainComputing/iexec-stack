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

package xtremweb.communications;

/**
 * Copyright 1999 Hannes Wallnoefer
 */

/**
 * This is thrown by the XmlRpcClient if the remote server reported an error. If
 * something went wrong at a lower level (e.g. no http connection) an
 * IOException will be thrown instead.
 */
public class XmlRpcException extends Exception {

	/**
	 * The fault code of the exception. For servers based on this library, this
	 * will always be 0. (If there are predefined error codes, they should be in
	 * the XML-RPC spec.)
	 */
	private final int code;

	public XmlRpcException(final int code, final String message) {
		super(message);
		this.code = code;
	}

	/**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}

}
