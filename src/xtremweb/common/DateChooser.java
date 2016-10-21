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

/*
 * This has been extracted from HEP JAS by Standford university
 *        http://www-sldnt.slac.stanford.edu/jas/index.htm
 */

package xtremweb.common;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

interface DateListener {
	void dateChanged();
}

final class DateModel {
	DateModel(final Calendar date) {
		this.date = date;
	}

	DateModel(final Date date) {
		this.date = Calendar.getInstance();
		this.date.setTime(date);
	}

	synchronized void addDateListener(final DateListener l) {
		listeners.add(l);
	}

	synchronized void removeDateListener(final DateListener l) {
		listeners.remove(l);
	}

	synchronized void fireDateChanged() {
		final Iterator e = listeners.iterator();
		while (e.hasNext()) {
			final DateListener l = (DateListener) e.next();
			l.dateChanged();
		}
	}

	void set(final int field, final int value) {
		date.set(field, value);
		fireDateChanged();
	}

	void roll(final int field, final boolean up) {
		date.roll(field, up);
		fireDateChanged();
	}

	void add(final int field, final int delta) {
		date.add(field, delta);
		fireDateChanged();
	}

	int get(final int field) {
		return date.get(field);
	}

	Calendar getCalendar() {
		return date;
	}

	int getDaysInMonth() {
		final Calendar temp = (Calendar) date.clone();
		temp.add(Calendar.DATE, 31);
		return (31 - temp.get(Calendar.DAY_OF_MONTH)) + date.get(Calendar.DAY_OF_MONTH);
	}

	Date getTime() {
		return date.getTime();
	}

	private final Collection listeners = new Vector();
	private final Calendar date;
}

class AbstractDateComboModel extends AbstractListModel implements ComboBoxModel, DateListener {
	AbstractDateComboModel(final DateModel model, final int field) {
		this.setModel(model);
		this.setField(field);
		model.addDateListener(this);
	}

	@Override
	public void dateChanged() {
		fireContentsChanged(this, 0, 100);
	}

	@Override
	public Object getElementAt(final int index) {
		return element[index];
	}

	public int getElementSize() {
		return element.length;
	}

	@Override
	public int getSize() {
		return getModel().getCalendar().getMaximum(getField()) + 1;
	}

	@Override
	public Object getSelectedItem() {
		final int i = getModel().get(getField());
		return element[i];
	}

	@Override
	public void setSelectedItem(final Object value) {
		final int i = ((Integer) value).intValue();
		getModel().set(getField(), i);
	}

	/**
	 * @return the model
	 */
	public DateModel getModel() {
		return model;
	}

	/**
	 * @param model
	 *            the model to set
	 */
	public void setModel(final DateModel model) {
		this.model = model;
	}

	/**
	 * @return the field
	 */
	public int getField() {
		return field;
	}

	/**
	 * @param field
	 *            the field to set
	 */
	public void setField(final int field) {
		this.field = field;
	}

	private DateModel model;
	private int field;
	private static Integer[] element;
	static {
		element = new Integer[100];
		for (int i = 0; i < element.length; i++) {
			element[i] = new Integer(i);
		}
	}
}

class DateMonthModel extends AbstractDateComboModel {
	DateMonthModel(final DateModel model) {
		super(model, Calendar.MONTH);
	}

}

class MonthCellRenderer extends BasicComboBoxRenderer {
	@Override
	public Component getListCellRendererComponent(final JList list, final Object value, final int index,
			final boolean isSelected, final boolean cellHasFocus) {
		final int i = ((Integer) value).intValue();

		return super.getListCellRendererComponent(list, month[i], index, isSelected, cellHasFocus);
	}

	private static final String[] month = { "January", "February", "March", "April", "May", "June", "July", "August",
			"September", "October", "November", "December" };
}

class DateDayModel extends AbstractDateComboModel {
	DateDayModel(final DateModel model) {
		super(model, Calendar.DAY_OF_MONTH);
	}

	@Override
	public int getSize() {
		return getModel().getDaysInMonth();
	}

	@Override
	public Object getElementAt(final int index) {
		return getElementAt(index + 1);
	}
}

class DateHourModel extends AbstractDateComboModel {
	DateHourModel(final DateModel model) {
		super(model, Calendar.HOUR_OF_DAY);
	}

	@Override
	public int getSize() {
		return 12;
	}

	@Override
	public Object getSelectedItem() {
		int i = getModel().get(getField());

		if (i >= 12) {
			i -= 12;
		}
		return getElementAt(i);
	}

	@Override
	public void setSelectedItem(final Object value) {
		final int ampm = getModel().get(Calendar.AM_PM);
		int i = ((Integer) value).intValue();
		if (ampm > 0) {
			i += 12;
		}
		getModel().set(getField(), i);
	}
}

class DateMinuteModel extends AbstractDateComboModel {
	DateMinuteModel(final DateModel model) {
		super(model, Calendar.MINUTE);
	}
}

class DateSecondModel extends AbstractDateComboModel {
	DateSecondModel(final DateModel model) {
		super(model, Calendar.SECOND);
	}
}

class DateAMPMModel extends AbstractDateComboModel {
	DateAMPMModel(final DateModel model) {
		super(model, Calendar.AM_PM);
	}

	@Override
	public void setSelectedItem(final Object value) {
		int hour = getModel().get(Calendar.HOUR_OF_DAY);
		final int i = ((Integer) value).intValue();

		if ((i == 0) && (hour >= 12)) {
			hour -= 12;
		}
		if ((i == 1) && (hour < 12)) {
			hour += 12;
		}
		getModel().set(Calendar.HOUR_OF_DAY, hour);
	}
}

class MinuteCellRenderer extends BasicComboBoxRenderer {
	@Override
	public Component getListCellRendererComponent(final JList list, final Object value, final int index,
			final boolean isSelected, final boolean cellHasFocus) {
		final String s = f.format(value);

		return super.getListCellRendererComponent(list, s, index, isSelected, cellHasFocus);
	}

	private static Format f = new DecimalFormat("00");
}

class HourCellRenderer extends BasicComboBoxRenderer {
	HourCellRenderer() {
		setHorizontalAlignment(SwingConstants.RIGHT);
	}

	@Override
	public Component getListCellRendererComponent(final JList list, final Object value, final int index,
			final boolean isSelected, final boolean cellHasFocus) {
		int i = ((Integer) value).intValue();
		if (i == 0) {
			i = 12;
		}
		final String s = String.valueOf(i);
		return super.getListCellRendererComponent(list, s, index, isSelected, cellHasFocus);
	}
}

class AMPMCellRenderer extends BasicComboBoxRenderer {
	@Override
	public Component getListCellRendererComponent(final JList list, final Object value, final int index,
			final boolean isSelected, final boolean cellHasFocus) {
		final int i = ((Integer) value).intValue();

		return super.getListCellRendererComponent(list, ampm[i], index, isSelected, cellHasFocus);
	}

	private static String[] ampm = { "AM", "PM" };
}

class DateYearModel extends AbstractDateComboModel {
	DateYearModel(final DateModel model, final int offset) {
		super(model, Calendar.YEAR);
		this.offset = offset;
	}

	@Override
	public int getSize() {
		return getElementSize();
	}

	@Override
	public Object getSelectedItem() {
		final int i = getModel().get(getField());
		return getElementAt(i - offset);
	}

	@Override
	public void setSelectedItem(final Object value) {
		final int i = ((Integer) value).intValue();
		getModel().set(getField(), i + offset);
	}

	private final int offset;
}

class YearCellRenderer extends BasicComboBoxRenderer {
	YearCellRenderer(final int offset) {
		YearCellRenderer.offset = offset;
	}

	@Override
	public Component getListCellRendererComponent(final JList list, final Object value, final int index,
			final boolean isSelected, final boolean cellHasFocus) {
		final int i = ((Integer) value).intValue();

		return super.getListCellRendererComponent(list, String.valueOf(i + offset), index, isSelected, cellHasFocus);
	}

	private static int offset;
}

class CalendarHeader extends JComponent {

	CalendarHeader(final DateModel model) {
		this.model = model;
		final int offset = 1950;

		setLayout(new FlowLayout());
		final JComboBox day = new JComboBox(new DateDayModel(model));
		final JComboBox month = new JComboBox(new DateMonthModel(model));
		final JComboBox year = new JComboBox(new DateYearModel(model, offset));

		month.setRenderer(new MonthCellRenderer());
		year.setRenderer(new YearCellRenderer(offset));

		final JButton down = new JButton("<");
		final JButton up = new JButton(">");

		down.addActionListener(new RollListener(-1));
		up.addActionListener(new RollListener(+1));
		add(down);
		add(day);
		add(month);
		add(year);
		add(up);
	}

	private final DateModel model;

	private class RollListener implements ActionListener {
		RollListener(final int delta) {
			this.delta = delta;
		}

		@Override
		public void actionPerformed(final ActionEvent evt) {
			model.add(Calendar.MONTH, delta);
		}

		private final int delta;
	}
}

class TimeHeader extends JComponent {
	TimeHeader(final DateModel model) {
		this.model = model;

		setLayout(new FlowLayout());

		final JComboBox hour = new JComboBox(new DateHourModel(model));
		final JComboBox minute = new JComboBox(new DateMinuteModel(model));
		final JComboBox second = new JComboBox(new DateSecondModel(model));
		final JComboBox ampm = new JComboBox(new DateAMPMModel(model));

		hour.setRenderer(new HourCellRenderer());
		minute.setRenderer(new MinuteCellRenderer());
		second.setRenderer(new MinuteCellRenderer());
		ampm.setRenderer(new AMPMCellRenderer());

		add(hour);
		add(minute);
		add(second);
		add(ampm);
	}

	private final DateModel model;
}

class CalendarPane extends JComponent implements DateListener, ActionListener {
	CalendarPane(final DateModel model) {
		this.model = model;
		model.addDateListener(this);

		setLayout(null);
		final Insets insets = new Insets(1, 1, 1, 1);

		days = new JButton[31];

		for (int i = 0; i < 31; i++) {
			days[i] = new JButton(String.valueOf(i + 1));
			days[i].addActionListener(this);
			add(days[i]);
		}

		fg = days[30].getForeground();
		bg = days[30].getBackground();
		buttonSize = days[30].getPreferredSize();
		panelSize = new Dimension(buttonSize.width * 7, buttonSize.height * 7);
		hidden = new Point(buttonSize.width * 10, buttonSize.height * 10);

		for (int i = 0; i < 31; i++) {
			days[i].setSize(buttonSize);
		}

		for (int i = 0; i < 7; i++) {
			final JLabel l = new JLabel(labels[i], SwingConstants.CENTER);
			add(l);
			l.setLocation(i * buttonSize.width, 0);
			l.setSize(buttonSize);
		}

		layoutCalendar();
	}

	@Override
	public Dimension getPreferredSize() {
		return panelSize;
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		final String s = e.getActionCommand();
		final int day = Integer.valueOf(s).intValue();
		model.set(Calendar.DAY_OF_MONTH, day);
	}

	@Override
	public void dateChanged() {
		layoutCalendar();
	}

	void layoutCalendar() {
		final Calendar selected = model.getCalendar();
		final int day = selected.get(Calendar.DAY_OF_MONTH) - 1;

		if (selectedButton != days[day]) {
			if (selectedButton != null) {
				selectedButton.setForeground(fg);
				selectedButton.setBackground(bg);
				selectedButton.repaint();
			}
			selectedButton = days[day];
			selectedButton.setForeground(UIManager.getColor("textHighlightText"));
			selectedButton.setBackground(UIManager.getColor("textHighlight"));
			selectedButton.repaint();
		}

		final Calendar date = (Calendar) selected.clone();
		date.set(Calendar.DAY_OF_MONTH, 1);

		int i = 0;

		for (; i < model.getDaysInMonth(); i++) {
			final int x = (date.get(Calendar.DAY_OF_WEEK) - 1) * buttonSize.width;
			final int y = (date.get(Calendar.WEEK_OF_MONTH)) * buttonSize.height;
			days[i].setLocation(x, y);
			date.add(Calendar.DATE, 1);
		}

		for (; i < 31; i++) {
			days[i].setLocation(hidden);
		}
	}

	private JButton selectedButton;
	private final Color fg;
	private final Color bg;
	private final Dimension buttonSize;
	private final Dimension panelSize;
	private final Point hidden;
	private final DateModel model;
	private final JButton[] days;
	private final static String[] labels = { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };
}

class CalendarTest {
	public static void main(final String[] argv) {
		final JFrame f = new JFrame("test");
		final DateModel model = new DateModel(Calendar.getInstance());

		f.getContentPane().add(new CalendarHeader(model), BorderLayout.NORTH);
		f.getContentPane().add(new CalendarPane(model), BorderLayout.CENTER);
		f.getContentPane().add(new TimeHeader(model), BorderLayout.SOUTH);
		f.pack();
		f.setVisible(true);
	}
}

public class DateChooser extends JASDialog {
	public DateChooser(final Frame f, final Date d) {
		super(f, "Choose Date...");
		model = new DateModel(d);
		getContentPane().add(new CalendarHeader(model), BorderLayout.NORTH);
		getContentPane().add(new CalendarPane(model), BorderLayout.CENTER);
		getContentPane().add(new TimeHeader(model), BorderLayout.SOUTH);
		pack();
	}

	public Date getDate() {
		return model.getTime();
	}

	private final DateModel model;
}
