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

/**
 * @file   : CallDef.java
 * @author : Oleg Lodygensky (lodygens /at\ lal.in2p3.fr)
 * @date   : May 14th, 2003
 * @since  : v1r2-rc3 (RPC-V)
 *
 */

package xtremweb.worker;

import java.util.Vector;

/**
 * This encapsulates a java method call definition; the jar file , class, method
 * names as well as the method params
 *
 * @see ParamDef
 */
public class CallDef {

	private String jarFileName;
	private String className;
	private String methodName;
	private Vector params;
	private boolean debug;

	public CallDef() {
		jarFileName = null;
		className = null;
		methodName = null;
		params = null;
		debug = false;
	}

	public CallDef(final boolean b) {
		this();
		debug = b;
	}

	private void println(final String str) {
		if (debug) {
			System.out.println(str);
		}
	}

	@Override
	public String toString() {
		String ret = new String();

		if (jarFileName != null) {
			ret = ret.concat(jarFileName + " ");
		}
		if (className != null) {
			ret = ret.concat(className + " ");
		}
		if (methodName != null) {
			ret = ret.concat(methodName + " (");
		}

		if (params != null) {
			for (int i = 0; i < params.size(); i++) {

				final ParamDef p = (ParamDef) params.elementAt(i);

				ret = ret.concat(p.toString());
				if (i < (params.size() - 1)) {
					ret = ret.concat(",");
				}
			}
		}

		ret = ret.concat(")");
		return ret;
	}

	public void setJarFileName(final String v) {
		jarFileName = v;
		System.err.println("\n\n\nset jar file = " + jarFileName + "\n\n\n");
		if (jarFileName == null) {
			return;
		}

		if (jarFileName.toLowerCase().indexOf(".jar") == -1) {
			jarFileName = jarFileName.concat(".jar");
		}
	}

	public void setClassName(final String v) {
		className = v;
	}

	public void setMethodName(final String v) {
		methodName = v;
	}

	public void setParams(final Vector v) {
		params = v;
	}

	public String getJarFileName() {
		return jarFileName;
	}

	public String getClassName() {
		return className;
	}

	public String getMethodName() {
		return methodName;
	}

	public Vector getParams() {
		return params;
	}

	public Class[] getParamTypes() {

		Class[] types;

		if (params == null) {
			return null;
		}

		types = new Class[params.size()];

		for (int i = 0; i < params.size(); i++) {
			final ParamDef p = (ParamDef) params.elementAt(i);
			types[i] = p.getType();
			println("type[" + i + "] = " + types[i].toString());
		}

		return types;
	}

	public Object[] getParamValues() {

		Object[] values;

		if (params == null) {
			return null;
		}

		values = new Object[params.size()];

		for (int i = 0; i < params.size(); i++) {
			final ParamDef p = (ParamDef) params.elementAt(i);
			values[i] = p.getValue();
			println("value[" + i + "] = " + values[i].toString());
		}

		return values;
	}

}
