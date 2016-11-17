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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tigase.xml.DomBuilderHandler;
import tigase.xml.Element;
import tigase.xml.SimpleParser;
import tigase.xml.SingletonFactory;

public class ScriptFileLoader {

	public static enum Action {
		expect, expect_all, expect_strict, send;
	}

	private enum ParserState {
		expect_stanza, send_stanza, start,expect_all_stanza,expect_strict_stanza;
	}

	public static class StanzaEntry {

		private Action action = null;
		private Element[] stanza = null;
		private String description;
		private final Set<String> params;

		public StanzaEntry(Action action, Element[] stanza, String description) {
			this.action = action;
			this.stanza = stanza;
			this.description = description;

			HashSet<String> ppp = new HashSet<String>();
			if (description != null) {
				String[] k = description.split(" ");
				if (k != null)
					for (String string : k) {
						if (string != null && string.trim().startsWith("#") && string.length() > 1)
							ppp.add(string.substring(1));
					}
			}
			this.params = Collections.unmodifiableSet(ppp);
		}

		public Action getAction() {
			return action;
		}

		public Element[] getStanza() {
			return stanza;
		}

		public String getDescription() {
			return description;
		}

		public Set<String> getParams() {
			return params;
		}

	}

	private static final SimpleParser parser = SingletonFactory.getParserInstance();

	private final String file;

	private final Map<String, String> replace;

	private final Queue<StanzaEntry> stanzas_buff;

	public ScriptFileLoader(String source_file, Queue<StanzaEntry> stanzas_buff2, Map<String, String> replaces) {
		this.file = source_file;
		this.stanzas_buff = stanzas_buff2;
		this.replace = replaces;
	}

	public void loadSourceFile() {
		try {
			ParserState state = ParserState.start;
			String description = null;
			StringBuilder buff = null;
			BufferedReader buffr = new BufferedReader(new FileReader(file));
			String line = buffr.readLine();
			while (line != null) {
				line = line.trim();
				switch (state) {
				case start:
					String keyword = "";
					String param = null;
					Matcher m = pattern.matcher(line);
					boolean b = m.matches();
					if (b) {
						keyword = m.group(2) == null ? m.group(5) : m.group(2);
						param = m.group(3) == null ? null : m.group(3);
					}
					if ("send".equals(keyword)) {
						state = ParserState.send_stanza;
						description = param;
						buff = new StringBuilder();
					}
					if ("expect".equals(keyword)) {
						state = ParserState.expect_stanza;
						description = param;
						buff = new StringBuilder();
					}
					if ("expect all".equals(keyword)) {
						state = ParserState.expect_all_stanza;
						description = param;
						buff = new StringBuilder();
					}
					if ("expect strict".equals(keyword)) {
						state = ParserState.expect_strict_stanza;
						description = param;
						buff = new StringBuilder();
					}
					
					break;
				case send_stanza:
				case expect_all_stanza:
				case expect_stanza:
				case expect_strict_stanza:
					if (!line.equals("{") && !line.equals("}") && !line.startsWith("#")) {
						buff.append(line + '\n');
					}
					if (line.equals("}")) {
						Element[] elems = parseXMLData(buff.toString());
							switch (state) {
							case send_stanza:
								stanzas_buff.offer(new StanzaEntry(Action.send, elems, description));
								break;
							case expect_stanza:
								stanzas_buff.offer(new StanzaEntry(Action.expect, elems, description));
								break;
							case expect_all_stanza:
								stanzas_buff.offer(new StanzaEntry(Action.expect_all, elems, description));
								break;
							case expect_strict_stanza:
								stanzas_buff.offer(new StanzaEntry(Action.expect_strict, elems, description));
								break;
							default:
								break;
							}
						state = ParserState.start;
					}
					break;
				default:
					break;
				}
				line = buffr.readLine();
			}
			buffr.close();
		} catch (IOException e) {
			throw new RuntimeException("Can't read source file: " + file + " due to exception: " + e.getMessage(), e);
		}
	}

	private Element[] parseXMLData(String data) throws IOException {

		// Replace a few "variables"
		if (replace != null) {
			for (java.util.Map.Entry<String, String> entry : replace.entrySet()) {
				data = data.replace(entry.getKey(), entry.getValue());

			}
		}

		final StringBuilder sb = new StringBuilder();
		DomBuilderHandler domHandler = new DomBuilderHandler() {
			@Override
			public void error(String errorMessage) {
				super.error(errorMessage);
				sb.append(errorMessage);
			}
		};
		parser.parse(domHandler, data.toCharArray(), 0, data.length());
		if (sb.length() > 0)
			throw new IOException(sb.toString());
		Queue<Element> elems = domHandler.getParsedElements();
		if (elems != null && elems.size() > 0) {
			return elems.toArray(new Element[elems.size()]);
		}
		return null;
	}

	private final static Pattern pattern =Pattern.compile("(^(.+)\\((.*)\\):.*)|(^(.+):.*)");
}
