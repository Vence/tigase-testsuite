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

import java.io.FileInputStream;
import java.net.Socket;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import javax.management.Attribute;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import tigase.test.TestAbstract;
import tigase.test.util.Params;
import tigase.test.util.SocketXMLIO;
import tigase.xml.Element;

/**
 * Describe class TestSSL here.
 *
 *
 * Created: Tue May 17 20:26:41 2005
 *
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public class TestSSL extends TestAbstract {

  private String hostname = "localhost";
  private String keys_password = "keystore";
  private String trusts_password = "truststore";
  private String keys_file = "certs/keystore";
  private String trusts_file = "certs/truststore";

  private String[] elems = {"stream:stream"};
  private int counter = 0;

  private SSLSocketFactory getSSLSocketFactory() throws Exception {
    SecureRandom sr = new SecureRandom();
    sr.nextInt();
//     KeyStore keys = KeyStore.getInstance("JKS");
//     keys.load(new FileInputStream(keys_file), keys_password.toCharArray());
//     KeyStore trusts = KeyStore.getInstance("JKS");
//     trusts.load(new FileInputStream(trusts_file), trusts_password.toCharArray());
//     KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
//     kmf.init(keys, keys_password.toCharArray());
//     TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
//     tmf.init(trusts);
//     SSLContext sslContext = SSLContext.getInstance("SSL");
//     sslContext.init(kmf.getKeyManagers(),
//       new X509TrustManager[] {new FakeTrustManager()}, sr);
    SSLContext sslContext = SSLContext.getInstance("SSL");
    sslContext.init(null,
      new X509TrustManager[] {new FakeTrustManager()}, sr);
    return sslContext.getSocketFactory();
  }

  private Socket initSSLSocket(Socket socket) throws Exception {
    SSLSocketFactory factory = getSSLSocketFactory();
    socket.setSoTimeout(5000);
    SSLSocket tlsClient =
      (SSLSocket)factory.createSocket(socket,
        socket.getInetAddress().getHostAddress(),
        socket.getPort(), true);
    tlsClient.setUseClientMode(true);
    tlsClient.startHandshake();
    return tlsClient;
  }

  /**
   * Creates a new <code>TestSSL</code> instance.
   *
   */
  public TestSSL() {
    super(
      new String[]
      {"jabber:client", "jabber:server", "jabber:component:accept"},
      new String[] {"ssl-init"},
      new String[] {"socket"},
      null
      );
    try {
      // Just init SSL Engine to avoid delay during test
      getSSLSocketFactory();
    } catch (Exception e) { } // end of try-catch
  }

  public String nextElementName(final Element reply) throws Exception {
    if (counter < elems.length) {
      Socket sock = initSSLSocket((Socket)params.get("socket"));
      params.put("socket", sock);
      params.put("socketxmlio", new SocketXMLIO(sock));
      return elems[counter++];
    } // end of if (counter < elems.length)
    return null;
  }

  public String getElementData(final String element) {
    if (element.equals("stream:stream")) {
      return "<stream:stream "
        + "xmlns='jabber:client' "
        + "xmlns:stream='http://etherx.jabber.org/streams' "
        + "to='" + hostname + "' "
        + "version='1.0'>";
    }
    return null;
  }

  public String[] getRespElementNames(final String element) {
    if (element.equals("stream:stream")) {
      return new String[] {"stream:stream", "stream:features"};
    }
    return null;
  }

  public Attribute[] getRespElementAttributes(final String element) {
    if (element.equals("stream:stream")) {
      return new Attribute[]
      {
        new Attribute("xmlns", "jabber:client"),
        new Attribute("xmlns:stream", "http://etherx.jabber.org/streams"),
        new Attribute("from", hostname),
        new Attribute("version", "1.0")
      };
    }
    if (element.equals("stream:features")) {
      return new Attribute[] { };
    }
    return null;
  }

  public String[] getRespOptionalNames(final String element) {
    return null;
  }

  // Implementation of TestIfc

  /**
   * Describe <code>init</code> method here.
   *
   * @param params a <code>Params</code> value
   */
  public void init(final Params params, Map<String, String> vars) {
    super.init(params, vars);
    hostname = params.get("-host", hostname);
    keys_password = params.get("-keys-file-password", keys_password);
    trusts_password = params.get("-trusts-file-password", trusts_password);
    keys_file = params.get("-keys-file", keys_file);
    trusts_file = params.get("-trusts-file", trusts_file);
  }

  class FakeTrustManager implements X509TrustManager {

    private X509Certificate[] acceptedIssuers = null;

    public FakeTrustManager(X509Certificate[] ai) {
      acceptedIssuers = ai;
    }

    public FakeTrustManager() { }

    // Implementation of javax.net.ssl.X509TrustManager

    /**
     * Describe <code>checkClientTrusted</code> method here.
     *
     * @param x509CertificateArray a <code>X509Certificate[]</code> value
     * @param string a <code>String</code> value
     * @exception CertificateException if an error occurs
     */
    public void checkClientTrusted(final X509Certificate[] x509CertificateArray,
      final String string) throws CertificateException {
    }

    public void checkServerTrusted(final X509Certificate[] x509CertificateArray,
      final String string) throws CertificateException {
    }

    public X509Certificate[] getAcceptedIssuers() {
      return acceptedIssuers;
    }

  }

} // TestSSL