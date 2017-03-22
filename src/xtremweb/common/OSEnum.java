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
 * Formerly XWOses.java<br />
 *
 * This defines XtremWeb compatible OSes<br />
 *
 * Created: 29 janvier 2007
 *
 * @author <a href="mailto: lodygens *a**t* lal.in2p3.fr">Oleg Lodygensky</a>
 * @version %I% %G%
 */

public enum OSEnum {

	NONE, LINUX, WIN32, MACOSX, SOLARIS, JAVA;

	public static final OSEnum LAST = JAVA;
	public static final int SIZE = LAST.ordinal() + 1;

	/**
	 * This retrieves an OS from its ordinal value
	 *
	 * @exception IllegalArgumentException
	 *                is thrown if v is not a valid ordinal value
	 */
	public static OSEnum fromInt(final int v) throws IllegalArgumentException {
		for (final OSEnum i : OSEnum.values()) {
			if (i.ordinal() == v) {
				return i;
			}
		}
		throw new IllegalArgumentException("unvalid XWOSes value " + v);
	}

	/**
	 * This retrieves the OS name If this OS is not supported by XtremWeb, this
	 * forces the program to immediately stop
	 *
	 * @see #getOsName(String)
	 */
	public static String getOsName() {
		try {
			return getOsName(System.getProperty("os.name").trim());
		} catch (final Exception e) {
			XWTools.fatal(e.toString());
		}
		return null;
	}

	/**
	 * This forces os name to predefined values to avoid confusion Example :
	 * "Windows 2000" and "Windows XP" are summarized as "win32"
	 *
	 * @param osName
	 *            the OS name
	 * @exception ClassNotFound
	 *                exception is thrown if osName is not supported by XtremWeb
	 */
	public static String getOsName(final String osName) throws IllegalArgumentException {

		if ((osName.toUpperCase().indexOf("WINDOWS") != -1) || (osName.compareToIgnoreCase(WIN32.toString()) == 0)) {
			return WIN32.toString();
		}
		if (osName.compareToIgnoreCase(LINUX.toString()) == 0) {
			return LINUX.toString();
		}
		if (osName.compareToIgnoreCase(SOLARIS.toString()) == 0) {
			return SOLARIS.toString();
		}
		if (osName.compareToIgnoreCase(JAVA.toString()) == 0) {
			return JAVA.toString();
		}
		if ((osName.compareToIgnoreCase(MACOSX.toString()) == 0) || (osName.compareToIgnoreCase("mac os x") == 0)) {
			return MACOSX.toString();
		}

		throw new IllegalArgumentException(osName + " not supported");
	}

	/**
	 * This retreives this host OS name
	 *
	 * @see #getOs(String)
	 */
	public static OSEnum getOs() {
		try {
			return getOs(getOsName());
		} catch (final Exception e) {
			XWTools.fatal(e.toString());
		}
		return null;
	}

	/**
	 * This forces os name to predefined values to avoid confusion Example :
	 * "Windows 2000" and "Windows XP" are summarized as "win32"
	 *
	 * @param osName
	 *            the OS name
	 * @exception ClassNotFound
	 *                exception is thrown if osName is not supported by XtremWeb
	 */
	public static OSEnum getOs(final String osName) throws IllegalArgumentException {

		return valueOf(getOsName(osName));
	}

	/**
	 * This tests whether OS is Win32
	 */
	public boolean isWin32() {
		return (OSEnum.valueOf(getOsName()) == WIN32);
	}

	/**
	 * This tests whether OS is Linux
	 */
	public boolean isLinux() {
		return (OSEnum.valueOf(getOsName()) == LINUX);
	}

	/**
	 * This tests whether OS is Solaris
	 */
	public boolean isSolaris() {
		return (OSEnum.valueOf(getOsName()) == SOLARIS);
	}

	/**
	 * This tests whether OS is Mac OS
	 */
	public boolean isMacosx() {
		return (OSEnum.valueOf(getOsName()) == MACOSX);
	}

	/**
	 * This array stores default sandbox pathnames Defaults are Sun xVM
	 * VirtualBox paths
	 *
	 * @since 6.0.0
	 */
	public static final String[] sandboxes = { null, // NONE
			"/usr/bin/lxc", // LINUX
			null, // WIN32
			"/usr/bin/sandbox-exec", // MACOSX
			null, // SOLARIS
			null // JAVA
	};

	/**
	 * This retrieves the default sandbox pathname
	 *
	 * @see #getOs(String)
	 */
	public static String getSandboxPath() {
		try {
			return sandboxes[getOs().ordinal()];
		} catch (final Exception e) {
		}
		return null;
	}

	/**
	 * This array stores path to xtremweb.jar
	 *
	 * @since 6.0.0
	 */
	public static final String[] classpathes = { null, // NONE
			"/opt/xwhep-worker-" + CommonVersion.getCurrent().rev() + "/lib", // LINUX
			"c:\\Program Files\\CNRS\\XWHEP\\worker\\lib", // WIN32
			"/private/etc/xwhep.worker", // MACOSX
			null, // SOLARIS
			null // JAVA
	};

	/**
	 * This retrieves the default sandbox pathname
	 *
	 * @see #getOs(String)
	 */
	public static String getClassPath() {
		try {
			return classpathes[getOs().ordinal()];
		} catch (final Exception e) {
		}
		return null;
	}

	/**
	 * This retrieves this enum string representation
	 *
	 * @return a array containing this enum string representation
	 */
	public static String[] getLabels() {
		final String[] labels = new String[SIZE];
		for (final OSEnum c : OSEnum.values()) {
			labels[c.ordinal()] = c.toString();
		}
		return labels;
	}

	public static void main(final String[] argv) {
		for (final OSEnum i : OSEnum.values()) {
			System.out.println(i.toString());
		}
	}

}
