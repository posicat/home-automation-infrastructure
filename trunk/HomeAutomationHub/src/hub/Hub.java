package hub;

public class Hub {

	public static void main(String[] args) {
		ConnectionManager server = new ConnectionManager(10042);
		new Thread(server).start();

		System.out.println("Listening...");
		try {
			while(!server.isStopped()) {
				Thread.sleep(1000);
			}
		} catch (InterruptedException e) {
		    e.printStackTrace();
		}
		System.out.println("Stopping Server");
		server.stop();
	}

}
