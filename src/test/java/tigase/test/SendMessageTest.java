package tigase.test;

import tigase.test.util.Params;

public class SendMessageTest {
	
	@org.junit.Test
	public void sendMessage() throws Exception{
		  Params params = new Params("-script D:/myscript.xmpt D:/test1.html");
		  TestScriptLoader tl = new TestScriptLoader(params);
		  tl.loadTests();
		  tl.runTests();
		  
		  Thread.sleep(500000);
	}

}
