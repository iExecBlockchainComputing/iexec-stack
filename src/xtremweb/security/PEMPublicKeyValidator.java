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

package xtremweb.security;

/**
 *
 * X509ProxyValidator : validates a X509 proxy against an X509 certification path
 *      using a PKIX CertPathValidator
 *
 * Author: Oleg Lodygensky
 *
 * Environment variables : 
 *   - X509_CERT_DIR   must contain the directory where CA cert are stored
 *   - X509_USER_PROXY must contain the user 509 proxy
 * 
 * First this constructs list of CA cert paths from X509_CERT_DIR
 * Then this constructs a cert PATH for the X509_USER_PROXY
 * Finally this validates the X509 proxy path against CA cert paths
 *
 * Example 
 *    - X509_USER_PROXY contains two public keys
 *        [0] IssuerDN  = CN=Oleg Lodygensky, OU=LAL, O=CNRS, C=FR, O=GRID-FR
 *        [0] SubjectDN = CN=proxy, CN=Oleg Lodygensky, OU=LAL, O=CNRS, C=FR, O=GRID-FR
 *        [1] IssuerDN  = CN=GRID2-FR, O=CNRS, C=FR
 *        [1] SubjectDN = CN=Oleg Lodygensky, OU=LAL, O=CNRS, C=FR, O=GRID-FR
 *
 *    - X509_CERT_DIR must then contains public keys to create the following path
 *        [0] IssuerDN  = CN=CNRS2-Projets, O=CNRS, C=FR
 *        [0] SubjectDN = CN=GRID2-FR, O=CNRS, C=FR
 *        [1] IssuerDN  = CN=CNRS2, O=CNRS, C=FR
 *        [1] SubjectDN = CN=CNRS2-Projets, O=CNRS, C=FR
 *        [2] IssuerDN  = CN=CNRS2, O=CNRS, C=FR
 *        [2] SubjectDN = CN=CNRS2, O=CNRS, C=FR
 *
 * If X509_CERT_DIR does not contains this path, the X509_USER_PROXY can not be validated
 *
 * @since 7.0.0
 */

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import xtremweb.common.XWPropertyDefs;

/**
 * This reads public keys from USERCERTDIR. Public keys are used to verify
 * signature.
 * 
 * @see xtremweb.common.XWPropertyDefs#USERCERTDIR
 * @since 7.4.1
 */
public class PEMPublicKeyValidator {
	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	/**
	 * Digital signature algorithm
	 */
	private Signature signator = null;

	/**
	 * This doesn't allow self signed keys : the client sends its public key and
	 * the server checks CA certification
	 */
	public PEMPublicKeyValidator() throws CertificateException, IOException,
			NoSuchAlgorithmException {

		signator = Signature.getInstance("SHA256WithRSAEncryption");
	}

	/**
	 * This authenticates
	 * 
	 * @return the certificate that authenticates data from is
	 */
	public X509Certificate authenticate(InputStream is) throws IOException,
			NoSuchAlgorithmException, InvalidKeyException, SignatureException {

		DataInputStream in = null;
		try {
			in = new DataInputStream(is);
			return authenticate(in);
		} finally {
			in = null;
		}
	}

	/**
	 * This authenticates data from input stream
	 * 
	 * @param cert
	 *            is the public key to authenticate data
	 * @param in
	 *            is the input stream
	 */
	public static void authenticate(X509Certificate cert, DataInputStream in)
			throws IOException, NoSuchAlgorithmException, InvalidKeyException,
			SignatureException {

		byte[] signature = null;
		Signature signator = null;
		try {
			signator = Signature.getInstance("SHA256WithRSAEncryption");
			final long t = in.readLong();
			final double q = in.readDouble();
			final int length = in.readInt();
			signature = new byte[length];
			in.readFully(signature);

			signator.initVerify(cert.getPublicKey());
			signator.update(Protection.makeBytes(t, q));
			signator.verify(signature);
		} finally {
			signature = null;
			signator = null;
		}
	}

	/**
	 * This is the main method for test purposes only
	 */
	public static void main(String[] args) throws Exception {

		String certToCheck = System.getenv(XWPropertyDefs.USERCERTDIR
				.toString());

		if (System.getProperty(XWPropertyDefs.USERCERTDIR.toString()) != null) {
			certToCheck = System.getProperty(XWPropertyDefs.USERCERTDIR
					.toString());
		}

		if (certToCheck == null) {
			System.err.println(XWPropertyDefs.USERCERTDIR.toString()
					+ " must be set");
			System.exit(1);
		}

		final PEMPublicKeyValidator validator = new PEMPublicKeyValidator();
	}
}
