package org.fierry.build.app;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

import org.fierry.build.filters.FileFiltersRegistry;
import org.fierry.build.project.ExternsVisitor;
import org.fierry.build.utils.Resources;
import org.fierry.build.yaml.BuildY;

public class Build {

	public static final String FILE = ".build.yml";
	private List<Project> projects;
	
	public static Build create(Collection<String> projects) {
		return create(projects, false);
	}
	
	public static Build create(Collection<String> includes, Boolean quiet) {
		Path dir  = Resources.getBuildDirectory(quiet);
		Path file = dir.resolve(FILE);
		
		Build build = new Build(Resources.loadYaml(BuildY.class, file, quiet));
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
		Files.walkFileTree(Resources.getJarDirectory().resolve("externs"), visitor);
		
		return visitor;
	}
}