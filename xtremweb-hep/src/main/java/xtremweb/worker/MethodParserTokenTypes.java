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

public interface MethodParserTokenTypes {
	int EOF = 1;
	int NULL_TREE_LOOKAHEAD = 3;
	int COLON = 4;
	int LPAREN = 5;
	int RPAREN = 6;
	int IDENT = 7;
	int DOT = 8;
	int LITERAL_jar = 9;
	int SPACE = 10;
	int COMMA = 11;
	int LBRACKET = 12;
	int RBRACKET = 13;
	int LITERAL_boolean = 14;
	int LITERAL_char = 15;
	int LITERAL_byte = 16;
	int LITERAL_short = 17;
	int LITERAL_int = 18;
	int LITERAL_long = 19;
	int LITERAL_float = 20;
	int LITERAL_double = 21;
	int LITERAL_String = 22;
	int LCURL = 23;
	int RCURL = 24;
	int LITERAL_true = 25;
	int LITERAL_false = 26;
	int DOUBLE = 27;
	int INTEGER = 28;
	int STRING_LITERAL = 29;
	int NEWLINE = 30;
	int NUM = 31;
	int ESC = 32;
	int DIGIT = 33;
}
