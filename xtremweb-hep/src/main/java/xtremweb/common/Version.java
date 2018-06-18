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

/**
 * Version.java
 *
 * Assumes that all the version have the same number of digits
 */
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.InvalidKeyException;
import java.util.Enumeration;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.jar.Manifest;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class Version extends XMLable {
	private String version;
	private String build;
	private static final String RESNAME = "META-INF/MANIFEST.MF";
    public static Version currentVersion = new Version();
	public Version() {
        setXMLTag("Version");
/*
        System.out.println("this package = " + this.getClass().getPackage());
        displayCustomPackage();
		System.out.println("this package version = " + getPackageVersion());
*/

		fromString(getPackageVersion());
    }
 /*
    private void displayPackageDetails(final Package pkg) {

        final String name = pkg.getName();
        System.out.println(name);
        System.out.println("\tSpec Title/Version: " + pkg.getSpecificationTitle() + " " + pkg.getSpecificationVersion());
        System.out.println("\tSpec Vendor: " +  pkg.getSpecificationVendor());
        System.out.println("\tImplementation: " + pkg.getImplementationTitle() + " " + pkg.getImplementationVersion());
        System.out.println("\tImplementation Vendor: " + pkg.getImplementationVendor());
    }

	private void displayCustomPackage() {
		final Package[] packages = Package.getPackages();
		for (final Package pkg : packages)
		{
			final String name = pkg.getName();
			if (   !name.startsWith("sun") && !name.startsWith("java")
					&& !name.startsWith("com") && !name.startsWith("org") )
			{
				displayPackageDetails(pkg);
			}
		}
	}
*/
	private String getPackageVersion() {
		final Package[] packages = Package.getPackages();
		final String thisPkgName = this.getClass().getPackage().getName();
		if(thisPkgName == null) {
			return null;
		}
		for (final Package pkg : packages) {
			final String pkgName = pkg.getName();
			if(pkgName.compareTo(thisPkgName) != 0) {
				continue;
			}
			return pkg.getSpecificationVersion();
		}
		return null;
	}


    public Version(final String v) {
        setXMLTag("Version");
		fromString(v);
	}

	/**
	 * This calls this() and fromStrings(ver,br,bu)
	 */
	public Version(final String ver, final String bu) {
        setXMLTag("Version");
		fromStrings(ver, bu);
	}

	/**
	 * This constructs a new object from XML attributes received from input
	 * stream
	 *
	 * @param input
	 *            is the input stream
	 * @throws IOException
	 *             on XML error
	 */
	public Version(final DataInputStream input) throws IOException, SAXException {
        setXMLTag("Version");
		final XMLReader reader = new XMLReader(this);
		try {
			reader.read(input);
		} catch (final InvalidKeyException e) {
			e.printStackTrace();
		}
	}

	/**
	 *
	 */
	public void fromString(final String str) {
        if(str == null) {
            getLogger().debug("Version#fromString str is null");
            return;
        }
		StringTokenizer sk = new StringTokenizer(str, "-");
		version = sk.nextToken();
		try {
			build = sk.nextToken();
		} catch (final Exception e) {
		}
	}
	/**
	 * This retrieves version, branch and build from parameters
	 *
	 * @param ver
	 *            is the version
	 * @param bu
	 *            is the build
	 */
	public void fromStrings(final String ver, final String bu) {
		version = ver;
		build = bu;
	}

	public String full() {
		return version;
	}

	public String rev() {
		return version;
	}

	public String build() {
		return build;
	}

	/**
	 * This returns a string representation of this object,
	 *
	 * @return a string if this version
	 */
	@Override
	public String toString() {
		return full();
	}

	/**
	 * This calls toString()
	 *
	 * @param csv
	 *            is not used
	 */
	@Override
	public String toString(final boolean csv) {
		return toString();
	}

	/**
	 * This is called by XML parser to decode XML elements
	 *
	 * @see XMLReader#read(InputStream)
	 */
	@Override
	public void xmlElementStart(final String uri, final String tag, final String qname, final Attributes attrs)
			throws SAXException {

		try {
			super.xmlElementStart(uri, tag, qname, attrs);
			return;
		} catch (final SAXException ioe) {
		}

		getLogger().finest("     xmlElementStart() qname=\"" + qname + "\"");

		if (qname.compareToIgnoreCase(getXMLTag()) == 0) {
			fromXml(attrs);
		} else {
			throw new SAXException("invalid qname : " + qname);
		}
	}

	/**
	 * This writes this object to a String as an XML object<br />
	 *
	 * @return a String containing this object definition as XML
	 * @see #fromXml(Attributes)
	 */
	@Override
	public String toXml() {

		String ret = ("<" + getXMLTag() + " ");
		ret += " build=\"" + build + "\"";
		ret += " version=\"" + version + "\"";
		ret += " />";
		return ret;
	}

	/**
	 * This retrieves attributes from XML representation
	 *
	 * @param attrs
	 *            contains attributes XML representation
	 */
	@Override
	public void fromXml(final Attributes attrs) {

		if (attrs == null) {
			return;
		}

        build = attrs.getValue(0);
    	version = attrs.getValue(1);
        fromStrings(version, build);
        setCurrentVersion(new Version(version, build));
	}

	/**
	 * This writes the current version to stdout
	 *
	 * @since 8.0.1
	 */
	public static void main(final String argv[]) {
		System.out.println("Current version : " + Version.currentVersion);
	}
}
