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
import org.fierry.roots.utils.Uid;

public class LoopNode extends AbstractContainerNode implements IDeployable {

	private String uid;
	private String value;
	private String variable;
	
	private Uid generator;

	public LoopNode(Action token, Map<String, String> args) {
		super(token, args);
		
		String[] raw = StringUtils.normalizeSpace(token.getData()).split(" in ");
		this.variable = raw[0];
		this.value    = raw[1];
	}
	
	@Override public void consult(INode parent, IDeployableRoot root, GlobalConfig config) {
		assert parent instanceof IContainer;
		
		uid       = ((IContainer) parent).getUid();
		generator = new Uid();
		
		super.consult(parent, root, config);
	}
	
	@Override public String getUid() {
		return uid + " + math.uid(" + variable + ") + " + "'" + generator.generate(2) + "'";
	}

	@Override public void deploy(StringBuilder builder, Lang lang) {
		Template.get("nodes/loop", lang)
				.replace("value", value)
				.replace("variable", variable)
				.replaceLine("nodes", getDeployNodes(lang))
				.appendTo(builder);
	}

}
