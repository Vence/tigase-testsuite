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

package tigase.test.util;

import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import tigase.xml.Element;
import tigase.xml.ElementFactory;
import tigase.xml.SimpleHandler;

/**
 * <code>DomBuilderHandler</code> - implementation of
 *  <code>SimpleHandler</code> building <em>DOM</em> strctures during parsing
 *  time.
 *  It also supports creation multiple, sperate document trees if parsed
 *  buffer contains a few <em>XML</em> documents. As a result of work it returns
 *  always <code>Queue</code> containing all found <em>XML</em> trees in the
 *  same order as they were found in network data.<br/>
 *  Document trees created by this <em>DOM</em> builder consist of instances of
 *  <code>Element</code> class or instances of class extending
 *  <code>Element</code> class. To receive trees built with instances of proper
 *  class user must provide <code>ElementFactory</code> implementation creating
 *  instances of required <code>ELement</code> extension.
 *
 * <p>
 * Created: Sat Oct  2 22:01:34 2004
 * </p>
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */

public class DomBuilderHandler implements SimpleHandler {

  private static Logger log =
    Logger.getLogger("tigase.protocols.xmpp.DomBuilderHandler");

  private static final String ELEM_STREAM_STREAM = "stream:stream";
	private ElementFactory customFactory = null;

  private Object parserState = null;
  private String top_xmlns = null;
  private String def_xmlns = null;

  private LinkedList<Element> all_roots = new LinkedList<Element>();
  private Stack<Element> el_stack = new Stack<Element>();

  public DomBuilderHandler(ElementFactory factory) {
    customFactory = factory;
  }

  public Queue<Element> getParsedElements() {
    return all_roots;
  }

  public void error(String msg) {
    log.warning("XML content parse error.");
    log.warning(msg);
  }

  private Element newElement(String name, String cdata,
    StringBuilder[] attnames, StringBuilder[] attvals) {
    return customFactory.elementInstance(name, cdata, attnames, attvals);
  }

  public void startElement(StringBuilder name,
    StringBuilder[] attr_names, StringBuilder[] attr_values) {
    log.finest("Start element name: "+name);
    log.finest("Element attributes names: "+Arrays.toString(attr_names));
    log.finest("Element attributes values: "+Arrays.toString(attr_values));

    String tmp_name = name.toString();

    Element elem = newElement(tmp_name, null, attr_names, attr_values);
    String ns = elem.getXMLNS();
    if (ns == null) {
      elem.setDefXMLNS(def_xmlns);
    } // end of if (ns == null)
    else {
      def_xmlns = ns;
    } // end of if (ns == null) else
    el_stack.push(elem);
    if (tmp_name.equals(ELEM_STREAM_STREAM)) {
      top_xmlns = elem.getXMLNS();
      endElement(name);
    } // end of if (tmp_name.equals())
  }

	@Override
  public void elementCData(StringBuilder cdata) {
		if (log.isLoggable(Level.FINEST)) {
			log.finest("Element CDATA: "+cdata);
		}
		try {
			el_stack.peek().setCData(cdata.toString());
		} catch (EmptyStackException e) {
			// Do nothing here, it happens sometimes that client sends
			// some white characters after sending open stream data....
		}
  }

	@Override
  public boolean endElement(StringBuilder name) {
    log.finest("End element name: "+name);

	String tmp_name = name.toString();
	
    if (el_stack.isEmpty()) {
      el_stack.push(newElement(tmp_name, null, null, null));
    } // end of if (tmp_name.equals())

    Element elem = el_stack.pop();
	if (elem.getName() != tmp_name.intern())
		return false;
	
    if (el_stack.isEmpty()) {
      all_roots.offer(elem);
      def_xmlns = top_xmlns;
      log.finest("Adding new request: "+elem.toString());
    } // end of if (el_stack.isEmpty())
    else {
      el_stack.peek().addChild(elem);
    } // end of if (el_stack.isEmpty()) else
	return true;
  }

  public void otherXML(StringBuilder other) {
    log.finest("Other XML content: "+other);

    // Just ignore
  }

  public void saveParserState(Object state) {
    parserState = state;
  }

  public Object restoreParserState() {
    return parserState;
  }

}// DomBuilderHandler
