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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import tigase.test.TestAbstract;
import tigase.test.StatItem;
import tigase.test.util.Params;
import javax.management.Attribute;
import tigase.test.ResultsDontMatchException;
import tigase.xml.Element;
import tigase.xml.XMLUtils;
import tigase.test.util.ElementUtil;
import tigase.test.util.EqualError;

import static tigase.util.JIDUtils.*;

/**
 * Describe class TestIQCommandStop here.
 *
 *
 * Created: Wed Jan 24 21:23:38 2007
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public class TestIQCommandStop extends TestAbstract {

  private String user_name = "test_user@localhost";
  private String user_resr = "xmpp-test";
  private String user_emil = "test_user@localhost";
  private String hostname = "localhost";
  private String jid = null;
  private String id = null;
	private String xmlns = "http://jabber.org/protocol/commands";

  private String[] elems = {"iq"};
  private int counter = 0;
  private Element expected_result, optional_result = null;
  private Attribute[] result = null;
  private String[] resp_name = null;

	/**
	 * Creates a new <code>TestIQCommandStop</code> instance.
	 *
	 */
	public TestIQCommandStop() {
    super(
      new String[] {"jabber:client"},
      new String[] {"command-stop"},
      new String[] {"stream-open", "auth"},
      new String[] {"user-register", "tls-init"}
      );
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
		boolean error = false;
		String message = null;
		if (element != null) {
			if (expected_result != null) {
				EqualError res1 = ElementUtil.equalElemsDeep(expected_result, element);
				if (!res1.equals && optional_result != null) {
					EqualError res2 = ElementUtil.equalElemsDeep(optional_result, element);
					if (!res2.equals) {
						error = true;
						message = res1.message + ", " + res2.message;
					}
				} else {
					if (!res1.equals) {
						error = true;
						message = res1.message;
					}
				}
			} // end of if (ElementUtil.equalElemsDeep(expected_result, query))
		} else {
			if (expected_result != null) {
				error = true;
				message = "Missing expected element: " + expected_result.getName();
			} // end of if (expected_result == null)
		} // end of else
		if (error) {
			throw new ResultsDontMatchException(getClass().getName() +
				", expected: '" + expected_result + "', Received: '" + element + "'"
				+ ", equals error: " + message);
		} // end of if (error)
    if (counter < elems.length) {
      return elems[counter++];
    } // end of if (counter < elems.length)
    return null;
  }

  /**
   * Describe <code>getElementData</code> method here.
   *
   * @param string a <code>String</code> value
   * @return a <code>String</code> value
   * @exception Exception if an error occurs
   */
  public String getElementData(final String string) throws Exception {
    result = new Attribute[] {
      new Attribute("type", "result"),
      new Attribute("id", "command_" + counter),
      new Attribute("to", jid),
    };
    switch (counter) {
    case 1:
			expected_result = new Element("iq",
				new Element[] {new Element("command",
						new String[] {"xmlns", "status", "node"},
						new String[] {xmlns, "completed", "controll/stop"})},
				new String[] {"type", "id", "from"},
				new String[] {"result", "command_1", "tigase-xmpp-server." + hostname});
			resp_name = new String[] {"iq"};
      return
				"<iq type=\"set\" to=\"tigase-xmpp-server." + hostname
				+ "\" id=\"command_1\" >"
				+ "<command xmlns=\"http://jabber.org/protocol/commands\""
				+ "node=\"controll/stop\" /></iq>";
    default:
      return null;
    } // end of switch (counter)
  }

  /**
   * Describe <code>getRespElementNames</code> method here.
   *
   * @param string a <code>String</code> value
   * @return a <code>String[]</code> value
   * @exception Exception if an error occurs
   */
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
		return result;
  }

  // Implementation of tigase.test.TestIfc

  /**
   * Describe <code>init</code> method here.
   *
   * @param params a <code>Params</code> value
   */
  public void init(final Params params, Map<String, String> vars) {
    super.init(params, vars);
    user_name = params.get("-user-name", user_name);
    user_resr = params.get("-user-resr", user_resr);
    user_emil = params.get("-user-emil", user_emil);
    hostname = params.get("-host", hostname);
    String name = getNodeNick(user_name);
    if (name == null || name.equals("")) {
      jid = user_name + "@" + hostname + "/" + user_resr;
    } else {
      jid = user_name + "/" + user_resr;
    } // end of else
    if (name == null || name.equals("")) {
      id = user_name + "@" + hostname;
    } else {
      id = user_name;
    } // end of else
  }

}
