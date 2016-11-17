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

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Describe class NullFilter here.
 *
 *
 * Created: Sun Jul 10 22:22:34 2005
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public class NullFilter implements OutputFilter {

  /**
   * Creates a new <code>NullFilter</code> instance.
   *
   */
  public NullFilter() {

  }

  // Implementation of tigase.test.util.OutputFilter

  /**
   * Describe <code>init</code> method here.
   *
   * @param bufferedWriter a <code>BufferedWriter</code> value
   * @param string a <code>String</code> value
   * @param string1 a <code>String</code> value
   * @exception IOException if an error occurs
   */
  public void init(final BufferedWriter bufferedWriter, final String string,
    final String string1) throws IOException {

  }

  /**
   * Describe <code>close</code> method here.
   *
   * @param string a <code>String</code> value
   * @exception IOException if an error occurs
   */
  public void close(final String string) throws IOException {

  }

  /**
   * Describe <code>addContent</code> method here.
   *
   * @param string a <code>String</code> value
   * @exception IOException if an error occurs
   */
  public void addContent(final String string) throws IOException {

  }

  /**
   * Describe <code>setColumnHeaders</code> method here.
   *
   * @param stringArray a <code>String[]</code> value
   * @exception IOException if an error occurs
   */
  public void setColumnHeaders(final String ... stringArray) throws IOException {

  }

  /**
   * Describe <code>addRow</code> method here.
   *
   * @param stringArray a <code>String[]</code> value
   * @exception IOException if an error occurs
   */
  public void addRow(final String ... stringArray) throws IOException {

  }

} // NullFilter