package org.fierry.build.view.nodes;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.fierry.build.app.Project;
import org.fierry.build.files.RootFile;
import org.fierry.build.view.core.Node;
import org.fierry.build.view.core.Root;
import org.fierry.build.view.parser.Token;
import org.fierry.build.view.parser.Token.Action;

public class RequireRoot extends Root {

	private String path;
	private String alias;
	
	public RequireRoot(Token.Action token, Map<String, String> args) {
		super(token, args);

		String[] raw = StringUtils.normalizeSpace(token.getData()).split(" as ");
		this.path  = raw[0];
		this.alias = raw[1];
	}
	
	@Override public void install(Project project, RootFile file) {
		super.install(project, file);
		file.addRequire(alias, path);
	}

	@Override public Node createNode(Action token, Map<String, String> args) {
		throw new IllegalArgumentException("/require is not allowed to contain any nodes.");
	}

}
