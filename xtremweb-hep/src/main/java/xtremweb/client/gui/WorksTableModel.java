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
 * WorksTableModel.java
 *
 * Purpose : This is the table model to display XtremWeb works informations
 * Created : 18 Avril 2006
 *
 * @author <a href="mailto:lodygens /at\ lal.in2p3.fr>Oleg Lodygensky</a>
 * @version %I%, %G%
 */

package xtremweb.client.gui;

import java.net.ConnectException;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import xtremweb.common.Table;
import xtremweb.common.UID;
import xtremweb.common.WorkInterface;
import xtremweb.common.XMLVector;

/**
 * This class defines a swing table model to display XtremWeb informations<br />
 * This displays and manages an src/common/WorkInterface object
 */

class WorksTableModel extends TableModel {

	/**
	 * This is the default constructor.
	 */
	public WorksTableModel(final MainFrame p) {
		this(p, true);
	}

	/**
	 * This is a constructor.
	 *
	 * @param detail
	 *            tells whether to add a last column to get details
	 */
	public WorksTableModel(final MainFrame p, final boolean detail) {
		super(p, new WorkInterface(), detail);
	}

	/**
	 * This calls TableModel#getButtons() and enables all buttons
	 *
	 * @return a Vector of JButton
	 * @see xtremweb.client.gui.TableModel#getButtons()
	 */
	@Override
	public Hashtable getButtons() {

		final Hashtable ret = super.getButtons();

		((JButton) (ret.get(ADD_LABEL))).setEnabled(true);
		((JButton) (ret.get(DEL_LABEL))).setEnabled(true);

		return ret;
	}

	/**
	 * This adds a task
	 */
	@Override
	public void add() {
		JOptionPane.showMessageDialog(getParent(), "You can't add work, please use the job manager", WARNING,
				JOptionPane.WARNING_MESSAGE);
	}

	/**
	 * This views a task
	 */
	@Override
	public void view() {
		super.view("Work viewer");
	}

	/**
	 * This deletes a task
	 */
	@Override
	public void del() {
		JOptionPane.showMessageDialog(getParent(), "You can't delete work, please use the jo manager", WARNING,
				JOptionPane.WARNING_MESSAGE);
	}

	/**
	 * This retreives an object from server
	 *
	 * @return a TableInterface or null on error
	 * @see xtremweb.communications.CommAPI#get(UID)
	 */
	@Override
	public Table getRow(final UID uid) throws ConnectException {
		try {
			getParent().setTitleConnected();
			return getParent().commClient().get(uid, true);
		} catch (final Exception e) {
			getParent().setTitleNotConnected();
			getLogger().exception(e);
			throw new ConnectException(e.toString());
		}
	}

	/**
	 * This retreives a Vector of work UID from server
	 *
	 * @see xtremweb.communications.CommAPI#getWorks()
	 */
	@Override
	public XMLVector getRows() throws ConnectException {
		try {
			getParent().setTitleConnected();
			return getParent().commClient().getWorks();
		} catch (final Exception e) {
			getParent().setTitleNotConnected();
			getLogger().exception(e);
			throw new ConnectException(e.toString());
		}
	}

} // class WorksTableModel
