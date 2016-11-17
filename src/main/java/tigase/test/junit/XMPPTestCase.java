/*
 * XMPPTestCase.java
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



package tigase.test.junit;

//~--- non-JDK imports --------------------------------------------------------

import tigase.test.util.EqualError;
import tigase.test.util.ScriptFileLoader;
import tigase.test.util.ScriptFileLoader.Action;
import tigase.test.util.ScriptFileLoader.StanzaEntry;

import tigase.xml.Element;

import static org.junit.Assert.fail;

//~--- JDK imports ------------------------------------------------------------

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Set;

import javax.management.Attribute;

/**
 * Class description
 *
 *
 * @version        Enter version here..., 13/02/20
 * @author         Enter your name here...
 */
public abstract class XMPPTestCase {
	private String configFile;

	//~--- constructors ---------------------------------------------------------

	/**
	 * Constructs ...
	 *
	 */
	public XMPPTestCase() {
		this.configFile = null;
	}

	/**
	 *
	 * @param sourceFile
	 *            file with test script
	 */
	public XMPPTestCase(String sourceFile) {
		this.configFile = sourceFile;
	}

	//~--- methods --------------------------------------------------------------

	/**
	 * Method description
	 *
	 *
	 * @param el1
	 * @param el2
	 *
	 * @return
	 */
	public static EqualError equalElems(Element el1, Element el2) {
		if (!el1.getName().equals(el2.getName())) {
			return new EqualError(false,
														"Element names are different: " + el1.getName() + " and " +
														el2.getName());
		}    // end of if (!el1.getName().equals(el2.getName()))

		Map<String, String> attrs = el1.getAttributes();

		if (attrs != null) {
			for (String key : attrs.keySet()) {
				String atval2 = el2.getAttributeStaticStr(key);

				if (atval2 == null) {
					return new EqualError(false,
																"Element: " + el2.getName() + " missing attribute: " +
																key);
				}    // end of if (at2 == null)
				if (!(attrs.get(key).equals("*") || atval2.equals("*")) &&
						!attrs.get(key).equals(atval2)) {
					return new EqualError(false,
																"Element: " + el2.getName() +
																" different value for attribute: " + key + ", " +
																attrs.get(key) + " != " + atval2);
				}    // end of if (!attrs.get(key).equals(atval2))
			}      // end of for (String key: attrs.keySet())
		}        // end of if (attrs != null)

		String cdata1 = el1.getCData();

		if ((cdata1 != null) &&!cdata1.trim().isEmpty()) {
			String cdata2 = el2.getCData();

			if (cdata2 == null) {
				return new EqualError(false,
															"Missing CData for element: " + el2.getName() +
															", expected: " + cdata1);
			}    // end of if (cdata2 == null)
			if (!cdata1.trim().equals(cdata2.trim())) {
				return new EqualError(false,
															"Different CData for element: " + el2.getName() +
															", expected: " + cdata1 + ", found: " + cdata2);
			}    // end of if (!cdata1.equals(cdata2))
		}      // end of if (cdata1 != null)

		return new EqualError(true, null);
	}

	/**
	 * Method description
	 *
	 *
	 * @param el1
	 * @param el2
	 *
	 * @return
	 */
	public static EqualError equalElemsDeep(Element el1, Element el2) {
		EqualError result = equalElems(el1, el2);

		if (!result.equals) {
			return result;
		}    // end of if (!res)

		List<Element> children = el1.getChildren();

		if (children == null) {
			return result;
		}    // end of if (children == null)
		for (Element child1 : children) {
			List<Element> children2 = el2.getChildren();

			if ((children2 == null) || (children2.size() == 0)) {
				return new EqualError(false, "Missing children for element: " + el1.getName());
			}    // end of if (child2 == null)

			boolean found_child = false;

			for (Element child2 : children2) {
				found_child |= equalElemsDeep(child1, child2).equals;
			}    // end of for (Element child2: children2)
			if (!found_child) {
				return new EqualError(false,
															"Element: " + el2.getName() + ", missing child: " +
															child1.toString());
			}    // end of if (!res)
		}      // end of for (Element child: children)

		return result;
	}

	private static boolean equalsAll(Element[] expect, Set<String> params,
																	 Collection<Element> data) {
		if (((expect == null) || (expect.length == 0)) &&
				((data == null) || (data.size() == 0))) {
			return true;
		} else if ((expect == null) || (data == null)) {
			return false;
		} else {
			boolean result = true;

			for (Element e : expect) {
				boolean found = false;

				for (Element d : data) {
					EqualError error = equalElemsDeep(e, d);
					boolean tmpRes   = error.equals;

					if (tmpRes && params.contains("equals")) {
						EqualError error1 = equalElemsDeep(d, e);

						tmpRes &= error1.equals;
					}
					found |= tmpRes;
				}
				result &= found;
				if (!found) {
					System.out.println("EQ :: not found  " + e);
				}
			}

			return result;
		}
	}

	private static EqualError equalsOneOf(Element[] expect, Set<String> params,
					Collection<Element> data) {
		EqualError rs = null;

		if ((expect == null) && (data == null)) {
			return new EqualError(false, "Null expected");
		} else if ((expect == null) || (data == null)) {
			return new EqualError(false, "Result or expect data is null");
		} else {
			boolean result = false;

			for (Element d : data) {
				for (Element e : expect) {
					EqualError error = equalElemsDeep(e, d);
					boolean tmpRes   = error.equals;

					if (tmpRes && params.contains("equals")) {
						EqualError error1 = equalElemsDeep(d, e);

						tmpRes &= error1.equals;
					}
					result |= tmpRes;
					if (!tmpRes && (rs == null)) {
						rs = error;
					}
				}
			}

			return result
						 ? null
						 : rs;
		}
	}

	private static boolean equalsStrict(Element[] expect, Set<String> params,
					Collection<Element> data) {
		if (((expect == null) || (expect.length == 0)) &&
				((data == null) || (data.size() == 0))) {
			return true;
		} else if ((expect == null) || (data == null)) {
			return false;
		} else if (expect.length != data.size()) {
			return false;
		} else {
			boolean result          = true;
			Iterator<Element> dataI = data.iterator();
			int c                   = 0;

			while (dataI.hasNext()) {
				Element e        = expect[c++];
				Element d        = dataI.next();
				EqualError error = equalElemsDeep(e, d);

				if (params.contains("equals")) {
					EqualError error1 = equalElemsDeep(d, e);

					result &= error1.equals;
				}
				result &= error.equals;
			}

			return result;
		}
	}

	//~--- get methods ----------------------------------------------------------

	/**
	 * Method description
	 *
	 *
	 * @param elem
	 * @param attrs
	 *
	 * @return
	 */
	public static boolean hasAttributes(Element elem, Attribute[] attrs) {
		for (Attribute attr : attrs) {
			String val = elem.getAttributeStaticStr(attr.getName());

			if (val == null) {
				return false;
			}      // end of if (val == null)
							else {
				if (!val.equals(attr.getValue())) {
					return false;
				}    // end of if (!val.equals(attr.getValue()))
			}      // end of if (val == null) else
		}        // end of for ()

		return true;
	}

	/**
	 * Method description
	 *
	 *
	 * @param entries
	 *
	 * @return
	 */
	public static boolean isScriptValid(Collection<StanzaEntry> entries) {
		ScriptFileLoader.Action wantKind = ScriptFileLoader.Action.send;

		for (StanzaEntry scriptEntry : entries) {
			if ((wantKind != null) && (scriptEntry.getAction() != wantKind)) {
				throw new RuntimeException("Invalid script for JUnit test! Expected " + wantKind);
			} else if ((wantKind == null) && (scriptEntry.getAction() == Action.send)) {
				throw new RuntimeException(
						"Invalid script for JUnit test! Expected not send element!");
			}
			if ((scriptEntry.getAction() == Action.send) &&
					((scriptEntry.getStanza() == null) || (scriptEntry.getStanza().length != 1))) {
				throw new RuntimeException(
						"Invalid script for JUnit test! send may have only one element!");
			}
			switch (scriptEntry.getAction()) {
			case send :
				wantKind = null;

				break;

			default :
				wantKind = Action.send;

				break;
			}
		}

		return true;
	}

	//~--- methods --------------------------------------------------------------

	/**
	 * Method description
	 *
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		Pattern pattern = Pattern.compile("(^(.+)\\((.*)\\):.*)|(^(.+):.*)");
		Matcher m       = pattern.matcher("send:{");
		boolean b       = m.matches();

		if (b) {
			for (int i = 0; i <= m.groupCount(); i++) {
				System.out.println(i + ": " + m.group(i));
			}
		}

		String keyword = (m.group(2) == null)
										 ? m.group(5)
										 : m.group(2);
		String param   = (m.group(3) == null)
										 ? null
										 : m.group(3);

		System.out.println(keyword + "(" + param + ")");
	}

	/**
	 * Method description
	 *
	 *
	 * @param configFile
	 * @param xmlio
	 */
	public static void test(final String configFile, final JUnitXMLIO xmlio) {
		test(configFile, xmlio, null);
	}

	/**
	 * Method run test.
	 *
	 *
	 * @param configFile
	 * @param xmlio
	 * @param wait
	 */
	public static void test(final String configFile, final JUnitXMLIO xmlio, Long wait) {
		try {
			if (configFile == null) {
				throw new RuntimeException("Script file must be specified.");
			}
			System.out.println("Loading script " + configFile);

			Queue<StanzaEntry> script         = new LinkedList<StanzaEntry>();
			ScriptFileLoader scriptFileLoader = new ScriptFileLoader(configFile, script, null);

			scriptFileLoader.loadSourceFile();
			for (StanzaEntry scriptEntry : script) {
				Boolean ok = null;

				switch (scriptEntry.getAction()) {
				case send :
					Element stanza = scriptEntry.getStanza()[0];

					xmlio.read().clear();
					System.out.print("Sending stanza" + ((scriptEntry.getDescription() == null)
									? " <" + stanza.getName() +
										((stanza.getAttributeStaticStr("id") == null)
										 ? ""
										 : " id='" + stanza.getAttributeStaticStr("id") + "'") + ">"
									: " (" + scriptEntry.getDescription() + ")") + "...  ");
					xmlio.write(stanza);
					if (wait != null) {
						Thread.sleep(wait);
					}

					break;

				case expect : {
					Queue<Element> read = xmlio.read();

					System.out.print(" checking response... (" + read.size() + "/" +
													 scriptEntry.getStanza().length + ") ");
					if (scriptEntry.getParams().contains("stop")) {
						System.out.println("!!!");
					}

					EqualError ee = equalsOneOf(scriptEntry.getStanza(), scriptEntry.getParams(),
																			read);

					System.out.println((ee == null)
														 ? "OK"
														 : "FAIL");
					if (ee != null) {
						String error_message = ((scriptEntry.getDescription() != null)
																		? (scriptEntry.getDescription() + " :: ")
																		: "") + "expected:\n" +
																						Arrays.toString(scriptEntry.getStanza()) +
																						"\nreceived: " +
																						Arrays.toString(
																							xmlio.read().toArray(
																								new Element[0])) + "\nDescription: " +
																									ee.message;

						fail(error_message);
					}

					break;
				}

				case expect_all : {
					Queue<Element> read = xmlio.read();

					System.out.print(" checking response... (" + read.size() + "/" +
													 scriptEntry.getStanza().length + ") ");
					ok = equalsAll(scriptEntry.getStanza(), scriptEntry.getParams(), read);
					System.out.println(ok
														 ? "OK"
														 : "FAIL");
					if (!ok) {
						String error_message = "Expected all of: " +
																	 Arrays.toString(scriptEntry.getStanza()) +
																	 ", received: " +
																	 Arrays.toString(xmlio.read().toArray(new Element[0]));

						fail(error_message);
					}

					break;
				}

				case expect_strict : {
					Queue<Element> read = xmlio.read();

					System.out.print(" checking response... (" + read.size() + "/" +
													 ((scriptEntry.getStanza() == null)
														? 0
														: scriptEntry.getStanza().length) + ") ");
					ok = equalsStrict(scriptEntry.getStanza(), scriptEntry.getParams(), read);
					System.out.println(ok
														 ? "OK"
														 : "FAIL");
					if (!ok) {
						String error_message = "Expected sequence: " +
																	 Arrays.toString(scriptEntry.getStanza()) +
																	 ", received: " +
																	 Arrays.toString(xmlio.read().toArray(new Element[0]));

						fail(error_message);
					}

					break;
				}
				}
				if ((ok != null) &&!ok) {
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	/**
	 * Method description
	 *
	 *
	 * @param xmlio
	 */
	public void test(final JUnitXMLIO xmlio) {
		test(xmlio, null);
	}

	/**
	 * Method description
	 *
	 *
	 * @param xmlio
	 * @param wait
	 */
	public void test(final JUnitXMLIO xmlio, Long wait) {
		test(this.configFile, xmlio, wait);
	}
}


//~ Formatted in Tigase Code Convention on 13/02/20
