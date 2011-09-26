package org.fierry.build.filters;

import java.nio.file.Path;

import org.fierry.build.app.Project;

public interface IFileFilter {

	public Boolean accept(Path absolute, Project project);
	
	public Boolean fileCreated(Path absolute, Project project);
	
	public Boolean fileUpdated(Path absolute, Project project);
	
	public Boolean fileDeleted(Path absolute, Project project);
}