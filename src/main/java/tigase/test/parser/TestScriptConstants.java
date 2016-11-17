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
package tigase.test.parser;

public interface TestScriptConstants {

  int EOF = 0;
  int COMMENT = 4;
  int LONGDESCR = 6;
  int LETTER = 8;
  int DIGIT = 9;
  int STRING = 10;
  int DECIMAL = 11;
  int SIMPLESTR = 12;
  int VARNAME = 13;
  int PARAMNAME = 14;
  int COLON = 15;
  int SEMICOLON = 16;
  int AT = 17;
  int LBRACE = 18;
  int RBRACE = 19;
  int ONERROR = 20;
  int ASSIGN = 21;
  int PLUS = 22;
  int MINUS = 23;
  int STAR = 24;
  int SLASH = 25;

  int DEFAULT = 0;
  int IN_LONG_DESCR = 1;

  String[] tokenImage = {
    "<EOF>",
    "\"\\n\"",
    "\"\\r\"",
    "\"\\f\"",
    "<COMMENT>",
    "\">>\"",
    "\"<<\"",
    "<token of kind 7>",
    "<LETTER>",
    "<DIGIT>",
    "<STRING>",
    "<DECIMAL>",
    "<SIMPLESTR>",
    "<VARNAME>",
    "<PARAMNAME>",
    "\":\"",
    "\";\"",
    "\"@\"",
    "\"{\"",
    "\"}\"",
    "\"!!\"",
    "\"=\"",
    "\"+\"",
    "\"-\"",
    "\"*\"",
    "\"/\"",
    "\" \"",
    "\"\\t\"",
    "\"?\"",
    "\"!\"",
    "\",\"",
  };

}