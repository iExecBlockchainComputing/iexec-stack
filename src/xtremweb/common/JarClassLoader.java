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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.jar.Attributes;

public class JarClassLoader extends java.net.URLClassLoader {

	private final URL url;

	public JarClassLoader(final URL url) {
		super(new URL[] { url });
		this.url = url;
	}

	public String getMainClassName() throws IOException {
		final URL u = new URL("jar", "", url + "!/");
		final JarURLConnection uc = (JarURLConnection) u.openConnection();
		final Attributes attr = uc.getMainAttributes();
		return attr != null ? attr.getValue(Attributes.Name.MAIN_CLASS) : null;
	}

	public void invokeClass(final String name, final String[] args)
			throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException {

		final Class c = loadClass(name);
		final Method m = c.getMethod("main", new Class[] { args.getClass() });
		m.setAccessible(true);
		final int mods = m.getModifiers();
		if ((m.getReturnType() != void.class) || !Modifier.isStatic(mods) || !Modifier.isPublic(mods)) {
			throw new NoSuchMethodException("main");
		}
		try {
			m.invoke(null, new Object[] { args });
		} catch (final IllegalAccessException e) {
			System.err.println("JarClassLoader " + e);
		}
	}

	public static void main(final String[] args) {
		try (final JarClassLoader loader = new JarClassLoader(new URL(args[0]))){
			final String[] theargs = new String[args.length - 2];
			System.arraycopy(args, 2, theargs, 0, args.length - 2);
			loader.invokeClass(args[1], theargs);
		} catch (final Exception e) {
			e.printStackTrace();
			System.out.println("Usage : JarClassLoader URL className arg [arg, arg ...]");
		}
	}
}
