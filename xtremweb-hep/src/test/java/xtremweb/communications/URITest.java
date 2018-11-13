package xtremweb.communications;
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

import xtremweb.communications.URI;

import org.junit.Test; 

import static org.junit.Assert.assertTrue;


/**
 * This tests XML serialization
 *
 * Created: 29 juin 2017
 *
 * @author Oleg Lodygensky
 * @since 10.5.2
 */

public class URITest {

	public URITest() {
	}

	/**
	 * This tests that an XW URI must have host and scheme
	 */
	@Test
	public void start() {

		try {
			new URI("1234");
			assert(false);
		} catch (final Exception e) {
			assert(true);
		}
		try {
			new URI("xw://server/1234");
			assert(true);
		} catch (final Exception e) {
			assert(false);
		}
		try {
			new URI("file:///1234");
			assert(true);
		} catch (final Exception e) {
			assert(false);
		}
	}
}
