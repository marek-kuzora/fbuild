package org.fierry.build.filters;

import java.nio.file.Path;

import org.fierry.build.projects.IProject;

public class IgnoreFileFilter extends ExtensionFileFilter {
	public static final String[] FILE_EXT = new String[] {".swp", ".swo", ".DS_Store", ".gitignore", "~"};

	@Override public Boolean accept(Path path, IProject project) {
		return accept(path, project, FILE_EXT);
	}
	
	@Override public Boolean fileCreated(Path path, IProject project) {
		return false;
	}

	@Override public Boolean fileUpdated(Path path, IProject project) {
		return false;
	}

	@Override public Boolean fileDeleted(Path path, IProject project) {
		return false;
	}

}
