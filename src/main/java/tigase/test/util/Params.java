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

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Describe class Params here.
 *
 *
 * Created: Tue May 17 23:10:31 2005
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public class Params implements Map<String, Object> {

  private Map<String, Object> map = null;

  public Params() {
    map = new TreeMap<String, Object>();
  }

	@SuppressWarnings({"unchecked"})
  public Params(Map map) {
    this.map = new TreeMap<String, Object>(map);
  }

  public Params(String pars) {
    this.map = parseParams(pars.split("[ \n]"));
  }

  public Params(String[] pars) {
    this.map = parseParams(pars);
  }

  protected static Map<String, Object> parseParams(String[] args) {
    Map<String, Object> map = new TreeMap<String, Object>();
    if (args != null && args.length > 0) {
      for (int i = 0; i < args.length; i++) {
        String arg = args[i].trim();
        if (arg.startsWith("-")) {
          if (i+1 >= args.length || args[i+1].trim().startsWith("-")) {
            map.put(arg, null);
          } // end of if (i+1 == args.length)
          else {
            map.put(arg, args[++i].trim());
          } // end of else
        } // end of if (args[i].equals(""))
      } // end of for (int i = 0; i < args.length; i++)
    }
    return map;
  }

	public Map<String, String> getMap() {
    Map<String, String> result = new TreeMap<String, String>();
		for (Map.Entry<String, Object> entry: map.entrySet()) {
			result.put(entry.getKey(), entry.getValue().toString());
		}
		return result;
	}

	// Implementation of java.util.Map

  /**
   * Describe <code>hashCode</code> method here.
   *
   * @return an <code>int</code> value
   */
  public int hashCode() {
    return map.hashCode();
  }

  /**
   * Describe <code>put</code> method here.
   *
   * @param key an <code>String</code> value
   * @param value an <code>Object</code> value
   * @return an <code>Object</code> value
   */
  public Object put(final String key, final Object value) {
    return map.put(key, value);
  }

  /**
   * Describe <code>clear</code> method here.
   *
   */
  public void clear() {
    map.clear();
  }

  /**
   * Describe <code>equals</code> method here.
   *
   * @param object an <code>Object</code> value
   * @return a <code>boolean</code> value
   */
  public boolean equals(final Object object) {
    return map.equals(object);
  }

  /**
   * Describe <code>entrySet</code> method here.
   *
   * @return a <code>Set</code> value
   */
	@SuppressWarnings({"unchecked"})
  public Set<Map.Entry<String, Object>> entrySet() {
    return map.entrySet();
  }

  /**
   * Describe <code>get</code> method here.
   *
   * @param object an <code>Object</code> value
   * @return an <code>Object</code> value
   */
  public Object get(final Object key) {
    return map.get(key);
  }

  public String get(final String key, final String def) {
    String val = (String)map.get(key);
    if (val != null) {
      if (val.startsWith("\"") && val.endsWith("\"")) {
        return val.substring(1, val.length() - 1);
      } else {
        return val;
      } // end of else
    } else {
      return def;
    } // end of else
//     return map.containsKey(key) ? (String)map.get(key) : def;
  }

  public int get(final String key, final int def) {
    return map.containsKey(key) ? Integer.decode(map.get(key).toString()) : def;
  }

  public long get(final String key, final long def) {
    return map.containsKey(key) ? Long.decode(map.get(key).toString()) : def;
  }

  public static boolean decodeBoolean(final String val) {
    if (val == null) {
      return false;
    } // end of if (val == null)
    if (val.equals("1") || val.equalsIgnoreCase("true")
      || val.equalsIgnoreCase("yes") || val.equalsIgnoreCase("on")
      || val.equalsIgnoreCase("tak")) {
      return true;
    }
    return false;
  }

  public boolean isFalse(final String key) {
    if (!containsKey(key)) {
      return true;
    } // end of if (!containsKey(key))
    String val = ((String)get(key)).toLowerCase();
    if (val.equals("false") || val.equals("0")
      || val.equals("no") || val.equals("off")) {
      return true;
    }
    return false;
  }

  public boolean get(final String key, final boolean def) {
    return map.containsKey(key) ? decodeBoolean(map.get(key).toString()) : def;
  }

  /**
   * Describe <code>putAll</code> method here.
   *
   * @param map a <code>Map</code> value
   */
	@SuppressWarnings({"unchecked"})
  public void putAll(final Map map) {
    if (map != null) {
      this.map.putAll(map);
    } // end of if (map != null)
  }

  /**
   * Describe <code>size</code> method here.
   *
   * @return an <code>int</code> value
   */
  public int size() {
    return map.size();
  }

  /**
   * Describe <code>values</code> method here.
   *
   * @return a <code>Collection</code> value
   */
  public Collection<Object> values() {
    return map.values();
  }

  /**
   * Describe <code>remove</code> method here.
   *
   * @param object an <code>Object</code> value
   * @return an <code>Object</code> value
   */
  public Object remove(final Object key) {
    return map.remove(key);
  }

  /**
   * Describe <code>containsKey</code> method here.
   *
   * @param object an <code>Object</code> value
   * @return a <code>boolean</code> value
   */
  public boolean containsKey(final Object key) {
    return map.containsKey(key);
  }

  /**
   * Describe <code>containsValue</code> method here.
   *
   * @param object an <code>Object</code> value
   * @return a <code>boolean</code> value
   */
  public boolean containsValue(final Object object) {
    return map.containsValue(object);
  }

  /**
   * Describe <code>isEmpty</code> method here.
   *
   * @return a <code>boolean</code> value
   */
  public boolean isEmpty() {
    return map.isEmpty();
  }

  /**
   * Describe <code>keySet</code> method here.
   *
   * @return a <code>Set</code> value
   */
	@SuppressWarnings({"unchecked"})
  public Set<String> keySet() {
    return map.keySet();
  }

  public String toString() {
    return map.toString();
  }

} // ParamsUtil