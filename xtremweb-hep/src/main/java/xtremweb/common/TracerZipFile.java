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

	private ZipFile infile = null;
	private String outFileName;

	/**
	 * This is the constructor to create read only file.
	 */
	public TracerZipFile(final String filename) {
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
	 * @param outFileName
	 *            is the name of the file to create.
	 * @param host
	 *            contains the host definition.
	 * @param resultDelay
	 *            contains the trace period.
	 * @param sendResultDelay
	 *            contains the send trace period.
	 */
	public TracerZipFile(final String fn, final HostInterface host, final int resultDelay, final int sendResultDelay) {

		outFileName = fn;
		try (final FileOutputStream fos = new FileOutputStream(outFileName);
				ZipOutputStream outfile = new ZipOutputStream(fos);) {

			final String lineFeed = new String("\n\r");
			final ZipEntry zipEntry = new ZipEntry("Configuration");
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

			outfile.write(String.valueOf(sendResultDelay).getBytes(XWTools.UTF8));
			outfile.write(lineFeed.getBytes(XWTools.UTF8));

			outfile.closeEntry();
		} catch (final IOException e) {
		}

	}

	public void close() {
		try {
			if (infile != null) {
				infile.close();
			}
		} catch (final Exception e) {
		}
	}

	/**
	 * This is the constructor to create write only file. It inserts header (ie
	 * configuration) in zip file.
	 *
	 * @param entryName
	 *            is the name of the entry to create.
	 * @param fileName
	 *            is the name of the data file.
	 */
	public void addEntry(final String entryName, final String fileName) throws IOException {

		final File file = new File(fileName);

		try (final FileOutputStream fos = new FileOutputStream(outFileName);
				final ZipOutputStream outfile = new ZipOutputStream(fos);
				final FileInputStream dataIn = new FileInputStream(file)) {

			final ZipEntry zipEntry = new ZipEntry(entryName);
			outfile.putNextEntry(zipEntry);

			while (dataIn.available() != 0) {
				outfile.write(dataIn.read());
			}

			outfile.closeEntry();
		}

	}

	private byte[] readEntry(final String entryName) throws IOException {

		final ZipEntry ze = infile.getEntry(entryName);
		final InputStream istr = infile.getInputStream(ze);
		final int sz = (int) ze.getSize();
		final int N = 1024;
		final byte buf[] = new byte[sz];

		try (final BufferedInputStream bis = new BufferedInputStream(istr)) {
			if (bis.read(buf, 0, Math.min(N, sz)) == -1) {
				return new byte[0];
			}
		}

		return buf;
	}

	private TracesConfig readTracesConfig() throws Exception {

		final TracesConfig ret = new TracesConfig();

		final byte buf[] = readEntry("Configuration");
		if (buf.length == 0) {
			return null;
		}
		final String strBuf = new String(buf);
		final String lineFeed = new String("\n\r");
		int indexStart = 0;
		int indexEnd = strBuf.indexOf(lineFeed);
		if (indexEnd == -1) {
			return null;
		}
		ret.setHostName(strBuf.substring(indexStart, indexEnd));

		indexStart = indexEnd + lineFeed.length();
		indexEnd = strBuf.indexOf(lineFeed, indexStart);
		if (indexEnd == -1) {
			return null;
		}
		ret.setTimeZone(strBuf.substring(indexStart, indexEnd));

		indexStart = indexEnd + lineFeed.length();
		indexEnd = strBuf.indexOf(lineFeed, indexStart);
		if (indexEnd == -1) {
			return null;
		}
		ret.setIpAddr(strBuf.substring(indexStart, indexEnd));

		indexStart = indexEnd + lineFeed.length();
		indexEnd = strBuf.indexOf(lineFeed, indexStart);
		if (indexEnd == -1) {
			return null;
		}
		ret.setHwAddr(strBuf.substring(indexStart, indexEnd));

		indexStart = indexEnd + lineFeed.length();
		indexEnd = strBuf.indexOf(lineFeed, indexStart);
		if (indexEnd == -1) {
			return null;
		}
		ret.setVersion(strBuf.substring(indexStart, indexEnd));

		indexStart = indexEnd + lineFeed.length();
		indexEnd = strBuf.indexOf(lineFeed, indexStart);
		if (indexEnd == -1) {
			return null;
		}
		ret.setResultDelay(Integer.parseInt(new String(strBuf.substring(indexStart, indexEnd))));

		indexStart = indexEnd + lineFeed.length();
		indexEnd = strBuf.indexOf(lineFeed, indexStart);
		if (indexEnd == -1) {
			return null;
		}
		ret.setSendResultDelay(Integer.parseInt(new String(strBuf.substring(indexStart, indexEnd))));

		return ret;
	}

	private short[] readMasks() throws Exception {

		final byte buf[] = readEntry("mask");
		if (buf.length == 0) {
			return null;
		}
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

	}

	private int byteToInt(final byte[] buf, final int offset) throws Exception {

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

	private short byteToShort(final byte[] buf, final int offset) throws Exception {

		try {
			short ret = 0;
			ret |= (short) (buf[offset] << 8);
			ret |= (short) (0x00ff & buf[offset + 1]);

			return ret;
		} catch (final Exception e) {
			throw e;
		}
	}

	private TracerState[] readStates(final short[] masks) throws Exception {
		int i = 0;

		final byte buf[] = readEntry("state");
		if (buf.length == 0) {
			return new TracerState[0];
		}
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

	}

	private TracerConfig[] readConfigs() throws Exception {

		final byte buf[] = readEntry("config");
		if (buf.length == 0) {
			return new TracerConfig[0];
		}
		final TracerConfig[] configs = new TracerConfig[buf.length / TracerConfig.LENGTH];

		int bufIdx = 0;

		for (int i = 0; i < configs.length; i++) {

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
