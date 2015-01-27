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

import java.util.Collection;
import java.util.Iterator;

/**
 * This class aims to store time stamps
 */
public class MileStone {

	private long lastTime, firstTime;

	public static String toString(String c, String m, UID u, long t,
			long first, long last) {

		return new String(c + ";" + m + ";" + t
				+ (u == null ? "" : ";" + u.toString()));
	}

	/**
	 * This contains class names we want to get statistics for
	 */
	private static Collection<String> classes;
	/**
	 * This contains the instance class name
	 */
	private String className;
	/**
	 * This tells whether we get statistics for className This is true if
	 * classes
	 */
	private boolean print;

	/**
	 * This constructs a new instance
	 * 
	 * @param cl
	 *            is the class for this instance
	 */
	public MileStone(Class cl) {

		className = cl.getName();

		final Iterator<String> li = classes.iterator();
		while (li.hasNext()) {
			final String c = li.next();
			print = (className.indexOf(c) != -1);
			if (print) {
				break;
			}
		}
	}

	/**
	 * This constructs a new instance and sets classes attribute<br />
	 * If classes is already set, this does nothing
	 * 
	 * @param v
	 *            is a Vector of String containing class names to feed classes
	 * @see #classes
	 */
	public MileStone(Collection<String> v) {

		if (classes != null) {
			return;
		}

		classes = v;
		print = false;
		className = null;
	}

	/**
	 * This retrieves stamp flag
	 * 
	 * @return true if time slicing, false otherwise
	 */
	public boolean print() {
		return print;
	}

	/**
	 * This clears all stamps
	 */
	public void clear() {
		firstTime = lastTime = System.currentTimeMillis();
	}

	/**
	 * This prints out a mile stone, if class name matches classes
	 * 
	 * @param msg
	 *            is the string to print
	 * @see #className
	 * @see #classes
	 */
	public void println(String msg) {
		println(msg, null);
	}

	/**
	 * This prints out a mile stone, if class name matches classes
	 * 
	 * @param msg
	 *            is the string to print
	 * @param uid
	 *            is appended at the end of the message
	 * @see #className
	 * @see #classes
	 */
	public void println(String msg, UID uid) {
		if (print == false) {
			return;
		}
		final long t = System.currentTimeMillis();
		System.out
				.println(toString(className, msg, uid, t, firstTime, lastTime));
		lastTime = t;
	}
}
