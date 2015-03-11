package sg.edu.nus.nustranslator.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class MultiThreadedServer {

	protected ServerSocket serverSocket = null;
	protected boolean running = true;

	// Constructor
	public MultiThreadedServer() {
	}

	//Initiation
	public static void main(String args[]) {
		MultiThreadedServer server = new MultiThreadedServer();
		server.start();
	}
	
	// public methods
	public void start() {
		openServerSocket();
		System.out.println("Server Started");
		//read file data
		String data = getData();
		
		//handling requests
		while (this.running) {
			Socket clientSocket = null;
			try {
				clientSocket = this.serverSocket.accept();
				System.out.println("Accepted New Request");
			} catch (IOException e) {
				if (!this.running) {
					System.out.println("Server Stopped.");
					return;
				}
				throw new RuntimeException("Error accepting client connection", e);
			}
			new Thread(new WorkerRunnable(clientSocket, data)).start();
		}
		System.out.println("Server Stopped.");
	}

	public void stop() {
		this.running = false;
		try {
			this.serverSocket.close();
		} catch (IOException e) {
			throw new RuntimeException("Error closing server", e);
		}
	}
	
	//private helper methods
	private void openServerSocket() {
		try {
			this.serverSocket = new ServerSocket(Configurations.port);
		} catch (IOException e) {
			throw new RuntimeException("Cannot open port", e);
		}
	}
	
	private String getData() {
		String data = "";
		try {
			@SuppressWarnings("resource")
			Scanner scanner = new Scanner(new FileReader(".\\data.txt"));
			while (scanner.hasNextLine()) {
				data += scanner.nextLine() + String.format("%n");
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		return data;
	}

	//Private Class
	private class WorkerRunnable implements Runnable {

		protected Socket clientSocket = null;
		protected String data = null;

		public WorkerRunnable(Socket clientSocket, String data) {
			this.clientSocket = clientSocket;
			this.data = data;
		}

		public void run() {
			try {
				DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
				out.writeBytes(data);
				System.out.println("Finished Tranfering data");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}

