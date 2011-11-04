package org.fierry.roots.nodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fierry.build.linking.GlobalConfig;
import org.fierry.roots.api.IDeployableRoot;
import org.fierry.roots.api.INode;
import org.fierry.roots.api.IParameter;
import org.fierry.roots.parser.Token;
import org.fierry.roots.parser.Token.Action;

public abstract class AbstractNode implements INode {

	protected String type;
	protected INode parent;
	protected GlobalConfig config;

	protected List<INode> nodes;
	protected List<String> tags;
	protected Map<String, String> args;
	
	public AbstractNode(Token.Action token, Map<String, String> args) {
		this.args = args;
		this.type = token.getType();
		this.tags = token.getTags();
		
		this.nodes = new ArrayList<INode>();
	}
	
	@Override public INode addNode(Action token) {
		INode node = createNode(token, new HashMap<String, String>(args));
		nodes.add(node);
		
		return node;
	}
	
	abstract protected AbstractNode createNode(Token.Action token, Map<String, String> args);


	@Override public void addArgument(String name, String value) {
		args.put(name, value);
	}

	@Override public void configure() {
		for(INode node : nodes) {
			node.configure();
		}
		consumeParameters();
	}
	
	private void consumeParameters() {
		Collection<IParameter> params = new ArrayList<IParameter>();
		
		for(INode node : nodes) {
			if(node instanceof IParameter) { params.add((IParameter)node); }
		}
		
		for(IParameter param : params) {
			param.configureParent(this);
			nodes.remove(param);
		}
	}

	@Override public void consult(INode parent, IDeployableRoot root, GlobalConfig config) {
		this.parent = parent;
		this.config = config;
		
		for(INode node : nodes) {
			node.consult(this, root, config);
		}
	}

}
