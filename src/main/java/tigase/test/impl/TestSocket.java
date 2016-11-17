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
package tigase.test.impl;

import java.net.Socket;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.util.Map;
import tigase.test.TestAbstract;
import tigase.test.ResultCode;
import javax.management.Attribute;
import tigase.xml.Element;
import tigase.test.util.Params;
import tigase.test.util.SocketXMLIO;

/**
 * Describe class TestScoket here.
 *
 *
 * Created: Tue May 17 20:19:38 2005
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public class TestSocket extends TestAbstract {

	/**
	 * <code>RECEIVE_BUFFER_SIZE</code> defines a size for TCP/IP packets.
	 * XMPP data packets are quite small usually, below 1kB so we don't need
	 * big TCP/IP data buffers.
	 */
	private static final int RECEIVE_BUFFER_SIZE = 2*1024;
  private String hostname = null;
	private String serverip = null;
  private int port = 5222;
  private int socket_wait = 5000;
	private boolean ignore_presence = false;

  /**
   * Creates a new <code>TestScoket</code> instance.
   *
   */
  public TestSocket() {
    super(
      new String[]
      {"jabber:client", "jabber:server", "jabber:component:accept"},
      new String[] {"socket"},
      null,
      null
      );
  }

  public String nextElementName(final Element reply) {
    return null;
  }

  public String getElementData(final String element) {
    return null;
  }

  public String[] getRespElementNames(final String element) {
    return null;
  }

  public Attribute[] getRespElementAttributes(final String element) {
    return null;
  }

  public String[] getRespOptionalNames(final String element) {
    return null;
  }

  // Implementation of TestIfc

  static long connectCounter = 0;
  static long connectAverTime = 0;

  /**
   * Describe <code>run</code> method here.
   *
   * @return a <code>boolean</code> value
   */
  public boolean run() {
    try {
      //      debug("socket create...\n");
      Socket client = new Socket();
      //      debug("socket setting reuse address to true...\n");
      client.setReuseAddress(true);
      //      debug("socket setting so timeout...\n");
      client.setSoTimeout(socket_wait);
			client.setReceiveBufferSize(RECEIVE_BUFFER_SIZE);
      //      debug("socket connecting...\n");
      //      System.out.println("socket-wait = " + socket_wait);
      long timeStart = System.currentTimeMillis();
      client.connect(new InetSocketAddress(serverip, port), socket_wait);
      long timeEnd = System.currentTimeMillis();
      long connectTime = timeEnd - timeStart;
      connectAverTime = (connectAverTime + connectTime) / 2;
      if (++connectCounter % 10 == 0) {
        debug("Socket connect aver time: " + connectAverTime + " ms\n");
      } // end of if (timeEnd - timeStart > 1000)
      //      debug("socket done.");
      params.put("socket", client);
			SocketXMLIO socket = new SocketXMLIO(client);
			socket.setIgnorePresence(ignore_presence);
      params.put("socketxmlio", socket);
      return true;
    } catch (SocketTimeoutException e) {
      resultCode = ResultCode.PROCESSING_EXCEPTION;
      exception = e;
      return false;
    } catch (Exception e) {
      resultCode = ResultCode.PROCESSING_EXCEPTION;
      exception = e;
      e.printStackTrace();
			System.out.println("IP: " + serverip + ", PORT:" + port);
      return false;
    } // end of try-catch
  }

  /**
   * Describe <code>init</code> method here.
   *
   * @param map a <code>Map</code> value
   */
  public void init(final Params map, Map<String, String> vars) {
    super.init(map, vars);
		serverip = map.get("-serverip", "127.0.0.1");
		hostname = map.get("-host", "localhost");
    port = map.get("-port", port);
    socket_wait = params.get("-socket-wait", socket_wait);
		ignore_presence = params.get("-ignore-presence", ignore_presence);
	}

} // TestScoket
