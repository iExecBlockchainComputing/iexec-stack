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
 * Browser.java
 *
 * Purpose : This defines a frame to dislplay HTML content.
 * By default it loads and displays http://dghep.lal.in2p3.fr/lal/doc/xwhephelp.html
 * Created : 27 Aout 2008
 *
 * @author <a href="mailto:lodygens /at\ lal.in2p3.fr>Oleg Lodygensky</a>
 * @version %I, %G
 */

package xtremweb.common;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class Browser extends JFrame {

	private JEditorPane htmlPane;

	public JEditorPane getPane() {
		return htmlPane;
	}

	/**
	 * This contains http://www.xtremweb-hep.org/lal/doc/xwhephelp.html
	 */
	private static final String URL = "http://www.xtremweb-hep.org/lal/doc/xwhephelp.html";

	public static String getURL() {
		return URL;
	}

	/**
	 * If this browser displays a URL content, this is false. If this browser
	 * displays a text content, this is true
	 */
	private final boolean localContent;

	/**
	 * This calls this(_URL)
	 *
	 * @see #Browser(String)
	 */
	public Browser() throws IOException {
		this(new URL(URL));
	}

	/**
	 * This instantiates a new object with the given url
	 *
	 * @param url
	 *            contains the URL to download the content from
	 */
	public Browser(final URL url) throws IOException {
		super(url.toString());
		localContent = false;
		createFrame(new JEditorPane(url));
	}

	/**
	 * This instantiates a new object with the given content
	 *
	 * @param content
	 *            contains the text to display
	 */
	public Browser(final String content) throws IOException {
		super("Help");
		localContent = true;
		createFrame(new JEditorPane("text/html", content));
	}

	/**
	 * This instantiates a new object with the given url
	 *
	 * @param url
	 *            contains the URL to download the content from
	 */
	private void createFrame(final JEditorPane pane) throws IOException {

		htmlPane = pane;
		htmlPane.setEditable(false);
		if (localContent == false) {
			htmlPane.addHyperlinkListener(new HyperlinkListener() {
				@Override
				public void hyperlinkUpdate(final HyperlinkEvent event) {
					if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
						try {
							htmlPane.setPage(event.getURL());
						} catch (final IOException ioe) {
							ioe.printStackTrace();
						}
					}
				}
			});
		}
		final JScrollPane scrollPane = new JScrollPane(htmlPane);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		final Dimension size = new Dimension(800, 600);
		setSize(size);
		setPreferredSize(size);
		pack();
	}

	public static void main(final String[] args) {
		try {
			URL url = new URL(URL);
			if (args.length > 0) {
				url = new URL(args[0]);
			}
			final Browser browser = new Browser(url);
			browser.setVisible(true);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
}
