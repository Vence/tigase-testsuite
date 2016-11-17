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
import tigase.test.util.Params;
import tigase.test.util.TestUtil;
import tigase.xml.Element;

import static tigase.util.JIDUtils.*;

/**
 * Describe class TestSendMessage here.
 *
 *
 * Created: Tue Jun  7 09:02:51 2005
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public class TestSendMessage extends TestAbstract {

  private String user_name = "test_user@localhost";
  private String user_resr = "xmpp-test";
  private String hostname = "localhost";
  private String from = null;
  private String to = "test_user@localhost";
  private String remote_address = null;
  private int loop = 1;
  private boolean daemon_to = false;
	private String expect_type = "chat";
  private String msg_1 = null;
  private String msg_2 = null;

  private String msg = "message";
  private int counter = 0;

  /**
   * Creates a new <code>TestSendMessage</code> instance.
   *
   */
  public TestSendMessage() {
    super(
      new String[] {"jabber:client"},
      new String[] {"msg-send"},
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
	@Override
  public String nextElementName(final Element element) throws Exception {
    if (counter++ < loop) {
      return msg;
    } // end of if (counter < loop)
    return null;
  }

  /**
   * Describe <code>getElementData</code> method here.
   *
   * @param string a <code>String</code> value
   * @return a <code>String</code> value
   * @exception Exception if an error occurs
   */
	@Override
  public String getElementData(final String string) throws Exception {
    if (string.equals("message")) {
//      try {
//				Thread.sleep(1000);
//			} catch (Exception e) {
//			}
			remote_address = daemon_to ? getDaemonTo() : to;
      return msg_1 + remote_address + "'>Test message no. " + counter + msg_2;
    } // end of if (string.equals("message"))
    return null;
  }

  private String getDaemonTo() {
    return TestUtil.getSeqJID();
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
		if (timeoutOk) {
			return new String[] {"complete rubish, there should be no response at all"};
		} else {
			return new String[] {"message"};
		} // end of else
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
    return new String[] {"presence"};
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
		//		return null;
    return new Attribute[]
    {
      new Attribute("type", expect_type)
    };
  }

  // Implementation of tigase.test.TestIfc

  /**
   * Describe <code>init</code> method here.
   *
   * @param params a <code>Params</code> value
   */
	@Override
  public void init(final Params params, Map<String, String> vars) {
    super.init(params, vars);
    user_name = params.get("-user-name", user_name);
    hostname = params.get("-host", hostname);
    user_resr = params.get("-user-resr", user_resr);
    String name = getNodeNick(user_name);
    if (name == null || name.equals("")) {
      from = user_name + "@" + hostname + "/" + user_resr;
    } else {
      from = user_name + "/" + user_resr;
    } // end of else
    to = params.get("-to-jid", to);
    if (to.equalsIgnoreCase("$(self)")) {
      to = from;
    } // end of if (to.equalsIgnoreCase("self"))
    loop = params.get("-messages", loop);
    daemon_to = to.equals("$(daemons)");
		expect_type = params.get("-expect-type", expect_type);
    msg_1 = "<message type='chat' from='" + from + "' to='";
    msg_2 = ", from: " + from + ".</message>";
  }

} // TestSendMessage
