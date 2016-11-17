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
 * $Rev: $
 * Last modified by $Author: $
 * $Date: $
 */
package tigase.test.junit;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import tigase.test.util.XMLIO;
import tigase.xml.Element;

public abstract class JUnitXMLIO implements XMLIO {

	private Queue<Element> outQueue = new LinkedList<Element>();

	@Override
	public Queue<Element> read() throws IOException {
		return this.outQueue;
	}

	protected void send(Collection<Element> elements) {
		this.outQueue.addAll(elements);
	}

	protected void send(Element element) {
		this.outQueue.add(element);
	}

	public abstract void write(Element data) throws IOException;

	@Override
	public void write(String data) throws IOException {
		throw new RuntimeException("Please use write(Element data)");
	}

}