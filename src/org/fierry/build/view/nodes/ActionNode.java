package org.fierry.build.view.nodes;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.fierry.build.utils.Lines;
import org.fierry.build.utils.Resource;
import org.fierry.build.utils.Uid;
import org.fierry.build.view.core.Deployable;
import org.fierry.build.view.core.Node;
import org.fierry.build.view.core.Root;
import org.fierry.build.view.core.UidCapable;
import org.fierry.build.view.parser.Token;
import org.fierry.build.view.parser.Token.Action;

public class ActionNode extends MultilineNode implements Deployable, UidCapable {

	protected String uid;
	
	public ActionNode(Token.Action token, Map<String, String> args) {
		super(token, args);
	}
	
	@Override public Node createNode(Action token, Map<String, String> args) {
		return new ActionNode(token, args);
	}
	
	@Override public void consult(Node parent, Root root) {
		assert parent instanceof UidCapable : "Parent is expected to implement UidCapable interface.";
		uid = ((UidCapable) parent).generateUid(this);
		
		super.consult(parent, root);
	}
	
	@Override public String generateUid(ActionNode node) {
		int idx = nodes.indexOf(node);
		assert idx != -1;
		
		return "'" + Uid.generate(idx) + "'";
	}

	@Override public void deploy(StringBuilder builder) {
		
		Resource.get("action_deploy")
				.replace("type", type)
				.replace("uid", uid)
				.replaceLine("value", getDeployValue())
				.replaceLine("nodes", getDeployNodes())
				.appendTo(builder);
	}
	
	private String getDeployValue() {
		return StringUtils.join(data, Lines.separator);
	}
	
	private String getDeployNodes() {
		StringBuilder builder = new StringBuilder();
		for(Node node : nodes) {
			if(node instanceof Deployable) {
				((Deployable) node).deploy(builder);
			}
		}
		return builder.toString();
	}
}
