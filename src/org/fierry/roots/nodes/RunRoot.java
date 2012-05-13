package org.fierry.roots.nodes;

import java.util.Map;

import org.fierry.roots.parser.Token.Action;

public class RunRoot extends AbstractDeployableRoot {

	public RunRoot(Action token, Map<String, String> args) {
		super(token, args);
	}

	@Override protected String getDeployReturn() {
		return "roots().execute('" + rootType + "', " + behavior + "(), n)";
	}
}
