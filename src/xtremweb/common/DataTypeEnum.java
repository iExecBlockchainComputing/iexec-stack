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

import java.io.File;

/**
 * DataTypeEnumEnum.java
 *
 * Created: 19 juillet 2006
 *
 * @author <a href="mailto:lodygens /at\ .in2p3.fr>Oleg Lodygensky</a>
 * @since 1.8.0
 * @version %I%, %G%
 */

/**
 * This class describes static variables that define user right levels
 */

public enum DataTypeEnum {

	NONE {
		@Override
		public String fileExtension() {
			return null;
		}

		@Override
		public String mimeType() {
			return null;
		}
	},
	/** This denotes a binary file */
	BINARY {
		@Override
		public String fileExtension() {
			return ".exe";
		}

		@Override
		public String mimeType() {
			return MIMETYPEAPP;
		}
	},
	/**
	 * This denotes a binary file
	 * 
	 * @since 9.0.0
	 */
	LIBRARY {
		@Override
		public String fileExtension() {
			return ".dll";
		}

		@Override
		public String mimeType() {
			return MIMETYPEAPP;
		}
	},
	/**
	 * This denotes an ISO file
	 * 
	 * @since 8.0.0
	 */
	ISO {
		@Override
		public String fileExtension() {
			return ".iso";
		}

		@Override
		public String mimeType() {
			return MIMETYPEAPP;
		}
	},
	/**
	 * This denotes a VDI file
	 * 
	 * @since 8.0.0
	 */
	VDI {
		@Override
		public String fileExtension() {
			return ".vdi";
		}

		@Override
		public String mimeType() {
			return MIMETYPEAPP;
		}
	},
	/**
	 * This denotes a VMDK file
	 * 
	 * @since 8.0.0
	 */
	VMDK {
		@Override
		public String fileExtension() {
			return ".vmdk";
		}

		@Override
		public String mimeType() {
			return MIMETYPEAPP;
		}
	},
	/** This denotes a java file */
	JAVA {
		@Override
		public String fileExtension() {
			return ".jar";
		}

		@Override
		public String mimeType() {
			return MIMETYPEAPP;
		}
	},
	/** This denotes a text file */
	TEXT {
		@Override
		public String fileExtension() {
			return ".txt";
		}

		@Override
		public String mimeType() {
			return MIMETYPETEXT;
		}
	},
	/** This denotes a compress file */
	ZIP {
		@Override
		public String fileExtension() {
			return ".zip";
		}

		@Override
		public String mimeType() {
			return MIMETYPEZIP;
		}
	},
	/**
	 * This denotes a text file containing a list of URI to download
	 * 
	 * @since 8.0.0
	 */
	URIPASSTHROUGH {
		@Override
		public String fileExtension() {
			return ".uris";
		}

		@Override
		public String mimeType() {
			return MIMETYPETEXT;
		}
	},
	/**
	 * This denotes an sh script
	 * 
	 * @since 8.0.0
	 */
	SH {
		@Override
		public String fileExtension() {
			return ".sh";
		}

		@Override
		public String mimeType() {
			return MIMETYPETEXT;
		}
	},
	/**
	 * This denotes a BAT script
	 * 
	 * @since 8.0.0
	 */
	BAT {
		@Override
		public String fileExtension() {
			return ".bat";
		}

		@Override
		public String mimeType() {
			return MIMETYPETEXT;
		}
	},
	/** This denotes an X509 certificate file */
	X509 {
		@Override
		public String fileExtension() {
			return ".pem";
		}

		@Override
		public String mimeType() {
			return MIMETYPETEXT;
		}
	},
	/** This denotes an UDP packet file */
	UDPPACKET {
		@Override
		public String fileExtension() {
			return ".udp";
		}

		@Override
		public String mimeType() {
			return MIMETYPEAPP;
		}
	},
	/** This denotes a stream */
	STREAM {
		@Override
		public String fileExtension() {
			return ".tcp";
		}

		@Override
		public String mimeType() {
			return MIMETYPESTREAM;
		}
	};

	public static final DataTypeEnum LAST = STREAM;
	public static final int SIZE = LAST.ordinal() + 1;

	final String MIMETYPEAPP = "application/octet-stream";
	final String MIMETYPETEXT = "text/plain";
	final String MIMETYPEZIP = "application/zip";
	final String MIMETYPESTREAM = "application/octet-stream";

	/**
	 * This is the default work result data name. Default result data name :
	 * ResultsOf_
	 */
	public static final String RESULTHEADER = "ResultsOf_";

	/**
	 * This retrieves this enum file extension
	 * 
	 * @return a string containing file extension or null if not applicable
	 * @since 9.0.0
	 */
	public abstract String fileExtension();

	/**
	 * This retrieves this enum mime type
	 * 
	 * @return a string containing mime type or null if not applicable
	 * @since 9.0.0
	 */
	public abstract String mimeType();

	/**
	 * This retrieves all labels
	 * 
	 * @return a array containing this enum string representation
	 */
	public static String[] getLabels() {

		final String[] labels = new String[SIZE];
		for (final DataTypeEnum c : DataTypeEnum.values()) {
			labels[c.ordinal()] = c.toString();
		}
		return labels;
	}

	/**
	 * This retrieves this enum file extension
	 * 
	 * @return a string containing file extension
	 * @see #fileExtension()
	 */
	public String getFileExtension() {
		return this.fileExtension();
	}

	/**
	 * This retrieves this enum MIME type
	 * 
	 * @return a string containing the MIME type
	 * @see #mimeType()
	 * @since 8.1.0
	 */
	public String getMimeType() {
		return this.mimeType();
	}

	/**
	 * This retrieves the data file type accordingly to its file extension (i.e.
	 * .jar returns JAVA)
	 * @since 9.1.0
	 * @return the data file type
	 */
	public static DataTypeEnum getFileType(final String filePath) {
		if (filePath == null) {
			return null;
		}
		return getFileType(new File(filePath));
	}
	/**
	 * This retrieves the data file type accordingly to its file extension (i.e.
	 * .jar returns JAVA)
	 * 
	 * @return the data file type
	 */
	public static DataTypeEnum getFileType(final File file) {
		if (file == null) {
			return null;
		}
		for (final DataTypeEnum i : DataTypeEnum.values()) {
			if (i == NONE) {
				continue;
			}
			if (file.getName().toLowerCase().endsWith(i.getFileExtension())) {
				return i;
			}
		}
		return null;
	}

	/**
	 * This retrieves an DataTypeEnum from its integer value
	 * 
	 * @param v
	 *            is the integer value of the DataTypeEnum
	 * @return a DataTypeEnum
	 */
	public static DataTypeEnum fromInt(int v) throws IndexOutOfBoundsException {
		for (final DataTypeEnum i : DataTypeEnum.values()) {
			if (i.ordinal() == v) {
				return i;
			}
		}
		throw new IndexOutOfBoundsException("unvalid DataTypeEnum value " + v);
	}

	/**
	 * This is for debug purposes
	 * 
	 * @param argv
	 */
	public static void main(String[] argv) {
		for (final DataTypeEnum i : DataTypeEnum.values()) {
			System.out.println(i.toString());
		}
	}

}
