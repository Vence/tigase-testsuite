package tigase.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import tigase.test.util.ScriptFileLoader;
import tigase.test.util.ScriptFileLoader.StanzaEntry;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author andrzej
 */
public class ValidateDataFilesTest {
	
	@org.junit.Test
	public void testValidation() throws IOException {
		Queue<StanzaEntry> parsed = new ArrayDeque<>();
		Map<String,String> replaces = new HashMap<>();
		File dir = new File("tests/data");
		for (File f : dir.listFiles()) {
			if (!f.getName().endsWith(".cot"))
				continue;
			
			ScriptFileLoader loader = new ScriptFileLoader(f.getAbsolutePath(), parsed, replaces);
			loader.loadSourceFile();
		}
		assertTrue(true);
	}
	
}
