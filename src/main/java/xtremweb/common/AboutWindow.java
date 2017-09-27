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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.border.Border;

/**
 * Contains the <tt>JDialog</tt> instance that shows "about" information for the
 * application.
 */

public class AboutWindow {

	private static final Component HORIZONTAL_SEPARATOR = Box.createRigidArea(new Dimension(6, 0));

	/**
	 * Constant handle to the <tt>JDialog</tt> that contains about information.
	 */
	private final JDialog theDialog = new JDialog();

	/**
	 * Constant handle to the main <tt>BoxPanel</tt> instance.
	 */
	private final BoxPanel theMainPanel = new BoxPanel(BoxPanel.Y_AXIS);

	/**
	 * Constant handle to the <tt>ImageIcon</tt> to use for the about window.
	 */
	private final ImageIcon theIcon = new ImageIcon();

	/**
	 * Constant dimension for the dialog.
	 */
	private final Dimension theDialogDimension = new Dimension(240, 110);

	/**
	 * Constructs the elements of the about window.
	 */
	public AboutWindow() {
		theDialog.setModal(true);
		theDialog.setResizable(false);
		theDialog.setTitle("About XWHEP");

		final Border border = BorderFactory.createEmptyBorder(12, 6, 6, 6);

		final BoxPanel topPanel = new BoxPanel(BoxPanel.X_AXIS);
		topPanel.add(new JLabel(theIcon));
		topPanel.add(HORIZONTAL_SEPARATOR);
		topPanel.add(HORIZONTAL_SEPARATOR);
		topPanel.add(HORIZONTAL_SEPARATOR);
		final String version = CommonVersion.getCurrent().full();
		final String labelStart = new String("XWHEP");
		final String labelEnd = new String("Copyright (c) CNRS - http://www.cnrs.fr");
		final String labelAddress = new String("http://www.cnrs.fr");
		final String fullLabel = labelStart + " " + version + "\n" + labelAddress + "\n" + labelEnd;
		final MultiLineLabel label = new MultiLineLabel(fullLabel);
		label.setFont(new Font("Sans Serif", Font.PLAIN, 11));
		label.setForeground(Color.black);

		final BoxPanel labelPanel = new BoxPanel(BoxPanel.Y_AXIS);
		labelPanel.add(Box.createVerticalGlue());
		labelPanel.add(label);

		final Dimension labelDim = new Dimension(160, 60);
		labelPanel.setPreferredSize(labelDim);
		labelPanel.setMaximumSize(labelDim);

		topPanel.add(labelPanel);

		theMainPanel.setBorder(border);
		theMainPanel.setPreferredSize(theDialogDimension);
		theDialog.setSize(theDialogDimension);

		final ActionListener closeDialogListener = new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				theDialog.setVisible(false);
			}
		};

		final String[] buttonKeys = { "Close" };

		final String[] buttonTips = { "Close dialog box" };

		final ActionListener[] listeners = { closeDialogListener };

		final ButtonRow buttons = new ButtonRow(buttonKeys, buttonTips, listeners);

		theMainPanel.add(topPanel);
		theMainPanel.add(buttons);
		theDialog.getContentPane().add(theMainPanel);
		theDialog.pack();
	}

	/**
	 * Displays the "About" dialog window to the user.
	 */
	public void showDialog() {
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		final int appWidth = Math.min(screenSize.width, theDialogDimension.width);

		final int appHeight = Math.min(screenSize.height - 40, theDialogDimension.height);
		theDialog.setLocation((screenSize.width - appWidth) / 2, (screenSize.height - appHeight) / 2);

		theDialog.setVisible(true);
	}
}
