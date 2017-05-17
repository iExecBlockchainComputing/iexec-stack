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

package xtremweb.communications;

/**
 * @since RPC-V
 * @author <a href="mailto:lodygens a t lal.in2p3.fr">Oleg Lodygensky</a>
 *
 *         This class defines ports used by XtremWeb
 */

public enum Connection {

	/**
	 * This is the TCP port (with or without SSL) with no client challenge
	 *
	 * @see #TCPSPORT
	 */
	TCPPORT {
		@Override
		public String layer() {
			return xtremweb.communications.TCPClient.class.getName();
		}

		/**
		 * This retrieves a property name as found in config file This is for
		 * backward compatibility only
		 *
		 * @since 8.2.0
		 */
		@Override
		public String propertyName() {
			return "port.tcp";
		}
	},
	/**
	 * This is used to challenge client (using private/public keys)
	 */
	TCPSPORT {
		@Override
		public String layer() {
			return xtremweb.communications.TCPClient.class.getName();
		}

		/**
		 * This retrieves a property name as found in config file This is for
		 * backward compatibility only
		 *
		 * @since 8.2.0
		 */
		@Override
		public String propertyName() {
			return "port.ssl.tcp";
		}
	},
	/**
	 * This is the UDP port (with or without SSL)
	 */
	UDPPORT {
		@Override
		public String layer() {
			return xtremweb.communications.UDPClient.class.getName();
		}

		/**
		 * This retrieves a property name as found in config file This is for
		 * backward compatibility only
		 *
		 * @since 8.2.0
		 */
		@Override
		public String propertyName() {
			return "port.udp";
		}
	},
	/**
	 * This is the HTTP port to connect to the worker management page (so that
	 * the resource owner can see and configure its local worker and even stop
	 * it)
	 *
	 * SSL is not used
	 */
	HTTPWORKERPORT {
		@Override
		public String layer() {
			return xtremweb.communications.HTTPClient.class.getName();
		}

		/**
		 * This retrieves a property name as found in config file This is for
		 * backward compatibility only
		 *
		 * @since 8.2.0
		 */
		@Override
		public String propertyName() {
			return "port.worker.http";
		}
	},
	/**
	 * This is the HTTP port (if SSLKeystore is not set)
	 */
	HTTPPORT {
		@Override
		public String layer() {
			return xtremweb.communications.HTTPClient.class.getName();
		}

		@Override
		public int defaultPortValue() {
			return HTTPDEFAULTPORT;
		}

		/**
		 * This retrieves a property name as found in config file This is for
		 * backward compatibility only
		 *
		 * @since 8.2.0
		 */
		@Override
		public String propertyName() {
			return "port.http";
		}
	},
	/**
	 * This is the HTTPS port (if SSLKeystore is set)
	 */
	HTTPSPORT {
		@Override
		public String layer() {
			return xtremweb.communications.HTTPClient.class.getName();
		}

		@Override
		public int defaultPortValue() {
			return HTTPSDEFAULTPORT;
		}

		/**
		 * This retrieves a property name as found in config file This is for
		 * backward compatibility only
		 *
		 * @since 8.2.0
		 */
		@Override
		public String propertyName() {
			return "port.https";
		}
	},
	/**
	 * This is the client shell port
	 */
	XMLRPCPORT {
		@Override
		public String layer() {
			return xtremweb.communications.TCPClient.class.getName();
		}

		/**
		 * This retrieves a property name as found in config file This is for
		 * backward compatibility only
		 *
		 * @since 8.2.0
		 */
		@Override
		public String propertyName() {
			return "port.xmlrpc";
		}
	},
	/**
	 * This is used by xtremweb.rpcd.client.rpcudp
	 */
	SUNRPCPORT {
		@Override
		public String layer() {
			return xtremweb.communications.UDPClient.class.getName();
		}

		/**
		 * This retrieves a property name as found in config file This is for
		 * backward compatibility only
		 *
		 * @since 8.2.0
		 */
		@Override
		public String propertyName() {
			return "port.sunrpc";
		}
	},
	/**
	 * This is the smartsocket proxy port
	 *
	 * @since 8.0.0
	 */
	SMARTSOCKETSPORT {
		@Override
		public String layer() {
			return xtremweb.communications.TCPClient.class.getName();
		}

		/**
		 * This retrieves a property name as found in config file This is for
		 * backward compatibility only
		 *
		 * @since 8.2.0
		 */
		@Override
		public String propertyName() {
			return HUBPORTPNAME;
		}
	},
	/**
	 * This is the proxy port used to connect to XW server
	 */
	PROXYPORT {
		@Override
		public String layer() {
			return xtremweb.communications.TCPClient.class.getName();
		}

		/**
		 * This retrieves a property name as found in config file This is for
		 * backward compatibility only
		 *
		 * @since 8.2.0
		 */
		@Override
		public String propertyName() {
			return "port.proxy";
		}
	};

	/**
	 * This is the property name for the hub address
	 *
	 * @since 8.0.0
	 */
	public static final String HUBPNAME = "smartsockets.hub.addresses";
	/**
	 * This is the property name for the hub port
	 *
	 * @since 8.0.0
	 */
	public static final String HUBPORTPNAME = "smartsockets.hub.port";
	/**
	 * This is the property name for the port range
	 *
	 * @since 8.0.0
	 */
	public static final String PORTPNAME = "smartsockets.port.range";
	/**
	 * This defines the unsecured XtremWeb URI scheme name
	 */
	private static final String XWSCHEME = "xw";

	/**
	 * This returns XWSCHEME
	 *
	 * @return XWSCHEME
	 */
	public static String xwScheme() {
		return XWSCHEME;
	}

	/**
	 * This defines the secured XtremWeb URI scheme name
	 */
	private static final String XWSSLSCHEME = "xws";

	/**
	 * This returns XWSSLSCHEME
	 *
	 * @return XWSSLSCHEME
	 */
	public static String xwsScheme() {
		return XWSSLSCHEME;
	}

	/**
	 * This is '://'
	 */
	public static final String SCHEMESEPARATOR = "://";

	/**
	 * This returns SCHEMESPERATAOR
	 *
	 * @return SCHEMESEPARATOR
	 */
	public static String getSchemeSeparator() {
		return SCHEMESEPARATOR;
	}

	/**
	 * This defines the unsecured ATTIC URI scheme name
	 */
	private static final String ATTICSCHEME = "attic";

	/**
	 * This returns ATTICSCHEME
	 *
	 * @return ATTICSCHEME
	 */
	public static String atticScheme() {
		return ATTICSCHEME;
	}

	/**
	 * This defines the unsecured HTTP URI scheme name
	 */
	private static final String HTTPSCHEME = "http";

	/**
	 * This returns HTTPSCHEME
	 *
	 * @return HTTPSCHEME
	 */
	public static String httpScheme() {
		return HTTPSCHEME;
	}

	/**
	 * This defines the unsecured HTTP URI scheme name
	 */
	public static final String HTTPSSLSCHEME = "https";

	/**
	 * This returns HTTPSSLSCHEME
	 *
	 * @return HTTPSSLSCHEME
	 */
	public static String httpsScheme() {
		return HTTPSSLSCHEME;
	}

	/**
	 * This defines the file URI scheme name
	 */
	private static final String FILESCHEME = "file";

	/**
	 * This returns FILESCHEME
	 *
	 * @return FILESCHEME
	 */
	public static String fileScheme() {
		return FILESCHEME;
	}

	/**
	 * This defines the Web standard HTTP port : 80
	 */
	public static final int HTTPDEFAULTPORT = 80;
	/**
	 * This defines the Web standard HTTPS port : 443
	 */
	public static final int HTTPSDEFAULTPORT = 443;
	/**
	 * This defines the first port used by XtremWeb Other ports are defined as
	 * BASEPORT + Connection.ordinal()
	 */
	public static final int BASEPORT = 4321;

	/**
	 * This defines port given is property index
	 *
	 * @return BASEPORT + this.ordinal()
	 * @see #BASEPORT
	 */
	public int defaultPortValue() {
		return BASEPORT + this.ordinal();
	}

	/** This retrieves the communication layer to use for the given port */
	public abstract String layer();

	/**
	 * This retrieves the property name used in the configuration file for the
	 * given port
	 */
	public abstract String propertyName();

	public static Connection fromLayer(final String layer) throws IndexOutOfBoundsException {
		for (final Connection c : Connection.values()) {
			if (c.layer().compareTo(layer) == 0) {
				return c;
			}
		}
		throw new IndexOutOfBoundsException("invalid layer " + layer);
	}

	/**
	 * This is the standard well known port limit
	 *
	 * @since 8.0.0
	 */
	public static final int PRIVILEGEDPORT = 1024;
}
