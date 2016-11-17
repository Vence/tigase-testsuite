/*
 * TestExtCompOpen.java
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



package tigase.test.impl;

//~--- non-JDK imports --------------------------------------------------------

import tigase.test.TestAbstract;
import tigase.test.util.Params;

import tigase.util.Algorithms;

import tigase.xml.Element;

//~--- JDK imports ------------------------------------------------------------

import java.util.Map;

import javax.management.Attribute;

/**
 * Describe class TestExtCompOpen here.
 *
 *
 * Created: Tue May 17 20:23:58 2005
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public class TestExtCompOpen
				extends TestAbstract {
	/** Field description */
	protected String hostname = "localhost";
	private String conn_id    = null;
	private int counter       = 0;
	private String[] elems    = { "stream:stream", "handshake" };
	private String secret     = "someSecret";

	//~--- constructors ---------------------------------------------------------

	/**
	 * Creates a new <code>TestExtCompOpen</code> instance.
	 *
	 */
	public TestExtCompOpen() {
		super(new String[] { "jabber:component:connect" },
					new String[] { "stream-ext-comp" }, new String[] { "socket" }, null);
	}

	//~--- methods --------------------------------------------------------------

	/**
	 * Method description
	 *
	 *
	 * @param reply
	 *
	 * @return
	 */
	@Override
	public String nextElementName(final Element reply) {
		if (counter < elems.length) {
			return elems[counter++];
		}    // end of if (counter < elems.length)

		return null;
	}

	/**
	 * Method description
	 *
	 *
	 * @param reply
	 *
	 * @throws Exception
	 */
	@Override
	public void replyElement(final Element reply) throws Exception {
		if ((reply != null) && reply.getName().equals("stream:stream")) {
			conn_id = reply.getAttributeStaticStr("id");
		}
	}

	//~--- get methods ----------------------------------------------------------

	/**
	 * Method description
	 *
	 *
	 * @param element
	 *
	 * @return
	 *
	 * @throws Exception
	 */
	@Override
	public String getElementData(final String element) throws Exception {
		if (element.equals("stream:stream")) {
			return "<stream:stream " + "xmlns='jabber:component:accept' " +
						 "xmlns:stream='http://etherx.jabber.org/streams' " + "to='" + hostname +
						 "'> ";
		}
		if (element.equals("handshake")) {
			String digest = Algorithms.hexDigest(conn_id, secret, "SHA");

			return "<handshake>" + digest + "</handshake>";
		}

		return null;
	}

	/**
	 * Method description
	 *
	 *
	 * @param element
	 *
	 * @return
	 */
	@Override
	public String[] getRespElementNames(final String element) {
		if (element.equals("stream:stream")) {
			return new String[] { "stream:stream" };
		}
		if (element.equals("handshake")) {
			return new String[] { "handshake" };
		}

		return null;
	}

	/**
	 * Method description
	 *
	 *
	 * @param element
	 *
	 * @return
	 */
	@Override
	public Attribute[] getRespElementAttributes(final String element) {
		if (element.equals("stream:stream")) {
			return new Attribute[] { new Attribute("xmlns", "jabber:component:accept"),
															 new Attribute("xmlns:stream",
															 "http://etherx.jabber.org/streams"),
															 new Attribute("from", hostname) };
		}

		return null;
	}

	/**
	 * Method description
	 *
	 *
	 * @param element
	 *
	 * @return
	 */
	@Override
	public String[] getRespOptionalNames(final String element) {
		return null;
	}

	//~--- methods --------------------------------------------------------------

	// Implementation of TestIfc

	/**
	 * Describe <code>init</code> method here.
	 *
	 * @param map a <code>Map</code> value
	 * @param vars
	 */
	@Override
	public void init(final Params map, Map<String, String> vars) {
		super.init(map, vars);
		hostname = params.get("-host", hostname);
		secret   = params.get("-secret", secret);
	}
}    // TestExtCompOpen


//~ Formatted in Tigase Code Convention on 13/02/20
