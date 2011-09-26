package org.fierry.build.filters;

import java.nio.file.Path;

import org.fierry.build.app.Project;

public class IgnoreFileFilter extends ExtensionFileFilter {
	public static final String[] FILE_EXT = new String[] {".swp", ".swo", ".DS_Store", ".gitignore", "~"};

	@Override public Boolean accept(Path absolute, Project project) {
		return accept(absolute, project, FILE_EXT);
	}
	
	@Override public Boolean fileCreated(Path absolute, Project project) {
		return false;
	}

	@Override public Boolean fileUpdated(Path absolute, Project project) {
		return false;
	}

	@Override public Boolean fileDeleted(Path absolute, Project project) {
		return false;
	}

}
