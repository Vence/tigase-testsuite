/*
 * TestSASL.java
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

import tigase.util.Base64;

import tigase.xml.Element;

//~--- JDK imports ------------------------------------------------------------

import java.io.IOException;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.management.Attribute;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.AuthorizeCallback;
import javax.security.sasl.RealmCallback;
import javax.security.sasl.Sasl;
import javax.security.sasl.SaslClient;

/**
 * Describe class TestSASL here.
 *
 *
 * Created: Tue May 17 20:27:16 2005
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public class TestSASL
				extends TestAbstract {
	private String data       = null;
	private String hostname   = "localhost";
	private String response   = null;
	private SaslClient sasl   = null;
	private String user_name  = "test_user@localhost";
	private String user_pass  = "test_pass";
	private String user_resr  = "xmpp-test";
	private boolean bosh_mode = false;

	//~--- constructors ---------------------------------------------------------

	/**
	 * Creates a new <code>TestSASL</code> instance.
	 *
	 */
	public TestSASL() {
		super(new String[] { "jabber:client", "jabber:server", "jabber:component:accept" },
					new String[] { "auth-sasl" }, new String[] { "stream-open" },
					new String[] { "tls-init",
												 "user-register" });
	}

	//~--- methods --------------------------------------------------------------

	/**
	 * Method description
	 *
	 *
	 * @param reply
	 *
	 * @return
	 *
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Override
	public String nextElementName(final Element reply) throws Exception {
		if (reply == null) {
			response = "challenge";

			Set<String> mechs = (Set<String>) params.get("features");

			if (mechs == null) {
				data =
					"<auth xmlns='urn:ietf:params:xml:ns:xmpp-sasl' mechanism='CRAP-NO-MECHS-IN-FEATURES'/>";
			} else {
				if (mechs.contains("DIGEST-MD5")) {
					data =
						"<auth xmlns='urn:ietf:params:xml:ns:xmpp-sasl' mechanism='DIGEST-MD5'/>";
				} else {
					if (mechs.contains("PLAIN")) {
						data = "<auth xmlns='urn:ietf:params:xml:ns:xmpp-sasl' mechanism='PLAIN'>";
						data += Base64.encode(new String("\0" + user_name + "\0" +
																						 user_pass).getBytes());
						data     += "</auth>";
						response = "success";
					} else {
						data =
							"<auth xmlns='urn:ietf:params:xml:ns:xmpp-sasl' mechanism='CRAP-NO-KNOWN-MECHS'/>";
					}
				}
			}

			return "auth";
		}    // end of if (success)

		String rname = reply.getName();

		if (rname.equals("challenge")) {
			if (sasl == null) {
				Map<String, String> props = new TreeMap<String, String>();

				props.put(Sasl.QOP, "auth");
				sasl = Sasl.createSaslClient(new String[] { "DIGEST-MD5", "CRAM-MD5", "GSSAPI",
								"PLAIN" }, user_name,

				// null,
				"xmpp", hostname, props, new AuthCallbackHandler());
			}    // end of if (sasl == null)

			byte[] chall = Base64.decode(reply.getChildCDataStaticStr(CHALLENGE_PATH));

			// System.out.println("Challenge: " + new String(chall));
			byte[] resp = sasl.evaluateChallenge(chall);

			// System.out.println("Response: " + new String(resp));
			if (sasl.isComplete()) {
				data     = "<response xmlns='urn:ietf:params:xml:ns:xmpp-sasl'/>";
				response = "success";
			}    // end of if (sasl.isComplete())
							else {
				data = "<response xmlns='urn:ietf:params:xml:ns:xmpp-sasl'>" +
							 Base64.encode(resp) + "</response>";
				response = "challenge";
			}    // end of if (sasl.isComplete()) else

			// System.out.println("Expected: " + response);
			// System.out.println("Data: " + data);
			return "response";
		}    // end of if (rname.equals("challenge"))
		if (reply.getName().equals("success")) {
			params.put("authorized", true);
			data = "<stream:stream " + "xmlns='jabber:client' " +
						 "xmlns:stream='http://etherx.jabber.org/streams' " + "to='" + hostname +
						 "' " + "version='1.0'>";

			return "stream:stream";
		}    // end of if (reply.getName().equals("success"))

		return null;
	}

	//~--- get methods ----------------------------------------------------------

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
		if (element.equals("response")) {
			return new String[] { "success", "challenge" };
		}    // end of if (element.equals("response"))

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
	@Override
	public String getElementData(final String element) {
		return data;
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
		if (element.equals("stream:stream")) {
			if (bosh_mode) {
				return new String[] { "stream:features" };
			} else {
				return new String[] { "stream:stream", "stream:features" };
			}
		}

		return new String[] { response };
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
		if (element.equals("stream:stream")) {
			return new Attribute[] { new Attribute("xmlns", "jabber:client"),
															 new Attribute("xmlns:stream",
															 "http://etherx.jabber.org/streams"),
															 new Attribute("from", hostname),
															 new Attribute("version", "1.0") };
		}
		if (element.equals("stream:features")) {
			return new Attribute[] {};
		}

		return new Attribute[] { new Attribute("xmlns", "urn:ietf:params:xml:ns:xmpp-sasl") };
	}

	//~--- methods --------------------------------------------------------------

	// Implementation of TestIfc

	/**
	 * Describe <code>init</code> method here.
	 *
	 * @param map a <code>Map</code> value
	 * @param vars
	 */
	@Override
	public void init(final Params map, Map<String, String> vars) {
		super.init(map, vars);
		user_name = params.get("-user-name", user_name);
		user_pass = params.get("-user-pass", user_pass);
		user_resr = params.get("-user-resr", user_resr);
		hostname  = params.get("-host", hostname);
		bosh_mode = params.get("bosh-mode") != null;
	}

	//~--- inner classes --------------------------------------------------------

	private class AuthCallbackHandler
					implements CallbackHandler {
		/**
		 * Method description
		 *
		 *
		 * @param callbacks
		 *
		 * @throws IOException
		 * @throws UnsupportedCallbackException
		 */
		@Override
		public void handle(final Callback[] callbacks)
						throws IOException, UnsupportedCallbackException {
			String realm = null;
			String name  = null;

			for (int i = 0; i < callbacks.length; i++) {
				if (callbacks[i] instanceof RealmCallback) {
					((RealmCallback) callbacks[i]).setText(hostname);

					// System.out.println("RealmCallback: " + hostname);
				} else if (callbacks[i] instanceof NameCallback) {
					((NameCallback) callbacks[i]).setName(user_name);

					// System.out.println("NameCallback: " + user_name);
				} else if (callbacks[i] instanceof PasswordCallback) {
					((PasswordCallback) callbacks[i]).setPassword(user_pass.toCharArray());

					// System.out.println("PasswordCallback: " + user_pass);
				} else if (callbacks[i] instanceof AuthorizeCallback) {
					AuthorizeCallback authCallback = ((AuthorizeCallback) callbacks[i]);
					String authenId                = authCallback.getAuthenticationID();

					// System.out.println("AuthorizeCallback: authenId: " + authenId);
					String authorId = authCallback.getAuthorizationID();

					// System.out.println("AuthorizeCallback: authorId: " + authorId);
					if (authenId.equals(authorId)) {
						authCallback.setAuthorized(true);
						authCallback.setAuthorizedID(authorId);
					}    // end of if (authenId.equals(authorId))
				} else {
					throw new UnsupportedCallbackException(callbacks[i], "Unrecognized Callback");
				}
			}
		}
	}
}    // TestSASL


//~ Formatted in Tigase Code Convention on 13/02/16
