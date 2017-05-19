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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.StringTokenizer;

import xtremweb.common.Logger;
import xtremweb.common.LoggerLevel;

/**
 * XWUtilLinux.java Samuel Heriard
 *
 * Linux impl. of XWUtil
 */
public class XWUtilLinux extends XWUtilImpl {

	private long machineTotal = 0, machineTotalOld = 0;
	private long machineUser = 0, machineUserOld = 0;
	private long machineNice = 0, machineNiceOld = 0;
	private long machineIdle = 0, machineIdleOld = 0;
	private long machineSys = 0, machineSysOld = 0;

	private long totalPidUserOld = 0;

	private LinkedList<Integer> pids = null;
	private final Logger logger;

	public XWUtilLinux() {
		logger = new Logger(this);
		logger.setLoggerLevel(LoggerLevel.INFO);
	}

	/**
	 * This retrieves family PIDs
	 *
	 */
	@Override
	public void raz() {
		pids = (LinkedList<Integer>)getProcessFamily(getPid());
		if (pids == null) {
			logger.error("XWUtilLinux::raz () : can't pids");
		}
	}

	/**
	 * This retrieves the CPU loads : user, nice, sys, idle and total loads
	 * Param : $1 is the CPU number
	 *
	 */
	private void cpuLoads() {

		final File procStats = new File("/proc/stat");

		if (!procStats.exists()) {
			logger.error("Cannot read /proc/stat");
			return;
		}

		try (final BufferedReader bufferFile = new BufferedReader(new FileReader(procStats))) {
			String line = "";

			line = bufferFile.readLine();
			bufferFile.close();

			// each processor section begins with
			// cpu : jiffies ...

			if (line.indexOf("cpu") == 0) {

				final StringTokenizer tokenizer = new StringTokenizer(line, "\t ");

				tokenizer.nextToken();

				final long machineUserTmp = machineUser;
				final long machineNiceTmp = machineNice;
				final long machineSysTmp = machineSys;
				final long machineIdleTmp = machineIdle;

				final long machineTotalTmp = machineTotal;

				machineUser = new Long(tokenizer.nextToken()).longValue();
				machineNice = new Long(tokenizer.nextToken()).longValue();
				machineSys = new Long(tokenizer.nextToken()).longValue();
				machineIdle = new Long(tokenizer.nextToken()).longValue();

				machineTotal = machineUser + machineNice + machineSys + machineIdle;

				machineUserOld = machineUserTmp;
				machineNiceOld = machineNiceTmp;
				machineSysOld = machineSysTmp;
				machineIdleOld = machineIdleTmp;

				machineTotalOld = machineTotalTmp;

			} else {
				logger.error("can't find cpu line in /proc/stat");
			}

		} catch (final Exception e) {
			logger.exception(e);
		}

		return;
	}

	/**
	 * This retrieves this machine average cpu load percentage
	 *
	 * @return the average cpu load percentage (0 <= ret <= 100)
	 */
	@Override
	public int getCpuLoad() {

		cpuLoads();

		final long diffMachineTotal = machineTotal - machineTotalOld;
		final long diffMachineUser = machineUser - machineUserOld;

		if (diffMachineTotal == 0) {
			return 100;
		}
		return (int) ((diffMachineUser * 100) / diffMachineTotal);
	}

	/**
	 * This retrieves the process group ID of the provided process which is set
	 * by default to the PPID See : man getpgrp
	 *
	 * @param pid
	 *            is the process PID
	 * @return -1 on error
	 */
	private int getProcessGroup(final int pid) {

		final File procStat = new File("/proc/" + pid + "/stat");

		if (!procStat.exists()) {
			logger.error("can't get process group from /proc/" + pid + "/stat");
			return -1;
		}

		try (BufferedReader bufferFile = new BufferedReader(new FileReader(procStat))) {

			String line = "";
			line = bufferFile.readLine();
			final StringTokenizer tokenizer = new StringTokenizer(line, "\t ");

			for (int j = 0; j < 3; j++) {
				tokenizer.nextToken();
			}

			return Integer.parseInt(tokenizer.nextToken());
		} catch (final Throwable e) {
			logger.error("XWUtilLinux::getProcessGroup () 02 : " + e);
		}

		return -1;
	}

	/**
	 * This retrieves the children PID of this process including this process
	 * PID itself. It is based on process groud ID which is set by defautl to
	 * the PPID See : man getpgrp
	 *
	 * @param parent
	 *            is the process PID
	 * @see #getProcessGroup (int)
	 */
	private Collection<Integer> getProcessFamily(final int parent) {

		LinkedList<Integer> ret = new LinkedList<Integer>();
		final File dir = new File("/proc/");
		final int grp = getProcessGroup(parent);

		if (grp == -1) {
			return null;
		}

		final String[] list = dir.list();

		for (int i = 0; i < list.length; i++) {

			final File file = new File(dir, list[i]);
			File procStats = null;

			if (!file.isDirectory()) {
				continue;
			}

			procStats = new File(file, "stat");
			if (!procStats.exists() || !procStats.isFile()) {
				continue;
			}

			try (BufferedReader bufferFile = new BufferedReader(new FileReader(procStats))){
				String line = bufferFile.readLine();

				final StringTokenizer tokenizer = new StringTokenizer(line, "\t ");
				final int thisChildPid = new Integer(tokenizer.nextToken()).intValue();
				final int thisChildGid = getProcessGroup(thisChildPid);

				if (thisChildGid == grp) {
					ret.add(new Integer(thisChildPid));
				}
			} catch (final Throwable e) {
				e.printStackTrace();
				logger.error("XWUtilLinux::getProcessFamily ()" + e);
				ret = null;
			}
		}

		return ret;
	}

	/**
	 * This retrieves the CPU load in user mode for the given PID Param : $1 is
	 * the PID
	 *
	 */
	private int processUser(final int pid) {

		final File procStats = new File("/proc/" + pid + "/stat");

		if (!procStats.exists()) {
			logger.error("can't get process user from /proc/" + pid + "/stat");
			return 0;
		}

		try (BufferedReader bufferFile = new BufferedReader(new FileReader(procStats))){

			String line = "";
			line = bufferFile.readLine();
			final StringTokenizer tokenizer = new StringTokenizer(line, "\t ");

			for (int i = 0; i < 13; i++) {
				tokenizer.nextToken();
			}

			return new Long(tokenizer.nextToken()).intValue();
		} catch (final Throwable e) {
			e.printStackTrace();
			logger.error("XWUtilLinux::getProcessUser () 02 : " + e);
		}

		return 0;
	}

	/**
	 * This retrieves this process cpu load average, including all its children
	 *
	 * @return the average cpu load percentage (0 <= ret <= 100)
	 */
	@Override
	public int getProcessLoad() {

		long diffMachineTotal = machineTotal - machineTotalOld;

		long totalPidUser = 0;

		if (pids == null) {
			logger.error("processLoad () : pids == null");
			return 0;
		}

		final Iterator<Integer> theIterator = pids.iterator();

		while (theIterator.hasNext()) {

			final int pid = theIterator.next().intValue();

			totalPidUser = totalPidUser + processUser(pid);
		}

		final long diffPidUser = totalPidUser - totalPidUserOld;

		if (diffMachineTotal == 0) {
			diffMachineTotal = 100;
		}

		final int pidPercentage = (int) ((diffPidUser * 100) / diffMachineTotal);

		totalPidUserOld = totalPidUser;

		return pidPercentage;
	}

	/**
	 * in MHz
	 */
	@Override
	public int getSpeedProc() {
		String valStr = null;
		final File procInterrupts = new File("/proc/cpuinfo");

		if (!procInterrupts.exists()) {
			logger.error("Cannot read /proc/cpuinfo");
			return 0;
		}

		try (final BufferedReader bufferFile = new BufferedReader(new FileReader(procInterrupts))) {
			String l = "";

			while ((l != null) && (valStr == null)) {
				if (l.indexOf("cpu MHz") != -1) {
					final int start = l.indexOf(":") + 1;
					if (start != -1) {
						valStr = l.substring(start);
					}
				}
				l = bufferFile.readLine();
			}
			bufferFile.close();

			if (valStr != null) {
				return new Float(valStr).intValue();
			}
		} catch (final Exception e) {
			logger.error(" Exception: " + e);
			return 0;
		}

		return 0;
	}

	@Override
	public String getProcModel() {
		String valStr = null;
		final File procInterrupts = new File("/proc/cpuinfo");

		if (!procInterrupts.exists()) {
			logger.error("Cannot read /proc/cpuinfo");
			return new String();
		}

		try (final BufferedReader bufferFile = new BufferedReader(new FileReader(procInterrupts))) {
			String l = "";

			while ((l != null) && (valStr == null)) {

				if (l.indexOf("model name") != -1) {
					final int start = l.indexOf(":") + 1;
					if (start != -1) {
						valStr = l.substring(start);
					}
				}
				l = bufferFile.readLine();
			}
			bufferFile.close();
		} catch (final Exception e) {
			logger.error(" Exception: " + e);
			return "";
		}

		if (valStr != null) {
			return valStr;
		}
		return "";
	}

	/**
	 * in Kb
	 */
	@Override
	public long getTotalMem() {
		String valStr = null;
		final File procInterrupts = new File("/proc/meminfo");

		if (!procInterrupts.exists()) {
			logger.error("Cannot read /proc/meminfo");
			return 0;
		}

		try (final BufferedReader bufferFile = new BufferedReader(new FileReader(procInterrupts))) {
			String l = "";

			while ((l != null) && (valStr == null)) {
				if (l.indexOf("MemTotal") != -1) {
					final int start = l.indexOf(":") + 1;
					if (start != -1) {
						valStr = l.substring(start);
					}
				}
				l = bufferFile.readLine();
			}
			bufferFile.close();
			if (valStr != null) {
				final int kb = valStr.indexOf("kB");
				valStr = valStr.substring(0, kb);
				return new Float(valStr).longValue();
			}
		} catch (final Exception e) {
			logger.error(" Exception: " + e);
			return 0;
		}
		return 0;
	}

	/**
	 * in Kb
	 */
	@Override
	public long getTotalSwap() {
		String valStr = null;
		final File procInterrupts = new File("/proc/meminfo");

		if (!procInterrupts.exists()) {
			logger.error("Cannot read /proc/meminfo");
			return 0;
		}

		try (final BufferedReader bufferFile = new BufferedReader(new FileReader(procInterrupts))) {
			String l = "";

			while ((l != null) && (valStr == null)) {
				if (l.indexOf("SwapTotal") != -1) {
					final int start = l.indexOf(":") + 1;
					if (start != -1) {
						valStr = l.substring(start);
					}
				}
				l = bufferFile.readLine();
			}
			bufferFile.close();
			if (valStr != null) {
				final int kb = valStr.indexOf("kB");
				valStr = valStr.substring(0, kb);
				return new Float(valStr).longValue();
			}
		} catch (final Exception e) {
			logger.error(" Exception: " + e);
			return 0;
		}
		return 0;
	}

	/**
	 * This is the standard main () method args[0] may contain a PID to
	 * calculate its user CPU usage
	 */
	public static void main(final String[] args) {

		final XWUtilLinux cpu = new XWUtilLinux();

		while (true) {

			final int cpuLoad = cpu.getCpuLoad();
			if (args.length > 0) {
				try {
					cpu.pids = (LinkedList<Integer>) cpu.getProcessFamily(new Integer(args[0]).intValue());
					final int processLoad = cpu.getProcessLoad();
					System.out.print("%CPU = " + cpuLoad);
					System.out.print("  %CPU [" + args[0] + "] = " + processLoad);
					System.out.println("  allocated = " + (cpuLoad - processLoad));
				} catch (final Exception e) {
					e.printStackTrace();
				}
			}

			try {
				java.lang.Thread.sleep(1000);
			} catch (final Exception e) {
			}
		}
	}

}
