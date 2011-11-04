package org.fierry.build.filters;

import java.nio.file.Path;

import org.fierry.build.app.Project;
import org.fierry.build.resources.Css;

public class CssFileFilter extends ExtensionFileFilter implements IFileFilter {

	public static final String FILE_EXT = ".css";

	@Override public Boolean accept(Path path, Project project) {
		return accept(path, project, FILE_EXT);
	}

	@Override public Boolean fileCreated(Path path, Project project) {
		return fileUpdated(path, project);
	}

	@Override public Boolean fileUpdated(Path path, Project project) {
		project.getResource(path, Css.class).setContent(project.read(path));
		return true;
	}

	@Override public Boolean fileDeleted(Path path, Project project) {
		project.getResource(path, Css.class).removeContent();
		return true;
	}
}
