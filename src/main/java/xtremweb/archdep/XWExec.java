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

package xtremweb.archdep;

/**
 * <p>
 * This interface describes needed methods to manage process
 * </p>
 * <p>
 * An object implementing this interface can be obtained with the
 * <code>xwexec()</code> of <code>ArchDepFactory</code>
 * </p>
 *
 * @see xtremweb.archdep.ArchDepFactory
 */

public interface XWExec {

	void init();

	// Launch the execution on a separate process
	boolean exec(String[] args, String stdin, String stdout, String stderr, String workingDir);

	// Stop the curant execution
	boolean kill();

	boolean destroy();

	// Wait for the end of the current execution
	int waitFor();

	// Suspend the current execution
	boolean suspend();

	// Re-activate the current execution
	boolean activate();

	boolean isRunning();

}
