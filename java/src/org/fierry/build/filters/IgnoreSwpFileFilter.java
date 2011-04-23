package org.fierry.build.filters;

import java.nio.file.Path;

import org.fierry.build.projects.IProject;

public class IgnoreSwpFileFilter extends UnsupportedFileFilter {
	public static final String FILE_EXT = ".swp";

	@Override public Boolean accept(Path path, IProject project) {
		return accept(path, project, FILE_EXT);
	}
	
	@Override public void fileCreated(Path path, IProject project) {}

	@Override public void fileUpdated(Path path, IProject project) {}

	@Override public void fileDeleted(Path path, IProject project) {}

}
