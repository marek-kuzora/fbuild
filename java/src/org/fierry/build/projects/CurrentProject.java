package org.fierry.build.projects;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CurrentProject extends Project {
	
	public CurrentProject() {
		super(getProjectDirectory());
	}
	
	/**
	 * Returns the project's file directory.
	 */
	private static Path getProjectDirectory() {
		Path dir = Paths.get("").toAbsolutePath();
		while(dir.getParent() != null) {
			Path file = dir.resolve(Project.FILE);
			
			if(Files.exists(file)) { return dir; }
			else { dir = dir.getParent(); }
		}
		throw new IllegalStateException("Project file not found: " + Paths.get("").toAbsolutePath());
	}
}