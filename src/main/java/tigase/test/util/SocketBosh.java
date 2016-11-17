/*
 * SocketBosh.java
 *
 * Tigase Jabber/XMPP Server
 * Copyright (C) 2004-2012 "Artur Hefczyc" <artur.hefczyc@tigase.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. Look for COPYING file in the top folder.
 * If not, see http://www.gnu.org/licenses/.
 *
 */



package tigase.test.util;

//~--- non-JDK imports --------------------------------------------------------

import tigase.xml.Element;

//~--- JDK imports ------------------------------------------------------------

import java.io.EOFException;
import java.io.IOException;

import java.net.Socket;
import java.net.SocketException;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Describe class SocketBosh here.
 *
 *
 * Created: Wed May 18 16:23:47 2005
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public class SocketBosh
				extends SocketXMLIO {
	private static final int BUFF_SIZE = 2 * 1024;
	private static long new_socket     = 0;

	//~--- fields ---------------------------------------------------------------

	private String authId         = null;
	private long rid              = 1216;
	private String sid            = null;
	private boolean restart       = false;
	private boolean terminate     = false;
	private boolean reinitialized = false;
	private boolean keep_alive    = false;

	//~--- constructors ---------------------------------------------------------

	/**
	 * Creates a new <code>SocketXMLReader</code> instance.
	 *
	 *
	 * @param sock
	 *
	 * @throws IOException
	 */
	public SocketBosh(Socket sock) throws IOException {
		super(sock);
	}

	//~--- set methods ----------------------------------------------------------

	/**
	 * Method description
	 *
	 *
	 * @param keep_alive
	 */
	public void setKeepAlive(boolean keep_alive) {
		this.keep_alive = keep_alive;
	}

	//~--- methods --------------------------------------------------------------

	/*
	 *  (non-Javadoc)
	 * @see tigase.test.util.XMLIO#write(java.lang.String)
	 */

	/**
	 * Method description
	 *
	 *
	 * @param data
	 *
	 * @throws IOException
	 */
	@Override
	public void write(String data) throws IOException {

		// System.out.println("SocketBosh writing data: " + data);
		if ((data != null) && data.startsWith("<stream:stream")) {
			restart = true;
			initSocket(null);
		} else if ((data != null) && data.startsWith( "</stream:stream>" ) ) {
			// ignore stream closing
		} else {
			initSocket(data);
		}
	}

	/*
	 *  (non-Javadoc)
	 * @see tigase.test.util.XMLIO#read()
	 */

	/**
	 * Method description
	 *
	 *
	 * @return
	 *
	 * @throws IOException
	 */
	@Override
	public Queue<Element> read() throws IOException {
		if (!reinitialized) {
			initSocket(null);
		}

		Queue<Element> results = new LinkedList<Element>();

		try {
			Queue<Element> elements = super.read();

			if (elements != null) {
				results = new LinkedList<Element>();

				Element body = null;

				while ((body = elements.poll()) != null) {

					// System.out.println("Received: " + body.toString());
					if (body.getName() == "body") {
						reinitialized = false;

						String temp = body.getAttributeStaticStr("sid");

						if (temp != null) {
							sid = temp;
						}
						temp = body.getAttributeStaticStr("authid");
						if (temp != null) {
							authId = temp;
						}
						if (body.getChildren() != null) {
							results.addAll(body.getChildren());
						}
					} else {
						results.offer(body);
					}
				}
			}

//    if (body_found) {
//      //System.out.println("Opening new connection...");
//      initSocket(null);
//    }
		} catch (EOFException e) {
			initSocket(null);
		} catch (SocketException e) {
			initSocket(null);
		}
		if (ignore_presence) {
			Iterator<Element> it = results.iterator();
			int counter          = 0;

			while (it.hasNext()) {
				Element el = it.next();

				if (el.getName() == "presence") {
					it.remove();
					++counter;
				}
			}
			if (counter > 0) {

				// System.out.println("Removed " + counter + " presences...");
			}
		}

		return results;
	}

	/**
	 * Method description
	 *
	 */
	@Override
	public void close() {

		// System.out.println("Closing Bosh socket.");
		// super.close();
		terminate = true;
		try {
			initSocket(null);
		} catch (Exception e) {

			// e.printStackTrace();
		}
	}

	/**
	 * Method description
	 *
	 *
	 * @param data
	 *
	 * @throws IOException
	 */
	protected void initSocket(String data) throws IOException {
		if (!keep_alive ||!isConnected()) {

			// System.out.println("Opening a new connection, keep-alive set to: " + keep_alive);
			Socket client = new Socket();

			client.setReuseAddress(true);
			client.setSoTimeout(socket.getSoTimeout());
			client.setReceiveBufferSize(BUFF_SIZE);
			client.connect(socket.getRemoteSocketAddress(), socket.getSoTimeout());
			setSocket(client);
			reinitialized = true;

			// System.out.println("New Bosh socket: " + (++new_socket));
		}
		if ((data != null) && data.startsWith("<body")) {
			super.write(data);
		} else {
			if (sid != null) {
				Element body = new Element("body", data, new String[] { "xmlns", "sid", "rid" },
																	 new String[] { "http://jabber.org/protocol/httpbind",
								sid, "" + (++rid) });

				// System.out.println("SocketBosh writing data: " + body_str);
				if (restart) {
					body.setAttribute("xmpp:restart", "true");
					restart = false;
				}
				if (terminate) {

					// System.out.println("terminating connection: " + sid);
					body.setAttribute("type", "terminate");
				}
				super.write(body.toString());
			}
			if (terminate) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException ex) {

					// TODO Auto-generated catch block
					// ex.printStackTrace();
				}
				super.close();
			}
		}
	}
}    // SocketBosh


//~ Formatted in Tigase Code Convention on 13/02/20
