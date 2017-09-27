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
 * This interface aims to use columns of all Interfaces (AppInterface etc.)
 * These interfaces have enumerations that must implement this interface
 *
 * @author Oleg Lodygensky
 * @since 8.2.0
 */
public interface XWBaseColumn {
	/**
	 * This retrieves the ordinal
	 *
	 * @return the ordinal
	 */
	public abstract int getOrdinal();

	/**
	 * This creates a new object from String representing this enum value
	 *
	 * @param s
	 *            the String representation
	 * @return the object representing the column
	 * @throws Exception
	 *             is thrown on instantiation error
	 */
	public abstract Object fromString(String s) throws Exception;
}
