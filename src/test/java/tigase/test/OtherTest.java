package tigase.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class OtherTest {
	
	@org.junit.Test
	public void test() throws IOException{
		//客户端 
		//1、创建客户端Socket，指定服务器地址和端口 
		Socket socket =new Socket("192.168.72.130",5222); 
		//2、获取输出流，向服务器端发送信息 
		OutputStream os = socket.getOutputStream();//字节输出流 
		PrintWriter pw =new PrintWriter(os);//将输出流包装成打印流 
		
//		String sendMsg = "<message to=\"admin@182.168.72.130/wwl\" type=\"chat\">"
//				+ "<body>Test message no. 1, from: all-xmpp-test@test-d/def-user-resource.</body>"
//				+ "</message>"	;
		
		String sendMsg = " <stream:stream xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams' to='192.168.72.130' version='1.0'>";
		pw.write(sendMsg); 
		pw.flush(); 
		socket.shutdownOutput(); 
		//3、获取输入流，并读取服务器端的响应信息 
		InputStream is = socket.getInputStream(); 
		BufferedReader br = new BufferedReader(new InputStreamReader(is)); 
		String info = null; 
		while((info=br.readLine())!= null){ 
		 System.out.println("接收报文<<："+info); 
		} 
		 
		//4、关闭资源 
		br.close(); 
		is.close(); 
		pw.close(); 
		os.close(); 
		socket.close(); 
		
		try {
			Thread.sleep(500000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
