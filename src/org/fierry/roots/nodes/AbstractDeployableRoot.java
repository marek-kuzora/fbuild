package org.fierry.roots.nodes;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.fierry.build.linking.GlobalConfig;
import org.fierry.build.resources.Roots;
import org.fierry.build.utils.CoffeeScript;
import org.fierry.build.utils.Template;
import org.fierry.roots.api.IDeployableRoot;
import org.fierry.roots.api.INode;
import org.fierry.roots.parser.Token.Action;
import org.fierry.roots.utils.Requires;

public abstract class AbstractDeployableRoot extends AbstractActionNode implements IDeployableRoot {

	protected String name;
	protected String rootType;
	
	protected String resource;
	protected String behavior;
	protected Requires requires;
	
	public AbstractDeployableRoot(Action token, Map<String, String> args) {
		super(token, args);
		
		String[] raw = StringUtils.normalizeSpace(token.getData()).split(":");
		this.name     = raw[0].trim();
		this.rootType = raw[1].trim();
	}

	@Override public void install(Roots resource) {
		this.resource = resource.getName();
		this.requires = new Requires(resource.getRequires());
	}

	@Override public void consult(INode parent, IDeployableRoot root, GlobalConfig config) {
		production = config.getActionProduction(rootType);
		behavior   = require(production.getBehavior());

		super.consult(this, this, config);
	}
	
	@Override public String require(String path) {
		return requires.require(path);
	}
	
	@Override public void deploy(StringBuilder builder) {
		Template.get("nodes/root")
				.replace("name", getDeployName())
				.replace("return", getDeployReturn())
				.replaceLine("nodes", getDeployNodes())
				.replaceLine("requires", requires.deploy())
				.appendTo(builder);
	}
	
	protected String getDeployName() {
		return resource + ":" + name;
	}
	
	protected abstract String getDeployReturn();

	@Override protected String getDeployNodes() {
		return CoffeeScript.get().compile(super.getDeployNodes());
	}
}
