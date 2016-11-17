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
package tigase.test;

/**
 * Describe class StatItem here.
 *
 *
 * Created: Mon Jul 11 21:41:03 2005
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public class StatItem {

  private String name = null;
  private String value = null;
  private String unit = null;
  private String description = null;

  /**
   * Creates a new <code>StatItem</code> instance.
   *
   */
  public StatItem(String name, String value, String unit, String description) {
    this.name = name;
    this.value = value;
    this.unit = unit;
    this.description = description;
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
   * Gets the value of value
   *
   * @return the value of value
   */
  public String getValue() {
    return this.value;
  }

  /**
   * Sets the value of value
   *
   * @param argValue Value to assign to this.value
   */
  public void setValue(final String argValue) {
    this.value = argValue;
  }

  /**
   * Gets the value of unit
   *
   * @return the value of unit
   */
  public String getUnit() {
    return this.unit;
  }

  /**
   * Sets the value of unit
   *
   * @param argUnit Value to assign to this.unit
   */
  public void setUnit(final String argUnit) {
    this.unit = argUnit;
  }

  /**
   * Gets the value of description
   *
   * @return the value of description
   */
  public String getDescription() {
    return this.description;
  }

  /**
   * Sets the value of description
   *
   * @param argDescription Value to assign to this.description
   */
  public void setDescription(final String argDescription) {
    this.description = argDescription;
  }

} // StatItem