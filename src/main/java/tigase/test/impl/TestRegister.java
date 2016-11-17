/*
 * Tigase XMPP/Jabber Test Suite
 * Copyright (C) 2004-2009 "Artur Hefczyc" <artur.hefczyc@tigase.org>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. Look for COPYING file in the top folder.
 * If not, see http://www.gnu.org/licenses/.
 *
 * $Rev$
 * Last modified by $Author$
 * $Date$
 */

package tigase.test.impl;

//~--- non-JDK imports --------------------------------------------------------

import tigase.test.TestAbstract;
import tigase.test.util.Params;

import tigase.xml.Element;

//~--- JDK imports ------------------------------------------------------------

import java.util.Map;

import javax.management.Attribute;

//~--- classes ----------------------------------------------------------------

/**
 * Describe class TestRegister here.
 *
 *
 * Created: Wed May 18 12:14:52 2005
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public class TestRegister extends TestAbstract {
	private int counter = 0;
	private String[] elems = { "iq", "iq" };
	private String user_emil = "test_user@localhost";
	private String user_name = "test_user@localhost";
	private String user_pass = "test_pass";

	//~--- constructors ---------------------------------------------------------

	/**
	 * Creates a new <code>TestRegister</code> instance.
	 *
	 */
	public TestRegister() {
		super(new String[] { "jabber:client" }, new String[] { "user-register" },
				new String[] { "stream-open" }, new String[] { "ssl-init",
				"tls-init" });
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
		switch (counter) {
			case 1 :
				return "<iq type='get' id='reg1'><query xmlns='jabber:iq:register'/></iq>";

			case 2 :
				return "<iq type='set' id='reg2'>" + "<query xmlns='jabber:iq:register'>"
						+ "<username>" + user_name + "</username>" + "<password>" + user_pass
							+ "</password>" + "<email>" + user_emil + "</email>" + "</query>" + "</iq>";

			default :
				return null;
		}    // end of switch (counter)
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
		switch (counter) {
			case 1 :
				return new Attribute[] { new Attribute("type", "result"),
						new Attribute("id", "reg1") };

			case 2 :
				return new Attribute[] { new Attribute("type", "result"),
						new Attribute("id", "reg2") };

			default :
				return null;
		}    // end of switch (counter)
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
		return new String[] { "iq" };
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
		user_pass = params.get("-user-pass", user_pass);
		user_emil = params.get("-user-emil", user_emil);
	}

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
}    // TestRegister


//~ Formatted in Sun Code Convention


//~ Formatted by Jindent --- http://www.jindent.com
