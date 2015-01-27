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

package xtremweb.exec;

/**
 * ClassExecutor.java
 * 
 * 
 * Created: Sun Feb 27 14:34:40 2005
 * 
 * @author <a href="mailto:fedak@lri.fr">Gilles Fedak</a>
 * @version 1.0
 */
public class ClassExecutor extends JavaByteCodeExecutor {

	public ClassExecutor(String[] classPath, String jarName, String argv[])
			throws ExecutorLaunchException {
		super((classPath == null ? null : Executor.join(classPath, ":")),
				jarName, (argv == null ? null : Executor.join(argv, " ")), true);
	}

	public ClassExecutor(String classPath, String jarName, String argv)
			throws ExecutorLaunchException {
		super(classPath, jarName, argv, true);
	} // JarExecutor constructor

	public static void main(String[] args) {

	} // end of main()

}
