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
 * @author Oleg Lodygensky
 * @since RPCXW
 */

public class CommLayers {
	/**
	 * This is the first value
	 */
	public static final int LAYERS_FIRST = 0;

	public static final int RMI = LAYERS_FIRST;
	public static final int TCP = 1;
	public static final int XMLRPC = 2;

	/**
	 * This is the last value
	 */
	public static final int LAYERS_LAST = XMLRPC;
	public static final int LAYERS_MAX = LAYERS_LAST + 1;

	private static String[] layersText;

	static {
		layersText = new String[LAYERS_MAX];
		layersText[RMI] = "RMI";
		layersText[TCP] = "TCP";
		layersText[XMLRPC] = "XMLRPC";
	}

	public static String toString(final int s) throws Exception {

		try {
			return layersText[s];
		} catch (final Exception e) {
		}
		throw new Exception("unknown layers : " + s);
	}

	public static int fromString(final String s) throws Exception {

		for (int i = LAYERS_FIRST; i < LAYERS_MAX; i++) {
			if (s.toUpperCase().compareTo(layersText[i]) == 0) {
				return (i);
			}
		}

		throw new Exception("unknown layers : " + s);
	}

} // CommLayers
