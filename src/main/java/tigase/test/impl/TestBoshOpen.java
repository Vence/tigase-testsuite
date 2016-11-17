/*
 * TestBoshOpen.java
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

import tigase.xml.Element;

//~--- JDK imports ------------------------------------------------------------

import java.util.Map;

import javax.management.Attribute;

/**
 * Describe class TestBoshOpen here.
 *
 *
 * Created: Tue May 17 20:23:58 2005
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public class TestBoshOpen
				extends TestAbstract {
	/** Field description */
	protected String hostname = "localhost";
	private int counter       = 0;
	private String[] elems    = { "body" };

	//~--- constructors ---------------------------------------------------------

	/**
	 * Creates a new <code>TestBoshOpen</code> instance.
	 *
	 */
	public TestBoshOpen() {
		super(new String[] { "jabber:client", "jabber:server", "jabber:component:accept" },
					new String[] { "stream-bosh" }, new String[] { "socket-bosh" }, null);
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
	public void replyElement(final Element reply) throws Exception {
		if ((reply != null) && reply.getName().equals("stream:stream")) {
			params.put("session-id", reply.getAttributeStaticStr("id"));
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
	public String getElementData(final String element) throws Exception {
		if (element.equals("body")) {
			return "<body " + "xmlns='http://jabber.org/protocol/httpbind' " +
						 "xmlns:xmpp='urn:xmpp:xbosh' " + "xml:lang='en' " + "rid='1216' " +
						 "content='text/xml; charset=utf-8' " + "to='" + hostname + "' " +
						 "ver='1.6'/>";
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
	public String[] getRespElementNames(final String element) {
		if (element.equals("body")) {
			return new String[] { "stream:features" };
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
	public Attribute[] getRespElementAttributes(final String element) {
		if (element.equals("stream:features")) {
			return new Attribute[] {};
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
	public void init(final Params map, Map<String, String> vars) {
		super.init(map, vars);
		hostname = params.get("-host", hostname);
	}
}    // TestBoshOpen


//~ Formatted in Tigase Code Convention on 13/02/20
