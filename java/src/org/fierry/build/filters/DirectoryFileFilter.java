package org.fierry.build.filters;

import java.nio.file.Files;
import java.nio.file.Path;

import org.fierry.build.app.Project;

public class DirectoryFileFilter implements IFileFilter {

	@Override
	public Boolean accept(Path absolute, Project project) {
		return Files.isDirectory(absolute);
	}

	@Override
	public Boolean fileCreated(Path absolute, Project project) {
		return true;
	}

	@Override
	public Boolean fileUpdated(Path absolute, Project project) {
		return false;
	}

	@Override
	public Boolean fileDeleted(Path absolute, Project project) {
		return true;
	}

}
