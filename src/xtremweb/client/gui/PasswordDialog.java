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
 * Created : 18 Avril 2006
 * @author Oleg Lodygensky
 */

package xtremweb.client.gui;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;

public class PasswordDialog extends JDialog implements ActionListener {
	private static String OK = "Ok";
	private static String CANCEL = "Cancel";
	private static String HELP = "Help";

	private final JFrame parent;
	private final JPasswordField password;
	private final JPasswordField password2;
	private boolean cancelled;

	/**
	 * This constructor does everything
	 */
	public PasswordDialog(final JFrame f, final String login) {

		super(f, "XWHEP login", true);

		cancelled = true;

		// Use the default FlowLayout.
		parent = f;

		// login field
		final JLabel jlogin = new JLabel(login);
		jlogin.setForeground(Color.red);
		final JLabel lLabel = new JLabel("Login : ");
		lLabel.setForeground(Color.red);

		// password field
		password = new JPasswordField(10);
		password.setEchoChar('#');
		password.setActionCommand(OK);
		password.addActionListener(this);

		final JLabel pLabel = new JLabel("New password : ");
		pLabel.setLabelFor(password);

		// password2 field
		password2 = new JPasswordField(10);
		password2.setEchoChar('#');
		password2.setActionCommand(OK);
		password2.addActionListener(this);

		final JLabel pLabel2 = new JLabel("Retype new password : ");
		pLabel2.setLabelFor(password2);

		final JButton okButton = new JButton(OK);
		final JButton cancelButton = new JButton(CANCEL);
		final JButton helpButton = new JButton(HELP);

		okButton.setActionCommand(OK);
		cancelButton.setActionCommand(CANCEL);
		helpButton.setActionCommand(HELP);
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);
		helpButton.addActionListener(this);

		final JPanel textPane = new JPanel(new GridLayout(5, 2));
		textPane.add(lLabel);
		textPane.add(jlogin);
		textPane.add(pLabel);
		textPane.add(password);
		textPane.add(pLabel2);
		textPane.add(password2);

		textPane.add(okButton);
		textPane.add(cancelButton);
		textPane.add(helpButton);

		getContentPane().add(textPane);
		pack();
		setSize(400, 150);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		final String cmd = e.getActionCommand();

		if (OK.equals(cmd)) {
			cancelled = false;
			setVisible(false);
		} else if (CANCEL.equals(cmd)) {
			cancelled = true;
			setVisible(false);
		} else {
			JOptionPane.showMessageDialog(parent, "You must enter password twice to avoid errors");
		}
	}

}
