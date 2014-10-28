package hub;

public abstract class NodeInterface implements Runnable {

	private boolean running = false;
	private ChannelController controller = null;
	protected String nodeName;

	public NodeInterface(ChannelController controller) {
		super();
		this.controller = controller;
		this.controller.addNode(this);
	}

	public void shutdown() {
		this.controller.removeNode(this);
	}

	public void run() {
		this.running = true;
		watchForData();
		this.running = false;
		shutdown();
	}
	
	public void stop() {
		this.running = false;
	}

	public boolean isRunning() {
		return running;
	}

	public void sendDataToController(String data, NodeInterface fromNode) {
		this.controller.processIncomingData(data, fromNode);
		
	}

	// Abstract Methods
	public abstract void watchForData();

	public abstract void sendDataToNode(String data) throws Exception;

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}


}