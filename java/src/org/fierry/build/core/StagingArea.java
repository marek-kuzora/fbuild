package org.fierry.build.core;

import static org.fierry.build.io.Filters.*;

import java.util.Date;

import org.fierry.build.io.FileNode;
import org.fierry.build.io.Files;
import org.fierry.build.visitor.ProjectBuilder;

public class StagingArea {

	private Project project;
	private Long timestamp;
	
	public StagingArea() {
		project = new Project();
		timestamp = new Date().getTime();
		
		create(Files.getProjectDirectory(), project);
	}
	
	private void create(FileNode dir, Package pkg) {
		FileNode pkgNode = dir.get(".package.yml");
		if(pkgNode.exists()) { pkg = new Package(pkgNode, pkg); }
		
		for(FileNode child : dir.children(getJavascriptFiles())) {
			pkg.addFile(new JavaScriptFile(child));
		}

		for(FileNode child : dir.children(getDirectories())) { 
			create(child, pkg); 
		}
	}
	
	public void build() {
		ProjectBuilder builder = new ProjectBuilder();
		project.accept(builder);
		
		for(FileNode node : project.getDeployLocations()) {
			node.write(builder.getResult());
		}
	}
	
	
	public Project getProject() {
		return project;
	}
}
