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

import java.util.Arrays;
import java.util.Comparator;
import tigase.test.TestIfc;

/**
 * Describe class DependsComparator here.
 *
 *
 * Created: Wed May 18 07:40:46 2005
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public class DependsComparator implements Comparator<TestIfc> {

  private static DependsComparator comp = null;

  /**
   * Creates a new <code>DependsComparator</code> instance.
   *
   */
  private DependsComparator() {

  }

  public static DependsComparator getInstance() {
    if (comp == null) {
      comp = new DependsComparator();
    } // end of if (comp == null)
    return comp;
  }

  // Implementation of java.util.Comparator

  private boolean testDeps(String[] imp, String[] dep) {
    if (dep != null) {
      Arrays.sort(dep);
      for (String im : imp) {
        if (Arrays.binarySearch(dep, im) >= 0) {
          return true;
        } // end of if (Arrays.binarySearch(deps2, im) >= 0)
      } // end of for ()
    } // end of if (dep == null)
    return false;
  }

  /**
   * Describe <code>compare</code> method here.
   *
   * @param object an <code>Object</code> value
   * @param object1 an <code>Object</code> value
   * @return an <code>int</code> value
   */
  public int compare(final TestIfc test1, final TestIfc test2) {
//  		System.err.println("NEW RUN");
// 		System.err.println("Comparing impl: " + Arrays.toString(test1.implemented())
// 			+ " with deps: " + Arrays.toString(test2.depends()));
    if (testDeps(test1.implemented(), test2.depends())) {
      return -1;
    } // end of if (testDeps(test1.implements(), test2.depends()))
// 		System.err.println("Comparing impl: " + Arrays.toString(test1.implemented())
// 			+ " with deps: " + Arrays.toString(test2.optional()));
    if (testDeps(test1.implemented(), test2.optional())) {
      return -1;
    } // end of if (testDeps(test1.implemented(), test2.optional())
// 		System.err.println("Comparing impl: " + Arrays.toString(test2.implemented())
// 			+ " with deps: " + Arrays.toString(test1.depends()));
    if (testDeps(test2.implemented(), test1.depends())) {
      return 1;
    } // end of if (testDeps(test1.implements(), test2.depends()))
// 		System.err.println("Comparing impl: " + Arrays.toString(test2.implemented())
// 			+ " with deps: " + Arrays.toString(test1.optional()));
    if (testDeps(test2.implemented(), test1.optional())) {
      return 1;
    } // end of if (testDeps(test1.implemented(), test2.optional())
    return 0;
  }

} // DependsComparator
