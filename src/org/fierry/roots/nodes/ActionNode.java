package org.fierry.roots.nodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.fierry.build.linking.GlobalConfig;
import org.fierry.build.project.Lang;
import org.fierry.build.utils.Lines;
import org.fierry.build.utils.Template;
import org.fierry.roots.api.IContainer;
import org.fierry.roots.api.IDeployable;
import org.fierry.roots.api.IDeployableRoot;
import org.fierry.roots.api.IMultiline;
import org.fierry.roots.api.INode;
import org.fierry.roots.parser.Token.Action;

public class ActionNode extends AbstractActionNode implements IMultiline, IDeployable {

	private String uid;
	private String behavior;
	private Collection<String> data;
	
	public ActionNode(Action token, Map<String, String> args) {
		super(token, args);
		data = new ArrayList<String>();
		data.add(token.getData());
	}
	
	@Override public void addData(String value) {
		data.add(value);
	}
	
	@Override public void consult(INode parent, IDeployableRoot root, GlobalConfig config) {
		assert parent instanceof IContainer;
		IContainer container = (IContainer) parent;
		
		uid        = container.getUid();
		production = container.getProduction(type);
		behavior   = root.require(production.getBehavior());
	
		super.consult(parent, root, config);
	}

	@Override public void deploy(StringBuilder builder, Lang lang) {
		Template.get("nodes/action", lang)
				.replace("uid", uid)
				.replace("type", type)
				.replace("behavior", behavior)
				.replaceLine("value", getDeployValue())
				.replaceLine("nodes", getDeployNodes(lang))
				.appendTo(builder);
	}
	
	private String getDeployValue() {
		return StringUtils.join(data, Lines.separator);
	}
}
