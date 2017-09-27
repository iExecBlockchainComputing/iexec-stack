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

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * MD5InputStream, a subclass of FilterInputStream implementing MD5
 * functionality on a stream.
 * <p>
 * Originally written by Santeri Paavolainen, Helsinki Finland 1996 <br>
 * (c) Santeri Paavolainen, Helsinki Finland 1996 <br>
 * Some changes Copyright (c) 2002 Timothy W Macinta <br>
 * <p>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Library General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * <p>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library General Public License for more
 * details.
 * <p>
 * You should have received a copy of the GNU Library General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 675 Mass Ave, Cambridge, MA 02139, USA.
 * <p>
 * See http://www.twmacinta.com/myjava/fast_md5.php for more information on this
 * file.
 * <p>
 * Please note: I (Timothy Macinta) have put this code in the com.twmacinta.util
 * package only because it came without a package. I was not the the original
 * author of the code, although I did optimize it (substantially) and fix some
 * bugs.
 *
 * @author Santeri Paavolainen <santtu@cs.hut.fi>
 * @author Timothy W Macinta (twm@alum.mit.edu) (added main() method)
 **/

public class MD5InputStream extends FilterInputStream {
	/**
	 * MD5 context
	 */
	private final MD5 md5;

	/**
	 * Creates a MD5InputStream
	 *
	 * @param in
	 *            The input stream
	 */
	public MD5InputStream(final InputStream in) {
		super(in);

		md5 = new MD5();
	}

	/**
	 * Read a byte of data.
	 *
	 * @see java.io.FilterInputStream
	 */
	@Override
	public int read() throws IOException {
		final int c = in.read();

		if (c == -1) {
			return -1;
		}

		if ((c & ~0xff) != 0) {
			System.out.println("MD5InputStream.read() got character with (c & ~0xff) != 0)!");
		} else {
			md5.update(c);
		}

		return c;
	}

	/**
	 * Reads into an array of bytes.
	 *
	 * @see java.io.FilterInputStream
	 */
	@Override
	public int read(final byte bytes[], final int offset, final int length) throws IOException {
		int r;

		if ((r = in.read(bytes, offset, length)) == -1) {
			return r;
		}

		md5.update(bytes, offset, r);

		return r;
	}

	/**
	 * Returns array of bytes representing hash of the stream as finalized for
	 * the current state.
	 *
	 * @see MD5#Final
	 */
	public byte[] hash() {
		return md5.Final();
	}

	public MD5 getMD5() {
		return md5;
	}

	/**
	 * This method is here for testing purposes only - do not rely on it being
	 * here.
	 **/
	public static void main(final String[] arg) {

		// //////////////////////////////////////////////////////////////
		//
		// usage: java com.twmacinta.util.MD5InputStream [--use-default-md5]
		// [--no-native-lib] filename
		//
		// ///////

		// determine the filename to use and the MD5 impelementation to use

		final String filename = arg[arg.length - 1];
		try (final FileInputStream fis = new FileInputStream(filename);
				final InputStream is = new BufferedInputStream(fis);
				final MD5InputStream in = new MD5InputStream(new BufferedInputStream(fis))) {
			boolean useDefaultMd5 = false;
			boolean useNativeLib = true;
			for (int i = 0; i < (arg.length - 1); i++) {
				if (arg[i].equals("--use-default-md5")) {
					useDefaultMd5 = true;
				} else if (arg[i].equals("--no-native-lib")) {
					useNativeLib = false;
				}
			}

			// initialize common variables

			final byte[] buf = new byte[65536];
			int num_read;

			// Use the default MD5 implementation that comes with Java

			if (useDefaultMd5) {
				final java.security.MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
				while ((num_read = is.read(buf)) != -1) {
					digest.update(buf, 0, num_read);
				}
				System.out.println(MD5.asHex(digest.digest()) + "  " + filename);

				// Use the optimized MD5 implementation

			} else {

				// disable the native library search, if requested

				if (!useNativeLib) {
					MD5.initNativeLibrary(true);
				}

				// calculate the checksum

				while ((num_read = in.read(buf)) != -1) {
					;
				}
				System.out.println(MD5.asHex(in.hash()) + "  " + filename);
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

}
