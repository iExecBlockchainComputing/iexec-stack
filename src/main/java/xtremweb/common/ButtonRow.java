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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * This class generates a row of buttons with a standard spacing between them.
 * The row of buttons can be oriented either horizontally or vertically,
 * depending on the parameter.
 */
// 2345678|012345678|012345678|012345678|012345678|012345678|012345678|012345678|
public final class ButtonRow extends JPanel {

	/**
	 * The array of <tt>JButton</tt>s.
	 */
	private final JButton[] buttons;

	/**
	 * The number of pixels separating buttons.
	 */
	private static final int BUTTONSEP = 6;

	/**
	 * Specifies that the buttons should be aligned along the x axis.
	 */
	public static final int X_AXIS = BoxLayout.X_AXIS;

	/**
	 * Specifies that the buttons should be aligned along the y axis.
	 */
	public static final int Y_AXIS = BoxLayout.Y_AXIS;

	/**
	 * This will create a "glue" at the top of the button panel, pushing the
	 * buttons to the bottom.
	 */
	public static final int TOP_GLUE = 10;

	/**
	 * This will create a "glue" at the bottom of the button panel, pushing the
	 * buttons to the top.
	 */
	public static final int BOTTOM_GLUE = 11;

	/**
	 * This will create a "glue" at the left of the button panel, pushing the
	 * buttons to the left.
	 */
	public static final int LEFT_GLUE = 12;

	/**
	 * This will create a "glue" at the right of the button panel, pushing the
	 * buttons to the right.
	 */
	public static final int RIGHT_GLUE = 13;

	/**
	 * This will give the button panel no glue, leaving the buttons in the
	 * middle.
	 */
	public static final int NO_GLUE = 14;

	/**
	 * Creates a row of buttons with standard separation between each button and
	 * with the specified tooltips and listeners. This constructor uses the
	 * default <tt>X_AXIS</tt> orientation and the default <tt>NO_GLUE</tt>.
	 *
	 * @param labelKeys
	 *            the array of keys for looking up the locale-specific labels to
	 *            use for the buttons
	 *
	 * @param toolTipKeys
	 *            the array of keys for looking up the locale-specific tooltips
	 *            to use for the buttons
	 *
	 * @param listeners
	 *            the array of <tt>ActionListeners</tt> to use for the buttons
	 */
	public ButtonRow(final String[] labelKeys, final String[] toolTipKeys, final ActionListener[] listeners) {
		this(labelKeys, toolTipKeys, listeners, X_AXIS, NO_GLUE);
	}

	/**
	 * Creates a row of buttons with standard separation between each button,
	 * aligned either vertically or horizontally, with or without glue.
	 *
	 * @param labelKeys
	 *            the array of keys for looking up the locale-specific labels to
	 *            use for the buttons
	 *
	 * @param toolTipKeys
	 *            the array of keys for looking up the locale-specific tooltips
	 *            to use for the buttons
	 *
	 * @param listeners
	 *            the array of <tt>ActionListeners</tt> to use for the buttons
	 *
	 * @param orientation
	 *            the orientation to use for the row of buttons, either
	 *            ButtonRow.X_AXIS or ButtonRow.Y_AXIS
	 *
	 * @param glue
	 *            the glue determining the placement of the buttons, either
	 *            TOP_GLUE, BOTTOM_GLUE, LEFT_GLUE, RIGHT_GLUE, or NO_GLUE
	 */
	public ButtonRow(final String[] labelKeys, final String[] toolTipKeys, final ActionListener[] listeners,
			final int orientation, final int glue) {
		final BoxLayout bl = new BoxLayout(this, orientation);
		setLayout(bl);
		final int length = labelKeys.length;
		final int sepLength = length - 1;
		buttons = new JButton[length];
		final Component[] separators = new Component[sepLength];
		int i = 0;
		while (i < length) {
			final String label = labelKeys[i];
			buttons[i] = new JButton(label);
			if (toolTipKeys[i] != null) {
				final String tip = toolTipKeys[i];
				buttons[i].setToolTipText(tip);
			}
			i++;
		}
		setListeners(listeners);
		i = 0;
		Dimension d;
		if (orientation == BoxLayout.X_AXIS) {
			d = new Dimension(BUTTONSEP, 0);
			while (i < (sepLength)) {
				separators[i] = Box.createRigidArea(d);
				i++;
			}
		}

		// otherwise the orientation should be BoxLayout.Y_AXIS
		else {
			d = new Dimension(0, BUTTONSEP);
			while (i < (sepLength)) {
				separators[i] = Box.createRigidArea(d);
				i++;
			}
		}
		i = 0;
		if ((glue == TOP_GLUE) && (orientation == Y_AXIS)) {
			add(Box.createVerticalGlue());
		} else if ((glue == LEFT_GLUE) && (orientation == X_AXIS)) {
			add(Box.createHorizontalGlue());
		} else if ((glue == NO_GLUE) && (orientation == X_AXIS)) {
			add(Box.createHorizontalGlue());
		}
		while (i < length) {
			add(buttons[i]);
			if (i < sepLength) {
				add(separators[i]);
			}
			i++;
		}
		if ((glue == BOTTOM_GLUE) && (orientation == Y_AXIS)) {
			add(Box.createVerticalGlue());
		} else if ((glue == RIGHT_GLUE) && (orientation == X_AXIS)) {
			add(Box.createHorizontalGlue());
		} else if ((glue == NO_GLUE) && (orientation == X_AXIS)) {
			add(Box.createHorizontalGlue());
		}
	}

	/**
	 * Assigns listeners to each button in the row.
	 *
	 * @param listeners
	 *            the array of listeners to assign to the buttons
	 */
	private void setListeners(final ActionListener[] listeners) {
		int i = 0;
		final int length = buttons.length;
		final int listenLength = listeners.length;
		if (listenLength <= length) {
			while (i < length) {
				buttons[i].addActionListener(listeners[i]);
				i++;
			}
		}
	}

	/**
	 * This method allows access to specific buttons in the button row.
	 *
	 * @param index
	 *            the index of the button to retrieve
	 * @return the <tt>JButton</tt> at that index
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of bounds
	 */
	public JButton getButtonAtIndex(final int index) {
		if (index > buttons.length) {
			throw new IndexOutOfBoundsException();
		}
		return buttons[index];
	}

	/**
	 * Sets the button at the specified index to be enabled or disabled.
	 *
	 * @param buttonIndex
	 *            the index of the button to enable or disable
	 * @param enabled
	 *            whether to enable or disable the button
	 */
	public void setButtonEnabled(final int buttonIndex, final boolean enabled) {
		if (buttonIndex > buttons.length) {
			throw new IndexOutOfBoundsException();
		}
		buttons[buttonIndex].setEnabled(enabled);
	}
}
