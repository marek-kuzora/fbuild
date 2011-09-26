package org.fierry.build.filters;

import java.nio.file.Path;

import org.fierry.build.app.Project;
import org.fierry.build.utils.Shell;

public class CoffeeScriptFileFilter extends ExtensionFileFilter implements IFileFilter {
	public static final String FILE_EXT = ".coffee";

	@Override
	public Boolean accept(Path absolute, Project project) {
		return accept(absolute, project, FILE_EXT);
	}

	@Override
	public Boolean fileCreated(Path absolute, Project project) {
		return fileUpdated(absolute, project);

	}

	@Override
	public Boolean fileUpdated(Path absolute, Project project) {
		String[] args = { "coffee", "-p", "-c", "-b", absolute.toString() };
		project.getScriptFile(absolute).setContent(Shell.run(args));
		return true;
	}

	@Override
	public Boolean fileDeleted(Path absolute, Project project) {
		project.getScriptFile(absolute).removeContent();
		return true;
	}
}
