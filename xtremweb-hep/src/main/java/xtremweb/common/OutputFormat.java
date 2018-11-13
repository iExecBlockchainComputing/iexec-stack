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
 * This class defines output format used to write objects.
 *
 *
 * Created: Thu May 31 10:03:43 2001
 *
 * @author <a href="mailto: fedak@lri.fr">Gilles Fedak</a>
 */

public enum OutputFormat {
	/**
	 * This defines the minimal format
	 *
	 * @since 7.0.0
	 */
	SHORT, CSV, TEXT, HTML, XML;

	public static void main(final String[] argv) {
		for (final OutputFormat i : OutputFormat.values()) {
			System.out
					.println(i.toString() + " = " + i.ordinal() + " valueOf() = " + OutputFormat.valueOf(i.toString()));
		}
	}

}
