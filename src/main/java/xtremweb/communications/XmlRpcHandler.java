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

/*
 * Copyright 1999 Hannes Wallnoefer
 */

import java.util.Vector;

/**
 * The XML-RPC server uses this interface to call a method of an RPC handler.
 * This should be implemented by any class that wants to directly take control
 * when it is called over RPC. Classes not implementing this interface will be
 * wrapped into an Invoker object that tries to find the matching method for an
 * XML-RPC request.
 */

public interface XmlRpcHandler {

	/**
	 * Return the result, or throw an Exception if something went wrong.
	 */
	public Object execute(String method, Vector params) throws Exception;

}
