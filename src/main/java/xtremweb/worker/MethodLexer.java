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

// $ANTLR 2.7.2: "method.g" -> "MethodLexer.java"$

package xtremweb.worker;

import java.io.InputStream;
import java.io.Reader;
import java.util.Hashtable;

import antlr.ANTLRHashString;
import antlr.ByteBuffer;
import antlr.CharBuffer;
import antlr.CharStreamException;
import antlr.CharStreamIOException;
import antlr.InputBuffer;
import antlr.LexerSharedInputState;
import antlr.NoViableAltForCharException;
import antlr.RecognitionException;
import antlr.Token;
import antlr.TokenStream;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.TokenStreamRecognitionException;
import antlr.collections.impl.BitSet;

public class MethodLexer extends antlr.CharScanner implements MethodParserTokenTypes, TokenStream {
	public MethodLexer(final InputStream in) {
		this(new ByteBuffer(in));
	}

	public MethodLexer(final Reader in) {
		this(new CharBuffer(in));
	}

	public MethodLexer(final InputBuffer ib) {
		this(new LexerSharedInputState(ib));
	}

	public MethodLexer(final LexerSharedInputState state) {
		super(state);
		caseSensitiveLiterals = true;
		setCaseSensitive(true);
		literals = new Hashtable();
		literals.put(new ANTLRHashString("String", this), new Integer(22));
		literals.put(new ANTLRHashString("int", this), new Integer(18));
		literals.put(new ANTLRHashString("true", this), new Integer(25));
		literals.put(new ANTLRHashString("boolean", this), new Integer(14));
		literals.put(new ANTLRHashString("short", this), new Integer(17));
		literals.put(new ANTLRHashString("false", this), new Integer(26));
		literals.put(new ANTLRHashString("jar", this), new Integer(9));
		literals.put(new ANTLRHashString("byte", this), new Integer(16));
		literals.put(new ANTLRHashString("char", this), new Integer(15));
		literals.put(new ANTLRHashString("long", this), new Integer(19));
		literals.put(new ANTLRHashString("double", this), new Integer(21));
		literals.put(new ANTLRHashString("float", this), new Integer(20));
	}

	@Override
	public Token nextToken() throws TokenStreamException {
		Token theRetToken = null;
		tryAgain: for (;;) {
			final Token _token = null;
			int _ttype = Token.INVALID_TYPE;
			resetText();
			try {
				try {
					switch (LA(1)) {
					case '\t':
					case ' ': {
						mSPACE(true);
						theRetToken = _returnToken;
						break;
					}
					case 'A':
					case 'B':
					case 'C':
					case 'D':
					case 'E':
					case 'F':
					case 'G':
					case 'H':
					case 'I':
					case 'J':
					case 'K':
					case 'L':
					case 'M':
					case 'N':
					case 'O':
					case 'P':
					case 'Q':
					case 'R':
					case 'S':
					case 'T':
					case 'U':
					case 'V':
					case 'W':
					case 'X':
					case 'Y':
					case 'Z':
					case '_':
					case 'a':
					case 'b':
					case 'c':
					case 'd':
					case 'e':
					case 'f':
					case 'g':
					case 'h':
					case 'i':
					case 'j':
					case 'k':
					case 'l':
					case 'm':
					case 'n':
					case 'o':
					case 'p':
					case 'q':
					case 'r':
					case 's':
					case 't':
					case 'u':
					case 'v':
					case 'w':
					case 'x':
					case 'y':
					case 'z': {
						mIDENT(true);
						theRetToken = _returnToken;
						break;
					}
					case '\n':
					case '\r': {
						mNEWLINE(true);
						theRetToken = _returnToken;
						break;
					}
					case '0':
					case '1':
					case '2':
					case '3':
					case '4':
					case '5':
					case '6':
					case '7':
					case '8':
					case '9': {
						mNUM(true);
						theRetToken = _returnToken;
						break;
					}
					case '"': {
						mSTRING_LITERAL(true);
						theRetToken = _returnToken;
						break;
					}
					case '.': {
						mDOT(true);
						theRetToken = _returnToken;
						break;
					}
					case ':': {
						mCOLON(true);
						theRetToken = _returnToken;
						break;
					}
					case ',': {
						mCOMMA(true);
						theRetToken = _returnToken;
						break;
					}
					case '(': {
						mLPAREN(true);
						theRetToken = _returnToken;
						break;
					}
					case ')': {
						mRPAREN(true);
						theRetToken = _returnToken;
						break;
					}
					case '{': {
						mLCURL(true);
						theRetToken = _returnToken;
						break;
					}
					case '}': {
						mRCURL(true);
						theRetToken = _returnToken;
						break;
					}
					case '[': {
						mLBRACKET(true);
						theRetToken = _returnToken;
						break;
					}
					case ']': {
						mRBRACKET(true);
						theRetToken = _returnToken;
						break;
					}
					default: {
						if (LA(1) == EOF_CHAR) {
							uponEOF();
							_returnToken = makeToken(Token.EOF_TYPE);
						} else {
							throw new NoViableAltForCharException(LA(1), getFilename(), getLine(), getColumn());
						}
					}
					}
					if (_returnToken == null) {
						continue tryAgain;
					}
					_ttype = _returnToken.getType();
					_ttype = testLiteralsTable(_ttype);
					_returnToken.setType(_ttype);
					return _returnToken;
				} catch (final RecognitionException e) {
					throw new TokenStreamRecognitionException(e);
				}
			} catch (final CharStreamException cse) {
				if (cse instanceof CharStreamIOException) {
					throw new TokenStreamIOException(((CharStreamIOException) cse).io);
				} else {
					throw new TokenStreamException(cse.getMessage());
				}
			}
		}
	}

	public final void mSPACE(final boolean _createToken)
			throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		final int _begin = text.length();
		_ttype = SPACE;
		final int _saveIndex;

		switch (LA(1)) {
		case ' ': {
			match(' ');
			break;
		}
		case '\t': {
			match('\t');
			break;
		}
		default: {
			throw new NoViableAltForCharException(LA(1), getFilename(), getLine(), getColumn());
		}
		}
		if (_createToken && (_token == null) && (_ttype != Token.SKIP)) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mIDENT(final boolean _createToken)
			throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		final int _begin = text.length();
		_ttype = IDENT;
		final int _saveIndex;

		{
			switch (LA(1)) {
			case 'a':
			case 'b':
			case 'c':
			case 'd':
			case 'e':
			case 'f':
			case 'g':
			case 'h':
			case 'i':
			case 'j':
			case 'k':
			case 'l':
			case 'm':
			case 'n':
			case 'o':
			case 'p':
			case 'q':
			case 'r':
			case 's':
			case 't':
			case 'u':
			case 'v':
			case 'w':
			case 'x':
			case 'y':
			case 'z': {
				matchRange('a', 'z');
				break;
			}
			case 'A':
			case 'B':
			case 'C':
			case 'D':
			case 'E':
			case 'F':
			case 'G':
			case 'H':
			case 'I':
			case 'J':
			case 'K':
			case 'L':
			case 'M':
			case 'N':
			case 'O':
			case 'P':
			case 'Q':
			case 'R':
			case 'S':
			case 'T':
			case 'U':
			case 'V':
			case 'W':
			case 'X':
			case 'Y':
			case 'Z': {
				matchRange('A', 'Z');
				break;
			}
			case '_': {
				match('_');
				break;
			}
			default: {
				throw new NoViableAltForCharException(LA(1), getFilename(), getLine(), getColumn());
			}
			}
		}
		{
			_loop49: do {
				switch (LA(1)) {
				case 'a':
				case 'b':
				case 'c':
				case 'd':
				case 'e':
				case 'f':
				case 'g':
				case 'h':
				case 'i':
				case 'j':
				case 'k':
				case 'l':
				case 'm':
				case 'n':
				case 'o':
				case 'p':
				case 'q':
				case 'r':
				case 's':
				case 't':
				case 'u':
				case 'v':
				case 'w':
				case 'x':
				case 'y':
				case 'z': {
					matchRange('a', 'z');
					break;
				}
				case 'A':
				case 'B':
				case 'C':
				case 'D':
				case 'E':
				case 'F':
				case 'G':
				case 'H':
				case 'I':
				case 'J':
				case 'K':
				case 'L':
				case 'M':
				case 'N':
				case 'O':
				case 'P':
				case 'Q':
				case 'R':
				case 'S':
				case 'T':
				case 'U':
				case 'V':
				case 'W':
				case 'X':
				case 'Y':
				case 'Z': {
					matchRange('A', 'Z');
					break;
				}
				case '_': {
					match('_');
					break;
				}
				case '0':
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
				case '8':
				case '9': {
					matchRange('0', '9');
					break;
				}
				default: {
					break _loop49;
				}
				}
			} while (true);
		}
		if (_createToken && (_token == null) && (_ttype != Token.SKIP)) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mNEWLINE(final boolean _createToken)
			throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		final int _begin = text.length();
		_ttype = NEWLINE;
		final int _saveIndex;

		switch (LA(1)) {
		case '\r': {
			match('\r');
			match('\n');
			break;
		}
		case '\n': {
			match('\n');
			break;
		}
		default: {
			throw new NoViableAltForCharException(LA(1), getFilename(), getLine(), getColumn());
		}
		}
		if (_createToken && (_token == null) && (_ttype != Token.SKIP)) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mNUM(final boolean _createToken)
			throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		final int _begin = text.length();
		_ttype = NUM;
		final int _saveIndex;

		{
			int _cnt53 = 0;
			_loop53: do {
				if ((((LA(1) >= '0') && (LA(1) <= '9')))) {
					mDIGIT(false);
				} else {
					if (_cnt53 >= 1) {
						break _loop53;
					} else {
						throw new NoViableAltForCharException(LA(1), getFilename(), getLine(), getColumn());
					}
				}

				_cnt53++;
			} while (true);
		}

		_ttype = INTEGER;

		{
			if ((LA(1) == '.')) {
				match('.');
				{
					int _cnt56 = 0;
					_loop56: do {
						if ((((LA(1) >= '0') && (LA(1) <= '9')))) {
							mDIGIT(false);
						} else {
							if (_cnt56 >= 1) {
								break _loop56;
							} else {
								throw new NoViableAltForCharException(LA(1), getFilename(), getLine(), getColumn());
							}
						}

						_cnt56++;
					} while (true);
				}

				_ttype = DOUBLE;

			} else {
			}

		}
		if (_createToken && (_token == null) && (_ttype != Token.SKIP)) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	protected final void mDIGIT(final boolean _createToken)
			throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		final int _begin = text.length();
		_ttype = DIGIT;
		final int _saveIndex;

		matchRange('0', '9');
		if (_createToken && (_token == null) && (_ttype != Token.SKIP)) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mSTRING_LITERAL(final boolean _createToken)
			throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		final int _begin = text.length();
		_ttype = STRING_LITERAL;
		final int _saveIndex;

		match('"');
		{
			_loop60: do {
				switch (LA(1)) {
				case '\\': {
					mESC(false);
					break;
				}
				case '\t':
				case '\n':
				case '\r':
				case ' ':
				case '\'':
				case '(':
				case ')':
				case ',':
				case '.':
				case '0':
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
				case '6':
				case '7':
				case '8':
				case '9':
				case ':':
				case 'A':
				case 'B':
				case 'C':
				case 'D':
				case 'E':
				case 'F':
				case 'G':
				case 'H':
				case 'I':
				case 'J':
				case 'K':
				case 'L':
				case 'M':
				case 'N':
				case 'O':
				case 'P':
				case 'Q':
				case 'R':
				case 'S':
				case 'T':
				case 'U':
				case 'V':
				case 'W':
				case 'X':
				case 'Y':
				case 'Z':
				case '[':
				case ']':
				case '_':
				case 'a':
				case 'b':
				case 'c':
				case 'd':
				case 'e':
				case 'f':
				case 'g':
				case 'h':
				case 'i':
				case 'j':
				case 'k':
				case 'l':
				case 'm':
				case 'n':
				case 'o':
				case 'p':
				case 'q':
				case 'r':
				case 's':
				case 't':
				case 'u':
				case 'v':
				case 'w':
				case 'x':
				case 'y':
				case 'z':
				case '{':
				case '}': {
					{
						match(_tokenSet_0);
					}
					break;
				}
				default: {
					break _loop60;
				}
				}
			} while (true);
		}
		match('"');
		if (_createToken && (_token == null) && (_ttype != Token.SKIP)) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	protected final void mESC(final boolean _createToken)
			throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		final int _begin = text.length();
		_ttype = ESC;
		final int _saveIndex;

		match('\\');
		{
			switch (LA(1)) {
			case 'n': {
				match('n');
				break;
			}
			case 'r': {
				match('r');
				break;
			}
			case 't': {
				match('t');
				break;
			}
			case 'b': {
				match('b');
				break;
			}
			case 'f': {
				match('f');
				break;
			}
			case '"': {
				match('"');
				break;
			}
			case '\'': {
				match('\'');
				break;
			}
			case '\\': {
				match('\\');
				break;
			}
			default: {
				throw new NoViableAltForCharException(LA(1), getFilename(), getLine(), getColumn());
			}
			}
		}
		if (_createToken && (_token == null) && (_ttype != Token.SKIP)) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mDOT(final boolean _createToken)
			throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		final int _begin = text.length();
		_ttype = DOT;
		final int _saveIndex;

		match('.');
		if (_createToken && (_token == null) && (_ttype != Token.SKIP)) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mCOLON(final boolean _createToken)
			throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		final int _begin = text.length();
		_ttype = COLON;
		final int _saveIndex;

		match(':');
		if (_createToken && (_token == null) && (_ttype != Token.SKIP)) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mCOMMA(final boolean _createToken)
			throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		final int _begin = text.length();
		_ttype = COMMA;
		final int _saveIndex;

		match(',');
		if (_createToken && (_token == null) && (_ttype != Token.SKIP)) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mLPAREN(final boolean _createToken)
			throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		final int _begin = text.length();
		_ttype = LPAREN;
		final int _saveIndex;

		match('(');
		if (_createToken && (_token == null) && (_ttype != Token.SKIP)) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mRPAREN(final boolean _createToken)
			throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		final int _begin = text.length();
		_ttype = RPAREN;
		final int _saveIndex;

		match(')');
		if (_createToken && (_token == null) && (_ttype != Token.SKIP)) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mLCURL(final boolean _createToken)
			throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		final int _begin = text.length();
		_ttype = LCURL;
		final int _saveIndex;

		match('{');
		if (_createToken && (_token == null) && (_ttype != Token.SKIP)) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mRCURL(final boolean _createToken)
			throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		final int _begin = text.length();
		_ttype = RCURL;
		final int _saveIndex;

		match('}');
		if (_createToken && (_token == null) && (_ttype != Token.SKIP)) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mLBRACKET(final boolean _createToken)
			throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		final int _begin = text.length();
		_ttype = LBRACKET;
		final int _saveIndex;

		match('[');
		if (_createToken && (_token == null) && (_ttype != Token.SKIP)) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	public final void mRBRACKET(final boolean _createToken)
			throws RecognitionException, CharStreamException, TokenStreamException {
		int _ttype;
		Token _token = null;
		final int _begin = text.length();
		_ttype = RBRACKET;
		final int _saveIndex;

		match(']');
		if (_createToken && (_token == null) && (_ttype != Token.SKIP)) {
			_token = makeToken(_ttype);
			_token.setText(new String(text.getBuffer(), _begin, text.length() - _begin));
		}
		_returnToken = _token;
	}

	private static final long[] mk_tokenSet_0() {
		final long[] data = { 576271090842609152L, 3458764508183396350L, 0L, 0L };
		return data;
	}

	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());

}
