package xtremweb.common;

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

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import xtremweb.common.BytePacket;
import xtremweb.common.Logger;

/**
 * This tests XML serialization
 * 
 * Created: 23 janvier 2013
 * 
 * @author Oleg Lodygensky
 * @version 8.2.0
 */

public class BytePacketTest {
	private final Logger logger;

	public BytePacketTest() {
		logger = new Logger(this);
	}

	@Test
	public void start() {

		try {
			final BytePacket b0 = new BytePacket();
			final BytePacket b1 = new BytePacket();

			b0.putInt(12345);
			b0.putInt(54321);

			b1.setData(b0.getData());
			assertTrue(b0.getData().equals(b1.getData()));
		} catch (final Exception e) {
			logger.exception(e);
		}
	}

}
