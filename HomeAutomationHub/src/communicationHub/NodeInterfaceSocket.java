package communicationHub;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class NodeInterfaceSocket extends NodeInterface {

	protected Socket clientSocket = null;
	private OutputStream output;
	private Scanner input;

	public NodeInterfaceSocket(Socket clientSocket, ChannelController controller) {
		super(controller);
		this.clientSocket = clientSocket;
	}

	@Override
	public void watchForData() {
		try {
			output = clientSocket.getOutputStream();
			input = new Scanner(clientSocket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		while (super.isRunning()) {
			try {
				super.sendDataToController(input.nextLine(), this);
			} catch (NoSuchElementException e) {
				return;
			}
		}
	}

	@Override
	public void sendDataToNode(String data) throws IOException {
		output.write((data+"\r\n").getBytes());
	}
}
