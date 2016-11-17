/*
 * TestCommon.java
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

import tigase.test.ResultCode;
import tigase.test.TestEmpty;
import tigase.test.util.ElementUtil;
import tigase.test.util.EqualError;
import tigase.test.util.Params;
import tigase.test.util.ScriptFileLoader;
import tigase.test.util.ScriptFileLoader.StanzaEntry;
import tigase.test.util.TestUtil;
import tigase.test.util.XMLIO;

import tigase.util.JIDUtils;

import tigase.xml.DomBuilderHandler;
import tigase.xml.Element;
import tigase.xml.SimpleParser;
import tigase.xml.SingletonFactory;

//~--- JDK imports ------------------------------------------------------------

import java.net.SocketTimeoutException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class TestCommon.java is responsible for
 *
 * <p>
 * Created: Mon Apr 23 08:47:37 2007
 * </p>
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version 1.0.0
 */
public class TestCommon
				extends TestEmpty {
	private static final Logger log          = Logger.getLogger(TestCommon.class.getName());
	private static final SimpleParser parser = SingletonFactory.getParserInstance();

	//~--- fields ---------------------------------------------------------------

	/** Field description */
	protected Exception exception = null;

	// private Element lastReceived = null;
//private long repeat_wait = 1;

	/** Field description */
	protected Params params = null;

	/** Field description */
	protected Map<String, String> vars      = null;
	private String cdata                    = "";
	private String error_message            = "";
	private String hostname                 = "localhost";
	private String id                       = null;
	private String jid                      = null;
	private long repeat                     = 0;
	private String source_file              = "tests/data/sample-file.cot";
	private Queue<StanzaEntry> stanzas_buff = null;
	private String to                       = "test_user@localhost";
	private String user_emil                = "test_user@localhost";
	private String user_name                = "test_user@localhost";
	private String user_resr                = "xmpp-test";
	private Pattern p                       = Pattern.compile(".*@\\{([^}]+)\\}.*");
	private boolean initial_presence        = false;
	private boolean fullExceptionStack      = false;
	private List<Element> all_results       = new ArrayList<Element>();

	/** Field description */
	protected boolean timeoutOk = false;

	/** Field description */
	protected ResultCode resultCode = ResultCode.TEST_OK;
	private ScriptFileLoader scriptFileLoader;

	//~--- constructors ---------------------------------------------------------

	/**
	 * Creates a new <code>TestCommon</code> instance.
	 *
	 */
	public TestCommon() {
		super(new String[] { "jabber:client" }, new String[] { "common" },
					new String[] { "stream-open",
												 "auth", "xmpp-bind", "presence" }, new String[] { "tls-init" });
	}

	/**
	 * Constructs ...
	 *
	 *
	 * @param ns
	 * @param imp
	 * @param depend
	 * @param opt
	 */
	public TestCommon(String[] ns, String[] imp, String[] depend, String[] opt) {
		super(ns, imp, depend, opt);
	}

	//~--- methods --------------------------------------------------------------

	private static Element[] parseXMLData(String data) {

		// Replace a few "variables"
		DomBuilderHandler domHandler = new DomBuilderHandler();

		parser.parse(domHandler, data.toCharArray(), 0, data.length());

		Queue<Element> elems = domHandler.getParsedElements();

		if ((elems != null) && (elems.size() > 0)) {
			return elems.toArray(new Element[elems.size()]);
		}

		return null;
	}

	//~--- get methods ----------------------------------------------------------

	/**
	 * Describe <code>getResultCode</code> method here.
	 *
	 * @return an <code>int</code> value
	 */
	@Override
	public ResultCode getResultCode() {
		return resultCode;
	}

	/**
	 * Method description
	 *
	 *
	 * @return
	 */
	@Override
	public String getResultMessage() {
		switch (resultCode) {
		case PROCESSING_EXCEPTION :
			if (fullExceptionStack) {
				return getClass().getName() + ", " + resultCode.getMessage() +
							 exception.toString() + "\n" + TestUtil.stack2String(exception) +
							 error_message;
			} else {
				return getClass().getName() + ", " + exception.getMessage() + error_message;
			}
		default :
			return resultCode.getMessage() + ", " + error_message;
		}    // end of switch (resultCode)
	}

	//~--- methods --------------------------------------------------------------

	/**
	 * Method description
	 *
	 *
	 * @param params
	 * @param vars
	 */
	@Override
	public void init(final Params params, Map<String, String> vars) {
		super.init(params, vars);
		this.params = params;
		this.vars   = vars;

		// System.out.println(params.toString());
		// System.out.println(vars.toString());
		if (stanzas_buff == null) {
			user_name        = params.get("-user-name", user_name);
			user_resr        = params.get("-user-resr", user_resr);
			user_emil        = params.get("-user-emil", user_emil);
			hostname         = params.get("-host", hostname);
			cdata            = params.get("-cdata", cdata);
			initial_presence = params.get("-initial-presence", initial_presence);

			String name = JIDUtils.getNodeNick(user_name);

			if ((name == null) || name.equals("")) {
				jid = user_name + "@" + hostname + "/" + user_resr;
				id  = user_name + "@" + hostname;
			} else {
				jid = user_name + "/" + user_resr;
				id  = user_name;
			}    // end of else
			to                 = params.get("-to-jid", to);
			timeoutOk          = params.containsKey("-time-out-ok");
			fullExceptionStack = params.containsKey("-full-stack-trace");
			source_file        = params.get("-source-file", source_file);
			stanzas_buff       = new LinkedList<StanzaEntry>();

			String number                = params.get("-number", "");
			Map<String, String> replaces = new HashMap<String, String>();

			replaces.put("$(from-jid)", jid);
			replaces.put("$(from-id)", id);
			replaces.put("$(to-jid)", to);
			replaces.put("$(to-id)", JIDUtils.getNodeID(to));
			replaces.put("$(to-hostname)", JIDUtils.getNodeHost(to));
			replaces.put("$(hostname)", hostname);
			replaces.put("$(number)", number);
			replaces.put("$(cdata)", cdata);
			this.scriptFileLoader = new ScriptFileLoader(source_file, stanzas_buff, replaces);
			this.scriptFileLoader.loadSourceFile();

			// loadSourceFile(source_file);
			// repeat_max = params.get("-repeat-script", repeat_max);
			// repeat_wait = params.get("-repeat-wait", repeat_wait);
		}
	}

	/**
	 * Method description
	 *
	 */
	@Override
	public void release() {
		try {
			XMLIO io = (XMLIO) params.get("socketxmlio");

			io.close();
		} catch (Exception e) {

			// e.printStackTrace();
		}
	}

	/**
	 * Describe <code>run</code> method here.
	 *
	 * @return a <code>boolean</code> value
	 */
	@Override
	public boolean run() {

//  for (int repeat = 0; repeat < repeat_max; repeat++) {
		try {
			if ((repeat == 0) && initial_presence) {
				XMLIO io       = (XMLIO) params.get("socketxmlio");
				String toWrite = "<presence/>";

				debug("\nSending: " + toWrite);
				addOutput(toWrite);
				io.write(toWrite);
			}
			++repeat;

			Queue<StanzaEntry> stanzas = new LinkedList<StanzaEntry>(stanzas_buff);
			StanzaEntry entry          = null;

			while ((entry = stanzas.poll()) != null) {
				XMLIO io = (XMLIO) params.get("socketxmlio");

				if (io == null) {
					resultCode = ResultCode.SOCKET_NOT_INITALIZED;

					return false;
				}      // end of if (sock == null)
				switch (entry.getAction()) {
				case send :
					for (Element elem : entry.getStanza()) {
						if (elem.getAttributeStaticStr("id") == null) {
							elem.setAttribute("id", "" + repeat);
						}

						String toWrite = applyParams(elem.toString());

						debug("\nSending: " + toWrite);
						addOutput(toWrite);
						io.write(toWrite);
					}    // end of for (Element elem: stanza)

					break;

				case expect :
					boolean found          = false;
					Queue<Element> results = null;

					error_message = "\n" + repeat + ": Expected: " +
													Arrays.toString(entry.getStanza());
					while ((all_results.size() == 0) &&
								 ((results == null) || (results.size() == 0))) {
						results = io.read();
					}    // end of while (!found)
					if (results != null) {
						for (Element el : results) {
							debug("\nReceived: " + el.toString());
							addInput(el.toString());
						}
						all_results.addAll(results);
						results.clear();
					}

					String eq_msg = "";

					for (int exp = 0; (exp < entry.getStanza().length) &&!found; ++exp) {
						for (int idx = 0; idx < all_results.size(); idx++) {
							Element lastReceived = all_results.get(idx);
							EqualError res       = ElementUtil.equalElemsDeep(entry.getStanza()[exp],
																			 lastReceived, stanza_variables);

							found  = res.equals;
							eq_msg += (found
												 ? ""
												 : res.message + "\n");
							if (found) {

								// System.out.println("FOUND: " + received.toString());
								all_results.remove(idx);

								break;
							}
						}
					}    // end of for (Element expected: entry.stanza)
					if (!found) {

						// System.out.println("\nFound: " + found + ", message: " + eq_msg);
						resultCode    = ResultCode.RESULT_DOESNT_MATCH;
						error_message = "\n" + repeat + ": Expected one of: " +
														Arrays.toString(entry.getStanza()) + ", received: " +
														Arrays.toString(all_results.toArray(new Element[0])) +
														"\n equals error message: " + eq_msg;

						return false;
					}    // end of if (!found)

					break;

				default :
					break;
				}      // end of switch (entry.action)
			}        // end of while ((entry = stanzas.poll()) != null)
		} catch (SocketTimeoutException e) {
			if (timeoutOk) {
				return true;
			} else {
				resultCode = ResultCode.PROCESSING_EXCEPTION;
				exception  = e;
				addInput("" + repeat + ": " + getClass().getName() + ", " + e.getMessage() +
								 error_message);

				return false;
			}    // end of if (timeoutOk) else
		} catch (Exception e) {
			addInput("" + repeat + ": " + getClass().getName() + ", " + e + "\n" +
							 TestUtil.stack2String(e) + error_message);
			resultCode = ResultCode.PROCESSING_EXCEPTION;
			exception  = e;
			e.printStackTrace();

			return false;
		}    // end of catch

//  try { Thread.sleep(repeat_wait);
//  } catch (InterruptedException e) { } // end of try-catch
//    }
		return true;
	}

	private String applyParams(String input) {
		String result = input;

		for (String key : vars.keySet()) {

			// System.out.println("key: " + key);
			while (result.contains(key)) {
				String newVal = vars.get(key);

				if ((newVal != null) && newVal.startsWith("\"") && newVal.endsWith("\"")) {
					newVal = newVal.substring(1, newVal.length() - 1);
				}

				// System.out.println("Replacing with: " + newVal);
				result = result.replace(key, newVal);
			}    // end of while (result.contains(key))
		}      // end of for ()

		// System.out.println("stanza_variables: " + stanza_variables);
		for (String key : stanza_variables.keySet()) {

			// System.out.println("key: " + key);
			while (result.contains(key)) {

				// System.out.println("result: " + result + ", contains key: " + key);
				String newVal = stanza_variables.get(key);

				if ((newVal != null) && newVal.startsWith("\"") && newVal.endsWith("\"")) {
					newVal = newVal.substring(1, newVal.length() - 1);
				}
				if (newVal != null) {

					// System.out.println("Replacing with: " + newVal);
					result = result.replace(key, newVal);
				} else {
					break;
				}
			}    // end of while (result.contains(key))
		}      // end of for ()

		// if (lastReceived == null) {
//  return result;
//    }
//
//    // System.out.println("lastReceived: " + lastReceived.toString());
//    Map<String, String> attrs = lastReceived.getAttributes();
//
//    // System.out.println("Attributes: " + attrs.toString());
//    Matcher m = p.matcher(result);
//
//    while (m.matches()) {
//  String att = m.group(1);
//  String val = attrs.get(att);
//
//  // System.out.println("Found attribute to replace: " + att + ", value: " + val);
//  result = result.replaceAll("@\\{" + att + "\\}", val);
//
//  // System.out.println("Replaced string: " + result);
//  m = p.matcher(result);
//    }
		return result;
	}
}    // TestCommon


//~ Formatted in Tigase Code Convention on 13/02/20
