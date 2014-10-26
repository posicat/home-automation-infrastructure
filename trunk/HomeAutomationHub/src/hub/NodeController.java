package hub;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class NodeController implements Runnable {

	public static int MODE_UNDEFINED = 0;
	public static int MODE_PERSISTENT_SOCKET = 100;
	public static int MODE_PERSISTENT_HTTP = 101;
	public static int MODE_TRANSIENT_SOCKET = 200;
	public static int MODE_TRANSIENT_HTTP = 201;

	protected Socket clientSocket = null;
	protected int connectionMode = 0;
	protected ChannelController controller = null;
	protected boolean running = false;
	private OutputStream output;
	private Scanner input;

	public NodeController(Socket clientSocket, int mode, ChannelController controller) {
		if (mode == MODE_PERSISTENT_SOCKET) {
			this.clientSocket = clientSocket;
			this.connectionMode = mode;
			this.controller = controller;
			this.controller.addNode(this);
		}
	}

	public void run() {
		this.running = true;
		try {
			output = clientSocket.getOutputStream();
			input = new Scanner(clientSocket.getInputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		watchInput();
	}

	private void watchInput() {
		while (running) {
			try {
				String data = input.nextLine();
				controller.processIncomingData(data, this);
			} catch(NoSuchElementException e) {
				running=false;
				return;
			}
		}
	}

	public void sendData(String data) throws IOException {
		output.write(data.getBytes());
	}
}
