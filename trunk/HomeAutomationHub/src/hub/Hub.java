package hub;

public class Hub {

	private static ChannelController controller = new ChannelController();
	
	public static void main(String[] args) {
		NodeSocketConnectionManager server = new NodeSocketConnectionManager(10042,controller);
		new Thread(server).start();

		System.out.println("Listening...");
		try {
			while (!server.isStopped()) {
				Thread.sleep(100);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Stopping Server");
		server.stop();
	}

}
