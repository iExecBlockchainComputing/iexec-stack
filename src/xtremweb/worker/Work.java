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

/**
 * Work.java
 *
 *
 * Created: Mon Jun  4 15:28:06 2001
 *
 * @author <a href="mailto:fedak@lri.fr">Gilles Fedak</a>
 */

import java.io.File;
import java.io.IOException;

import xtremweb.common.StatusEnum;
import xtremweb.common.WorkInterface;
import xtremweb.common.XWTools;

/**
 * This class describes a work to compute as managed by the worker
 */
public final class Work extends WorkInterface {

	/**
	 * This is the work description file name
	 */
	public static final String workDescName = "workDesc";

	/**
	 * This is the directory where the work is stored which name is
	 * <tmpDir>/XW.<userName>.<hostName>/works/<UID>/ This directory contains :
	 * <li>workDesc file (a text file describing the work) so that work can be
	 * resumed (see variable workDescName above); <li>scratch, the directory
	 * where the work is run so that it finds its I/O files and directories
	 * structure as eventually provided in its files; <li>the result file if
	 * computation run successfully which name is UID.zip
	 */
	private File scratchDir;

	/** The scratch dir name */
	private final String SCRATCHNAME = "scratch";

	/**
	 * This is this work package name (worker shared data used by the data driven scheduler)
	 * @since 10.0.0
	 */
	private String packageName;

	/**
	 * This creates a new work, providing its interface. This is typically
	 * called as a new work is provided by the server. This writes all necessary
	 * informations to disk, so that this work could be recreated, if necessary
	 * 
	 * @see #Work(WorkInterface, boolean)
	 * @param job
	 *            describes this work
	 * @exception IOException
	 *                is thrown on disk I/O error
	 */
	public Work(WorkInterface job) throws IOException {
		this(job, true);
	}

	/**
	 * This creates a new work writing, or not, on disk. This is needed on I/O
	 * problems so that we can send a ERROR message to the dispatcher
	 * 
	 * @see PoolWork#addWork(WorkInterface)
	 * @see CommManager#run()
	 * @param job
	 *            is this work informations
	 * @param dir
	 *            tells to check directory structure or not
	 * @since XtremWeb 1.3.12
	 * @exception IOException
	 *                is thrown on disk I/O error
	 */
	public Work(WorkInterface job, boolean dir) throws IOException {

		super(job);

		packageName = null;
		if (dir) {
			prepareDir();
		} else {
			clean();
		}
	}

	/**
	 * This creates needed directory structure accordingly to package, if any
	 * @throws IOException 
	 */
	public void prepareDir() throws IOException  {
		final String extension = "" + getUID().toString() + "_cwd";
		final File root = packageName != null ? Worker.getConfig().getDataPackageDir(packageName) : Worker.getConfig().getWorksDir();
		setScratchDir(new File(root, extension));
	}

	/**
	 * This creates needed directory structure
	 * @param v is the base path
	 * @throws IOException 
	 */
	private synchronized void setScratchDir(final File v) throws IOException {
		scratchDir = new File(v, SCRATCHNAME);
		getLogger().config("SCRATCHDIR = " + scratchDir);
		XWTools.checkDir(scratchDir);
		notifyAll();
	}

	/**
	 * This return the scratch directory
	 * @return
	 */
	public File getScratchDir() {
		return scratchDir;
	}

	/**
	 * This calls clean(scratchDir);
	 */
	public void clean() {
		clean(scratchDir);
	}

	/**
	 * This erases all work dir/files from disk
	 */
	private synchronized void clean(final File dirWork) {
		if (dirWork == null) {
			notifyAll();
			return;
		}

		getLogger().config("cleaning = " + dirWork);

		if (dirWork.isDirectory()) {

			final String[] files = dirWork.list();
			if (files == null) {
				notifyAll();
				return;
			}

			for (int i = 0; i < files.length; i++) {
				clean(new File(dirWork, files[i]));
			}
		}
		dirWork.delete();
		notifyAll();
	}

	/**
	 * This sets this work package name (worker shared data used by the data driven scheduler)
	 * @param pkg is this work package name
	 * @since 10.0.0
	 */
	public void setPackage(final String pkg) {
		this.packageName = pkg;
	}
	/**
	 * This retrieves this work package name (worker shared data used by the data driven scheduler)
	 * @since 10.0.0
	 */
	public String getPackage() {
		return packageName;
	}

	/**
	 * This checks if this work has a package name
	 * @since 10.0.0
	 * @return true if packageName attribute is set; false otherwise
	 */
	public boolean hasPackage() {
		return (packageName != null) && (packageName.length() > 0);
	}

	public String getScratchDirName() throws IOException {
		return scratchDir.getCanonicalPath();
	}

	public boolean isRunning() {
		return (getStatus() == StatusEnum.RUNNING);
	}

	@Override
	public synchronized void setRunning() {
		setStatus(StatusEnum.RUNNING);
		notifyAll();
	}

	public boolean isCompleted() {
		return (getStatus() == StatusEnum.COMPLETED);
	}

	@Override
	public synchronized void setCompleted() {
		setStatus(StatusEnum.COMPLETED);
		notifyAll();
	}

	public synchronized void setAborted() {
		setStatus(StatusEnum.ABORTED);
		notifyAll();
	}

	public boolean isError() {
		return (getStatus() == StatusEnum.ERROR);
	}

	public synchronized void setError() {
		setStatus(StatusEnum.ERROR);
		notifyAll();
	}

	public boolean isPending() {
		return (getStatus() == StatusEnum.PENDING);
	}

	@Override
	public synchronized void setPending() {
		setStatus(StatusEnum.PENDING);
		notifyAll();
	}
}
