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

import java.net.Socket;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import tigase.test.TestIfc;
import tigase.util.ClassUtil;

/**
 * Describe class TestUtil here.
 * 
 * 
 * Created: Sat May 28 08:15:38 2005
 * 
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public class TestUtil {

	private static Set<TestIfc> all_tests = null;
	private static List<String> daemons_jids = new CopyOnWriteArrayList<String>();
	private static Map<String, Socket> daemons = new ConcurrentHashMap<String, Socket>();
	private static Queue<Socket> active_connections = new ConcurrentLinkedQueue<Socket>();
	private static Random rand = new Random(System.currentTimeMillis());
	private static int seq = 0;

	/**
	 * Creates a new <code>TestUtil</code> instance.
	 * 
	 */
	private TestUtil() {
	}

	public static void addActiveConnection(Socket conn) {
		active_connections.add(conn);
	}

	public static void addDaemonJID(String jid, Socket sock) {
		daemons_jids.add(jid);
		daemons.put(jid, sock);
	}

	public static void shutdownAllDaemons() {
		for (String jid : daemons_jids) {
			removeDaemonJID(jid);
		}
	}

	public static boolean removeDaemonJID(String jid) {
		Socket soc = daemons.remove(jid);
		try {
			soc.close();
		} catch (Exception e) {
		} // end of try-catch
		return daemons_jids.remove(jid);
	}

	public static String getRandomJID() {
		String jid = null;
		while (jid == null && daemons_jids.size() > 0) {
			jid = daemons_jids.get(rand.nextInt(daemons_jids.size()));
			Socket sock = daemons.get(jid);
			if (!sock.isConnected()) {
				removeDaemonJID(jid);
				jid = null;
			} // end of if (!sock.isConnected())
		} // end of while (jid == null && daemons_jids.size() > 0)
		return jid;
	}

	public static String getSeqJID() {
		String jid = null;
		if (seq + 1 >= daemons_jids.size()) {
			seq = 0;
			jid = daemons_jids.get(daemons_jids.size() - 1);
		} else {
			jid = daemons_jids.get(seq++);
		} // end of else
		Socket sock = daemons.get(jid);
		if (!sock.isConnected()) {
			removeDaemonJID(jid);
			jid = getSeqJID();
		} // end of if (!sock.isConnected())
		return jid;
	}

	public static Set<TestIfc> getTests() throws Exception {
		if (all_tests == null) {
			all_tests = ClassUtil.getImplementations(TestIfc.class);
		} // end of if (tests == null)
		return all_tests;
	}

	private static LinkedList<TestIfc> calculateDependsTree(String[] test_ns,
			List<TestIfc> tests, Params params) throws Exception {

		for (int i = 0; i < test_ns.length; ++i) {
			if (test_ns[i].equals("auth")) {
				test_ns[i] = params.get("-def-auth", "auth-sasl");
			} // end of if (test_ns[i].equals("auth"))
			if (test_ns[i].equals("stream-open")) {
				test_ns[i] = params.get("-def-stream", "stream-client");
			} // end of if (test_ns[i].equals("auth"))
		} // end of for (int = 0; < i; ++)
		LinkedList<TestIfc> result = new LinkedList<TestIfc>();
		ListIterator<TestIfc> it = tests.listIterator();
		while (it.hasNext()) {
			TestIfc test = it.next();
			String[] impl_ns = test.implemented();
			Arrays.sort(impl_ns);
			for (String ns : test_ns) {
				if (Arrays.binarySearch(impl_ns, ns) >= 0) {
					result.add(test.getClass().newInstance());
					it.remove();
					break;
				} // end of if (Arrays.binarySearch(impl_ns, ns) >= 0)
			} // end of for ()
		} // end of for ()
		// System.err.println("Results: " + result.toString());
		List<TestIfc> tmp = new LinkedList<TestIfc>();
		for (TestIfc test : result) {
			if (test.depends() != null) {
				// System.err.println("Depends on: " + Arrays.toString(test.depends()));
				List<TestIfc> deps = calculateDependsTree(test.depends(), tests, params);
				if (deps != null) {
					// System.err.println("New deps: " + deps.toString());
					tmp.addAll(0, deps);
				} // end of if (deps != null)
			} // end of if (test.depends() != null)
		} // end of for ()
		result.addAll(0, tmp);
		Collections.sort(result, DependsComparator.getInstance());
		return result;
	}

	public static LinkedList<TestIfc> getDependsTree(String[] test_ns, Params params)
			throws Exception {

		if (all_tests == null) {
			all_tests = ClassUtil.getImplementations(TestIfc.class);
		} // end of if (tests == null)
		LinkedList<TestIfc> result =
				calculateDependsTree(test_ns, new LinkedList<TestIfc>(all_tests), params);
		// System.err.println(result.toString());
		return result;
	}

	public static String toStringArrayNS(String[] arr, String delim) {
		StringBuilder sb = new StringBuilder();
		if (arr != null) {
			for (String ns : arr) {
				sb.append(" xmlns='" + ns + "'" + delim);
			} // end of for ()
		} // end of if (arr != null)
		return sb.toString();
	}

	public static String toStringCollNS(Collection<TestIfc> list) {
		LinkedList<String> elems = new LinkedList<String>();
		for (TestIfc test : list) {
			String[] xmlns = test.implemented();
			if (xmlns != null) {
				for (String ns : xmlns) {
					elems.add(ns);
				} // end of for ()
			} // end of if (nss != null)
		} // end of for ()
		String[] arr = elems.toArray(new String[elems.size()]);
		return toStringArrayNS(arr, "\n");
	}

	public static void debug(String msg, boolean deb) {
		if (deb) {
			System.out.print(msg);
			System.out.flush();
		} // end of if (debug)
	}

	public static void debug(Exception exc, boolean deb) {
		if (deb && exc != null) {
			exc.printStackTrace();
		} // end of if (deb)
	}

	public static String stack2String(Exception exc) {
		StringBuilder sb = new StringBuilder();
		for (StackTraceElement ste : exc.getStackTrace()) {
			sb.append(ste.toString() + "\n");
		} // end of for ()
		return sb.toString();
	}

	public static void displayStats(String user_name, long tests_ok, long tests_er,
			long total_time, long total_successful) {
		debug(user_name + ": Successful tests             : " + tests_ok + ", errors: "
				+ tests_er + ".\n", true);
		debug(user_name + ": Total tests time             : " + (total_time / 1000)
				+ "sec.\n", true);
		debug(user_name + ": All successful tests time    : " + (total_successful / 1000)
				+ "sec.\n", true);
		if (tests_ok > 0) {
			debug(user_name + ": Average successful test time : "
					+ (total_successful / tests_ok) + "ms.\n\n", true);
		} // end of if (tests_ok > 0)
	}

} // TestUtil
