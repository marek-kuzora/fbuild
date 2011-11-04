package org.fierry.build.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.fierry.build.app.Build;

public class Commands {

	public static void newProject(String project) throws IOException {
		int idx = project.indexOf('/');
		if(idx == -1) { project += "/source"; }
		
		String name   = project.substring(0, idx);
		String source = project.substring(idx + 1);
		
		if(!Directory.existsBuild()) { Build.createEmpty(); }
		assert !Build.load().contains(name) : "Project with the given name already exists: " + name;
		
		Path dir = Directory.getBuild();
		Files.createDirectories(dir.resolve("release"));

		Path cnt  = dir.resolve(source);
		Files.createDirectories(cnt);
		
		Path main = cnt.resolve("app.coffee");
		if(!Files.exists(main)) { Files.createFile(main); }
		
		Template.get("new_project")
				.replace("name", name)
				.replace("source", source)
				.appendTo(dir.resolve(Build.FILE));
	}
}
