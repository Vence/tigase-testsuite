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
 * Describe class ResultsDontMatchException here.
 *
 *
 * Created: Sat Jun 18 08:11:34 2005
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public class ResultsDontMatchException extends Exception {

	public static final long serialVersionUID = 1L;

	/**
   * Creates a new <code>ResultsDontMatchException</code> instance.
   *
   */
  public ResultsDontMatchException() {
    super();
  }

  public ResultsDontMatchException(String msg) {
    super(msg);
  }

} // ResultsDontMatchException