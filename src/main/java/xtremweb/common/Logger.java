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
	public void setLoggerLevel(final LoggerLevel l) {
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
		final String levelstr = System.getProperty(XWPropertyDefs.LOGGERLEVEL.toString());
		if (levelstr != null) {
			level = LoggerLevel.valueOf(levelstr.toUpperCase());
		}
	}

	/**
	 * This calls this() and sets the name to the parameter class name
	 */
	public Logger(final Object o) {
		this();
		name = o.getClass().getName();
	}

	/**
	 * This calls this() and sets the logging level to the provided one
	 */
	public Logger(final LoggerLevel l) {
		this();
		level = l;
	}

	/**
	 * This calls this() and sets the name and the logging level to the provided
	 * ones
	 */
	public Logger(final LoggerLevel l, final Object o) {
		this(o);
		level = l;
	}

	/**
	 * This helps to format date : the format is "yyyy-MM-dd HH:mm:ss"
	 */
	private static final SimpleDateFormat LOG_DATE_FORMAT = new SimpleDateFormat("[dd/MMM/yyyy:HH:mm:ss Z]", Locale.US);

	/**
	 * This logs out a message
	 */
	public void printLog(final LoggerLevel levelmsg, final String msg) {
		final String n = (name != null ? name + "_" : "") + Thread.currentThread().getName() + "_"
				+ Thread.currentThread().getId();
		System.out.println(LOG_DATE_FORMAT.format(new Date()) + " [" + n + "] " + levelmsg + " : " + msg);
	}

	/**
	 * This logs out an exception
	 */
	public void exception(final Exception e) {
		final String str = e.toString();
		final Throwable cause = e.getCause();
		warn(str + " (" + (cause == null ? e.getMessage() : cause.toString()) + ")");
		if (debug()) {
			e.printStackTrace();
		}
	}

	/**
	 * This logs out an exception
	 */
	public void exception(final String msg, final Exception e) {
		error(msg);
		exception(e);
	}

	/**
	 * This tells whether logger level is set to FINEST
	 *
	 * @return true if logger level is set to FINEST
	 */
	public boolean finest() {
		return level.ordinal() <= LoggerLevel.FINEST.ordinal();
	}

	/**
	 * This logs out a FINEST message
	 */
	public void finest(final String msg) {
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
		return level.ordinal() <= LoggerLevel.DEBUG.ordinal();
	}

	/**
	 * This logs out a DEBUG message
	 */
	public void debug(final String msg) {
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
		return level.ordinal() <= LoggerLevel.INFO.ordinal();
	}

	/**
	 * This logs out an INFO message
	 */
	public void info(final String msg) {
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
		return level.ordinal() <= LoggerLevel.CONFIG.ordinal();
	}

	/**
	 * This logs out an CONFIG message
	 */
	public void config(final String msg) {
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
		return level.ordinal() <= LoggerLevel.WARN.ordinal();
	}

	/**
	 * This logs out a WARN message
	 */
	public void warn(final String msg) {
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
		return level.ordinal() <= LoggerLevel.ERROR.ordinal();
	}

	/**
	 * This logs out an ERROR message
	 */
	public void error(final String msg) {
		if (error()) {
			printLog(LoggerLevel.ERROR, msg);
		}
	}

	/**
	 * This logs out a FATAL message and exits
	 */
	public void fatal(final String msg) {
		printLog(LoggerLevel.FATAL, msg);
		System.exit(XWReturnCode.FATAL.ordinal());
	}
}
