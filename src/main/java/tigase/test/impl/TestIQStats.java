/*
 * TestIQStats.java
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

import tigase.test.StatItem;
import tigase.test.TestAbstract;
import tigase.test.util.Params;

import tigase.xml.Element;

import static tigase.util.JIDUtils.*;

//~--- JDK imports ------------------------------------------------------------

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.management.Attribute;

/**
 * Describe class TestIQStats here.
 *
 *
 * Created: Thu Jun 16 06:21:54 2005
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public class TestIQStats
				extends TestAbstract {
	private int counter     = 0;
	private String[] elems  = { "iq" };
	private String from     = null;
	private String hostname = "localhost";

	//~--- constructors ---------------------------------------------------------

	/**
	 * Creates a new <code>TestIQStats</code> instance.
	 *
	 */
	public TestIQStats() {
		super(new String[] { "jabber:client" }, new String[] { "iq-stats" },
					new String[] { "stream-open",
												 "auth", "xmpp-bind" }, new String[] { "tls-init" });
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
			return "<iq type='set' id='stats_1' to='stats@" + hostname + "'>" +
						 "<command xmlns='http://jabber.org/protocol/commands' node='stats'>" +
						 "<x xmlns='jabber:x:data' type='submit'>" +
						 "<field type='list-single' var='Stats level' >" + "<value>FINER</value>" +
						 "</field>" + "</x>" + "</command>" + "</iq>";

		default :
			return null;
		}    // end of switch (counter)
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
															 new Attribute("id", "stats_1") };

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

		List<StatItem> stats = new LinkedList<StatItem>();
		List<Element> items  = element.getChildrenStaticStr(IQ_COMMAND_X_PATH);

		for (Element item : items) {
			String name = item.getAttributeStaticStr("var");
			int idx     = name.indexOf("/");
			String comp = "unknown";
			String stat = name;

			if (idx >= 0) {
				comp = name.substring(0, idx);
				stat = name.substring(idx + 1);
			}
			stats.add(new StatItem(comp, item.getCDataStaticStr(FIELD_VALUE_PATH), "none",
														 stat));
		}    // end of for (Element item: items)
		params.put("Statistics", stats);

		return null;
	}
}    // TestIQStats


//~ Formatted in Tigase Code Convention on 13/02/20
