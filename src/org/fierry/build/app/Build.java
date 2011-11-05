package org.fierry.build.app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

import org.fierry.build.utils.Directory;
import org.fierry.build.visitors.ExternsVisitor;
import org.fierry.build.yaml.BuildY;
import org.fierry.build.yaml.Yaml;

public class Build {

	public static final String FILE = ".build.yml";
	private List<Project> projects;
	
	/*
	 * CREATING / LOADING
	 */
	
	public static Build load() {
		return load(Directory.getBuild());
	}
	
	public static Build load(Path dir) {
		Path file = dir.resolve(FILE);
		return new Build(Yaml.load(BuildY.class, file));
	}
	
	public static void createEmpty() throws IOException {
		assert !Directory.existsBuild() : "Build file already exists.";
		
		Path file = Directory.getRun().resolve(FILE);
		Files.write(file, "projects:".getBytes());
	}
	
	private Build(BuildY raw) {
		projects = raw.getProjects();
	}
	
	/*
	 * OPERATIONS
	 */
	
	public void build() {
		for(Project project : projects) {
			project.build();
		}
	}
	
	public void deploy() throws IOException {
		for(Project project : projects) {
			project.deploy();
		}
	}
	
	public void compile() throws IOException {
		ExternsVisitor visitor = ExternsVisitor.load();
		for(Project project : projects) {
			project.compile(visitor.clone());
		}
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
	
	/*
	 * GETTERS
	 */
	
	public boolean contains(String name) {
		for(Project project : projects) {
			if(name.equals(project.getName())) { return true; }
		}
		return false;
	}
}