/*
 * TestReceiveMessage.java
 *
 * Tigase Jabber/XMPP Server
 * Copyright (C) 2004-2013 "Tigase, Inc." <office@tigase.com>
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

import tigase.test.ResultCode;
import tigase.test.TestAbstract;
import tigase.test.util.Params;
import tigase.test.util.TestUtil;
import tigase.test.util.XMLIO;

import tigase.xml.Element;

import static tigase.util.JIDUtils.*;

//~--- JDK imports ------------------------------------------------------------

import java.net.Socket;
import java.net.SocketTimeoutException;

import java.util.Map;
import java.util.Queue;

import javax.management.Attribute;

/**
 * Describe class TestReceiveMessage here.
 *
 *
 * Created: Wed Jun  8 08:57:57 2005
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public class TestReceiveMessage
				extends TestAbstract {
	private String data              = null;
	private String hostname          = "localhost";
	private String jid               = null;
	private String msg_1             = null;
	private String msg_2             = null;
	private long repeat              = 0;
	private Attribute[] resp_attribs = null;
	private String user_name         = "test_user@localhost";
	private String user_resr         = "xmpp-test";

	//~--- constructors ---------------------------------------------------------

	/**
	 * Creates a new <code>TestReceiveMessage</code> instance.
	 *
	 */
	public TestReceiveMessage() {
		super(new String[] { "jabber:client" }, new String[] { "msg-listen" },
					new String[] { "stream-open",
												 "auth", "xmpp-bind", "presence" }, new String[] { "tls-init",
												 "privacy-block-msg",
						"privacy-long-list" });
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
	@Override
	public String nextElementName(final Element element) throws Exception {
		if ((element == null) ||

		// ignore "service-unavailable" (error type="cancel" code="503")
		// bounce-backs from unavailable peers
		("error".equals(element.getAttributeStaticStr("type")) &&
				"503".equals(element.getAttributeStaticStr("code")))) {

			// System.out.println("JIDUtils = " + jid);
			TestUtil.addDaemonJID(jid, (Socket) params.get("socket"));
			data = null;

			return "";
		}    // end of if (element == null)

//  data = msg_1 + element.getAttribute("from") + msg_2;
		data = "<message type='chat' from='" + jid + "' to='" +
					 element.getAttributeStaticStr("from") + "'><body>Message OK</body>"

		// + " || original stanza: \n" + element.toString() + "\n\n"
		+ "</message>";

		return "message";
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
		return data;
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
		return new String[] { "message" };
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

	/**
	 * Describe <code>getRespElementAttributes</code> method here.
	 *
	 * @param string a <code>String</code> value
	 * @return an <code>Attribute[]</code> value
	 * @exception Exception if an error occurs
	 */
	@Override
	public Attribute[] getRespElementAttributes(final String string) throws Exception {
		return resp_attribs;
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
		hostname  = params.get("-host", hostname);
		user_resr = params.get("-user-resr", user_resr);
		repeat    = params.get("-messages", -1);

		String name = getNodeNick(user_name);

		if ((name == null) || name.equals("")) {
			jid = user_name + "@" + hostname + "/" + user_resr;
		} else {
			jid = user_name + "/" + user_resr;
		}    // end of else

		// resp_attribs = new Attribute[] {new Attribute("to", jid)};
		msg_1 = "<message type='chat' from='" + jid + "' to='";
		msg_2 = "'><body>Message OK</body></message>";
	}

	/**
	 * Describe <code>run</code> method here.
	 *
	 * @return a <code>boolean</code> value
	 */
	@Override
	public boolean run() {
		try {
			String elem = null;
			XMLIO io    = (XMLIO) params.get("socketxmlio");

			if (io == null) {
				TestUtil.removeDaemonJID(jid);
				resultCode = ResultCode.SOCKET_NOT_INITALIZED;
				System.out.println("Message listener FINISHED 1");

				return false;
			}        // end of if (sock == null)
			elem = nextElementName(reply);
			while ((elem != null) && ((repeat--) != 0)) {
				debug("Processing element: " + elem + "\n");

				Queue<Element> results = io.read();
				Element rep            = null;

				while ((rep = results.poll()) != null) {
					reply = rep;
					debug("Received data: " + reply.toString() + "\n");
					elem = nextElementName(reply);

					String data = getElementData(elem);

					if ((data != null) &&!data.equals("")) {
						io.write(data);
						debug("Sent data: " + data + "\n");
					}    // end of if (data != null && !data.equals(""))
				}
			}
			Thread.sleep(100);
			TestUtil.removeDaemonJID(jid);

			// System.out.println("Message listener FINISHED 2");
			return true;
		} catch (SocketTimeoutException e) {
			TestUtil.removeDaemonJID(jid);
			addInput("" + e + "\n" + TestUtil.stack2String(e));
			resultCode = ResultCode.PROCESSING_EXCEPTION;
			exception  = e;

			// System.out.println("Message listener FINISHED 3");
			return false;
		} catch (Exception e) {
			TestUtil.removeDaemonJID(jid);
			addInput("" + e + "\n" + TestUtil.stack2String(e));
			resultCode = ResultCode.PROCESSING_EXCEPTION;
			exception  = e;

			// e.printStackTrace();
			// System.out.println("Message listener FINISHED 4");
			return false;
		}    // end of try-catch

		// System.out.println("Message listener FINISHED 5");
	}
}    // TestReceiveMessage


//~ Formatted in Tigase Code Convention on 13/02/28
