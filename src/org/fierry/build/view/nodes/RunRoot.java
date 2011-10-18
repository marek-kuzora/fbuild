package org.fierry.build.view.nodes;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.fierry.build.app.Project;
import org.fierry.build.files.RootFile;
import org.fierry.build.utils.Lines;
import org.fierry.build.utils.Shell;
import org.fierry.build.utils.Resource;
import org.fierry.build.utils.Uid;
import org.fierry.build.view.core.Deployable;
import org.fierry.build.view.core.Node;
import org.fierry.build.view.core.Root;
import org.fierry.build.view.core.UidCapable;
import org.fierry.build.view.parser.Token;
import org.fierry.build.view.parser.Token.Action;

public class RunRoot extends Root implements Deployable, UidCapable {

	protected String name;
	protected String type;
	
	protected String fileName;
	protected Map<String, String> requires;
	
	public RunRoot(Token.Action token, Map<String, String> args) {
		super(token, args);
		
		String[] raw = StringUtils.normalizeSpace(token.getData()).split(":");
		this.name = raw[0].trim();
		this.type = raw[1].trim();
	}
	
	@Override public Node createNode(Action token, Map<String, String> args) {
		return new ActionNode(token, args);
	}
	
	@Override public String generateUid(ActionNode node) {
		int idx = nodes.indexOf(node);
		assert idx != -1;
		
		return "'" + Uid.generate(idx) + "'";
	}

	@Override public void install(Project project, RootFile file) {
		super.install(project, file);
		
		fileName = file.getName();
		requires = file.getRequires();
	}

	@Override public void deploy(StringBuilder builder) {
		Resource.get("root_module")
				.replace("name", getName())
				.replace("return", getReturn())
				.replaceLine("nodes21", getNodes())
				.replaceLine("requires", getRequires())
				.appendTo(builder);
	}
	
	protected String getName() {
		return fileName + SEPARATOR + name;
	}
	
	protected String getReturn() {
		return "execute('" + type + "', n)";
	}
	
	/**
	 * Builds nodes definitions from Deployable child nodes.
	 * Translates resulting String from CoffeeScript into JavaScript.
	 */
	protected String getNodes() {
		StringBuilder builder = new StringBuilder();
		for(Node node : nodes) {
			if(node instanceof Deployable) {
				((Deployable) node).deploy(builder);
			}
		}
		String[] args = { "coffee", "-s", "-c", "-b" };
		return Shell.run(args, builder);
	}
	
	/**
	 * Builds requires definition directly as JavaScript String.
	 */
	protected String getRequires() {
		StringBuilder builder = new StringBuilder();
		for(Entry<String, String> e : requires.entrySet()) {
			// Deploy only standard non-roots requires.
			if(!e.getValue().contains(":")) {
				builder.append("var " + e.getKey() + " = require('" + e.getValue() + "');" + Lines.separator);
			}
		}
		return builder.toString();
	}
}
