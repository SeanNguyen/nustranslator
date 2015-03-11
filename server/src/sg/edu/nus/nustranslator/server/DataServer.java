package sg.edu.nus.nustranslator.server;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.io.FileReader;

public class DataServer {
	public static void main(String args[]) throws Exception {
		System.out.println("START");
		System.out.println(System.getProperty("user.dir"));
		@SuppressWarnings("resource")
		ServerSocket serverSocket = new ServerSocket(Configurations.port);
		Socket socket = serverSocket.accept();
		System.out.println("GOT CONNECTION");
		
		PrintWriter out = new PrintWriter(socket.getOutputStream());
		Scanner scanner = new Scanner(new FileReader(".\\data.txt"));
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			out.println(line);
		}
		System.out.println("COMPLETE SENDING DATA");
		scanner.close();
		out.close();
	}
}
