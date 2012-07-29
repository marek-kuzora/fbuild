package org.fierry.roots.nodes;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.fierry.build.linking.GlobalConfig;
import org.fierry.build.project.Lang;
import org.fierry.build.resources.Roots;
import org.fierry.build.utils.CoffeeScript;
import org.fierry.build.utils.Template;
import org.fierry.roots.api.IDeployableRoot;
import org.fierry.roots.api.INode;
import org.fierry.roots.parser.Token.Action;
import org.fierry.roots.utils.Requires;

public class DeployableRoot extends AbstractActionNode implements IDeployableRoot {

	private String root;
	private String productionID;
	
	protected String resource;
	protected String behavior;
	protected Requires requires;
	
	public DeployableRoot(Action token, Map<String, String> args) {
		super(token, args);
		
		String[] raw = StringUtils.normalizeSpace(token.getData()).split(":");
		this.productionID = raw[0].trim();
		this.root         = raw[1].trim();
	}

	@Override
	public void install(Roots resource) {
		this.resource = resource.getName();
		this.requires = new Requires(resource.getRequires());		
	}
	
	@Override public void consult(INode parent, IDeployableRoot root, GlobalConfig config) {
		production = config.getActionProductionById(productionID);
		behavior   = require(production.getBehavior());

		super.consult(this, this, config);
	}
	
	@Override public String require(String path) {
		return requires.require(path);
	}

	
	@Override public void deploy(StringBuilder builder, Lang lang) {
		Template.get("nodes/root_new")
				.replace("name", resource)
				.replace("return", getDeployReturn())
				.replaceLine("nodes", getDeployNodes(lang))
				.replaceLine("requires", requires.deploy())
				.appendTo(builder);
	}
	
	@Override protected String getDeployNodes(Lang lang) {
		String nodes = super.getDeployNodes(lang);
		if(Lang.JavaScript == lang) { System.out.println(nodes); }
		return lang == Lang.JavaScript ? nodes : CoffeeScript.get().compile(nodes);
	}
	
	protected String getDeployReturn() {
		return "new View('" + root + "', " + behavior + "(), n)";
	}


}
