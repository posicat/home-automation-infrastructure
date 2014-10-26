package hub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ChannelController {

	// Data packet field names
	public static final String NODE_SOURCE_IDENTIFIER = "source";
	public static final String NODE_DATA_DESTINATION = "destination";
	public static final String NODE_DATA_BLOCK = "data";
	public static final String NODE_REGISTER_CHANNELS = "register";
	public static final String NODE_ERROR_MESSAGE = "error";

	// Channel constants
	public static final String NODE_SEND_TO_ALL_ADDRESS = "all";
	
	Hashtable<String, ArrayList<NodeController>> channels = new Hashtable<String, ArrayList<NodeController>>();
	ArrayList<NodeController> allNodes = new ArrayList<NodeController>();

	public void addNodeToChannel(String channel, NodeController node) {
		ArrayList<NodeController> nodes = channels.get(channel);
		nodes = addNodeToList(nodes, node);
		channels.put(channel, nodes);
	}

	public void removeNodeFromChannel(String channel, NodeController node) {
		ArrayList<NodeController> nodes = channels.get(channel);
		removeNodeFromList(nodes, node);
		channels.put(channel, nodes);
	}

	public void addNode(NodeController node) {
		addNodeToList(allNodes, node);
	}

	public void removeNode(NodeController node) {
		removeNodeFromList(allNodes, node);
	}

	private ArrayList<NodeController> removeNodeFromList(
			ArrayList<NodeController> nodes, NodeController node) {
		if (nodes == null) {
			nodes = new ArrayList<NodeController>();
		}
		nodes.remove(node);
		return nodes;
	}

	private ArrayList<NodeController> addNodeToList(
			ArrayList<NodeController> nodes, NodeController node) {
		if (nodes == null) {
			nodes = new ArrayList<NodeController>();
		}
		if (!nodes.contains(node)) {
			nodes.add(node);
		}
		return nodes;
	}

	public void processIncomingData(String data, NodeController fromNode) {
		String errors = "";
		try {

			JSONObject json = null;
			json = new JSONObject(data);

			if (json.has(NODE_REGISTER_CHANNELS)) {
				JSONArray registerChannels = json
						.getJSONArray(NODE_REGISTER_CHANNELS);
				for (int i = 0; i < registerChannels.length(); i++) {
					addNodeToChannel(registerChannels.getString(i), fromNode);
				}
			}

			if (json.has(NODE_DATA_DESTINATION) && json.has(NODE_DATA_BLOCK)) {
				JSONArray channels = json.getJSONArray(NODE_DATA_DESTINATION);
				JSONObject channelData = json.getJSONObject(NODE_DATA_BLOCK);
				for (int i = 0; i < channels.length(); i++) {
					String channel = channels.getString(i);
					JSONObject jsonOut = new JSONObject();
					jsonOut.put(NODE_DATA_BLOCK, channelData);
					jsonOut.put(NODE_SOURCE_IDENTIFIER, channel);
					sendToChannel(channel, jsonOut.toString() + "\r\n");
				}
			}
		} catch (JSONException e1) {
			errors += e1.getMessage() + "\r\n";
		} catch (IOException e) {
			errors += e.getMessage() + "\r\n";
		}

		if (errors != "") {
			try {
				JSONObject jsonOut = new JSONObject();
				jsonOut.put(NODE_ERROR_MESSAGE, errors);
				fromNode.sendData(jsonOut.toString() + "\r\n");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void sendToChannel(String channel, String data) throws IOException {
		ArrayList<NodeController> nodes;
		if (NODE_SEND_TO_ALL_ADDRESS.equals(channel)) {
			nodes = allNodes;
		} else {
			nodes = channels.get(channel);
		}
		if (nodes != null) {
			for (NodeController node : nodes) {
				node.sendData(data);
			}
		}
	}
}
