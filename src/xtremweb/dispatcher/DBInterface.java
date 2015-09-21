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

package xtremweb.dispatcher;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.AccessControlException;
import java.security.InvalidKeyException;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import xtremweb.common.AppInterface;
import xtremweb.common.AppTypeEnum;
import xtremweb.common.Cache;
import xtremweb.common.DataInterface;
import xtremweb.common.DataTypeEnum;
import xtremweb.common.GroupInterface;
import xtremweb.common.HostInterface;
import xtremweb.common.Logger;
import xtremweb.common.MD5;
import xtremweb.common.SessionInterface;
import xtremweb.common.StatusEnum;
import xtremweb.common.Table;
import xtremweb.common.TableColumns;
import xtremweb.common.TaskInterface;
import xtremweb.common.TraceInterface;
import xtremweb.common.UID;
import xtremweb.common.UserGroupInterface;
import xtremweb.common.UserInterface;
import xtremweb.common.UserRightEnum;
import xtremweb.common.WorkInterface;
import xtremweb.common.XWConfigurator;
import xtremweb.common.XWPropertyDefs;
import xtremweb.common.XWTools;
import xtremweb.communications.EmailSender;
import xtremweb.communications.URI;
import xtremweb.database.ColumnSelection;
import xtremweb.database.DBConnPoolThread;
import xtremweb.database.SQLRequest;
import xtremweb.database.SQLRequestReadable;
import xtremweb.database.SQLRequestWorkStatus;
import xtremweb.security.X509Proxy;
import xtremweb.security.XWAccessRights;

/**
 * DBInterface.java Created: Sun Feb 25 22:06:12 2001
 * 
 * @author Gilles Fedak
 * @version %I% %G%
 */

public final class DBInterface {
	/**
	 * Configurator
	 * 
	 * @since 9.0.0
	 */
	private final XWConfigurator config;

	private final Logger logger;

	/**
	 * This helps to send mail
	 * @since 9.1.0
	 */
	EmailSender emailSender;

	/**
	 * Constants that define some fixed dirnames These are not considered as
	 * "hardcoded", but a part of the specification.
	 */

	@SuppressWarnings("unused")
	private final int statCounter = 0;

	/** This aims to recover works when a server has crashed */
	@SuppressWarnings("unused")
	private final boolean recoverWorks = true;

	private static DBInterface instance = null;

	public static final DBInterface getInstance() {
		return instance;
	}

	/**
	 * This implements connection to database
	 */
	private final DBConnPoolThread dbConnPool;

	/**
	 * This is a cache to reduce MySQL accesses
	 * 
	 * @since 7.4.0
	 */
	private final Cache cache;
	/**
	 * This is this local host name; this is used to create URI to store objects
	 * in local cache
	 */
	private final String localHostName = XWTools.getLocalHostName();

	/**
	 * This creates a new URI for the provided UID
	 * 
	 * @since 7.4.0
	 * @return a new URI, if UID is not null, null otherwise
	 */
	private URI newURI(final UID uid) throws URISyntaxException {
		if (uid == null) {
			return null;
		}
		return new URI(localHostName, uid);
	}

	/**
	 * This caches an object interface
	 * 
	 * @since 7.4.0
	 */
	protected void putToCache(final Table itf) {

		if (itf == null) {
			return;
		}

		try {
			final UID uid = itf.getUID();
			final URI uri = newURI(uid);
			cache.add(itf, uri);
		} catch (final Exception e) {
			logger.exception("can't put to cache", e);
		}
	}

	/**
	 * This inserts an object to both DB and cache
	 * 
	 * @throws IOException
	 * @since 9.0.0
	 */
	private <T extends Table> void insert(final T row) throws IOException {
		row.insert();
		putToCache(row);
	}

	/**
	 * This retrieves an object interface from cache
	 * 
	 * @since 7.4.0
	 */
	private Table getFromCache(final URI uri) {
		return cache.get(uri);
	}

	/**
	 * This retrieves an object from cache
	 * 
	 * @since 8.2.0
	 */
	private <T extends Table> T getFromCache(URI uri, T row) {
		final Table itf = getFromCache(uri);
		if (itf == null) {
			return null;
		}
		//
		// we must check interface type because the cache contains all object
		// types
		//
		if (itf.getClass().toString().compareTo(row.getClass().toString()) != 0) {
			return null;
		}
		return (T)itf;
	}

	/**
	 * This retrieves an object from cache
	 * 
	 * @since 7.4.0
	 */
	private <T extends Table> T getFromCache(final UID uid, final T row) {
		if (uid == null) {
			return null;
		}
		try {
			final URI uri = newURI(uid);
			if (uri == null) {
				return null;
			}
			return getFromCache(uri, row);
		} catch (final Exception e) {
			logger.exception("can't retrieve from cache", e);
			return null;
		}
	}

	/**
	 * This retrieves an object interface from cache
	 * 
	 * @since 7.4.0
	 */
	private Table getFromCache(final UserInterface u, final URI uri) throws IOException,
	AccessControlException {

		final Table ret = getFromCache(uri);

		if (ret == null) {
			return null;
		}

		final UserInterface owner = user(ret.getOwner());
		final UID ownerGroup = (owner == null ? null :owner.getGroup());

		final boolean accessdenied = ((ret.canRead(u, ownerGroup) == false) && (u.getRights()
				.lowerThan(UserRightEnum.ADVANCED_USER)));

		if (accessdenied) {
			throw new AccessControlException(u.getLogin() + " can't access "
					+ uri);
		}
		return ret;
	}

	/**
	 * This retrieves an object from cache
	 * 
	 * @since 8.2.0
	 */
	private <T extends Table> T getFromCache(UserInterface u, UID uid, T row)
			throws IOException, AccessControlException {

		if (uid == null) {
			return null;
		}
		try {
			final URI uri = newURI(uid);
			return getFromCache(u, uri, row);
		} catch (final URISyntaxException e) {
			logger.exception(e);
			return null;
		}
	}

	/**
	 * This retrieves an object from cache
	 * 
	 * @since 7.4.0
	 */
	private <T extends Table> T getFromCache(UserInterface u, URI uri, T row)
			throws IOException, AccessControlException {
		if (uri == null) {
			return null;
		}
		final Table itf = getFromCache(u, uri);
		if (itf == null) {
			return null;
		}
		return getFromCache(uri, row);
	}

	/**
	 * This removes an object from cache
	 * 
	 * @since 7.4.0
	 */
	private void removeFromCache(Table row) {
		try {
			removeFromCache(row.getUID());
		} catch (final Exception e) {
			logger.exception("can't remove from cache", e);
		}
	}

	/**
	 * This removes an object from cache
	 * 
	 * @since 7.4.0
	 */
	private void removeFromCache(UID uid) {
		try {
			final URI uri = newURI(uid);
			removeFromCache(uri);
		} catch (final Exception e) {
			logger.exception("can't remove from cache", e);
		}
	}

	/**
	 * This removes an object from cache
	 * 
	 * @since 7.4.0
	 */
	private void removeFromCache(URI uri) throws IOException {
		if (uri == null) {
			return;
		}
		cache.remove(uri);
	}

	/**
	 * This instantiates a DBConnPoolThread, update application pools and set default SQLRequest attributes
	 * @see xtremweb.database.DBConnPoolThread
	 * @see xtremweb.database.SQLRequest#setDbName(String)
	 * @see xtremweb.database.SQLRequest#setHsqldb(boolean)
	 */
	public DBInterface(final XWConfigurator c) throws IOException {
		logger = new Logger(this);
		config = c;
		dbConnPool = new DBConnPoolThread(config);
		dbConnPool.start();
		cache = new Cache(config);

		emailSender = new EmailSender();

		updateAppsPool();

		final String tracesDirName = getTracesPath();

		final File d = new File(tracesDirName);
		if ((!d.exists()) && (!d.mkdirs())) {
			logger.warn("Cannot mkdirs " + d);
		}

		instance = this;
		SQLRequest.setDbName(config.getProperty(XWPropertyDefs.DBNAME));
	}

	/**
	 * This sends an email the theClient
	 * @since 9.1.0
	 * @param theClient is the user modifying/deleting an object
	 * @param row is the modified/deleted row
	 * @param header is the email subject
	 * @param msg is the email message
	 */
	private <T extends Table> void sendMail(final UserInterface theClient, final T row, final String msg) {
		if(theClient == null) {
			logger.warn("can't send mail : client is null");
			return;
		}
		try {
			emailSender.send("XtremWeb-HEP@" + XWTools.getLocalHostName() + " : " + row.getUID(), 
					theClient.getEMail(),
					msg + "\n" + row.toString(false, true) + "\n\n" +
							"https://" + XWTools.getLocalHostName() + ":" + System.getProperty(XWPropertyDefs.HTTPSPORT.toString())+ "/get/" + row.getUID());
		} catch (Exception e) {
			logger.warn(e.toString());
		}
	}

	/**
	 * This retrieves all rows from table and caches them This forces DB read
	 * 
	 * @param row
	 *            defines the row type
	 * @return the first found row, or null if not found
	 */
	protected <T extends Table> T select(T row) throws IOException {
		return select(row, (String) null);
	}

	/**
	 * This retrieves a row from DB by its UID
	 * 
	 * @param row
	 *            defines the row type
	 * @param uid
	 *            is the UID of the row to retrieve
	 * @return the first found row, or null if not found
	 */
	protected <T extends Table> T select(T row, UID uid) throws IOException {
		if (uid == null) {
			return null;
		}
		return select(row,
				SQLRequest.MAINTABLEALIAS + ".UID='" + uid.toString() + "'");
	}

	/**
	 * This retrieves all rows from table and caches them This forces DB read
	 * 
	 * @param row
	 *            defines the row type
	 * @param criterias
	 *            is the SQL criterias
	 * @return the first found row, or null if not found
	 */
	protected <T extends Table> T select(T row, String criterias)
			throws IOException {
		return selectOne(row, criterias);
	}

	/**
	 * This retrieves rows from DB
	 * 
	 * @param row
	 *            defines the row type
	 * @return a collection of TableInterface
	 * @since 5.8.0
	 */
	protected <T extends Table> Collection<T> selectAll(T row)
			throws IOException {
		return selectAll(row, (String) null);
	}

	/**
	 * This contains accessrights&amp; + XWAccessRights.OTHERREAD_INT + &gt;0;
	 * 
	 * @since 7.0.0
	 */
	private final String publicConditions = "accessrights&"
			+ XWAccessRights.OTHERREAD_INT + ">0";

	/**
	 * This retrieves rows from DB that are "anonymously" readable (i.e.
	 * row.accessrights &amp; XWAccessRights.OTHERREAD_INT &gt; 0)
	 * 
	 * @param row
	 *            defines the row type
	 * @return a Collection of public rows
	 * @since 7.0.0
	 */
	public <T extends Table> Collection<T> selectAllPublic(T row)
			throws IOException {
		if (row == null) {
			logger.warn("selectAll : row is null ?!?!");
			return null;
		}
		return selectAll(row, publicConditions);
	}

	/**
	 * This retrieves rows from DB
	 * 
	 * @param row
	 *            defines the row type
	 * @param conditions
	 *            restrict selected rows
	 * @return a Collection of found rows
	 * @since 5.8.0
	 */
	protected <T extends Table> Collection<T> selectAll(T row, String conditions)
			throws IOException {
		if (row == null) {
			logger.warn("selectAll : row is null ?!?!");
			return null;
		}

		logger.finest("selectAll : " + row.getClass().getName() + ", "
				+ conditions);

		return DBConnPoolThread.getInstance().selectAll(row, conditions);
	}

	/**
	 * This retrieves UIDs from DB
	 * 
	 * @param row
	 *            defines the row type
	 * @return a Collection of found UID
	 * @since 5.8.0
	 */
	protected <T extends Table> Collection<UID> selectUID(T row)
			throws IOException {
		return selectUID(row, (String) null);
	}

	/**
	 * This retrieves UIDs from DB
	 * 
	 * @param row
	 *            defines the row type
	 * @param conditions
	 *            restrict selected rows
	 * @return a Collection of found UID
	 * @since 5.8.0
	 */
	protected <T extends Table> Collection<UID> selectUID(T row,
			String conditions) throws IOException {
		if (row == null) {
			logger.warn("selectUID : row is null ?!?!");
			return null;
		}

		logger.finest("selectUID : " + row.getClass().getName() + ", "
				+ conditions);
		return DBConnPoolThread.getInstance().selectUID(row, conditions);
	}

	/**
	 * This retrieves first row
	 * 
	 * @param row
	 *            defines the row type
	 * @return the first found row, or null
	 * @since 5.8.0
	 */
	protected <T extends Table> T selectOne(T row) throws IOException {
		return selectOne(row, (String) null);
	}

	/**
	 * This retrieves one row from DB
	 * 
	 * @param row
	 *            defines the row type
	 * @param conditions
	 *            restrict selected rows
	 * @return a Collection of found rows
	 * @since 10.0.0
	 */
	protected <T extends Table> T selectOne(T row, String conditions)
			throws IOException {
		if (row == null) {
			logger.warn("selectOne : row is null ?!?!");
			return null;
		}

		logger.finest("selectOne : " + row.getClass().getName() + ", "
				+ conditions);

		return DBConnPoolThread.getInstance().selectOne(row, conditions);
	}

	/**
	 * This deletes a row in DB;
	 * 
	 * @param theCient
	 *            is the requesting client
	 * @param urights
	 *            is the expected rights to delete (DELETEJOB, DELETEDATA etc)
	 * @param row
	 *            is the row to delete
	 * @since 5.8.0
	 * @see xtremweb.common#UserRights
	 * @exception IOException
	 *                is thrown on DB access or I/O error
	 * @exception AccessControlException
	 *                is thrown if the client does not have sufficient rights to
	 *                delete
	 */
	private <T extends Table> boolean delete(final UserInterface theClient, final T row)
			throws AccessControlException, IOException {

		if (row == null) {
			return false;
		}

		final UserInterface owner = user(row.getOwner());
		final UID ownerGroup = (owner == null	? null : owner.getGroup());

		if ((!row.canWrite(theClient, ownerGroup))
				&& (theClient.getRights()
						.lowerThan(UserRightEnum.SUPER_USER))) {
			if (config.getAdminUid() != null) {
				final UserInterface admin = user(config.getAdminUid());
				sendMail(admin, row, theClient.getLogin() + " can't delete");
			}
			throw new AccessControlException("delete(row) "
					+ theClient.getLogin() + " can't delete " + row.getUID());
		}

		//		sendMail(owner, row, theClient.getLogin() + " has deleted");
		row.delete();
		removeFromCache(row);

		return true;
	}

	/**
	 * This checks access rights and eventually deletes a row in DB by calling
	 * row.delete()
	 * 
	 * @param theCient
	 *            is the requesting client
	 * @param urights
	 *            is the expected rights to delete (DELETEJOB, DELETEDATA etc)
	 * @param row
	 *            is the row to delete
	 * @since 5.8.0
	 * @see xtremweb.common#UserRights
	 * @exception IOException
	 *                is thrown on DB access or I/O error
	 * @exception AccessControlException
	 *                is thrown if the client does not have sufficient rights to
	 *                delete
	 */
	private boolean delete(UserInterface theClient, DataInterface row)
			throws IOException, AccessControlException {

		if (row == null) {
			return false;
		}

		final UserInterface owner = user(row.getOwner());
		final UID ownerGroup = (owner == null ? null : owner.getGroup());

		if ((!row.canRead(theClient, ownerGroup))
				&& (theClient.getRights().lowerThan(UserRightEnum.SUPER_USER))) {
			throw new AccessControlException("delete(data) "
					+ theClient.getLogin() + " can't delete " + row.getUID());
		}
		logger.debug("DBInterface#delete dec links " + row.toXml());
		row.decLinks();
		update(theClient, UserRightEnum.DELETEDATA, row);
		try {
			if (row.getLinks() <= 0) {
				logger.debug("deleting " + row.toXml());
				return this.delete(theClient, (Table) row);
			}
		} catch (final AccessControlException e) {
		}
		return true;
	}

	/**
	 * This updates a row in DB and cache; this does not check access rights
	 * 
	 * @param row
	 *            is the row to update
	 * @since 5.8.0
	 * @see xtremweb.common.UserRightEnum
	 * @exception IOException
	 *                is thrown on DB access or I/O error
	 * @exception AccessControlException
	 *                is thrown if the client does not have sufficient rights to
	 *                update
	 */
	synchronized protected <T extends Table> void update(T row) throws IOException,
	AccessControlException {
		if (row == null) {
			return;
		}
		putToCache(row);
		row.update();
	}

	/**
	 * This updates a row in DB
	 * 
	 * @param rows
	 *            is the vector of rows to update
	 * @since 5.8.0
	 * @see xtremweb.common.UserRightEnum
	 * @exception IOException
	 *                is thrown on DB access or I/O error
	 * @exception AccessControlException
	 *                is thrown if the client does not have sufficient rights to
	 *                update
	 */
	synchronized protected <T extends Table> void update(final Collection<T> rows)
			throws IOException, AccessControlException {
		if (rows == null) {
			return;
		}
		for (final Iterator<T> rowit = rows.iterator(); rowit.hasNext();) {
			putToCache(rowit.next());
		}
		DBConnPoolThread.getInstance().update(rows);
	}

	/**
	 * This updates a row in DB;
	 * 
	 * @param theCient
	 *            is the requesting client
	 * @param urights
	 *            is the expected rights to update (INSERTJOB, INSERTDATA etc)
	 * @param row
	 *            is the row to update
	 * @since 5.8.0
	 * @see xtremweb.common#UserRights
	 * @exception IOException
	 *                is thrown on DB access or I/O error
	 * @exception AccessControlException
	 *                is thrown if the client does not have sufficient rights to
	 *                update
	 */
	private <T extends Table> void update(final UserInterface theClient,
			final UserRightEnum urights, final T row) throws IOException,
			AccessControlException {

		final UserInterface owner = user(row.getOwner());
		final UID ownerGroup = (owner == null ? null : owner.getGroup());

		if (!row.canWrite(theClient, ownerGroup)
				&& (theClient.getRights().lowerThan(urights))) {
			if (config.getAdminUid() != null) {
				final UserInterface admin = user(config.getAdminUid());
				sendMail(admin, row, theClient.getLogin() + " can't update");
			}
			throw new AccessControlException(theClient.getLogin()
					+ " can't update " + row.getUID());
		}
		row.update();
		putToCache(row);
		//		sendMail(owner, row, theClient.getLogin() + " has updated");
	}

	/**
	 * This creates a new readable application to retrieve from DB
	 * 
	 * @param u
	 *            is the requesting user
	 * @since 5.8.0
	 */
	private AppInterface readableApp(UserInterface u) throws IOException {
		final SQLRequestReadable r = new SQLRequestReadable(
				AppInterface.TABLENAME, u, ColumnSelection.selectAll);
		return new AppInterface(r);
	}

	/**
	 * This creates a new readable application to retrieve from DB
	 * 
	 * @param u
	 *            is the requesting user
	 * @param uid
	 *            is the UID of the application to retrieve
	 * @since 5.8.0
	 * @return the application which uid is provided; null if uid is null
	 */
	private AppInterface readableApp(UserInterface u, UID uid)
			throws IOException {
		if (uid == null) {
			return null;
		}
		final SQLRequestReadable r = new SQLRequestReadable(
				AppInterface.TABLENAME, u, ColumnSelection.selectAll, uid);
		return new AppInterface(r);
	}

	/**
	 * This creates a new readable application to retrieve apps UID form DB
	 * 
	 * @param u
	 *            is the requesting user
	 * @since 5.8.0
	 */
	private AppInterface readableAppUID(UserInterface u) throws IOException {
		final SQLRequestReadable r = new SQLRequestReadable(
				AppInterface.TABLENAME, u, ColumnSelection.selectUID);
		return new AppInterface(r);
	}

	/**
	 * This retrieves an application independently of access rights from cache
	 * or from DB
	 * 
	 * @param uid
	 *            is the UID of the host to retrieve
	 * @since 8.0.0
	 */
	protected AppInterface app(UID uid) throws IOException {
		if (uid == null) {
			return null;
		}
		final AppInterface rowType = new AppInterface();
		AppInterface ret = getFromCache(uid, rowType);
		if (ret != null) {
			return ret;
		}
		ret = select(rowType, uid);
		return ret;
	}

	/**
	 * This retrieves an application from cache or from DB
	 * 
	 * @param u
	 *            is the requesting user
	 * @param uid
	 *            is the UID of the application to retrieve
	 * @since 5.8.0
	 * @return the application which uid is provided; null if uid is null
	 */
	public AppInterface app(UserInterface u, UID uid) throws IOException,
	AccessControlException {
		if (uid == null) {
			return null;
		}
		final AppInterface row = new AppInterface();
		AppInterface ret = getFromCache(u, uid, row);
		if (ret != null) {
			return ret;
		}
		final AppInterface readableRow = readableApp(u, uid);
		ret = select(readableRow);
		return ret;
	}

	/**
	 * This retrieves an app accordingly to conditions
	 * 
	 * @param u
	 *            is the requesting user
	 * @param conditions
	 *            restrict selected rows
	 * @return the last loaded row
	 * @since 5.8.0
	 */
	public AppInterface app(UserInterface u, String conditions)
			throws IOException {
		final AppInterface row = readableApp(u);
		return selectOne(row, conditions);
	}

	/**
	 * This retrieves all applications without any confitions
	 * 
	 * @return a Collection of applications
	 * @since 5.8.0
	 */
	private Collection<AppInterface> apps() throws IOException {
		final AppInterface row = new AppInterface();
		return selectAll(row);
	}

	/**
	 * This retrieves UID of readable applications for the given user
	 * 
	 * @param u
	 *            is the requesting user
	 * @return a Collection of UID
	 * @since 5.8.0
	 */
	public Collection<UID> appsUID(UserInterface u) throws IOException {
		final AppInterface row = readableAppUID(u);
		return selectUID(row);
	}

	/**
	 * This creates a new readable data to retrieve from DB
	 * 
	 * @param u
	 *            is the requesting user
	 * @param uid
	 *            is the UID of the data to retrieve
	 * @since 5.8.0
	 */
	private DataInterface readableData(UserInterface u, UID uid)
			throws IOException {
		if (uid == null) {
			return null;
		}
		final SQLRequestReadable r = new SQLRequestReadable(
				DataInterface.TABLENAME, u, ColumnSelection.selectAll, uid);
		return new DataInterface(r);
	}

	/**
	 * This creates a new readable data to retrieve datas UID from DB
	 * 
	 * @param u
	 *            is the requesting user
	 * @since 5.8.0
	 */
	private DataInterface readableDataUID(UserInterface u) throws IOException {
		final SQLRequestReadable r = new SQLRequestReadable(
				DataInterface.TABLENAME, u, ColumnSelection.selectUID);
		return new DataInterface(r);
	}

	/**
	 * This retrieves a data; it first look in cache, then in DB. Access rights
	 * are bypassed
	 * 
	 * @param uid
	 *            is the UID of the data to retrieve
	 * @since 5.8.0
	 */
	protected DataInterface data(UID uid) throws IOException,
	AccessControlException {
		if (uid == null) {
			return null;
		}
		final DataInterface rowType = new DataInterface();
		DataInterface ret = getFromCache(uid, rowType);
		if (ret != null) {
			return ret;
		}
		ret = select(rowType, uid);
		return ret;
	}

	/**
	 * This retrieves a data for the requesting user. Data access rights are
	 * checked
	 * 
	 * @param u
	 *            is the requesting user
	 * @param uri
	 *            is the URI of the data to retrieve
	 * @since 5.8.0
	 */
	protected DataInterface data(UserInterface u, URI uri) throws IOException,
	AccessControlException {

		if (uri == null) {
			return null;
		}
		return data(u, uri.getUID());
	}

	/**
	 * This retrieves a data for the requesting user. It first look in cache,
	 * then in DB. Data access rights are checked.
	 * 
	 * @param u
	 *            is the requesting user
	 * @param uid
	 *            is the UID of the data to retrieve
	 * @since 5.8.0
	 */
	protected DataInterface data(UserInterface u, UID uid) throws IOException,
	AccessControlException {
		if (uid == null) {
			return null;
		}
		final DataInterface row = new DataInterface();
		final DataInterface ret = getFromCache(u, uid, row);
		if (ret != null) {
			return ret;
		}

		final DataInterface readableRow = readableData(u, uid);
		return select(readableRow);
	}

	/**
	 * This retrieves a set of data UID for the requesting user. Data access
	 * rights are checked.
	 * 
	 * @param user
	 *            is the requesting user
	 * @return a Collection of UID
	 * @since 5.8.0
	 */
	public Collection<UID> datasUID(UserInterface user) throws IOException {
		final DataInterface row = readableDataUID(user);
		return selectUID(row);
	}

	/**
	 * This retrieves a set of UID of data owned by the requesting user.
	 * 
	 * @param user
	 *            is the requesting user
	 * @return a Collection of UID
	 * @since 7.0.0
	 */
	public Collection<UID> ownerDatasUID(UserInterface user) throws IOException {
		final DataInterface row = readableDataUID(user);
		return selectUID(row, "maintable.owneruid='" + user.getUID() + "'");
	}

	/**
	 * This retrieves the data cache size
	 * 
	 * @since 5.8.0
	 */
	public int dataSize(UserInterface user) throws IOException {
		try {
			datasUID(user).size();
		} catch (final Exception e) {
		}
		return 0;
	}

	/**
	 * This creates a new readable group to retrieve from DB
	 * 
	 * @param u
	 *            is the requesting user
	 * @since 5.8.0
	 */
	private GroupInterface readableGroup(UserInterface u) throws IOException {
		final SQLRequestReadable r = new SQLRequestReadable(
				GroupInterface.TABLENAME, u, ColumnSelection.selectAll);
		return new GroupInterface(r);
	}

	/**
	 * This creates a new readable group to retrieve from DB
	 * 
	 * @param u
	 *            is the requesting user
	 * @param uid
	 *            is the UID of the group to retrieve
	 * @since 5.8.0
	 */
	private GroupInterface readableGroup(UserInterface u, UID uid)
			throws IOException {
		if (uid == null) {
			return null;
		}
		final SQLRequestReadable r = new SQLRequestReadable(
				GroupInterface.TABLENAME, u, ColumnSelection.selectAll, uid);
		return new GroupInterface(r);
	}

	/**
	 * This creates a new readable group to retrieve groups UID from DB
	 * 
	 * @param u
	 *            is the requesting user
	 * @since 5.8.0
	 */
	private GroupInterface readableGroupUID(UserInterface u) throws IOException {
		final SQLRequestReadable r = new SQLRequestReadable(
				GroupInterface.TABLENAME, u, ColumnSelection.selectUID);
		return new GroupInterface(r);
	}

	/**
	 * This retrieves a group for the requesting user. It first looks in cache,
	 * then in DB. Group access rights are checked.
	 * 
	 * @param u
	 *            is the requesting user
	 * @param uid
	 *            is the UID of the group to retrieve
	 * @since 5.8.0
	 */
	protected GroupInterface group(UserInterface u, UID uid)
			throws IOException, AccessControlException {

		if (uid == null) {
			return null;
		}
		final GroupInterface row = new GroupInterface();
		GroupInterface ret = getFromCache(u, uid, row);
		if (ret != null) {
			return ret;
		}

		final GroupInterface readableRow = readableGroup(u, uid);
		ret = select(readableRow);
		return ret;
	}

	/**
	 * This retrieves a group from DB for the requesting user, accordingly to
	 * conditions. Group access rights are checked.
	 * 
	 * @param u
	 *            is the requesting user
	 * @param conditions
	 *            restrict selected rows
	 * @since 5.8.0
	 */
	public GroupInterface group(UserInterface u, String conditions)
			throws IOException {
		final GroupInterface row = readableGroup(u);
		return selectOne(row, conditions);
	}

	/**
	 * This retrieves groups UID from DB for the requesting user.
	 * 
	 * @param u
	 *            is the requesting user
	 * @return a Collection of UID
	 * @since 5.8.0
	 */
	public Collection<UID> groupsUID(UserInterface u) throws IOException {
		return groupsUID(u, (String) null);
	}

	/**
	 * This retrieves groups UID for the requesting user. Group access rights
	 * are checked.
	 * 
	 * @param u
	 *            is the requesting user
	 * @param criterias
	 *            is the SQL criterias
	 * @return a Collection of UID
	 * @since 5.8.0
	 */
	public Collection<UID> groupsUID(UserInterface u, String criterias)
			throws IOException {
		final GroupInterface row = readableGroupUID(u);
		return selectUID(row, criterias);
	}

	/**
	 * This retrieves the groups cache size for the given user
	 * 
	 * @param u
	 *            is the requesting user
	 * @since 5.8.0
	 */
	public int groupSize(UserInterface u) throws IOException {
		try {
			groupsUID(u).size();
		} catch (final Exception e) {
		}
		return 0;
	}

	/**
	 * This creates a new readable host to retrieve from DB
	 * 
	 * @param u
	 *            is the requesting user
	 * @since 5.8.0
	 */
	private HostInterface readableHost(UserInterface u) throws IOException {
		if (u == null) {
			return null;
		}
		final SQLRequestReadable r = new SQLRequestReadable(
				HostInterface.TABLENAME, u, ColumnSelection.selectAll);
		return new HostInterface(r);
	}

	/**
	 * This creates a new readable host to retrieve from DB
	 * 
	 * @param u
	 *            is the requesting user
	 * @param uid
	 *            is the UID of the host to retrieve
	 * @since 5.8.0
	 */
	private HostInterface readableHost(UserInterface u, UID uid)
			throws IOException {
		if ((u == null) || (uid == null)) {
			return null;
		}
		final SQLRequestReadable r = new SQLRequestReadable(
				HostInterface.TABLENAME, u, ColumnSelection.selectAll, uid);
		return new HostInterface(r);
	}

	/**
	 * This creates a new readable host to retrieve hosts UID from DB
	 * 
	 * @param u
	 *            is the requesting user
	 * @since 5.8.0
	 */
	private HostInterface readableHostUID(UserInterface u) throws IOException {
		if (u == null) {
			return null;
		}
		final SQLRequestReadable r = new SQLRequestReadable(
				HostInterface.TABLENAME, u, ColumnSelection.selectUID);
		return new HostInterface(r);
	}

	/**
	 * This retrieves a host independently of access rights
	 * 
	 * @param uid
	 *            is the UID of the host to retrieve
	 * @since 5.8.0
	 */
	protected HostInterface host(UID uid) throws IOException {
		if (uid == null) {
			return null;
		}
		final HostInterface rowType = new HostInterface();
		final HostInterface ret = getFromCache(uid, rowType);
		if (ret != null) {
			return ret;
		}
		return select(rowType, uid);
	}

	/**
	 * This retrieves a host for the requesting user. Host access rights are
	 * checked.
	 * 
	 * @param u
	 *            is the requesting user
	 * @param uid
	 *            is the UID of the host to retrieve
	 * @since 5.8.0
	 */
	protected HostInterface host(UserInterface u, UID uid) throws IOException {

		if (uid == null) {
			return null;
		}
		final HostInterface row = new HostInterface();
		final HostInterface ret = getFromCache(u, uid, row);
		if (ret != null) {
			return ret;
		}
		final HostInterface readableRow = readableHost(u, uid);
		return select(readableRow);
	}

	/**
	 * This retrieves a host from DB for the requesting user, accordingly to
	 * conditions. Host access rights are checked.
	 * 
	 * @param u
	 *            is the requesting user
	 * @param conditions
	 *            restrict selected rows
	 * @return the last loaded row
	 * @since 5.8.0
	 */
	public HostInterface host(UserInterface u, String conditions)
			throws IOException {
		final HostInterface row = readableHost(u);
		return selectOne(row, conditions);
	}

	/**
	 * This retrieves hosts fot the requesting user. Host access rights are
	 * checked.
	 * 
	 * @param u
	 *            is the requesting user
	 * @return a Collection of workers
	 * @since 5.8.0
	 */
	public Collection<HostInterface> hosts(UserInterface u) throws IOException {
		final HostInterface row = readableHost(u);
		return selectAll(row);
	}

	/**
	 * This retrieves enumeration of Host from DB for the requesting suer. Host
	 * access rights are checked.
	 * 
	 * @param u
	 *            is the requesting user
	 * @param conditions
	 *            restrict selected rows
	 * @return a vector of Host
	 * @since 5.8.0
	 */
	public Collection<HostInterface> hosts(UserInterface u, String conditions)
			throws IOException {
		final HostInterface row = readableHost(u);
		return selectAll(row, conditions);
	}

	/**
	 * This retrieves hosts UID from DB for the requesting user. Host access
	 * rights are checked.
	 * 
	 * @param u
	 *            is the requesting user
	 * @return a Collection of UID
	 * @since 5.8.0
	 */
	public Collection<UID> hostsUID(UserInterface u) throws IOException {
		return hostsUID(u, (String) null);
	}

	/**
	 * This retrieves hosts UID from DB for the requesting user, according to
	 * conditions. Host access rights are checked.
	 * 
	 * @param u
	 *            is the requesting user
	 * @param conditions
	 *            restrict selected rows
	 * @return a Collection of UID
	 * @since 5.9.0
	 */
	public Collection<UID> hostsUID(UserInterface u, String conditions)
			throws IOException {
		final HostInterface row = readableHostUID(u);
		return selectUID(row, conditions);
	}

	/**
	 * This retrieves the hosts cache size
	 * 
	 * @param u
	 *            is the requesting user
	 * @since 5.8.0
	 */
	public int hostSize(UserInterface u) throws IOException {
		try {
			hostsUID(u).size();
		} catch (final Exception e) {
		}
		return 0;
	}

	/**
	 * This creates a new readable session to retrieve from DB
	 * 
	 * @param u
	 *            is the requesting user
	 * @since 5.8.0
	 */
	private SessionInterface readableSession(UserInterface u)
			throws IOException {
		final SQLRequestReadable r = new SQLRequestReadable(
				SessionInterface.TABLENAME, u, ColumnSelection.selectAll);
		return new SessionInterface(r);
	}

	/**
	 * This creates a new readable session to retrieve from DB
	 * 
	 * @param u
	 *            is the requesting user
	 * @param uid
	 *            is the UID of the session to retrieve
	 * @since 5.8.0
	 */
	private SessionInterface readableSession(UserInterface u, UID uid)
			throws IOException {
		if (uid == null) {
			return null;
		}
		final SQLRequestReadable r = new SQLRequestReadable(
				SessionInterface.TABLENAME, u, ColumnSelection.selectAll, uid);
		return new SessionInterface(r);
	}

	/**
	 * This creates a new readable session to retrieve sessions UID from DB
	 * 
	 * @param u
	 *            is the requesting user
	 * @since 5.8.0
	 */
	private SessionInterface readableSessionUID(UserInterface u)
			throws IOException {
		final SQLRequestReadable r = new SQLRequestReadable(
				SessionInterface.TABLENAME, u, ColumnSelection.selectUID);
		return new SessionInterface(r);
	}

	/**
	 * This retrieves a session for the requesting user. Session access rights
	 * are checked.
	 * 
	 * @param u
	 *            is the requesting user
	 * @param uid
	 *            is the UID of the session to retrieve
	 * @since 5.8.0
	 */
	protected SessionInterface session(UserInterface u, UID uid)
			throws IOException, AccessControlException {

		if (uid == null) {
			return null;
		}
		final SessionInterface row = new SessionInterface();
		final SessionInterface ret = getFromCache(u, uid, row);
		if (ret != null) {
			return ret;
		}
		final SessionInterface readableRow = readableSession(u, uid);
		return select(readableRow);
	}

	/**
	 * This retrieves a session from DB for the requesting user according to
	 * conditions. Session access rights are checked.
	 * 
	 * @param u
	 *            is the requesting user
	 * @param conditions
	 *            restrict selected rows
	 * @return the last loaded row
	 * @since 5.8.0
	 */
	protected SessionInterface session(UserInterface u, String conditions)
			throws IOException {
		final SessionInterface row = readableSession(u);
		return selectOne(row);
	}

	/**
	 * This retrieves a session from DB for the requesting user. Session access
	 * rights are checked.
	 * 
	 * @param u
	 *            is the requesting user
	 * @return a Collection of sessions
	 * @since 5.8.0
	 */
	protected Collection<SessionInterface> sessions(UserInterface u)
			throws IOException {
		final SessionInterface row = readableSession(u);
		return selectAll(row);
	}

	/**
	 * This retrieves a session from DB for the requesting user according to
	 * criteria. Session access rights are checked.
	 * 
	 * @param u
	 *            is the requesting user
	 * @param criteria
	 *            restrict selected rows
	 * @return a Collection of sessions
	 * @since 5.8.0
	 */
	protected Collection<SessionInterface> sessions(UserInterface u,
			String criteria) throws IOException {
		final SessionInterface row = readableSession(u);
		return selectAll(row, criteria);
	}

	/**
	 * This retrieves session UID from DB for the requesting user. Session
	 * access rights are checked.
	 * 
	 * @param u
	 *            is the requesting user
	 * @return a Collection of UID
	 * @since 5.8.0
	 */
	protected Collection<UID> sessionsUID(UserInterface u) throws IOException {
		return sessionsUID(u, (String) null);
	}

	/**
	 * This retrieves a session from DB for the requesting user according to
	 * criteria. Session access rights are checked.
	 * 
	 * @param u
	 *            is the requesting user
	 * @return a Collection of UID
	 * @since 5.8.0
	 */
	protected Collection<UID> sessionsUID(UserInterface u, String criterias)
			throws IOException {
		final SessionInterface row = readableSessionUID(u);
		return selectUID(row, criterias);
	}

	/**
	 * This retrieves the number of sessions
	 * 
	 * @param u
	 *            is the requesting user
	 * @return how many sessions exist
	 * @since 5.8.0
	 */
	protected int sessionSize(UserInterface u) throws IOException {
		try {
			sessionsUID(u).size();
		} catch (final Exception e) {
		}
		return 0;
	}

	/**
	 * This creates a new readable task to retrieve from DB
	 * 
	 * @param u
	 *            is the requesting user
	 * @since 5.8.0
	 */
	private TaskInterface readableTask(UserInterface u) throws IOException {
		final SQLRequestReadable r = new SQLRequestReadable(
				TaskInterface.TABLENAME, u, ColumnSelection.selectAll);
		return new TaskInterface(r);
	}

	/**
	 * This creates a new readable task to retrieve from DB
	 * 
	 * @param u
	 *            is the requesting user
	 * @param uid
	 *            is the UID of the task to retrieve
	 * @since 5.8.0
	 */
	private TaskInterface readableTask(UserInterface u, UID uid)
			throws IOException {
		if (uid == null) {
			return null;
		}
		final SQLRequestReadable r = new SQLRequestReadable(
				TaskInterface.TABLENAME, u, ColumnSelection.selectAll, uid);
		return new TaskInterface(r);
	}

	/**
	 * This creates a new readable task to retrieve tasks UID from DB
	 * 
	 * @param u
	 *            is the requesting user
	 * @since 5.8.0
	 */
	private TaskInterface readableTaskUID(UserInterface u) throws IOException {
		final SQLRequestReadable r = new SQLRequestReadable(
				TaskInterface.TABLENAME, u, ColumnSelection.selectUID);
		return new TaskInterface(r);
	}

	/**
	 * This retrieves a task with its uid. This first looks in cache, then in
	 * DB. Access rights are not checked.
	 * 
	 * @param uid
	 *            is the UID of the task to retrieve
	 * @return a task or null
	 * @since 5.8.0
	 */
	protected TaskInterface task(UID uid) throws IOException {
		if (uid == null) {
			return null;
		}
		final TaskInterface rowType = new TaskInterface();
		final TaskInterface ret = getFromCache(uid, rowType);
		if (ret != null) {
			return ret;
		}
		return select(rowType, uid);
	}

	/**
	 * This retrieves a Collection of tasks with the status. Access rights are
	 * not checked.
	 * 
	 * @param status
	 *            is the status of the task to retrieve
	 * @return a Collection of tasks or null
	 * @since 5.8.0
	 */
	protected Collection<TaskInterface> tasks(StatusEnum status)
			throws IOException {
		if (status == null) {
			return null;
		}
		return tasks(TaskInterface.Columns.STATUS + "='" + status + "'");
	}

	/**
	 * This retrieves a vector of tasks for a given work Access rights are not
	 * checked.
	 * 
	 * @param work
	 *            is the work we look for
	 * @return a Collection of tasks or null
	 * @since 8.0.0
	 */
	protected Collection<TaskInterface> tasks(WorkInterface work)
			throws IOException {
		if ((work == null) || (work.getUID() == null)) {
			return null;
		}
		return tasks(TaskInterface.Columns.WORKUID + "='" + work.getUID() + "'");
	}

	/**
	 * This retrieves the first task for a given work and a given worker Access
	 * rights are not checked.
	 * 
	 * @param work
	 *            is the work we look for
	 * @param host
	 *            is the worker we look for
	 * @return a task or null
	 * @since 8.0.0
	 */
	protected TaskInterface task(final WorkInterface work, final HostInterface host)
			throws IOException {
		if ((work == null) || (work.getUID() == null) || (host == null)) {
			return null;
		}
		return task(TaskInterface.Columns.WORKUID + "='" + work.getUID()
				+ "' and " + TaskInterface.Columns.HOSTUID + "='"
				+ host.getUID() + "'");
	}

	/**
	 * This retrieves the first task for a given work and a given status
	 * 
	 * @param work
	 *            is the work we look for
	 * @param status
	 *            is the status we look for
	 * @return a task or null if none
	 * @since 8.0.0
	 */
	protected TaskInterface task(WorkInterface work, StatusEnum status)
			throws IOException {
		if ((work == null) || (work.getUID() == null) || (status == null)) {
			return null;
		}
		return task(TaskInterface.Columns.WORKUID + "='" + work.getUID()
				+ "' and " + TaskInterface.Columns.STATUS + "='" + status + "'");
	}

	/**
	 * This retrieves the first task for a given work
	 * 
	 * @param work
	 *            is the work we look for
	 * @return a task or null
	 * @since 8.0.0
	 */
	protected TaskInterface task(WorkInterface work) throws IOException {
		if ((work == null) || (work.getUID() == null)) {
			return null;
		}
		return task(TaskInterface.Columns.WORKUID + "='" + work.getUID() + "'");
	}

	/**
	 * This retrieves a vector of tasks with the status, independently of access
	 * rights
	 * 
	 * @param criteria
	 *            is the given criteria
	 * @return a Collection of tasks or null
	 * @since 5.8.0
	 */
	protected Collection<TaskInterface> tasks(String criteria)
			throws IOException {
		final TaskInterface row = new TaskInterface();
		return selectAll(row, criteria);
	}

	/**
	 * This retrieves a vector of tasks with the status, independently of access
	 * rights
	 * 
	 * @return a Collection of tasks or null
	 * @since 5.8.0
	 */
	protected Collection<TaskInterface> tasks() throws IOException {
		return tasks((String) null);
	}

	/**
	 * This retrieves a task for the requesting user. This first looks in cache,
	 * then in DB. Access rights are checked.
	 * 
	 * @param u
	 *            is the requesting user
	 * @param uid
	 *            is the UID of the task to retrieve
	 * @return a task or null
	 * @since 5.8.0
	 */
	protected TaskInterface task(UserInterface u, UID uid) throws IOException,
	AccessControlException {

		if (uid == null) {
			return null;
		}
		final TaskInterface row = new TaskInterface();
		final TaskInterface ret = getFromCache(u, uid, row);
		if (ret != null) {
			return ret;
		}
		final TaskInterface  readableRow = readableTask(u, uid);
		return select(readableRow);
	}

	/**
	 * This retrieves the first found task accordingly to conditions. Access
	 * rights are checked.
	 * 
	 * @param u
	 *            is the requesting user
	 * @param conditions
	 *            restrict selected rows
	 * @return the first found task
	 * @since 5.8.0
	 */
	protected TaskInterface task(UserInterface u, String conditions)
			throws IOException {
		final TaskInterface row = readableTask(u);
		return selectOne(row, conditions);
	}

	/**
	 * This retrieves the first found task accordingly to conditions Access
	 * rights are not checked.
	 * 
	 * @param conditions
	 *            restrict selected rows
	 * @return the first found task
	 * @since 5.8.0
	 */
	protected TaskInterface task(String conditions) throws IOException {
		final TaskInterface row = new TaskInterface();
		return selectOne(row, conditions);
	}

	/**
	 * This retrieves works UIDs for the specified client. Since 7.0.0 non
	 * privileged users get their own jobs only
	 * 
	 * @param client
	 *            is the ClientInterface, describing the client
	 * @return null on error; a Collection of UID otherwise
	 * @exception IOException
	 *                is thrown general error
	 * @exception InvalidKeyException
	 *                is thrown on credential error
	 * @exception AccessControlException
	 *                is thrown on access rights violation
	 */
	public Collection<UID> getTaskUids(UserInterface client)
			throws IOException, InvalidKeyException, AccessControlException {

		final UserInterface theClient = checkClient(client,
				UserRightEnum.LISTJOB);

		if (theClient.getRights() == UserRightEnum.WORKER_USER) {
			throw new AccessControlException(client.getLogin()
					+ " : a worker can not list tasks");
		}

		return tasksUID(theClient);
	}

	/**
	 * This retrieves tasks UID
	 * 
	 * @param u
	 *            is the requesting user
	 * @return a Collection of UID
	 * @since 5.8.0
	 */
	protected Collection<UID> tasksUID(UserInterface u) throws IOException {
		final TaskInterface row = readableTaskUID(u);
		return selectUID(row);
	}

	/**
	 * This retrieves the amount of tasks
	 * 
	 * @param u
	 *            is the requesting user
	 * @return the amount of found tasks
	 * @since 5.8.0
	 */
	protected int taskSize(UserInterface u) throws IOException {
		try {
			tasksUID(u).size();
		} catch (final Exception e) {
		}
		return 0;
	}

	/**
	 * This retrieves all tasks
	 * 
	 * @param u
	 *            is the requesting user
	 * @return a Collection of tasks
	 * @since 5.8.0
	 */
	protected Collection<TaskInterface> tasks(UserInterface u)
			throws IOException {
		final TaskInterface row = readableTask(u);
		return selectAll(row);
	}

	/**
	 * This retrieves all tasks according to criterias
	 * 
	 * @param u
	 *            is the requesting user
	 * @param criteria
	 *            is the SQL criteria
	 * @return a Collection of tasks
	 * @since 5.8.0
	 */
	protected Collection<TaskInterface> tasks(UserInterface u, String criteria)
			throws IOException {
		final TaskInterface row = readableTask(u);
		return selectAll(row, criteria);
	}

	/**
	 * This creates a new readable trace to retrieve from DB
	 * 
	 * @param u
	 *            is the requesting user
	 * @since 5.8.0
	 */
	private TraceInterface readableTrace(UserInterface u) throws IOException {
		final SQLRequestReadable r = new SQLRequestReadable(
				TraceInterface.TABLENAME, u, ColumnSelection.selectAll);
		return new TraceInterface(r);
	}

	/**
	 * This creates a new readable trace to retrieve from DB
	 * 
	 * @param u
	 *            is the requesting user
	 * @param uid
	 *            is the UID of the trace to retrieve
	 * @since 5.8.0
	 */
	private TraceInterface readableTrace(UserInterface u, UID uid)
			throws IOException {
		if (uid == null) {
			return null;
		}
		final SQLRequestReadable r = new SQLRequestReadable(
				TraceInterface.TABLENAME, u, ColumnSelection.selectAll, uid);
		return new TraceInterface(r);
	}

	/**
	 * This creates a new readable trace to retrieve traces UID from DB
	 * 
	 * @param u
	 *            is the requesting user
	 * @since 5.8.0
	 */
	private TraceInterface readableTraceUID(UserInterface u) throws IOException {
		final SQLRequestReadable r = new SQLRequestReadable(
				TraceInterface.TABLENAME, u, ColumnSelection.selectUID);
		return new TraceInterface(r);
	}

	/**
	 * This retrieves a trace
	 * 
	 * @param u
	 *            is the requesting user
	 * @param uid
	 *            is the UID of the trace to retrieve
	 * @since 5.8.0
	 */
	protected TraceInterface trace(UserInterface u, UID uid) throws IOException {
		if (uid == null) {
			return null;
		}
		final TraceInterface row = readableTrace(u, uid);
		return select(row);
	}

	/**
	 * This retrieves an trace accordingly to conditions
	 * 
	 * @param u
	 *            is the requesting user
	 * @param conditions
	 *            restrict selected rows
	 * @return the last loaded row
	 * @since 5.8.0
	 */
	protected TraceInterface trace(UserInterface u, String conditions)
			throws IOException {
		final TraceInterface row = readableTrace(u);
		return selectOne(row, conditions);
	}

	/**
	 * This retrieves enumeration of Trace stored UIDs
	 * 
	 * @param u
	 *            is the requesting user
	 * @return a Collection of the Trace stored UIDs
	 * @since 5.8.0
	 */
	protected Collection<TraceInterface> traces(UserInterface u)
			throws IOException {
		final TraceInterface row = readableTrace(u);
		return selectAll(row);
	}

	/**
	 * This retrieves traces UID
	 * 
	 * @param u
	 *            is the requesting user
	 * @return a Collection of UID
	 * @since 5.8.0
	 */
	protected Collection<UID> tracesUID(UserInterface u) throws IOException {
		final TraceInterface row = readableTraceUID(u);
		return selectUID(row);
	}

	/**
	 * This retrieves the traces cache size
	 * 
	 * @param u
	 *            is the requesting user
	 * @since 5.8.0
	 */
	protected int traceSize(UserInterface u) throws IOException {
		try {
			tracesUID(u).size();
		} catch (final Exception e) {
		}
		return 0;
	}

	/**
	 * This creates a new readable usergroup to retrieve from DB
	 * 
	 * @param u
	 *            is the requesting user
	 * @since 5.8.0
	 */
	private UserGroupInterface readableUserGroup(UserInterface u)
			throws IOException {
		final SQLRequestReadable r = new SQLRequestReadable(
				UserGroupInterface.TABLENAME, u, ColumnSelection.selectAll);
		return new UserGroupInterface(r);
	}

	/**
	 * This creates a new readable usergroup to retrieve from DB
	 * 
	 * @param u
	 *            is the requesting user
	 * @param uid
	 *            is the UID of the usergroup to retrieve
	 * @since 5.8.0
	 */
	private UserGroupInterface readableUserGroup(UserInterface u, UID uid)
			throws IOException {
		if (uid == null) {
			return null;
		}
		final SQLRequestReadable r = new SQLRequestReadable(
				UserGroupInterface.TABLENAME, u, ColumnSelection.selectAll, uid);
		return new UserGroupInterface(r);
	}

	/**
	 * This creates a new readable usergroup to retrieve usergroups UID from DB
	 * 
	 * @param u
	 *            is the requesting user
	 * @since 5.8.0
	 */
	private UserGroupInterface readableUserGroupUID(UserInterface u)
			throws IOException {
		final SQLRequestReadable r = new SQLRequestReadable(
				UserGroupInterface.TABLENAME, u, ColumnSelection.selectUID);
		return new UserGroupInterface(r);
	}

	/**
	 * This retrieves a UserInterface group for the requesting user. This first
	 * looks in cache, then in DB. Access rights are checked.
	 * 
	 * @param u
	 *            is the requesting user
	 * @param uid
	 *            is the UID
	 * @since 5.8.0
	 */
	protected UserGroupInterface usergroup(UserInterface u, UID uid)
			throws IOException, AccessControlException {

		if (uid == null) {
			return null;
		}
		final UserGroupInterface row = new UserGroupInterface();
		final UserGroupInterface ret = getFromCache(u, uid, row);
		if (ret != null) {
			return ret;
		}
		final UserGroupInterface readableRow = readableUserGroup(u, uid);
		return select(readableRow);
	}

	/**
	 * This retrieves a UserInterface group from DB for the requesting user.
	 * Access rights are checked.
	 * 
	 * @param u
	 *            is the requesting user
	 * @param conditions
	 *            restrict selected rows
	 * @return the last loaded row
	 * @since 5.8.0
	 */
	protected UserGroupInterface usergroup(UserInterface u, String conditions)
			throws IOException {
		final UserGroupInterface row = readableUserGroup(u);
		return selectOne(row, conditions);
	}

	/**
	 * This retrieves a UserInterface group. This first looks in cache, then in
	 * DB. Access rights are not checked.
	 * 
	 * @param uid
	 *            is the UID
	 * @since 5.8.0
	 */
	protected UserGroupInterface usergroup(UID uid) throws IOException {

		if (uid == null) {
			return null;
		}
		final UserGroupInterface rowType = new UserGroupInterface();
		final UserGroupInterface ret = getFromCache(uid, rowType);
		if (ret != null) {
			return ret;
		}
		return select(rowType, uid);
	}

	/**
	 * This retrieves UserInterface groups form DB for the requesting user.
	 * Access rights are checked.
	 * 
	 * @param u
	 *            is the requesting user
	 * @return a Collection of the UserGroup stored UIDs
	 */
	protected Collection<UserGroupInterface> usergroups(UserInterface u)
			throws IOException {
		final UserGroupInterface row = readableUserGroup(u);
		return selectAll(row);
	}

	/**
	 * This retrieves UserInterface groups form DB for the requesting user.
	 * Access rights are checked.
	 * 
	 * @param u
	 *            is the requesting user
	 * @return a Collection of UID
	 * @since 5.8.0
	 */
	protected Collection<UID> usergroupsUID(UserInterface u) throws IOException {
		final UserGroupInterface row = readableUserGroupUID(u);
		return selectUID(row);
	}

	/**
	 * This retrieves the UserInterface groups cache size
	 * 
	 * @param u
	 *            is the requesting user
	 * @since 5.8.0
	 */
	protected int usergroupSize(UserInterface u) throws IOException {
		try {
			usergroupsUID(u).size();
		} catch (final Exception e) {
		}
		return 0;
	}

	/**
	 * This creates a new readable UserInterface to retrieve from DB
	 * 
	 * @param u
	 *            is the requesting user
	 * @since 5.8.0
	 */
	private UserInterface readableUser(UserInterface u) throws IOException {
		final SQLRequestReadable r = new SQLRequestReadable(
				UserInterface.TABLENAME, u, ColumnSelection.selectAll);
		return new UserInterface(r);
	}

	/**
	 * This creates a new readable UserInterface to retrieve from DB
	 * 
	 * @param u
	 *            is the requesting user
	 * @param uid
	 *            is the UID of the UserInterface to retrieve
	 * @since 5.8.0
	 */
	private UserInterface readableUser(UserInterface u, UID uid)
			throws IOException {
		if (uid == null) {
			return null;
		}
		final SQLRequestReadable r = new SQLRequestReadable(
				UserInterface.TABLENAME, u, ColumnSelection.selectAll, uid);
		return new UserInterface(r);
	}

	/**
	 * This creates a new readable UserInterface to retrieve users UID from DB
	 * 
	 * @param u
	 *            is the requesting user
	 * @since 5.8.0
	 */
	private UserInterface readableUserUID(UserInterface u) throws IOException {
		final SQLRequestReadable r = new SQLRequestReadable(
				UserInterface.TABLENAME, u, ColumnSelection.selectUID);
		return new UserInterface(r);
	}

	/**
	 * This retrieves an UserInterface with the given criteria, independently of
	 * access rights
	 * 
	 * @param criteria
	 *            aims to select the right user
	 * @return the found UserInterface or null
	 * @since 5.8.0
	 */
	protected UserInterface user(String criteria) throws IOException {
		final UserInterface row = new UserInterface();
		return select(row, criteria);
	}

	/**
	 * This retrieves users for the requesting user. This first looks in cache,
	 * then in DB. Access rights are not checked.
	 * 
	 * @param uid
	 *            is the UID
	 * @since 5.8.0
	 */
	protected UserInterface user(UID uid) throws IOException {

		if (uid == null) {
			return null;
		}
		final UserInterface rowType = new UserInterface();
		final UserInterface ret = getFromCache(uid, rowType);
		if (ret != null) {
			return ret;
		}
		return select(rowType, uid);
	}

	/**
	 * This retrieves users for the requesting user. This first looks in cache,
	 * then in DB. Access rights are checked.
	 * 
	 * @param u
	 *            is the requesting user
	 * @param uid
	 *            is the UID
	 * @since 5.8.0
	 */
	protected UserInterface user(UserInterface u, UID uid) throws IOException,
	AccessControlException {

		if (uid == null) {
			return null;
		}
		final UserInterface row = new UserInterface();
		final UserInterface ret = getFromCache(u, uid, row);
		if (ret != null) {
			return ret;
		}
		final UserInterface readableRow = readableUser(u, uid);
		return select(readableRow);
	}

	/**
	 * This retrieves an UserInterface from DB for the requesting user according
	 * to conditions. This first looks in cache, then in DB. Access rights are
	 * checked.
	 * 
	 * @param u
	 *            is the requesting user
	 * @param conditions
	 *            restrict selected rows
	 * @since 5.8.0
	 */
	protected UserInterface user(UserInterface u, String conditions)
			throws IOException {
		final UserInterface row = readableUser(u);
		return select(row, conditions);
	}

	/**
	 * This retrieves users from DB for the requesting user. Access rights are
	 * checked.
	 * 
	 * @param u
	 *            is the requesting user
	 * @return a Collection of UID
	 * @since 5.8.0
	 */
	protected Collection<UID> usersUID(UserInterface u) throws IOException {
		return usersUID(u, (String) null);
	}

	/**
	 * This retrieves users from DB for the requesting user, according to
	 * criteria. Access rights are checked.
	 * 
	 * @param u
	 *            is the requesting user
	 * @param criterias
	 *            filters users
	 * @return a Collection of UID
	 * @since 5.8.0
	 */
	protected Collection<UID> usersUID(UserInterface u, String criterias)
			throws IOException {
		final UserInterface row = readableUserUID(u);
		return selectUID(row, criterias);
	}

	/**
	 * This creates a new readable work to retrieve from DB
	 * 
	 * @param u
	 *            is the requesting work
	 * @since 5.8.0
	 */
	private WorkInterface readableWork(UserInterface u) throws IOException {
		final SQLRequestReadable r = new SQLRequestReadable(
				WorkInterface.TABLENAME, u, ColumnSelection.selectAll);
		return new WorkInterface(r);
	}

	/**
	 * This creates a new readable work to retrieve from DB
	 * 
	 * @param u
	 *            is the requesting work
	 * @param uid
	 *            is the UID of the work to retrieve
	 * @since 5.8.0
	 */
	private WorkInterface readableWork(UserInterface u, UID uid)
			throws IOException {
		if (uid == null) {
			return null;
		}
		final SQLRequestReadable r = new SQLRequestReadable(
				WorkInterface.TABLENAME, u, ColumnSelection.selectAll, uid);
		return new WorkInterface(r);
	}

	/**
	 * This creates a new readable work to retrieve works UID from DB,
	 * eventually according to works status
	 * 
	 * @param u
	 *            is the requesting work
	 * @param s
	 *            is the work status (e.g RUNNING, PENDING...)
	 * @since 8.2.0
	 */
	private WorkInterface readableWorkUID(UserInterface u, StatusEnum s)
			throws IOException {
		final SQLRequestReadable r = new SQLRequestReadable(
				WorkInterface.TABLENAME, u, ColumnSelection.selectUID,
				s != null ? WorkInterface.Columns.STATUS + " = '" + s + "'"
						: null);
		return new WorkInterface(r);
	}

	/**
	 * This calls readableWorkUID(u, null)
	 * 
	 * @see #readableWorkUID(UserInterface, String)
	 * @since 5.8.0
	 */
	private WorkInterface readableWorkUID(UserInterface u) throws IOException {
		return readableWorkUID(u, null);
	}

	/**
	 * This retrieves a work. This first looks in cache, then in DB. Access
	 * rights are not checked.
	 * 
	 * @param uid
	 *            is the UID of the task to retrieve
	 * @return a task or null
	 * @since 5.8.0
	 */
	protected WorkInterface work(UID uid) throws IOException {

		if (uid == null) {
			return null;
		}
		final WorkInterface rowType = new WorkInterface();
		final WorkInterface ret = getFromCache(uid, rowType);
		if (ret != null) {
			return ret;
		}
		return select(rowType, uid);
	}

	/**
	 * This retrieves a work from DB for the requesting user, according to
	 * conditions. Access rights are checked.
	 * 
	 * @param conditions
	 *            restrict selected rows
	 * @return the last loaded row
	 * @since 5.8.0
	 */
	protected WorkInterface work00(UserInterface u, String conditions)
			throws IOException {
		final WorkInterface row = readableWork(u);
		return selectOne(row, conditions);
	}

	/**
	 * This retrieves works from DB for the requesting user. Access rights are
	 * checked.
	 * 
	 * @param u
	 *            is the requesting user
	 * @return a Collection of works
	 * @since 5.8.0
	 */
	protected Collection<WorkInterface> works(UserInterface u)
			throws IOException {
		final WorkInterface row = readableWork(u);
		return selectAll(row);
	}

	/**
	 * This retrieves a work from DB for the requesting user. Access rights are
	 * checked.
	 * 
	 * @param u
	 *            is the requesting user
	 * @return a vector of works
	 * @since 5.8.0
	 */
	protected WorkInterface work(UserInterface u) throws IOException {
		final WorkInterface row = readableWork(u);
		return selectOne(row);
	}

	/**
	 * This retrieves readable works for the given user
	 * 
	 * @param u
	 *            is the requesting user
	 * @return a vector of works
	 * @since 5.8.0
	 */
	protected WorkInterface work(UserInterface u, UID uid) throws IOException,
	AccessControlException {

		if (uid == null) {
			return null;
		}
		final WorkInterface row = new WorkInterface();
		final WorkInterface ret = getFromCache(u, uid, row);
		if (ret != null) {
			return ret;
		}
		final WorkInterface readableRow = readableWork(u, uid);
		return select(readableRow);
	}

	/**
	 * This retrieves a vector of tasks with the status, independently of access
	 * rights
	 * 
	 * @return a Collection of tasks or null
	 * @since 5.8.0
	 */
	protected Collection<WorkInterface> works() throws IOException {
		final WorkInterface row = new WorkInterface();
		return selectAll(row);
	}

	/**
	 * This retrieves all works with the given status. This is used by scheduler
	 * 
	 * @see Scheduler#retrieve()
	 * @param s
	 *            is the expected status
	 * @return a Collection of works
	 * @since 5.8.0
	 */
	public Collection<WorkInterface> works(StatusEnum s) throws IOException {
		final SQLRequestWorkStatus r = new SQLRequestWorkStatus(s);
		final WorkInterface row = new WorkInterface(r);
		return selectAll(row);
	}

	/**
	 * This retrieves all works with the given status. This is used by scheduler
	 * 
	 * @see MatchingScheduler#retrieve()
	 * @param s
	 *            is the expected status
	 * @param owneruid
	 *            is the uid of the owner
	 * @return a Collection of works
	 * @since 5.8.0
	 */
	public Collection<WorkInterface> works(StatusEnum s, UID owneruid)
			throws IOException {
		if (owneruid == null) {
			return null;
		}
		final SQLRequestWorkStatus r = new SQLRequestWorkStatus(s, owneruid);
		final WorkInterface row = new WorkInterface(r);
		return selectAll(row);
	}

	/**
	 * This retrieves UID of readable works for the given user
	 * 
	 * @param u
	 *            is the requesting user
	 * @return a Collection of UID
	 * @since 5.8.0
	 */
	protected Collection<UID> worksUID(UserInterface u) throws IOException {
		return worksUID(u, (String) null);
	}

	/**
	 * This retrieves a collection of readable work UID for the given user,
	 * given work status
	 * 
	 * @param u
	 *            is the requesting user
	 * @param s
	 *            is the status of the works to retrieve
	 * @return a Collection of UID
	 * @since 8.1.2
	 */
	protected Collection<UID> worksUID(UserInterface u, StatusEnum s)
			throws IOException {
		final WorkInterface row = readableWorkUID(u, s);
		return selectUID(row);
	}

	/**
	 * This retrieves UID of readable works for the given user, according to
	 * criteria
	 * 
	 * @param u
	 *            is the requesting user
	 * @param criterias
	 *            filters works
	 * @return a Collection of UID
	 * @since 5.8.0
	 */
	protected Collection<UID> worksUID(UserInterface u, String criterias)
			throws IOException {
		final WorkInterface row = readableWorkUID(u);
		return selectUID(row, criterias);
	}

	/**
	 * This retrieves works UID owned by the given user
	 * 
	 * @param u
	 *            is the requesting user
	 * @return a Collection of UID
	 * @since 7.0.0
	 */
	protected Collection<UID> ownerWorksUID(UserInterface u) throws IOException {
		final WorkInterface row = readableWorkUID(u);
		return selectUID(row, "maintable.owneruid='" + u.getUID() + "'");
	}

	/**
	 * This retrieves replica UID for the given work
	 * 
	 * @param u
	 *            is the requesting user
	 * @param originalUid is the UID of the replicated work
	 * @return a Collection of UID
	 * @since 10.0.0
	 */
	protected Collection<UID> replicasUID(UserInterface u, final UID originalUid) throws IOException {
		final WorkInterface row = readableWorkUID(u);
		return selectUID(row, "maintable.replicateduid='" + originalUid + "'");
	}

	/**
	 * This retrieves works UID owned by the given user
	 * 
	 * @param u
	 *            is the requesting user
	 * @return a Collection of UID
	 * @since 7.0.0
	 */
	protected Collection<UID> ownerWorksUID(UserInterface u, StatusEnum s)
			throws IOException {
		final WorkInterface row = readableWorkUID(u, s);
		return selectUID(row, "maintable.owneruid='" + u.getUID() + "'");
	}

	/**
	 * This tests whether client has the right to connect to this server and to
	 * do the expected action, accordingly to the action level.<br />
	 * Since 7.0.0 two credential types are accepted : login/password (as it
	 * used to be) and also X509 certificate. The certificate must be provided
	 * in the client certificate attribute. This is accepted if and only if the
	 * Dispatcher#proxyValidator attribute is not null. The certificate is then
	 * verified by Dispatcher#proxyValidator. If the validation is successfully
	 * passed, the certificate is stored in DB so that the bridge can use it.
	 * 
	 * Since 7.0.0 any UserInterface can connect using its certificate without
	 * being registered. As soon as the dispatcher can validate the
	 * UserInterface certificate against its CA certificates path. If the
	 * certificate is validated through certificates path, a new UserInterface
	 * is inserted into the database, with STANDARD_UserInterface rights and its
	 * certificate concat(subjectDN, issuerDN) as login. Keep in mind that login
	 * may be truncated; please see UserIntergace.USERLOGINLENGTH.
	 * 
	 * @param client
	 *            describes the client that is connecting
	 * @param actionLevel
	 *            is the right level
	 * @return a UserInterface object if the client provides a known
	 *         (UserInterface id/password) and if that UserInterface has
	 *         sufficient privilege level; null otherwise.
	 * @exception IOException
	 *                is thrown on DB access or I/O error
	 * @exception InvalidKeyException
	 *                is thrown on client integrity error (user unknown, bad
	 *                password...)
	 * @exception AccessControlException
	 *                is thrown if client does not have enough rights
	 * @see Dispatcher#proxyValidator
	 * @see xtremweb.common.UserInterface#USERLOGINLENGTH
	 */
	protected UserInterface checkClient(UserInterface client,
			UserRightEnum actionLevel) throws IOException, InvalidKeyException,
			AccessControlException {

		logger.finest("checkClient(" + client.toXml() + ", " + actionLevel
				+ ")");

		if ((client == null) || (actionLevel == null)) {
			throw new IOException("Can't check : client is null");
		}

		UserInterface result = null;

		final String certificate = client.getCertificate();

		if ((certificate != null) && (Dispatcher.getProxyValidator() == null)) {
			throw new AccessControlException(
					"Server config : server can't check certificate");
		}
		if ((certificate != null) && (Dispatcher.getProxyValidator() != null)) {

			X509Proxy proxy = null;
			ByteArrayInputStream is = null;
			String subjectName = null;
			String issuerName = null;
			String loginName = null;
			byte[] strb = null;

			try {
				logger.finest(certificate);

				strb = certificate.getBytes();
				is = new ByteArrayInputStream(strb);
				proxy = new X509Proxy(is);
				Dispatcher.getProxyValidator().validate(proxy);
				subjectName = proxy.getSubjectName();
				issuerName = proxy.getIssuerName();

				loginName = subjectName + "_" + issuerName;
			} catch (final Exception e) {
				loginName = null;
				proxy = null;
				logger.exception("Certificate validation failure", e);
				throw new InvalidKeyException(subjectName
						+ " : certificate validation faliled");
			} finally {
				strb = null;
				is = null;
				subjectName = null;
				issuerName = null;
			}

			UID useruid = null;
			UserInterface newUserItf = null;
			String random = null;
			String md5hex = null;
			MD5 md5 = null;
			try {
				client.setLogin(loginName);
				if (client.getUID() != null) {
					result = user(client.getUID());
				}
				if (result == null) {
					result = user(UserInterface.Columns.LOGIN.toString()
							+ "= '" + client.getLogin() + "'");
				}
				if (result == null) {
					if (config.getAdminUid() == null) {
						throw new IOException("admin.uid is not set");
					}
					useruid = new UID();
					newUserItf = new UserInterface(useruid);
					newUserItf.setRights(UserRightEnum.STANDARD_USER);
					random = subjectName + Math.random();
					strb = random.getBytes();
					md5 = new MD5(strb);
					md5hex = md5.asHex();
					newUserItf.setOwner(config.getAdminUid());
					newUserItf.setLogin(loginName);
					newUserItf.setPassword(md5hex);
					newUserItf.setEMail("unknown");
					newUserItf.setCertificate(certificate);
					result = new UserInterface(newUserItf);
				}
			} catch (final Exception e) {
				logger.exception("Cant insert new user", e);
				throw new IOException("Cant insert new UserInterface "
						+ loginName);
			} finally {
				strb = null;
				loginName = null;
				random = null;
				md5 = null;
				md5hex = null;
				proxy = null;
				useruid = null;
				newUserItf = null;
				proxy = null;
			}
		}

		if (result == null) {
			if ((client.getPassword() == null) || (client.getLogin() == null)) {
				result = null;
			} else {
				if (client.getUID() != null) {
					result = user(client.getUID());
				}
				if (result == null) {
					result = user(UserInterface.Columns.LOGIN.toString()
							+ "= '" + client.getLogin() + "' AND "
							+ UserInterface.Columns.PASSWORD.toString() + "= '"
							+ client.getPassword() + "'");
				}
				if ((result != null)
						&& ((result.getPassword().compareTo(
								client.getPassword()) != 0) || (result
										.getLogin().compareTo(client.getLogin()) != 0))) {
					result = null;
				}
			}
		}

		if (result == null) {
			throw new InvalidKeyException("unknown user or bad login/password");
		}

		if (result.getRights() == null) {
			result = null;
			throw new AccessControlException(client.getLogin()
					+ " : has no right");
		}

		if (result.getRights().lowerThan(actionLevel)) {
			result = null;
			throw new AccessControlException(client.getLogin()
					+ " : not enough rights to " + actionLevel);
		}

		return result;
	}

	/**
	 * This calls remove(client, uri.getUID());
	 * 
	 * @see #remove(UserInterface, UID)
	 */
	public boolean remove(UserInterface client, URI uri) throws IOException,
	InvalidKeyException, AccessControlException {
		if (uri == null) {
			return false;
		}
		return remove(client, uri.getUID());
	}

	/**
	 * This removes an object from cache and DB Objects are parsed in the most
	 * probable order, to improve performances.
	 * 
	 * @param uid
	 *            is the UID of the object to remove
	 * @return true on success, false otherwise
	 * @exception IOException
	 *                is thrown on DB access or I/O error
	 * @exception InvalidKeyException
	 *                is thrown on client integrity error (user unknown, bad
	 *                password...)
	 * @exception AccessControlException
	 *                is thrown if client does not have enough rights
	 */
	public boolean remove(UserInterface client, UID uid) throws IOException,
	InvalidKeyException, AccessControlException {

		final UserInterface theClient = checkClient(client,
				UserRightEnum.STANDARD_USER);

		if (theClient.getRights() == UserRightEnum.WORKER_USER) {
			throw new AccessControlException(theClient.getLogin()
					+ " : a worker can not remove anything");
		}
		if (uid == null) {
			logger.debug("DBInterface#remove : UID is null");
			return false;
		}

		if (deleteJob(theClient, uid)) {
			logger.finest("job deleted " + uid);
			return true;
		}
		if (removeData(theClient, uid)) {
			logger.finest("data deleted " + uid);
			return true;
		}
		if (removeSession(theClient, uid)) {
			logger.finest("session deleted " + uid);
			return true;
		}
		if (removeGroup(theClient, uid)) {
			logger.finest("group deleted " + uid);
			return true;
		}
		if (removeTask(theClient, uid)) {
			logger.finest("task deleted " + uid);
			return true;
		}

		final AppInterface theApp = app(theClient, uid);
		boolean ret = false;
		if (theApp != null) {

			final UserInterface owner = user(theApp.getOwner());

			final UID ownerGroup = (owner == null ? null : owner.getGroup());

			ret = removeApplication(theClient, theApp, ownerGroup);
			if (ret) {
				logger.finest("app deleted " + uid);
				return true;
			}
		}

		if (removeUser(theClient, uid)) {
			logger.finest("UserInterface deleted " + uid);
			return true;
		}

		if (removeUserGroup(theClient, uid)) {
			logger.finest("usergroup deleted " + uid);
			return true;
		}

		throw new IOException("Object not found");
	}

	/**
	 * This calls chmod(client, uri.getUID(), chmodstr);
	 * 
	 * @see #chmod(UserInterface, UID, String)
	 */
	public boolean chmod(UserInterface client, URI uri, String chmodstr)
			throws IOException, InvalidKeyException, AccessControlException,
			ParseException {
		if (uri == null) {
			throw new IOException("uri not set");
		}
		return chmod(client, uri.getUID(), chmodstr);
	}

	/**
	 * This changes access rights of the object noted by the provided UID
	 * Objects are parsed in the most probable order, to improve performances.
	 * 
	 * @param client
	 *            is the requesting client
	 * @param uid
	 *            is the UID of the object to chmod
	 * @param chmodstr
	 *            represents access rights modifications (e.g. "0x777", "au+w"
	 *            etc.)
	 * @return true on success, false otherwise
	 * @exception ParseException
	 *                if chmodstr is invalid
	 * @exception IOException
	 *                is thrown on DB access or I/O error
	 * @exception InvalidKeyException
	 *                is thrown on client integrity error (user unknown, bad
	 *                password...)
	 * @exception AccessControlException
	 *                is thrown if client does not have enough rights
	 * @see XWAccessRights#chmod(String)
	 */
	public boolean chmod(UserInterface client, UID uid, String chmodstr)
			throws IOException, InvalidKeyException, AccessControlException,
			ParseException {

		final UserInterface theClient = checkClient(client,
				UserRightEnum.STANDARD_USER);
		if (theClient.getRights() == UserRightEnum.WORKER_USER) {
			throw new AccessControlException(theClient.getLogin()
					+ " : a worker cannot chmod");
		}
		if (uid == null) {
			throw new IOException("uid can't be null");
		}
		if (chmodstr == null) {
			throw new IOException("chmodstr can't be null");
		}

		Table theRow = null;
		UserRightEnum userrights = null;
		XWAccessRights rights = null;
		try {
			theRow = app(theClient, uid);
			userrights = UserRightEnum.INSERTAPP;
			if (theRow == null) {
				theRow = data(theClient, uid);
				userrights = UserRightEnum.ADVANCED_USER;
			}
			if (theRow == null) {
				theRow = usergroup(theClient, uid);
				userrights = UserRightEnum.SUPER_USER;
			}
			if (theRow == null) {
				theRow = user(theClient, uid);
				userrights = UserRightEnum.ADVANCED_USER;
			}
			if (theRow == null) {
				theRow = group(theClient, uid);
				userrights = UserRightEnum.ADVANCED_USER;
			}
			if (theRow == null) {
				theRow = session(theClient, uid);
				userrights = UserRightEnum.ADVANCED_USER;
			}
			if (theRow == null) {
				return false;
			}
			rights = theRow.getAccessRights();
			rights.chmod(chmodstr);
			theRow.setAccessRights(rights);
			update(theClient, userrights, theRow);

			return true;
		} finally {
			theRow = null;
			userrights = null;
			rights = null;
		}
	}

	/**
	 * This reads the applications table
	 */
	public final void updateAppsPool() {

		try {
			final Vector<AppInterface> apps = (Vector<AppInterface>) apps();
			if (apps == null) {
				logger.warn("Can't retrieve any app");
				return;
			}

			for (final Enumeration<AppInterface> enums = apps.elements(); enums
					.hasMoreElements();) {
				final AppInterface theApp = enums.nextElement();
				theApp.getName();
				theApp.isService();
			}
		} catch (final Exception e) {
			logger.exception(e);
		}

	}

	/**
	 * This set all server works to WAITING status and reload works
	 * 
	 * @param serverName
	 *            is the server name
	 */
	public void unlockWorks(String serverName) {
		try {
			DBConnPoolThread.getInstance().unlockWorks(serverName);
		} catch (final Exception e) {
			logger.exception(e);
		}
	}

	/**
	 * This set work to WAITING status
	 * 
	 * @param uid
	 *            is the work uid
	 */
	public void unlockWork(UID uid) {
		try {
			final WorkInterface work = work(uid);
			if (!work.isWaiting()) {
				work.setWaiting();
				update(work);
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This checks if client can read data and if so, increments data links and
	 * updates the data
	 * 
	 * @param theClient
	 *            is the client wishing to use data
	 * @param uri
	 *            is the uri of the data to use
	 * @return true if data can't be accessed
	 */
	private boolean useData(final UserInterface theClient, URI uri)
			throws IOException, InvalidKeyException {

		final Table row = getFromCache(theClient, uri);
		if (row == null) {
			return false;
		}

		final UserInterface owner = user(row.getOwner());
		final UID ownerGroup = (owner == null ? null : owner.getGroup());

		if ((row.canRead(theClient, ownerGroup) == false)
				&& (theClient.getRights().lowerThan(UserRightEnum.SUPER_USER))) {

			throw new AccessControlException(theClient.getLogin()
					+ " can't use " + uri);
		}

		final DataInterface theData = data(theClient, uri.getUID());
		theData.incLinks();
		update(theClient, UserRightEnum.STANDARD_USER, theData);
		return true;
	}

	/**
	 * This adds/updates a data according to data access rights. If client
	 * rights is higher or equals to WORKER_USER, data access rights can be
	 * bypassed. This allows worker to store a result for users on job
	 * completion. owner may be != theClient in case of updating.
	 * 
	 * @param client
	 *            identifies the client
	 * @param data
	 *            is the data definition
	 * @exception IOException
	 *                is thrown on DB access or I/O error
	 * @exception InvalidKeyException
	 *                is thrown on client integrity error (user unknown, bad
	 *                password...)
	 * @exception AccessControlException
	 *                is thrown if client does not have enough rights
	 */
	public void addData(UserInterface client, DataInterface data)
			throws IOException, InvalidKeyException, AccessControlException {

		if (data == null) {
			throw new IOException("data is null");
		}
		final UserInterface theClient = checkClient(client,
				UserRightEnum.INSERTDATA);
		final Date theDate = new java.util.Date();

		final UserInterface owner = user(data.getOwner());
		final UID ownerGroup = (owner == null) ? null : owner.getGroup();

		final DataInterface theData = data(theClient, data.getUID());
		if (theData != null) {

			if ((theData.canWrite(theClient, ownerGroup))
					|| (theClient.getRights()
							.higherOrEquals(UserRightEnum.WORKER_USER))) {

				theData.updateInterface(data);
				if (theData.getURI() == null) {
					try {
						theData.setURI(new URI(XWTools.getLocalHostName(),
								theData.getUID()));
					} catch (final URISyntaxException e) {
						logger.exception(e);
						logger.fatal(e.getMessage());
					}
				}
				update(theClient, UserRightEnum.INSERTDATA, theData);
			} else {
				logger.error(client.getLogin() + " can't update "
						+ data.getName());
				throw new AccessControlException(client.getLogin()
						+ " can't update " + data.getName());
			}

			return;
		}
		if (data.getUID() == null) {
			final UID uid = new UID();
			data.setUID(uid);
		}
		if (data.getOwner() == null) {
			data.setOwner(theClient.getUID());
		}
		if (data.getAccessRights() == null) {
			data.setAccessRights(XWAccessRights.DEFAULT);
		}
		if (data.getStatus() == null) {
			data.setStatus(StatusEnum.UNAVAILABLE);
		}
		data.getAccessRights(); // this set default value, if not set
		data.setInsertionDate(theDate);
		if (data.getType() == DataTypeEnum.X509) {
			data.setAccessRights(XWAccessRights.USERREADWRITE);
		}
		try {
			data.setURI(new URI(XWTools.getLocalHostName(), data.getUID()));
		} catch (final URISyntaxException e) {
			logger.exception(e);
			logger.fatal(e.getMessage());
		}

		data.setLinks(0);

		insert(data);
	}

	/**
	 * This retrieves data UIDs for the specified client. Since 7.0.0 non
	 * privileged users get their own data only
	 * 
	 * @param client
	 *            describes the client
	 * @return null on error; a Collection of UID otherwise
	 * @exception IOException
	 *                is thrown on DB access or I/O error
	 * @exception InvalidKeyException
	 *                is thrown on client integrity error (user unknown, bad
	 *                password...)
	 * @exception AccessControlException
	 *                is thrown if client does not have enough rights
	 */
	public Collection<UID> getDatas(UserInterface client) throws IOException,
	InvalidKeyException, AccessControlException {

		final UserInterface theClient = checkClient(client,
				UserRightEnum.LISTDATA);

		if (theClient.getRights() == UserRightEnum.WORKER_USER) {
			throw new AccessControlException(theClient.getLogin()
					+ " : a worker can not list datas");
		}

		if (theClient.getRights().higherOrEquals(UserRightEnum.ADVANCED_USER)) {
			return datasUID(theClient);
		}
		return ownerDatasUID(theClient);
	}

	/**
	 * This does not check the client integrity; this must have been done by the
	 * caller. This checks client rights is not lower than GETDATA and returns
	 * data(theClient, name)
	 * 
	 * @param client
	 *            is the requesting client
	 * @param uid
	 *            is the uid of the data to retrieve
	 * @return the found data or null
	 * @exception IOException
	 *                is thrown on DB access or I/O error
	 * @exception InvalidKeyException
	 *                is thrown on client integrity error (user unknown, bad
	 *                password...)
	 * @exception AccessControlException
	 *                is thrown if client does not have enough rights
	 * @see #data(UserInterface, UID)
	 */
	public DataInterface getData(UserInterface client, UID uid)
			throws IOException, InvalidKeyException, AccessControlException {

		final UserInterface theClient = checkClient(client,
				UserRightEnum.GETDATA);
		if (theClient.getRights().lowerThan(UserRightEnum.GETDATA)) {
			throw new AccessControlException(theClient.getLogin()
					+ " not enough rights to GETDATA");
		}
		return data(theClient, uid);
	}

	/**
	 * This retrieves the path where to store traces
	 * 
	 * @return the path
	 */
	public final String getTracesPath() {
		return new String(config.getProperty(XWPropertyDefs.HOMEDIR) + "Traces");
	}

	/**
	 * This retrieves users
	 * 
	 * @param client
	 *            is the requesting client
	 * @return a Collection of users UID, null on error
	 * @exception IOException
	 *                is thrown on DB access or I/O error
	 * @exception InvalidKeyException
	 *                is thrown on client integrity error (user unknown, bad
	 *                password...)
	 * @exception AccessControlException
	 *                is thrown if client does not have enough rights
	 */
	public Collection<UID> getUsers(UserInterface client) throws IOException,
	InvalidKeyException, AccessControlException {

		final UserInterface theClient = checkClient(client,
				UserRightEnum.LISTUSER);

		if (theClient.getRights() == UserRightEnum.WORKER_USER) {
			throw new AccessControlException(theClient.getLogin()
					+ " : a worker can not list users");
		}
		return usersUID(theClient);
	}

	/**
	 * This does not check the client integrity; this must have been done by the
	 * caller. This checks client rights is not lower than GETUserInterface and
	 * returns user(theClient, name)
	 * 
	 * @param client
	 *            is the requesting client
	 * @param login
	 *            is the login of the UserInterface to retrieve
	 * @return the found user or null
	 * @exception IOException
	 *                is thrown on DB access or I/O error
	 * @exception InvalidKeyException
	 *                is thrown on client integrity error (user unknown, bad
	 *                password...)
	 * @exception AccessControlException
	 *                is thrown if client does not have enough rights
	 * @see #data(UserInterface, UID)
	 */
	public UserInterface getUser(UserInterface client, String login)
			throws IOException, InvalidKeyException, AccessControlException {

		final UserInterface theClient = checkClient(client,
				UserRightEnum.GETUSER);
		return user(theClient, SQLRequest.MAINTABLEALIAS + "."
				+ UserInterface.Columns.LOGIN + "='" + login + "'");
	}

	/**
	 * This does not check the client integrity; this must have been done by the
	 * caller. This checks client rights is not lower than GETUserInterface and
	 * returns user(theClient, name)
	 * 
	 * @param client
	 *            is the requesting client
	 * @param uid
	 *            is the uid of the UserInterface to retrieve
	 * @return the found user or null
	 * @exception IOException
	 *                is thrown on DB access or I/O error
	 * @exception InvalidKeyException
	 *                is thrown on client integrity error (user unknown, bad
	 *                password...)
	 * @exception AccessControlException
	 *                is thrown if client does not have enough rights
	 * @see #data(UserInterface, UID)
	 */
	public UserInterface getUser(UserInterface client, UID uid)
			throws IOException, InvalidKeyException, AccessControlException {

		final UserInterface theClient = checkClient(client,
				UserRightEnum.GETUSER);
		if (theClient.getRights().lowerThan(UserRightEnum.GETUSER)) {
			throw new AccessControlException(theClient.getLogin()
					+ " not enought rights to GETUSER");
		}
		final UserInterface ret = user(theClient, uid);
		if (ret == null) {
			return null;
		}
		final UserInterface itf = new UserInterface(ret);

		itf.setPassword("*****");
		itf.setCertificate("*****");
		return itf;
	}

	/**
	 * This adds or updates a new user. Any user with user rights lower than
	 * UserRights.INSERTUSER can only update its own definition But even in this
	 * case, an user can never change its own user rights, nor its user group
	 * (otherwise it would be too easy to gain unexpected privileged access).
	 * UserRights#INSERTUSER privilege to add or to modify UserInterface
	 * definition. If the user already exists in DB, it is updated. If inserted
	 * in a group, new user access rights are its group one.
	 * 
	 * @param client
	 *            is the <code>UserInterface</code> that identifies the client
	 * @param useritf
	 *            is a UserInterface object containing new user informations
	 * @return true on success, false otherwise
	 * @exception IOException
	 *                is thrown on DB access or I/O error
	 * @exception InvalidKeyException
	 *                is thrown on client integrity error (user unknown, bad
	 *                password...)
	 * @exception AccessControlException
	 *                is thrown if client does not have enough rights
	 */
	public boolean addUser(UserInterface client, UserInterface useritf)
			throws IOException, InvalidKeyException, AccessControlException {

		if (useritf == null) {
			throw new IOException("useritf is null");
		}

		final UserInterface theClient = checkClient(client,
				UserRightEnum.STANDARD_USER);

		if (theClient.getRights().lowerThan(UserRightEnum.INSERTUSER)) {
			throw new AccessControlException(client.getLogin()
					+ " can't add users");
		}

		if (theClient.getRights() == UserRightEnum.WORKER_USER) {
			throw new AccessControlException(client.getLogin()
					+ " : a worker can not send/modify an user");
		}

		final UID clientGroupUID = theClient.getGroup();
		final UserGroupInterface clientGroup = usergroup(theClient,
				clientGroupUID);
		if ((clientGroupUID != null) && (clientGroup == null)) {
			throw new IOException("can't find user group " + clientGroupUID);
		}

		final UID newuserGroupUID = theClient.getGroup();
		final UserGroupInterface newuserGroup = usergroup(theClient,
				newuserGroupUID);
		if ((newuserGroupUID != null) && (newuserGroup == null)) {
			throw new IOException("can't find user group " + newuserGroupUID);
		}

		if (theClient.getRights().lowerThan(UserRightEnum.SUPER_USER)) {
			if ((clientGroupUID == null)
					|| (newuserGroupUID == null)
					|| ((clientGroupUID != null) && (clientGroupUID
							.equals(newuserGroupUID) == false))) {
				throw new AccessControlException(client.getLogin()
						+ "/"
						+ clientGroupUID
						+ " can't add users"
						+ (clientGroupUID == null ? " in no group"
								: " in group " + newuserGroupUID));
			}
		}

		UserInterface newUser = null;

		if (useritf.getUID() != null) {
			newUser = user(theClient, useritf.getUID());
		} else {
			useritf.setUID(new UID());
		}

		if (useritf.getOwner() == null) {
			useritf.setOwner(theClient.getUID());
		}

		XWAccessRights useraccessrights = XWAccessRights.DEFAULT;
		if (newuserGroup != null) {
			useraccessrights = newuserGroup.getAccessRights();
		}

		if (newUser == null) {
			newUser = user(theClient,
					SQLRequest.MAINTABLEALIAS + "."
							+ UserInterface.Columns.LOGIN.toString() + "='"
							+ useritf.getLogin() + "'");
		}

		if (newUser == null) {
			try {
				newUser = new UserInterface(useritf);
				insert(newUser);
			} catch (final Exception e) {
				logger.exception(theClient.getLogin() + " can't create "
						+ useritf.getLogin(), e);
				throw new AccessControlException(theClient.getLogin()
						+ " can't create " + useritf.getLogin());
			}
		} else {
			newUser.updateInterface(useritf);
		}

		final UserRightEnum urights = newUser.getRights();
		if ((urights == null)
				|| (urights.higherOrEquals(UserRightEnum.ADVANCED_USER))
				|| (urights.doesEqual(UserRightEnum.WORKER_USER))) {
			useraccessrights = XWAccessRights.USERALL;
		}

		if (newUser.getOwner() == null) {
			newUser.setOwner(theClient.getUID());
		}

		newUser.setAccessRights(useraccessrights);
		useraccessrights = null;

		update(theClient, UserRightEnum.INSERTUSER, newUser);

		newUser = null;

		return true;
	}

	/**
	 * This retrieves user groups from SQL table usergroups. <br>
	 * 
	 * @param client
	 *            is the <code>UserInterface</code> that identifies the client
	 * @return a Collection of usergroups UID, null on error
	 * @exception IOException
	 *                is thrown on DB access or I/O error
	 * @exception InvalidKeyException
	 *                is thrown on client integrity error (user unknown, bad
	 *                password...)
	 * @exception AccessControlException
	 *                is thrown if client does not have enough rights
	 */
	public Collection<UID> getUserGroups(UserInterface client)
			throws IOException, InvalidKeyException, AccessControlException {

		final UserInterface theClient = checkClient(client,
				UserRightEnum.LISTUSERGROUP);

		if (theClient.getRights() == UserRightEnum.WORKER_USER) {
			throw new AccessControlException(client.getLogin()
					+ " : a worker can not list UserInterface groups");
		}

		return usergroupsUID(theClient);
	}

	/**
	 * This does not check the client integrity; this must have been done by the
	 * caller. This checks client rights is not lower than GETUSERGROUP and
	 * returns usergroup(theClient, uid)
	 * 
	 * @param client
	 *            is the requesting client
	 * @param uid
	 *            is the UID of the group to retrieve
	 * @return the found usergroup or null
	 * @exception IOException
	 *                is thrown on DB access or I/O error
	 * @exception InvalidKeyException
	 *                is thrown on client integrity error (user unknown, bad
	 *                password...)
	 * @exception AccessControlException
	 *                is thrown if client does not have enough rights
	 * @see #usergroup(UserInterface, UID)
	 */
	public UserGroupInterface getUserGroup(UserInterface client, UID uid)
			throws IOException, InvalidKeyException, AccessControlException {

		final UserInterface theClient = checkClient(client,
				UserRightEnum.GETUSERGROUP);
		if (theClient.getRights().lowerThan(UserRightEnum.GETUSERGROUP)) {
			throw new AccessControlException(theClient.getLogin()
					+ " not enough rights to GETUSERGROUP");
		}
		return usergroup(theClient, uid);
	}

	/**
	 * This updates or adds a new user group. UserRights.SUPER_USER privilege is
	 * needed to do so.
	 * 
	 * @param client
	 *            is the <code>UserInterface</code> that identifies the client
	 * @param groupitf
	 *            is an UserGroupInterface object containing new UserInterface
	 *            group informations.
	 * @return true on success, false otherwise
	 * @exception IOException
	 *                is thrown on DB access or I/O error
	 * @exception InvalidKeyException
	 *                is thrown on client integrity error (user unknown, bad
	 *                password...)
	 * @exception AccessControlException
	 *                is thrown if client does not have enough rights
	 */
	public boolean addUserGroup(UserInterface client,
			UserGroupInterface groupitf) throws IOException,
			InvalidKeyException, AccessControlException {

		final UserInterface theClient = checkClient(client,
				UserRightEnum.INSERTUSERGROUP);

		final UID groupUid = groupitf.getUID();
		if (groupUid == null) {
			throw new IOException("addUserGroup : no UID");
		}

		if (groupitf.getOwner() == null) {
			groupitf.setOwner(theClient.getUID());
		}
		groupitf.setAccessRights(XWAccessRights.OWNERGROUP);

		final UserGroupInterface theGroupByUID = usergroup(theClient, groupUid);
		final UserGroupInterface theGroup = (theGroupByUID != null ? theGroupByUID
				: usergroup(theClient, SQLRequest.MAINTABLEALIAS + "."
						+ UserGroupInterface.Columns.LABEL.toString() + "='"
						+ groupitf.getLabel() + "'"));

		if (theGroup == null) {
			insert(groupitf);
		}

		update(theClient, UserRightEnum.INSERTUSERGROUP, groupitf);

		return true;
	}

	/**
	 * This inserts or updates a service. This should not be called from
	 * communication handlers since this does not verify client identity. This
	 * is to be called from the dispatcher package itself only.
	 * 
	 * @param classname
	 *            is the java class name of the service
	 * @exception ClassNotFoundException
	 *                is throws if classname can't be instantiated
	 * @exception IOException
	 *                is thrown on DB access or I/O error
	 * @exception InvalidKeyException
	 *                is thrown on client integrity error (user unknown, bad
	 *                password...)
	 * @exception AccessControlException
	 *                is thrown if client does not have enough rights
	 */
	public void insertService(String classname) throws ClassNotFoundException,
	IOException, InvalidKeyException, AccessControlException {

		final UserInterface admin = user(SQLRequest.MAINTABLEALIAS + "."
				+ UserInterface.Columns.LOGIN.toString() + "='"
				+ config.getProperty(XWPropertyDefs.ADMINLOGIN) + "'");

		if (admin == null) {
			throw new IOException("Can't retrieve UserInterface "
					+ config.getProperty(XWPropertyDefs.ADMINLOGIN));
		}

		final String ifname = classname.substring(0,
				classname.lastIndexOf('.') + 1) + "Interface";
		final Object obj = Class.forName(ifname);

		if (obj == null) {
			logger.error("DBInterface#insertService() : service '" + ifname
					+ "'not found");
			throw new ClassNotFoundException("insertService() : service '"
					+ ifname + "'not found");
		}

		final AppInterface app = new AppInterface(new UID());
		app.setName(classname);
		app.setService(true);
		addApp(admin, app);
	}

	/**
	 * This adds/updates an application UserRights.SUPER_USER privilege is
	 * needed to insert a global application, an application that anybody can
	 * use and any worker can execute, an application with 0X777 as access
	 * rights. If the client don't have UserRights.SUPER_USER rights, the
	 * application is available for that user only : application access rights
	 * is forced to 0x700 which means that this user will be the only allowed to
	 * insert job, and workers belonging to this user only will be able to
	 * execute. There may be an exception if the application name already exists
	 * e.g. : an user tries to insert its own private application which name is
	 * already used.
	 * 
	 * @param client
	 *            identifies the client
	 * @param appitf
	 *            is an ApplicationInterface object
	 * @exception IOException
	 *                is thrown on DB access or I/O error
	 * @exception InvalidKeyException
	 *                is thrown on client integrity error (user unknown, bad
	 *                password...)
	 * @exception AccessControlException
	 *                is thrown if client does not have enough rights
	 */
	public void addApp(UserInterface client, AppInterface appitf)
			throws IOException, InvalidKeyException, AccessControlException {

		final UserInterface theClient = checkClient(client,
				UserRightEnum.STANDARD_USER);

		if (appitf == null) {
			throw new IOException("addApplication : appitf is null");
		}
		if (theClient.getRights() == UserRightEnum.WORKER_USER) {
			throw new AccessControlException(
					"a worker can not insert/update application");
		}

		if (appitf.getOwner() == null) {
			appitf.setOwner(theClient.getUID());
		}
		if (appitf.getAccessRights() == null) {
			appitf.setAccessRights(XWAccessRights.DEFAULT);
		}
		final UID clientGroup = theClient.getGroup();

		final UserInterface owner = user(appitf.getOwner());
		final UID ownerGroup = (owner != null ? owner.getGroup() : null);
		final AppInterface theApp = (appitf.getUID() != null ? app(theClient,
				appitf.getUID()) : app(theClient,
						AppInterface.Columns.NAME.toString() + "='" + appitf.getName()
						+ "'"));

		if (theApp != null) {
			if (((theClient.getUID().equals(theApp.getOwner())) && (theClient
					.getRights().higherOrEquals(UserRightEnum.INSERTAPP)))
					|| (theApp.canWrite(theClient, ownerGroup))
					|| (theClient.getRights()
							.higherOrEquals(UserRightEnum.ADVANCED_USER))) {

				useData(theClient, appitf.getLaunchScriptSh());
				removeData(theClient, theApp.getLaunchScriptSh());
				theApp.setLaunchScriptSh(appitf.getLaunchScriptSh());
				useData(theClient, appitf.getLaunchScriptCmd());
				removeData(theClient, theApp.getLaunchScriptCmd());
				theApp.setLaunchScriptCmd(appitf.getLaunchScriptCmd());
				useData(theClient, appitf.getUnloadScriptSh());
				removeData(theClient, theApp.getUnloadScriptSh());
				theApp.setUnloadScriptSh(appitf.getUnloadScriptSh());
				useData(theClient, appitf.getUnloadScriptCmd());
				removeData(theClient, theApp.getUnloadScriptCmd());
				theApp.setUnloadScriptCmd(appitf.getUnloadScriptCmd());
				useData(theClient, appitf.getDefaultStdin());
				removeData(theClient, theApp.getDefaultStdin());
				theApp.setDefaultStdin(appitf.getDefaultStdin());
				useData(theClient, appitf.getDefaultDirin());
				removeData(theClient, theApp.getDefaultDirin());
				theApp.setDefaultDirin(appitf.getDefaultDirin());
				useData(theClient, appitf.getBaseDirin());
				removeData(theClient, theApp.getBaseDirin());
				theApp.setBaseDirin(appitf.getBaseDirin());
				useData(theClient, appitf.getLDLinuxIX86());
				removeData(theClient, theApp.getLDLinuxIX86());
				theApp.setLDLinux_ix86(appitf.getLDLinuxIX86());
				useData(theClient, appitf.getLDLinuxPpc());
				removeData(theClient, theApp.getLDLinuxPpc());
				theApp.setLDLinux_ppc(appitf.getLDLinuxPpc());
				useData(theClient, appitf.getLDLinuxAmd64());
				removeData(theClient, theApp.getLDLinuxAmd64());
				theApp.setLDLinux_amd64(appitf.getLDLinuxAmd64());
				useData(theClient, appitf.getLDWin32IX86());
				removeData(theClient, theApp.getLDWin32IX86());
				theApp.setLDWin32_ix86(appitf.getLDWin32IX86());
				useData(theClient, appitf.getLDWin32Amd64());
				removeData(theClient, theApp.getLDWin32Amd64());
				theApp.setLDWin32_amd64(appitf.getLDWin32Amd64());
				useData(theClient, appitf.getLDMacosIX86());
				removeData(theClient, theApp.getLDMacosIX86());
				theApp.setLDMacos_ix86(appitf.getLDMacosIX86());
				useData(theClient, appitf.getLDMacosPpc());
				removeData(theClient, theApp.getLDMacosPpc());
				theApp.setLDMacos_ppc(appitf.getLDMacosPpc());
				useData(theClient, appitf.getLDSolarisSparc());
				removeData(theClient, theApp.getLDSolarisSparc());
				theApp.setLDSolaris_sparc(appitf.getLDSolarisSparc());
				useData(theClient, appitf.getLDSolarisAlpha());
				removeData(theClient, theApp.getLDSolarisAlpha());
				theApp.setLDSolaris_alpha(appitf.getLDSolarisAlpha());
				useData(theClient, appitf.getLDOsf1Sparc());
				removeData(theClient, theApp.getLDOsf1Sparc());
				theApp.setLDOsf1_sparc(appitf.getLDOsf1Sparc());
				useData(theClient, appitf.getLDOsf1Alpha());
				removeData(theClient, theApp.getLDOsf1Alpha());
				theApp.setLDOsf1_alpha(appitf.getLDOsf1Alpha());
				useData(theClient, appitf.getJava());
				useData(theClient, theApp.getJava());
				theApp.setJava(appitf.getJava());
				useData(theClient, appitf.getLinuxIX86());
				removeData(theClient, theApp.getLinuxIX86());
				theApp.setLinux_ix86(appitf.getLinuxIX86());
				useData(theClient, appitf.getLinuxPpc());
				removeData(theClient, theApp.getLinuxPpc());
				theApp.setLinux_ppc(appitf.getLinuxPpc());
				useData(theClient, appitf.getLinuxAmd64());
				removeData(theClient, theApp.getLinuxAmd64());
				theApp.setLinux_amd64(appitf.getLinuxAmd64());
				useData(theClient, appitf.getWin32IX86());
				removeData(theClient, theApp.getWin32IX86());
				theApp.setWin32_ix86(appitf.getWin32IX86());
				useData(theClient, appitf.getWin32Amd64());
				removeData(theClient, theApp.getWin32Amd64());
				theApp.setWin32_amd64(appitf.getWin32Amd64());
				useData(theClient, appitf.getMacosIX86());
				removeData(theClient, theApp.getMacosIX86());
				theApp.setMacos_ix86(appitf.getMacosIX86());
				useData(theClient, appitf.getMacosPpc());
				removeData(theClient, theApp.getMacosPpc());
				theApp.setMacos_ppc(appitf.getMacosPpc());
				useData(theClient, appitf.getSolarisSparc());
				removeData(theClient, theApp.getSolarisSparc());
				theApp.setSolaris_sparc(appitf.getSolarisSparc());
				useData(theClient, appitf.getSolarisAlpha());
				removeData(theClient, theApp.getSolarisAlpha());
				theApp.setSolaris_alpha(appitf.getSolarisAlpha());
				useData(theClient, appitf.getOsf1Sparc());
				removeData(theClient, theApp.getOsf1Sparc());
				theApp.setOsf1_sparc(appitf.getOsf1Sparc());
				useData(theClient, appitf.getOsf1Alpha());
				removeData(theClient, theApp.getOsf1Alpha());
				theApp.setOsf1_alpha(appitf.getOsf1Alpha());

				if (theClient.getRights()
						.lowerThan(UserRightEnum.ADVANCED_USER)) {
					logger.debug("set app AR to USERALL");
					appitf.setAccessRights(XWAccessRights.USERALL);
				}
				if (theClient.getRights().doesEqual(UserRightEnum.SUPER_USER)) {
					if (appitf.getAccessRights() == null) {
						logger.debug("set app AR to DEFAULT");
						appitf.setAccessRights(XWAccessRights.DEFAULT);
					}
				} else {
					if ((theClient.getRights()
							.higherOrEquals(UserRightEnum.INSERTAPP))
							&& (clientGroup != null)) {
						logger.debug("set app AR to OWNERGROUP");
						appitf.setAccessRights(XWAccessRights.OWNERGROUP);
					}
				}
				theApp.updateInterface(appitf);
				update(theClient, UserRightEnum.INSERTAPP, theApp);
			} else {
				logger.error("DBInterface#addApplication" + client.getLogin()
						+ " can't update " + appitf.getName());
				throw new AccessControlException(client.getLogin()
						+ " can't update " + appitf.getName());
			}

			return;
		}

		try {
			if (appitf.getUID() == null) {
				appitf.setUID(new UID());
			}
		} catch (final Exception e) {
			logger.exception(
					theClient.getLogin() + " can't create " + appitf.getName(),
					e);
			throw new AccessControlException(theClient.getLogin()
					+ " can't create " + appitf.getName() + " : "
					+ e.getMessage());
		}

		if (theClient.getRights().lowerThan(UserRightEnum.ADVANCED_USER)) {
			logger.debug("set app AR to USERALL");
			appitf.setAccessRights(XWAccessRights.USERALL);
		}
		if (theClient.getRights().doesEqual(UserRightEnum.SUPER_USER)) {
			if (appitf.getAccessRights() == null) {
				logger.debug("set app AR to DEFAULT");
				appitf.setAccessRights(XWAccessRights.DEFAULT);
			}
		} else {
			if ((theClient.getRights().higherOrEquals(UserRightEnum.INSERTAPP))
					&& (clientGroup != null)) {
				logger.debug("set app AR to OWNERGROUP");
				appitf.setAccessRights(XWAccessRights.OWNERGROUP);
			}
		}

		final AppTypeEnum apptype = appitf.getType();
		if ((apptype == null) || (apptype == AppTypeEnum.NONE)) {
			appitf.setType(AppTypeEnum.DEPLOYABLE);
		}

		insert(appitf);

		useData(theClient, appitf.getDefaultStdin());
		useData(theClient, appitf.getDefaultDirin());
		useData(theClient, appitf.getBaseDirin());
		useData(theClient, appitf.getLDLinuxIX86());
		useData(theClient, appitf.getLDLinuxPpc());
		useData(theClient, appitf.getLDLinuxAmd64());
		useData(theClient, appitf.getLDWin32IX86());
		useData(theClient, appitf.getLDWin32Amd64());
		useData(theClient, appitf.getLDMacosIX86());
		useData(theClient, appitf.getLDMacosPpc());
		useData(theClient, appitf.getLDSolarisSparc());
		useData(theClient, appitf.getLDSolarisAlpha());
		useData(theClient, appitf.getLDOsf1Sparc());
		useData(theClient, appitf.getLDOsf1Alpha());
		useData(theClient, appitf.getJava());
		useData(theClient, appitf.getLinuxIX86());
		useData(theClient, appitf.getLinuxPpc());
		useData(theClient, appitf.getLinuxAmd64());
		useData(theClient, appitf.getWin32IX86());
		useData(theClient, appitf.getWin32Amd64());
		useData(theClient, appitf.getMacosIX86());
		useData(theClient, appitf.getMacosPpc());
		useData(theClient, appitf.getSolarisSparc());
		useData(theClient, appitf.getSolarisAlpha());
		useData(theClient, appitf.getOsf1Sparc());
		useData(theClient, appitf.getOsf1Alpha());
	}

	/**
	 * retrieve applications from SQL table apps accordingly to conditions.
	 * 
	 * @param client
	 *            is the <code>UserInterface</code> that identifies the client
	 * @return a Collection UID objects
	 * @exception IOException
	 *                is thrown on DB access or I/O error
	 * @exception InvalidKeyException
	 *                is thrown on client integrity error (user unknown, bad
	 *                password...)
	 * @exception AccessControlException
	 *                is thrown if client does not have enough rights
	 */
	public Collection<UID> getApplications(UserInterface client)
			throws IOException, InvalidKeyException, AccessControlException {

		final UserInterface theClient = checkClient(client,
				UserRightEnum.LISTAPP);

		if (theClient.getRights() == UserRightEnum.WORKER_USER) {
			throw new AccessControlException(theClient.getLogin()
					+ " : a worker can not list applications");
		}
		return appsUID(theClient);
	}

	/**
	 * This retrieves an object
	 * 
	 * @param uid
	 *            is the UID of the object to retrieve
	 * @return the object or null if not found
	 * @exception IOException
	 *                is thrown on DB access or I/O error
	 * @exception InvalidKeyException
	 *                is thrown on client integrity error (user unknown, bad
	 *                password...)
	 * @exception AccessControlException
	 *                is thrown if client does not have enough rights
	 */
	public Table get(UserInterface client, UID uid) throws IOException,
	InvalidKeyException, AccessControlException {

		Table ret = null;

		ret = getJob(client, uid);
		if (ret != null) {
			return ret;
		}
		ret = getTask(client, uid);
		if (ret != null) {
			return ret;
		}
		ret = getData(client, uid);
		if (ret != null) {
			return ret;
		}
		ret = getApplication(client, uid);
		if (ret != null) {
			return ret;
		}
		ret = getUser(client, uid);
		if (ret != null) {
			return ret;
		}
		ret = getUserGroup(client, uid);
		if (ret != null) {
			return ret;
		}
		ret = getSession(client, uid);
		if (ret != null) {
			return ret;
		}
		ret = getGroup(client, uid);
		if (ret != null) {
			return ret;
		}
		ret = getHost(client, uid);
		if (ret != null) {
			return ret;
		}

		return null;
	}

	/**
	 * This retrieves a task for the specified client. This specifically permits
	 * to retrieve job instanciation informations (e.g. start date, worker...)
	 * 
	 * @see #getJob(UserInterface, UID)
	 * @param client
	 *            is the ClientInterface, describing the client
	 * @param uid
	 *            is the UID of the task to retrieve
	 * @return null on error; a TaskInterface otherwise
	 * @exception IOException
	 *                is thrown general error
	 * @exception InvalidKeyException
	 *                is thrown on credential error
	 * @exception AccessControlException
	 *                is thrown on access rights violation
	 */
	public TaskInterface getTask(UserInterface client, UID uid)
			throws IOException, InvalidKeyException, AccessControlException {

		final UserInterface theClient = checkClient(client, UserRightEnum.LISTJOB);

		final TaskInterface theTask = task(theClient, uid);
		if (theTask != null) {
			return theTask;
		}
		final TaskInterface theTaskbyWork = task(theClient, TaskInterface.Columns.WORKUID.toString()
				+ "='" + uid + "'");

		if (theTaskbyWork == null) {
			return null;
		}

		final UserInterface owner = user(theTaskbyWork.getOwner());
		final UID ownerGroup = (owner == null ? null : owner.getGroup());
		if ((theTaskbyWork.canRead(theClient, ownerGroup) == false)
				&& (theClient.getRights().lowerThan(UserRightEnum.SUPER_USER))) {
			throw new AccessControlException(client.getLogin() + " can't read "
					+ uid);
		}
		return theTaskbyWork;
	}

	/**
	 * This does not check the client integrity; this must have been done by the
	 * caller. This checks client rights is not lower than GETAPP and returns
	 * app(theClient, name)
	 * 
	 * @param client
	 *            is the requesting client
	 * @param name
	 *            is the name of the applicaiton to retrieve
	 * @return the found application or null
	 * @exception IOException
	 *                is thrown on DB access or I/O error
	 * @exception InvalidKeyException
	 *                is thrown on client integrity error (user unknown, bad
	 *                password...)
	 * @exception AccessControlException
	 *                is thrown if client does not have enough rights
	 * @see #app(UserInterface, String)
	 */
	public AppInterface getApplication(UserInterface client, String name)
			throws IOException, InvalidKeyException, AccessControlException {

		final UserInterface theClient = checkClient(client,
				UserRightEnum.GETAPP);
		if (theClient.getRights().lowerThan(UserRightEnum.GETAPP)) {
			throw new AccessControlException(theClient.getLogin()
					+ "can't getapp");
		}
		return app(theClient, AppInterface.Columns.NAME.toString() + "='"
				+ name + "'");
	}

	/**
	 * This does not check the client integrity; this must have been done by the
	 * caller. This checks client rights is not lower than GETAPP and returns
	 * app(theClient, uid)
	 * 
	 * @param client
	 *            is the requesting client
	 * @param uid
	 *            is the UID of the application to retrieve
	 * @return the found application or null
	 * @exception IOException
	 *                is thrown on DB access or I/O error
	 * @exception InvalidKeyException
	 *                is thrown on client integrity error (user unknown, bad
	 *                password...)
	 * @exception AccessControlException
	 *                is thrown if client does not have enough rights
	 * @see #app(UserInterface, UID)
	 */
	public AppInterface getApplication(UserInterface client, UID uid)
			throws IOException, InvalidKeyException, AccessControlException {

		final UserInterface theClient = checkClient(client,
				UserRightEnum.GETAPP);
		if (theClient.getRights().lowerThan(UserRightEnum.GETAPP)) {
			throw new AccessControlException(theClient.getLogin()
					+ " not enough rights to GETAPP");
		}
		final AppInterface app = app(theClient, uid);
		return app;
	}

	/**
	 * This removes an application from DB
	 * 
	 * @param uid
	 *            is the UID of the application to remove
	 * @return true on success, false otherwise
	 * @exception IOException
	 *                is thrown on DB access or I/O error
	 * @exception InvalidKeyException
	 *                is thrown on client integrity error (user unknown, bad
	 *                password...)
	 * @exception AccessControlException
	 *                is thrown if client does not have enough rights
	 */
	private boolean removeApplication(final UserInterface theClient,
			final AppInterface theApp, UID ownerGroup)
					throws IOException, InvalidKeyException, AccessControlException {

		if (theApp == null) {
			return false;
		}
		if ((theApp.canWrite(theClient, ownerGroup) == false)
				&& (theClient.getRights().lowerThan(UserRightEnum.SUPER_USER))) {
			throw new AccessControlException(theClient.getLogin()
					+ " can not remove app " + theApp.getName());
		}

		removeData(theClient, theApp.getLaunchScriptSh());
		removeData(theClient, theApp.getLaunchScriptCmd());
		removeData(theClient, theApp.getUnloadScriptSh());
		removeData(theClient, theApp.getUnloadScriptCmd());
		removeData(theClient, theApp.getDefaultStdin());
		removeData(theClient, theApp.getBaseDirin());
		removeData(theClient, theApp.getDefaultDirin());
		removeData(theClient, theApp.getLinuxIX86());
		removeData(theClient, theApp.getLinuxAmd64());
		removeData(theClient, theApp.getLinuxPpc());
		removeData(theClient, theApp.getWin32IX86());
		removeData(theClient, theApp.getWin32Amd64());
		removeData(theClient, theApp.getMacosIX86());
		removeData(theClient, theApp.getMacosPpc());
		removeData(theClient, theApp.getMacosX8664());
		removeData(theClient, theApp.getJava());
		removeData(theClient, theApp.getSolarisSparc());
		removeData(theClient, theApp.getSolarisAlpha());
		removeData(theClient, theApp.getOsf1Sparc());
		removeData(theClient, theApp.getOsf1Alpha());
		removeData(theClient, theApp.getLDLinuxIX86());
		removeData(theClient, theApp.getLDLinuxAmd64());
		removeData(theClient, theApp.getLDLinuxPpc());
		removeData(theClient, theApp.getLDWin32IX86());
		removeData(theClient, theApp.getLDWin32Amd64());
		removeData(theClient, theApp.getLDMacosIX86());
		removeData(theClient, theApp.getLDMacosPpc());
		removeData(theClient, theApp.getLDMacosX8664());
		removeData(theClient, theApp.getLDSolarisSparc());
		removeData(theClient, theApp.getLDSolarisAlpha());
		removeData(theClient, theApp.getLDOsf1Sparc());
		removeData(theClient, theApp.getLDOsf1Alpha());

		return delete(theClient, theApp);
	}

	/**
	 * This calls removeData(theClient uri.getUID())
	 */
	private boolean removeData(UserInterface theClient, URI uri)
			throws IOException, InvalidKeyException, AccessControlException {
		if (uri == null) {
			return false;
		}
		final UID uid = uri.getUID();
		return removeData(theClient, uid);
	}

	/**
	 * This calls delete(theClient data(uid))
	 */
	private boolean removeData(UserInterface theClient, UID uid)
			throws IOException, InvalidKeyException, AccessControlException {

		final DataInterface theData = data(theClient, uid);
		if(theData != null) {
			deleteJob(theClient, theData.getWork());
		}
		return delete(theClient, theData);
	}

	/**
	 * This deletes a group from DB and all its associated jobs
	 * 
	 * @param client
	 *            describes the requesting client
	 * @param groupUID
	 *            is the uid of the group to delete
	 * @return true on success; false otherwise
	 * @exception IOException
	 *                is thrown on DB access or I/O error
	 * @exception InvalidKeyException
	 *                is thrown on client integrity error (user unknown, bad
	 *                password...)
	 * @exception AccessControlException
	 *                is thrown if client does not have enough rights
	 */
	private boolean removeGroup(UserInterface theClient, UID groupUID)
			throws IOException, InvalidKeyException, AccessControlException {

		final GroupInterface group = group(theClient, groupUID);
		if (group == null) {
			return false;
		}

		if ((theClient.getRights() == null)
				|| (theClient.getRights().lowerThan(UserRightEnum.DELETEGROUP))) {
			throw new AccessControlException(theClient.getLogin()
					+ " : not enough rights to delete group " + groupUID);
		}

		if ((group.getOwner().equals(theClient.getUID()) == false)
				&& (theClient.getRights().lowerThan(UserRightEnum.SUPER_USER))) {
			throw new AccessControlException(theClient.getLogin()
					+ " : can't remove group " + groupUID);
		}

		removeSession(theClient, group.getSession());
		if (deleteJobs(theClient, getGroupJobs(theClient, groupUID)) == true) {
			return delete(theClient, group);
		}
		return true;
	}

	/**
	 * This deletes a session from DB and all its associated jobs
	 * 
	 * @param client
	 *            describes the requesting client
	 * @param sessionUID
	 *            is the UID of the session to delete
	 * @return true on success; false otherwise
	 * @exception IOException
	 *                is thrown on DB access or I/O error
	 * @exception InvalidKeyException
	 *                is thrown on client integrity error (user unknown, bad
	 *                password...)
	 * @exception AccessControlException
	 *                is thrown if client does not have enough rights
	 */
	private boolean removeSession(UserInterface theClient, UID sessionUID)
			throws IOException, InvalidKeyException, AccessControlException {

		if (sessionUID == null) {
			return false;
		}
		final SessionInterface session = session(theClient, sessionUID);
		if (session == null) {
			return false;
		}

		if ((theClient.getRights() == null)
				|| (theClient.getRights()
						.lowerThan(UserRightEnum.DELETESESSION))) {
			throw new AccessControlException(theClient.getLogin()
					+ " : not enough rights to delete session " + sessionUID);
		}

		if (deleteJobs(theClient, getSessionJobs(theClient, sessionUID)) == true) {
			return delete(theClient, session);
		}

		return true;
	}

	/**
	 * This deletes a session from DB and all its associated jobs
	 * 
	 * @param client
	 *            describes the requesting client
	 * @param taskUID
	 *            is the UID of the session to delete
	 * @return true on success; false otherwise
	 * @exception IOException
	 *                is thrown on DB access or I/O error
	 * @exception InvalidKeyException
	 *                is thrown on client integrity error (user unknown, bad
	 *                password...)
	 * @exception AccessControlException
	 *                is thrown if client does not have enough rights
	 */
	private boolean removeTask(final UserInterface theClient, final UID taskUID)
			throws IOException, InvalidKeyException, AccessControlException {

		final TaskInterface theTask = task(theClient, taskUID);
		if (theTask == null) {
			return false;
		}

		if ((theClient.getRights() == null)
				|| (theClient.getRights().lowerThan(UserRightEnum.DELETEJOB))) {
			throw new AccessControlException(theClient.getLogin()
					+ " : not enough rights to delete task " + taskUID);
		}

		return delete(theClient, theTask);
	}

	/**
	 * This removes an user. The UserInterface which UID is provided is
	 * effectivly removed if requesting client has UserRights.SUPER_USER
	 * privileges, or provided UID is the client one. <br>
	 * This also removes users's jobs (sessions, groups, tasks and works).
	 * 
	 * @param client
	 *            identifies the client
	 * @param userUID
	 *            is the user UID
	 * @return true on success, false otherwise
	 * @exception IOException
	 *                is thrown on DB access or I/O error
	 * @exception InvalidKeyException
	 *                is thrown on client integrity error (user unknown, bad
	 *                password...)
	 * @exception AccessControlException
	 *                is thrown if client does not have enough rights
	 */
	private boolean removeUser(UserInterface theClient, UID userUID)
			throws IOException, InvalidKeyException, AccessControlException {

		boolean ret = true;

		final UserInterface theuser = user(theClient, userUID);
		if (theuser == null) {
			return false;
		}
		if ((theClient.getRights() == null)
				|| (theClient.getRights().lowerThan(UserRightEnum.DELETEUSER))) {
			throw new AccessControlException(theClient.getLogin()
					+ " : not enough rights to delete UserInterface " + userUID);
		}

		final Vector<UID> workuids = (Vector<UID>) worksUID(
				theClient,
				SQLRequest.MAINTABLEALIAS + "."
						+ TableColumns.OWNERUID.toString() + "='"
						+ userUID.toString() + "'");
		if (workuids != null) {
			for (final Enumeration<UID> enums = workuids.elements(); enums
					.hasMoreElements();) {
				final UID workuid = enums.nextElement();
				ret = deleteJob(theClient, workuid);
				if (!ret) {
					break;
				}
			}
		}

		if (ret) {
			ret = delete(theClient, theuser);
		}
		return ret;
	}

	/**
	 * This removes an user group and all its members
	 * 
	 * @param client
	 *            is the requesting user
	 * @param uid
	 *            is the user group UID to remove.
	 * @return true on success, false otherwise
	 * @exception IOException
	 *                is thrown on DB access or I/O error
	 * @exception InvalidKeyException
	 *                is thrown on client integrity error (user unknown, bad
	 *                password...)
	 * @exception AccessControlException
	 *                is thrown if client does not have enough rights
	 */
	private boolean removeUserGroup(final UserInterface theClient,
			final UID groupUID) throws IOException, InvalidKeyException,
			AccessControlException {

		final UserGroupInterface group = usergroup(theClient, groupUID);
		if (group == null) {
			return false;
		}
		if ((theClient.getRights() == null)
				|| (theClient.getRights()
						.lowerThan(UserRightEnum.DELETEUSERGROUP))) {

			throw new AccessControlException(theClient.getLogin()
					+ " : not enough rights to delete UserInterface group "
					+ groupUID);
		}
		if (!group.isProject()) {
			return false;
		}

		boolean ret = false;
		Vector<UID> useruids = (Vector<UID>) usersUID(theClient,
				SQLRequest.MAINTABLEALIAS + "."
						+ UserInterface.Columns.USERGROUPUID.toString() + "='"
						+ groupUID.toString() + "'");
		if (useruids != null) {
			final Iterator<UID> li = useruids.iterator();
			while (li.hasNext()) {
				final UID userUID = li.next();
				if (userUID == null) {
					logger.error("user uid is null???");
					continue;
				}
				ret = removeUser(theClient, userUID);
				if (!ret) {
					break;
				}
			}
		}
		useruids = null;

		if (ret) {
			ret = delete(theClient, group);
		}
		return ret;
	}

	/**
	 * Get all known traces.
	 * 
	 * @param client
	 *            is the <code>ClientInterface</code> that identifies the client
	 * @param since
	 *            is the date from which to retrieve traces
	 * @param before
	 *            is the date to which to retrieve traces
	 * @return an vector of <CODE>TraceInterface</CODE>
	 */
	public Vector<UID> getRegisteredTraces(UserInterface client,
			java.util.Date since, java.util.Date before) {

		logger.error("getRegisteredTraces() not implemented");
		return null;
	}

	/* Tracer */

	public void writeStatFile(HostInterface host, long start, long end,
			byte[] file) throws IOException {

		logger.error("writeStatFile() not implemented");
	}

	/**
	 * This retrieves sessions. <br>
	 * 
	 * @param client
	 *            is the <code>ClientInterface</code> that identifies the client
	 * @return a Collection of UID
	 * @exception IOException
	 *                is thrown on DB access or I/O error
	 * @exception InvalidKeyException
	 *                is thrown on client integrity error (user unknown, bad
	 *                password...)
	 * @exception AccessControlException
	 *                is thrown if client does not have enough rights
	 */
	public Collection<UID> getSessions(final UserInterface client)
			throws IOException, InvalidKeyException, AccessControlException {

		final UserInterface theClient = checkClient(client,
				UserRightEnum.LISTSESSION);

		if (theClient.getRights() == UserRightEnum.WORKER_USER) {
			throw new AccessControlException(theClient.getLogin()
					+ " : a worker can not list sessions");
		}
		return getSessions(theClient, client.getUID());
	}

	/**
	 * This retrieves sessions for the given client<br />
	 * This method has a private access because it does not call checkClient()
	 * to try to avoid too much DB access. It is typically called from whithin
	 * this current source file from methods which must have already called
	 * checkClient() !<br>
	 * Typically deleteGroup(), deleteSession(), disconnect() etc.
	 * 
	 * @param client
	 *            is the client uid
	 * @return a Collection of UID
	 */
	private Collection<UID> getSessions(final UserInterface theClient,
			final UID client) throws IOException {
		return sessionsUID(theClient, SQLRequest.MAINTABLEALIAS + "."
				+ TableColumns.OWNERUID.toString() + "='" + client.toString()
				+ "'");
	}

	/**
	 * This does not check the client integrity; this must have been done by the
	 * caller. This checks client rights is not lower than GETSESSION and
	 * returns work(theClient, uid)
	 * 
	 * @param client
	 *            is the requesting client
	 * @param uid
	 *            is the UID of the work to retrieve
	 * @return the found work or null
	 * @exception IOException
	 *                is thrown on DB access or I/O error
	 * @exception AccessControlException
	 *                is thrown if UserInterface rights lower than GETSESSION
	 * @see #work(UserInterface, UID)
	 */
	public SessionInterface getSession(final UserInterface client, final UID uid)
			throws IOException, InvalidKeyException, AccessControlException {

		final UserInterface theClient = checkClient(client,
				UserRightEnum.GETSESSION);
		if (theClient.getRights().lowerThan(UserRightEnum.GETSESSION)) {
			throw new AccessControlException(theClient.getLogin()
					+ " not enought rights to GETSESSION");
		}
		return session(theClient, uid);
	}

	/**
	 * This retrieves groups of the provided client.<br />
	 * This method has a private access because it does not call checkClient()
	 * to try to avoid too much DB access. It is typically called from whithin
	 * this current source file from methods which must have already called
	 * checkClient() !<br>
	 * Typically deleteGroup(), deleteSession(), disconnect() etc.
	 * 
	 * @param client
	 *            is the client uid
	 * @return a Vector of UID
	 * @throws AccessControlException
	 * @throws InvalidKeyException
	 */
	public Collection<UID> getGroups(final UserInterface client)
			throws IOException, InvalidKeyException, AccessControlException {
		final UserInterface theClient = checkClient(client,
				UserRightEnum.LISTGROUP);

		if (theClient.getRights() == UserRightEnum.WORKER_USER) {
			throw new AccessControlException(theClient.getLogin()
					+ " : a worker can not list groups");
		}
		return groupsUID(theClient, SQLRequest.MAINTABLEALIAS + "."
				+ TableColumns.OWNERUID.toString() + "='" + theClient.getUID()
				+ "'");
	}

	/**
	 * This checks client rights and returns group(client, uid)
	 * 
	 * @see #group(UserInterface, UID)
	 */
	public GroupInterface getGroup(final UserInterface client, final UID uid)
			throws IOException, InvalidKeyException, AccessControlException {

		final UserInterface theClient = checkClient(client,
				UserRightEnum.GETGROUP);
		return group(theClient, uid);
	}

	/**
	 * This checks group access rights and eventually then call
	 * getGroupJobs(client, udi)
	 * 
	 * @param client
	 *            describes the requesting client
	 * @param uid
	 *            is the group UID to retrieve jobs for
	 * @return a Collection of UID
	 * @see #getGroupJobs(UserInterface, UID)
	 * @exception IOException
	 *                is thrown on DB access or I/O error
	 * @exception InvalidKeyException
	 *                is thrown on client integrity error (user unknown, bad
	 *                password...)
	 * @exception AccessControlException
	 *                is thrown if client does not have enough rights
	 */
	public Collection<UID> getGroupJobs(final UserInterface client,
			final UID uid) throws IOException, InvalidKeyException,
			AccessControlException {

		final UserInterface theClient = checkClient(client,
				UserRightEnum.LISTJOB);

		if (theClient.getRights() == UserRightEnum.WORKER_USER) {
			throw new AccessControlException(theClient.getLogin()
					+ " : a worker can not list group jobs");
		}
		final GroupInterface group = group(theClient, uid);
		if (group == null) {
			return null;
		}
		if ((group.getOwner().equals(theClient.getUID()) == false)
				&& (theClient.getRights().lowerThan(UserRightEnum.SUPER_USER))) {
			throw new AccessControlException(theClient.getLogin()
					+ " : can't retrieve jobs of group " + uid);
		}

		return worksUID(theClient,
				SQLRequest.MAINTABLEALIAS + "."
						+ WorkInterface.Columns.GROUPUID.toString() + "='"
						+ uid.toString() + "'");
	}

	/**
	 * This retrieves jobs for a given session
	 * 
	 * @param client
	 *            describes the requesting client
	 * @param uid
	 *            is the session UID to retrieve jobs for
	 * @return a Vector of UID
	 * @see #worksUID(UserInterface, String)
	 * @exception IOException
	 *                is thrown on DB access or I/O error
	 * @exception InvalidKeyException
	 *                is thrown on client integrity error (user unknown, bad
	 *                password...)
	 * @exception AccessControlException
	 *                is thrown if client does not have enough rights
	 */
	public Collection<UID> getSessionJobs(final UserInterface client,
			final UID uid) throws IOException, InvalidKeyException,
			AccessControlException {
		final UserInterface theClient = checkClient(client,
				UserRightEnum.LISTJOB);

		if (theClient.getRights() == UserRightEnum.WORKER_USER) {
			throw new AccessControlException(theClient.getLogin()
					+ " : a worker can not list session jobs");
		}
		return worksUID(theClient,
				SQLRequest.MAINTABLEALIAS + "."
						+ WorkInterface.Columns.SESSIONUID.toString() + "='"
						+ uid.toString() + "'");
	}

	/**
	 * This adds/updates a session
	 * 
	 * @param client
	 *            describes the requesting client
	 * @param sessionitf
	 *            describes the session to insert in DB
	 * @return true on success; false on DB error or if session already exists
	 *         in DB
	 * @exception IOException
	 *                is thrown on DB access or I/O error
	 * @exception InvalidKeyException
	 *                is thrown on client integrity error (user unknown, bad
	 *                password...)
	 * @exception AccessControlException
	 *                is thrown if client does not have enough rights
	 */
	public boolean addSession(final UserInterface client,
			final SessionInterface sessionitf) throws IOException,
			InvalidKeyException, AccessControlException {

		final UserInterface theClient = checkClient(client,
				UserRightEnum.INSERTSESSION);

		if (theClient.getRights() == UserRightEnum.WORKER_USER) {
			throw new AccessControlException(
					"a worker can not send/modify a session");
		}

		final SessionInterface session = session(theClient, sessionitf.getUID());
		if (session != null) {
			update(theClient, UserRightEnum.INSERTSESSION, session);
			return true;
		}
		if (sessionitf.getUID() == null) {
			UID uid = new UID();
			sessionitf.setUID(uid);
			uid = null;
		}
		if (sessionitf.getOwner() == null) {
			sessionitf.setOwner(theClient.getUID());
		}

		insert(sessionitf);

		return true;
	}

	/**
	 * This adds/updates a group in DB
	 * 
	 * @param client
	 *            describes the requesting client
	 * @param groupitf
	 *            describes the group to insert in DB
	 * @return true on success; false on DB error or group already exists
	 * @exception IOException
	 *                is thrown on DB access or I/O error
	 * @exception InvalidKeyException
	 *                is thrown on client integrity error (user unknown, bad
	 *                password...)
	 * @exception AccessControlException
	 *                is thrown if client does not have enough rights
	 */
	public boolean addGroup(final UserInterface client,
			final GroupInterface groupitf) throws IOException,
			InvalidKeyException, AccessControlException {

		final UserInterface theClient = checkClient(client,
				UserRightEnum.INSERTGROUP);

		if (theClient.getRights() == UserRightEnum.WORKER_USER) {
			throw new AccessControlException(
					"a worker can not send/modify a group");
		}

		final GroupInterface group = group(theClient, groupitf.getUID());
		if (group != null) {
			group.updateInterface(groupitf);
			update(theClient, UserRightEnum.INSERTGROUP, group);
			return true;
		}

		if (groupitf.getUID() == null) {
			final UID uid = new UID();
			groupitf.setUID(uid);
		}
		if (groupitf.getOwner() == null) {
			groupitf.setOwner(theClient.getUID());
		}
		insert(groupitf);

		return true;
	}

	/**
	 * This delete a job (a work and its associated task)
	 * 
	 * @see #deleteJobs(UserInterface, Vector)
	 * @exception IOException
	 *                is thrown on DB access or I/O error
	 * @exception InvalidKeyException
	 *                is thrown on client integrity error (user unknown, bad
	 *                password...)
	 * @exception AccessControlException
	 *                is thrown if client does not have enough rights
	 */
	private boolean deleteJob(final UserInterface theClient, final UID jobUID)
			throws IOException, InvalidKeyException, AccessControlException {

		if (jobUID == null) {
			System.out.println("jobuid is null");
			return false;
		}

		final WorkInterface theWork = work(theClient, jobUID);
		if (theWork == null) {
			System.out.println("no work for jobuid " + jobUID);
			return false;
		}
		if ((theClient.getRights() == null)
				|| (theClient.getRights().lowerThan(UserRightEnum.DELETEJOB))) {

			throw new AccessControlException(theClient.getLogin()
					+ " : not enough rights to delete job " + jobUID);
		}

		try {
			removeData(theClient, theWork.getStdin());
		} catch (final AccessControlException e) {
			logger.warn(e.getMessage());
		}
		try {
			removeData(theClient, theWork.getDirin());
		} catch (final AccessControlException e) {
			logger.warn(e.getMessage());
		}
		try {
			removeData(theClient, theWork.getResult());
		} catch (final AccessControlException e) {
			logger.warn(e.getMessage());
		}
		try {
			removeData(theClient, theWork.getUserProxy());
		} catch (final AccessControlException e) {
			logger.warn(e.getMessage());
		}

		final Vector<Table> updateRows = new Vector<Table>();
		final Vector<TaskInterface> theTasks = (Vector<TaskInterface>) tasks(theWork);

		if (theTasks != null) {

			final Enumeration<TaskInterface> thetaskEnum = theTasks.elements();

			while (thetaskEnum.hasMoreElements()) {

				final TaskInterface theTask = thetaskEnum.nextElement();
				final UID hostUID = theTask.getHost();
				final HostInterface theHost = host(hostUID);
				if(delete(theClient, theTask) == true) {
					if (theHost != null) {
						switch (theWork.getStatus()) {
						case RESULTREQUEST:
						case DATAREQUEST:
						case RUNNING:
							theHost.decRunningJobs();
							updateRows.add(theHost);
							break;
						}
					}
				}
			}
		}

		final UID appUID = theWork.getApplication();
		final AppInterface theApp = app(theClient, appUID);
		final UID expectedHostUID = theWork.getExpectedHost();
		final HostInterface theExpectedHost = (expectedHostUID != null ? host(
				theClient, expectedHostUID) : null);

		switch (theWork.getStatus()) {
		case PENDING:
			if (theExpectedHost != null) {
				theExpectedHost.decPendingJobs();
			}
			theClient.decPendingJobs();
			if (theApp != null) {
				theApp.decPendingJobs();
			}
			break;
		case RESULTREQUEST:
		case DATAREQUEST:
		case RUNNING:
			theClient.decRunningJobs();
			if (theApp != null) {
				theApp.decRunningJobs();
			}
			if (theExpectedHost != null) {
				theExpectedHost.decRunningJobs();
			}
			break;
		}


		deleteJobs(theClient, replicasUID(theClient, theWork.getUID()));

		delete(theClient, theWork);

		updateRows.add(theExpectedHost);
		updateRows.add(theClient);
		updateRows.add(theApp);
		update(updateRows);

		return true;
	}

	/**
	 * This deletes a vector of jobs by iteratively calling
	 * deleJob(UserInterface, UID)
	 * 
	 * @see #deleteJob(UserInterface, UID)
	 * @param client
	 *            describes the requesting client
	 * @param jobs
	 *            is a Collection containing work UIDs
	 * @return true on success; false otherwise
	 * @exception IOException
	 *                is thrown general error
	 * @exception InvalidKeyException
	 *                is thrown on credential error
	 * @exception AccessControlException
	 *                is thrown on access rights violation
	 */
	private boolean deleteJobs(final UserInterface theClient,
			final Collection<UID> jobs) throws IOException,
			InvalidKeyException, AccessControlException {

		boolean result = true;

		if ((jobs == null) || (jobs.size() < 1)) {
			return true;
		}

		try {
			final Iterator<UID> li = jobs.iterator();
			while (li.hasNext()) {
				final UID jobUID = li.next();
				if (deleteJob(theClient, jobUID) == false) {
					System.out.println("deletejob(" + jobUID + ") returned false");
					result = false;
				}
			}
		} catch (final Exception e) {
			result = false;
			logger.exception("DeleteJobs", e);
		}

		return result;

	}

	/**
	 * This broadcasts a new job to all workers accordingly to available
	 * application binaries This uses the "expectedHost" job attribute e.g. : if
	 * we don't have win32 binary, we don't broadcast to win32 workers
	 * 
	 * @see #addWork(UserInterface, HostInterface, WorkInterface)
	 * @return a Collection of new jobs UID
	 * @exception IOException
	 *                is thrown general error
	 * @exception InvalidKeyException
	 *                is thrown on credential error
	 * @exception AccessControlException
	 *                is thrown on access rights violation
	 */
	public Collection<UID> broadcast(final UserInterface client,
			final WorkInterface job) throws IOException, InvalidKeyException,
			AccessControlException {

		final Vector<UID> ret = new Vector<UID>();
		final UserInterface theClient = checkClient(client,
				UserRightEnum.INSERTJOB);

		if (theClient.getRights() == UserRightEnum.WORKER_USER) {
			throw new AccessControlException("a worker can not broadcast");
		}
		final AppInterface app = app(theClient, job.getApplication());

		if (app == null) {
			logger.error("DBInterface#broadcast : can't find application");
			throw new IOException("broadcast() : can't find application");
		}

		final Collection<UID> workers = getRegisteredWorkers(client);
		if ((workers == null) || (workers.size() == 0)) {
			logger.error("DBInterface#broadcast : can't find any worker to broadcast");
			throw new IOException(
					"broadcast() : can't find any worker to broadcast");
		}

		final Iterator<UID> li = workers.iterator();
		try {
			while (li.hasNext()) {
				final HostInterface worker = host(theClient, li.next());

				if (app.getBinary(worker.getCpu(), worker.getOs()) == null) {
					logger.warn(worker.getUID()
							+ " : "
							+ app.getName()
							+ " is not broadcasted since there is no compatible binary");
					continue;
				}
				job.setExpectedHost(worker.getUID());
				job.setUID(new UID());
				final WorkInterface work = addWork(client, null, job);
				if (work != null) {
					ret.add(work.getUID());
					work.setPending();
				} else {
					logger.warn("broadcast() can't submit");
				}
			}
		} catch (final Exception e) {
			return null;
		}

		return ret;
	}

	/**
	 * This adds/updates a work according to work access rights. If client
	 * rights is higher or equals to WORKER_USER, work access rights can be
	 * bypassed (but not allowing work insertion) This allows workers to update
	 * job (e.g. set job status to COMPLETED) This sets access rights to minimal
	 * value e.g. : if application access rights are 0x700, job ones must be
	 * 0x700 or lower (0x600, 0x500...)
	 * 
	 * 
	 * @param client
	 *            describes the requesting client
	 * @param job
	 *            is a MobileWork describing the work to insert
	 * @return the provided job uid don activation; a new jobUID if a new job
	 *         has been created; null on error
	 * @exception IOException
	 *                is thrown on DB access or I/O error
	 * @exception InvalidKeyException
	 *                is thrown on rights error (user unknown, not enough right,
	 *                client is a worker that tries to insert a new work...)
	 */
	public WorkInterface addWork(final UserInterface client,
			final HostInterface _host, final WorkInterface job)
					throws IOException, InvalidKeyException, AccessControlException {

		final UID jobUID = job.getUID();
		final UserInterface theClient = checkClient(client,
				UserRightEnum.INSERTJOB);

		final UID appUID = job.getApplication();
		if (appUID == null) {
			throw new IOException("insertWork() : job defines no app ?!?");
		}

		final AppInterface theApp = app(theClient, appUID);
		if (theApp == null) {
			throw new IOException("insertWork() : app not found " + appUID);
		}
		final UserInterface appOwner = user(theApp.getOwner());

		if (appOwner == null) {
			throw new IOException("insertWork() : app has no owner " + appUID);
		}

		final UID appOwnerGroup = appOwner.getGroup();

		if ((theApp.canExec(theClient, appOwnerGroup) == false)
				&& (theClient.getRights().lowerThan(UserRightEnum.SUPER_USER))) {
			throw new IOException("insertWork() : " + client.getLogin()
					+ " don't have rights to submit job for app " + appUID);
		}

		job.setService(theApp.isService());

		final XWAccessRights jobRights = (job.getAccessRights() == null ? XWAccessRights.DEFAULT
				: job.getAccessRights());

		final int jobRightsInt = jobRights.value()
				& theApp.getAccessRights().value();
		final XWAccessRights newJobRights = new XWAccessRights(jobRightsInt);
		job.setAccessRights(newJobRights);
		final UserRightEnum clientRights = theClient.getRights();

		if(job.getOwner() == null) {
			job.setOwner(theClient.getUID());
		}

		final WorkInterface theWork = work(theClient, jobUID);
		if (theWork != null) {
			if ((theWork.canWrite(theClient, appOwnerGroup))
					|| (clientRights.higherOrEquals(UserRightEnum.WORKER_USER))) {

				final UID hostUID = (_host == null ? null : _host.getUID());
				final HostInterface theHost = (hostUID == null ? null
						: host(hostUID));
				TaskInterface theTask = null;
				if (theHost != null) {
					if (clientRights.doesEqual(UserRightEnum.WORKER_USER)) {
						if (theHost != null) {
							theTask = task(theWork, theHost);
						}
					} else {
						theTask = task(theWork);
					}
					if (theTask == null) {
						throw new IOException(client.getLogin() + " work "
								+ jobUID + " has no task run by "
								+ _host.getUID());
					}
				}

				if ((theWork.getMinMemory() == 0) || (theWork.getMinMemory() < theApp.getMinMemory())) {
					theWork.setMinMemory(theApp.getMinMemory());
				}
				if ((theWork.getMinCpuSpeed() == 0) || (theWork.getMinCpuSpeed() < theApp.getMinCpuSpeed())) {
					theWork.setMinCpuSpeed(theApp.getMinCpuSpeed());
				}
				if ((theWork.getDiskSpace() == 0)|| (theWork.getDiskSpace() < theApp.getMinFreeMassStorage())) {
					theWork.setDiskSpace(theApp.getMinFreeMassStorage());
				}

				final UserInterface jobOwner = user(theWork.getOwner());
				final UserInterface delegatedClient = (theClient.getRights().doesEqual(UserRightEnum.WORKER_USER) ? jobOwner : theClient); 

				useData(delegatedClient, job.getResult());
				removeData(delegatedClient, theWork.getResult());
				theWork.setResult(job.getResult());

				useData(delegatedClient, job.getStdin());
				removeData(delegatedClient, theWork.getStdin());
				theWork.setStdin(job.getStdin());

				useData(delegatedClient, job.getDirin());
				removeData(delegatedClient, theWork.getDirin());
				theWork.setDirin(job.getDirin());
				try {
					useData(delegatedClient, job.getUserProxy());
				} catch (final AccessControlException e) {
					logger.warn(e.getMessage());
				}
				try {
					removeData(delegatedClient, theWork.getUserProxy());
				} catch (final AccessControlException e) {
					logger.warn(e.getMessage());
				}

				theWork.updateInterface(job);

				final Vector<Table> rows = new Vector<Table>();

				logger.debug(delegatedClient.getLogin() + " is updating "
						+ theWork.getUID() + " status = " + job.getStatus());

				switch (theWork.getStatus()) {
				case RESULTREQUEST:
					theWork.setResultRequest();
					if (theTask != null) {
						theTask.setResultRequest();
					}
					break;
				case DATAREQUEST:
					theWork.setDataRequest();
					if (theTask != null) {
						theTask.setDataRequest();
					}
					break;
				case ABORTED:
					theWork.setPending();
					if (theTask != null) {
						theTask.setPending();
					}
					theApp.decRunningJobs();
					theApp.incPendingJobs();
					jobOwner.decRunningJobs();
					jobOwner.incPendingJobs();
					if (theHost != null) {
						theHost.decRunningJobs();
						if (hostUID.equals(theWork.getExpectedHost())) {
							theHost.incPendingJobs();
						}
					}
					break;
				case COMPLETED:

					theApp.decRunningJobs();
					jobOwner.decRunningJobs();
					if (theHost != null) {
						theHost.decRunningJobs();
					}
					theWork.setResult(job.getResult());
					theWork.setCompleted();
					if (theTask != null) {
						final Date startdate = theTask.getLastStartDate();
						if (startdate != null) {
							final int exectime = (int) (System
									.currentTimeMillis() - startdate.getTime());
							if (theHost != null) {
								theHost.incAvgExecTime(exectime);
							}
							jobOwner.incUsedcputime(exectime);
							theApp.incAvgExecTime(exectime);
						}
						theTask.setCompleted();
					}
					final UID originalUid = theWork.getReplicatedUid();
					if(originalUid != null) {
						synchronized(this) {
							final WorkInterface replicatedWork = work(originalUid);
							final int expectedReplications = replicatedWork.getExpectedReplications();
							final int currentReplications = replicatedWork.getTotalReplica();
							if((currentReplications < expectedReplications) 
									|| (expectedReplications < 0)){
								logger.debug(delegatedClient.getLogin() + " " + originalUid + " still has replications ; currently " + currentReplications  + " ; expected " +  expectedReplications);
								final WorkInterface newWork = new WorkInterface(replicatedWork);
								newWork.setUID(new UID());
								newWork.replicate(originalUid);
								newWork.setTotalReplica(0);
								newWork.setReplicaSetSize(0);
								newWork.setExpectedReplications(0);

								theApp.incPendingJobs();
								jobOwner.incPendingJobs();
								if (hostUID.equals(theWork.getExpectedHost())) {
									theHost.incPendingJobs();
								}

								insert(newWork);
								rows.add(newWork);
								replicatedWork.incTotalReplica();
							}
							replicatedWork.setReplicating();
							if(currentReplications >= replicatedWork.getTotalReplica()) {
								replicatedWork.setCompleted();
							}
							rows.add(replicatedWork);
						}
					} else {
						final int expectedReplications = theWork.getExpectedReplications();
						final int currentReplications = theWork.getTotalReplica();
						if((currentReplications < expectedReplications) 
								|| (expectedReplications < 0)){
							theWork.setReplicating();
						}
					}
					break;
				case ERROR:
					if (theHost != null) {
						theHost.incErrorJobs();
						theHost.decRunningJobs();
					}
					theApp.decRunningJobs();
					theApp.incErrorJobs();
					jobOwner.incErrorJobs();
					jobOwner.decRunningJobs();
					theWork.setError(job.getErrorMsg());
					if (theTask != null) {
						theTask.setError();
					}
					break;
				}

				if (theTask != null) {
					rows.add(theTask);
				}
				rows.add(theWork);
				rows.add(theApp);
				rows.add(jobOwner);
				if (theHost != null) {
					rows.add(theHost);
				}
				sendMail(jobOwner, theWork, delegatedClient.getLogin() + " has updated ");
				update(rows);
			} else {
				throw new AccessControlException(client.getLogin()
						+ " can't update " + jobUID);
			}
		} else {
			if (theClient.getRights() == UserRightEnum.WORKER_USER) {
				throw new AccessControlException(
						"a worker can not insert a new work");
			}
			if (jobUID == null) {
				job.setUID(new UID());
			}
			if (job.getStatus() == null) {
				job.setStatus(StatusEnum.UNAVAILABLE);
			}

			final Vector<Table> rows = new Vector<Table>();

			job.setReplicatedUid(null);
			logger.debug(theClient.getLogin() + " " + jobUID + " replications = " + job.getExpectedReplications() + " by " + job.getReplicaSetSize());

			// if job.getExpectedReplications() < 0, we replicate for ever
			int replica =  job.getExpectedReplications() < 0 ? job.getExpectedReplications() - job.getReplicaSetSize() : 0;
			boolean firstJob = true;
			
			for( ; replica <= job.getReplicaSetSize() && replica <= job.getExpectedReplications(); replica++) {
				final WorkInterface newWork = new WorkInterface(job);
				newWork.setUID(jobUID); // we insert the original work (to eventually be replicated)
				if(firstJob == true) {
					newWork.setTotalReplica(Math.min(job.getReplicaSetSize(), job.getExpectedReplications()));   // this is the original work
					firstJob= false;
				} else {
					newWork.setUID(new UID());  // each eventual replica has its own UID
					newWork.replicate(jobUID);
				}
				newWork.setPending();
				newWork.setArrivalDate(new java.util.Date());
				newWork.setActive(true);
				if ((newWork.getMinMemory() == 0) || (newWork.getMinMemory() > theApp.getMinMemory())) {
					newWork.setMinMemory(theApp.getMinMemory());
				}
				if ((newWork.getMinCpuSpeed() == 0) || (newWork.getMinCpuSpeed() > theApp.getMinCpuSpeed())) {
					newWork.setMinCpuSpeed(theApp.getMinCpuSpeed());
				}
				if ((newWork.getDiskSpace() == 0)|| (newWork.getDiskSpace() > theApp.getMinFreeMassStorage())) {
					newWork.setDiskSpace(theApp.getMinFreeMassStorage());
				}
				insert(newWork);

				useData(theClient, newWork.getResult());
				useData(theClient, newWork.getStdin());
				useData(theClient, newWork.getDirin());

				rows.add(newWork);
				theClient.incPendingJobs();
			}

			rows.add(theClient);
			theApp.incPendingJobs();
			rows.add(theApp);
			update(rows);
		}

		return theWork;
	}

	/**
	 * This retrieves a job status
	 * 
	 * @param client
	 *            describes the requesting client
	 * @param uid
	 *            is the UID of the job to retrieve status for
	 * @return -1 on error, job status otherwise
	 * @exception IOException
	 *                is thrown general error
	 * @exception InvalidKeyException
	 *                is thrown on credential error
	 * @exception AccessControlException
	 *                is thrown on access rights violation
	 */
	public StatusEnum jobStatus(final UserInterface client, final UID uid)
			throws IOException, InvalidKeyException, AccessControlException {

		final UserInterface theClient = checkClient(client,
				UserRightEnum.GETJOB);
		final WorkInterface work = work(theClient, uid);
		final UserInterface owner = user(work.getOwner());
		final UID ownerGroup = (owner == null ? null : owner.getGroup());

		if ((work.canRead(theClient, ownerGroup))
				&& (theClient.getRights().lowerThan(UserRightEnum.SUPER_USER))) {
			throw new AccessControlException(client.getLogin() + " can read "
					+ uid);
		}

		return work.getStatus();
	}

	/**
	 * This retrieves works UIDs for the specified client. Since 7.0.0 non
	 * privileged users get their own jobs only
	 * 
	 * @param client
	 *            is the ClientInterface, describing the client
	 * @param s
	 *            is the status of the expected works
	 * @return null on error; a Collection of UID otherwise
	 * @exception IOException
	 *                is thrown general error
	 * @exception InvalidKeyException
	 *                is thrown on credential error
	 * @exception AccessControlException
	 *                is thrown on access rights violation
	 */
	public Collection<UID> getAllJobs(final UserInterface client,
			final StatusEnum s) throws IOException, InvalidKeyException,
			AccessControlException {

		final UserInterface theClient = checkClient(client,
				UserRightEnum.LISTJOB);

		if (theClient.getRights() == UserRightEnum.WORKER_USER) {
			throw new AccessControlException(client.getLogin()
					+ " : a worker can not list jobs");
		}

		if (theClient.getRights().higherOrEquals(UserRightEnum.ADVANCED_USER)) {
			return worksUID(theClient, s);
		}
		return ownerWorksUID(theClient, s);
	}

	/**
	 * This checks client rights and returns getJob(UserInterface, UID)
	 * 
	 * @see #getJob(UserInterface, UID)
	 */
	protected WorkInterface getJob(final UserInterface client, final UID uid)
			throws IOException, InvalidKeyException, AccessControlException {

		final UserInterface theClient = checkClient(client,
				UserRightEnum.GETJOB);
		return work(theClient, uid);
	}

	/**
	 * This checks client integrity and returns getHost(UserInterface, UID)
	 * 
	 * @see #getHost(UserInterface, UID)
	 */
	protected HostInterface getHost(final UserInterface client, final UID uid)
			throws IOException, InvalidKeyException, AccessControlException {

		final UserInterface theClient = checkClient(client,
				UserRightEnum.GETUSERGROUP);
		return host(theClient, uid);
	}

	/**
	 * This is called by client to disconnect from server This deletes the
	 * client sessions
	 * 
	 * @param client
	 *            defines the client
	 * @exception IOException
	 *                is thrown general error
	 * @exception InvalidKeyException
	 *                is thrown on credential error
	 * @exception AccessControlException
	 *                is thrown on access rights violation
	 */
	public Collection<UID> disconnect(final UserInterface client)
			throws IOException, InvalidKeyException, AccessControlException {

		final Vector<UID> ret = new Vector<UID>();
		final UserInterface theClient = checkClient(client,
				UserRightEnum.DELETESESSION);
		final Collection<UID> sessions = getSessions(theClient,
				theClient.getUID());
		if (sessions != null) {
			final Iterator<UID> enums = sessions.iterator();
			while (enums.hasNext()) {
				UID sessionUID = enums.next();
				ret.addAll(getSessionJobs(theClient, sessionUID));
				removeSession(theClient, sessionUID);
				sessionUID = null;
			}
		}
		return ret;
	}

	/**
	 * This method is remotly called by worker at start up time. It is inserted
	 * in the hashtable if not present. <BR>
	 * If host is allready in the hashtable which may happen if host has
	 * disconnected and is currently trying to reconnect, its datas are
	 * refreshed and its code returned. <BR>
	 * Host is added to SQL table if missing and SQL table is modified as
	 * needed.
	 * 
	 * @param user
	 *            describes host user
	 * @param _host
	 *            describes remote host
	 * @return host(found or created)
	 */
	public HostInterface hostRegister(final UserInterface user,
			final HostInterface _host) throws IOException, InvalidKeyException,
			AccessControlException {

		final String hostName = _host.getName();
		final Date lastAlive = new Date(System.currentTimeMillis());

		if (_host.getUID() == null) {
			throw new IOException("_host UID must not be null!!!");
		}

		_host.setOwner(user.getUID());
		_host.setLastAlive(lastAlive);

		switch (user.getRights()) {
		case STANDARD_USER:
			_host.setAccessRights(XWAccessRights.USERALL);
			break;
		case WORKER_USER:
			if (user.getGroup() != null) {
				_host.setAccessRights(XWAccessRights.OWNERGROUP);
			} else {
				_host.setAccessRights(XWAccessRights.DEFAULT);
			}
			break;
		default:
			throw new AccessControlException("What is that host owner rights ??? :"
					+ user.getRights());
		}

		try {
			final HostInterface host = host(user, _host.getUID());

			if (host != null) {

				if (_host.getUploadBandwidth() != 0) {
					host.setUploadBandwidth(_host.getUploadBandwidth());
				}
				if (_host.getDownloadBandwidth() != 0) {
					host.setDownloadBandwidth(_host.getDownloadBandwidth());
				}
				if (_host.getNbPing() != 0) {
					host.setNbPing(_host.getNbPing());
				}
				if (_host.getAvgPing() != 0) {
					host.setAvgPing(_host.getAvgPing());
				}
				host.setSharedApps(_host.getSharedApps());
				host.setSharedDatas(_host.getSharedDatas());
				host.setSharedPackages(_host.getSharedPackages());
				host.setIncomingConnections(_host.incomingConnections());

				host.setProject(_host.getProject());
				host.setIPAddr(_host.getIPAddr());
				host.setNatedIPAddr(_host.getNatedIPAddr());

				host.setJobId(_host.getJobId());
				host.setBatchId(_host.getBatchId());

				host.setAccessRights(_host.getAccessRights());
				host.setVersion(_host.getVersion());
				host.setOs(_host.getOs());
				host.setOsVersion(_host.getOsVersion());
				host.setJavaVersion(_host.getJavaVersion());
				host.setJavaDataModel(_host.getJavaDataModel());
				host.setCpu(_host.getCpu());
				host.setCpuModel(_host.getCpuModel());
				host.setLastAlive(_host.getLastAlive());
				host.setAvailable(_host.isAvailable());
				host.setSgId(_host.getSgId());
				host.setPoolWorkSize(_host.getPoolWorkSize());
				host.setCpuSpeed(_host.getCpuSpeed());
				host.setFreeTmp(_host.getFreeTmp());
				host.setTotalMem(_host.getTotalMem());

				update(host);
				return host;
			} else {
				try {
					logger.info(hostName + " not in DB; inserting "
							+ _host.getUID().toString());
					_host.setCurrentVersion();
					insert(_host);
					return _host;
				} catch (final Exception e) {
					logger.exception(hostName + " can't create new host", e);
				}
			}
		} catch (final Exception e) {
			logger.exception(e);
			logger.debug("new connection");
		}

		return null;

	}

	/**
	 * Set worker attribute.
	 * 
	 * @param theClient
	 *            is the requesting user
	 * @param uid
	 *            is the worker UID to change flag for
	 * @param column
	 *            defines either TRACES or ACTIVE attribute to change
	 * @param flag
	 *            is the value to set the attribute to
	 * @return true on success; false otherwise.
	 */
	protected boolean changeWorker(final UserInterface theClient,
			final UID uid, final HostInterface.Columns column,
			final boolean flag) throws IOException {

		final HostInterface host = host(theClient, uid);
		if (host == null) {
			return false;
		}
		switch (column) {
		case TRACES:
			host.setTracing(flag);
			break;
		case ACTIVE:
			host.setActive(flag);
			break;
		}

		update(host);
		return true;
	}

	/**
	 * Set workers attribute.
	 * 
	 * @param hosts
	 *            is a hashtable which contains host UID as key and their
	 *            dedicated activate flag as value.
	 * @param column
	 *            defines either TRACES or ACTIVE attribute to change
	 * @return true on success; false otherwise.
	 * @exception IOException
	 *                is thrown general error
	 * @exception InvalidKeyException
	 *                is thrown on credential error
	 * @exception AccessControlException
	 *                is thrown on access rights violation
	 */
	public boolean changeWorkers(final UserInterface client,
			final Hashtable<String, Boolean> hosts, final HostInterface.Columns column)
					throws IOException, InvalidKeyException, AccessControlException {

		final UserInterface theClient = checkClient(client,
				UserRightEnum.SUPER_USER);

		if (hosts.isEmpty()) {
			return false;
		}
		final Enumeration<String> enums = hosts.keys();
		while (enums.hasMoreElements()) {
			final UID uid = new UID(enums.nextElement());
			final Boolean flag = hosts.get(uid);
			if (!changeWorker(theClient, uid, column, flag.booleanValue())) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Set active attibute for a given worker
	 * 
	 * @param uid
	 *            is the host UID
	 * @param flag
	 *            is the active attribute value
	 * @return true on success; false otherwise.
	 * @exception IOException
	 *                is thrown general error
	 * @exception InvalidKeyException
	 *                is thrown on credential error
	 * @exception AccessControlException
	 *                is thrown on access rights violation
	 */
	public boolean activateWorker(final UserInterface client, final UID uid,
			final boolean flag) throws IOException, InvalidKeyException,
			AccessControlException {

		final UserInterface theClient = checkClient(client,
				UserRightEnum.SUPER_USER);
		return changeWorker(theClient, uid, HostInterface.Columns.ACTIVE, flag);
	}

	/**
	 * Set active flag for a given workers list.
	 * 
	 * @param hosts
	 *            is a hashtable which contains host uid as key and their
	 *            dedicated activate flag as value.
	 * @return true on success; false otherwise.
	 */
	public boolean activateWorkers(final UserInterface client,
			final Hashtable<String, Boolean> hosts) throws IOException, InvalidKeyException,
			AccessControlException {
		return changeWorkers(client, hosts, HostInterface.Columns.ACTIVE);
	}

	/**
	 * This activate some workers.
	 * 
	 * @param nbWorkers
	 *            is the number of workers to activate.
	 * @return the number of activated workers on success, -1 on error
	 * @exception IOException
	 *                is thrown general error
	 * @exception InvalidKeyException
	 *                is thrown on credential error
	 * @exception AccessControlException
	 *                is thrown on access rights violation
	 */
	public int activateWorkers(final UserInterface client, final int nbWorkers)
			throws InvalidKeyException, AccessControlException, IOException {

		int count = 0;

		final UserInterface theClient = checkClient(client,
				UserRightEnum.SUPER_USER);
		final Collection<HostInterface> hosts = hosts(theClient);
		for (final Iterator<HostInterface> enums = hosts.iterator(); enums
				.hasNext();) {
			final HostInterface host = enums.next();
			if (count < nbWorkers) {
				host.setActive(true);
				count++;
			} else {
				host.setActive(false);
			}
		}
		return count;
	}

	/**
	 * Set trace flag for a given workers list.
	 * 
	 * @param hosts
	 *            is a hashtable which contains host uid as key and their
	 *            dedicated trace flag as value.
	 * @return true on success; false otherwise.
	 */
	public boolean traceWorkers(final UserInterface client,
			final Hashtable<String, Boolean> hosts) throws IOException, InvalidKeyException,
			AccessControlException {
		return changeWorkers(client, hosts, HostInterface.Columns.TRACES);
	}

	/**
	 * This retrieves all registered workers (all workers that have been
	 * connected at least once)
	 * 
	 * @return a Collection of UID
	 * @exception IOException
	 *                is thrown general error
	 * @exception InvalidKeyException
	 *                is thrown on credential error
	 * @exception AccessControlException
	 *                is thrown on access rights violation
	 */
	public Collection<UID> getRegisteredWorkers(UserInterface client)
			throws IOException, InvalidKeyException, AccessControlException {

		final UserInterface theClient = checkClient(client,
				UserRightEnum.LISTHOST);

		final Vector<UID> ret = new Vector<UID>(100, 50);

		final Collection<HostInterface> hosts = hosts(theClient);
		final Iterator<HostInterface> enums = hosts.iterator();
		while (enums.hasNext()) {
			ret.add(enums.next().getUID());
		}
		return ret;
	}

	/**
	 * This retrieves all alive workers (all workers that have been connected
	 * this last ALIVE period)
	 * 
	 * @return a Collection of UID
	 * @since 5.8.0
	 * @exception IOException
	 *                is thrown general error
	 * @exception InvalidKeyException
	 *                is thrown on credential error
	 * @exception AccessControlException
	 *                is thrown on access rights violation
	 */
	public Collection<UID> getAliveWorkers(final UserInterface client)
			throws IOException, InvalidKeyException, AccessControlException {

		final UserInterface theClient = checkClient(client,
				UserRightEnum.LISTHOST);

		if (theClient.getRights() == UserRightEnum.WORKER_USER) {
			throw new AccessControlException(client.getLogin()
					+ " : a worker can not list workers");
		}

		return hostsUID(theClient,
				"(unix_timestamp(now())-unix_timestamp(lastalive) < 1000)");
		/*
		 * Vector<UID> ret = new Vector(100, 50);
		 * 
		 * Vector<Host> hosts = hosts(theClient); if(hosts != null) {
		 * Enumeration<Host> enums = hosts.elements();
		 * while(enums.hasMoreElements()) {
		 * ret.add(enums.nextElement().getUID()); } } return ret;
		 */
	}

	/**
	 * This retrieves server the provided worker should connect to, if any. <br>
	 * The new server is provided to worker through 'Alive' signal. April 4th,
	 * 2003 : no policy is defined yet :(
	 * 
	 * @param workerUID
	 *            is the worker host UID
	 * @since v1r2-rc1(RPC-V)
	 */
	public String getServer(UID workerUID) {
		return null;
	}
}
