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
 * Project : XTremWeb
 * File    : TracerZipFile.java
 *
 * Initial revision : July 2001
 * By               : Anna Lawer
 *
 * New revision : January 2002
 * By           : Oleg Lodygensky
 * e-mail       : lodygens /at\ .in2p3.fr
 */

package xtremweb.common;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class TracerZipFile {

	private ZipOutputStream outfile = null;
	private ZipFile infile = null;

	/**
	 * This is the constructor to create read only file.
	 */
	public TracerZipFile(String filename) {
		try {
			infile = new ZipFile(filename);
		} catch (final Exception e) {
			System.err.println(e.toString());
		}
	}

	/**
	 * This is the constructor to create write only file. It inserts header (ie
	 * configuration) in zip file.
	 * 
	 * @param filename
	 *            is the name of the file to create.
	 * @param host
	 *            contains the host definition.
	 * @param resultDelay
	 *            contains the trace period.
	 * @param sendResultDelay
	 *            contains the send trace period.
	 */
	public TracerZipFile(String filename, HostInterface host, int resultDelay,
			int sendResultDelay) {

		try {
			outfile = new ZipOutputStream(new FileOutputStream(filename));

			ZipEntry zipEntry;

			final String lineFeed = new String("\n\r");

			zipEntry = new ZipEntry("Configuration");
			outfile.putNextEntry(zipEntry);

			outfile.write(host.getName().getBytes(XWTools.UTF8));
			outfile.write(lineFeed.getBytes(XWTools.UTF8));

			outfile.write(host.getTimeZone().getBytes(XWTools.UTF8));
			outfile.write(lineFeed.getBytes(XWTools.UTF8));

			outfile.write(host.getIPAddr().getBytes(XWTools.UTF8));
			outfile.write(lineFeed.getBytes(XWTools.UTF8));

			outfile.write(host.getHWAddr().getBytes(XWTools.UTF8));
			outfile.write(lineFeed.getBytes(XWTools.UTF8));

			final String version = host.getVersion();
			outfile.write(version.getBytes(XWTools.UTF8));
			outfile.write(lineFeed.getBytes(XWTools.UTF8));

			outfile.write(String.valueOf(resultDelay).getBytes(XWTools.UTF8));
			outfile.write(lineFeed.getBytes(XWTools.UTF8));

			outfile.write(String.valueOf(sendResultDelay)
					.getBytes(XWTools.UTF8));
			outfile.write(lineFeed.getBytes(XWTools.UTF8));

			outfile.closeEntry();
		} catch (final Exception e) {
			System.err.println(e.toString());
		}

	} // TracerZipFile ()

	public void close() {
		try {
			if (outfile != null) {
				outfile.close();
			}
			if (infile != null) {
				infile.close();
			}
		} catch (final Exception e) {
		}
	} // close ()

	/**
	 * This is the constructor to create write only file. It inserts header (ie
	 * configuration) in zip file.
	 * 
	 * @param entryName
	 *            is the name of the entry to create.
	 * @param fileName
	 *            is the name of the data file.
	 */
	public void addEntry(String entryName, String fileName) throws Exception {
		try {

			final File file = new File(fileName);
			final FileInputStream dataIn = new FileInputStream(file);
			ZipEntry zipEntry;

			zipEntry = new ZipEntry(entryName);
			outfile.putNextEntry(zipEntry);

			while (dataIn.available() != 0) {
				outfile.write(dataIn.read());
			}

			dataIn.close();
			outfile.closeEntry();
		} catch (final Exception e) {
			e.printStackTrace();
			throw e;
		}

	} // addEntry()

	private byte[] readEntry(String entryName) throws IOException {

		final ZipEntry ze = infile.getEntry(entryName);
		final InputStream istr = infile.getInputStream(ze);
		final BufferedInputStream bis = new BufferedInputStream(istr);
		final int sz = (int) ze.getSize();
		final int N = 1024;
		final byte buf[] = new byte[sz];

		if (bis.read(buf, 0, Math.min(N, sz)) == -1) {
			return null;
		}

		bis.close();

		return buf;
	}

	private TracesConfig readTracesConfig() throws Exception {

		final TracesConfig ret = new TracesConfig();

		try {
			final byte buf[] = readEntry("Configuration");
			final String strBuf = new String(buf);
			final String lineFeed = new String("\n\r");
			int indexStart = 0;
			int indexEnd = 0;

			indexEnd = strBuf.indexOf(lineFeed);
			if (indexEnd == -1) {
				return null;
			}
			ret.setHostName(new String(strBuf.substring(indexStart, indexEnd)));

			indexStart = indexEnd + lineFeed.length();
			indexEnd = strBuf.indexOf(lineFeed, indexStart);
			if (indexEnd == -1) {
				return null;
			}
			ret.setTimeZone(new String(strBuf.substring(indexStart, indexEnd)));

			indexStart = indexEnd + lineFeed.length();
			indexEnd = strBuf.indexOf(lineFeed, indexStart);
			if (indexEnd == -1) {
				return null;
			}
			ret.setIpAddr(new String(strBuf.substring(indexStart, indexEnd)));

			indexStart = indexEnd + lineFeed.length();
			indexEnd = strBuf.indexOf(lineFeed, indexStart);
			if (indexEnd == -1) {
				return null;
			}
			ret.setHwAddr(new String(strBuf.substring(indexStart, indexEnd)));

			indexStart = indexEnd + lineFeed.length();
			indexEnd = strBuf.indexOf(lineFeed, indexStart);
			if (indexEnd == -1) {
				return null;
			}
			ret.setVersion(new String(strBuf.substring(indexStart, indexEnd)));

			indexStart = indexEnd + lineFeed.length();
			indexEnd = strBuf.indexOf(lineFeed, indexStart);
			if (indexEnd == -1) {
				return null;
			}
			ret.setResultDelay(new Integer(new String(strBuf.substring(
					indexStart, indexEnd))).intValue());

			indexStart = indexEnd + lineFeed.length();
			indexEnd = strBuf.indexOf(lineFeed, indexStart);
			if (indexEnd == -1) {
				return null;
			}
			ret.setSendResultDelay(new Integer(new String(strBuf.substring(
					indexStart, indexEnd))).intValue());
		} catch (final Exception e) {
			throw e;
		}

		return ret;
	}

	private short[] readMasks() throws Exception {

		try {
			final byte buf[] = readEntry("mask");
			final short[] mask = new short[buf.length / 2];

			//
			// buf [i+1] is the mask LSB
			// buf [i] is the mask MSB
			//

			int maskIndex = 0;
			for (int i = 0; i < buf.length; i += 2) {

				mask[maskIndex] |= byteToShort(buf, i);
				maskIndex++;
			}

			return mask;

		} catch (final Exception e) {
			throw e;
		}
	}

	private int byteToInt(byte[] buf, int offset) throws Exception {

		try {
			int ret = 0xff000000 & (buf[offset] << 24);
			ret |= 0x00ff0000 & (buf[offset + 1] << 16);
			ret |= 0x0000ff00 & (buf[offset + 2] << 8);
			ret |= 0x000000ff & buf[offset + 3];

			return ret;
		} catch (final Exception e) {
			throw e;
		}
	}

	private short byteToShort(byte[] buf, int offset) throws Exception {

		try {
			short ret = 0;
			ret |= (short) (buf[offset] << 8);
			ret |= (short) (0x00ff & buf[offset + 1]);

			return ret;
		} catch (final Exception e) {
			throw e;
		}
	}

	private TracerState[] readStates(short[] masks) throws Exception {
		int i = 0;

		try {
			final byte buf[] = readEntry("state");
			final TracerState[] states = new TracerState[masks.length];

			int bufIdx = 0;

			for (i = 0; i < masks.length; i++) {

				states[i] = new TracerState();

				if ((masks[i] & 0x01) == 0x01) {
					states[i].setCpuUser(byteToInt(buf, bufIdx));
					bufIdx += 4;
				}

				if ((masks[i] & 0x02) == 0x02) {
					states[i].setCpuNice(byteToInt(buf, bufIdx));
					bufIdx += 4;
				}

				if ((masks[i] & 0x04) == 0x04) {
					states[i].setCpuSystem(byteToInt(buf, bufIdx));
					bufIdx += 4;
				}

				if ((masks[i] & 0x08) == 0x08) {
					states[i].setCpuIdle(byteToInt(buf, bufIdx));
					bufIdx += 4;
				}

				if ((masks[i] & 0x10) == 0x10) {
					states[i].setCpuAidle(byteToInt(buf, bufIdx));
					bufIdx += 4;
				}

				if ((masks[i] & 0x20) == 0x20) {
					states[i].setLoadOne(byteToShort(buf, bufIdx));
					bufIdx += 2;
				}

				if ((masks[i] & 0x40) == 0x40) {
					states[i].setLoadFive(byteToShort(buf, bufIdx));
					bufIdx += 2;
				}

				if ((masks[i] & 0x80) == 0x80) {
					states[i].setLoadFifteen(byteToShort(buf, bufIdx));
					bufIdx += 2;
				}

				if ((masks[i] & 0x0100) == 0x0100) {
					states[i].setProcRun(byteToShort(buf, bufIdx));
					bufIdx += 2;
				}

				if ((masks[i] & 0x0200) == 0x0200) {
					states[i].setProcTotal(byteToShort(buf, bufIdx));
					bufIdx += 2;
				}

				if ((masks[i] & 0x0400) == 0x0400) {
					states[i].setMemFree(byteToInt(buf, bufIdx));
					bufIdx += 4;
				}

				if ((masks[i] & 0x0800) == 0x0800) {
					states[i].setMemShared(byteToInt(buf, bufIdx));
					bufIdx += 4;
				}

				if ((masks[i] & 0x1000) == 0x1000) {
					states[i].setMemBuffers(byteToInt(buf, bufIdx));
					bufIdx += 4;
				}

				if ((masks[i] & 0x2000) == 0x2000) {
					states[i].setMemCached(byteToInt(buf, bufIdx));
					bufIdx += 4;
				}

				if ((masks[i] & 0x4000) == 0x4000) {
					states[i].setSwapFree(byteToInt(buf, bufIdx));
					bufIdx += 4;
				}

				if ((masks[i] & 0x8000) == 0x8000) {
					states[i].setTime(byteToInt(buf, bufIdx));
					bufIdx += 4;
				}
			}

			return states;

		} catch (final Exception e) {
			throw e;
		}
	}

	private TracerConfig[] readConfigs() throws Exception {

		int i = 0;

		try {
			final byte buf[] = readEntry("config");
			final TracerConfig[] configs = new TracerConfig[buf.length
					/ TracerConfig.LENGTH];

			int bufIdx = 0;

			for (i = 0; i < configs.length; i++) {

				configs[i] = new TracerConfig();

				configs[i].setCpuNum(byteToShort(buf, bufIdx));
				bufIdx += 2;

				configs[i].setCpuSpeed(byteToShort(buf, bufIdx));
				bufIdx += 2;

				configs[i].setMemTotal(byteToInt(buf, bufIdx));
				bufIdx += 4;

				configs[i].setSwapTotal(byteToInt(buf, bufIdx));
				bufIdx += 4;

				configs[i].setBoottime(byteToInt(buf, bufIdx));
				bufIdx += 4;

				configs[i].setKernel(new byte[16]);
				System.arraycopy(buf, bufIdx, configs[i].getKernel(), 0, 16);
				bufIdx += 16;

				configs[i].setTime(byteToInt(buf, bufIdx));
			}

			return configs;
		} catch (final Exception e) {
			throw e;
		}
	}

	public Traces read() throws Exception {
		final Traces ret = new Traces();

		try {
			ret.setT_config(readTracesConfig());
			ret.setMasks(readMasks());
			ret.setStates(readStates(ret.getMasks()));
			ret.setConfigs(readConfigs());

			return ret;
		} catch (final Exception e) {
			throw e;
		}
	}

} // class TracerZipFile

