package org.fierry.build.projects;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.fierry.build.ProjectThread;
import org.fierry.build.Projects;
import org.fierry.build.files.Package;
import org.fierry.build.files.TopPackage;
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
	private Map<Path, TopPackage> pkgs;
	
	public Project(Path dir) {
		assert Files.exists(dir) : "Project directory doesn't exist: " + dir;
		assert Files.exists(dir.resolve(FILE)) : "Project file doesn't exist under: " + dir;
		
		this.dir = dir;
		this.state = State.INITIAL;
		this.projects = new HashSet<IProject>();
		
		this.pkgs = new HashMap<Path, TopPackage>();
		this.project = Resources.loadYaml(ProjectY.class, dir.resolve(FILE));
	}

	@Override public void loadDependences(Projects registry) {
		if(state == State.INITIAL) {
			for(String name : project.dependences) {
				IProject p = registry.get(name);
				
				assert !projects.contains(p) : "Bidirectional relationship not allowed: " + getName() + " and " + p.getName();

				projects.add(p);
				p.loadDependences(registry);
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
	
	@Override public void setPackage(Path path) {
		if(path.getNameCount() == 1) {
			String name = isSrc(path) ? project.name : project.name + "." + path.toString();
			
			pkgs.put(path, new TopPackage(name, this));
		} else {
			TopPackage top = pkgs.get(path.getName(0));
			top.setPackage(path.subpath(1, path.getNameCount()));
		}
	}

	private Boolean isSrc(Path path) {
		return project.src.equals(path.toString());
	}
	
	@Override public void deletePackage(Path path) {
		if(path.getNameCount() == 1) {
			pkgs.remove(path);
		} else {
			TopPackage top = pkgs.get(path.getName(0));
			top.deletePackage(path.subpath(1, path.getNameCount()));
		}
	}

	// Bad handling! The name starts with the project. Next for now we can require other packages within the
	// current topPackage? e.g. fierry.test.a requires fierry.test.b & the b package is in the test/ directory instead of src!
	@Override public Package getPackage(String name) {
		Path path = Paths.get(project.src, name.replace('.', File.pathSeparatorChar));
		return getPackage(path);
	}
	
	@Override public Package getPackage(Path path) {
		TopPackage top = pkgs.get(path.getName(0));
		return top.getPackage(path.subpath(1, path.getNameCount()));
	}
	
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
