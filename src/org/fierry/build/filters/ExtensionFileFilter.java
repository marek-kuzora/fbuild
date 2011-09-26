package org.fierry.build.filters;

import java.nio.file.Files;
import java.nio.file.Path;

import org.fierry.build.app.Project;
import org.fierry.build.utils.FileUtils;

public abstract class ExtensionFileFilter implements IFileFilter {
	
	public Boolean accept(Path absolute, Project project, String expected) {
		if(Files.isDirectory(absolute)) { return false; }
		
		String extension = FileUtils.getExtension(absolute);
		return extension.endsWith(expected);
	}
	
	public Boolean accept(Path absolute, Project project, String[] expectedArr) {
		for(String expected : expectedArr) {
			if(accept(absolute, project, expected)) { return true; }
		}
		return false;
	}

}
