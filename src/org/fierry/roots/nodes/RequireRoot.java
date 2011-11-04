package org.fierry.roots.nodes;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.fierry.build.resources.Roots;
import org.fierry.roots.api.IRoot;
import org.fierry.roots.parser.Token.Action;

public class RequireRoot extends AbstractNode implements IRoot {

	private String path;
	private String name;
	
	public RequireRoot(Action token, Map<String, String> args) {
		super(token, args);
		
		String[] raw = StringUtils.normalizeSpace(token.getData()).split(" as ");
		this.path = raw[0];
		this.name = raw[1];
	}

	@Override public void install(Roots resource) {
		resource.addRequire(name, path);
	}

	@Override protected AbstractNode createNode(Action token, Map<String, String> args) {
		throw new IllegalArgumentException("/require cannot contain any nodes.");
	}	
}
