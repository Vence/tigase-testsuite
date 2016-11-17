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
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Describe class HTMLContentFilter here.
 *
 *
 * Created: Thu Jun  9 20:10:31 2005
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public class HTMLContentFilter implements OutputFilter {

  private BufferedWriter bw  = null;
  private long start = 0;

  /**
   * Creates a new <code>HTMLContentFilter</code> instance.
   *
   */
  public HTMLContentFilter() { }

  // Implementation of tigase.test.util.OutputFilter

  /**
   * Describe <code>init</code> method here.
   *
   * @param out a <code>BufferedWriter</code> value
   * @param title a <code>String</code> value
   * @param description a <code>String</code> value
   */
  public void init(final BufferedWriter out, final String title,
    final String description) throws IOException {
    bw = out;
    bw.write("  <h2>" + title + "</h2>\n");
    bw.write("  " + description + "\n");
    bw.write("  <p>Test start time: <b>" +
      DateFormat.getDateTimeInstance().format(new Date()) + "</b></p>\n");
    bw.flush();
    start = System.currentTimeMillis();
  }

  public void addContent(String content) throws IOException {
    bw.write(content);
    bw.flush();
  }

  /**
   * Describe <code>close</code> method here.
   *
   */
  public void close(String closingInfo) throws IOException {
    bw.write("  </table>\n");
    bw.write("  </p>\n");
    bw.write("  <p>Test end time: <b>" +
      DateFormat.getDateTimeInstance().format(new Date()) + "</b></p>\n");
    Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    cal.setTimeInMillis(System.currentTimeMillis() - start);
    //cal.computeFields();
    bw.write("  <p>Total test time:");
    bw.write(" " + cal.get(Calendar.HOUR_OF_DAY) + " hours");
    bw.write(", " + cal.get(Calendar.MINUTE) + " minutes");
    bw.write(", " + cal.get(Calendar.SECOND) + " seconds");
    bw.write(", " + cal.get(Calendar.MILLISECOND) + " ms.</p>");
    bw.write(closingInfo);
    bw.flush();
    bw.close();
  }

  /**
   * Describe <code>setColumnHeaders</code> method here.
   *
   * @param hd a <code>String[]</code> value
   */
  public void setColumnHeaders(final String ... hd) throws IOException {
    bw.write("  <p>\n");
    bw.write("  <table border='1' style='min-width: 800px;'>\n");
//    bw.write("  <table width=\"800\" border='1'>\n");
    bw.write("   <thead valign='middle'>\n");
    bw.write("    <tr>\n");
    for (String header : hd) {
        if (header.equals("Test name") || header.equals("History")) {
            bw.write("     <th width='200px'>" + header + "</th>\n");
        } else {
            bw.write("     <th>" + header + "</th>\n");
            
        }
    } // end of for ()
    bw.write("    </tr>\n");
    bw.write("   </thead>\n");
    bw.flush();
  }

  /**
   * Describe <code>addRow</code> method here.
   *
   * @param cols a <code>String[]</code> value
   */
  public void addRow(final String ... cols) throws IOException {
    bw.write("   <tr valign=\"top\">\n");
    for (String col : cols) {
      bw.write("    <td>" + col + "</td>\n");
    } // end of for ()
    bw.write("   </tr>\n");
    bw.flush();
  }

} // HTMLContentFilter