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

import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import xtremweb.common.XWPropertyDefs;

/**
 * Date : Nov 11th, 2014
 *
 * @author Oleg Lodygensky (lodygens a t lal.in2p3.fr)
 *
 * @since 9.1.0
 */
public class EmailSender {

	String host = "aserver.org";
	String from = "dontreply@xtremweb.hep";
	String emailProtocol = "smtp";
	boolean sessionDebug;
	Session session;
	final static String DEF_SUBJECT = "message from xtremweb-hep";

	public EmailSender() {
		sessionDebug = false;
		host = System.getProperty(XWPropertyDefs.MAILSERVERADDRESS.toString());
		emailProtocol = System.getProperty(XWPropertyDefs.MAILPROTOCOL.toString());
		final Properties props = System.getProperties();
		props.put("mail.host", host);
		props.put("mail.transport.protocol", emailProtocol);
		session = Session.getDefaultInstance(props, null);
		session.setDebug(sessionDebug);
		final String emailaddr = System.getProperty(XWPropertyDefs.MAILSENDERADDRESS.toString());
		if ((emailaddr != null) && (emailaddr.length() > 1)) {
			from = emailaddr;
		}
	}

	public void send(final String to, final String txt) throws MessagingException {
		send(DEF_SUBJECT, to, txt);
	}

	public void send(final String subject, final String to, final String txt) throws MessagingException {
		if ((to == null) || (to.length() < 1)) {
			throw new MessagingException("can't send mail");
		}
		final Message msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(from));
		final InternetAddress[] address = { new InternetAddress(to) };
		msg.setRecipients(Message.RecipientType.TO, address);
		msg.setSubject(subject == null ? "no subject" : subject);
		msg.setSentDate(new Date());
		msg.setText(txt == null ? "empty message" : txt);
		Transport.send(msg);
	}
}