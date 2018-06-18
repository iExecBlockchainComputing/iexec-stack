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

package xtremweb.worker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class StreamPiper implements Runnable {

	private final BufferedReader input;
	private final BufferedWriter output;
	private boolean canRun = true;

	public StreamPiper(final InputStream in, final OutputStream out) {

		input = new BufferedReader(new InputStreamReader(in));
		output = new BufferedWriter(new OutputStreamWriter(out));
	}

	public void terminate() {
		canRun = false;
	}

	@Override
	public void run() {
		try {
			int byteRead = input.read();
			while (canRun && (byteRead >= 0)) {
				output.write(byteRead);
				byteRead = input.read();
			}
			input.close();
			output.close();
		} catch (final IOException e) {
			e.printStackTrace();
			System.err.println("stream piper error " + e);
		}
	}
}
