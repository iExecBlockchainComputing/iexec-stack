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
 * MainPanel.java
 *
 * Purpose : XtremWeb main GUI panel
 * Created : 18 Avril 2006
 *
 * @author <a href="mailto:lodygens /at\ lal.in2p3.fr>Oleg Lodygensky</a>
 * @version %I%, %G%
 */

package xtremweb.client.gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import xtremweb.common.Logger;
import xtremweb.common.LoggerLevel;

/**
 * This class describes the XtremWeb client swing panel.
 */

public final class MainPanel extends JPanel {

	private final Logger logger;

	public LoggerLevel getLoggerLevel() {
		return logger.getLoggerLevel();
	}

	/**
	 * This defines tabs order Tab must be inserted in this order
	 */
	public enum Tabs {
		JOBS, GROUPS, SESSIONS, DATAS, APPS, USERS, USERGROUPS, HOSTS, WORKS, TASKS
	}

	/**
	 * This sets the logger level. This also sets the logger levels checkboxes
	 * menu item.
	 */
	public void setLoggerLevel(LoggerLevel l) {
		logger.setLoggerLevel(l);
		jobsTableModel.setLoggerLevel(l);
		groupsTableModel.setLoggerLevel(l);
		sessionsTableModel.setLoggerLevel(l);
		appsTableModel.setLoggerLevel(l);
		datasTableModel.setLoggerLevel(l);
		usersTableModel.setLoggerLevel(l);
		usergroupsTableModel.setLoggerLevel(l);
		hostsTableModel.setLoggerLevel(l);
		tasksTableModel.setLoggerLevel(l);
		worksTableModel.setLoggerLevel(l);
	}

	/**
	 * This is the parent frame
	 */
	private final MainFrame parent;

	/**
	 * This is the tabbed pane containing all panes
	 */
	private JTabbedPane tabbedPane;

	/**
	 * This is the jobs table model
	 */
	private JobsTableModel jobsTableModel;
	/**
	 * This is the groups table model
	 */
	private GroupsTableModel groupsTableModel;
	/**
	 * This is the sessions table model
	 */
	private SessionsTableModel sessionsTableModel;
	/**
	 * This is the apps table model
	 */
	private AppsTableModel appsTableModel;
	/**
	 * This is the datas table model
	 */
	private DatasTableModel datasTableModel;
	/**
	 * This is the users table model
	 */
	private UsersTableModel usersTableModel;
	/**
	 * This is the user groups table model
	 */
	private UsergroupsTableModel usergroupsTableModel;
	/**
	 * This is the hosts table model
	 */
	private HostsTableModel hostsTableModel;
	/**
	 * This is the works table model
	 */
	private WorksTableModel worksTableModel;
	/**
	 * This is the tasks table model
	 */
	private TasksTableModel tasksTableModel;

	/**
	 * This is the tasks pane
	 */
	private TablePanel tasksPanel;
	/**
	 * This tells whether task pane is shown
	 */
	private boolean viewTasks;
	/**
	 * This is the works pane
	 */
	private TablePanel worksPanel;
	/**
	 * This tells whether works pane is shown
	 */
	private boolean viewWorks;

	/**
	 * This constructor inserts needed panels in a new tabbed pane
	 */
	public MainPanel(MainFrame p) {

		parent = p;

		tabbedPane = new JTabbedPane();
		logger = new Logger(this);

		try {
			jobsTableModel = new JobsTableModel(parent);
			groupsTableModel = new GroupsTableModel(parent);
			sessionsTableModel = new SessionsTableModel(parent);
			datasTableModel = new DatasTableModel(parent);
			appsTableModel = new AppsTableModel(parent);
			usersTableModel = new UsersTableModel(parent);
			usergroupsTableModel = new UsergroupsTableModel(parent);
			hostsTableModel = new HostsTableModel(parent);
			tasksTableModel = new TasksTableModel(parent);
			worksTableModel = new WorksTableModel(parent);

			tabbedPane.addTab("Jobs", new TablePanel(jobsTableModel));
			tabbedPane.setMnemonicAt(Tabs.JOBS.ordinal(), KeyEvent.VK_J);
			tabbedPane.addTab("Groups", new TablePanel(groupsTableModel));
			tabbedPane.setMnemonicAt(Tabs.GROUPS.ordinal(), KeyEvent.VK_G);
			tabbedPane.addTab("Sessions", new TablePanel(sessionsTableModel));
			tabbedPane.setMnemonicAt(Tabs.SESSIONS.ordinal(), KeyEvent.VK_S);
			tabbedPane.addTab("Datas", new TablePanel(datasTableModel));
			tabbedPane.setMnemonicAt(Tabs.DATAS.ordinal(), KeyEvent.VK_T);
			tabbedPane.addTab("Apps", new TablePanel(appsTableModel));
			tabbedPane.setMnemonicAt(Tabs.APPS.ordinal(), KeyEvent.VK_P);
			tabbedPane.addTab("Users", new TablePanel(usersTableModel));
			tabbedPane.setMnemonicAt(Tabs.USERS.ordinal(), KeyEvent.VK_U);
			tabbedPane.addTab("Usergroups",
					new TablePanel(usergroupsTableModel));
			tabbedPane.setMnemonicAt(Tabs.USERGROUPS.ordinal(), KeyEvent.VK_G);
			tabbedPane.addTab("Hosts", new TablePanel(hostsTableModel));
			tabbedPane.setMnemonicAt(Tabs.HOSTS.ordinal(), KeyEvent.VK_H);

			tasksPanel = new TablePanel(tasksTableModel);
			viewTasks = false;
			worksPanel = new TablePanel(worksTableModel);
			viewWorks = false;

			tabbedPane.addChangeListener(new ChangeListener() {
				// This method is called whenever the selected tab changes
				public void stateChanged(ChangeEvent evt) {
					final JTabbedPane pane = (JTabbedPane) evt.getSource();
					final TablePanel tab = (TablePanel) pane
							.getSelectedComponent();
					parent.setTotalLines(tab.getRowCount());
					parent.setSelectedLines(tab.getTable().getSelectedRows().length);
				}
			});
		} catch (final Exception e) {
			logger.exception(e);
			System.exit(1);
		}

		tabbedPane.setSelectedIndex(0);

		setLayout(new GridLayout(1, 1));
		add(tabbedPane);

		setMinimumSize(new Dimension(500, 450));

		setLoggerLevel(parent.getLoggerLevel());

	}

	/**
	 * This reset lists; this is used on reconnection
	 */
	public void reset() {
		jobsTableModel.reset();
		groupsTableModel.reset();
		sessionsTableModel.reset();
		appsTableModel.reset();
		datasTableModel.reset();
		usersTableModel.reset();
		usergroupsTableModel.reset();
		hostsTableModel.reset();
		tasksTableModel.reset();
		worksTableModel.reset();
	}

	/**
	 * This reset job list; this is used on reconnection
	 */
	public void resetJobList() {
		jobsTableModel.reset();
	}

	/**
	 * This enables/disables button accordingly to user rights. This is used on
	 * reconnection
	 */
	public void enableButtons() {
		jobsTableModel.getButtons();
		groupsTableModel.getButtons();
		sessionsTableModel.getButtons();
		datasTableModel.getButtons();
		appsTableModel.getButtons();
		usersTableModel.getButtons();
		usergroupsTableModel.getButtons();
		hostsTableModel.getButtons();
		tasksTableModel.getButtons();
		worksTableModel.getButtons();
	}

	/**
	 * This shows/hides tasks pane
	 */
	public boolean toggleTasks() {
		if (!viewTasks) {
			tabbedPane.addTab("Tasks", tasksPanel);
			viewTasks = true;
		} else {
			tabbedPane.remove(tasksPanel);
			viewTasks = false;
		}
		return viewTasks;
	}

	/**
	 * This shows/hides works pane
	 */
	public boolean toggleWorks() {
		if (!viewWorks) {
			tabbedPane.addTab("Works", worksPanel);
			viewWorks = true;
		} else {
			tabbedPane.remove(worksPanel);
			viewWorks = false;
		}
		return viewWorks;
	}

}
