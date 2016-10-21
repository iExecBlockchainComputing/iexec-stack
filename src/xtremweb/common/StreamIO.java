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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketException;
import java.nio.MappedByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.security.cert.X509Certificate;
import java.util.Hashtable;
import java.util.Vector;

/**
 * This class implements some basic stream I/O<br />
 * Created: Jun 1st, 2005<br />
 *
 * @author Oleg Lodygensky
 * @version RPCXW
 */

public class StreamIO implements AutoCloseable {

	private final Logger logger;

	/**
	 * This flag tells to use NIO or not
	 */
	private static boolean nio;
	/**
	 * This aims to display some time stamps
	 */
	private MileStone mileStone;
	/**
	 * This is the output stream
	 */
	private DataOutputStream output = null;
	/**
	 * This is the input stream
	 */
	private DataInputStream input = null;
	/**
	 * This is the default buffer length; it is set to 20K.
	 */
	public static final int DEFLENGTH = XWTools.PACKETSIZE;
	/**
	 * This is the actual buffer length
	 */
	private final int bufferLength;

	/**
	 * This constructor initiates I/O streams and buffer
	 *
	 * @param o
	 *            is the output stream to write to
	 * @param i
	 *            is the input stream to read for
	 * @param n
	 *            is the buffer length to use
	 * @param newio
	 *            tells to use nio or not
	 */
	public StreamIO(final DataOutputStream o, final DataInputStream i, final int n, final boolean newio) {

		logger = new Logger(this);
		output = o;
		input = i;
		bufferLength = n;
		try {
			mileStone = new MileStone(getClass());
		} catch (final Exception e) {
		}
		nio = newio;
	}

	/**
	 * This constructor initiates I/O streams and sets the buffer length to
	 * DEFLENGTH
	 *
	 * @param o
	 *            is the output stream to write to
	 * @param i
	 *            is the input stream to read for
	 * @param newio
	 *            tells to use nio or not
	 */
	public StreamIO(final DataOutputStream o, final DataInputStream i, final boolean newio) {
		this(o, i, DEFLENGTH, newio);
	}

	/**
	 * This constructor initiates I/O streams and sets the buffer length to
	 * DEFLENGTH This sets nio to true
	 *
	 * @param o
	 *            is the output stream to write to
	 * @param i
	 *            is the input stream to read for
	 */
	public StreamIO(final DataOutputStream o, final DataInputStream i) {
		this(o, i, true);
	}

	/**
	 * This retreives the data input stream<br />
	 * This is needed to receive object as XML
	 */
	public DataInputStream input() {
		return input;
	}

	/**
	 * This retreives the data output stream<br />
	 * This is needed to send object as XML
	 */
	public DataOutputStream output() {
		return output;
	}

	/**
	 * This closes input and output channel
	 */
	@Override
	public void close() {
		try {
			if (input != null) {
				for (int i = input.available(); i > 0; i = input.available()) {
					input.skipBytes(i);
				}
				input.close();
			}
			if (output != null) {
				output.flush();
				output.close();
			}
		} catch (final IOException ioe) {
		}
	}

	/**
	 * This writes the given array to the given file This does not use this
	 * object attributes since it is a static method
	 *
	 * @param array
	 *            is the byte array to write
	 * @param fname
	 *            is the file name to write array to
	 * @exception IOException
	 *                is thrown on I/O error or if the provided file does not
	 *                exist
	 */
	public static void array2file(final byte[] array, final String fname) throws IOException {
		array2file(array, new File(fname));
	}

	/**
	 * This is used by array2file() only
	 */
	private static byte[] arraybuf = new byte[20480];

	/**
	 * This writes the given array to the given file This does not use this
	 * object attributes since it is a static method
	 *
	 * @param array
	 *            is the byte array to write
	 * @param file
	 *            is the file to write array to
	 * @exception IOException
	 *                is thrown on I/O error or if the provided file does not
	 *                exist
	 */
	public static void array2file(final byte[] array, final File file) throws IOException {
		if (!nio) {
			final ByteArrayInputStream bis = new ByteArrayInputStream(array);
			final DataInputStream dis = new DataInputStream(bis);
			final DataOutputStream dos = new DataOutputStream(new FileOutputStream(file));

			for (int nread = dis.read(arraybuf); nread > 0; nread = dis.read(arraybuf)) {
				dos.write(arraybuf, 0, nread);
			}
			dos.close();
		} else {
			final ByteArrayInputStream bis = new ByteArrayInputStream(array);
			final ReadableByteChannel inChannel = Channels.newChannel(bis);
			final FileChannel outChannel = new FileOutputStream(file).getChannel();

			final MappedByteBuffer bb = outChannel.map(FileChannel.MapMode.READ_WRITE, 0, array.length);
			outChannel.write(bb);
			inChannel.close();
			outChannel.close();
		}
	}

	/**
	 * This reads the given file and returns the content in a byte array This
	 * does not use this object attributes since it is a static method
	 *
	 * @param fname
	 *            is the name of the file to read
	 * @return a byte array containing the content file
	 * @exception IOException
	 *                is thrown on I/O error or if the provided file does not
	 *                exist
	 * @exception ArrayIndexOutOfBoundsException
	 *                is thrown if file content size if more than
	 *                util.LONGFILESIZE
	 * @exception IOException
	 *                is thrown on I/O error
	 * @see XWTools#LONGFILESIZE
	 */
	public static byte[] file2array(final String fname) throws ArrayIndexOutOfBoundsException, IOException {
		return file2array(new File(fname));
	}

	/**
	 * This reads the given file and returns the content in a byte array This
	 * does not use this object attributes since it is a static method
	 *
	 * @param file
	 *            is the file to read
	 * @return a byte array containing the content file
	 * @exception IOException
	 *                is thrown on I/O error or if the provided file does not
	 *                exist
	 * @exception ArrayIndexOutOfBoundsException
	 *                is thrown if file content size if more than
	 *                util.LONGFILESIZE
	 * @exception IOException
	 *                is thrown on I/O error
	 * @see XWTools#LONGFILESIZE
	 */
	public static byte[] file2array(final File file) throws ArrayIndexOutOfBoundsException, IOException {
		byte[] contents;
		final FileInputStream fis = new FileInputStream(file);

		if (!nio) {
			if (file.length() > XWTools.LONGFILESIZE) {
				throw new ArrayIndexOutOfBoundsException("too huge size : " + file.length());
			} else {
				contents = new byte[(int) file.length()];
				fis.read(contents);
				fis.close();
				return contents;
			}
		} else {
			final ByteArrayOutputStream bos = new ByteArrayOutputStream((int) file.length());
			final WritableByteChannel outChannel = Channels.newChannel(bos);
			final FileChannel inChannel = new FileInputStream(file).getChannel();

			final MappedByteBuffer bb = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
			outChannel.write(bb);

			inChannel.transferTo(0, file.length(), outChannel);
			inChannel.close();
			outChannel.close();
			return bos.toByteArray();
		}
	}

	/**
	 * This writes a String to output stream. The written string may be read by
	 * readString().
	 *
	 * @param v
	 *            is the value to write
	 * @exception Exception
	 *                is thrown on on I/O error
	 */
	public void writeString(final String v) throws IOException {
		byte[] strb = v.getBytes(XWTools.UTF8);
		output.write(strb);
		strb = null;
	}

	/**
	 * This reads a String from input stream
	 *
	 * @return the read String
	 * @exception Exception
	 *                is thrown on on I/O error
	 */
	public String readString() throws IOException, EOFException {
		byte[] readStringBytes = new byte[bufferLength];
		final int read = input.read(readStringBytes);

		final StringBuffer out = new StringBuffer();
		char current;

		for (int i = 0; i < read; i++) {
			current = (char) readStringBytes[i];
			if ((current == 0x9) || (current == 0xA) || (current == 0xD) || ((current >= 0x20) && (current <= 0xD7FF))
					|| ((current >= 0xE000) && (current <= 0xFFFD))
					|| ((current >= 0x10000) && (current <= 0x10FFFF))) {
				out.append(current);
			}
		}
		readStringBytes = null;
		return out.toString();
	}

	/**
	 * This writes the string as a sequence of byte.
	 *
	 * @param v
	 *            is the value to write
	 * @exception Exception
	 *                is thrown on I/O error
	 * @since 2.0.0
	 */
	public void writeBytes(final String v) throws IOException {
		output.writeBytes(v);
		output.flush();
	}

	/**
	 * This writes a byte
	 *
	 * @param v
	 *            is the value to write
	 * @exception Exception
	 *                is thrown on I/O error
	 */
	public void writeByte(final byte v) throws IOException {
		output.writeByte(v);
		output.flush();
	}

	/**
	 * This reads a byte
	 *
	 * @return the read byte
	 * @exception Exception
	 *                is thrown on I/O error
	 */
	public byte readByte() throws IOException {
		return input.readByte();
	}

	/**
	 * This writes an integer
	 *
	 * @param v
	 *            is the value to write
	 * @exception Exception
	 *                is thrown on I/O error
	 */
	public void writeInt(final int v) throws IOException {
		output.writeInt(v);
		output.flush();
	}

	/**
	 * This reads an integer from input stream
	 *
	 * @return the read integer
	 * @exception Exception
	 *                is thrown on I/O error
	 */
	public int readInt() throws IOException {
		return input.readInt();
	}

	/**
	 * This reads a long integer from input stream
	 *
	 * @return the read long integer
	 * @exception Exception
	 *                is thrown on I/O error
	 */
	public long readLong() throws IOException {
		return input.readLong();
	}

	/**
	 * This writes a long integer
	 *
	 * @param v
	 *            is the value to write
	 * @exception Exception
	 *                is thrown on I/O error
	 */
	public void writeLong(final long v) throws IOException {
		output.writeLong(v);
		output.flush();
	}

	/**
	 * This writes a byte array to output stream. This first writes the array
	 * size, then the array content itself.<br />
	 * If v is null a single 0 is only sent
	 *
	 * @param v
	 *            is the array to write to output stream
	 * @exception Exception
	 *                is thrown on I/O error or if the provided file does not
	 *                exist
	 * @see #readArray()
	 */
	public void writeArray(final byte[] v) throws IOException {
		if (v == null) {
			writeInt(0);
			return;
		}

		writeInt(v.length);

		output.write(v, 0, v.length);
		output.flush();
	}

	/**
	 * This reads a byte array from input stream. This first reads the array
	 * size, then the array content itself.
	 *
	 * @return a byte array, of null if the read array size is 0
	 * @exception IOException
	 *                is thrown on I/O error or if the provided file does not
	 *                exist
	 * @see #writeArray(byte[])
	 */
	public byte[] readArray() throws IOException {
		final int length = readInt();
		if (length == 0) {
			return null;
		}

		final byte[] ret = new byte[length];

		input.readFully(ret, 0, length);
		return ret;
	}

	/**
	 * This writes a file content to output stream. This first writes the file
	 * size, then the file content itself. If parameter is null a single 0 is
	 * only sent. The output stream is not closed.
	 *
	 * @param file
	 *            denotes the file to write to output stream
	 * @exception IOException
	 *                is thrown on I/O error or if the provided file does not
	 *                exist
	 */
	public void writeFile(final File file) throws IOException, SocketException {

		if (file == null) {
			throw new IOException("file is null");
		}

		if (!file.exists()) {
			throw new IOException("file not found : " + file);
		}

		final long length = file.length();

		logger.finest("writeFile : to be written " + length);
		writeLong(length);
		boolean thiscomnio = nio;
		if (length > XWTools.TWOGIGABYTES) {
			thiscomnio = false;
		}
		writeFileContent(file, thiscomnio);
	}

	/**
	 * This calls wrtieFileConten(file, this.nio)
	 */
	public void writeFileContent(final File file) throws IOException, SocketException {
		writeFileContent(file, nio);
	}

	/**
	 * This writes a file content to output stream. The output stream is not
	 * closed
	 *
	 * @param file
	 *            denotes the file to write to output stream
	 * @param thiscomnio
	 *            is true if NIO expected
	 * @exception IOException
	 *                is thrown on I/O error or if the provided file does not
	 *                exist
	 */
	public void writeFileContent(final File file, final boolean thiscomnio) throws IOException, SocketException {

		if (file == null) {
			throw new IOException("file is null");
		}

		if (!file.exists()) {
			throw new IOException("file not found " + file);
		}

		logger.debug("writeFileContent : " + thiscomnio);
		final long length = file.length();
		logger.debug("writeFileContent : thiscommio = " + thiscomnio + "; length = " + length);
		final FileInputStream fis = new FileInputStream(file);
		final FileChannel inChannel = fis.getChannel();

		try {
			if (length > 0) {
				long written = 0;
				if (!thiscomnio) {
					final byte[] buffer = new byte[bufferLength];
					int nfis = 0;
					while (nfis != -1) {
						nfis = fis.read(buffer);
						if (nfis != -1) {
							output.write(buffer, 0, nfis);
							written += nfis;
						}
						if (written >= length) {
							break;
						}
					}
				} else {
					final WritableByteChannel outChannel = Channels.newChannel(output);
					written = inChannel.transferTo(0, length, outChannel);
				}
				logger.finest("writeFileContent : bytes written " + written);
				if (written != length) {
					throw new IOException("writeFileContent : byte count error " + written + "/" + length);
				}
			}
		} finally {
			output.flush();
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (final Exception e) {
			}
			try {
				if (inChannel != null) {
					inChannel.close();
				}
			} catch (final Exception e) {
			}
		}
	}

	/**
	 * This reads a file content from input stream and stores it to file. The
	 * file size is first read, then the file content itself The input stream is
	 * not closed
	 *
	 * @param file
	 *            denotes the file to store content from input stream
	 * @exception IOException
	 *                is thrown on I/O error
	 */
	public void readFile(final File file) throws IOException, SocketException {

		final long length = readLong();
		logger.finest("readFile : to be read " + length);

		boolean thiscomnio = nio;
		if (length > XWTools.TWOGIGABYTES) {
			thiscomnio = false;
		}

		logger.debug("readFile(" + file + "," + thiscomnio + ")");

		final FileOutputStream fos = new FileOutputStream(file);
		final FileChannel outChannel = fos.getChannel();

		long written = 0;
		try {
			if (length > 0) {
				if (!thiscomnio) {
					int n = 0;
					final byte[] buffer = new byte[bufferLength];

					while (n != -1) {
						n = input.read(buffer);
						if (n != -1) {
							fos.write(buffer, 0, n);
							written += n;
						}
						if (written >= length) {
							break;
						}
					}
				} else {
					final ReadableByteChannel inChannel = Channels.newChannel(input);
					written = outChannel.transferFrom(inChannel, 0, length);
				}
				logger.finest("readFile : bytes read " + written);
				if (written != length) {
					throw new IOException("readFile : byte count error " + written + "/" + length);
				}
			}
		} finally {
			output.flush();
			try {
				fos.flush();
				fos.close();
			} catch (final Exception e) {
			}
			try {
				if (outChannel != null) {
					outChannel.close();
				}
			} catch (final Exception e) {
			}
		}
		logger.finest("readFile done");
	}

	/**
	 * This reads a file content from input stream and stores it to file
	 *
	 * @param file
	 *            denotes the file to store content from input stream
	 */
	public void readFileContent(final File file) throws IOException, SocketException {

		final byte[] buffer = new byte[bufferLength];
		final FileOutputStream fos = new FileOutputStream(file);

		try {
			if (!nio) {
				long written = 0;
				int n = 0;
				while (n != -1) {
					n = input.read(buffer);
					if (n != -1) {
						fos.write(buffer, 0, n);
						written += n;
					}
				}
				logger.finest("readFileContent : bytes read = " + written);
			} else {
				throw new IOException("StreamIO#readFileContent is not implemented with java.nio");
			}
		} finally {
			if (fos != null) {
				fos.close();
			}
		}
	}

	/**
	 * This reads a serializable object from input stream
	 *
	 * @return a serializable object
	 * @exception Exception
	 *                is thrown on I/O error
	 */
	public Hashtable readHashtable() throws IOException {

		try {
			final ObjectInputStream ois = new ObjectInputStream(input);
			return (Hashtable) ois.readObject();
		} catch (final ClassNotFoundException e) {
			throw new IOException(e.getMessage());
		}
	}

	/**
	 * This writes a serializable object to output stream
	 *
	 * @param o
	 *            is the object to write
	 * @exception IOException
	 *                is thrown on I/O error
	 */
	public void writeObject(final Hashtable o) throws IOException {
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(output);
			oos.writeObject(o);
			output.flush();
		} finally {
			oos = null;
		}
	}

	/**
	 * This writes a serializable object to output stream
	 *
	 * @param c
	 *            is the object to write
	 * @exception IOException
	 *                is thrown on I/O error
	 */
	public void writeObject(final X509Certificate c) throws IOException {
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(output);
			oos.writeObject(c);
			output.flush();
		} finally {
			oos = null;
		}
	}

	/**
	 * This writes an Vector to output stream; this first writes vector size
	 * following by vector datas, if any<br />
	 * If v is null, this writes a single 0
	 *
	 * @param v
	 *            is the Vector to send
	 * @exception IOException
	 *                is thrown on I/O error
	 */
	public void writeObject(final Vector v) throws IOException {

		if (v == null) {
			writeInt(0);
			return;
		}

		writeInt(v.size());
		for (int i = 0; i < v.size(); i++) {
			writeString(v.elementAt(i).toString());
		}
	}

	/**
	 * This reads an Vector from input stream; this first reads vector size then
	 * vector datas, if any<br />
	 * This tries to store received objects as UID into Vector; if received
	 * objects are not UID, they are stored as String into returned Vector
	 *
	 * @return a Vector of UID, or a Vector of String, or an empty Vector
	 * @exception IOException
	 *                is thrown on I/O error
	 */
	public Vector readVector() throws IOException {

		final Vector ret = new Vector();

		final int size = readInt();

		if (size == 0) {
			return ret;
		}

		for (int i = 0; i < size; i++) {
			final String str = readString();
			ret.add(new UID(str));
		}
		return ret;
	}

	/**
	 * This writes an object XML representation to output stream
	 *
	 * @param o
	 *            is the object to write
	 * @exception IOException
	 *                is thrown on I/O error
	 * @exception SocketException
	 *                is thrown on socket error
	 */
	public void writeObject00(final XMLable o) throws IOException {
		try {
			logger.finest("writeObject " + o.openXmlRootElement() + o.toXml() + o.closeXmlRootElement());
			final byte[] strb = o.openXmlRootElement().getBytes(XWTools.UTF8);
			output.write(strb);
			final XMLWriter writer = new XMLWriter(output);
			writer.write(o);
			final byte[] strb2 = o.closeXmlRootElement().getBytes(XWTools.UTF8);
			output.write(strb2);
		} catch (final Exception e) {
			if (logger.debug() == true) {
				logger.exception(e);
			}
			throw new IOException(e.getMessage());
		}
	}

	/**
	 * This pipes a String from input stream to a new input stream<br />
	 * This is needed to receive XML object representations
	 *
	 * @see #stream(String)
	 * @return the input stream where the string has been pushed
	 * @exception IOException
	 *                is thrown on IO error
	 */
	public DataInputStream stream() throws IOException {
		return stream(readString());
	}

	/**
	 * This pipes a String to an input stream
	 *
	 * @param in
	 *            is the String to pipe
	 * @return the input stream where the string has been pushed
	 * @exception IOException
	 *                is thrown on IO error ot if provided parameter is null
	 */
	public static DataInputStream stream(final String in) throws IOException {
		if (in == null) {
			throw new IOException("input string is null");
		}

		byte[] strb = in.getBytes(XWTools.UTF8);
		ByteArrayInputStream ba = new ByteArrayInputStream(strb);
		final DataInputStream ret = new DataInputStream(ba);
		ba = null;
		strb = null;
		return ret;
	}

	/**
	 * This is the standard main method; this is for debugging only
	 */
	public static void main(final String[] argv) {

		try {
			File fin = new File(argv[0]);
			File fout = new File("outtemp_nio");
			FileOutputStream fos = new FileOutputStream(fout);
			DataOutputStream output = new DataOutputStream(fos);

			StreamIO io = new StreamIO(output, null);

			long t0 = System.currentTimeMillis();
			io.writeFile(fin);
			long t1 = System.currentTimeMillis();

			long d0 = t1 - t0;
			System.out.println((StreamIO.nio ? "with" : "w/o ") + " NIO; Size =  " + fin.length() + " ; dT = " + d0);

			fout.delete();

			final File fout2 = new File("outtemp_no_nio");
			final FileOutputStream fos2 = new FileOutputStream(fout2);
			final DataOutputStream output2 = new DataOutputStream(fos2);
			io = new StreamIO(output2, null);
			StreamIO.nio = !StreamIO.nio;

			t0 = System.currentTimeMillis();
			io.writeFile(fin);
			t1 = System.currentTimeMillis();

			d0 = t1 - t0;
			System.out.println((StreamIO.nio ? "with" : "w/o ") + " NIO; Size =  " + fin.length() + " ; dT = " + d0);
			fout2.delete();

			final String long2000Chars = "01234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789";

			fout = new File("longString.txt");
			fos = new FileOutputStream(fout);
			output = new DataOutputStream(fos);

			io = new StreamIO(output, null);
			io.writeString(long2000Chars);

			fin = new File("longString.txt");
			final FileInputStream fis = new FileInputStream(fout);
			final DataInputStream input = new DataInputStream(fis);

			io = new StreamIO(null, input);
			System.out.println("\nread = " + io.readString());

			fout.delete();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}
