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
 * @file   : ParamDef.java
 * @author : Oleg Lodygensky (lodygens /at\ lal.in2p3.fr)
 * @date   : May 14th, 2003
 * @since  : v1r2-rc3 (RPC-V)
 */

package xtremweb.worker;

import java.lang.reflect.Array;
import java.util.Vector;

/**
 * This class stores a method param definition; its type, name and value. Param
 * may be a scalar, an array or a matrice.
 */
public class ParamDef {

	private Class type;
	private String name;
	private Object value;
	private boolean debug;

	/**
	 * This is the default constructor.
	 */
	public ParamDef() {
		name = null;
		type = null;
		value = null;
		debug = false;
	}

	/**
	 * This is the a constructor.
	 *
	 * @param b
	 *            set debug level.
	 */
	public ParamDef(final boolean b) {
		this();
		debug = b;
	}

	private void println(final String str) {
		if (debug) {
			System.out.println(str);
		}
	}

	/**
	 * This stores the full param definition in a String; its type, name and
	 * value.
	 *
	 * @return a String containing the param definition. If param is an array or
	 *         a matrice, all elements are written down.
	 */
	@Override
	public String toString() {
		String ret = null;

		if ((type == null) || (name == null) || (value == null)) {
			return new String("null");
		}

		//
		// scalar value
		//
		if (value.getClass().isArray() == false) {
			return value.getClass().getName() + " " + name + " " + value.toString();
		}

		//
		// vector value
		//
		final Object[] vector = (Object[]) value;

		//
		// matrice value : elements are also Array ?
		//
		if (vector[0].getClass().isArray()) {
			// write matrice type
			ret = new String(((Object[]) vector[0])[0].getClass().getName() + "[][] ");
		} else {
			// write vector type
			ret = new String(vector[0].getClass().getName() + "[] ");
		}

		// write vector values
		ret = ret.concat(name + " {");

		for (int i = 0; i < vector.length; i++) {
			if (vector[i].getClass().isArray()) {

				//
				// matrice value : elements are also Array !
				//
				final Object[] matrice = (Object[]) vector[i];

				// write matrice values
				ret = ret.concat("{");
				for (int j = 0; j < matrice.length; j++) {
					ret = ret.concat(matrice[j].toString());
					if (j < (matrice.length - 1)) {
						ret = ret.concat(",");
					}
				}
				ret = ret.concat("}");
			} else {
				//
				// not a matrice but a vector only : elements are not Array but
				// scalar !
				//
				// write vector values
				ret = ret.concat(vector[i].toString());
			}

			if (i < (vector.length - 1)) {
				ret = ret.concat(",");
			}
		}
		ret = ret.concat("}");

		return ret;
	}

	/**
	 * This sets the param type
	 *
	 * @param v
	 *            is a Class object defining the param type
	 */
	public void setType(final Class v) {
		type = v;
	}

	/**
	 * This sets the param name
	 *
	 * @param v
	 *            is a String object defining the param name
	 */
	public void setName(final String v) {
		name = v;
	}

	/**
	 * This sets the param value
	 *
	 * @param v
	 *            is an Object object defining the param value. If param is an
	 *            array, v contains a Vector which is converted as an array is
	 *            the expected type. If param is a matrice, v contains a Vector
	 *            of Vectors which are converted as a bidimentionnal array in
	 *            the expected type.
	 */
	public void setValue(final Object v) {
		value = v;

		if (value.getClass() != java.util.Vector.class) {
			return;
		}

		Vector vector = (Vector) v;
		value = vector.toArray((Object[]) Array.newInstance(vector.firstElement().getClass(), vector.size()));

		final Object[] array = (Object[]) value;

		type = array.getClass();

		if (array[0].getClass() != java.util.Vector.class) {
			return;
		}

		final int[] dimMatrice = { array.length, ((Vector) array[0]).size() };
		final Object[][] matrice = (Object[][]) Array.newInstance(((Vector) array[0]).firstElement().getClass(),
				dimMatrice);

		for (int i = 0; i < array.length; i++) {
			vector = (Vector) array[i];

			matrice[i] = vector.toArray((Object[]) Array.newInstance(vector.firstElement().getClass(), vector.size()));
		}

		value = matrice;
	}

	/**
	 * This gets the param type
	 *
	 * @return a Class containing the param type
	 */
	public Class getType() {
		println("\ngetType()\n");
		if (value.getClass().isArray() == false) {
			return type;
		}

		return getValue().getClass();
	}

	/**
	 * This gets the param name
	 *
	 * @return a String containing the param name
	 */
	public String getName() {
		return name;
	}

	/**
	 * This gets the param value
	 *
	 * @return an Object containing the param value, possibly an array or a
	 *         matrice
	 */
	public Object getValue() {

		println("\ngetValue()\n");
		println("\ttype = " + type.toString());

		if (type.isArray()) {
			final Object[] vector = (Object[]) value;

			println("\tarray");
			println("\t" + vector[0].getClass());
			if (vector[0].getClass() == Integer.class) {
				println("\tvector[0] est un Integer");
			} else {
				println("\ttvector[0] n'est pas un Integer");
			}

			if (vector[0].getClass().isArray()) {

				final Object[] matrice = (Object[]) vector[0];
				println("\tmatrice");
				if (matrice[0].getClass() == Integer.class) {
					println("\tmatrice [0] est un Integer");
				} else {
					println("\tmatrice [0] n'est pas un Integer");
				}

				if (matrice[0].getClass() == Boolean.class) {
					return booleanMatrice(vector);
				}
				if (matrice[0].getClass() == Character.class) {
					return charMatrice(vector);
				}
				if (matrice[0].getClass() == Byte.class) {
					return byteMatrice(vector);
				}
				if (matrice[0].getClass() == Short.class) {
					return shortMatrice(vector);
				}
				if (matrice[0].getClass() == Integer.class) {
					return integerMatrice(vector);
				}
				if (matrice[0].getClass() == Long.class) {
					return longMatrice(vector);
				}
				if (matrice[0].getClass() == Float.class) {
					return floatMatrice(vector);
				}
				if (matrice[0].getClass() == Double.class) {
					return doubleMatrice(vector);
				}
				if (matrice[0].getClass() == String.class) {
					return stringMatrice(vector);
				}
			}

			if (vector[0].getClass() == Boolean.class) {
				return booleanArray(vector);
			}
			if (vector[0].getClass() == Character.class) {
				return charArray(vector);
			}
			if (vector[0].getClass() == Byte.class) {
				return byteArray(vector);
			}
			if (vector[0].getClass() == Short.class) {
				return shortArray(vector);
			}
			if (vector[0].getClass() == Integer.class) {
				return integerArray(vector);
			}
			if (vector[0].getClass() == Long.class) {
				return longArray(vector);
			}
			if (vector[0].getClass() == Float.class) {
				return floatArray(vector);
			}
			if (vector[0].getClass() == Double.class) {
				return doubleArray(vector);
			}
			if (vector[0].getClass() == String.class) {
				return stringArray(vector);
			}
		}

		return value;
	}

	static private boolean[] booleanArray(final Object[] array) {
		final boolean[] ret = new boolean[array.length];
		for (int i = 0; i < array.length; i++) {
			ret[i] = ((Boolean) array[i]).booleanValue();
		}
		return ret;
	}

	static private char[] charArray(final Object[] array) {
		final char[] ret = new char[array.length];
		for (int i = 0; i < array.length; i++) {
			ret[i] = ((Character) array[i]).charValue();
		}
		return ret;
	}

	static private byte[] byteArray(final Object[] array) {
		final byte[] ret = new byte[array.length];
		for (int i = 0; i < array.length; i++) {
			ret[i] = ((Byte) array[i]).byteValue();
		}
		return ret;
	}

	static private short[] shortArray(final Object[] array) {
		final short[] ret = new short[array.length];
		for (int i = 0; i < array.length; i++) {
			ret[i] = ((Short) array[i]).shortValue();
		}
		return ret;
	}

	static private int[] integerArray(final Object[] array) {
		final int[] ret = new int[array.length];
		for (int i = 0; i < array.length; i++) {
			ret[i] = ((Integer) array[i]).intValue();
		}
		return ret;
	}

	static private long[] longArray(final Object[] array) {
		final long[] ret = new long[array.length];
		for (int i = 0; i < array.length; i++) {
			ret[i] = ((Long) array[i]).longValue();
		}
		return ret;
	}

	static private float[] floatArray(final Object[] array) {
		final float[] ret = new float[array.length];
		for (int i = 0; i < array.length; i++) {
			ret[i] = ((Float) array[i]).floatValue();
		}
		return ret;
	}

	static private double[] doubleArray(final Object[] array) {
		final double[] ret = new double[array.length];
		for (int i = 0; i < array.length; i++) {
			ret[i] = ((Double) array[i]).doubleValue();
		}
		return ret;
	}

	static private String[] stringArray(final Object[] array) {
		final String[] ret = new String[array.length];
		for (int i = 0; i < array.length; i++) {
			ret[i] = ((String) array[i]);
		}
		return ret;
	}

	static private boolean[][] booleanMatrice(final Object[] array) {
		final Object[] matrice = ((Object[]) array[0]);
		final boolean[][] ret = new boolean[array.length][matrice.length];

		for (int i = 0; i < array.length; i++) {
			ret[i] = booleanArray((Object[]) array[i]);
		}
		return ret;
	}

	static private char[][] charMatrice(final Object[] array) {
		final Object[] matrice = ((Object[]) array[0]);
		final char[][] ret = new char[array.length][matrice.length];

		for (int i = 0; i < array.length; i++) {
			ret[i] = charArray((Object[]) array[i]);
		}
		return ret;
	}

	static private byte[][] byteMatrice(final Object[] array) {
		final Object[] matrice = ((Object[]) array[0]);
		final byte[][] ret = new byte[array.length][matrice.length];

		for (int i = 0; i < array.length; i++) {
			ret[i] = byteArray((Object[]) array[i]);
		}
		return ret;
	}

	static private short[][] shortMatrice(final Object[] array) {
		final Object[] matrice = ((Object[]) array[0]);
		final short[][] ret = new short[array.length][matrice.length];

		for (int i = 0; i < array.length; i++) {
			ret[i] = shortArray((Object[]) array[i]);
		}
		return ret;
	}

	static private int[][] integerMatrice(final Object[] array) {
		final Object[] matrice = ((Object[]) array[0]);
		final int[][] ret = new int[array.length][matrice.length];

		for (int i = 0; i < array.length; i++) {
			ret[i] = integerArray((Object[]) array[i]);
		}
		return ret;
	}

	static private long[][] longMatrice(final Object[] array) {
		final Object[] matrice = ((Object[]) array[0]);
		final long[][] ret = new long[array.length][matrice.length];

		for (int i = 0; i < array.length; i++) {
			ret[i] = longArray((Object[]) array[i]);
		}
		return ret;
	}

	static private float[][] floatMatrice(final Object[] array) {
		final Object[] matrice = ((Object[]) array[0]);
		final float[][] ret = new float[array.length][matrice.length];

		for (int i = 0; i < array.length; i++) {
			ret[i] = floatArray((Object[]) array[i]);
		}
		return ret;
	}

	static private double[][] doubleMatrice(final Object[] array) {
		final Object[] matrice = ((Object[]) array[0]);
		final double[][] ret = new double[array.length][matrice.length];

		for (int i = 0; i < array.length; i++) {
			ret[i] = doubleArray((Object[]) array[i]);
		}
		return ret;
	}

	static private String[][] stringMatrice(final Object[] array) {
		final Object[] matrice = ((Object[]) array[0]);
		final String[][] ret = new String[array.length][matrice.length];

		for (int i = 0; i < array.length; i++) {
			ret[i] = stringArray((Object[]) array[i]);
		}
		return ret;
	}

}
