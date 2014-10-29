package communicationHub;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

public class ChannelController {

	// Data packet field names
	public static final String NODE_CHANNEL_IDENTIFIER 	= "source";
	public static final String NODE_DATA_DESTINATION 	= "destination";
	public static final String NODE_DATA_BLOCK 			= "data";
	public static final String NODE_REGISTER_CHANNELS 	= "register";
	public static final String NODE_STATUS_BLOCK 		= "status";
	public static final String NODE_ERROR_MESSAGE 		= "error";
	public static final String NODE_NODE_NAME 			= "nodeName";

	// Channel constants
	public static final String NODE_CHANNEL_CONTROLLER 	= "ChannelController";
	public static final String NODE_SEND_TO_ALL_ADDRESS = "all";

	Hashtable<String, ArrayList<NodeInterface>> channels = new Hashtable<String, ArrayList<NodeInterface>>();
	ArrayList<NodeInterface> allNodes = new ArrayList<NodeInterface>();

	public void addNodeToChannel(String channel, NodeInterface node) {
		ArrayList<NodeInterface> nodes = channels.get(channel);
		nodes = addNodeToList(nodes, node);
		channels.put(channel, nodes);
	}

	public void removeNodeFromChannel(String channel, NodeInterface node) {
		ArrayList<NodeInterface> nodes = channels.get(channel);
		removeNodeFromList(nodes, node);
		channels.put(channel, nodes);
	}

	public void addNode(NodeInterface node) {
		addNodeToList(allNodes, node);
	}

	public void removeNode(NodeInterface node) {
		removeNodeFromList(allNodes, node);
	}

	private ArrayList<NodeInterface> removeNodeFromList(ArrayList<NodeInterface> nodes, NodeInterface node) {
		if (nodes == null) {
			nodes = new ArrayList<NodeInterface>();
		}
		nodes.remove(node);
		return nodes;
	}

	private ArrayList<NodeInterface> addNodeToList(ArrayList<NodeInterface> nodes, NodeInterface node) {
		if (nodes == null) {
			nodes = new ArrayList<NodeInterface>();
		}
		if (!nodes.contains(node)) {
			nodes.add(node);
		}
		return nodes;
	}

	public void processIncomingData(String data, NodeInterface fromNode) {
		String errors = "";
		try {

			JSONObject json = null;
			json = new JSONObject(data);

			if (json.has(NODE_REGISTER_CHANNELS)) {
				if (json.has(NODE_NODE_NAME)) {
					fromNode.setNodeName(json.getString(NODE_NODE_NAME));
				}else{
					fromNode.setNodeName(UUID.randomUUID().toString());
				}
				
				JSONArray registerChannels = json.getJSONArray(NODE_REGISTER_CHANNELS);
				for (int i = 0; i < registerChannels.length(); i++) {
					addNodeToChannel(registerChannels.getString(i), fromNode);
				}
				
				JSONObject jsonOut = new JSONObject();
				jsonOut.put(NODE_STATUS_BLOCK, "registered");
				jsonOut.put(NODE_CHANNEL_IDENTIFIER, NODE_CHANNEL_CONTROLLER);
				jsonOut.put(NODE_NODE_NAME, fromNode.getNodeName());
				fromNode.sendDataToNode(jsonOut.toString());
			}

			if (json.has(NODE_DATA_DESTINATION) && json.has(NODE_DATA_BLOCK)) {
				JSONArray channels = json.getJSONArray(NODE_DATA_DESTINATION);
				JSONObject channelData = json.getJSONObject(NODE_DATA_BLOCK);
				for (int i = 0; i < channels.length(); i++) {
					String channel = channels.getString(i);
					JSONObject jsonOut = new JSONObject();
					jsonOut.put(NODE_NODE_NAME, fromNode.getNodeName());
					jsonOut.put(NODE_DATA_BLOCK, channelData);
					jsonOut.put(NODE_CHANNEL_IDENTIFIER, channel);
					sendToChannel(channel, jsonOut.toString());
				}
			}
		} catch (Exception e) {
			errors += e.getMessage();
		}

		if (errors != "") {
			try {
				JSONObject jsonOut = new JSONObject();
				jsonOut.put(NODE_ERROR_MESSAGE, errors);
				fromNode.sendDataToNode(jsonOut.toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void sendToChannel(String channel, String data) throws Exception {
		ArrayList<NodeInterface> nodes;
		if (NODE_SEND_TO_ALL_ADDRESS.equals(channel)) {
			nodes = allNodes;
		} else {
			nodes = channels.get(channel);
		}
		if (nodes != null) {
			for (NodeInterface node : nodes) {
				node.sendDataToNode(data);
			}
		}
	}
}
