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

// $ANTLR 2.7.2: "method.g" -> "MethodParser.java"$

package xtremweb.worker;

import java.text.ParseException;
import java.util.Vector;

import antlr.NoViableAltException;
import antlr.ParserSharedInputState;
import antlr.RecognitionException;
import antlr.Token;
import antlr.TokenBuffer;
import antlr.TokenStream;
import antlr.TokenStreamException;
import antlr.collections.impl.BitSet;

/**
 *
 * (Don't move those comments on very first lines since header part must start
 * on line 1)
 *
 * Created : May 14th, 2003<br />
 *
 * @author : Oleg Lodygensky (lodygens@lal.in2p3.fr)
 *
 *         Purpose : ANTLR grammar definition to decode java procedure calls
 *         Grammar : aJar.jar:aPackage.aClass.aMethod(paramsList) where -
 *         aJar.jar is optionnal but if present must be followed by ':' -
 *         aPackage is optionnal (may be formatted as pkg.subpkg. etc) - aClass
 *         is required (inner class not allowed) - aMethod is required with its
 *         open and close parenthesis - paramList is optionnal and depends on
 *         the method definition - paramList is formatted as param0[, param1,
 *         ... paramN] - paramN is formatted as paramType paramName paramValue -
 *         paramType is scalarType or vectorType or matriceType - scalarType is
 *         one of the following: - boolean - char - byte - short - int - long -
 *         float - double - String - vectorType is scalarType[] - matriceType is
 *         vectorType[] (i.e scalarType[][]) - paramName must be a valid java
 *         identifier - paramValue must be a valid java value (depending of
 *         paramType)
 *
 *         Note : I don't really understand where (SPACE)* is necessary But
 *         don't remove those found in this file or it won't work :(
 *
 */
public class MethodParser extends antlr.LLkParser implements MethodParserTokenTypes {

	protected MethodParser(final TokenBuffer tokenBuf, final int k) {
		super(tokenBuf, k);
		tokenNames = _tokenNames;
	}

	public MethodParser(final TokenBuffer tokenBuf) {
		this(tokenBuf, 3);
	}

	protected MethodParser(final TokenStream lexer, final int k) {
		super(lexer, k);
		tokenNames = _tokenNames;
	}

	public MethodParser(final TokenStream lexer) {
		this(lexer, 3);
	}

	public MethodParser(final ParserSharedInputState state) {
		super(state, 3);
		tokenNames = _tokenNames;
	}

	/**
	 * This is a test method
	 */
	public final void startRule() throws RecognitionException, TokenStreamException {

		CallDef n;

		try {
			n = methodCall();
			System.out.println("call def found = " + n.toString());
		} catch (final RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_0);
		}
	}

	/**
	 * This parses a method call definition
	 *
	 * @return a CallDef object containing the method call
	 */
	public final CallDef methodCall() throws RecognitionException, TokenStreamException {
		CallDef calldef;

		calldef = new CallDef();
		String jarfilename = null;
		String[] methodnames = null;
		Vector params = null;

		try {
			{
				if ((LA(1) == IDENT) && (LA(2) == DOT) && (LA(3) == LITERAL_jar)) {
					jarfilename = jarFileName();
					match(COLON);
				} else if ((LA(1) == IDENT) && (LA(2) == DOT) && (LA(3) == IDENT)) {
				} else {
					throw new NoViableAltException(LT(1), getFilename());
				}

			}
			methodnames = methodName();
			match(LPAREN);
			params = paramsList();
			match(RPAREN);

			calldef.setJarFileName(jarfilename);

			if (methodnames[0] != null) {
				calldef.setClassName(methodnames[0]);
			}
			if (methodnames[1] != null) {
				calldef.setMethodName(methodnames[1]);
			}

			calldef.setParams(params);

		} catch (final RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_0);
		}
		return calldef;
	}

	/**
	 * This parses the jar file name
	 *
	 * @return a string containing the jar file name
	 */
	private final String jarFileName() throws RecognitionException, TokenStreamException {
		String jarfilename;

		Token jarname = null;

		jarfilename = null;

		try {
			jarname = LT(1);
			match(IDENT);
			dotJar();

			jarfilename = jarname.getText();

		} catch (final RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_1);
		}
		return jarfilename;
	}

	/**
	 * This parses the method name
	 *
	 * @return a string containing the method name
	 */
	private final String[] methodName() throws RecognitionException, TokenStreamException {
		String[] names;

		Token methodname = null;

		names = new String[2];
		names[0] = null;
		names[1] = null;
		String classname = null;

		try {
			classname = className();
			match(DOT);
			methodname = LT(1);
			match(IDENT);

			names[0] = classname;
			names[1] = methodname.getText();

		} catch (final RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_2);
		}
		return names;
	}

	/**
	 * This parses a method parameter list; it calls param as many time as
	 * needed
	 *
	 * @return a Vector containing the method parameters
	 */
	private final Vector paramsList() throws RecognitionException, TokenStreamException {
		Vector params;

		params = new Vector();
		ParamDef p1 = null;
		ParamDef p2 = null;

		try {
			{
				switch (LA(1)) {
				case LITERAL_boolean:
				case LITERAL_char:
				case LITERAL_byte:
				case LITERAL_short:
				case LITERAL_int:
				case LITERAL_long:
				case LITERAL_float:
				case LITERAL_double:
				case LITERAL_String: {
					p1 = param();

					params.add(p1);

					{
						_loop22: do {
							if (((LA(1) == SPACE) || (LA(1) == COMMA))) {
								{
									_loop19: do {
										if ((LA(1) == SPACE)) {
											match(SPACE);
										} else {
											break _loop19;
										}

									} while (true);
								}
								match(COMMA);
								{
									_loop21: do {
										if ((LA(1) == SPACE)) {
											match(SPACE);
										} else {
											break _loop21;
										}

									} while (true);
								}
								p2 = param();

								params.add(p2);

							} else {
								break _loop22;
							}

						} while (true);
					}
					break;
				}
				case RPAREN: {
					break;
				}
				default: {
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
			}
		} catch (final RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_3);
		}
		return params;
	}

	/**
	 * This parses the class name
	 *
	 * @return a string containing the class name
	 */
	private final String className() throws RecognitionException, TokenStreamException {
		String classname;

		Token n1 = null;
		Token n2 = null;

		classname = null;

		try {
			n1 = LT(1);
			match(IDENT);

			if (n1.getText() == null) {
				return classname;
			}

			if (classname == null) {
				classname = n1.getText();
			} else {
				classname = classname.concat("." + n1.getText());
			}

			{
				_loop6: do {
					if ((LA(1) == DOT) && (LA(2) == IDENT) && (LA(3) == DOT)) {
						match(DOT);
						n2 = LT(1);
						match(IDENT);

						if (n2.getText() == null) {
							return classname;
						}

						if (classname == null) {
							classname = n2.getText();
						} else {
							classname = classname.concat("." + n2.getText());
						}

					} else {
						break _loop6;
					}

				} while (true);
			}
		} catch (final RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_4);
		}
		return classname;
	}

	/**
	 * This parses the jar file extension ".jar"
	 */
	private final void dotJar() throws RecognitionException, TokenStreamException {

		try {
			match(DOT);
			match(LITERAL_jar);
		} catch (final RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_1);
		}
	}

	/**
	 * This parses a method parameter; its type, name and value
	 *
	 * @return a ParamDef object containing the parameter
	 */
	private final ParamDef param() throws RecognitionException, TokenStreamException {
		ParamDef param;

		Token n = null;

		param = new ParamDef();
		Class t = null;
		Object v = null;

		try {
			t = type();
			{
				_loop12: do {
					if ((LA(1) == SPACE)) {
						match(SPACE);
					} else {
						break _loop12;
					}

				} while (true);
			}
			n = LT(1);
			match(IDENT);
			{
				_loop14: do {
					if ((LA(1) == SPACE)) {
						match(SPACE);
					} else {
						break _loop14;
					}

				} while (true);
			}
			v = value(t);

			param.setType(t);
			param.setName(n.getText());
			param.setValue(v);

		} catch (final RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_5);
		}
		return param;
	}

	/**
	 * This parses a method parameter type which can be a scalar, vector or
	 * matrice
	 *
	 * @return a Class containing a method parameters type
	 */
	private final Class type() throws RecognitionException, TokenStreamException {
		Class t;

		t = null;

		try {
			t = scalarType();
			{
				switch (LA(1)) {
				case LBRACKET: {
					match(LBRACKET);
					match(RBRACKET);
					{
						switch (LA(1)) {
						case LBRACKET: {
							match(LBRACKET);
							match(RBRACKET);
							break;
						}
						case IDENT:
						case SPACE: {
							break;
						}
						default: {
							throw new NoViableAltException(LT(1), getFilename());
						}
						}
					}
					break;
				}
				case IDENT:
				case SPACE: {
					break;
				}
				default: {
					throw new NoViableAltException(LT(1), getFilename());
				}
				}
			}
		} catch (final RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_6);
		}
		return t;
	}

	/**
	 * This parses parameter value, which can be a scalar, vector or matrice
	 *
	 * @param type
	 *            is the parameter type as read
	 * @return an object containing the value in the expected type
	 */
	private final Object value(final Class type) throws RecognitionException, TokenStreamException {
		Object v;

		v = null;

		try {
			if ((((LA(1) >= LITERAL_true) && (LA(1) <= STRING_LITERAL)))) {
				v = scalarValue(type);

			} else if ((LA(1) == LCURL) && (((LA(2) >= LITERAL_true) && (LA(2) <= STRING_LITERAL)))) {
				v = vectorValues(type);

			} else if ((LA(1) == LCURL) && (LA(2) == LCURL)) {
				v = matriceValues(type);

			} else {
				throw new NoViableAltException(LT(1), getFilename());
			}

		} catch (final ParseException e) {
			System.out.println(e.toString());
		}
		return v;
	}

	/**
	 * This parses a scalar method parameter type (int, boolean etc. or String)
	 *
	 * @return a Class containing a method parameters type
	 */
	private final Class scalarType() throws RecognitionException, TokenStreamException {
		Class t;

		t = null;

		try {
			switch (LA(1)) {
			case LITERAL_boolean:
			case LITERAL_char:
			case LITERAL_byte:
			case LITERAL_short:
			case LITERAL_int:
			case LITERAL_long:
			case LITERAL_float:
			case LITERAL_double: {
				t = basicType();
				break;
			}
			case LITERAL_String: {
				t = stringType();
				break;
			}
			default: {
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		} catch (final RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_7);
		}
		return t;
	}

	/**
	 * This parses a scalar method parameter type (int, boolean...)
	 *
	 * @return a Class containing a method parameters type
	 */
	private final Class basicType() throws RecognitionException, TokenStreamException {
		Class t;

		t = null;

		try {
			switch (LA(1)) {
			case LITERAL_boolean: {
				match(LITERAL_boolean);
				t = Boolean.TYPE;
				break;
			}
			case LITERAL_char: {
				match(LITERAL_char);
				t = Character.TYPE;
				break;
			}
			case LITERAL_byte: {
				match(LITERAL_byte);
				t = Byte.TYPE;
				break;
			}
			case LITERAL_short: {
				match(LITERAL_short);
				t = Short.TYPE;
				break;
			}
			case LITERAL_int: {
				match(LITERAL_int);
				t = Integer.TYPE;
				break;
			}
			case LITERAL_long: {
				match(LITERAL_long);
				t = Long.TYPE;
				break;
			}
			case LITERAL_float: {
				match(LITERAL_float);
				t = Float.TYPE;
				break;
			}
			case LITERAL_double: {
				match(LITERAL_double);
				t = Double.TYPE;
				break;
			}
			default: {
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		} catch (final RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_7);
		}
		return t;
	}

	/**
	 * This parses a scalar method parameter String type
	 *
	 * @return a Class containing a method parameters type
	 */
	private final Class stringType() throws RecognitionException, TokenStreamException {
		Class t;

		t = null;

		try {
			match(LITERAL_String);
			t = String.class;
		} catch (final RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_7);
		}
		return t;
	}

	/**
	 * This parses parameter value
	 *
	 * @param type
	 *            is the parameter type as read
	 * @return an object containing the value in the expected type
	 * @exception ParseException
	 *                is thrown if value is not in the expected type
	 *
	 */
	private final Object scalarValue(final Class type)
			throws RecognitionException, TokenStreamException, ParseException {
		Object s;

		Token valD = null;
		Token valI = null;
		Token valString = null;

		s = null;

		try {
			switch (LA(1)) {
			case LITERAL_true: {
				match(LITERAL_true);

				if (type != Boolean.TYPE) {
					throw new ParseException("boolean value not expected", 0);
				}
				s = new Boolean(true);

				break;
			}
			case LITERAL_false: {
				match(LITERAL_false);

				if (type != Boolean.TYPE) {
					throw new ParseException("boolean value not expected", 0);
				}
				s = new Boolean(false);

				break;
			}
			case DOUBLE: {
				valD = LT(1);
				match(DOUBLE);

				if (type == Double.TYPE) {
					s = new Double(Double.parseDouble(valD.getText()));
				} else if (type == Float.TYPE) {
					s = new Float(Float.parseFloat(valD.getText()));
				} else {
					throw new ParseException("double/float value not expected", 0);
				}

				break;
			}
			case INTEGER: {
				valI = LT(1);
				match(INTEGER);

				if (type != Integer.TYPE) {
					throw new ParseException("integer value not expected", 0);
				}
				s = new Integer(Integer.parseInt(valI.getText()));

				break;
			}
			case STRING_LITERAL: {
				valString = LT(1);
				match(STRING_LITERAL);

				if (type != String.class) {
					throw new ParseException("string value not expected", 0);
				}
				final String val = valString.getText();
				s = new String(val.substring(1, val.length() - 1));

				break;
			}
			default: {
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
		} catch (final RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_8);
		}
		return s;
	}

	/**
	 * This parses a matrice parameter value. It catches ParseException that can
	 * be thrown by scalarValue
	 *
	 * @param type
	 *            is the parameter type as read
	 * @return an object containing the value in the expected type
	 */
	private final Object vectorValues(final Class type) throws RecognitionException, TokenStreamException {
		Object v;

		v = new Vector();
		Object v1 = null;
		Object v2 = null;

		try {
			match(LCURL);
			v1 = scalarValue(type);

			((Vector) v).add(v1);

			{
				_loop43: do {
					if (((LA(1) == SPACE) || (LA(1) == COMMA))) {
						{
							_loop40: do {
								if ((LA(1) == SPACE)) {
									match(SPACE);
								} else {
									break _loop40;
								}

							} while (true);
						}
						match(COMMA);
						{
							_loop42: do {
								if ((LA(1) == SPACE)) {
									match(SPACE);
								} else {
									break _loop42;
								}

							} while (true);
						}
						v2 = scalarValue(type);

						((Vector) v).add(v2);

					} else {
						break _loop43;
					}

				} while (true);
			}
			match(RCURL);
		} catch (final ParseException e) {
			System.out.println(e.toString());
		}
		return v;
	}

	/**
	 * This parses a matrice parameter value
	 *
	 * @param type
	 *            is the parameter type as read
	 * @return an object containing the value in the expected type
	 */
	private final Object matriceValues(final Class type) throws RecognitionException, TokenStreamException {
		Object m;

		m = new Vector();
		Object m1 = null;
		Object m2 = null;

		try {
			match(LCURL);
			m1 = vectorValues(type);

			((Vector) m).add(m1);

			{
				_loop36: do {
					if (((LA(1) == SPACE) || (LA(1) == COMMA))) {
						{
							_loop33: do {
								if ((LA(1) == SPACE)) {
									match(SPACE);
								} else {
									break _loop33;
								}

							} while (true);
						}
						match(COMMA);
						{
							_loop35: do {
								if ((LA(1) == SPACE)) {
									match(SPACE);
								} else {
									break _loop35;
								}

							} while (true);
						}
						m2 = vectorValues(type);

						((Vector) m).add(m2);

					} else {
						break _loop36;
					}

				} while (true);
			}
			match(RCURL);
		} catch (final RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_5);
		}
		return m;
	}

	public static final String[] _tokenNames = { "<0>", "EOF", "<2>", "NULL_TREE_LOOKAHEAD", "COLON", "(", ")", "IDENT",
			"DOT", "\"jar\"", "SPACE", "COMMA", "[", "]", "\"boolean\"", "\"char\"", "\"byte\"", "\"short\"", "\"int\"",
			"\"long\"", "\"float\"", "\"double\"", "\"String\"", "{", "}", "\"true\"", "\"false\"", "DOUBLE", "INTEGER",
			"STRING_LITERAL", "NEWLINE", "NUM", "ESC", "DIGIT" };

	private static final long[] mk_tokenSet_0() {
		final long[] data = { 2L, 0L };
		return data;
	}

	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());

	private static final long[] mk_tokenSet_1() {
		final long[] data = { 16L, 0L };
		return data;
	}

	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());

	private static final long[] mk_tokenSet_2() {
		final long[] data = { 32L, 0L };
		return data;
	}

	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());

	private static final long[] mk_tokenSet_3() {
		final long[] data = { 64L, 0L };
		return data;
	}

	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());

	private static final long[] mk_tokenSet_4() {
		final long[] data = { 256L, 0L };
		return data;
	}

	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());

	private static final long[] mk_tokenSet_5() {
		final long[] data = { 3136L, 0L };
		return data;
	}

	public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());

	private static final long[] mk_tokenSet_6() {
		final long[] data = { 1152L, 0L };
		return data;
	}

	public static final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());

	private static final long[] mk_tokenSet_7() {
		final long[] data = { 5248L, 0L };
		return data;
	}

	public static final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());

	private static final long[] mk_tokenSet_8() {
		final long[] data = { 16780352L, 0L };
		return data;
	}

	public static final BitSet _tokenSet_8 = new BitSet(mk_tokenSet_8());

}
