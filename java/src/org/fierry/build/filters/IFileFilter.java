package org.fierry.build.filters;

import java.nio.file.Path;

import org.fierry.build.projects.IProject;

public interface IFileFilter {

	public Boolean accept(Path path, IProject project);
	
	public Boolean fileCreated(Path path, IProject project);
	
	public Boolean fileUpdated(Path path, IProject project);
	
	public Boolean fileDeleted(Path path, IProject project);
}
