<?php

/*
 * File   : communications/TCPClient.php
 * Date   : 11/04/2006
 * Author : Oleg Lodygensky
 * Email  : lodygens@lal.in2p3.fr
 *
 * This implements TCP client in PHP
 */


if(!defined("_TCPClient_php_")) {
  define ("_TCPClient_php_", true);

	/**
	 * This class is a copy of src/communications/IdRpc.java
	 */
	public class IdRpc {

		var SEPARATOR = 0;

    var FIRST = 0;

    var NULL = FIRST;

    var DISCONNECT = 1;
		var CLOSE = 2;
		var SENDAPP = 3;		 
		var GETAPPBYNAME = 4;		 
		var GETAPPBYUID = 5;		 
		var LOADAPP = 6;
		var GETAPPS = 7;
		var REMOVEAPP = 8;
		var SENDGROUP = 9;
		var GETGROUP = 10;
		var GETGROUPS = 11;
    var GETGROUPWORKS = 12;		 
		var REMOVEGROUP = 13;
		var SENDWORKER = 14;
		var GETWORKER = 15;
		var GETWORKERS = 16;
		var ACTIVATEWORKER = 17;
		var REMOVEWORKER = 18;
		var SETWORKERSPARAMETERS = 19;
		var GETWORKERSPARAMETERS = 20;
    var SETWORKERSNB = 21;
		var SENDSESSION = 22;
		var GETSESSION = 23;
		var GETSESSIONS = 24;
    var GETSESSIONWORKS = 25;
		var REMOVESESSION = 26;
		var SENDTASK = 27;
		var GETTASK = 28;
		var GETTASKS = 29;
		var REMOVETASK = 30;
		var SENDTRACE = 31;
		var GETTRACE = 32;
		var GETALLTRACES = 33;
		var GETTRACES = 34;
		var REMOVETRACE = 35;
		var SENDUSERGROUP = 36;
		var GETUSERGROUP = 37;
		var GETUSERGROUPS = 38;
		var REMOVEUSERGROUP = 39;
		var SENDUSER = 40;
		var GETUSERBYUID = 41;
		var GETUSERBYLOGIN = 42;
		var GETUSERS = 43;
		var REMOVEUSER = 44;
		var SENDWORK = 45;
		var SUBMITWORK = 46;
		var BROADCASTWORK = 47;
    var SENDMOBILEWORK = 48;
		var GETWORK = 49;
		var WORKREQUEST = 50;
		var LOADWORK = 51;
		var GETWORKS = 52;
		var REMOVEWORK = 53;
		var REMOVEWORKS = 54;
    var GETRESULT = 55;
    var SENDRESULT = 56;
		var WORKALIVEBYUID = 57;
		var WORKALIVE = 58;
		var GETWORKERBIN = 59;
		var TACTIVITYMONITOR = 60;
		var GETTRUSTEDADDRESSES = 61;
		var ADDTRUSTEDADDRESS = 62;
		var REMOVETRUSTEDADDRESS = 63;
		var TRACEWORKERS = 64;
		var MOUNT = 65;
		var UMOUNT = 66;
		var SHUTDOWN = 67;

		var LAST = SHUTDOWN;
		var MAX = LAST + 1;


	}



	/**
	 * This implements src/communications/TCPClient.java in PHP
	 */
	class TCPClient {	

		var opened;
		var serverName;
		var socket;
		var connectionLess;

		/**
		 * This is the contructor
		 */
		function TCPClient(serverName) {
			$this->opened = FALSE;
			$this->connectionLess = TRUE;
			$this->serverName = serverName;
			$this->socket = FALSE;
		}

		/**
		 * This opens connection to server
		 */
		function open() {

			if(opened == true)
				return;

			$port = 4325;

			socket = socket_create(AF_INET, SOCK_STREAM);
			if(socket == FALSE) {
				return FALSE;
			}

			if(socket_connect(socket, configGetDispatcherHost(), 4325) == FALSE) {
				return FALSE;
			}
			opened = true;
		}

		/**
		 * This closes communication channel
		 */
		function close() {

			if($this->opened == false)
				return;

			open();
			io.writeInt($IdRpc->CLOSE);

			socket_close($this->socket);

			$this->socket = false;
			$this->opened = false;
		}

		/**
		 * This sends the IrRpc code and the UserInterface
		 * <ul>
		 * <li>This sends
		 *  <ol>
		 *   <li> an integer as defined in src/communications/IdRpc.java
		 *   <li> <code>src/common/UserInterface.java</code> XML representation
		 *  </ol>
		 * <li>This does not close communication channel
		 * </ul>
		 * @param code is the IdRpc code to send
		 * @see #xtremweb.communications.IdRpc
		 * @see #xtremweb.common.XMLable
		 * @see xtremweb.common.StreamIO#writeObject(XMLable)
		 */
		function sendUserInterface(int code) throws RemoteException {
				open();
				io.writeInt(code);
				io.writeObject(config.ids.user);
		}
		/**
		 * This sends the IrRpc code, the UserInterface that identifies the client
		 * and an optionnal parameter
		 * <ul>
		 * <li>This sends
		 *  <ol>
		 *   <li> objects sent by sendUserInterface(int)
		 *   <li> a serialized java.lang.String 
		 *  </ol>
		 * <li>This does not close communication channel
		 * </ul>
		 * @param code is the IdRpc code to send
		 * @param obj is the parameter to send
		 * @see #sendUserInterface(int)
		 */
 		function sendUserInterface(int code, String obj) throws RemoteException {
 				sendUserInterface(code);
 				if(obj != null) {
 						io.writeString(obj);
 				}
 		}
		/**
		 * This creates or updates an work on server side.<br />
		 * This sends objects sent by sendUserInterface(int, XMLable)<br />
		 * This receives nothing<br />
		 * This closes communication channel
		 * @param obj is the object to send
		 * @see #sendUserInterface(int, XMLable)
		 */
		function sendWork(WorkInterface obj) throws RemoteException {
				sendUserInterface(IdRpc.SENDWORK, obj);
				close();
		}
		/**
		 * This deletes a work from server.<br />
		 * This sends objects sent by sendUserInterface(int, UID)<br />
		 * This receives nothing<br />
		 * This closes communication channel
		 * @param uid is the UID of the object to remove
		 * @see #sendUserInterface(int, UID)
		 */
		function removeWork(uid) {
				sendUserInterface(IdRpc.REMOVEWORK, uid);
				close();
		}
	}
}
?>
