package org.fierry.build.view.nodes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.fierry.build.view.core.Node;
import org.fierry.build.view.parser.Token;

public abstract class MultilineNode extends Node {
	
	protected Collection<String> data;
	
	public MultilineNode(Token.Action token, Map<String, String> args) {
		super(token, args);
		
		data = new ArrayList<String>();
		data.add(token.getData());
	}
	
	public void addData(String value) {
		data.add(value);
	}
}
