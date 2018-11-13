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

package xtremweb.security;

import java.io.PrintStream;
import java.text.ParseException;

import xtremweb.common.UserRightEnum;

/**
 * XWAccessRights.java
 *
 * Created: Oct 26, 2006
 *
 * @author <a href="mailto:lodygens /at\ lal.in2p3.fr>Oleg Lodygensky</a>
 * @version %I%, %G%
 */

/**
 * This class describes static variables that define access rights
 */

public final class XWAccessRights {

	/**
	 * This contains this access rigths as integer
	 */
	private int value;

	/**
	 * This is the regular expression to change mode.<br />
	 * This reg exp is <code>[auog]{1,4}[+|-][trwx]{1,4}</code> Since 8.0.0,
	 * "+/-t" to change the sticky bit.
	 */
	public static final String REGEXP = "[auog]{0,4}[+|-][trwx]{1,4}";

	/**
	 * This defines the sticky bit. If the sticky bit is set, accessrights can
	 * be bypassed by a VWORKER_USER only
	 *
	 * @since 8.0.0
	 * @see UserRightEnum#VWORKER_USER
	 */
	public final static int STICKYBIT_INT = 0x01000;
	/**
	 * This defines the stickybit. If the sticky bit is set, accessrights can be
	 * bypassed
	 *
	 * @since 8.0.0
	 */
	public final static XWAccessRights STICKYBIT = new XWAccessRights(STICKYBIT_INT);

	/** owner do everything value */
	public final static int USERALL_INT = 0x0700;
	/** owner do everything */
	public final static XWAccessRights USERALL = new XWAccessRights(USERALL_INT);
	/** owner exec flag value */
	public final static int USEREXEC_INT = 0x0100;
	/** owner exec flag */
	public final static XWAccessRights USEREXEC = new XWAccessRights(USEREXEC_INT);
	/** owner write flag value */
	public final static int USERWRITE_INT = 0x0200;
	/** owner write flag */
	public final static XWAccessRights USERWRITE = new XWAccessRights(USERWRITE_INT);
	/** owner read/list flag value */
	public final static int USERREAD_INT = 0x0400;
	/** owner read/list flag */
	public final static XWAccessRights USERREAD = new XWAccessRights(USERREAD_INT);
	public final static int USERREADWRITE_INT = 0x0600;
	/** owner read/list flag */
	public final static XWAccessRights USERREADWRITE = new XWAccessRights(USERREADWRITE_INT);

	/** group user do everything value */
	public final static int GROUPALL_INT = 0x0070;
	/** group user can do everything */
	public final static XWAccessRights GROUPALL = new XWAccessRights(GROUPALL_INT);
	/** group user exec flag value */
	public final static int GROUPEXEC_INT = 0x0010;
	/** group user exec flag */
	public final static XWAccessRights GROUPEXEC = new XWAccessRights(GROUPEXEC_INT);
	/** group user write flag value */
	public final static int GROUPWRITE_INT = 0x0020;
	/** group user write flag */
	public final static XWAccessRights GROUPWRITE = new XWAccessRights(GROUPWRITE_INT);
	/** group user read/list flag value */
	public final static int GROUPREAD_INT = 0x0040;
	/** group user read/list flag */
	public final static XWAccessRights GROUPREAD = new XWAccessRights(GROUPREAD_INT);

	/** other user can do everything value */
	public final static int OTHERALL_INT = 0x0007;
	/** other user can do everything */
	public final static XWAccessRights OTHERALL = new XWAccessRights(OTHERALL_INT);
	/** can other user exec flag value */
	public final static int OTHEREXEC_INT = 0x0001;
	/** can other user exec flag */
	public final static XWAccessRights OTHEREXEC = new XWAccessRights(OTHEREXEC_INT);
	/** can other user write flag value */
	public final static int OTHERWRITE_INT = 0x0002;
	/** can other user write flag */
	public final static XWAccessRights OTHERWRITE = new XWAccessRights(OTHERWRITE_INT);
	/** can other user read/list flag value */
	public final static int OTHERREAD_INT = 0x0004;
	/** can other user read/list flag */
	public final static XWAccessRights OTHERREAD = new XWAccessRights(OTHERREAD_INT);

	/**
	 * Access denied integer value (0x0)
	 */
	public final static int NONE_INT = 0;
	/**
	 * Access denied
	 *
	 * @see #NONE_INT
	 */
	public final static XWAccessRights NONE = new XWAccessRights(NONE_INT);
	/**
	 * Group access rights integer value (0x750). Owner can do everything, group
	 * can read and execute, others (non group member) can do nothing
	 *
	 * @since 5.8.0
	 */
	public final static int OWNERGROUP_INT = USERALL_INT | GROUPREAD_INT | GROUPEXEC_INT;
	/**
	 * Group access rights. Owner can do everything, group can read and execute,
	 * others (non group member) can do nothing
	 *
	 * @since 5.8.0
	 * @see #OWNERGROUP_INT
	 */
	public final static XWAccessRights OWNERGROUP = new XWAccessRights(OWNERGROUP_INT);
	/**
	 * Default access rights integer value (0x755). Owner can do everything,
	 * group can read and execute, others can read and execute
	 */
	public final static int DEFAULT_INT = USERALL_INT | GROUPREAD_INT | GROUPEXEC_INT | OTHERREAD_INT | OTHEREXEC_INT;
	/**
	 * Default access rights. Owner can do everything, group can read and
	 * execute, others can read and execute
	 *
	 * @see #DEFAULT_INT
	 */
	public final static XWAccessRights DEFAULT = new XWAccessRights(DEFAULT_INT);
	/**
	 * Full access rights integer value (0x777). Stikcy bit set. Owner, group
	 * and others can read, write and execute
	 */
	public final static int ALL_INT = STICKYBIT_INT | USERALL_INT | GROUPALL_INT | OTHERALL_INT;
	/**
	 * Full access rights to owner, group and others can read, write and execute
	 *
	 * @see #ALL_INT
	 */
	public final static XWAccessRights ALL = new XWAccessRights(ALL_INT);

	public XWAccessRights(final int v) {
		value = v & ALL_INT;
	}

	public XWAccessRights(final String v) throws ParseException {
		value = NONE_INT;
		chmod(v);
	}

	/**
	 * This creates a new access rights according to the given string.
	 *
	 * @param v
	 *            represents the new access rights (e.g. "u+w", "0x700" etc.)
	 * @exception ParseException
	 *                is throw if v does not represent a valid access rights
	 * @see #REGEXP
	 */
	public void chmod(String v) throws ParseException {

		if (v == null) {
			value = NONE_INT;
			return;
		}

		v = v.trim().toLowerCase();

		try {
			if (v.startsWith("0x")) {
				value = Integer.parseInt(v.substring(2), 16) & ALL_INT;
			} else {
				value = Integer.parseInt(v, 10) & ALL_INT;
			}
		} catch (final NumberFormatException e) {
			if (v.matches(REGEXP) == false) {
				throw new ParseException(v + " is not a valid mod", 0);
			}

			if (v.indexOf('t') != -1) {
				if (v.indexOf('+') != -1) {
					value |= STICKYBIT_INT;
				} else if (v.indexOf('-') != -1) {
					value &= ~STICKYBIT_INT;
				}
			}

			if ((v.indexOf('a') != -1) || (v.indexOf('u') != -1)) {
				if (v.indexOf('r') != -1) {
					if (v.indexOf('+') != -1) {
						value |= USERREAD_INT;
					} else if (v.indexOf('-') != -1) {
						value &= ~USERREAD_INT;
					}
				}
				if (v.indexOf('w') != -1) {
					if (v.indexOf('+') != -1) {
						value |= USERWRITE_INT;
					} else if (v.indexOf('-') != -1) {
						value &= ~USERWRITE_INT;
					}
				}
				if (v.indexOf('x') != -1) {
					if (v.indexOf('+') != -1) {
						value |= USEREXEC_INT;
					} else if (v.indexOf('-') != -1) {
						value &= ~USEREXEC_INT;
					}
				}
			}
			if ((v.indexOf('a') != -1) || (v.indexOf('g') != -1)) {
				if (v.indexOf('r') != -1) {
					if (v.indexOf('+') != -1) {
						value |= GROUPREAD_INT;
					} else if (v.indexOf('-') != -1) {
						value &= ~GROUPREAD_INT;
					}
				}
				if (v.indexOf('w') != -1) {
					if (v.indexOf('+') != -1) {
						value |= GROUPWRITE_INT;
					} else if (v.indexOf('-') != -1) {
						value &= ~GROUPWRITE_INT;
					}
				}
				if (v.indexOf('x') != -1) {
					if (v.indexOf('+') != -1) {
						value |= GROUPEXEC_INT;
					} else if (v.indexOf('-') != -1) {
						value &= ~GROUPEXEC_INT;
					}
				}
			}
			if ((v.indexOf('a') != -1) || (v.indexOf('o') != -1)) {
				if (v.indexOf('r') != -1) {
					if (v.indexOf('+') != -1) {
						value |= OTHERREAD_INT;
					} else if (v.indexOf('-') != -1) {
						value &= ~OTHERREAD_INT;
					}
				}
				if (v.indexOf('w') != -1) {
					if (v.indexOf('+') != -1) {
						value |= OTHERWRITE_INT;
					} else if (v.indexOf('-') != -1) {
						value &= ~OTHERWRITE_INT;
					}
				}
				if (v.indexOf('x') != -1) {
					if (v.indexOf('+') != -1) {
						value |= OTHEREXEC_INT;
					} else if (v.indexOf('-') != -1) {
						value &= ~OTHEREXEC_INT;
					}
				}
			}
		}
	}

	public int value() {
		return value;
	}

	@Override
	public String toString() {
		return "" + value;
	}

	public String toHexString() {
		return ("0x" + Integer.toHexString(value));
	}

	/**
	 * This displays some help to output stream
	 */
	public final static void help(final PrintStream out) {
		out.printf("%04X : the sticky bit that allows to bypass access rights.\n", STICKYBIT_INT);
		out.printf("%04X : allow read  by owner.\n", USERREAD_INT);
		out.printf("%04X : allow write by owner.\n", USERWRITE_INT);
		out.printf("%04X : allow exec  by owner.\n", USEREXEC_INT);
		out.printf("%04X : allow read  by group.\n", GROUPREAD_INT);
		out.printf("%04X : allow write by group.\n", GROUPWRITE_INT);
		out.printf("%04X : allow exec  by group.\n", GROUPEXEC_INT);
		out.printf("%04X : allow read  by others.\n", OTHERREAD_INT);
		out.printf("%04X : allow write by others.\n", OTHERWRITE_INT);
		out.printf("%04X : allow exec  by others.\n", OTHEREXEC_INT);
		out.println("");
		out.println("Regular expression to change rights : " + REGEXP + " where :");
		out.println("u       The user permission bits in the original mode of the file.");
		out.println("g       The group permission bits in the original mode of the file.");
		out.println("o       The other permission bits in the original mode of the file.");
		out.println("a       is equivalent to ugo.");
		out.println("+       This sets   mode.");
		out.println("-       This clears mode.");
		out.println("t       The sticky  bit.");
		out.println("r       The read    bit.");
		out.println("w       The write   bit.");
		out.println("x       The execute bit.");
		out.println("e.g. : xwchmod 700; xwchmod go-rwx");
	}

	/**
	 * This dumps enums to stdout
	 */
	public static void main(final String[] argv) {
		help(new PrintStream(System.out));

		try {
			if (argv.length > 0) {
				final String mod = argv[0];
				System.out.println(mod + " = " + new XWAccessRights(mod).toHexString());
				final XWAccessRights r = XWAccessRights.DEFAULT;
				System.out.print(r.toHexString() + ".chmod(" + mod + ") = ");
				r.chmod(mod);
				System.out.println(r.toHexString());
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}
