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
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 ** NewClassLoader provides a minimalistic ClassLoader which shows how to load a
 * class which resides in a .class file. <br>
 * <br>
 **
 ** @author Gilles Fedak
 **
 ** @version %I% %G%
 **
 **/

public class NewClassLoader extends MultiClassLoader {
	// The added class paths
	private final List<String> paths;

	public NewClassLoader() {

		paths = new ArrayList<String>();
	}

	public void addClassPath(final String path) {
		paths.add(path);

	}

	public void removeClassPath(final String path) {
		for (int i = 0; i < paths.size(); i++) {
			final String p = paths.get(i);
			if (p.equals(path)) {
				paths.remove(i);
				break;
			}
		}
	}

	protected byte[] getClassFromAddedClassPaths(final String className) {

		byte[] result = null;
		int rb, chunk, size;
		final String fsep = System.getProperty("file.separator");

		try {
			final String fileName = formatClassName(className);

			// Lookup the class into all the added class paths
			for (int i = 0; i < paths.size(); i++) {
				final String path = paths.get(i);

				final File f = new File(path + fsep + fileName);
				if (f.exists()) {
					final FileInputStream fis = new FileInputStream(f);

					size = (int) f.length();
					result = new byte[size];
					rb = 0;
					chunk = 0;
					while ((size - rb) > 0) {
						chunk = fis.read(result, rb, size - rb);
						if (chunk == -1) {
							break;
						}
						rb += chunk;
					}

					break;
				}
			}
		} catch (final Exception e) {
		}
		return result;
	}

	@Override
	protected byte[] loadClassBytes(final String className) {

		return (getClassFromAddedClassPaths(className));
	}

} // End of Class NewClassLoader.
