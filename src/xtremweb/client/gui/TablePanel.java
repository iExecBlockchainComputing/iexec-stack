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
 * TablePanel.java
 *
 * Purpose : XtremWeb main GUI panel
 * Created : 18 Avril 2006
 *
 * @author <a href="mailto:lodygens /at\ lal.in2p3.fr>Oleg Lodygensky</a>
 * @version %I%, %G%
 */

package xtremweb.client.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.ConnectException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import xtremweb.common.StatusEnum;
import xtremweb.common.UID;

/**
 * This class describes the XtremWeb client swing panel.
 */

public class TablePanel extends JPanel {

	private final JTable jTable;
	private final TableModel model;

	public TableModel getTableModel() {
		return model;
	}

	private final TableSorter sorter;

	/**
	 * This constructor inserts needed tables.
	 */
	public TablePanel(final TableModel m) {

		super(new GridBagLayout());
		model = m;

		sorter = new TableSorter(model);

		final GridBagLayout gbLayout = (GridBagLayout) getLayout();
		final GridBagConstraints gbConstraints = new GridBagConstraints();

		jTable = new JTable(sorter);
		sorter.setTableHeader(jTable.getTableHeader());

		model.setTable(jTable);
		model.setSorter(sorter);

		final Hashtable buttons = model.getButtons();

		gbConstraints.anchor = GridBagConstraints.CENTER;
		gbConstraints.fill = GridBagConstraints.BOTH;
		gbConstraints.gridx = GridBagConstraints.RELATIVE;
		gbConstraints.gridy = GridBagConstraints.RELATIVE;
		gbConstraints.weightx = 1.0;
		gbConstraints.weighty = 0.0;

		final Enumeration enums = buttons.elements();
		int i = 0;
		final int size = buttons.size();
		while (enums.hasMoreElements()) {

			final JComponent button = (JComponent) (enums.nextElement());

			if (i == (size - 1)) {
				gbConstraints.gridwidth = GridBagConstraints.REMAINDER;
			}
			gbLayout.setConstraints(button, gbConstraints);
			add(button);

			i++;
		}

		setUpButtonRenderer(jTable);
		setUpButtonEditor(jTable);
		jTable.setPreferredScrollableViewportSize(new Dimension(500, 70));

		//
		// Create the scroll pane and add the table to it.
		//
		final JScrollPane jScrollPane = new JScrollPane(jTable);
		gbConstraints.weighty = 1.0;
		gbConstraints.ipadx = -5;
		gbConstraints.gridwidth = GridBagConstraints.REMAINDER;
		gbLayout.setConstraints(jScrollPane, gbConstraints);
		add(jScrollPane);
	}

	/**
	 * This retreives datas from server; this also enable/disable buttons
	 */
	public void refresh() throws ConnectException {
		// retreive datas from server
		model.refresh();
		// enable/disable buttons
		model.getButtons();
	}

	public int getRowCount() {
		return model.getRowCount();
	}

	public JTable getTable() {
		return model.getTable();
	}

	/**
	 * This calls JTable::setDefaultRenderer(). Its sets a renderer for JButton;
	 * we don't have to worry about rendering JCheckBox since it is included in
	 * standard Java API.
	 *
	 * @see the java programming API.
	 * @param table
	 *            containing button cells.
	 */
	private void setUpButtonRenderer(final JTable table) {
		table.setDefaultRenderer(JButton.class, new PushButtonRenderer());
		table.setDefaultRenderer(String.class, new StringRenderer());
		table.setDefaultRenderer(UID.class, new StringRenderer());
		table.setDefaultRenderer(Date.class, new DateRenderer());
	}

	/**
	 * This defines action on button click in dedicated table button cells. Its
	 * sets an editor for JButton; we don't have to worry about editing
	 * JCheckBox since it is included in standard Java API.
	 *
	 * @see the java programming API.
	 * @param table
	 *            containing button cells.
	 */
	private void setUpButtonEditor(final JTable table) {
		//
		// First, set up button that bring up the dialog.
		//
		final JButton pushButton = new JButton("Detail");

		//
		// Now create editors to encapsulate buttons, and
		// set them up as editors.
		//
		final PushButtonEditor pushButtonEditor = new PushButtonEditor(pushButton);
		table.setDefaultEditor(JButton.class, pushButtonEditor);
	}

	/************************************************
	 * This inner class defines the Button renderer *
	 ************************************************/

	class ArrayRenderer extends JComboBox implements TableCellRenderer {
		public ArrayRenderer() {
		}

		@Override
		public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
				final boolean hasFocus, final int row, final int column) {
			return this;
		}
	}

	/************************************************
	 * This inner class defines the Button renderer *
	 ************************************************/

	class PushButtonRenderer extends JButton implements TableCellRenderer {
		public PushButtonRenderer() {
			super("Detail");
		}

		@Override
		public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
				final boolean hasFocus, final int row, final int column) {
			return this;
		}
	}

	/************************************************
	 * This inner class defines the String renderer *
	 ************************************************/

	class StringRenderer extends JLabel implements TableCellRenderer {
		public StringRenderer() {
			super("");
			setOpaque(true);
			this.setBackground(Color.WHITE);
		}

		@Override
		public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
				final boolean hasFocus, final int row, final int column) {
			if (value != null) {

				Color color = Color.BLACK;

				setText(value.toString());
				try {
					switch (StatusEnum.valueOf(value.toString())) {
					case COMPLETED:
						color = Color.GREEN.darker();
						break;
					case RUNNING:
						color = Color.ORANGE.darker();
						break;
					case ERROR:
						color = Color.RED.darker();
						break;
					}
				} catch (final Exception e) {
					// not a status
				}

				this.setForeground(color);

				if (isSelected) {
					this.setBackground(Color.LIGHT_GRAY);
				} else {
					this.setBackground(Color.WHITE);
				}
			}
			return this;
		}
	}

	/************************************************
	 * This inner class defines the Date renderer *
	 ************************************************/

	class DateRenderer extends JLabel implements TableCellRenderer {
		public DateRenderer() {
			super("");
			setOpaque(true);
			this.setBackground(Color.WHITE);
		}

		@Override
		public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
				final boolean hasFocus, final int row, final int column) {
			if (value != null) {
				setText(value.toString());
				if (isSelected) {
					this.setBackground(Color.LIGHT_GRAY);
				} else {
					this.setBackground(Color.WHITE);
				}
			}
			return this;
		}
	}

	/**************************************************
	 * This inner class defines the PushButton editor *
	 **************************************************/

	class PushButtonEditor extends DefaultCellEditor {

		public PushButtonEditor(final JButton b) {
			// This is an artefact only, since our button is a simple JButton.
			// Unfortunately, the constructor expects JCheckBox, JComboBox,
			// or JText.
			super(new JCheckBox());

			editorComponent = b;
			setClickCountToStart(1); // This is usually 1 or 2.

			// Must do this so that editing stops when appropriate.
			b.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(final ActionEvent e) {
					fireEditingStopped();
				}
			});
		}

		@Override
		public Object getCellEditorValue() {
			return new Boolean(false);
		}

		@Override
		public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected,
				final int row, final int column) {

			final JTable dataTable;
			final String dlgTitle;
			final String dlgLabel;

			((JButton) editorComponent).setSelected(false);

			return editorComponent;

		} // getTableCellEditorComponent()

	} // class PushButtonEditor

}
