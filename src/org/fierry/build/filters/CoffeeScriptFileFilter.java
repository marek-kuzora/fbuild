package org.fierry.build.filters;

import java.nio.file.Path;

import org.fierry.build.app.Project;
import org.fierry.build.resources.Script;
import org.fierry.build.utils.Shell;

public class CoffeeScriptFileFilter extends ExtensionFileFilter implements IFileFilter {
	public static final String FILE_EXT = ".coffee";

	@Override
	public Boolean accept(Path path, Project project) {
		return accept(path, project, FILE_EXT);
	}

	@Override
	public Boolean fileCreated(Path path, Project project) {
		return fileUpdated(path, project);
	}

	@Override
	public Boolean fileUpdated(Path path, Project project) {
		String   file = project.read(path);
		String[] args = { "coffee", "-s", "-c", "-b" };

		project.getResource(path, Script.class).setContent(Shell.run(args, file));
		return true;
	}

	@Override
	public Boolean fileDeleted(Path path, Project project) {
		project.getResource(path, Script.class).removeContent();
		return true;
	}
}
