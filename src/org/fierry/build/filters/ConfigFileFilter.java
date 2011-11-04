package org.fierry.build.filters;

import java.nio.file.Path;

import org.fierry.build.app.Project;
import org.fierry.build.resources.Config;
import org.fierry.build.yaml.ConfigY;
import org.fierry.build.yaml.Yaml;

public class ConfigFileFilter extends ExtensionFileFilter implements IFileFilter {

	public static final String FILE_EXT = ".yml";

	@Override public Boolean accept(Path path, Project project) {
		return accept(path, project, FILE_EXT);
	}

	@Override public Boolean fileCreated(Path path, Project project) {
		return fileUpdated(path, project);
	}

	@Override public Boolean fileUpdated(Path path, Project project) {
		ConfigY config = Yaml.load(ConfigY.class, path);
		if(config != null) {
			project.getResource(path, Config.class).setContent(config);
		}
		return true;
	}

	@Override public Boolean fileDeleted(Path path, Project project) {
		project.getResource(path, Config.class).removeContent();
		return true;
	}

}
