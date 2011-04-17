package org.fierry.build.filters;

import java.nio.file.Path;

import org.fierry.build.projects.IProject;

public interface IFileFilter {

	public Boolean accept(Path path, IProject project);
	
	public void fileCreated(Path path, IProject project);
	
	public void fileUpdated(Path path, IProject project);
	
	public void fileDeleted(Path path, IProject project);
}
