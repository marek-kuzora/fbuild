package org.fierry.build.visitor;

import org.fierry.build.core.Package;

public class RetrospectVisitor implements IVisitor {

	@Override
	public void visit(Package pkg) {
		System.out.println("Name: " + pkg.getName());
		System.out.println("JavascriptFiles: " + pkg.getFiles());
		System.out.println();
		
		for(Package child : pkg.getPackages()) {
			child.accept(this);
		}
	}

}
