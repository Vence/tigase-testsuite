/*
 * SocketXMLIO.java
 *
 * Tigase Jabber/XMPP Server
 * Copyright (C) 2004-2012 "Artur Hefczyc" <artur.hefczyc@tigase.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
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

import tigase.xml.DefaultElementFactory;
import tigase.xml.Element;
import tigase.xml.SimpleParser;

//~--- JDK imports ------------------------------------------------------------

import java.io.EOFException;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStream;

import java.net.ConnectException;
import java.net.Socket;

import java.util.Iterator;
import java.util.Queue;

/**
 * Describe class SocketXMLIO here.
 *
 *
 * Created: Wed May 18 16:23:47 2005
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public class SocketXMLIO
				implements XMLIO {
	private static final int BUFF_SIZE = 2 * 1024;

	//~--- fields ---------------------------------------------------------------

	/** Field description */
	protected Socket socket       = null;
	private DomBuilderHandler dom = null;

	// private BufferedReader inp = null;
	private InputStreamReader inp = null;
	private OutputStream out      = null;
	private SimpleParser parser   = null;
	private char[] in_data        = new char[BUFF_SIZE];

	/** Field description */
	protected boolean ignore_presence = false;

	//~--- constructors ---------------------------------------------------------

	/**
	 * Creates a new <code>SocketXMLReader</code> instance.
	 *
	 *
	 * @param sock
	 *
	 * @throws IOException
	 */
	public SocketXMLIO(Socket sock) throws IOException {
		setSocket(sock);
	}

	//~--- set methods ----------------------------------------------------------

	/**
	 * Method description
	 *
	 *
	 * @param sock
	 *
	 * @throws IOException
	 */
	public void setSocket(Socket sock) throws IOException {
		socket = sock;
		out    = sock.getOutputStream();

		// inp = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		inp    = new InputStreamReader(sock.getInputStream());
		dom    = new DomBuilderHandler(new DefaultElementFactory());
		parser = new SimpleParser();
	}

	//~--- methods --------------------------------------------------------------

	/**
	 * Method description
	 *
	 */
	@Override
	public void close() {
		try {
			write( "</stream:stream>" );
			socket.close();
		} catch (Exception e) {}
	}

	//~--- get methods ----------------------------------------------------------

	/**
	 * Method description
	 *
	 *
	 * @return
	 */
	public boolean isConnected() {
		return (socket != null) && socket.isConnected();
	}

	//~--- methods --------------------------------------------------------------

	/*
	 *  (non-Javadoc)
	 * @see tigase.test.util.XMLIO#write(tigase.xml.Element)
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
	public void write(Element data) throws IOException {
		write(data.toString());
	}

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
		if (!socket.isConnected()) {
			throw new ConnectException("Socket is not connected.");
		}    // end of if (!socket.isConnected())

		// System.out.println("OUTPUT: " + data);
		out.write(data.getBytes());
		out.flush();
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
		if (!socket.isConnected()) {
			throw new ConnectException("Socket is not connected.");
		}    // end of if (!socket.isConnected())

		int res = inp.read(in_data);

		if (res < 0) {
			throw new EOFException("End of stream on network socket detected.");
		}    // end of if (res < 0)
		if (res > 0) {

//    char[] tmp_data = new char[res];
//    for (int i = 0; i < tmp_data.length; i++) {
//      tmp_data[i] = in_data[i];
//    } // end of for (int i = 0; i < tmp_data.length; i++)
//      System.out.println("\n [" + res + "] INPUT: " + new String(in_data, 0, res));
			parser.parse(dom, in_data, 0, res);
		}    // end of if (res > 0)

		Queue<Element> results = dom.getParsedElements();

		if (ignore_presence && (results != null)) {
			Iterator<Element> it = results.iterator();

			while (it.hasNext()) {
				Element el = it.next();

				if (el.getName() == "presence") {
					it.remove();
				}
			}
		}

		return results;
	}

	//~--- set methods ----------------------------------------------------------

	/**
	 * Method description
	 *
	 *
	 * @param ignore
	 */
	@Override
	public void setIgnorePresence(boolean ignore) {
		ignore_presence = ignore;
	}
}    // SocketXMLIO


//~ Formatted in Tigase Code Convention on 13/02/16
