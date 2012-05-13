package org.fierry.roots.nodes;

import java.util.Map;

import org.fierry.roots.parser.Token.Action;

public class ExportRoot extends AbstractDeployableRoot {

	public ExportRoot(Action token, Map<String, String> args) {
		super(token, args);
	}

	@Override protected String getDeployReturn() {
		return "function() { return roots().execute_raw('" + rootType + "', " + behavior + "(), n); }";
	}

}
