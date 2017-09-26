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

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public final class LoginDialog extends JDialog implements ActionListener {
	private static final String OK = "Ok";
	private static final String CANCEL = "Cancel";
	private static final String HELP = "Help";

	private final JFrame _parent;
	private JTextField server;
	private JTextField login;
	private JPasswordField password;
	private boolean cancelled;

	/**
	 * This constructor does everything
	 */
	public LoginDialog(final JFrame f) {

		super(f, "XWHEP login", true);

		setCancelled(true);

		// Use the default FlowLayout.
		_parent = f;

		// server field
		setServer(new JTextField(50));
		getServer().addActionListener(this);
		final JLabel sLabel = new JLabel("Server : ");
		sLabel.setLabelFor(getServer());

		// login field
		setLogin(new JTextField(50));
		getLogin().addActionListener(this);
		final JLabel lLabel = new JLabel("Login : ");
		lLabel.setLabelFor(getLogin());

		// password field
		setPassword(new JPasswordField(10));
		getPassword().setEchoChar('#');
		getPassword().setActionCommand(OK);
		getPassword().addActionListener(this);

		final JLabel pLabel = new JLabel("Password : ");
		pLabel.setLabelFor(getPassword());

		final JButton okButton = new JButton(OK);
		final JButton cancelButton = new JButton(CANCEL);
		final JButton helpButton = new JButton(HELP);

		okButton.setActionCommand(OK);
		okButton.setMnemonic(KeyEvent.VK_O);
		cancelButton.setActionCommand(CANCEL);
		cancelButton.setMnemonic(KeyEvent.VK_C);
		helpButton.setActionCommand(HELP);
		helpButton.setMnemonic(KeyEvent.VK_H);
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);
		helpButton.addActionListener(this);

		final JPanel textPane = new JPanel(new GridLayout(5, 2));
		textPane.add(sLabel);
		textPane.add(getServer());
		textPane.add(lLabel);
		textPane.add(getLogin());
		textPane.add(pLabel);
		textPane.add(getPassword());

		textPane.add(okButton);
		textPane.add(cancelButton);
		textPane.add(helpButton);

		getContentPane().add(textPane);
		resetFocus();
		pack();
		setSize(400, 150);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		final String cmd = e.getActionCommand();

		if (OK.equals(cmd)) {
			setCancelled(false);
			setVisible(false);
		} else if (CANCEL.equals(cmd)) {
			setCancelled(true);
			setVisible(false);
		} else {
			JOptionPane.showMessageDialog(_parent,
					"Server is either a resolved server name, or an IP address\n"
							+ "Login/Password are those provided by the server administrator",
					TableModel.INFO, JOptionPane.INFORMATION_MESSAGE);
		}
	}

	// Must be called from the event-dispatching thread.
	protected void resetFocus() {
		getLogin().requestFocusInWindow();
	}

	/**
	 * @return the server
	 */
	public JTextField getServer() {
		return server;
	}

	/**
	 * @param server
	 *            the server to set
	 */
	public void setServer(final JTextField server) {
		this.server = server;
	}

	/**
	 * @return the cancelled
	 */
	public boolean isCancelled() {
		return cancelled;
	}

	/**
	 * @param cancelled
	 *            the cancelled to set
	 */
	public void setCancelled(final boolean cancelled) {
		this.cancelled = cancelled;
	}

	/**
	 * @return the login
	 */
	public JTextField getLogin() {
		return login;
	}

	/**
	 * @param login
	 *            the login to set
	 */
	public void setLogin(final JTextField login) {
		this.login = login;
	}

	/**
	 * @return the password
	 */
	public JPasswordField getPassword() {
		return password;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(final JPasswordField password) {
		this.password = password;
	}

}
