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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This helps to create an SSL challenge
 * 
 * @author Oleg Lodygensky
 * @since 7.4.0
 */

public class Protection {
	public static byte[] makeDigest(String user, String password, long t1,
			double q1) throws NoSuchAlgorithmException {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA");
			md.update(user.getBytes());
			md.update(password.getBytes());
			md.update(makeBytes(t1, q1));
			return md.digest();
		} finally {
			md = null;
		}
	}

	public static byte[] makeBytes(long t, double q) {
		ByteArrayOutputStream byteOut = null;
		DataOutputStream dataOut = null;
		try {
			byteOut = new ByteArrayOutputStream();
			dataOut = new DataOutputStream(byteOut);
			dataOut.writeLong(t);
			dataOut.writeDouble(q);
			final byte[] ret = byteOut.toByteArray();
			return ret;
		} catch (final IOException e) {
			return new byte[0];
		} finally {
			try {
				dataOut.close();
			} catch (final Exception ignore) {
			}
			byteOut = null;
			dataOut = null;
		}
	}
}
