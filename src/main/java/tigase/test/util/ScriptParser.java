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
import java.util.Map;
import java.util.TreeMap;

/**
 * Describe class ScriptParser here.
 *
 *
 * Created: Sat May 28 07:47:46 2005
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public class ScriptParser {

  private static final int BUFF_SIZE = 64 * 1024;
  private static final String[] KEYWORDS =
  {
    "$(loop)", "$(self)", "$(daemons)"
  };

  public enum State {
    START,
      COMMENT,
      DESCR_START,
      DESCR_END,
      DESCRIPTION,
      PARAMS,
      VAR_NAME,
      VAR_VALUE,
      VAR_SEPARATORS,
      TEST_NAME;
  }

  private String scriptName = null;
  private ParserHandler hand = null;
  private StringBuilder test_name = null;
  private StringBuilder params = null;
  private StringBuilder descr = null;
  private Map<String, String> vars = new TreeMap<String, String>();
  private StringBuilder var_name = null;
  private StringBuilder var_value = null;
  private StringBuilder var_sep = null;
  private boolean keyword_error = false;
  private boolean equals_found = false;

  protected void error(int line, int pos, String msg) {
    System.err.println("Test script syntax error:\n"
      + "Line: " + line + ", position: " + pos
      + ", " + msg);
  }

  /**
   * Creates a new <code>ScriptParser</code> instance.
   *
   */
  public ScriptParser(String script, ParserHandler handler) {
    scriptName = script;
    hand = handler;
  }

  public void parseScript() throws IOException {
    State state = State.TEST_NAME;
    State tmp_state = State.TEST_NAME;
    test_name = new StringBuilder();
    BufferedReader script = new BufferedReader(new FileReader(scriptName));
    char[] buff = new char[BUFF_SIZE];
    int res = 0;
    int line = 0;
    int line_pos = 0;
    while ((res = script.read(buff)) != -1) {
      for (int i = 0; i < res; i++) {
        if (buff[i] == '\r') {
          continue;
        } // end of if (buff[i] == '\r')
        if (buff[i] == '\n') {
          ++line; line_pos = 0;
          //          buff[i] = ' ';
        } // end of if (buff[i] == '\n')
        ++line_pos;
        switch (state) {
        case TEST_NAME:
          switch (buff[i]) {
          case ':':
            hand.handleTestName(replaceAll(test_name).trim());
            test_name = new StringBuilder();
            break;
          case '{':
            state = State.PARAMS;
            params = new StringBuilder();
            break;
          case '}':
            // Syntax error, ignoring
            error(line, line_pos, "found '}' before '{'");
            break;
          case '#':
            tmp_state = state;
            state = State.COMMENT;
            break;
          case '>':
            state = State.DESCR_START;
            break;
          case '<':
            // Syntax error, ignoring
            error(line, line_pos, "found '<' before '>>'");
            break;
          case '\n':
            // just skip
            break;
          default:
            test_name.append(buff[i]);
            break;
          } // end of switch (buff[i])
          break;
        case COMMENT:
          if (buff[i] == '\n') {
            state = tmp_state;
          } // end of if (buff[i] == '\n')
          break;
        case DESCR_START:
          if (buff[i] == '>') {
            state = State.DESCRIPTION;
            descr = new StringBuilder();
          } // end of if (buff[i] == '>')
          else {
            error(line, line_pos,
              "second '>' must follow first '>' to start test description.");
            state = State.TEST_NAME;
          } // end of else
          break;
        case DESCRIPTION:
          if (buff[i] == '<') {
            hand.handleTestDescription(replaceAll(descr).trim());
            state = State.DESCR_END;
          } // end of if (buff[i] == '<')
          else {
            descr.append(buff[i]);
          } // end of else
          break;
        case DESCR_END:
          if (buff[i] != '<') {
            error(line, line_pos,
              "second '<' must follow first '<' to end test description.");
          } // end of else
          state = State.TEST_NAME;
          break;
        case PARAMS:
          switch (buff[i]) {
          case '}':
            hand.handleTestParams(new Params(replaceAll(params).trim()));
            state = State.TEST_NAME;
            break;
          case '#':
            tmp_state = state;
            state = State.COMMENT;
            break;
          case '$':
            state = State.VAR_NAME;
            var_name = new StringBuilder();
            var_name.append('$');
            break;
          default:
            params.append(buff[i]);
            break;
          } // end of switch (buff[i])
          break;
        case VAR_NAME:
          switch (buff[i]) {
          case ')':
            var_name.append(buff[i]);
            keyword_error = false;
            for (String key : KEYWORDS) {
              if (key.equals(var_name.toString())) {
                keyword_error = true;
                break;
              } // end of if ()
            } // end of for ()
            state = State.VAR_SEPARATORS;
            var_sep = new StringBuilder();
            var_value = new StringBuilder();
            equals_found = false;
            break;
          default:
            var_name.append(buff[i]);
            break;
          } // end of switch (buff[i])
          break;
        case VAR_SEPARATORS:
          switch (buff[i]) {
          case '=':
            equals_found = true;
            if (keyword_error) {
              error(line, line_pos,
                "KEY_WORD: " + var_name.toString()
                + ", used as variable name, ignored.");
            } // end of if ()
            break;
          case '}':
            state = State.TEST_NAME;
            params.append(var_name);
            hand.handleTestParams(new Params(replaceAll(params).trim()));
            break;
          case '$':
            state = State.VAR_NAME;
            params.append(var_name);
            params.append(var_sep);
            var_name = new StringBuilder();
            var_name.append('$');
            break;
          case '#':
            params.append(var_name);
            params.append(var_sep);
            tmp_state = State.PARAMS;
            state = State.COMMENT;
            break;
          default:
            if (Character.isWhitespace(buff[i])) {
              var_sep.append(buff[i]);
            } else {
              if (equals_found) {
                state = State.VAR_VALUE;
                var_value.append(buff[i]);
              } // end of if (equals_found)
              else {
                state = State.PARAMS;
                params.append(var_name);
                params.append(var_sep);
                params.append(buff[i]);
              } // end of if (equals_found) else
            } // end of else
            break;
          } // end of switch (buff[i])
          break;
        case VAR_VALUE:
          if (Character.isWhitespace(buff[i])) {
            state = State.PARAMS;
//             System.out.println("Found var def: "
//               + var_name + " = " + var_value);
            vars.put(var_name.toString(), var_value.toString());
          } else {
            var_value.append(buff[i]);
          } // end of else
          break;
        default:
          break;
        } // end of switch (state)
      } // end of for (int i = 0; i < res; i++)
    } // end of while ((res = script.read(buff)) != -1)
    hand.handleScriptEnd();
  }

  private String replaceAll(StringBuilder source) {
    //    System.out.println("SOURCE: " + source);
    String result = source.toString();
    for (String key : vars.keySet()) {
      while (result.contains(key)) {
        result = result.replace(key, vars.get(key));
      } // end of while (result.contains(key))
    } // end of for ()
    //    System.out.println("RESULT: " + result);
    return result;
  }

} // ScriptParser