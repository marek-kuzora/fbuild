package org.fierry.build.view.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fierry.build.view.parser.Token;

public abstract class Node {

	protected String type;
	protected List<Node> nodes;
	protected List<String> tags;
	protected Map<String, String> args;
	
	public Node(Token.Action token, Map<String, String> args) {
		this.args  = args;
		this.type  = token.getType();
		this.tags  = token.getTags();
		this.nodes = new ArrayList<Node>();
	}
	
	public void addArgument(String name, String value) {
		args.put(name, value);
	}
	
	public Node addNode(Token.Action token) {
		Node node = createNode(token, new HashMap<String, String>(args));
		nodes.add(node);
		return node;
	}
	
	abstract public Node createNode(Token.Action token, Map<String, String> args);
	
	public void consult(Node parent, Root root) {
		for(Node node : nodes) {
			node.consult(this, root);
		}
	}
}
