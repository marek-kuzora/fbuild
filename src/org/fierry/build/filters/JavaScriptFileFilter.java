package org.fierry.build.filters;

import java.nio.file.Path;

import org.fierry.build.app.Project;

public class JavaScriptFileFilter extends ExtensionFileFilter implements IFileFilter {
	public static final String FILE_EXT = ".js";

	@Override public Boolean accept(Path absolute, Project project) {
		return accept(absolute, project, FILE_EXT);
	}

	@Override public Boolean fileCreated(Path path, Project project) {
		return fileUpdated(path, project);
	}

	@Override public Boolean fileUpdated(Path path, Project project) {
		project.getScriptFile(path).setContent(project.readFile(path));
		return true;
	}

	@Override public Boolean fileDeleted(Path path, Project project) {
		project.getScriptFile(path).removeContent();
		return true;
	}
}
