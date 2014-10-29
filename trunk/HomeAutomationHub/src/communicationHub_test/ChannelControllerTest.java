package communicationHub_test;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import communicationHub.ChannelController;
import communicationHub.NodeInterfaceString;

public class ChannelControllerTest {

	private ChannelController controller;

	@Before
	public void setUp() {
		controller = new ChannelController();
	}

	@Test
	public void testRegisterChannel() throws Exception {
		NodeInterfaceString testInterface = new NodeInterfaceString(controller);

		testInterface.sendDataToController("{\"register\":[\"a\"]}");

		String result = testInterface.getDataFromController();
		JSONAssert.assertEquals("{\"source\":\"ChannelController\",\"status\":\"registered\",\"nodeName\":\""
				+ testInterface.getNodeName() + "\"}", result, true);
	}

	@Test
	public void testSendDataToChannel() throws Exception {
		NodeInterfaceString testInterface = new NodeInterfaceString(controller);

		testInterface.sendDataToController("{\"register\":[\"a\"]}");

		String result = testInterface.getDataFromController();
		JSONAssert.assertEquals("{\"source\":\"ChannelController\",\"status\":\"registered\",\"nodeName\":\""
				+ testInterface.getNodeName() + "\"}", result, true);

		testInterface.sendDataToController("{\"destination\":[\"a\"],\"data\":{\"test\":\"success\"}}");

		result = testInterface.getDataFromController();
		JSONAssert.assertEquals("{\"source\":\"a\",\"nodeName\":\"" + testInterface.getNodeName()
				+ "\",\"data\":{\"test\":\"success\"}}", result, true);
	}

	@Test
	public void testSendDataToDifferentChannel() throws Exception {
		NodeInterfaceString testInterface = new NodeInterfaceString(controller);

		testInterface.sendDataToController("{\"register\":[\"a\"]}");

		String result = testInterface.getDataFromController();
		JSONAssert.assertEquals("{\"source\":\"ChannelController\",\"status\":\"registered\",\"nodeName\":\""
				+ testInterface.getNodeName() + "\"}", result, true);

		testInterface.sendDataToController("{\"destination\":[\"b\"],\"data\":{\"test\":\"success\"}}");

		result = testInterface.getDataFromController();
		assertEquals(null, result);
	}

	@Test
	public void testSendDataToMultipleChannels() throws Exception {
		NodeInterfaceString testInterfaceA = new NodeInterfaceString(controller);
		NodeInterfaceString testInterfaceB = new NodeInterfaceString(controller);

		testInterfaceA.sendDataToController("{\"register\":[\"a\"]}");
		testInterfaceB.sendDataToController("{\"register\":[\"b\"]}");

		String result = testInterfaceA.getDataFromController();
		JSONAssert.assertEquals("{\"source\":\"ChannelController\",\"status\":\"registered\",\"nodeName\":\""
				+ testInterfaceA.getNodeName() + "\"}", result, true);

		result = testInterfaceB.getDataFromController();
		JSONAssert.assertEquals("{\"source\":\"ChannelController\",\"status\":\"registered\",\"nodeName\":\""
				+ testInterfaceB.getNodeName() + "\"}", result, true);

		// Send from A to A, should be received by A only
		testInterfaceA.sendDataToController("{\"destination\":[\"a\"],\"data\":{\"test\":\"success\"}}");

		result = testInterfaceA.getDataFromController();
		JSONAssert.assertEquals("{\"source\":\"a\",\"nodeName\":\"" + testInterfaceA.getNodeName()
				+ "\",\"data\":{\"test\":\"success\"}}", result, true);

		result = testInterfaceB.getDataFromController();
		assertEquals(null, result);

		// Send from A to B, should be received by B only
		testInterfaceA.sendDataToController("{\"destination\":[\"b\"],\"data\":{\"test\":\"success\"}}");

		result = testInterfaceA.getDataFromController();
		assertEquals(null, result);

		result = testInterfaceB.getDataFromController();
		JSONAssert.assertEquals("{\"source\":\"b\",\"nodeName\":\"" + testInterfaceA.getNodeName()
				+ "\",\"data\":{\"test\":\"success\"}}", result, true);

		// Send from B to A, should be received by A only
		testInterfaceB.sendDataToController("{\"destination\":[\"a\"],\"data\":{\"test\":\"success\"}}");

		result = testInterfaceA.getDataFromController();
		JSONAssert.assertEquals("{\"source\":\"a\",\"nodeName\":\"" + testInterfaceB.getNodeName()
				+ "\",\"data\":{\"test\":\"success\"}}", result, true);

		result = testInterfaceB.getDataFromController();
		assertEquals(null, result);
	}
}
