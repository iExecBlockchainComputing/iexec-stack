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

import java.io.IOException;
import java.io.OutputStream;

/**
 * Copyright (c) 2001, 2002 by Pensamos Digital, All Rights Reserved.
 * <p>
 *
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
 * This OutputStream discards all data written to it.
 *
 * @author Tim Macinta (twm@alum.mit.edu)
 **/

public class NullOutputStream extends OutputStream {

	private boolean closed = false;

	public NullOutputStream() {
	}

	@Override
	public void close() {
		this.closed = true;
	}

	@Override
	public void flush() throws IOException {
		if (this.closed) {
			throwClosed();
		}
	}

	private void throwClosed() throws IOException {
		throw new IOException("This OutputStream has been closed");
	}

	@Override
	public void write(final byte[] b) throws IOException {
		if (this.closed) {
			throwClosed();
		}
	}

	@Override
	public void write(final byte[] b, final int offset, final int len) throws IOException {
		if (this.closed) {
			throwClosed();
		}
	}

	@Override
	public void write(final int b) throws IOException {
		if (this.closed) {
			throwClosed();
		}
	}

}
