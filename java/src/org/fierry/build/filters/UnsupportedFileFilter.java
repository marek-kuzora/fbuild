package org.fierry.build.filters;

import java.nio.file.Path;

import org.fierry.build.app.Project;

public class UnsupportedFileFilter extends ExtensionFileFilter {

	@Override public Boolean accept(Path absolute, Project project) {
		return true;
	}

	@Override public Boolean fileCreated(Path absolute, Project project) {
		System.err.println("Created unsupported file: " + absolute);
		return false;
	}
	
	@Override public Boolean fileUpdated(Path absolute, Project project) {
		System.err.println("Updated unsupported file: " + absolute);
		return false;
	}
	
	@Override public Boolean fileDeleted(Path absolute, Project project) {
		System.err.println("Deleted unsupported file: " + absolute);
		return false;
	}
	
	

}
