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

import java.util.List;
import java.util.Map;
import tigase.test.TestAbstract;
import javax.management.Attribute;
import tigase.xml.Element;
import tigase.test.util.Params;
import tigase.test.util.TestUtil;
import tigase.util.Algorithms;
import java.net.Socket;

/**
 * Describe class TestAuth here.
 *
 *
 * Created: Tue May 17 20:27:02 2005
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public class TestAuth extends TestAbstract {

  private String user_name = "test_user@localhost";
  private String user_pass = "test_pass";
  private String user_resr = "xmpp-test";
  private String hostname = "localhost";
  private boolean digest = true;
  private String pass_elem = null;

  private String[] elems = {"iq", "iq"};
  private int counter = 0;

  /**
   * Creates a new <code>TestAuth</code> instance.
   *
   */
  public TestAuth() {
    super(
      new String[] {"jabber:client"},
      new String[] {"auth-plain", "auth-digest"},
      new String[] {"stream-open"},
      new String[] {"ssl-init", "tls-init", "user-register"}
      );
  }

  public String nextElementName(final Element reply) throws Exception {
    if (counter < elems.length) {
      return elems[counter++];
    } // end of if (counter < elems.length)
    //    System.out.println("\nUser: " + user_name + " AUTH OK");
    params.put("authorized", true);
    if (params.containsKey("-active-connection")) {
      TestUtil.addActiveConnection((Socket)params.get("socket"));
    } // end of if (params.containsKey())
    return null;
  }

  public String getElementData(final String element) throws Exception {
    switch (counter) {
    case 1:
      return
        "<iq type='get' id='" + user_name + "_1' to='" + hostname + "'>"
        + "<query xmlns='jabber:iq:auth'>"
        + "<username>" + user_name + "</username>"
        + "</query>" +
        "</iq>";
    case 2:
      return
        "<iq type='set' id='" + user_name + "_2'>"
        + "<query xmlns='jabber:iq:auth'>"
        + "<username>" + user_name + "</username>"
        + getPassElem()
        + "<resource>" + user_resr + "</resource>"
        + "</query>" +
        "</iq>";
    default:
      return null;
    } // end of switch (counter)
  }

  private String getPassElem() throws Exception {
    if (digest) {
      return "<digest>"
        + Algorithms.digest((String)params.get("session-id"), user_pass, "SHA")
        + "</digest>";
    } // end of if (digest)
    else {
      return "<password>" + user_pass + "</password>";
    } // end of if (digest) else
  }

  public String[] getRespElementNames(final String element) {
    return new String[] {"iq"};
  }

  public Attribute[] getRespElementAttributes(final String element) {
    switch (counter) {
    case 1:
      return new Attribute[]
      {
        new Attribute("type", "result"),
        new Attribute("id", user_name + "_1")
      };
    case 2:
      return new Attribute[]
      {
        new Attribute("type", "result"),
        new Attribute("id", user_name + "_2")
      };
    default:
      return null;
    } // end of switch (counter)
  }

  public String[] getRespOptionalNames(final String element) {
    return null;
  }

  // Implementation of TestIfc

  /**
   * Describe <code>init</code> method here.
   *
   * @param map a <code>Map</code> value
   */
  public void init(final Params map, Map<String, String> vars) {
    super.init(map, vars);
    user_name = params.get("-user-name", user_name);
    user_pass = params.get("-user-pass", user_pass);
    user_resr = params.get("-user-resr", user_resr);
    hostname = params.get("-host", hostname);
    String test_ns = (String)params.get("-test-ns");
    boolean found = false;
    if (test_ns != null) {
      if (test_ns.contains("auth-digest")) {
        digest = true;
        found = true;
      } // end of if (test_ns.contains("auth-digest"))
      if (test_ns.contains("auth-plain")) {
        digest = false;
        found = true;
      } // end of if (test_ns.contains("auth-digest"))
    }
    if (!found) {
      String auth = (String)params.get("-def-auth");
      if (auth.contains("auth-digest")) {
        digest = true;
      } // end of if (test_ns.contains("auth-digest"))
      if (auth.contains("auth-plain")) {
        digest = false;
      } // end of if (test_ns.contains("auth-digest"))
    } // end of if (auth == null)
  }

} // TestAuth
