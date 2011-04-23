package org.fierry.build.filters;

import java.nio.file.Path;

import org.fierry.build.files.MemoryFile;
import org.fierry.build.projects.IProject;
import org.fierry.build.utils.Shell;

public class CoffeeScriptFileFilter extends ExtensionFileFilter {
	public static final String FILE_EXT = ".coffee";
	
	@Override
	public Boolean accept(Path path, IProject project) {
		return accept(path, project, FILE_EXT);
	}

	@Override
	public void fileCreated(Path path, IProject project) {
		fileUpdated(path, project);
	}

	@Override
	public void fileUpdated(Path path, IProject project) {
		Path abs = project.getDirectory().resolve(path);
		String[] args = { "coffee", "-p", "-c", abs.toString() };
		
		String name = path.getFileName().toString();
		String content = Shell.run(args);
			
		MemoryFile file = new MemoryFile(name, content);
		project.getPackage(path.getParent()).setFile(file);
	}

	@Override
	public void fileDeleted(Path path, IProject project) {
		String name = path.getFileName().toString();
		project.getPackage(path.getParent()).deleteFile(name);
	}

}
