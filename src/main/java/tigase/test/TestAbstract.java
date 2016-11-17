/*
 * TestAbstract.java
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



package tigase.test;

//~--- non-JDK imports --------------------------------------------------------

import tigase.test.util.Params;
import tigase.test.util.TestUtil;
import tigase.test.util.XMLIO;

import tigase.xml.Element;

import static tigase.test.util.TestUtil.*;

//~--- JDK imports ------------------------------------------------------------

import java.net.SocketException;
import java.net.SocketTimeoutException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import javax.management.Attribute;

/**
 * Describe class TestAbstract here.
 *
 *
 * Created: Sun May 22 11:08:23 2005
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public abstract class TestAbstract
				extends TestEmpty {
	/** Field description */
	protected static final String[] CHALLENGE_PATH = { "challenge" };

	/** Field description */
	protected static final String COMMAND_EL_NAME = "command";

	/** Field description */
	protected static final String[] FIELD_VALUE_PATH = { "field", "value" };

	/** Field description */
	protected static final String IQ_EL_NAME = "iq";

	/** Field description */
	protected static final String QUERY_EL_NAME = "query";

	/** Field description */
	protected static final String[] IQ_QUERY_VERSION_PATH = { IQ_EL_NAME, QUERY_EL_NAME,
					"version" };

	/** Field description */
	protected static final String[] IQ_QUERY_PATH = { IQ_EL_NAME, QUERY_EL_NAME };

	/** Field description */
	protected static final String[] IQ_QUERY_OS_PATH = { IQ_EL_NAME, QUERY_EL_NAME, "os" };

	/** Field description */
	protected static final String[] IQ_QUERY_NAME_PATH = { IQ_EL_NAME, QUERY_EL_NAME,
					"name" };

	/** Field description */
	protected static final String[] IQ_COMMAND_X_PATH = { IQ_EL_NAME, COMMAND_EL_NAME,
																												"x" };

	/** Field description */
	protected static final String[] STREAM_MECHANISMS_PATH = { "stream:features",
					"mechanisms" };

	//~--- fields ---------------------------------------------------------------

	/** Field description */
	protected Exception exception = null;

	/** Field description */
	protected Params params = null;

	/** Field description */
	protected Element reply = null;

	/** Field description */
	protected Map<String, String> vars = null;
	private String error_message       = "";

	/** Field description */
	protected boolean timeoutOk = false;

	/** Field description */
	protected ResultCode resultCode = ResultCode.TEST_OK;

	/** Field description */
	protected boolean disconnectOk     = false;
	private boolean fullExceptionStack = false;

	//~--- constructors ---------------------------------------------------------

	/**
	 * Creates a new <code>TestAbstract</code> instance.
	 *
	 * @param base_xmlns
	 * @param implemented
	 * @param depends
	 * @param optional
	 */
	public TestAbstract(final String[] base_xmlns, final String[] implemented,
											final String[] depends, final String[] optional) {
		super(base_xmlns, implemented, depends, optional);
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
	public abstract String getElementData(final String element) throws Exception;

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
	public abstract Attribute[] getRespElementAttributes(final String element)
					throws Exception;

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
	public abstract String[] getRespElementNames(final String element) throws Exception;

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
	public abstract String[] getRespOptionalNames(final String element) throws Exception;

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
	public abstract String nextElementName(final Element reply) throws Exception;

	/**
	 * Method description
	 *
	 *
	 * @param data
	 * @param vars
	 * @param vals
	 *
	 * @return
	 */
	public static String substituteVars(final String data, final String[] vars,
					final String[] vals) {
		String result = data;

		for (int i = 0; i < vars.length; i++) {
			result = result.replace(vars[i], vals[i]);
		}    // end of for (int i = 0; i < vars.length; i++)

		return result;
	}

	//~--- get methods ----------------------------------------------------------

	/**
	 * Method description
	 *
	 *
	 * @return
	 */
	@Override
	public Element getLastResult() {
		return reply;
	}

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
	 * Describe <code>getResultMessage</code> method here.
	 *
	 * @return a <code>String</code> value
	 */
	@Override
	public String getResultMessage() {
		switch (resultCode) {
		case PROCESSING_EXCEPTION :
			if (fullExceptionStack) {
				return getClass().getName() + ", " + getUserPasswordResource() + ", " +
							 resultCode.getMessage() + exception.toString() + "\n" +
							 stack2String(exception);
			} else {
				return getClass().getName() + ", " + getUserPasswordResource() + ", " +
							 exception.getMessage();
			}
		default :
			return getClass().getName() + ", " + getUserPasswordResource() + ", " +
						 resultCode.getMessage() + ", " + error_message;
		}    // end of switch (resultCode)
	}

	/**
	 * Method description
	 *
	 *
	 * @param elem
	 * @param attrs
	 *
	 * @return
	 */
	public boolean hasAttributes(Element elem, Attribute[] attrs) {
		debug("Checking elem: " + elem + " for attributes: " + Arrays.toString(attrs) + "\n");
		if (attrs != null) {
			for (Attribute attr : attrs) {
				String val = elem.getAttributeStaticStr(attr.getName());

				if (val == null) {
					debug("Value null for attribute: " + attr.getName());

					return false;
				} else {
					if (!val.equals(attr.getValue())) {
						debug("Values are not equal for attribute: " + attr.getName() + ", elem: " +
									val + ", attr.getValue(): " + attr.getValue());

						return false;
					}    // end of if (!val.equals(attr.getValue()))
				}      // end of if (val == null) else
			}        // end of for ()
		}          // end of if (attrs != null)

		return true;
	}

	//~--- methods --------------------------------------------------------------

	/**
	 * Describe <code>init</code> method here.
	 *
	 * @param params a <code>Params</code> value
	 * @param vars
	 */
	@Override
	public void init(final Params params, Map<String, String> vars) {
		super.init(params, vars);
		this.params        = params;
		this.vars          = vars;
		timeoutOk          = params.containsKey("-time-out-ok");
		disconnectOk       = params.containsKey("-disconnect-ok");
		fullExceptionStack = params.containsKey("-full-stack-trace");

//  && !params.containsKey("-on-one-socket");
//    collectHistory = true;
	}

	// Implementation of tigase.test.TestIfc

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
			e.printStackTrace();
		}
	}

	/**
	 * Method description
	 *
	 *
	 * @param reply
	 *
	 * @throws Exception
	 */
	public void replyElement(final Element reply) throws Exception {}

	/**
	 * Describe <code>run</code> method here.
	 *
	 * @return a <code>boolean</code> value
	 */
	@Override
	public boolean run() {
		try {
			String elem = null;

			while ((elem = nextElementName(reply)) != null) {
				debug("Processing element: " + elem + "\n");

				XMLIO io = (XMLIO) params.get("socketxmlio");

				if (io == null) {
					resultCode = ResultCode.SOCKET_NOT_INITALIZED;

					return false;
				}        // end of if (sock == null)

				String data = getElementData(elem);

				if ((data != null) &&!data.equals("")) {
					debug("Element data: " + data + "\n");
					addOutput(data);
					io.write(data);
				}        // end of if (data != null && !data.equals(""))

				String[] responses     = getRespElementNames(elem);
				boolean[] resp_found   = new boolean[responses.length];
				String[] optional_resp = getRespOptionalNames(elem);

				Arrays.fill(resp_found, false);

				int index = 0;

				while (!resp_found[resp_found.length - 1]) {
					Queue<Element> results = io.read();
					Element rep            = null;

					while ((index < resp_found.length) && (rep = results.poll()) != null) {
						reply = rep;
						if (reply.getName().equals("stream:features")) {
							processStreamFeatures(reply);
						}
						replyElement(reply);
						debug("Received: " + reply.toString() + "\n");
						addInput(reply.toString());
						resp_found[index] = checkResponse(reply, responses[index], optional_resp);
						if (!resp_found[index++]) {
							resultCode = ResultCode.RESULT_DOESNT_MATCH;

							return false;
						}    // end of else
					}
					if (results.size() > 0) {
						reply = results.poll();
						if (reply.getName() != "stream:stream") {
							debug("Too many results: " + reply);
							resultCode = ResultCode.RESULT_DOESNT_MATCH;

							return false;
						}
					}      // end of if (index >= resp_found.length)
				}        // end of while (!resp_found[resp_found-1])
			}

			return true;
		} catch (SocketTimeoutException e) {
			if (timeoutOk) {
				return true;
			} else {

				// System.out.println(params.toString());
				resultCode = ResultCode.PROCESSING_EXCEPTION;
				exception  = e;
				addInput(getClass().getName() + ", " + getUserPasswordResource() + ", " +
								 e.getMessage());

				return false;
			}    // end of if (timeoutOk) else
		} catch (SocketException e) {
			if (e.getMessage().equals("Broken pipe") && disconnectOk) {
				return true;
			} else {
				resultCode = ResultCode.PROCESSING_EXCEPTION;
				exception  = e;
				addInput(getClass().getName() + ", " + getUserPasswordResource() + ", " +
								 e.getMessage());

				return false;
			}
		} catch (ResultsDontMatchException e) {
			resultCode = ResultCode.PROCESSING_EXCEPTION;
			exception  = e;
			addInput(getClass().getName() + ", " + getUserPasswordResource() + ", " +
							 e.getMessage());

			return false;
		} catch (Exception e) {
			addInput(getClass().getName() + ", " + getUserPasswordResource() + ", " + e +
							 "\n" + TestUtil.stack2String(e));
			resultCode = ResultCode.PROCESSING_EXCEPTION;
			exception  = e;
			System.out.println(Arrays.toString(implemented()));
			System.out.println(params.toString());

			// e.printStackTrace();
			return false;
		}    // end of try-catch
	}

	private boolean checkResponse(Element reply, String response, String[] optional_resp)
					throws Exception {
		debug("checking reply: " + reply + " with expected: " + response + "\n");
		if (reply.getName().equals(response) &&
				hasAttributes(reply, getRespElementAttributes(response))) {
			return true;
		} else {
			if (optional_resp != null) {
				for (String opt : optional_resp) {
					if (reply.getName().equals(opt) &&
							hasAttributes(reply, getRespElementAttributes(opt))) {
						return true;
					}
				}    // end of for ()
			}
		}        // end of else
		error_message = getClass().getName() + ", expected: '" + response +
										"', Received: '" + reply.toString() + "'";

		return false;
	}

	//~--- get methods ----------------------------------------------------------

//public ResultCode getResult() {
//  return resultCode;
//}
	private String getUserPasswordResource() {
		String user_name = params.get("-user-name", null);
		String hostname  = params.get("-host", null);
		String user_resr = params.get("-user-resr", null);
		String user_pass = params.get("-user-pass", null);
		String result    = "";

		if (user_name != null) {
			result += user_name + "@";
		}
		if (hostname != null) {
			result += hostname;
		}
		if (user_resr != null) {
			result += "/" + user_resr;
		}
		if (user_pass != null) {
			result += "(" + user_pass + ")";
		}

		return result;
	}

	//~--- methods --------------------------------------------------------------

	private void processStreamFeatures(Element features) {
		List<Element> children = features.getChildrenStaticStr(STREAM_MECHANISMS_PATH);

		if ((children != null) && (children.size() > 0)) {
			Set<String> mechs = new HashSet<String>();

			for (Element child : children) {
				mechs.add(child.getCData());
			}
			params.put("features", mechs);
		}
	}
}    // TestAbstract


//~ Formatted in Tigase Code Convention on 13/02/20
