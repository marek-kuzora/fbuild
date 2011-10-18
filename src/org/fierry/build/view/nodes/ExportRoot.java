package org.fierry.build.view.nodes;

import java.util.Map;

import org.fierry.build.view.parser.Token;

public class ExportRoot extends RunRoot {

	public ExportRoot(Token.Action token, Map<String, String> args) {
		super(token, args);
	}
	
	@Override protected String getReturn() {
		return "function() { return registry.create('" + type + "', n); }";
	}
}