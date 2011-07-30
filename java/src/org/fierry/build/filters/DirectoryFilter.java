package org.fierry.build.filters;

import java.nio.file.Files;
import java.nio.file.Path;

import org.fierry.build.projects.IProject;

public class DirectoryFilter implements IFileFilter {

	@Override
	public Boolean accept(Path path, IProject project) {
		return Files.isDirectory(project.getDirectory().resolve(path))
				&& !path.getFileName().toString().isEmpty();
	}

	@Override
	public Boolean fileCreated(Path path, IProject project) {
		project.setPackage(path);
		return true;
	}

	@Override
	public Boolean fileUpdated(Path path, IProject project) {
		return false;
	}

	@Override
	public Boolean fileDeleted(Path path, IProject project) {
		project.deletePackage(path);
		return true;
	}

}
