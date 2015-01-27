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

import java.util.Hashtable;
import java.util.Map;

/**
 * A simple class loader capable of loading from multiple sources, such as local
 * files or a URL.
 * 
 * This class is derived from an article by Chuck McManis
 * http://www.javaworld.com/javaworld/jw-10-1996/indepth.src.html with large
 * modifications.
 * 
 * Note that this has been updated to use the non-deprecated version of
 * defineClass() -- JDM.
 * 
 * @author Jack Harich - 8/18/97
 * @author John D. Mitchell - 99.03.04
 */
public abstract class MultiClassLoader extends ClassLoader {

	// ---------- Fields --------------------------------------
	private final Map<String, Class> classes = new Hashtable<String, Class>();
	private char classNameReplacementChar;

	private final boolean monitorOn = false;
	private final boolean sourceMonitorOn = true;

	// ---------- Initialization ------------------------------
	public MultiClassLoader() {
	}

	// ---------- Superclass Overrides ------------------------
	/**
	 * This is a simple version for external clients since they will always want
	 * the class resolved before it is returned to them.
	 */
	@Override
	public Class loadClass(String className) throws ClassNotFoundException {
		return (loadClass(className, true));
	}

	// ---------- Abstract Implementation ---------------------
	@Override
	public synchronized Class loadClass(String className, boolean resolveIt)
			throws ClassNotFoundException {

		Class result;
		byte[] classBytes;
		monitor(">> MultiClassLoader.loadClass(" + className + ", " + resolveIt
				+ ")");

		// ----- Check our local cache of classes
		result = classes.get(className);
		if (result != null) {
			monitor(">> returning cached result.");
			notifyAll();
			return result;
		}

		// ----- Check with the primordial class loader
		try {
			result = super.findSystemClass(className);
			monitor(">> returning system class (in CLASSPATH).");
			notifyAll();
			return result;
		} catch (final ClassNotFoundException e) {
			monitor(">> Not a system class.");
		}

		// ----- Try to load it from preferred source
		// Note loadClassBytes() is an abstract method
		classBytes = loadClassBytes(className);
		if (classBytes == null) {
			notifyAll();
			throw new ClassNotFoundException();
		}

		// ----- Define it (parse the class file)
		result = defineClass(className, classBytes, 0, classBytes.length);
		if (result == null) {
			notifyAll();
			throw new ClassFormatError();
		}

		// ----- Resolve if necessary
		if (resolveIt) {
			resolveClass(result);
		}

		// Done
		classes.put(className, result);
		monitor(">> Returning newly loaded class.");
		notifyAll();
		return result;
	}

	// ---------- Public Methods ------------------------------
	/**
	 * This optional call allows a class name such as "COM.test.Hello" to be
	 * changed to "COM_test_Hello", which is useful for storing classes from
	 * different packages in the same retrival directory. In the above example
	 * the char would be '_'.
	 */
	public void setClassNameReplacementChar(char replacement) {
		classNameReplacementChar = replacement;
	}

	// ---------- Protected Methods ---------------------------
	protected abstract byte[] loadClassBytes(String className);

	protected String formatClassName(String className) {
		if (classNameReplacementChar == '\u0000') {
			// '/' is used to map the package to the path
			return className.replace('.', '/') + ".class";
		} else {
			// Replace '.' with custom char, such as '_'
			return className.replace('.', classNameReplacementChar) + ".class";
		}
	}

	protected void monitor(String text) {
		if (monitorOn) {
			print(text);
		}
	}

	// --- Std
	protected static void print(String text) {
		System.out.println(text);
	}

} // End class
