package org.fierry.roots.nodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
	
	private ActionNode tagsNode;
	
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
		
		if(isDomAction()) {
			tagsNode = new ActionNode(createTagToken(), new HashMap<String, String>());
			tagsNode.consult(this, root, config);
		}
	}
	
	private Action createTagToken() {
		String data = "'" + StringUtils.join(tags, " ").replaceAll("(^| )-", " ").trim() + "'";
		return new Action("tag", new ArrayList<String>(), data);
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
	
	@Override protected String getDeployNodes(Lang lang) {
		return getDeployTags(lang) + super.getDeployNodes(lang);
	}
	
	private String getDeployTags(Lang lang) {
		if(isDomAction()) {
			StringBuilder builder = new StringBuilder();
			tagsNode.deploy(builder, lang);
			return builder.toString();
		}
		return "";
	}
	
	private Boolean isDomAction() {
		return production.groups.contains("dom/element") && tags.size() > 0;
	}
	
	private String getDeployValue() {
		return StringUtils.join(data, Lines.separator);
	}
}
