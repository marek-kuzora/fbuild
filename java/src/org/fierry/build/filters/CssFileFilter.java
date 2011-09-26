package org.fierry.build.filters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.fierry.build.app.Project;

public class CssFileFilter extends ExtensionFileFilter implements IFileFilter {

	public static final String FILE_EXT = ".css";

	@Override public Boolean accept(Path absolute, Project project) {
		return accept(absolute, project, FILE_EXT);
	}

	@Override public Boolean fileCreated(Path absolute, Project project) {
		return fileUpdated(absolute, project);
	}

	@Override public Boolean fileUpdated(Path absolute, Project project) {
		try {
			project.getCssFile(absolute).setContent(Files.readAllBytes(absolute));
			return true;
		}
		catch(IOException e) { throw new RuntimeException(e); }
	}

	@Override public Boolean fileDeleted(Path absolute, Project project) {
		project.getCssFile(absolute).removeContent();
		return true;
	}
}
