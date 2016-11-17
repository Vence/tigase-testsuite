/*
 * TestIQVersion.java
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



package tigase.test.impl;

//~--- non-JDK imports --------------------------------------------------------

import tigase.test.TestAbstract;
import tigase.test.util.Params;

import tigase.xml.Element;

import static tigase.util.JIDUtils.*;

//~--- JDK imports ------------------------------------------------------------

import java.net.Socket;

import java.util.Map;
import java.util.TreeMap;

import javax.management.Attribute;

/**
 * Describe class TestIQVersion here.
 *
 *
 * Created: Thu Jun 16 05:58:19 2005
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public class TestIQVersion
				extends TestAbstract {
	private int counter     = 0;
	private String[] elems  = { "iq" };
	private String from     = null;
	private String hostname = "localhost";

	//~--- constructors ---------------------------------------------------------

	/**
	 * Creates a new <code>TestIQVersion</code> instance.
	 *
	 */
	public TestIQVersion() {
		super( new String[] { "jabber:client" },
					 new String[] { "iq-version" },
					 new String[] { "stream-open", "auth", "xmpp-bind" },
					 new String[] { "stream-open", "auth", "user-register", "tls-init" }
		);
	}

	//~--- methods --------------------------------------------------------------

	// Implementation of tigase.test.TestAbstract

	/**
	 * Describe <code>nextElementName</code> method here.
	 *
	 * @param element an <code>Element</code> value
	 * @return a <code>String</code> value
	 * @exception Exception if an error occurs
	 */
	public String nextElementName(final Element element) throws Exception {
		if (counter < elems.length) {
			return elems[counter++];
		}    // end of if (counter < elems.length)

		Map<String, String> ver = new TreeMap<String, String>();

		ver.put("Name", element.getCDataStaticStr(IQ_QUERY_NAME_PATH));
		ver.put("Version", element.getCDataStaticStr(IQ_QUERY_VERSION_PATH));
		ver.put("OS", element.getCDataStaticStr(IQ_QUERY_OS_PATH));

		Socket socket = (Socket) params.get("socket");
		String remote = socket.getInetAddress().getHostAddress();
		String local  = socket.getLocalAddress().getHostAddress();

		ver.put("Local IP", local);
		ver.put("Remote IP", remote);
		params.put("Version", ver);

		return null;
	}

	//~--- get methods ----------------------------------------------------------

	/**
	 * Describe <code>getElementData</code> method here.
	 *
	 * @param string a <code>String</code> value
	 * @return a <code>String</code> value
	 * @exception Exception if an error occurs
	 */
	public String getElementData(final String string) throws Exception {
		switch (counter) {
		case 1 :
			return "<iq type='get' to='" + hostname + "' from='" + from + "' id='version_1'>" +
						 "<query xmlns='jabber:iq:version'/>" + "</iq>";

		default :
			return null;
		}    // end of switch (counter)
	}

	/**
	 * Describe <code>getRespElementNames</code> method here.
	 *
	 * @param string a <code>String</code> value
	 * @return a <code>String[]</code> value
	 * @exception Exception if an error occurs
	 */
	public String[] getRespElementNames(final String string) throws Exception {
		return new String[] { "iq" };
	}

	/**
	 * Describe <code>getRespOptionalNames</code> method here.
	 *
	 * @param string a <code>String</code> value
	 * @return a <code>String[]</code> value
	 * @exception Exception if an error occurs
	 */
	public String[] getRespOptionalNames(final String string) throws Exception {
		return null;
	}

	/**
	 * Describe <code>getRespElementAttributes</code> method here.
	 *
	 * @param string a <code>String</code> value
	 * @return an <code>Attribute[]</code> value
	 * @exception Exception if an error occurs
	 */
	public Attribute[] getRespElementAttributes(final String string) throws Exception {
		switch (counter) {
		case 1 :
			return new Attribute[] { new Attribute("type", "result"),
															 new Attribute("id", "version_1") };

		default :
			return null;
		}    // end of switch (counter)
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

		String user_name = params.get("-user-name", "test_user@localhost");
		String user_resr = params.get("-user-resr", "xmpp-test");
		String name      = getNodeNick(user_name);

		if ((name == null) || name.equals("")) {
			from = user_name + "@" + hostname + "/" + user_resr;
		} else {
			from = user_name + "/" + user_resr;
		}    // end of else
	}
}    // TestIQVersion


//~ Formatted in Tigase Code Convention on 13/02/16
