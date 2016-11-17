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

package tigase.test;

//~--- non-JDK imports --------------------------------------------------------

import tigase.test.util.Params;

import tigase.xml.Element;

//~--- JDK imports ------------------------------------------------------------

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//~--- classes ----------------------------------------------------------------

/**
 * Describe class TestEmpty here.
 *
 *
 * Created: Mon Apr 23 17:25:02 2007
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public abstract class TestEmpty implements TestIfc {

	/**
	 * Stanza variables are gathered from stanzas received from the server.
	 * In the 'expect' statement you mark location of such data with @{var-name}
	 * it is stored then in this Map and all occurrences of @{var-name} in
	 * 'send' and subsequent 'expect' stanzas gets replaced with information
	 * received from the server.
	 */
	protected static final Map<String, String> stanza_variables = new ConcurrentHashMap<String,
		String>(10, 0.25f, 2);

	//~--- fields ---------------------------------------------------------------

	private String[] BASE_XMLNS = null;
	private String[] DEPENDS = null;
	private String[] IMPLEMENTED = null;
	private String[] OPTIONAL = null;

	// private List<HistoryEntry> history = null;
	private boolean collectHistory = true;
	private boolean deb = false;
	private HistoryCollectorIfc historyColl = null;
	private String testName = null;

	//~--- constructors ---------------------------------------------------------

	/**
	 * Creates a new <code>TestEmpty</code> instance.
	 *
	 *
	 * @param base_xmlns
	 * @param implemented
	 * @param depends
	 * @param optional
	 */
	public TestEmpty(final String[] base_xmlns, final String[] implemented,
			final String[] depends, final String[] optional) {
		BASE_XMLNS = base_xmlns;
		IMPLEMENTED = implemented;
		DEPENDS = depends;
		OPTIONAL = optional;
	}

	//~--- methods --------------------------------------------------------------

	/**
	 * Describe <code>getHistory</code> method here.
	 *
	 *
	 * @param input
	 */

//@Override
//public List<HistoryEntry> getHistory() {
//  return history;
//}
	public void addInput(String input) {
		if (collectHistory) {

			// System.out.println("Adding input!!");
			// history.add(new HistoryEntry(Direction.INPUT, input, getName()));
			historyColl.handleHistoryEntry(new HistoryEntry(Direction.INPUT, input, testName));
		}
	}

	/**
	 * Method description
	 *
	 *
	 * @param output
	 */
	public void addOutput(String output) {
		if (collectHistory) {

			// System.out.println("Adding output!!");
			// history.add(new HistoryEntry(Direction.OUTPUT, output, testName));
			historyColl.handleHistoryEntry(new HistoryEntry(Direction.OUTPUT, output, testName));
		}
	}

	/**
	 * Describe <code>baseXMLNS</code> method here.
	 *
	 * @return a <code>String[]</code> value
	 */
	@Override
	public String[] baseXMLNS() {
		return BASE_XMLNS;
	}

	/**
	 * Method description
	 *
	 *
	 * @param msg
	 */
	public void debug(String msg) {
		if (deb) {
			System.out.print(msg);
			System.out.flush();
		}    // end of if (debug)
	}

	/**
	 * Describe <code>depends</code> method here.
	 *
	 * @return a <code>String[]</code> value
	 */
	@Override
	public String[] depends() {
		return DEPENDS;
	}

	//~--- get methods ----------------------------------------------------------

	/**
	 * Describe <code>getLastResult</code> method here.
	 *
	 * @return an <code>Element</code> value
	 */
	@Override
	public Element getLastResult() {
		return null;
	}

	/**
	 * Method description
	 *
	 *
	 * @return
	 */
	@Override
	public String getName() {
		return testName;
	}

	/**
	 * Describe <code>getResultCode</code> method here.
	 *
	 * @return an <code>int</code> value
	 */
	@Override
	public ResultCode getResultCode() {
		return ResultCode.TEST_OK;
	}

	/**
	 * Describe <code>getResultMessage</code> method here.
	 *
	 * @return a <code>String</code> value
	 */
	@Override
	public String getResultMessage() {
		return null;
	}

	//~--- methods --------------------------------------------------------------

	/**
	 * Describe <code>implemented</code> method here.
	 *
	 * @return a <code>String[]</code> value
	 */
	@Override
	public String[] implemented() {
		return IMPLEMENTED;
	}

	/**
	 * Describe <code>init</code> method here.
	 *
	 * @param params a <code>Params</code> value
	 * @param vars
	 */
	@Override
	public void init(final Params params, Map<String, String> vars) {
		collectHistory = params.get("-output-history", true)
				&&!(params.containsKey("-daemon") || params.containsKey("-background"));

//  if (collectHistory) {
//    history = new LinkedList<HistoryEntry>();
//  } // end of if (collectHistory)
		deb = params.containsKey("-debug");

		// We can reset stanza variables here:
		String reset_stanza_vars = (String) params.get("-reset-stanza-vars");

		if (reset_stanza_vars != null) {
			String[] stanza_vars = reset_stanza_vars.split(",");

			for (String var : stanza_vars) {
				stanza_variables.remove(var);
			}
		}

		// Let's check the stanza variables and apply all of them wherever needed
		for (Map.Entry<String, Object> param : params.entrySet()) {
			for (Map.Entry<String, String> stanza_var : stanza_variables.entrySet()) {
				if ((param.getValue() != null)
						&& param.getValue().toString().contains(stanza_var.getKey())) {
					String new_param_val = param.getValue().toString().replace(stanza_var.getKey(),
						stanza_var.getValue());

					param.setValue(new_param_val);
				}
			}
		}
	}

	/**
	 * Describe <code>optional</code> method here.
	 *
	 * @return a <code>String[]</code> value
	 */
	@Override
	public String[] optional() {
		return OPTIONAL;
	}

	// Implementation of tigase.test.TestIfc

	/**
	 * Describe <code>run</code> method here.
	 *
	 * @return a <code>boolean</code> value
	 */
	@Override
	public boolean run() {
		return false;
	}

	//~--- set methods ----------------------------------------------------------

	/**
	 * Method description
	 *
	 *
	 * @param histColl
	 */
	public void setHistoryCollector(HistoryCollectorIfc histColl) {
		this.historyColl = histColl;
	}

	/**
	 * Method description
	 *
	 *
	 * @param testName
	 */
	@Override
	public void setName(String testName) {
		this.testName = testName;
	}
}    // TestEmpty


//~ Formatted in Sun Code Convention


//~ Formatted by Jindent --- http://www.jindent.com
