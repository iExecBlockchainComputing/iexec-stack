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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Logger.java
 * 
 * Created : 15 avril 2008
 * 
 * @author Oleg Lodygensky
 */

public class Logger {

	private LoggerLevel level;
	private String name = null;

	/**
	 * This retrieves the logging level
	 * 
	 * @return the current logging level
	 */
	public LoggerLevel getLoggerLevel() {
		return level;
	}

	/**
	 * This sets the logging level
	 * 
	 * @param l
	 *            is the new logging level
	 */
	public void setLoggerLevel(LoggerLevel l) {
		level = l;
	}

	/**
	 * This sets name to null. This sets the logging level from
	 * System.getProperty, if set. This sets the logging level to INFO if
	 * System.getProperty is not set.
	 */
	public Logger() {
		level = LoggerLevel.INFO;
		name = null;
		final String levelstr = System.getProperty(XWPropertyDefs.LOGGERLEVEL
				.toString());
		try {
			level = LoggerLevel.valueOf(levelstr.toUpperCase());
		} catch (final Exception e) {
		}
	}

	/**
	 * This calls this() and sets the name to the parameter class name
	 */
	public Logger(Object o) {
		this();
		name = o.getClass().getName();
	}

	/**
	 * This calls this() and sets the logging level to the provided one
	 */
	public Logger(LoggerLevel l) {
		this();
		level = l;
	}

	/**
	 * This calls this() and sets the name and the logging level to the provided
	 * ones
	 */
	public Logger(LoggerLevel l, Object o) {
		this(o);
		level = l;
	}

	/**
	 * This helps to format date : the format is "yyyy-MM-dd HH:mm:ss"
	 */
	private static final SimpleDateFormat LOG_DATE_FORMAT = new SimpleDateFormat(
			"[dd/MMM/yyyy:HH:mm:ss Z]", Locale.US);

	/**
	 * This logs out a message
	 */
	public void printLog(LoggerLevel levelmsg, String msg) {
		String n = (name != null ? name + "_" : "")
				+ Thread.currentThread().getName() + "_"
				+ Thread.currentThread().getId();
		System.out.println(LOG_DATE_FORMAT.format(new Date()) + " [" + n + "] "
				+ levelmsg + " : " + msg);
		n = null;
	}

	/**
	 * This logs out an exception
	 */
	public void exception(Exception e) {
		String str = e.toString();
		Throwable cause = e.getCause();
		warn(str + " (" + (cause == null ? e.getMessage() : cause.toString())
				+ ")");
		cause = null;
		str = null;
		if (debug()) {
			e.printStackTrace();
		}
	}

	/**
	 * This logs out an exception
	 */
	public void exception(String msg, Exception e) {
		error(msg);
		exception(e);
	}

	/**
	 * This tells whether logger level is set to FINEST
	 * 
	 * @return true if logger level is set to FINEST
	 */
	public boolean finest() {
		return (level.ordinal() <= LoggerLevel.FINEST.ordinal());
	}

	/**
	 * This logs out a FINEST message
	 */
	public void finest(String msg) {
		if (finest()) {
			printLog(LoggerLevel.FINEST, msg);
		}
	}

	/**
	 * This tells whether logger level is set to DEBUG
	 * 
	 * @return true if logger level is set to DEBUG
	 */
	public boolean debug() {
		return (level.ordinal() <= LoggerLevel.DEBUG.ordinal());
	}

	/**
	 * This logs out a DEBUG message
	 */
	public void debug(String msg) {
		if (debug()) {
			printLog(LoggerLevel.DEBUG, msg);
		}
	}

	/**
	 * This tells whether logger level is set to INFO
	 * 
	 * @return true if logger level is set to INFO
	 */
	public boolean info() {
		return (level.ordinal() <= LoggerLevel.INFO.ordinal());
	}

	/**
	 * This logs out an INFO message
	 */
	public void info(String msg) {
		if (info()) {
			printLog(LoggerLevel.INFO, msg);
		}
	}

	/**
	 * This tells whether logger level is set to CONFIG
	 * 
	 * @return true if logger level is set to CONFIG
	 */
	public boolean config() {
		return (level.ordinal() <= LoggerLevel.CONFIG.ordinal());
	}

	/**
	 * This logs out an CONFIG message
	 */
	public void config(String msg) {
		if (config()) {
			printLog(LoggerLevel.CONFIG, msg);
		}
	}

	/**
	 * This tells whether logger level is set to WARN
	 * 
	 * @return true if logger level is set to WARN
	 */
	public boolean warn() {
		return (level.ordinal() <= LoggerLevel.WARN.ordinal());
	}

	/**
	 * This logs out a WARN message
	 */
	public void warn(String msg) {
		if (warn()) {
			printLog(LoggerLevel.WARN, msg);
		}
	}

	/**
	 * This tells whether logger level is set to ERROR
	 * 
	 * @return true if logger level is set to ERROR
	 */
	public boolean error() {
		return (level.ordinal() <= LoggerLevel.ERROR.ordinal());
	}

	/**
	 * This logs out an ERROR message
	 */
	public void error(String msg) {
		if (error()) {
			printLog(LoggerLevel.ERROR, msg);
		}
	}

	/**
	 * This logs out a FATAL message and exits
	 */
	public void fatal(String msg) {
		printLog(LoggerLevel.FATAL, msg);
		System.exit(XWReturnCode.FATAL.ordinal());
	}
}
