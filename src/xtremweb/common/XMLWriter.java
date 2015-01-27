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

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Created: 20 janvier 2010<br />
 * This writes an object in XML to the given output stream. <br />
 * Since 9.1.0, this has been redesigned. Seems cleaner to create a writer,
 * given the output and then write an object; instead of creating a writer given
 * the object to write and then write to output stream...
 *
 * @author Oleg Lodygensky
 * @since 5.8.0
 */

public class XMLWriter {

	private final DataOutputStream output;
	/**
	 * This is kept to ensure backward compatibility
	 *
	 * @deprecated since 9.1.0, XMLWriter(DataOutputStream) should be preferred.
	 *             It is only a question of software design
	 */
	@Deprecated
	private final XMLable xmlObject;

	/**
	 * This default constructor sets output stream
	 *
	 * @since 9.1.0
	 */
	public XMLWriter(DataOutputStream o) {
		xmlObject = null;
		output = o;
	}

	/**
	 * This default constructor sets version to current version. This is kept to
	 * ensure backward compatibility
	 *
	 * @see #XMLWriter(DataOutputStream)
	 * @deprecated since 9.1.0, XMLWriter(DataOutputStream) should be preferred.
	 *             It is only a question of software design
	 */
	@Deprecated
	protected XMLWriter(XMLable o) {
		xmlObject = o;
		output = null;
	}

	/**
	 * This default constructor sets version to current version. This is kept to
	 * ensure backward compatibility
	 *
	 * @see #write(XMLable)
	 * @deprecated since 9.1.0, write(XMLable) should be preferred. It is only a
	 *             question of software design
	 */
	@Deprecated
	public void write(final DataOutputStream o) throws IOException {
		xmlObject.toXml(o);
	}

	/**
	 * This write the given parameter to output stream
	 *
	 * @param o
	 *            is the object to write
	 * @throws IOException
	 *             on write error
	 * @since 9.1.0
	 */
	public void write(final XMLable o) throws IOException {
		o.toXml(output);
	}

	/**
	 * This write the given parameter between XML root tags to output stream
	 *
	 * @param o
	 *            is the object to write
	 * @throws IOException
	 *             on write error
	 * @since 9.1.0
	 */
	public void writeWithTags(final XMLable o) throws IOException {
		final byte[] openTag = o.openXmlRootElement().getBytes(XWTools.UTF8);
		output.write(openTag);
		this.write(o);
		final byte[] closeTag = o.closeXmlRootElement().getBytes(XWTools.UTF8);
		output.write(closeTag);
	}
}
