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

package xtremweb.services.rpc;

/**
 * Date : Mar 25th, 2005 Project : RPCXW / RPCXW-C File : rpcdefs.java
 * 
 * @author <a href="mailto:lodygens_a_lal.in2p3.fr">Oleg Lodygensky</a>
 * @version %I% %G%
 */

public class rpcdefs {

	/**
	 * This defines the NFS prog number This has been extracted from /etc/rpc
	 */
	public static final int RPC_NFS = 100003;
	/**
	 * This defines the NFS prog number This has been extracted from /etc/rpc
	 */
	public static final int RPC_MOUNT = 100005;

	/**
	 * These define the NFS procedures
	 */
	public static final int NFSPROC_NULL = 0;
	public static final int NFSPROC_GETATTR = 1;
	public static final int NFSPROC_SETATTR = 2;
	public static final int NFSPROC_ROOT = 3;
	public static final int NFSPROC_LOOKUP = 4;
	public static final int NFSPROC_READLINK = 5;
	public static final int NFSPROC_READ = 6;
	public static final int NFSPROC_WRITECACHE = 7;
	public static final int NFSPROC_WRITE = 8;
	public static final int NFSPROC_CREATE = 9;
	public static final int NFSPROC_REMOVE = 10;
	public static final int NFSPROC_RENAME = 11;
	public static final int NFSPROC_LINK = 12;
	public static final int NFSPROC_SYMLINK = 13;
	public static final int NFSPROC_MKDIR = 14;
	public static final int NFSPROC_RMDIR = 15;
	public static final int NFSPROC_READDIR = 16;
	public static final int NFSPROC_STATFS = 17;
	/**
	 * This defines NFS proc in String, for debug purposes
	 */
	public final static String[] NFSPROC_TEXT = { "NFSPROC_NULL",
			"NFSPROC_GETATTR", "NFSPROC_SETATTR", "NFSPROC_ROOT",
			"NFSPROC_LOOKUP", "NFSPROC_READLINK", "NFSPROC_READ",
			"NFSPROC_WRITECACHE", "NFSPROC_WRITE", "NFSPROC_CREATE",
			"NFSPROC_REMOVE", "NFSPROC_RENAME", "NFSPROC_LINK",
			"NFSPROC_SYMLINK", "NFSPROC_MKDIR", "NFSPROC_RMDIR",
			"NFSPROC_READDIR", "NFSPROC_STATFS" };

	/**
	 * This defines the null MOUNT proc
	 */
	public static final int MOUNTPROC_NULL = 0;
	/**
	 * This defines the mount MOUNT proc This takes one parameter : the dirpath
	 */
	public static final int MOUNTPROC_MNT = 1;
	/**
	 * This defines the dump MOUNT proc
	 */
	public static final int MOUNTPROC_DUMP = 2;
	/**
	 * This defines the unmount MOUNT proc This takes one parameter : the
	 * dirpath
	 */
	public static final int MOUNTPROC_UMNT = 3;
	/**
	 * This defines the umountall MOUNT proc
	 */
	public static final int MOUNTPROC_UMNTALL = 4;
	/**
	 * This defines the export MOUNT proc
	 */
	public static final int MOUNTPROC_EXPORT = 5;
	/**
	 * This defines the export all MOUNT proc
	 */
	public static final int MOUNTPROC_EXPORTALL = 6;

	/**
	 * This defines MOUNT proc in String, for debug purposes
	 */
	public final static String[] MOUNTPROC_TEXT = { "MOUNTPROC_NULL",
			"MOUNTPROC_MNT", "MOUNTPROC_DUMP", "MOUNTPROC_UMNT",
			"MOUNTPROC_UMNTALL", "MOUNTPROC_EXPORT", "MOUNTPROC_EXPORTALL" };

	/**
	 * This defines the null NFS service authentication
	 */
	public static final int AUTH_NULL = 0;;
	/**
	 * This defines the NFS service authentication AUTH_UNIX
	 */
	public static final int AUTH_UNIX = 1;
	/**
	 * This defines the NFS service authentication AUTH_SHORT
	 */
	public static final int AUTH_SHORT = 2;
	/**
	 * This defines the NFS service authentication AUTH_DES
	 */
	public static final int AUTH_DES = 3;

	/**
	 * This defines AUTH in String, for debug purposes
	 */
	public final static String[] AUTH_TEXT = { "AUTH_NULL", "AUTH_UNIX",
			"AUTH_SHORT", "AUTH_DES" };

	/**
	 * This defines an RPC call
	 */
	public static final int RPC_CALL = 0;
	/**
	 * This defines an RPC reply
	 */
	public static final int RPC_REPLY = 1;

	/**
	 * This defines MOUNT proc in String, for debug purposes
	 */
	public final static String[] RPC_TEXT = { "CALL", "REPLY" };
}
