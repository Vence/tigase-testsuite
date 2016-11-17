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

import java.util.List;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.TreeMap;

@SuppressWarnings({"unchecked", "serial"})
public class TestScript implements TestScriptConstants {

  public static final String VERSION = "\"2.0.0\"";

  private List tests = new LinkedList();
  private Map globalPars = new TreeMap();
  private Map globalVars = new TreeMap();

  public List getTests() {
    return tests;
  }

  public Map getGlobalPars() {
    return globalPars;
  }

  public Map getGlobalVars() {
    return globalVars;
  }

  public static void main(String args[]) throws ParseException {
    TestScript parser = new TestScript(System.in);
    parser.Input();
    System.out.println("Tests found: \n");
    ListIterator lit = parser.tests.listIterator();
    while (lit.hasNext()) {
      TestNode tn = (TestNode)lit.next();
      System.out.print(tn.toString());
    }
  }

  final public void Input() throws ParseException {
  TestNode tn = null;
  Token par = null;
  Token val = null;
  Map vars = null;
  Map pars = null;
    label_1:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case DECIMAL:
      case SIMPLESTR:
      case VARNAME:
      case PARAMNAME:
      case 26:
        ;
        break;
      default:
        jj_la1[0] = jj_gen;
        break label_1;
      }
      if (jj_2_1(2)) {
        jj_consume_token(26);
      } else {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case VARNAME:
          vars = TestVars();
                          if (vars != null) globalVars.putAll(vars);
          break;
        case PARAMNAME:
          pars = TestParams();
                            if (pars != null) globalPars.putAll(pars);
          break;
        case DECIMAL:
        case SIMPLESTR:
        case 26:
          tn = Test();
                    tests.add(tn);
          break;
        default:
          jj_la1[1] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
      }
    }
      if (!globalPars.containsKey("-version") ||
          !globalPars.get("-version").toString().equals(VERSION)) {
        {if (true) throw new ParseException("Incorrect version of script file, "
                                 + VERSION + " supported.");}
      }
    jj_consume_token(0);
  }

  final public TestNode Test() throws ParseException {
  TestNode tn = null;
  TestNode onError = null;
  String longDescr = null;
    tn = BasicTest();
    if (jj_2_2(2)) {
      onError = OnError();
    } else {
      ;
    }
    longDescr = LongDescr();
    if (onError != null) {
      tn.setOnError(onError);
      onError.setParent(tn);
    }
    tn.setLongDescr(longDescr);
    {if (true) return tn;}
    throw new Error("Missing return statement in function");
  }

  final public TestNode BasicTest() throws ParseException {
  TestNode tn = new TestNode();
  TestNode child = null;
  String name = null;
  String id = null;
  String shortDescr = null;
  Map vars = null;
  Map pars = null;
    name = TestName();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case AT:
      jj_consume_token(AT);
      id = TestID();
      break;
    default:
      jj_la1[2] = jj_gen;
      ;
    }
    jj_consume_token(COLON);
    if (jj_2_3(2)) {
      shortDescr = ShortDescr();
    } else {
      ;
    }
    if (jj_2_5(2)) {
      LBrace();
      label_2:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case DECIMAL:
        case SIMPLESTR:
        case VARNAME:
        case PARAMNAME:
        case 26:
          ;
          break;
        default:
          jj_la1[3] = jj_gen;
          break label_2;
        }
        if (jj_2_4(2)) {
          jj_consume_token(26);
        } else {
          switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
          case VARNAME:
            vars = TestVars();
                                       if (vars != null) tn.addVars(vars);
            break;
          case PARAMNAME:
            pars = TestParams();
                                         if (pars != null) tn.addPars(pars);
            break;
          case DECIMAL:
          case SIMPLESTR:
          case 26:
            child = Test();
          if (child != null) { tn.addChild(child); child.setParent(tn); }
            break;
          default:
            jj_la1[4] = jj_gen;
            jj_consume_token(-1);
            throw new ParseException();
          }
        }
      }
      RBrace();
      label_3:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case 26:
          ;
          break;
        default:
          jj_la1[5] = jj_gen;
          break label_3;
        }
        jj_consume_token(26);
      }
    } else {
      ;
    }
      tn.setName(name);
      tn.setId(id);
      tn.setShortDescr(shortDescr);
      {if (true) return tn;}
    throw new Error("Missing return statement in function");
  }

  final public String TestName() throws ParseException {
  Token token = null;
  StringBuilder buff = new StringBuilder();
    label_4:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case SIMPLESTR:
        token = jj_consume_token(SIMPLESTR);
                            buff.append(token.image);
        break;
      case DECIMAL:
        token = jj_consume_token(DECIMAL);
                            buff.append(token.image);
        break;
      case 26:
        jj_consume_token(26);
            buff.append(" ");
        break;
      default:
        jj_la1[6] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case DECIMAL:
      case SIMPLESTR:
      case 26:
        ;
        break;
      default:
        jj_la1[7] = jj_gen;
        break label_4;
      }
    }
      {if (true) return buff.toString();}
    throw new Error("Missing return statement in function");
  }

  final public String TestID() throws ParseException {
  Token token = null;
  StringBuilder buff = new StringBuilder();
    token = jj_consume_token(SIMPLESTR);
                          buff.append(token.image);
    label_5:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case SEMICOLON:
        ;
        break;
      default:
        jj_la1[8] = jj_gen;
        break label_5;
      }
      jj_consume_token(SEMICOLON);
                      buff.append(";");
      token = jj_consume_token(SIMPLESTR);
                              buff.append(token.image);
    }
      {if (true) return buff.toString();}
    throw new Error("Missing return statement in function");
  }

  final public String ShortDescr() throws ParseException {
  Token token = null;
  StringBuilder buff = new StringBuilder();
  String tmp = null;
    label_6:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case SIMPLESTR:
        token = jj_consume_token(SIMPLESTR);
                            buff.append(token.image);
        break;
      case DECIMAL:
      case VARNAME:
        tmp = Expression();
                             buff.append(tmp);
        break;
      case 26:
        jj_consume_token(26);
            buff.append(" ");
        break;
      case 27:
        jj_consume_token(27);
             buff.append("\t");
        break;
      case 28:
        jj_consume_token(28);
            buff.append("?");
        break;
      case 29:
        jj_consume_token(29);
            buff.append("!");
        break;
      case MINUS:
        jj_consume_token(MINUS);
                buff.append("-");
        break;
      case 30:
        jj_consume_token(30);
            buff.append(",");
        break;
      default:
        jj_la1[9] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case DECIMAL:
      case SIMPLESTR:
      case VARNAME:
      case MINUS:
      case 26:
      case 27:
      case 28:
      case 29:
      case 30:
        ;
        break;
      default:
        jj_la1[10] = jj_gen;
        break label_6;
      }
    }
      {if (true) return buff.toString();}
    throw new Error("Missing return statement in function");
  }

  final public String Expression() throws ParseException {
  Token token = null;
  StringBuilder buff = new StringBuilder();
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case VARNAME:
      token = jj_consume_token(VARNAME);
                        buff.append(token.image);
      break;
    case DECIMAL:
      token = jj_consume_token(DECIMAL);
                          buff.append(token.image);
      break;
    default:
      jj_la1[11] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
    label_7:
    while (true) {
      if (jj_2_6(2)) {
        ;
      } else {
        break label_7;
      }
      label_8:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case 26:
          ;
          break;
        default:
          jj_la1[12] = jj_gen;
          break label_8;
        }
        jj_consume_token(26);
                           buff.append(' ');
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case PLUS:
        jj_consume_token(PLUS);
                 buff.append('+');
        break;
      case MINUS:
        jj_consume_token(MINUS);
                    buff.append('-');
        break;
      case STAR:
        jj_consume_token(STAR);
                   buff.append('*');
        break;
      case SLASH:
        jj_consume_token(SLASH);
                    buff.append('/');
        break;
      default:
        jj_la1[13] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      label_9:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case 26:
          ;
          break;
        default:
          jj_la1[14] = jj_gen;
          break label_9;
        }
        jj_consume_token(26);
                  buff.append(' ');
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case VARNAME:
        token = jj_consume_token(VARNAME);
                            buff.append(token.image);
        break;
      case DECIMAL:
        token = jj_consume_token(DECIMAL);
                              buff.append(token.image);
        break;
      default:
        jj_la1[15] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
      {if (true) return buff.toString();}
    throw new Error("Missing return statement in function");
  }

  final public void LBrace() throws ParseException {
    jj_consume_token(LBRACE);
  }

  final public void RBrace() throws ParseException {
    jj_consume_token(RBRACE);
  }

  final public TestNode OnError() throws ParseException {
  TestNode onError = null;
    jj_consume_token(ONERROR);
    label_10:
    while (true) {
      if (jj_2_7(2)) {
        ;
      } else {
        break label_10;
      }
      jj_consume_token(26);
    }
    onError = BasicTest();
    {if (true) return onError;}
    throw new Error("Missing return statement in function");
  }

  final public String LongDescr() throws ParseException {
  Token token = null;
    token = jj_consume_token(LONGDESCR);
    {if (true) return token.image;}
    throw new Error("Missing return statement in function");
  }

  final public Map TestVars() throws ParseException {
  Map vars = new TreeMap();
  Token var = null;
  Token val = null;
    label_11:
    while (true) {
      var = jj_consume_token(VARNAME);
      label_12:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case 26:
          ;
          break;
        default:
          jj_la1[16] = jj_gen;
          break label_12;
        }
        jj_consume_token(26);
      }
      jj_consume_token(ASSIGN);
      label_13:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case 26:
          ;
          break;
        default:
          jj_la1[17] = jj_gen;
          break label_13;
        }
        jj_consume_token(26);
      }
      val = Value();
    vars.put(var.image, (val != null ? val.image : null));
      if (jj_2_8(2)) {
        ;
      } else {
        break label_11;
      }
    }
      {if (true) return vars;}
    throw new Error("Missing return statement in function");
  }

  final public Map TestParams() throws ParseException {
  Map pars = new TreeMap();
  Token par = null;
  Token val = null;
    label_14:
    while (true) {
      par = jj_consume_token(PARAMNAME);
      label_15:
      while (true) {
        if (jj_2_9(2)) {
          ;
        } else {
          break label_15;
        }
        jj_consume_token(26);
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case ASSIGN:
        jj_consume_token(ASSIGN);
        label_16:
        while (true) {
          switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
          case 26:
            ;
            break;
          default:
            jj_la1[18] = jj_gen;
            break label_16;
          }
          jj_consume_token(26);
        }
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case STRING:
        case DECIMAL:
        case SIMPLESTR:
          val = Value();
          break;
        case VARNAME:
          val = jj_consume_token(VARNAME);
          break;
        default:
          jj_la1[19] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
        break;
      default:
        jj_la1[20] = jj_gen;
        ;
      }
    pars.put(par.image, (val != null ? val.image : null));
      if (jj_2_10(2)) {
        ;
      } else {
        break label_14;
      }
    }
      {if (true) return pars;}
    throw new Error("Missing return statement in function");
  }

  final public Token Value() throws ParseException {
  Token token = null;
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case DECIMAL:
      token = jj_consume_token(DECIMAL);
      break;
    case STRING:
      token = jj_consume_token(STRING);
      break;
    case SIMPLESTR:
      token = jj_consume_token(SIMPLESTR);
      break;
    default:
      jj_la1[21] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
      {if (true) return token;}
    throw new Error("Missing return statement in function");
  }

  final private boolean jj_2_1(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_1(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  }

  final private boolean jj_2_2(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_2(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(1, xla); }
  }

  final private boolean jj_2_3(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_3(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(2, xla); }
  }

  final private boolean jj_2_4(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_4(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(3, xla); }
  }

  final private boolean jj_2_5(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_5(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(4, xla); }
  }

  final private boolean jj_2_6(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_6(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(5, xla); }
  }

  final private boolean jj_2_7(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_7(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(6, xla); }
  }

  final private boolean jj_2_8(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_8(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(7, xla); }
  }

  final private boolean jj_2_9(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_9(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(8, xla); }
  }

  final private boolean jj_2_10(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_10(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(9, xla); }
  }

  final private boolean jj_3R_52() {
    if (jj_scan_token(DECIMAL)) return true;
    return false;
  }

  final private boolean jj_3_2() {
    if (jj_3R_17()) return true;
    return false;
  }

  final private boolean jj_3R_51() {
    if (jj_scan_token(VARNAME)) return true;
    return false;
  }

  final private boolean jj_3R_47() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_51()) {
    jj_scanpos = xsp;
    if (jj_3R_52()) return true;
    }
    while (true) {
      xsp = jj_scanpos;
      if (jj_3_6()) { jj_scanpos = xsp; break; }
    }
    return false;
  }

  final private boolean jj_3R_45() {
    if (jj_3R_29()) return true;
    return false;
  }

  final private boolean jj_3R_42() {
    if (jj_scan_token(30)) return true;
    return false;
  }

  final private boolean jj_3R_41() {
    if (jj_scan_token(MINUS)) return true;
    return false;
  }

  final private boolean jj_3R_40() {
    if (jj_scan_token(29)) return true;
    return false;
  }

  final private boolean jj_3R_39() {
    if (jj_scan_token(28)) return true;
    return false;
  }

  final private boolean jj_3R_38() {
    if (jj_scan_token(27)) return true;
    return false;
  }

  final private boolean jj_3R_37() {
    if (jj_scan_token(26)) return true;
    return false;
  }

  final private boolean jj_3R_36() {
    if (jj_3R_47()) return true;
    return false;
  }

  final private boolean jj_3R_30() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_35()) {
    jj_scanpos = xsp;
    if (jj_3R_36()) {
    jj_scanpos = xsp;
    if (jj_3R_37()) {
    jj_scanpos = xsp;
    if (jj_3R_38()) {
    jj_scanpos = xsp;
    if (jj_3R_39()) {
    jj_scanpos = xsp;
    if (jj_3R_40()) {
    jj_scanpos = xsp;
    if (jj_3R_41()) {
    jj_scanpos = xsp;
    if (jj_3R_42()) return true;
    }
    }
    }
    }
    }
    }
    }
    return false;
  }

  final private boolean jj_3R_35() {
    if (jj_scan_token(SIMPLESTR)) return true;
    return false;
  }

  final private boolean jj_3R_18() {
    Token xsp;
    if (jj_3R_30()) return true;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3R_30()) { jj_scanpos = xsp; break; }
    }
    return false;
  }

  final private boolean jj_3R_28() {
    if (jj_scan_token(ASSIGN)) return true;
    return false;
  }

  final private boolean jj_3_1() {
    if (jj_scan_token(26)) return true;
    return false;
  }

  final private boolean jj_3_10() {
    if (jj_scan_token(PARAMNAME)) return true;
    Token xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3_9()) { jj_scanpos = xsp; break; }
    }
    xsp = jj_scanpos;
    if (jj_3R_28()) jj_scanpos = xsp;
    return false;
  }

  final private boolean jj_3R_44() {
    Token xsp;
    if (jj_3_10()) return true;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3_10()) { jj_scanpos = xsp; break; }
    }
    return false;
  }

  final private boolean jj_3_8() {
    if (jj_scan_token(VARNAME)) return true;
    Token xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_scan_token(26)) { jj_scanpos = xsp; break; }
    }
    if (jj_scan_token(ASSIGN)) return true;
    return false;
  }

  final private boolean jj_3R_43() {
    Token xsp;
    if (jj_3_8()) return true;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3_8()) { jj_scanpos = xsp; break; }
    }
    return false;
  }

  final private boolean jj_3R_50() {
    if (jj_scan_token(26)) return true;
    return false;
  }

  final private boolean jj_3R_49() {
    if (jj_scan_token(DECIMAL)) return true;
    return false;
  }

  final private boolean jj_3R_48() {
    if (jj_scan_token(SIMPLESTR)) return true;
    return false;
  }

  final private boolean jj_3R_46() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3R_48()) {
    jj_scanpos = xsp;
    if (jj_3R_49()) {
    jj_scanpos = xsp;
    if (jj_3R_50()) return true;
    }
    }
    return false;
  }

  final private boolean jj_3R_33() {
    if (jj_3R_45()) return true;
    return false;
  }

  final private boolean jj_3R_34() {
    Token xsp;
    if (jj_3R_46()) return true;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3R_46()) { jj_scanpos = xsp; break; }
    }
    return false;
  }

  final private boolean jj_3R_32() {
    if (jj_3R_44()) return true;
    return false;
  }

  final private boolean jj_3R_31() {
    if (jj_3R_43()) return true;
    return false;
  }

  final private boolean jj_3_7() {
    if (jj_scan_token(26)) return true;
    return false;
  }

  final private boolean jj_3R_19() {
    Token xsp;
    xsp = jj_scanpos;
    if (jj_3_4()) {
    jj_scanpos = xsp;
    if (jj_3R_31()) {
    jj_scanpos = xsp;
    if (jj_3R_32()) {
    jj_scanpos = xsp;
    if (jj_3R_33()) return true;
    }
    }
    }
    return false;
  }

  final private boolean jj_3_4() {
    if (jj_scan_token(26)) return true;
    return false;
  }

  final private boolean jj_3R_17() {
    if (jj_scan_token(ONERROR)) return true;
    Token xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3_7()) { jj_scanpos = xsp; break; }
    }
    if (jj_3R_29()) return true;
    return false;
  }

  final private boolean jj_3_5() {
    if (jj_scan_token(18)) return true;
    Token xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3R_19()) { jj_scanpos = xsp; break; }
    }
    if (jj_scan_token(19)) return true;
    return false;
  }

  final private boolean jj_3_3() {
    if (jj_3R_18()) return true;
    return false;
  }

  final private boolean jj_3R_29() {
    if (jj_3R_34()) return true;
    return false;
  }

  final private boolean jj_3R_20() {
    if (jj_scan_token(26)) return true;
    return false;
  }

  final private boolean jj_3R_25() {
    if (jj_scan_token(26)) return true;
    return false;
  }

  final private boolean jj_3R_27() {
    if (jj_scan_token(DECIMAL)) return true;
    return false;
  }

  final private boolean jj_3R_24() {
    if (jj_scan_token(SLASH)) return true;
    return false;
  }

  final private boolean jj_3R_26() {
    if (jj_scan_token(VARNAME)) return true;
    return false;
  }

  final private boolean jj_3R_23() {
    if (jj_scan_token(STAR)) return true;
    return false;
  }

  final private boolean jj_3R_22() {
    if (jj_scan_token(MINUS)) return true;
    return false;
  }

  final private boolean jj_3_9() {
    if (jj_scan_token(26)) return true;
    return false;
  }

  final private boolean jj_3R_21() {
    if (jj_scan_token(PLUS)) return true;
    return false;
  }

  final private boolean jj_3_6() {
    Token xsp;
    while (true) {
      xsp = jj_scanpos;
      if (jj_3R_20()) { jj_scanpos = xsp; break; }
    }
    xsp = jj_scanpos;
    if (jj_3R_21()) {
    jj_scanpos = xsp;
    if (jj_3R_22()) {
    jj_scanpos = xsp;
    if (jj_3R_23()) {
    jj_scanpos = xsp;
    if (jj_3R_24()) return true;
    }
    }
    }
    while (true) {
      xsp = jj_scanpos;
      if (jj_3R_25()) { jj_scanpos = xsp; break; }
    }
    xsp = jj_scanpos;
    if (jj_3R_26()) {
    jj_scanpos = xsp;
    if (jj_3R_27()) return true;
    }
    return false;
  }

  public TestScriptTokenManager token_source;
  JavaCharStream jj_input_stream;
  public Token token, jj_nt;
  private int jj_ntk;
  private Token jj_scanpos, jj_lastpos;
  private int jj_la;
  public boolean lookingAhead = false;
  private boolean jj_semLA;
  private int jj_gen;
  final private int[] jj_la1 = new int[22];
  static private int[] jj_la1_0;
  static {
      jj_la1_0();
   }
   private static void jj_la1_0() {
      jj_la1_0 = new int[] {0x4007800,0x4007800,0x20000,0x4007800,0x4007800,0x4000000,0x4001800,0x4001800,0x10000,0x7c803800,0x7c803800,0x2800,0x4000000,0x3c00000,0x4000000,0x2800,0x4000000,0x4000000,0x4000000,0x3c00,0x200000,0x1c00,};
   }
  final private JJCalls[] jj_2_rtns = new JJCalls[10];
  private boolean jj_rescan = false;
  private int jj_gc = 0;

  public TestScript(java.io.InputStream stream) {
    jj_input_stream = new JavaCharStream(stream, 1, 1);
    token_source = new TestScriptTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 22; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public void ReInit(java.io.InputStream stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 22; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public TestScript(java.io.Reader stream) {
    jj_input_stream = new JavaCharStream(stream, 1, 1);
    token_source = new TestScriptTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 22; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 22; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public TestScript(TestScriptTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 22; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public void ReInit(TestScriptTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 22; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  final private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      if (++jj_gc > 100) {
        jj_gc = 0;
        for (int i = 0; i < jj_2_rtns.length; i++) {
          JJCalls c = jj_2_rtns[i];
          while (c != null) {
            if (c.gen < jj_gen) c.first = null;
            c = c.next;
          }
        }
      }
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  static private final class LookaheadSuccess extends java.lang.Error { }
  final private LookaheadSuccess jj_ls = new LookaheadSuccess();
  final private boolean jj_scan_token(int kind) {
    if (jj_scanpos == jj_lastpos) {
      jj_la--;
      if (jj_scanpos.next == null) {
        jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
      } else {
        jj_lastpos = jj_scanpos = jj_scanpos.next;
      }
    } else {
      jj_scanpos = jj_scanpos.next;
    }
    if (jj_rescan) {
      int i = 0; Token tok = token;
      while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
      if (tok != null) jj_add_error_token(kind, i);
    }
    if (jj_scanpos.kind != kind) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;
    return false;
  }

  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

  final public Token getToken(int index) {
    Token t = lookingAhead ? jj_scanpos : token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  final private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.Vector jj_expentries = new java.util.Vector();
  private int[] jj_expentry;
  private int jj_kind = -1;
  private int[] jj_lasttokens = new int[100];
  private int jj_endpos;

  private void jj_add_error_token(int kind, int pos) {
    if (pos >= 100) return;
    if (pos == jj_endpos + 1) {
      jj_lasttokens[jj_endpos++] = kind;
    } else if (jj_endpos != 0) {
      jj_expentry = new int[jj_endpos];
      for (int i = 0; i < jj_endpos; i++) {
        jj_expentry[i] = jj_lasttokens[i];
      }
      boolean exists = false;
      for (java.util.Enumeration e = jj_expentries.elements(); e.hasMoreElements();) {
        int[] oldentry = (int[])(e.nextElement());
        if (oldentry.length == jj_expentry.length) {
          exists = true;
          for (int i = 0; i < jj_expentry.length; i++) {
            if (oldentry[i] != jj_expentry[i]) {
              exists = false;
              break;
            }
          }
          if (exists) break;
        }
      }
      if (!exists) jj_expentries.addElement(jj_expentry);
      if (pos != 0) jj_lasttokens[(jj_endpos = pos) - 1] = kind;
    }
  }

  public ParseException generateParseException() {
    jj_expentries.removeAllElements();
    boolean[] la1tokens = new boolean[31];
    for (int i = 0; i < 31; i++) {
      la1tokens[i] = false;
    }
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 22; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 31; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.addElement(jj_expentry);
      }
    }
    jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = (int[])jj_expentries.elementAt(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  final public void enable_tracing() {
  }

  final public void disable_tracing() {
  }

  final private void jj_rescan_token() {
    jj_rescan = true;
    for (int i = 0; i < 10; i++) {
      JJCalls p = jj_2_rtns[i];
      do {
        if (p.gen > jj_gen) {
          jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
          switch (i) {
            case 0: jj_3_1(); break;
            case 1: jj_3_2(); break;
            case 2: jj_3_3(); break;
            case 3: jj_3_4(); break;
            case 4: jj_3_5(); break;
            case 5: jj_3_6(); break;
            case 6: jj_3_7(); break;
            case 7: jj_3_8(); break;
            case 8: jj_3_9(); break;
            case 9: jj_3_10(); break;
          }
        }
        p = p.next;
      } while (p != null);
    }
    jj_rescan = false;
  }

  final private void jj_save(int index, int xla) {
    JJCalls p = jj_2_rtns[index];
    while (p.gen > jj_gen) {
      if (p.next == null) { p = p.next = new JJCalls(); break; }
      p = p.next;
    }
    p.gen = jj_gen + xla - jj_la; p.first = token; p.arg = xla;
  }

  static final class JJCalls {
    int gen;
    Token first;
    int arg;
    JJCalls next;
  }

}