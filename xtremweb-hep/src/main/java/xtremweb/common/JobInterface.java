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

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.sql.ResultSet;
import java.util.Date;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import xtremweb.communications.URI;

/**
 * This describes a job by collecting informations from works, tasks, groups,
 * sessions, users, apps and hosts.<br />
 * This is used to display informations in a more friendly way than
 * xtremweb.common.WorkInterface only. This mainly translates UID to names and
 * labels (i.e. users UID to users login etc.).<br />
 * This is a read only class : this class cannot be used to modify works, apps
 * etc.<br />
 *
 * This derives from xtremweb.common.TableInterface to uniform code so that it
 * can be used where a TableInterface is expected even if objects of this class
 * donot transit through the network.<br />
 * This is only used on client side.<br />
 * <br />
 * This can be understood as the following SQL command:<br />
 * SELECT
 * works.uid,groups.name,sessions.name,users.name,apps.name,works.label,works
 * .status, works.cmdline,works.arrivaldate,
 * works.completeddate,works.returncode,works.error_msg, hosts.name FROM
 * users,apps,tasks,hosts,groups,sessions WHERE works.groupuid = groups.uid AND
 * works.sessionuid = sessions.uid AND works.uid = tasks.uid AND works.user =
 * users.uid AND works.app = apps.uid AND tasks.host = hosts.uid;<br />
 * <br />
 * Created: 23 avril 2006<br />
 *
 * @author <a href="mailto:lodygens /at\ .in2p3.fr>Oleg Lodygensky</a>
 */
public class JobInterface extends Table {

	/**
	 * @since 7.0.0
	 */
	private AppInterface app;
	/**
	 * @since 7.0.0
	 */
	private GroupInterface group;
	/**
	 * @since 7.0.0
	 */
	private HostInterface host;
	/**
	 * @since 7.0.0
	 */
	private SessionInterface session;
	/**
	 * @since 7.0.0
	 */
	private UserInterface user;
	/**
	 * @since 7.0.0
	 */
	private WorkInterface work;
	/**
	 * This is the XML tag
	 */
	public static final String THISTAG = "job";

	/**
	 * This enumerates this interface columns
	 */
	public enum Columns implements XWBaseColumn {

		/**
		 * This is the column index of the group UID
		 */
		GROUPNAME,
		/**
		 * This is the column index of the session UID
		 */
		SESSIONNAME,
		/**
		 * This is the column index of the user login
		 */
		USERLOGIN,
		/**
		 * This is the column index of the user X509 proxy
		 */
		USERPROXY,
		/**
		 * This is column index of the the application name
		 */
		APPNAME,
		/**
		 * This is the column index of the label, if any
		 */
		LABEL,
		/**
		 * This is the column index of the status
		 *
		 * @see StatusEnum
		 */
		STATUS {
			/**
			 * This creates a new UID from string
			 */
			@Override
			public StatusEnum fromString(final String v) {
				return StatusEnum.valueOf(v);
			}
		},
		/**
		 * This is the column index of the result URI
		 */
		RESULTURI {
			@Override
			public URI fromString(final String v) throws URISyntaxException {
				return new URI(v);
			}
		},
		/**
		 * This is the column index of the command line, if any
		 */
		CMDLINE {
			/**
			 * This creates an object from String representation for this column
			 * value This cleans the parameter to ensure SQL compliance
			 *
			 * @param v
			 *            the String representation
			 * @return a Boolean representing the column value
			 * @throws Exception
			 *             is thrown on instantiation error
			 */
			@Override
			public String fromString(final String v) {
				final String val = v;
				return val.replaceAll("[\\n\'\"]+", "_");
			}
		},
		/**
		 * This is the column index of the arrival date
		 */
		ARRIVALDATE {
			@Override
			public Date fromString(final String v) {
				return XWTools.getSQLDateTime(v);
			}
		},
		/**
		 * This is the column index of the completed date
		 */
		COMPLETEDDATE {
			@Override
			public Date fromString(final String v) {
				return XWTools.getSQLDateTime(v);
			}
		},
		/**
		 * This is the column index of the application return code
		 */
		RETURNCODE {
			@Override
			public Integer fromString(final String v) {
				return Integer.valueOf(v);
			}
		},
		/**
		 * This is the column index of the error message, if any
		 */
		ERROR_MSG {
			/**
			 * This creates an object from String representation for this column
			 * value This cleans the parameter to ensure SQL compliance
			 *
			 * @param v
			 *            the String representation
			 * @return a Boolean representing the column value
			 * @throws Exception
			 *             is thrown on instantiation error
			 */
			@Override
			public String fromString(final String v) {
				final String val = v;
				return val.replaceAll("[\\n\\s\'\"]+", "_");
			}
		},
		/**
		 * This is the column index of the worker, if any
		 */
		HOSTNAME;

		/**
		 * This is the index based on ordinal so that the first value is
		 * TableColumns + 1
		 *
		 * @see xtremweb.common#TableColumns
		 * @see Enum#ordinal()
		 * @since 8.2.0
		 */
		private int ord;

		/**
		 * This constructor sets the ord member as ord = this.ordinal +
		 * TableColumns.SIZE
		 *
		 * @since 8.2.0
		 */
		Columns() {
			ord = this.ordinal() + TableColumns.SIZE;
		}

		/**
		 * This retrieves the index based ordinal
		 *
		 * @return the index based ordinal
		 * @since 8.2.0
		 */
		@Override
		public int getOrdinal() {
			return ord;
		}

		/**
		 * This creates a new object from String for the given column
		 *
		 * @param v
		 *            the String representation
		 * @return v
		 * @throws Exception
		 *             is thrown on instantiation error
		 */
		@Override
		public Object fromString(final String v) throws Exception {
			return v;
		}

		/**
		 * This creates a new object from SQL result set
		 *
		 * @param rs
		 *            is the SQL result set
		 * @return the object representing the column
		 * @throws Exception
		 *             is thrown on instantiation error
		 */
		public final Object fromResultSet(final ResultSet rs) throws Exception {
			return this.fromString(rs.getString(this.toString()));
		}

		/**
		 * This retrieves an Columns from its integer value
		 *
		 * @param v
		 *            is the integer value of the Columns
		 * @return an Columns
		 */
		public static XWBaseColumn fromInt(final int v) throws IndexOutOfBoundsException {
			try {
				return TableColumns.fromInt(v);
			} catch (final Exception e) {
			}
			for (final Columns c : Columns.values()) {
				if (c.getOrdinal() == v) {
					return c;
				}
			}
			throw new IndexOutOfBoundsException(("unvalid Columns value ") + v);
		}

		/**
		 * This retrieves this enumeration string representation
		 *
		 * @return a array containing this enum string representation
		 */
		public static String[] getLabels() {
			final String[] labels = new String[ENUMSIZE];
			for (final TableColumns c : TableColumns.values()) {
				labels[c.getOrdinal()] = c.toString();
			}
			for (final Columns c : Columns.values()) {
				labels[c.getOrdinal()] = c.toString();
			}
			return labels;
		}
	}

	/**
	 * This is the size, including TableColumns
	 */
	private static final int ENUMSIZE = Columns.values().length;

	/**
	 * This is the default constructor
	 */
	public JobInterface() {

		super(THISTAG, THISTAG);
		setAttributeLength(ENUMSIZE);

		setColumns(Columns.getLabels());
		setShortIndexes(new int[] { TableColumns.UID.getOrdinal(), Columns.USERLOGIN.getOrdinal(),
				Columns.APPNAME.getOrdinal(), Columns.STATUS.getOrdinal() });
		app = null;
		group = null;
		host = null;
		session = null;
		user = null;
		work = null;
	}

	/**
	 * This construcs a new job
	 *
	 * @param w
	 *            is the WorkInterface of the job
	 * @param a
	 *            is the AppInterface of the job, or null is application is not
	 *            defined (app has been removed from server)
	 * @param u
	 *            is the UserInterface of the job or null is user is not defined
	 *            (user has been removed from server)
	 * @param h
	 *            is the HostInterface of the job or null is host is not defined
	 *            (host has been removed from server)
	 */
	public JobInterface(final WorkInterface w, final GroupInterface g, final SessionInterface s, final AppInterface a,
			final UserInterface u, final HostInterface h) throws IOException {

		this();

		app = a;
		group = g;
		host = h;
		session = s;
		user = u;
		work = w;
	}

	/**
	 * This calls this(StreamIO.stream(input));
	 *
	 * @param input
	 *            is a String containing an XML representation
	 */
	public JobInterface(final String input) throws IOException, SAXException {
		this(StreamIO.stream(input));
	}

	/**
	 * This constructs a new object from input stream providing XML
	 * representation
	 *
	 * @param input
	 *            is the input stream
	 * @see XMLReader#read(InputStream)
	 * @throws IOException
	 *             on XML error
	 */
	public JobInterface(final InputStream input) throws IOException, SAXException {
		this();
		final XMLReader reader = new XMLReader(this);
		try {
			reader.read(input);
		} catch (final InvalidKeyException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This constructs a new object from XML attributes received from input
	 * stream
	 *
	 * @param attrs
	 *            contains attributes XML representation
	 * @see Table#fromXml(Attributes)
	 * @throws IOException
	 *             on XML error
	 */
	public JobInterface(final Attributes attrs) throws IOException {
		this();
		super.fromXml(attrs);
	}

	/**
	 * @since 7.0.0
	 */
	public WorkInterface getWork() {
		return work;
	}

	/**
	 * This gets the group name
	 *
	 * @return this attribute, or null if not set
	 * @since 3.2.0
	 */
	public String getGroupName() {
		if (group != null) {
			return group.getName();
		}
		return null;
	}

	/**
	 * This gets the session name
	 *
	 * @return this attribute, or null if not set
	 * @since 3.2.0
	 */
	public String getSessionName() {
		if (session != null) {
			return session.getName();
		}
		return null;
	}

	/**
	 * This gets the expected host name
	 *
	 * @return this attribute, or null if not set
	 * @since RPCXW
	 */
	public String getHostName() {
		if (host != null) {
			return host.getName();
		}
		return null;
	}

	/**
	 * This gets this work application UID
	 *
	 * @return this work application UID
	 * @exception IOException
	 *                is thrown is attribute is nor set, neither well formed
	 */
	public String getApplicationName() throws IOException {
		return app.getName();
	}

	/**
	 * This gets the job name as defined by user at submission time. <br>
	 * Label is optional
	 *
	 * @return this attribute, or null if not set
	 */
	public String getLabel() {
		return work.getLabel();
	}

	/**
	 * This gets user login
	 *
	 * @return this attribute, or null if not set
	 * @exception IOException
	 *                is thrown is attribute is nor set, neither well formed
	 */
	public String getLogin() throws IOException {
		return user.getLogin();
	}

	/**
	 * This gets an attribute
	 *
	 * @exception IOException
	 *                is thrown if attribute is not set
	 * @return this attribute
	 */
	public StatusEnum getStatus() throws IOException {
		return work.getStatus();
	}

	/**
	 * This retrieves the URI where to get result
	 *
	 * @return this attribute, or null if not set
	 */
	public URI getResult() {
		return work.getResult();
	}

	/**
	 * This retrieves the URI of the X509 user proxy
	 *
	 * @return this attribute, or null if not set
	 */
	public URI getUserProxy() {
		return work.getUserProxy();
	}

	/**
	 * This gets an attribute
	 *
	 * @return job return code, 0 on error
	 */
	public int getReturnCode() {
		return work.getReturnCode();
	}

	/**
	 * This gets an attribute
	 *
	 * @return this attribute, or null if not set
	 */
	public String getCmdLine() {
		return work.getCmdLine();
	}

	/**
	 * This gets an attribute
	 *
	 * @return this attribute, or null if not set
	 */
	public Date getArrivalDate() {
		return work.getArrivalDate();
	}

	/**
	 * This gets an attribute
	 *
	 * @return this attribute, or null if not set
	 */
	public Date getCompletedDate() {
		return work.getCompletedDate();
	}

	/**
	 * This sets parameter value; this is called from
	 * TableInterface#fromXml(Attributes)
	 *
	 * @param attribute
	 *            is the name of the attribute to set
	 * @param v
	 *            is the new attribute value
	 * @return true if value has changed, false otherwise
	 * @see Table#fromXml(Attributes)
	 */
	@Override
	public final boolean setValue(final String attribute, final Object v) throws IllegalArgumentException {
		final String A = attribute.toUpperCase();
		try {
			return setValue(TableColumns.valueOf(A), v);
		} catch (final Exception e) {
			return setValue(Columns.valueOf(A), v);
		}
	}

	@Override
	public void fill(final ResultSet rs) throws IOException {
		setDirty(false);
	}

	@Override
	public void updateInterface(final Table t) throws IOException {
		// TODO Auto-generated method stub

	}
}
