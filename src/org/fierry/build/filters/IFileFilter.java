package org.fierry.build.filters;

import java.nio.file.Path;

import org.fierry.build.app.Project;

public interface IFileFilter {

	public Boolean accept(Path path, Project project);
	
	public Boolean fileCreated(Path path, Project project);
	
	public Boolean fileUpdated(Path path, Project project);
	
	public Boolean fileDeleted(Path path, Project project);
}