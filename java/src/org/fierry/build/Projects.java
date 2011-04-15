package org.fierry.build;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.fierry.build.projects.CurrentProject;
import org.fierry.build.projects.IProject;
import org.fierry.build.projects.Project;
import org.fierry.build.utils.Resources;

/**
 * Stores project instances defined in projects.yml file.
 */
public class Projects {
	public static final String FILE = "projects.yml";
	
	private CurrentProject current;
	private Map<String, IProject> projects;
	
	public Projects(CurrentProject current) {
		this.current = current;
		this.projects = new HashMap<String, IProject>();
		
		loadProjects();
	}
	
	/**
	 * Loads projects from theirs locations as specified in %config%/projects.yml.
	 */
	@SuppressWarnings("unchecked")
	private void loadProjects() {
		String cname = current.getName();
		Path file = Resources.getBuildDirectory().resolve(FILE);

		assert Files.exists(file) : "File projects.yml not found under: " + file;
		Map<String, String> values = Resources.loadYaml(Map.class, file);
		
		for(Entry<String, String> e : values.entrySet()) {
			String pname = e.getKey();
			
			if(!pname.equals(cname)) {
				IProject project = new Project(Paths.get(e.getValue()));
				assert project.getName().equals(pname) : "Declared project name don't match: " + pname + " vs " + project.getName();
				projects.put(pname, project);
			}
		}
		projects.put(cname, current);
	}
	
	/**
	 * Returns the current project.
	 */
	public CurrentProject getCurrent() {
		return current;
	}
	
	/**
	 * Returns project with the given name.
	 * @param name - project name.
	 */
	public IProject get(String name) {
		assert projects.containsKey(name) : "Project not found: " + name;
		return projects.get(name);
	}
}
