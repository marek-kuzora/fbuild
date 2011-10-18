package org.fierry.build.app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.fierry.build.filters.FileFiltersRegistry;
import org.fierry.build.project.ExternsVisitor;
import org.fierry.build.utils.Directory;
import org.fierry.build.utils.Resource;
import org.fierry.build.yaml.BuildY;
import org.fierry.build.yaml.Yaml;

public class Build {

	public static final String FILE = ".build.yml";
	private List<Project> projects;
	
	public static Build create() {
		return create(new ArrayList<String>(), true);
	}
	
	public static Build create(Collection<String> includes, Boolean quiet) {
		Path dir  = Directory.getBuild(quiet);
		Path file = dir.resolve(FILE);
		
		Build build = new Build(Yaml.load(BuildY.class, file, quiet));
		build.filter(includes);
		return build;
	}
	
	public Build(BuildY raw) {
		projects = raw.getProjects();
	}
	
	public void filter(Collection<String> includes) {
		if(includes.size() > 0) {
			for(int i = 0; i < projects.size(); i++) {
				if(!includes.contains(projects.get(i).getName())) {
					projects.remove(i--);
				}
			}
		}
	}
	
	public void build(FileFiltersRegistry filters) {
		for(Project project : projects) {
			project.build(filters);
		}
	}
	
	public void deploy() throws IOException {
		for(Project project : projects) {
			project.deploy();
		}
	}
	
	public void compile() throws IOException {
		ExternsVisitor visitor = loadExternsVisitor();
		for(Project project : projects) {
			project.compile(visitor.clone());
		}
	}
	
	private ExternsVisitor loadExternsVisitor() throws IOException {
		ExternsVisitor visitor = new ExternsVisitor();
		Files.walkFileTree(Directory.getJar().resolve("externs"), visitor);
		
		return visitor;
	}
	
	public void createProject(String project) throws IOException {
		int idx = project.indexOf('/');
		if(idx == -1) { project += "/source"; }
		
		String name   = project.substring(0, idx);
		String source = project.substring(idx+1);
		
		assert isUnique(name) : "Project with the given name already exists: " + name;
		
		Path dir  = Directory.getRun();
		Path file = dir.resolve(FILE);
		
		StringBuilder builder = new StringBuilder();
		
		builder.append(Files.exists(file) ? new String(Files.readAllBytes(file)) : "projects:");
		
		Resource.get("project_build_def")
				.replace("name", name)
				.replace("source", source)
				.appendTo(builder);
		
		Files.write(file, builder.toString().getBytes());
		Files.createDirectories(dir.resolve("release"));
		
		Path cnt  = dir.resolve(source);
		Files.createDirectories(cnt);
		
		Path main = cnt.resolve("app.coffee");
		if(!Files.exists(main)) { Files.createFile(main); }
	}
	
	private Boolean isUnique(String name) {
		for(Project project : projects) {
			if(name.equals(project.getName())) { return false; }
		}
		return true;
	}
}