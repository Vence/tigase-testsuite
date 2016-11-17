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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import tigase.test.parser.TestNode;
import tigase.test.util.Params;
import tigase.xml.Element;
import java.util.concurrent.CountDownLatch;
import tigase.test.util.XMLIO;

import static tigase.test.util.TestUtil.*;

/**
 * Describe class Test here.
 * 
 * 
 * Created: Sat May 28 08:05:39 2005
 * 
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public class Test {

	public static final String NS_SEP = ";";

	private String testName = null;
	private Map<String, String> pars = null;
	private Params main_params = null;
	private String description = null;
	private String[] test_ns = null;
	private boolean result = false;
	private String errorMsg = null;
	private Exception exception = null;
	private HistoryCollectorIfc historyColl = null;
	private int tests_ok = 0;
	private int tests_er = 0;
	private long total_time = 0;
	private long total_successful = 0;
	private boolean collectHistory = false;
	private LinkedList<HistoryEntry> history = new LinkedList<HistoryEntry>();
	private boolean on_one_socket = false;
	private boolean active_connection = false;
	private Element lastResult = null;
	private Test onError = null;
	private TestNode node = null;
	private boolean debug = false;
	private CountDownLatch latch = null;
	protected boolean debug_on_error = false;
	protected boolean last_result = false;
	private boolean stop_on_fail = false;

	private static TimerThread[] backroundTasks = new TimerThread[10000];
	private static int timer_idx = 0;

	// static {
	// for (int i = 0; i < backroundTasks.length; i++) {
	// backroundTasks[i] = new Timer("Background Timer " + i, false);
	// }
	// }

	public Test(TestNode node) {
		this.node = node;
		testName = node.getName();
		test_ns = node.getId() != null ? node.getId().split(NS_SEP) : null;
		pars = node.getPars();
		main_params = new Params(pars);
		description = node.getShortDescr();
		if (description == null || description.trim().equals("")) {
			String ldescr = node.getLongDescr();
			if (ldescr != null) {
				description = ldescr.substring(2, ldescr.length() - 2);
			} // end of if (ldescr != null)
		} // end of if (descr == null || descr.trim().equals(""))
		if (node.getOnError() != null) {
			onError = new Test(node.getOnError());
		} // end of if (node.getOnError() != null)

		if (!main_params.isFalse("-output-history") && !main_params.containsKey("-daemon")
				&& !main_params.containsKey("-background")) {
			collectHistory = true;
			// history = new LinkedList<HistoryEntry>();
		} // end of if (!main_params.isFalse())
	}

	private void initParams() {
		on_one_socket = main_params.containsKey("-on-one-socket");
		active_connection =
				main_params.containsKey("-active-connection")
						|| main_params.containsKey("-background");
	}

	public void runTest(HistoryCollectorIfc historyColl) {
				if ( debug ){
					debug( "\n\n\n===========================", debug );
				}
		this.historyColl = historyColl;
		initParams();
		debug = main_params.containsKey("-debug");
		debug_on_error =
				(main_params.containsKey("-debug-on-error") && !main_params
						.containsKey("-no-record"));
		stop_on_fail = main_params.get("-stop-on-fail", false);
		int loop = main_params.get("-loop", 1);
		String deb_name =
				"" + (node.getParent() != null ? node.getParent().getName() : "null") + "/"
						+ getName();
		latch = new CountDownLatch(loop);
		if (node.getParent() != null
				&& node.getParent().getPars().get("$(outer-loop)") != null
				&& !node.getParent().getPars().containsKey("-multi-thread")) {
			node.addVar("$(outer-loop)", node.getParent().getPars().get("$(outer-loop)"));
			// if (getName().equals("Multi 7")) {
			// System.out.println("\n\n" + deb_name +
			// ": Setting variable $(outer-loop) = " +
			// node.getParent().getPars().get("$(outer-loop)"));
			// }
		}
		// System.out.println(main_params.toString());
		int loop_start = 1;
		if (main_params.get("-loop-start") != null && node.getParent() != null
				&&
				// main_params.get("-loop-start").equals("$(outer-loop)") &&
				node.getParent().getPars().get("$(outer-loop)") != null
				&& !node.getParent().getPars().containsKey("-multi-thread")) {
			try {
				loop_start =
						Integer.parseInt(node.getParent().getPars().get("$(outer-loop)")) + 1;
			} catch (Exception e) {
				loop_start = 1;
			}
			// main_params.put("-loop-start",
			// node.getParent().getPars().get("$(outer-loop)"));
			// if (getName().equals("Multi 7")) {
			// System.out.println(deb_name + ": Setting property -loop-start = " +
			// node.getParent().getPars().get("$(outer-loop)"));
			// }
		} else {
			loop_start = main_params.get("-loop-start", 0);
		}
		// if (getName().equals("Multi 7")) {
		// System.out.println("" + deb_name + ": loop_start = " + loop_start);
		// }
		// if (node.getParent() != null) {
		// System.out.println(node.getParent().getPars().toString());
		// }
		// System.out.println(main_params.toString());
		// String user_name = (String)main_params.get("-user-name");
		boolean loop_user_name = true;
		// if (loop > 1) {
		// loop_user_name = true;
		// }
		// if (user_name != null && user_name.contains("$(loop)")) {
		// loop_user_name = true;
		// user_name = user_name.replace("$(loop)", "");
		// } // end of if (user_name != null && user_name.contains("$(loop)"))
		long loop_delay = 0;
		if (main_params.containsKey("-loop-delay")) {
			loop_delay = main_params.get("-loop-delay", 10);
		} // end of if (main_params.containsKey("-delay"))
		LinkedList<TestIfc> suite = new LinkedList<TestIfc>();
		Params test_params = null;
		long all_tests_start_time = System.currentTimeMillis();
		// System.out.println("Test name: " + testName + ", loop: " + loop
		// + ", lopp_start: " + loop_start);
		for (int cnt = loop_start; cnt < loop + loop_start; cnt++) {
			try {
				node.addPar("$(outer-loop)", "" + cnt);
				node.addVar("$(loop)", "" + cnt);
				if (on_one_socket && cnt > 0 && last_result) {
					LinkedList<TestIfc> suite_tmp = getDependsTree(test_ns, test_params);
					suite.clear();
					suite.addAll(suite_tmp.subList(suite_tmp.size() - 1, suite_tmp.size()));
				} else {
					test_params = new Params();
					test_params.putAll(main_params);
					suite = getDependsTree(test_ns, test_params);
				} // end of else
				if (suite.size() == 0) {
					errorMsg =
							"No tests implementation found for given name space: " + node.getId();
					result = false;
					return;
				} // end of if (suite.size() == 0)
				if (loop_user_name) {
					for (Map.Entry<String, Object> entry : main_params.entrySet()) {
						if (entry.getValue() != null
								&& entry.getValue().toString().contains("$(loop)")) {
							test_params.put(entry.getKey(),
									entry.getValue().toString().replace("$(loop)", "" + cnt));
						}
					}
					// test_params.put("-user-name", user_name+cnt);
				} // end of if (loop_user_name)
				runTest(suite, test_params, node.getVars());
				if ((stop_on_fail || cnt > 10) && (tests_ok < tests_er)) {
					debug("Too many errors, stopping test...\n", debug);
					result = false;
					errorMsg =
							"Too many errors, stopping test: " + tests_ok + " OK, " + tests_er + " ER";
					return;
				} // end of if (cnt > 10 && (cnt / tests_err <= 2))
			} catch (Exception e) {
				result = false;
				errorMsg = e.getMessage();
				exception = e;
				// e.printStackTrace();
				return;
			} // end of try-catch
			if (loop_delay > 0) {
				try {
					Thread.sleep(loop_delay);
				} catch (InterruptedException e) {
				} // end of try-catch
			} // end of if (loop_delay > 0)
		} // end of for (int cnt = 0; cnt < loop; cnt++)
		try {
			latch.await();
		} catch (InterruptedException e) {
		} // end of try-catch
		main_params = test_params;
		result = tests_ok > tests_er;
		if (main_params.containsKey("-delay")) {
			long delay = main_params.get("-delay", 1000);
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
			} // end of try-catch
		} // end of if (main_params.containsKey("-delay"))
		total_time = System.currentTimeMillis() - all_tests_start_time;
	}

	private void runTest(LinkedList<TestIfc> suite, Params test_params,
			Map<String, String> vars) {
		boolean daemon = test_params.containsKey("-daemon");
		boolean background = test_params.containsKey("-background");
		long socket_wait = test_params.get("-socket-wait", 5000);
		DaemonTest dt = new DaemonTest(suite, test_params, this, vars);
		if (daemon) {
			runThread(dt, true);
			++tests_ok;
			latch.countDown();
		} else {
			if (background) {
				runTimerTask(dt);
			} else {
				dt.run();
				// dt.suite.get(0).release();
				latch.countDown();
			}
		} // end of if (daemon) else
	}

	private void runThread(DaemonTest task, boolean daemon) {
		Thread t = new Thread(task);
		t.setDaemon(daemon);
		t.start();
	}

	private void runTimerTask(DaemonTest task) {
		TimerThread timer = null;
		synchronized (backroundTasks) {
			timer = backroundTasks[timer_idx];
			if (timer == null) {
				timer = new TimerThread(timer_idx, false);
				backroundTasks[timer_idx] = timer;
			}
			if (++timer_idx >= backroundTasks.length) {
				timer_idx = 0;
			}
		}
		TimerTest tt = new TimerTest(task, timer);
		timer.schedule(tt, tt.repeat_wait, tt.repeat_wait);
	}

	public void handleResult(DaemonTest dt, boolean result) {
		if (result) {
			++tests_ok;
		} else {
			++tests_er;
			String on_error = (String) dt.params.get("-on-error");
			if (onError != null) {
				onError.runTest(historyColl);
			}
		} // end of if (this_result) else
	}

	public int getTestsOK() {
		return tests_ok;
	}

	public void addTestsOK(int tests) {
		tests_ok += tests;
	}

	public int getTestsErr() {
		return tests_er;
	}

	public void addTestsErr(int tests) {
		tests_er += tests;
	}

	public long getTestsTotalTime() {
		return total_time;
	}

	public void addTestsTotalTime(long time) {
		total_time += time;
	}

	public long getSuccessfulTotalTime() {
		return total_successful;
	}

	public void addSuccessfulTotalTime(long time) {
		total_successful += time;
	}

	public List<HistoryEntry> getHistory() {
		return history;
	}

	public Exception getException() {
		return exception;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public Element getLastResult() {
		return lastResult;
	}

	public boolean getResult() {
		return result;
	}

	public void setResult(boolean res) {
		result = res;
	}

	public void setName(String name) {
		testName = name;
	}

	public String getName() {
		return testName;
	}

	// public void setParams(Params params) {
	// this.params = params;
	// }

	public Params getParams() {
		return main_params;
	}

	public void setDescription(String descr) {
		description = descr;
	}

	public String getDescription() {
		return description;
	}

	class TimerThread extends Timer {
		private int idx = 0;
		private int counter = 0;

		public TimerThread(int idx, boolean daemon) {
			super("Background Timer " + idx, daemon);
			this.idx = idx;
		}

		public void schedule(TimerTask task, long delay, long period) {
			++counter;
			super.schedule(task, delay, period);
		}

		public void stopped() {
			--counter;
			if (counter <= 0) {
				backroundTasks[idx] = null;
				this.cancel();
			}
		}
	}

	class TimerTest extends TimerTask {

		private DaemonTest dt = null;
		private TimerThread tt = null;
		private long repeat_max = 1;
		private long repeat_wait = 1;
		private long counter = 0;
		private boolean failure = false;

		public TimerTest(DaemonTest dt, TimerThread tt) {
			this.dt = dt;
			this.tt = tt;
			repeat_max = dt.params.get("-repeat-script", 1);
			repeat_wait = dt.params.get("-repeat-wait", 1);
			// System.out.println("repeat_max = " + repeat_max
			// + ", repeat_wait = " + repeat_wait);
		}

		@Override
		public void run() {
			++counter;
			if (counter == 2 && dt.suite.size() > 1) {
				dt.suite.subList(0, dt.suite.size() - 1).clear();
			}
			if (counter >= 2) {
				failure = !dt.params.get("authorized", false);
			}
			// System.out.println("TimerTest run...");
			if (!failure) {
				dt.run();
			} else {
				System.out.println("Test run " + counter + " failed for user: "
						+ dt.params.get("-user-name", "uknown"));
				counter = repeat_max;
			}
			if (counter >= repeat_max) {
				cancel();
				tt.stopped();
				dt.suite.get(0).release();
				latch.countDown();
			}
		}
	}

	class DaemonTest implements Runnable, HistoryCollectorIfc {

		private List<TestIfc> suite = null;
		private Params params = null;
		private Map<String, String> vars = null;
		private boolean authorized = false;
		private Test resultsHandler = null;

		public DaemonTest(LinkedList<TestIfc> suite, Params params, Test resultsHandler,
				Map<String, String> vars) {
			this.suite = suite;
			this.params = params;
			this.vars = vars;
			this.resultsHandler = resultsHandler;
		}

		@Override
		public void handleHistoryEntry(HistoryEntry historyEntry) {
			if (collectHistory) {
				historyColl.handleHistoryEntry(historyEntry);
			} // end of if (history)
		}

		// Implementation of java.lang.Runnable

		/**
		 * Describe <code>run</code> method here.
		 * 
		 */
		@Override
		public void run() {
			long test_start_time = System.currentTimeMillis();
			TestIfc tmptest = null;
			try {
				for (TestIfc test : suite) {
					if ( debug ){
						debug( "\n", debug );
					}
					test.setHistoryCollector(this);
					debug("Testing: " + toStringArrayNS(test.implemented(), "..."), debug);
					tmptest = test;
					test.setName(getName());
					test.init(params, vars);
					boolean res = test.run();
					if (res) {
						lastResult = test.getLastResult();
						authorized = params.get("authorized", false);
						if (authorized) {
							synchronized (this) {
								this.notifyAll();
							}
						} // end of if (authorized)
						debug("     success!\n", debug);
						last_result = true;
					} else {
						errorMsg = test.getResultMessage();
						debug("       failure!\n", debug || debug_on_error);
						debug(
								"Error code: " + test.getResultCode() + ", error message: "
										+ test.getResultMessage() + "\n", debug || debug_on_error);
						// if (test.getHistory() != null) {
						// for (HistoryEntry he : test.getHistory()) {
						// debug("" + he.getDirection() + "\n"
						// + he.getContent() + "\n", debug || debug_on_error);
						// } // end of for ()
						// } // end of if (test.getHistory() != null)
						last_result = false;
						synchronized (this) {
							this.notifyAll();
						}
						// return;
					} // end of if (test.run()) else
				} // end of for ()
			} // end of try
			catch (Exception e) {
				e.printStackTrace();
				// System.out.println(Arrays.toString(tmptest.implemented()));
				// System.out.println(params.toString());
				exception = e;
				errorMsg = e.toString();
				last_result = false;
				params.put("authorized", false);
				synchronized (this) {
					this.notifyAll();
				}
				// return;
			} // end of try-catch
			if (!on_one_socket && !active_connection) {
				// System.out.println("Closing XMLIO socket.");
				try {
					((XMLIO) params.get("socketxmlio")).close();
				} catch (Exception e) {
					// e.printStackTrace();
				}
			}
			synchronized (this) {
				this.notifyAll();
			}
			resultsHandler.handleResult(this, last_result);
			long this_test = System.currentTimeMillis() - test_start_time;
			total_time += this_test;
			total_successful += this_test;
		}

		public boolean getResult() {
			return result;
		}

		public boolean isAuthorized() {
			return authorized;
		}

	}

} // Test
