package org.fierry.roots.nodes;

import java.util.Map;

import org.fierry.build.linking.GlobalConfig;
import org.fierry.build.yaml.ActionY;
import org.fierry.roots.api.IDeployableRoot;
import org.fierry.roots.api.INode;
import org.fierry.roots.parser.Token.Action;
import org.fierry.roots.utils.Uid;

public abstract class AbstractActionNode extends AbstractContainerNode {

	protected ActionY production;
	protected Uid generator;
	
	public AbstractActionNode(Action token, Map<String, String> args) {
		super(token, args);
	}

	@Override public void consult(INode parent, IDeployableRoot root, GlobalConfig config) {
		generator = new Uid();
		super.consult(parent, root, config);
	}
	
	@Override public String getUid() {
		return "'" + generator.generate(2) + "'";
	}
	
	@Override public ActionY getProduction(String type) {
		return config.getActionProduction(production, type);
	}
}
