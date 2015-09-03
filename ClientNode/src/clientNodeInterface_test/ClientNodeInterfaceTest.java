package clientNodeInterface_test;

import java.util.ArrayList;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import clientNodeInterface.ClientNodeInterface;
import communicationHub.ChannelController;
import communicationHub.NodeSocketConnectionManager;

public class ClientNodeInterfaceTest {

	NodeSocketConnectionManager server;
	ChannelController controller;
	int testPort = 30042;
	
	ArrayList<String> receivedData = new ArrayList<String>();
	
	public void receiveData(String data) {
		receivedData.add(data);
	}
	

	@Before
	public void setUp() {
		controller = new ChannelController();
		server = new NodeSocketConnectionManager(testPort,controller);

	}
	
	@Test
	public void testSocketNodeCommunication() throws JSONException {
		ClientNodeInterface testNode = new ClientNodeInterface("localhost",testPort,receivedData);
		
		testNode.sendDataToController("{\"register\":[\"a\"]}");
		
		String result = receivedData.remove(0);
		JSONAssert.assertEquals("{\"source\":\"ChannelController\",\"status\":\"registered\",\"nodeName\":\""
				+ testNode.getNodeName() + "\"}", result, true); 

	}

}
