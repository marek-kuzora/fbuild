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
	public void fileCreated(Path path, IProject project) {
		project.setPackage(path);
	}

	@Override
	public void fileUpdated(Path path, IProject project) {}

	@Override
	public void fileDeleted(Path path, IProject project) {
		project.deletePackage(path);
	}

}
