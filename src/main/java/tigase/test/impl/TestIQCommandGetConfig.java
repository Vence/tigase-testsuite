/*
 * TestIQCommandGetConfig.java
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

import tigase.test.ResultsDontMatchException;
import tigase.test.StatItem;
import tigase.test.TestAbstract;
import tigase.test.util.Params;

import tigase.xml.Element;
import tigase.xml.XMLUtils;

import static tigase.util.JIDUtils.*;

//~--- JDK imports ------------------------------------------------------------

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.management.Attribute;

/**
 * Describe class TestIQCommandGetConfig here.
 *
 *
 * Created: Wed Jan 24 21:23:38 2007
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public class TestIQCommandGetConfig
				extends TestAbstract {
	private int counter             = 0;
	private String[] elems          = { "iq" };
	private Element expected_result,
									optional_result = null;
	private String hostname         = "localhost";
	private String id               = null;
	private String jid              = null;
	private String[] resp_name      = null;
	private Attribute[] result      = null;
	private String user_emil        = "test_user@localhost";
	private String user_name        = "test_user@localhost";
	private String user_resr        = "xmpp-test";
	private String xmlns            = "http://jabber.org/protocol/commands";

	//~--- constructors ---------------------------------------------------------

	/**
	 * Creates a new <code>TestIQCommandGetConfig</code> instance.
	 *
	 */
	public TestIQCommandGetConfig() {
		super(new String[] { "jabber:client" }, new String[] { "command-get-config" },
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
	@Override
	public String getElementData(final String string) throws Exception {
		result = new Attribute[] { new Attribute("type", "result"),
															 new Attribute("id", "config-get"),
															 new Attribute("to", jid), };
		switch (counter) {
		case 1 :
			expected_result = new Element("iq",
																		new Element[] {
																			new Element("command",
																				new Element[] {
																					new Element("x", new String[] { "xmlns",
							"type" }, new String[] { "jabber:x:data", "result" }) }, new String[] {
								"xmlns",
								"status", "node" }, new String[] { xmlns, "completed",
								"config-list" }) }, new String[] { "type",
							"id", "from" }, new String[] { "result", "config-get",
							"basic-conf@" + hostname });
			resp_name = new String[] { "iq" };

			return "<iq type=\"set\" to=\"basic-conf@" + hostname + "\"" +
						 " id=\"config-get\">" + "<command xmlns=\"" + xmlns + "\"" +
						 " node=\"config-list\">" + "<x xmlns=\"jabber:x:data\" type=\"submit\">" +
						 "<field type=\"list-single\" var=\"comp-name\">" +
						 "<value>sess-man</value></field></x></command></iq>";

//  "<iq type=\"set\" to=\"basic-conf@" + hostname + "\" id=\"command_1\" >"
//  + "<command xmlns=\"http://jabber.org/protocol/commands\""
//  + "node=\"config/list/sess-man\" /></iq>";
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
	@Override
	public Attribute[] getRespElementAttributes(final String string) throws Exception {
		return result;
	}

	/**
	 * Describe <code>getRespElementNames</code> method here.
	 *
	 * @param string a <code>String</code> value
	 * @return a <code>String[]</code> value
	 * @exception Exception if an error occurs
	 */
	@Override
	public String[] getRespElementNames(final String string) throws Exception {
		return resp_name;
	}

	/**
	 * Describe <code>getRespOptionalNames</code> method here.
	 *
	 * @param string a <code>String</code> value
	 * @return a <code>String[]</code> value
	 * @exception Exception if an error occurs
	 */
	@Override
	public String[] getRespOptionalNames(final String string) throws Exception {
		return null;
	}

	//~--- methods --------------------------------------------------------------

	// Implementation of tigase.test.TestIfc

	/**
	 * Describe <code>init</code> method here.
	 *
	 * @param params a <code>Params</code> value
	 * @param vars
	 */
	@Override
	public void init(final Params params, Map<String, String> vars) {
		super.init(params, vars);
		user_name = params.get("-user-name", user_name);
		user_resr = params.get("-user-resr", user_resr);
		user_emil = params.get("-user-emil", user_emil);
		hostname  = params.get("-host", hostname);

		String name = getNodeNick(user_name);

		if ((name == null) || name.equals("")) {
			jid = user_name + "@" + hostname + "/" + user_resr;
		} else {
			jid = user_name + "/" + user_resr;
		}    // end of else
		if ((name == null) || name.equals("")) {
			id = user_name + "@" + hostname;
		} else {
			id = user_name;
		}    // end of else
	}

	/**
	 * Describe <code>nextElementName</code> method here.
	 *
	 * @param element an <code>Element</code> value
	 * @return a <code>String</code> value
	 * @exception Exception if an error occurs
	 */
	@Override
	public String nextElementName(final Element element) throws Exception {
		boolean error  = true;
		String message = null;

		if (element != null) {
			List<Element> items = element.getChildrenStaticStr(IQ_COMMAND_X_PATH);

			if (items != null) {
				error = false;

				List<StatItem> stats = new LinkedList<StatItem>();
				String comp          = "unknown";

				for (Element item : items) {
					String name  = item.getAttributeStaticStr("var");
					String cdata = item.getChildCDataStaticStr(FIELD_VALUE_PATH);

					if ("comp-name".equals(name)) {
						comp = cdata;
					} else {
						if (!"hidden".equals(item.getAttributeStaticStr("type"))) {
							if (cdata == null) {
								cdata = "";
							}
							stats.add(new StatItem(comp, XMLUtils.unescape(cdata), "&nbsp;", name));
						}
					}
				}    // end of for (Element item: items)
				params.put("Configuration", stats);
			} else {
				message = "Not a configuration packet";
			}
		} else {
			if (expected_result == null) {
				error = false;
			}      // end of if (expected_result == null)
		}        // end of else
		if (error) {
			throw new ResultsDontMatchException("Expected: " + expected_result +
							", Received: " + element + "'" + ", equals error: " + message);
		}    // end of if (error)
		if (counter < elems.length) {
			return elems[counter++];
		}    // end of if (counter < elems.length)

		return null;
	}
}


//~ Formatted in Tigase Code Convention on 13/02/20
