package org.fierry.build.filters;

import java.nio.file.Files;
import java.nio.file.Path;

import org.fierry.build.projects.IProject;

public abstract class ExtensionFileFilter implements IFileFilter {
	
	public Boolean acceppt(Path path, IProject project, String expected) {
		if(Files.isDirectory(project.getDirectory().resolve(path))) { return false; }
		
		String name = path.getFileName().toString();
		String ext = name.substring(name.lastIndexOf('.'));

		return ext.equals(expected);
	}

}
