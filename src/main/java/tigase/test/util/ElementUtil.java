/*
 * ElementUtil.java
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



package tigase.test.util;

//~--- non-JDK imports --------------------------------------------------------

import tigase.xml.Element;

//~--- JDK imports ------------------------------------------------------------

import java.util.List;
import java.util.Map;

import javax.management.Attribute;

/**
 * Describe class ElementUtil here.
 *
 *
 * Created: Wed May 18 16:45:12 2005
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public abstract class ElementUtil {
	/**
	 * Creates a new <code>ElementUtil</code> instance.
	 *
	 */
	private ElementUtil() {}

	//~--- methods --------------------------------------------------------------

	/**
	 * Method description
	 *
	 *
	 * @param expected
	 * @param received
	 * @param stanza_variables
	 *
	 * @return
	 */
	public static boolean checkTestVariable(String expected, String received,
					Map<String, String> stanza_variables) {

		// Maybe this is a test variable?
		if (expected.startsWith("@{")) {

			// System.out.println("Found a test variable: " + expected);
			// Yes, this is a test variable
			// Do we have such a variable already?
			String stanza_var = stanza_variables.get(expected);

			if (stanza_var == null) {

				// System.out.println("Test variable does not exist yet, adding: (" + expected + ", "
				// + received + ")");
				// No, it is not there yet, so we add a new one
				stanza_variables.put(expected, received);
			} else {

				// System.out.println("Test variable exist already, comparing: " + test_var + " and "
				// + received);
				// The variable is already set, then we have to check whether the
				// received value matches it.
				if (!stanza_var.equals(received)) {
					return false;
				}
			}

			return true;
		}

		return false;
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
				if (!attrs.get(key).equals(atval2)) {
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
	 * @param stanza_variables
	 *
	 * @return
	 */
	public static EqualError equalElems(Element el1, Element el2,
					Map<String, String> stanza_variables) {
		if (!el1.getName().equals(el2.getName())) {
			return new EqualError(false,
														"Element names are different: " + el1.getName() + " and " +
														el2.getName());
		}    // end of if (!el1.getName().equals(el2.getName()))

		Map<String, String> attrs1 = el1.getAttributes();

		if (attrs1 != null) {
			for (String key1 : attrs1.keySet()) {
				String atval2 = el2.getAttributeStaticStr(key1);

				if (atval2 == null) {
					return new EqualError(false,
																"Element: " + el2.getName() + " missing attribute: " +
																key1);
				}    // end of if (at2 == null)

				String atval1 = attrs1.get(key1);

				if (!atval1.equals(atval2) &&
						!checkTestVariable(atval1, atval2, stanza_variables)) {
					return new EqualError(false,
																"Element: " + el2.getName() +
																" different value for attribute: " + key1 + ", " +
																attrs1.get(key1) + " != " + atval2);
				}    // end of if (!attrs.get(key).equals(atval2))
			}      // end of for (String key: attrs.keySet())
		}        // end of if (attrs != null)

		String cdata1 = el1.getCData();

		if (cdata1 != null) {
			cdata1 = cdata1.trim();
		}
		if ((cdata1 != null) &&!cdata1.isEmpty()) {
			String cdata2 = el2.getCData();

			if (cdata2 == null) {
				return new EqualError(false,
															"Missing CData for element: " + el2.getName() +
															", expected: " + cdata1);
			}    // end of if (cdata2 == null)
			cdata2 = cdata2.trim();
			if (!cdata1.equals(cdata2) &&!checkTestVariable(cdata1, cdata2, stanza_variables)) {
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

	/**
	 * Method description
	 *
	 *
	 * @param el1
	 * @param el2
	 * @param stanza_variables
	 *
	 * @return
	 */
	public static EqualError equalElemsDeep(Element el1, Element el2,
					Map<String, String> stanza_variables) {
		EqualError result = equalElems(el1, el2, stanza_variables);

		if (!result.equals) {
			return result;
		}    // end of if (!res)

		List<Element> children1 = el1.getChildren();

		if (children1 == null) {
			return result;
		}    // end of if (children == null)
		for (Element child1 : children1) {
			List<Element> children2 = el2.getChildren();

			if ((children2 == null) || (children2.size() == 0)) {
				return new EqualError(false, "Missing children for element: " + el1.getName());
			}    // end of if (child2 == null)

			boolean found_child = false;

			for (Element child2 : children2) {
				found_child |= equalElemsDeep(child1, child2, stanza_variables).equals;
			}    // end of for (Element child2: children2)
			if (!found_child) {
				return new EqualError(false,
															"Element: " + el2.getName() + ", missing child: " +
															child1.toString());
			}    // end of if (!res)
		}      // end of for (Element child: children)

		return result;
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
	 * @param elem
	 * @param attrs
	 * @param test_vars
	 *
	 * @return
	 */
	public static boolean hasAttributes(Element elem, Attribute[] attrs,
					Map<String, String> test_vars) {
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
}    // ElementUtil


//~ Formatted in Tigase Code Convention on 13/02/20
