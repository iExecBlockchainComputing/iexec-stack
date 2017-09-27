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

import java.io.IOException;
import java.io.InputStream;

// This class is borrowed from Apache JServ

class ServerInputStream extends InputStream {
	// bytes remaining to be read from the input stream. This is
	// initialized from CONTENT_LENGTH (or getContentLength()).
	// This is used in order to correctly return a -1 when all the
	// data POSTed was read. If this is left to -1, content length is
	// assumed as unknown and the standard InputStream methods will be used
	private long available = -1;

	private final InputStream in;

	public ServerInputStream(final InputStream in, final int available) {
		this.in = in;
		this.available = available;
	}

	@Override
	public int read() throws IOException {
		if (available > 0) {
			available--;
			return in.read();
		} else if (available == -1) {
			return in.read();
		}
		return -1;
	}

	@Override
	public int read(final byte b[]) throws IOException {
		return read(b, 0, b.length);
	}

	@Override
	public int read(final byte b[], final int off, int len) throws IOException {
		if (available > 0) {
			if (len > available) {
				// shrink len
				len = (int) available;
			}
			final int read = in.read(b, off, len);
			if (read != -1) {
				available -= read;
			} else {
				available = -1;
			}
			return read;
		} else if (available == -1) {
			return in.read(b, off, len);
		}
		return -1;
	}

	@Override
	public long skip(final long n) throws IOException {
		final long skip = in.skip(n);
		if (available > 0) {
			available -= skip;
		}
		return skip;
	}

}
