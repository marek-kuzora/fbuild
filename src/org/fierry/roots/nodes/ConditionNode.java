package org.fierry.roots.nodes;

import java.util.Map;

import org.fierry.build.project.Lang;
import org.fierry.build.utils.Template;
import org.fierry.roots.api.IDeployable;
import org.fierry.roots.parser.Token.Action;

public class ConditionNode extends AbstractContainerNode implements IDeployable {

	private String value;
	
	public ConditionNode(Action token, Map<String, String> args) {
		super(token, args);
		this.value = token.getData();
	}
	
	@Override public void deploy(StringBuilder builder, Lang lang) {
		Template.get("nodes/condition", lang)
				.replace("value", value)
				.replaceLine("nodes", getDeployNodes(lang))
				.appendTo(builder);
	}
}
