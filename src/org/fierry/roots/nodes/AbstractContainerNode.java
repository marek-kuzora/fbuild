package org.fierry.roots.nodes;

import java.util.Collection;
import java.util.Map;

import org.fierry.build.project.Lang;
import org.fierry.build.yaml.ActionY;
import org.fierry.roots.api.IContainer;
import org.fierry.roots.api.IDeployable;
import org.fierry.roots.api.INode;
import org.fierry.roots.parser.Token.Action;

public abstract class AbstractContainerNode extends AbstractNode implements IContainer {
	
	public AbstractContainerNode(Action token, Map<String, String> args) {
		super(token, args);
	}

	@Override public String getUid() {
		assert parent instanceof IContainer;
		return ((IContainer) parent).getUid();
	}
	
	@Override public ActionY getProduction(String type) {
		assert parent instanceof IContainer;
		return ((IContainer) parent).getProduction(type);
	}
	
	@Override protected AbstractNode createNode(Action token, Map<String, String> args) {
		String type = token.getType();
		
		if(type.equals("if")) { return new ConditionNode(token, args); }
		if(type.equals("for")) { return new LoopNode(token, args); }
		if(type.equals("use")) { return new TemplateNode(token, args); }
		
		return new ActionNode(token, args);
	}
	
	protected String getDeployNodes(Lang lang) {
		return getDeployNodes(nodes, lang);
	}
	
	protected String getDeployNodes(Collection<INode> nodes, Lang lang) {
		StringBuilder builder = new StringBuilder();
		for(INode node : nodes) {
			if(node instanceof IDeployable) {
				((IDeployable) node).deploy(builder, lang);
			}
		}
		return builder.toString();
	}

}
