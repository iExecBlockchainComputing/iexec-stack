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

import java.awt.FontMetrics;
import java.util.StringTokenizer;

import javax.swing.JTextArea;
import javax.swing.LookAndFeel;

/**
 * This class uses a <tt>JTextArea</tt> to simulate a <tt>JLabel</tt> that
 * allows multiple-line labels. It does this by using JLabel's values for
 * border, font, etc.
 */
public final class MultiLineLabel extends JTextArea {

	/**
	 * The default pixel width for labels when the width is not specified in the
	 * constructor.
	 */
	private final static int DEFAULT_LABEL_WIDTH = 200;

	/**
	 * Creates a label that can have multiple lines and that has the default
	 * width.
	 * 
	 * @param s
	 *            the <tt>String</tt> to display in the label
	 */
	public MultiLineLabel(String s) {
		setText(s);
	}

	/**
	 * Creates a label with new lines inserted after the specified number of
	 * pixels have been filled on each line.
	 * 
	 * @param s
	 *            the <tt>String</tt> to display in the label
	 * @param pixels
	 *            the pixel limit for each line.
	 */
	public MultiLineLabel(String s, int pixels) {
		setText(s, pixels);
	}

	/**
	 * Creates a label that can have multiple lines and that sets the number of
	 * rows and columns for the JTextArea.
	 * 
	 * @param s
	 *            the <tt>String</tt> to display in the label
	 * @param pixels
	 *            the pixel limit for each line.
	 * @param rows
	 *            the number of rows to include in the label
	 * @param cols
	 *            the number of columns to include in the label
	 */
	public MultiLineLabel(String s, int pixels, int rows, int cols) {
		super(rows, cols);
		setText(s, pixels);
	}

	/**
	 * Change the text before passing it up to the super setText.
	 * 
	 * @param s
	 *            the <tt>String</tt> to display in the label
	 * @param pixels
	 *            the pixel limit for each line.
	 */
	public void setText(String s, int pixels) {
		super.setText(createSizedString(s, pixels));
	}

	/**
	 * Change the text before passing it up to the super setText.
	 * 
	 * @param s
	 *            the <tt>String</tt> to display in the label
	 */
	@Override
	public void setText(String s) {
		super.setText(createSizedString(s, DEFAULT_LABEL_WIDTH));
	}

	/**
	 * Tells the look and feel to reset some of the values for this component so
	 * that it doesn't use JTextArea's default values.
	 * 
	 * DO NOT CALL THIS METHOD YOURSELF!
	 */
	@Override
	public void updateUI() {
		super.updateUI();
		setWrapStyleWord(true);
		setHighlighter(null);
		setEditable(false);
		LookAndFeel.installBorder(this, "Label.border");
		LookAndFeel.installColorsAndFont(this, "Label.background",
				"Label.foreground", "Label.font");
	}

	/**
	 * Convert the input string to a string with newlines at the closest word to
	 * the number of pixels specified in the 'pixels' parameter.
	 * 
	 * @param message
	 *            the <tt>String</tt> to display in the label
	 * @param pixels
	 *            the pixel width on each line before inserting a new line
	 *            character.
	 */
	private String createSizedString(String message, int pixels) {
		String nstr = new String();

		if (message == null) {
			return null;
		}

		final FontMetrics fm = getFontMetrics(getFont());
		final StringTokenizer st = new StringTokenizer(message);
		String word;
		String curLine = new String();

		while (st.hasMoreTokens()) {
			word = st.nextToken();
			if (fm.stringWidth(curLine + word) > pixels) {
				nstr += curLine + "\n";
				curLine = word + " ";
			} else {
				curLine += word + " ";
			}
		}
		if (curLine != null) {
			nstr += curLine;
		}

		return (nstr);
	}
}
