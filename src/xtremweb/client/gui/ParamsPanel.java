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
 * ParamsPanel.java
 *
 * Purpose : XtremWeb parameters GUI panel
 * Created : 18 Avril 2006
 *
 * @author <a href="mailto:lodygens /at\ lal.in2p3.fr>Oleg Lodygensky</a>
 * @version %I%, %G%
 */

package xtremweb.client.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import xtremweb.common.WorkerParameters;

/**
 * This class describes the XtremWeb parameter GUI panel
 */

public class ParamsPanel extends JPanel {
	/**
	 * This displays trusted addresses in a list
	 */
	private final JList trustedAddressesList;
	/**
	 * This is needed to be able to add/remove trusted addresses to/from
	 * <CODE>trustedAddressesList</CODE>
	 */
	private DefaultListModel trustedAddressesModel;
	/**
	 * This button helps to remove address from trusted ones.
	 */
	private final JButton removeTrustedAddress;
	/**
	 * This button helps to add address to trusted ones.
	 */
	private final JButton addTrustedAddress;

	/**
	 * These are needed to retreive parameters values from user.
	 */
	private JTextField paramsNbWorkers;

	private final MainFrame parent;

	/**
	 * This constructs a new panel with GUI to manage XtremWeb parameters
	 */
	public ParamsPanel(MainFrame p) {

		parent = p;
		final JTabbedPane tabbedPane = new JTabbedPane();
		final GridBagLayout gbLayout = new GridBagLayout();
		final GridBagConstraints gbConstraints = new GridBagConstraints();
		WorkerParameters params;

		final JButton aButton = new JButton("Commit");

		aButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				commitParams(e);
			}
		});

		gbConstraints.anchor = GridBagConstraints.CENTER;
		gbConstraints.fill = GridBagConstraints.VERTICAL;
		gbConstraints.gridx = GridBagConstraints.RELATIVE;
		gbConstraints.gridy = GridBagConstraints.RELATIVE;
		gbConstraints.weightx = 1.0;
		gbConstraints.weighty = 0.0;
		gbConstraints.gridwidth = GridBagConstraints.REMAINDER;
		gbLayout.setConstraints(aButton, gbConstraints);
		add(aButton);

		final JLabel label = new JLabel("Expected workers");
		gbConstraints.fill = GridBagConstraints.BOTH;
		gbConstraints.gridwidth = 1;
		gbLayout.setConstraints(label, gbConstraints);
		add(label);

		paramsNbWorkers = new JTextField("0", 5);
		gbConstraints.gridwidth = GridBagConstraints.REMAINDER;
		gbLayout.setConstraints(paramsNbWorkers, gbConstraints);
		add(paramsNbWorkers);

		trustedAddressesList = new JList(trustedAddressesModel);
		trustedAddressesList
				.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		final JScrollPane addrScrollPane = new JScrollPane(trustedAddressesList);
		gbConstraints.weighty = 1.0;
		gbConstraints.ipadx = -5;
		gbConstraints.gridwidth = GridBagConstraints.REMAINDER;
		gbLayout.setConstraints(addrScrollPane, gbConstraints);
		add(addrScrollPane);

		addTrustedAddress = new JButton("Add");
		addTrustedAddress.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addAddress(e);
			}
		});
		gbConstraints.weighty = 0.0;
		gbConstraints.gridwidth = 1;
		gbLayout.setConstraints(addTrustedAddress, gbConstraints);
		add(addTrustedAddress);

		removeTrustedAddress = new JButton("Remove");
		removeTrustedAddress.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeAddress(e);
			}
		});
		gbConstraints.gridwidth = GridBagConstraints.REMAINDER;
		gbLayout.setConstraints(removeTrustedAddress, gbConstraints);
		add(removeTrustedAddress);

	}

	/**
	 * This is called when user clicks 'commit' on page 'params'
	 */
	void commitParams(ActionEvent e) {

		int nbWorkers;
		final int sendResultDelay = 0;
		final int resultDelay = 0;

		try {
			nbWorkers = Integer.parseInt(paramsNbWorkers.getText());
		} catch (final Exception ex) {
			JOptionPane.showMessageDialog(this, "Integer format error!!!");
			return;
		}
	}

	/**
	 * This is called when user clicks 'add' on page 'params' to add a trusted
	 * adresse.
	 */
	void addAddress(ActionEvent e) {
		final JOptionPane jop = new JOptionPane();
		final String newAddr;
	}

	/**
	 * This is called when user clicks 'remove' on page 'params' to remove
	 * selected trusted adresse.
	 */
	void removeAddress(ActionEvent e) {
	}

	/**
	 * This converts a string to a list model accordingly to a separator
	 */
	private DefaultListModel stringToListModel(String input, char separator) {
		final DefaultListModel ret = new DefaultListModel();
		int index = 0;
		int lastIndex = -1;

		if (input == null) {
			return ret;
		}

		for (index = input.indexOf(separator); index != -1; index = input
				.indexOf(separator, index + 1)) {

			final String newStr = input.substring(lastIndex + 1, index);
			ret.addElement(newStr);
			lastIndex = index;
		}

		return ret;
	}

}
