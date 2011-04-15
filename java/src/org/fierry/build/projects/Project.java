package org.fierry.build.projects;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.fierry.build.ProjectThread;
import org.fierry.build.Projects;
import org.fierry.build.project.State;
import org.fierry.build.project.Visitor;
import org.fierry.build.utils.Resources;
import org.fierry.build.utils.FiltersRegistry;

public class Project implements IProject {
	public static final String FILE = ".project.yml";
	
	private Path dir;
	private State state;
	private ProjectY project;
	private Set<IProject> projects;
	
	public Project(Path dir) {
		assert Files.exists(dir) : "Project directory doesn't exist: " + dir;
		assert Files.exists(dir.resolve(FILE)) : "Project file doesn't exist under: " + dir;
		
		this.dir = dir;
		this.state = State.INITIAL;
		this.projects = new HashSet<IProject>();
		this.project = Resources.loadYaml(ProjectY.class, dir.resolve(FILE));
	}

	@Override public void loadDependences(Projects registry) {
		if(state == State.INITIAL) {
			for(String name : project.dependences) {
				IProject p = registry.get(name);
				
				p.loadDependences(registry);
				projects.add(p);
				
				// BUG - assert's not working...
//				assert !p.contains(this) : "Bidirectional relationship not allowed: " + getName() + " and " + p.getName();	
			}
			state = State.LOADED;
		}
	}

	@Override public void build(FiltersRegistry filters) {
		if(state == State.LOADED) {
			for(IProject p : projects) {
				p.build(filters);
			}
			
			try {
				WatchService watcher = FileSystems.getDefault().newWatchService();
				Map<WatchKey, Path> paths = new HashMap<WatchKey, Path>();
				
				Files.walkFileTree(dir, new Visitor(this, watcher, filters, paths));
				ProjectThread thread = new ProjectThread(this, watcher, filters, paths);
				thread.start();
			} 
			catch(IOException e) { throw new RuntimeException(e); }
			
			state = State.BUILDED;
		}
	}
	
//	@Override public Boolean contains(IProject project) {
//		return projects.contains(project);
//	}
	
	@Override public String getName() {
		return project.name;
	}
	
	@Override public Path getDirectory() {
		return dir;
	}
	
	@Override public Boolean isReleaseDirectory(Path absPath) {
		for(String rls : project.deploy) {
			if(absPath.startsWith(dir.resolve(rls))) { return true; }
		}
		return false;
	}
	
	@Override public int hashCode() {
		return getName().hashCode();
	}
	
	@Override public boolean equals(Object obj) {
		if(!(obj instanceof Project)) { return false; }
		return obj != null && hashCode() == obj.hashCode();
	}
}
