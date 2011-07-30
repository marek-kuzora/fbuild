package org.fierry.build.projects;

import java.nio.file.Path;

import org.fierry.build.files.Package;
import org.fierry.build.Projects;
import org.fierry.build.utils.FiltersRegistry;


public interface IProject {

	public void loadDependences(Projects projects);
	
	public void build(FiltersRegistry filters);
	
	public void deploy();
	
	
	public void setPackage(Path path);
	
	public void deletePackage(Path path);
	
	public Package getPackage(Path path);
	
	public Package getPackage(String name);
	
	
	public String getName();
	
	public Path getDirectory();
	
	public Boolean isReleaseDirectory(Path path);	
	
	public Boolean isHiddenDirectory(Path path);
	
	public Boolean isProjectReferenced(String name);
}
