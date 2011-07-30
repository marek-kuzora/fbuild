package org.fierry.build.filters;

import java.nio.file.Files;
import java.nio.file.Path;

import org.fierry.build.projects.IProject;

public abstract class ExtensionFileFilter implements IFileFilter {
	
	public Boolean accept(Path path, IProject project, String expected) {
		if(Files.isDirectory(project.getDirectory().resolve(path))) { return false; }
		
		String name = path.getFileName().toString();
		Integer idx = name.lastIndexOf('.');
		String ext = idx < 0 ? name : name.substring(name.lastIndexOf('.'));

		return ext.endsWith(expected);
	}
	
	public Boolean accept(Path path, IProject project, String[] expectedArr) {
		for(String expected : expectedArr) {
			if(accept(path, project, expected)) { return true; }
		}
		return false;
	}

}
