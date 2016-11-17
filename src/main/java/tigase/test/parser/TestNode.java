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
import java.util.Map;
import java.util.TreeMap;
import java.util.LinkedList;

/**
 * Describe class TestNode here.
 *
 *
 * Created: Fri Jul  8 15:37:17 2005
 *
 * @author <a href="mailto:artur.hefczyc@aprsmartlogik.com">Artur Hefczyc</a>
 * @version $Rev$
 */
public class TestNode {

  /**
   * Describe parent here.
   */
  private TestNode parent;
  private TestNode onError = null;

  private String name = null;
  private String id = null;
  private String shortDescr = null;
  private String longDescr = null;

  /**
   * Describe children here.
   */
  private List<TestNode> children = null;
  private Map<String, String> vars = null;
  private Map<String, String> pars = null;
  private Map<String, String> nodePars = null;

  /**
   * Creates a new <code>TestNode</code> instance.
   *
   */
  public TestNode() {
    children = new LinkedList<TestNode>();
    vars = new TreeMap<String, String>();
    pars = new TreeMap<String, String>();
    nodePars = new TreeMap<String, String>();
  }

  public void setVars(Map<String, String> vars) {
    this.vars = new TreeMap<String, String>(vars);
  }

  public void addVars(Map<String, String> vars) {
    this.vars.putAll(vars);
  }

  public void addVar(String key, String val) {
    this.vars.put(key, val);
  }

  public void addGlobalVars(Map<String, String> vars) {
    Map<String, String> tmp = new TreeMap<String, String>(vars);
    tmp.putAll(this.vars);
    this.vars = tmp;
  }

  public Map<String, String> getVars() {
    Map<String, String> result = new TreeMap<String, String>();
    if (parent != null) {
      result.putAll(parent.getVars());
    } // end of if (parent != null)
    result.putAll(vars);
    return result;
  }

  public void setPars(Map<String, String> pars) {
    this.pars = new TreeMap<String, String>(pars);
    this.nodePars = new TreeMap<String, String>(pars);
  }

  public void addPars(Map<String, String> pars) {
    this.pars.putAll(pars);
    this.nodePars.putAll(pars);
  }

  public void addPar(String key, String val) {
    this.pars.put(key, val);
    this.nodePars.put(key, val);
  }
  
  public Map<String, String> getNodePars() {
    Map<String, String> result = new TreeMap<String, String>(nodePars);
    return result;
  }

  public void addGlobalPars(Map<String, String> pars) {
    Map<String, String> tmp = new TreeMap<String, String>(pars);
    tmp.putAll(this.pars);
    this.pars = tmp;
  }

  public Map<String, String> getPurePars() {
    Map<String, String> result = new TreeMap<String, String>();
    if (parent != null) {
      result.putAll(parent.getPurePars());
    } // end of if (parent != null)
    result.putAll(pars);
    return result;
  }

  public Map<String, String> getPars() {
    Map<String, String> result = new TreeMap<String, String>();
    if (parent != null) {
      result.putAll(parent.getPurePars());
    } // end of if (parent != null)
    result.putAll(pars);
    for (String key : result.keySet()) {
      String newVal = replaceAll(result.get(key));
      if (newVal != null && newVal.startsWith("\"") && newVal.endsWith("\"")) {
        newVal = newVal.substring(1, newVal.length() - 1);
      }
      result.put(key, newVal);
    } // end of for (String key : result.keySet())
    return result;
  }

  /**
   * Gets the value of onError
   *
   * @return the value of onError
   */
  public TestNode getOnError() {
    return this.onError;
  }

  /**
   * Sets the value of onError
   *
   * @param argOnError Value to assign to this.onError
   */
  public void setOnError(final TestNode argOnError) {
    this.onError = argOnError;
  }

  /**
   * Gets the value of name
   *
   * @return the value of name
   */
  public String getName() {
    return this.name;
  }

  /**
   * Sets the value of name
   *
   * @param argName Value to assign to this.name
   */
  public void setName(final String argName) {
    this.name = argName;
  }

  /**
   * Gets the value of id
   *
   * @return the value of id
   */
  public String getId() {
    return this.id;
  }

  /**
   * Sets the value of id
   *
   * @param argId Value to assign to this.id
   */
  public void setId(final String argId) {
    this.id = argId;
  }

  /**
   * Gets the value of shortDescr
   *
   * @return the value of shortDescr
   */
  public String getShortDescr() {
    return replaceAll(this.shortDescr);
  }

  /**
   * Sets the value of shortDescr
   *
   * @param argShortDescr Value to assign to this.shortDescr
   */
  public void setShortDescr(final String argShortDescr) {
    this.shortDescr = argShortDescr;
  }

  /**
   * Gets the value of longDescr
   *
   * @return the value of longDescr
   */
  public String getLongDescr() {
    return replaceAll(this.longDescr);
  }

  /**
   * Sets the value of longDescr
   *
   * @param argLongDescr Value to assign to this.longDescr
   */
  public void setLongDescr(final String argLongDescr) {
    this.longDescr = argLongDescr;
  }

  /**
   * Get the <code>Children</code> value.
   *
   * @return a <code>List<TestNode></code> value
   */
  public List<TestNode> getChildren() {
    return children;
  }

  /**
   * Set the <code>Children</code> value.
   *
   * @param newChildren The new Children value.
   */
  public void setChildren(final List<TestNode> newChildren) {
    this.children = new LinkedList<TestNode>(newChildren);
  }

  /**
   * Describe <code>addChild</code> method here.
   *
   * @param child a <code>TestNode</code> value
   */
  public void addChild(final TestNode child) {
    children.add(child);
  }

  /**
   * Get the <code>Parent</code> value.
   *
   * @return a <code>TestNode</code> value
   */
  public TestNode getParent() {
    return parent;
  }

  /**
   * Set the <code>Parent</code> value.
   *
   * @param newParent The new Parent value.
   */
  public void setParent(final TestNode newParent) {
    this.parent = newParent;
  }

  public String toString(String indent) {
    StringBuilder sb = new StringBuilder();
    sb.append(indent + name + "\n");
    for (TestNode child : children) {
      sb.append(child.toString(indent + "  "));
    }
    return sb.toString();
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append('[');
    sb.append(name); sb.append('@');        sb.append(id);
    sb.append('\n');
    sb.append('"');  sb.append(shortDescr); sb.append('"');
    sb.append('\n');
    sb.append('"');  sb.append(longDescr);  sb.append('"');
    sb.append('\n');
    sb.append(getVars().toString());
    sb.append('\n');
    sb.append(getPars().toString());
    sb.append('\n');
    if (onError != null) {
      sb.append("!!"); sb.append(onError.toString()); sb.append("!!");
    }
    for (TestNode child : children) {
      sb.append('\n');
      sb.append(child.toString());
    }
    sb.append(']');
    return sb.toString();
  }

  private String replaceAll(String source) {
    //    System.out.println("SOURCE: " + source);
    String result = source;
    if (result == null) {
      return null;
    } // end of if (result == null)
    Map<String, String> allVars = getVars();
    for (String key : allVars.keySet()) {
      while (result.contains(key)) {
				String newVal = allVars.get(key);
				if (newVal != null && newVal.startsWith("\"") && newVal.endsWith("\"")) {
					newVal = newVal.substring(1, newVal.length() - 1);
				}
        result = result.replace(key, newVal);
      } // end of while (result.contains(key))
    } // end of for ()
    //    System.out.println("RESULT: " + result);
    return result;
  }

} // TestNode
