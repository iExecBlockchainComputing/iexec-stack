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

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Iterator;

/**
 * This defines XtremWeb application type.
 * 
 * Created: 16 novembre 2011
 * 
 * @author <a href="mailto: lodygens at lal in2p3 fr">Oleg Lodygensky</a>
 * @since 8.0.0
 */

public enum AppTypeEnum {

	NONE,
	/**
	 * This denotes an application where workers must download executable This
	 * denotes application as known until XWHEP 7
	 */
	DEPLOYABLE,
	/**
	 * This denotes an application where workers don't download binary because
	 * the application is supposed to be pre-installed on workers. All following
	 * values in this enum represent shared application.
	 */
	SHARED,
	/**
	 * On Dec 2nd, 2011, this denotes our 1st shared application. This denotes
	 * VirtualBox as shared application
	 */
	VIRTUALBOX;

	public static final AppTypeEnum LAST = VIRTUALBOX;
	public static final int SIZE = LAST.ordinal() + 1;

	/**
	 * This retrieves an OS from its ordinal value
	 * 
	 * @exception IllegalArgumentException
	 *                is thrown if v is not a valid ordinal value
	 */
	public static AppTypeEnum fromInt(int v) throws IllegalArgumentException {
		for (final AppTypeEnum i : AppTypeEnum.values()) {
			if (i.ordinal() == v) {
				return i;
			}
		}
		throw new IllegalArgumentException("unvalid XWOSes value " + v);
	}

	/**
	 * This array stores enum as string
	 */
	private static String[] labels = null;

	/**
	 * This retreives this enum string representation
	 * 
	 * @return a array containing this enum string representation
	 */
	public static String[] getLabels() {
		if (labels != null) {
			return labels;
		}

		labels = new String[SIZE];
		for (final AppTypeEnum c : AppTypeEnum.values()) {
			labels[c.ordinal()] = c.toString();
		}
		return labels;
	}

	/**
	 * This array stores default VirtualBox pathnames (one entry per OS). Each
	 * entry is a semicolon separated paths list Defaults are Oracle VirtualBox
	 * paths
	 * 
	 * @since 8.0.0 (FG)
	 */
	private static String[] virtualboxpaths = {
			null, // NONE
			"/usr/bin/VBoxHeadless", // LINUX
			"c:\\Program Files\\Oracle\\VirtualBox\\VBoxHeadless.exe;c:\\Program Files\\VirtualBox\\VBoxHeadless.exe;c:\\Program Files (x86)\\Oracle\\VirtualBox\\VBoxHeadless.exe;c:\\Program Files (x86)\\VirtualBox\\VBoxHeadless.exe", // WIN32
			"/Applications/VirtualBox.app/Contents/MacOS/VBoxHeadless", // MACOSX
			null, // OSF1
			null, // SOLARIS
			null // JAVA
	};

	/**
	 * This retrieves application default pathname
	 * 
	 * @param t
	 *            is the application type to retrieve path for
	 * @return application binary path if available
	 * @throws FileNotFoundException
	 *             if no binary path available for the provided application type
	 * @see xtremweb.common.OSEnum#getOs(String)
	 * @since 8.0.0 (FG)
	 */
	public static String getPathName(AppTypeEnum t)
			throws FileNotFoundException {
		try {
			switch (t) {
			case VIRTUALBOX:
				return virtualboxpaths[OSEnum.getOs().ordinal()];
			}
		} catch (final Exception e) {
		}
		throw new FileNotFoundException("no default path name for " + t);
	}

	/**
	 * This calls getPathName(this)
	 * 
	 * @throws FileNotFoundException
	 *             if no application binary path found for the current OS
	 * @since 8.0.0 (FG)
	 */
	public String getPathName() throws FileNotFoundException {
		return getPathName(this);
	}

	/**
	 * This retrieves application default pathname
	 * 
	 * @param t
	 *            is the application type to retrieve path for
	 * @return application binary path for the current OS
	 * @throws FileNotFoundException
	 *             if no application binary path found for the current OS
	 * @see xtremweb.common.OSEnum#getOs(String)
	 * @since 8.0.0 (FG)
	 */
	public static File getPath(AppTypeEnum t) throws FileNotFoundException {
		File f = null;
		Collection<String> v = null;
		Iterator<String> elems = null;
		try {
			v = XWTools.split(getPathName(t), ";");
			elems = v.iterator();
			while (elems.hasNext()) {
				final String path = elems.next();
				System.out.println(t.toString() + ".getPath = " + path);
				f = new File(path);
				if (f.exists() == true) {
					return f;
				}
			}
		} finally {
			f = null;
			elems = null;
			v = null;
		}
		throw new FileNotFoundException("no binary path for " + t);
	}

	/**
	 * This calls getPath(this)
	 * 
	 * @throws FileNotFoundException
	 *             if no application binary path found for the current OS
	 * @since 8.0.0 (FG)
	 */
	public File getPath() throws FileNotFoundException {
		return getPath(this);
	}

	/**
	 * This dumps path
	 * 
	 * @see xtremweb.common.OSEnum#getOs(String)
	 * @since 8.0.0 (FG)
	 */
	public void dumpPath() {
		Collection<String> v = null;
		Iterator<String> elems = null;
		try {
			v = XWTools.split(getPathName(), ";");
			elems = v.iterator();
			while (elems.hasNext()) {
				final String path = elems.next();
				System.out.println(path);
			}
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			elems = null;
			v = null;
		}
	}

	/**
	 * This checks if the provided application is available
	 * 
	 * @param t
	 *            is the application type to retrieve path for
	 * @return true if getPath(t) != null; false otherwise
	 * @throws FileNotFoundException
	 *             if application path is not valid
	 * @see #getPath(AppTypeEnum)
	 * @since 8.0.0 (FG)
	 */
	public static boolean available(AppTypeEnum t) throws FileNotFoundException {
		return (getPath(t) != null);
	}

	/**
	 * This calls availabel(this)
	 * 
	 * @since 8.0.0 (FG)
	 */
	public boolean available() throws FileNotFoundException {
		return available(this);
	}

	/**
	 * This is for debug purposes
	 * 
	 * @param argv
	 */
	public static void main(String[] argv) {
		for (final AppTypeEnum i : AppTypeEnum.values()) {
			System.out.println(i.toString());
		}
	}

}
