package hub;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class NodeSocketConnectionManager implements Runnable {

	protected int serverPort;
	protected ServerSocket serverSocket = null;
	protected boolean isStopped = false;
	protected Thread runningThread = null;
	protected ChannelController controller;

	public NodeSocketConnectionManager(int port, ChannelController controller) {
		this.serverPort = port;
		this.controller = controller;
	}

	public void run() {
		synchronized (this) {
			this.runningThread = Thread.currentThread();
		}
		openServerSocket();
		while (!isStopped()) {
			Socket clientSocket = null;
			try {
				clientSocket = this.serverSocket.accept();
			} catch (IOException e) {
				if (isStopped()) {
					System.out.println("Server Stopped.");
					return;
				}
				throw new RuntimeException("Error accepting client connection", e);
			}
			new Thread(new NodeInterfaceSocket(clientSocket, controller)).start();
		}
		System.out.println("Server Stopped.");
		this.stop();
	}

	synchronized boolean isStopped() {
		return this.isStopped;
	}

	public synchronized void stop() {
		this.isStopped = true;
		try {
			this.serverSocket.close();
		} catch (IOException e) {
			throw new RuntimeException("Error closing server", e);
		}
	}

	private void openServerSocket() {
		try {
			this.serverSocket = new ServerSocket(this.serverPort);
		} catch (IOException e) {
			throw new RuntimeException("Cannot open port " + this.serverPort, e);
		}
	}

}