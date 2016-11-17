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

import java.util.Map;
import javax.management.Attribute;
import tigase.test.TestAbstract;
import tigase.xml.Element;
import tigase.test.util.Params;

import static tigase.util.JIDUtils.*;

/**
 * Describe class TestRosterGet here.
 *
 *
 * Created: Thu Jun 30 22:45:45 2005
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public class TestRosterGet extends TestAbstract {

  private String user_name = "test_user@localhost";
  private String user_resr = "xmpp-test";
  private String user_emil = "test_user@localhost";
  private String hostname = "localhost";
  private String jid = null;
  private String id = null;

  private String[] elems = {"iq"};
  private int counter = 0;

  /**
   * Creates a new <code>TestRosterGet</code> instance.
   *
   */
  public TestRosterGet() {
    super(
      new String[] {"jabber:client"},
      new String[] {"roster-get"},
      new String[] {"stream-open", "auth", "xmpp-bind"},
      new String[] {"tls-init"}
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
    return
      "<iq type='get' id='roster_1' from='" + jid + "'>"
      + "<query xmlns='jabber:iq:roster'/>"
      + "</iq>";
  }

  /**
   * Describe <code>getRespElementNames</code> method here.
   *
   * @param string a <code>String</code> value
   * @return a <code>String[]</code> value
   * @exception Exception if an error occurs
   */
  public String[] getRespElementNames(final String string) throws Exception {
    return new String[] {"iq"};
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
    return new Attribute[] {
      new Attribute("type", "result"),
      new Attribute("id", "roster_1"),
      new Attribute("to", jid),
    };
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

} // TestRosterGet
