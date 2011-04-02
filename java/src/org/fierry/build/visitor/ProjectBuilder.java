package org.fierry.build.visitor;

import org.fierry.build.core.Package;

public class ProjectBuilder implements IVisitor {

	private StringBuilder builder;
	
	public ProjectBuilder() {
		builder = new StringBuilder();
	}
	
	@Override public void visit(Package pkg) {
		builder.append(pkg.getOutput());
		
		for(Package child : pkg.getPackages()) {
			child.accept(this);
		}
	}
	
	public String getResult() {
		return builder.toString();
	}

}
