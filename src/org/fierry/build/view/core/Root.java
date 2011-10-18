package org.fierry.build.view.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.fierry.build.app.Project;
import org.fierry.build.files.RootFile;
import org.fierry.build.view.parser.Token;

public abstract class Root extends Node {

	public Root(Token.Action token, Map<String, String> args) {
		super(token, args);
	}
	
	@Override public void consult(Node parent, Root root) {
		for(Node node : nodes) {
			node.consult(this, this);
		}
	};
	
	public void install(Project project, RootFile file) {
		configure();
	}
	
	protected void configure() {
		Collection<Parameter> params = new ArrayList<Parameter>();
		
		for(Node node : nodes) {
			if(node instanceof Parameter) { params.add((Parameter)node); }
		}
		
		for(Parameter param : params) {
			param.configure(this);
			nodes.remove(param);
		}
	}
	
	public void uninstall(Project project, RootFile file) {
		
	}
}