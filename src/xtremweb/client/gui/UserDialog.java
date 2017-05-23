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
 * This defines a modal Dialog to enter server, login and password
 */

package xtremweb.client.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

import xtremweb.common.Table;

public class UserDialog extends ViewDialog implements ActionListener {

	private static String p = "Change password";

	/**
	 * This constructor does everything
	 *
	 * @param f
	 *            is the aprent JFrame
	 * @param title
	 *            is the dialog title
	 * @param columns
	 *            is a String array containing columns name
	 * @param row
	 *            is a Vector containing the selected row
	 * @param editable
	 *            enables/disables edition
	 */
	public UserDialog(final JFrame f, final String title, final String[] columns, final Table row,
			final boolean editable) {

		super(f, title, columns, row.toVector(), editable);

		final JButton passwordButton = new JButton(p);

		passwordButton.setActionCommand(p);
		passwordButton.addActionListener(this);

		getTextPane().add(passwordButton);

		pack();
	}

	@Override
	public void actionPerformed(final ActionEvent e) {

		super.actionPerformed(e);
		if (!isVisible()) {
			return;
		}

		final String cmd = e.getActionCommand();

		if (p.equals(cmd)) {
			final JTextField jlogin = (JTextField) getFields().get("LOGIN");
			final PasswordDialog dlgPassword = new PasswordDialog(getParent(), jlogin.getText());
			dlgPassword.setVisible(true);
		}
	}

}
