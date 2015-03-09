package sg.edu.nus.nustranslator.server;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class TestClient {

	public static void main (String[] args) throws UnknownHostException, IOException, InterruptedException {
		System.out.println("THIS IS CLIENT");
		@SuppressWarnings("resource")
		Socket socket = new Socket("localhost", 50050);
		PrintStream out = new PrintStream(socket.getOutputStream());
		int count = 0;
		while(true) {
			out.println("Number: " + count++ + "\n");
			System.out.print("Number: " + count + "\n");
			Thread.sleep(100);
		}
	}
}
