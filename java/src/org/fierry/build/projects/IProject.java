package org.fierry.build.projects;

import java.nio.file.Path;

import org.fierry.build.Projects;
import org.fierry.build.utils.FiltersRegistry;


public interface IProject {

	public void loadDependences(Projects projects);
	
	public void build(FiltersRegistry filters);
	
//	public Boolean contains(IProject project);
	
	public String getName();
	
	public Path getDirectory();
	
	public Boolean isReleaseDirectory(Path path);	
	
//	public void setPackage(Path path);
	
//	public Package getPackage(Path path);
}
