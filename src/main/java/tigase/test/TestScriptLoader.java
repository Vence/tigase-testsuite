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

import static tigase.test.util.TestUtil.debug;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import tigase.test.parser.TestNode;
import tigase.test.parser.TestScript;
import tigase.test.util.HTMLContentFilter;
import tigase.test.util.HTMLFilter;
import tigase.test.util.NullFilter;
import tigase.test.util.OutputFilter;
import tigase.test.util.Params;

/**
 * Describe class TestScriptLoader here. Created: Sat Jul 9 00:38:27 2005
 * 
 * @author <a href="mailto:artur.hefczyc@tigase.org">Artur Hefczyc</a>
 * @version $Rev$
 */
public class TestScriptLoader implements HistoryCollectorIfc {

	private TestScript parser = null;
	private Params params = null;
	private List<TestNode> testNodes = null;
	private String scriptName = "xmpp-tests.xmpt";
	private File dir_name = new File("");
	private OutputFilter filter = null;
	private int output_cols = 5;
	private boolean stop_on_fail = false;
	private boolean output_history = false;
	private JobControll jobContr = new JobControll();
	private Test multiResCont = null;

	/**
	 * Creates a new <code>TestScriptLoader</code> instance.
	 * 
	 * @param params
	 */
	public TestScriptLoader(Params params) {
		this.params = params;
	}

	@SuppressWarnings({ "unchecked" })
	public void loadTests() throws Exception {
		scriptName = params.get("-script", scriptName);
		debug("Script name: " + scriptName + "\n", true);
		parser =
				new TestScript(new InputStreamReader(new FileInputStream(scriptName), "UTF-8"));
		parser.Input();
		testNodes = parser.getTests();
		Params pars = new Params(parser.getGlobalPars());
		pars.putAll(params);
		params = pars;
		// System.out.println(params.toString());
		// System.exit(1);
		stop_on_fail = params.get("-stop-on-fail", false);
	}

	public void runTests() throws Exception {
		filter = initOutputFilter();
		for (TestNode node : testNodes) {
			// node.addGlobalPars(parser.getGlobalPars());
			boolean result = runTest(node);
			if (stop_on_fail && !result) {
				break;
			} // end of if (stop_on_fail)
		} // end of for (TestNode node : testNodes)
		// jobContr.allDone();
		closeFilter(filter);
	}

	@SuppressWarnings({ "unchecked" })
	private boolean runTest(TestNode node) throws Exception {
		// Load included script files if any....
		if (node.getPars().containsKey("-include-script")) {
			String[] files = node.getPars().get("-include-script").split(",");
			for (String file : files) {
				debug("Loading tests from external file: " + file + "\n", true);
				TestScript script_parser =
						new TestScript(new InputStreamReader(new FileInputStream(file), "UTF-8"));
				script_parser.Input();
				List<TestNode> tests = script_parser.getTests();
				for (TestNode child : tests.get(0).getChildren()) {
					// debug("Adding external test: " + child.getName() + "\n", true);
					// Skip local preparation test cases....
					String nodeName = child.getName();
					if (!"Prepare".equalsIgnoreCase(nodeName)) {
						String append = node.getPars().get("-name-append");
						if (append != null) {
							nodeName += " (" + append + ")";
							child.setName(nodeName);
						}
						Map<String, String> nodePars = node.getNodePars();
						// System.out.println(nodePars.toString());
						nodePars.remove("-include-script");
						child.addPars(nodePars);
						// child.addVars(node.getVars());
						node.addChild(child);
					}
				}
			}
		}

		node.addGlobalPars(params.getMap());
		node.addGlobalVars(parser.getGlobalVars());
		if (node.getPars().containsKey("-multi-thread")) {
			debug(node.getName() + ": " + getDescription(node) + " ... ", true);
			multiResCont = new Test(node);
			multiResCont.getParams().put("-loop", 0);
			runMultiTest(node);
			jobContr.allDone(0);
			calculateResult(multiResCont);
			return multiResCont.getResult();
		} // end of if (test.getParams().containsKey("-multithread"))
		boolean loop_only = node.getPars().containsKey("-loop-only");
		boolean result = true;
		if (node.getId() != null) {
			debug(node.getName() + ": " + getDescription(node) + " ... ", true);
			multiResCont = new Test(node);
			multiResCont.runTest(this);
			calculateResult(multiResCont);
			result = multiResCont.getResult();
		}
		if (stop_on_fail && !result) {
			return result;
		} // end of if (stop_on_fail)
		int loop_end = 1;
		int loop_start = 0;
		if (loop_only) {
			try {
				loop_end = Integer.parseInt(node.getPars().get("-loop"));
			} catch (Exception e) {
				loop_end = 1;
			}
			try {
				loop_start = Integer.parseInt(node.getPars().get("-loop-start"));
			} catch (Exception e) {
				loop_start = 0;
			}
		}
		// System.out.println(node.getId() + ", loop_end: " + loop_end);
		for (int i = loop_start; i < loop_end; i++) {
			node.addPar("$(outer-loop)", "" + (i + 1));
			if (node.getChildren() != null) {
				for (TestNode child : node.getChildren()) {
					result = runTest(child);
					if (stop_on_fail && !result) {
						return result;
					} // end of if (stop_on_fail)
					if (node.getPars().containsKey("-delay")) {
						long delay = Long.decode(node.getPars().get("-delay").toString());
						if (delay > 0) {
							try {
								Thread.sleep(delay);
							} catch (InterruptedException e) {
							} // end of try-catch
						} // end of if (delay > 0)
					} // end of if (main_params.containsKey("-delay"))
				} // end of for (TestNode child: node.getChildren())
			} // end of if (node.getChildren() != null)
		}
		return result;
	}

	private boolean runMultiTest(TestNode node) throws IOException {
		if (node.getId() != null) {
			Test test = new Test(node);
			ThreadTest tt = new ThreadTest(test);
			new Thread(tt).start();
		}
		if (node.getChildren() != null) {
			for (TestNode child : node.getChildren()) {
				runMultiTest(child);
				if (node.getPars().containsKey("-delay")) {
					long delay = Long.decode(node.getPars().get("-delay").toString());
					if (delay > 0) {
						try {
							Thread.sleep(delay);
						} catch (InterruptedException e) {
						} // end of try-catch
					} // end of if (delay > 0)
				} // end of if (main_params.containsKey("-delay"))
			} // end of for (TestNode child: node.getChildren())
		} // end of if (node.getChildren() != null)
		return true;
	}

	@Override
	public synchronized void handleHistoryEntry(HistoryEntry historyEntry) {
		if (output_history) {
			multiResCont.getHistory().add(historyEntry);
		} // end of if (output_history)
	}

	private synchronized void calculateMultiResult(Test test) {
		Params multiParams = multiResCont.getParams();
		multiParams.put("-loop",
				multiParams.get("-loop", 0) + test.getParams().get("-loop", 1));
		multiResCont.addTestsTotalTime(test.getTestsTotalTime());
		multiResCont.addSuccessfulTotalTime(test.getSuccessfulTotalTime());
		multiResCont.addTestsOK(test.getTestsOK());
		multiResCont.addTestsErr(test.getTestsErr());
		// if (output_history) {
		// multiResCont.getHistory().addAll(test.getHistory());
		// } // end of if (output_history)
		multiResCont.setResult((multiResCont.getTestsOK() > multiResCont.getTestsErr()));
	}

	private void calculateResult(Test test) throws IOException {
		debugResult(test);
		if (!test.getParams().containsKey("-no-record")) {
			filterResult(test);
		} // end of if (!test.getParams().containsKey("-no-record"))
	}

	private void debugResult(Test test) {
		if (test.getResult()) {
			debug("success,  ", true);
		} else {
			debug(
					"FAILURE,  " + (test.debug_on_error ? "(" + test.getErrorMsg() + "),  " : ""),
					true);
		} // end of if (test.getResult()) else
		if (test.getTestsOK() > 0) {
			int loop = test.getParams().get("-loop", 1);
			if (loop > 1) {
				debug(
						"Total: "
								+ test.getTestsTotalTime()
								+ "ms, Average: "
								+ (test.getSuccessfulTotalTime() / test.getTestsOK())
								+ (test.getParams().get("-multi-thread") == null ? "ms, Loop: "
										: "ms, Tests: ") + loop + ", OK: " + test.getTestsOK() + "\n", true);
			} else {
				debug("Total: " + test.getTestsTotalTime() + "ms\n", true);
			} // end of else
		} else {
			debug("Total: " + test.getTestsTotalTime() + "ms\n", true);
		} // end of else
	}

	private void filterResult(Test test) throws IOException {
		String test_result = null;
		if (test.getResult()) {
			test_result = "success";
		} else {
			test_result = "FAILURE";
		} // end of if (test.getResult()) else
		String hist_file = test.getName().replace(' ', '_') + ".xml";
		if (output_history) {
			saveHistory(test, new File(dir_name, hist_file));
		} // end of if (!params.get("-output-history", true))
		long average =
				test.getTestsOK() > 0 ? (test.getSuccessfulTotalTime() / test.getTestsOK()) : 0;
		switch (output_cols) {
			case 7:
				filter.addRow(test.getName(), "<b>" + test_result + "</b>",
						"" + (test.getTestsTotalTime() / 1000) + " sec", "" + test.getTestsOK(),
						"<b>" + average + "</b> ms", test.getDescription(), "<a href='" + hist_file
								+ "'>" + test.getName() + "</a>");
				break;
			case 6:
				filter.addRow(test.getName(), "<b>" + test_result + "</b>",
						"" + (test.getTestsTotalTime() / 1000) + " sec", "" + test.getTestsOK(),
						"<b>" + average + "</b> ms", test.getDescription());
				break;
			case 5:
				filter.addRow(test.getName(), "<b>" + test_result + "</b>",
						"<b>" + test.getTestsTotalTime() + "</b> ms", test.getDescription(),
						"<a href='" + hist_file + "'>" + test.getName() + "</a>");
				break;
			case 4:
			default:
				filter.addRow(test.getName(), "<b>" + test_result + "</b>",
						"" + test.getTestsTotalTime() + " ms", test.getDescription());
				break;
		} // end of switch (output_cols)
	}

	private OutputFilter initOutputFilter() throws IOException {
		OutputFilter filter = null;
		if ((params.get("-output-format") != null)
				&& (params.get("-output-format", "html").equals("html") || params.get(
						"-output-format", "html").equals("html-content"))) {
			File file_name = new File(params.get("-output-file", "functional-tests.html"));
			dir_name = file_name.getAbsoluteFile().getParentFile();
			BufferedWriter bw = new BufferedWriter(new FileWriter(file_name, false));
			if (params.get("-output-format", "html").equals("html")) {
				filter = new HTMLFilter();
			} else {
				filter = new HTMLContentFilter();
			}
			filter.init(bw, params.get("-title", ""), getDescription());
			Map<String, String> ver_map = getVersion();
			if ((ver_map != null) && (ver_map.size() > 0)) {
				filter.addContent("   <h3>Server version info:</h3>\n");
				filter.addContent("   <table>\n");
				filter.addContent("    <tr valign=\"top\">" + "<td width=\"15%\">Name:</td>"
						+ "<td width=\"3%\">&nbsp;</td>" + "<td width=\"2%\">&nbsp;</td>"
						+ "<td width=\"80%\"><b>" + ver_map.get("Name") + "</b></td></tr>\n");
				filter.addContent("    <tr valign=\"top\">" + "<td width=\"15%\">Version:</td>"
						+ "<td width=\"3%\">&nbsp;</td>" + "<td width=\"2%\">&nbsp;</td>"
						+ "<td width=\"80%\"><b>" + ver_map.get("Version") + "</b></td></tr>\n");
				filter.addContent("    <tr valign=\"top\">" + "<td width=\"15%\">OS:</td>"
						+ "<td width=\"3%\">&nbsp;</td>" + "<td width=\"2%\">&nbsp;</td>"
						+ "<td width=\"80%\"><b>" + ver_map.get("OS") + "</b></td></tr>\n");
				filter.addContent("    <tr valign=\"top\">" + "<td width=\"15%\">&nbsp;</td>"
						+ "<td width=\"3%\">&nbsp;</td>" + "<td width=\"2%\">&nbsp;</td>"
						+ "<td width=\"80%\">&nbsp;</td></tr>\n");
				filter.addContent("    <tr valign=\"top\">" + "<td width=\"15%\">Local IP:</td>"
						+ "<td width=\"3%\">&nbsp;</td>" + "<td width=\"2%\">&nbsp;</td>"
						+ "<td width=\"80%\"><b>" + ver_map.get("Local IP") + "</b></td></tr>\n");
				filter.addContent("    <tr valign=\"top\">" + "<td width=\"15%\">Remote IP:</td>"
						+ "<td width=\"3%\">&nbsp;</td>" + "<td width=\"2%\">&nbsp;</td>"
						+ "<td width=\"80%\"><b>" + ver_map.get("Remote IP") + "</b></td></tr>\n");
				filter.addContent("   </table>\n");
			}
			List<StatItem> stat_list = getConfiguration();
			filter.addContent(outputStatistics(stat_list, "before"));
			filter.addContent("   <h3>Tests results:</h3>\n");
			output_cols = params.get("-output-cols", 5);
			output_history = params.get("-output-history", true);
			if (!output_history) {
				--output_cols;
			} // end of if (!params.get("-output-history", true))
			switch (output_cols) {
				case 7:
					filter.setColumnHeaders("Test name", "Result", "Total time", "OK", "Average",
							"Description", "History");
					break;
				case 6:
					filter.setColumnHeaders("Test name", "Result", "Total time", "OK", "Average",
							"Description");
					break;
				case 5:
					filter.setColumnHeaders("Test name", "Result", "Test time", "Description",
							"History");
					break;
				case 4:
				default:
					filter.setColumnHeaders("Test name", "Result", "Test time", "Description");
					break;
			} // end of switch (output_cols)
		} else {
			filter = new NullFilter();
		} // end of else
		return filter;
	}

	private void closeFilter(OutputFilter filter) throws IOException {
		filter.close(outputStatistics(getStatistics(), "after"));
	}

	@SuppressWarnings({ "unchecked" })
	private Map<String, String> getVersion() {
		TestNode node = getTestNode("Version");
		if (node != null) {
			node.addGlobalPars(params.getMap());
			node.addGlobalVars(parser.getGlobalVars());
			Test tmp_test = new Test(node);
			tmp_test.runTest(this);
			return (Map<String, String>) tmp_test.getParams().get("Version");
		} // end of if (node != null)
		return null;
	}

	private String outputStatistics(List<StatItem> stats, String when) {
		StringBuilder sb = new StringBuilder();
		if ((stats != null) && (stats.size() > 0)) {
			if (when.equals("before")) {
				sb.append("      <h3>Server basic configuration parameters:</h3>\n");
			} else {
				sb.append("      <h3>Server stats " + when + " test:</h3>\n");
			}
			sb.append("   <table>\n");
			for (StatItem item : stats) {
				// Configuration results attach one field which is readonly
				// with warning information about changing parameters
				// We don't really want this warning to be displayed in report.
				if (!item.getDescription().equals("Note")) {
					sb.append("    <tr valign=\"top\">" + "<td width=\"20%\">" + item.getName()
							+ ":</td>" + "<td width=\"20%\">" + item.getDescription() + "</td>"
							+ "<td width=\"2%\">&nbsp;</td>" + "<td width=\"58%\"><b>"
							+ item.getValue() + "</b></td></tr>\n");
				}
			} // end of for ()
			sb.append("   </table>\n");
			sb.append("  </p>\n");
		}
		return sb.toString();
	}

	@SuppressWarnings({ "unchecked" })
	private List<StatItem> getStatistics() {
		TestNode node = getTestNode("Statistics");
		if (node != null) {
			node.addGlobalPars(params.getMap());
			node.addGlobalVars(parser.getGlobalVars());
			Test tmp_test = new Test(node);
			tmp_test.runTest(this);
			return (List<StatItem>) tmp_test.getParams().get("Statistics");
		} // end of if (node != null)
		return null;
	}

	@SuppressWarnings({ "unchecked" })
	private List<StatItem> getConfiguration() {
		TestNode node = getTestNode("Configuration");
		if (node != null) {
			node.addGlobalPars(params.getMap());
			node.addGlobalVars(parser.getGlobalVars());
			Test tmp_test = new Test(node);
			tmp_test.runTest(this);
			return (List<StatItem>) tmp_test.getParams().get("Configuration");
		} // end of if (node != null)
		return null;
	}

	private TestNode getTestNode(String testName) {
		for (TestNode node : testNodes) {
			if (testName.equalsIgnoreCase(node.getName())) {
				return node;
			} // end of if (testName.equalsIgnoreCase(node.getName()))
		} // end of for ()
		return null;
	}

	private String getDescription() {
		StringBuilder sb = new StringBuilder();
		sb.append("<b>\n");
		sb.append("   <ol>\n");
		for (TestNode node : testNodes) {
			String descr = node.getLongDescr();
			sb.append("    <li>" + descr.substring(2, descr.length() - 2) + "</li>\n");
		} // end of for (TestNode node : testNodes)
		sb.append("   </ol>\n");
		sb.append("  </b>\n");
		return sb.toString();
	}

	private void saveHistory(Test test, File file_name) throws IOException {
		if (test.getHistory() != null) {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file_name, false));
			bw.write("<test-history test='" + test.getName() + "'>\n");
			for (HistoryEntry entry : test.getHistory()) {
				bw.write(entry.toString() + "\n");
			} // end of for ()
			bw.write("\n</test-history>");
			bw.close();
		}
	}

	private String getDescription(TestNode node) {
		String descr = node.getShortDescr();
		if ((descr == null) || descr.trim().equals("")) {
			String ldescr = node.getLongDescr();
			descr = ldescr.substring(2, ldescr.length() - 2);
		} // end of if (descr == null || descr.trim().equals(""))
		return descr;
	}

	private class JobControll {
		private int running = 0;
		private Lock lock = new ReentrantLock();
		private Condition done = lock.newCondition();

		public boolean allDone(long sleep) throws InterruptedException {
			lock.lock();
			try {
				if (sleep > 0) {
					Thread.sleep(sleep);
				}
				while (running > 0) {
					done.await();
				}
				return true;
			} finally {
				lock.unlock();
			} // end of try-finally
		}

		public void threadStarted() {
			lock.lock();
			try {
				++running;
			} finally {
				lock.unlock();
			} // end of try-finally
		}

		public void threadDone() {
			lock.lock();
			try {
				--running;
				if (running <= 0) {
					done.signal();
				} // end of if (running <= 0)
			} finally {
				lock.unlock();
			} // end of try-finally
		}

	}

	private class ThreadTest implements Runnable {
		private Test test = null;

		public ThreadTest(Test test) {
			jobContr.threadStarted();
			this.test = test;
		}

		// Implementation of java.lang.Runnable

		/**
		 * Describe <code>run</code> method here.
		 */
		public void run() {
			test.runTest(TestScriptLoader.this);
			try {
				calculateMultiResult(test);
			} // end of try
			catch (Exception e) {
				e.printStackTrace();
			} // end of try-catch
			jobContr.threadDone();
		}

	}

} // TestScriptLoader
