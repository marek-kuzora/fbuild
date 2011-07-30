package org.fierry.build.filters;

import java.io.File;
import java.nio.file.Path;

import org.fierry.build.projects.IProject;

public class UnsupportedFileFilter extends ExtensionFileFilter {

	@Override public Boolean accept(Path path, IProject project) {
		return true;
	}

	@Override public Boolean fileCreated(Path path, IProject project) {
		System.out.println("Created unsupported file: " + project.getName() + File.separator + path);
		return false;
	}
	
	@Override public Boolean fileUpdated(Path path, IProject project) {
		System.out.println("Updated unsupported file: " + project.getName() + File.separator + path);
		return false;
	}
	
	@Override public Boolean fileDeleted(Path path, IProject project) {
		System.out.println("Deleted unsupported file: " + project.getName() + File.separator + path);
		return false;
	}
	
	

}
