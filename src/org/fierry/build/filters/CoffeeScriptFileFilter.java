package org.fierry.build.filters;

import java.nio.file.Path;

import org.fierry.build.app.Project;
import org.fierry.build.parsers.InlineConfigurationParser;
import org.fierry.build.parsers.PerformanceParser;
import org.fierry.build.resources.Config;
import org.fierry.build.resources.Script;
import org.fierry.build.utils.CoffeeScript;

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
		String code   = project.read(path);
		Script script = project.getResource(path, Script.class);
		Config config = project.getResource(path.resolve(".yml"), Config.class);
		
		System.out.println("Compiling: " + path);
		
		// Parsing inline configuration directives.
		if(InlineConfigurationParser.accept(code)) {
			InlineConfigurationParser.parse(code, config);
		}
		
		// Parsing performance tests.
		if(PerformanceParser.accept(code)) {
			code = PerformanceParser.parse(code);
		}
		
		// Setting raw JS source into the Script resource.
		script.setContent(CoffeeScript.get().compile(code));		
		return true;
	}

	@Override
	public Boolean fileDeleted(Path path, Project project) {
		project.getResource(path, Script.class).removeContent();
		project.getResource(path.resolve(".yml"), Config.class).removeContent();
		return true;
	}
}
