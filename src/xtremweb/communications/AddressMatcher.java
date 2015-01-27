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

import java.security.Principal;
import java.util.StringTokenizer;

/**
 * AddressMatcher.java<br />
 * Bored to Helma Xmplrpc<br />
 * <br />
 * 
 * This aims to detect valid IP accordingly to this IP pattern.<br />
 * Example : if this stores 192.168.*.*, then 192.168.0.1 is a matching IP
 * address whereas 193.168.0.1 is not <br />
 * <br />
 * Created: Tue Jul 17 12:16:26 2001<br />
 * Modified: 15 juin 2006
 * 
 * @author See Helma
 * @author Oleg Lodygensky
 */
public class AddressMatcher implements Principal {

	private final int pattern[];

	/**
	 * This contructs an IP address matcher with the provided parameter
	 * 
	 * @param address
	 *            is an IP address string representation, eventually a wilcdard
	 *            address (e.g. "192.168.0.*")
	 * @exception NumberFormatException
	 *                is thrown if address parameter is not made with 4 dot
	 *                separated integers ot stars
	 */
	public AddressMatcher(String address) throws NumberFormatException {
		pattern = new int[4];
		final StringTokenizer st = new StringTokenizer(address, ".");
		if (st.countTokens() != 4) {
			throw new NumberFormatException("\"" + address
					+ "\" does not represent a valid IP address");
		}
		for (int i = 0; i < 4; i++) {
			final String next = st.nextToken();
			if ("*".equals(next)) {
				pattern[i] = 256;
			} else {
				pattern[i] = (byte) Integer.parseInt(next);
			}
		}
	}

	public boolean matches(byte address[]) {
		for (int i = 0; i < 4; i++) {
			if (pattern[i] > 255) {
				continue;
			}
			if (pattern[i] != address[i]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * This tests whether this address matcher matches provided parameter
	 * 
	 * @param ip
	 *            is an IP address string representation
	 * @return true if param matches this address matcher, false otherwise
	 */
	public boolean matchesExact(String ip) {
		try {
			final StringTokenizer st = new StringTokenizer(ip, ".");
			if (st.countTokens() != 4) {
				return false;
			}
			for (int i = 0; i < 4; i++) {
				final String next = st.nextToken();
				if ("*".equals(next)) {
					if (pattern[i] != 256) {
						return false;
					}
				} else if (pattern[i] != (byte) Integer.parseInt(next)) {
					return false;
				}
			}
			return true;
		} catch (final Exception e) {
		}
		return false;
	}

	/**
	 * This retrieves the IP address as String
	 * 
	 * @return this IP address match representation
	 */
	@Override
	public String toString() {
		String ret = "";
		for (int i = 0; i < 4; i++) {
			if (pattern[i] > 255) {
				ret += "*";
			} else if (pattern[i] < 0) {
				ret += (127 & pattern[i]) + 128;
			} else {
				ret += pattern[i];
			}
			ret += ".";
		}
		return ret.substring(0, ret.length() - 1) + " ";
	}

	/**
	 * This only calls toString()
	 * 
	 * @see #toString()
	 */
	public String getName() {
		return toString();
	}
	@Override public int hashCode() {
		return pattern.hashCode();	
	}
	/**
	 * This only calls matchesExact(String)
	 * 
	 * @param ip
	 *            is an IP address string representation
	 * @see #matchesExact(String)
	 */
	@Override
	public boolean equals(Object ip) {
		return matchesExact((String) ip);
	}

}
