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
 * @since 13.1.0
 *
 * @author Oleg Lodygensky
 */

public enum MarketOrderDirectionEnum {

	UNSET,
	/**
	 * User ask for a price
	 */
	BID,
	/**
	 * Workerpool ask for a price
	 */
	ASK,
	CLOSED;


	public static final MarketOrderDirectionEnum LAST = CLOSED;
	public static final int SIZE = LAST.ordinal() + 1;

	public static MarketOrderDirectionEnum fromInt(final int v) throws IllegalArgumentException {
		for (final MarketOrderDirectionEnum i : MarketOrderDirectionEnum.values()) {
			if (i.ordinal() == v) {
				return i;
			}
		}
		throw new IllegalArgumentException("unvalid value " + v);
	}


	public static void main(final String[] argv) {
		for (final MarketOrderDirectionEnum i : MarketOrderDirectionEnum.values()) {
			System.out.println(i.toString());
		}
	}

}
