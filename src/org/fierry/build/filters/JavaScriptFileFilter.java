package org.fierry.build.filters;

import java.nio.file.Path;

import org.fierry.build.app.Project;
import org.fierry.build.resources.Script;

/**
 * TODO remove if unused
 */
public class JavaScriptFileFilter extends ExtensionFileFilter implements IFileFilter {
	public static final String FILE_EXT = ".js";

	@Override public Boolean accept(Path absolute, Project project) {
		return accept(absolute, project, FILE_EXT);
	}

	@Override public Boolean fileCreated(Path path, Project project) {
		return fileUpdated(path, project);
	}

	@Override public Boolean fileUpdated(Path path, Project project) {
		project.getResource(path, Script.class).setContent(project.read(path));
		return true;
	}

	@Override public Boolean fileDeleted(Path path, Project project) {
		project.getResource(path, Script.class).removeContent();
		return true;
	}
}
