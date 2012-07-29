package org.fierry.roots.nodes;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.fierry.build.linking.GlobalConfig;
import org.fierry.build.project.Lang;
import org.fierry.build.utils.Template;
import org.fierry.roots.api.IContainer;
import org.fierry.roots.api.IDeployable;
import org.fierry.roots.api.IDeployableRoot;
import org.fierry.roots.api.INode;
import org.fierry.roots.parser.Token.Action;

public class TemplateNode extends AbstractContainerNode implements IDeployable {

	private String uid;
	private String world;
	private String variable;
	
	public TemplateNode(Action token, Map<String, String> args) {
		super(token, args);
		
		String  raw = StringUtils.normalizeSpace(token.getData());
		Integer idx = raw.indexOf(':');
		
		variable = raw.substring(0, idx).trim();
		world    = raw.substring(idx + 1).trim();
	}
	
	@Override protected AbstractNode createNode(Action token, Map<String, String> args) {
		throw new IllegalArgumentException("/require cannot contain any nodes.");
	}
	
	@Override public void consult(INode parent, IDeployableRoot root, GlobalConfig config) {
		assert parent instanceof IContainer;
		
		uid = ((IContainer) parent).getUid();
		super.consult(parent, root, config);
	}
	
	@Override public void deploy(StringBuilder builder, Lang lang) {
		Template.get("nodes/use", lang)
				.replace("uid", uid)
				.replace("world", world)
				.replace("variable", variable)
				.replaceLine("nodes", getDeployNodes(lang))
				.appendTo(builder);
	}

}
