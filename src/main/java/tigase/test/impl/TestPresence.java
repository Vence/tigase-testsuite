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
 */
package tigase.test.impl;

import tigase.test.TestAbstract;
import tigase.test.util.Params;
import tigase.xml.Element;

import java.util.Map;

import javax.management.Attribute;

public class TestPresence extends TestAbstract {

  private String[] elems = {"presence"};
  private int counter = 0;
	private boolean sendPresence = false;

	public TestPresence() {
		super(
				new String[] { "jabber:client" },
				new String[] { "presence" },
				new String[] { "stream-open", "auth-sasl", "xmpp-bind" },
				new String[] { "user-register", "tls-init" }
		);
	}

	@Override
	public String nextElementName( final Element element ) throws Exception {
		debug( "nextElementName: " + element + "\n" );
		if ( sendPresence && counter < elems.length ){
      return elems[counter++];
    } // end of if (counter < elems.length)
		return null;
	}

	@Override
	public String getElementData( final String string ) throws Exception {
		debug( "getElementData: " + string + "\n" );
		switch ( counter ) {
			case 1:
				if ( sendPresence ){
					return "<presence>"
								 + "<priority>1</priority>"
								 + "</presence>";
				}
			default:
				return null;
		}
	}

	@Override
	public String[] getRespElementNames( final String string ) throws Exception {
		debug( "getRespElementNames: " + string + "\n" );
		return new String[] { "presence" };
	}

	@Override
	public String[] getRespOptionalNames( final String string ) throws Exception {
		debug( "getRespOptionalNames: " + string + "\n" );
		return null;
	}

	@Override
	public Attribute[] getRespElementAttributes( final String string ) throws Exception {
		debug( "getRespElementAttributes: " + string + "\n" );
		return null;
	}

	@Override
	public void init( final Params map, Map<String, String> vars ) {
		super.init( map, vars );
		sendPresence = params.containsKey( "-presence" );
		debug( "\n" );
		debug( "init: " + sendPresence + "\n" );
	}
}
