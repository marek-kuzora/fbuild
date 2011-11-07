package org.fierry.build.yaml;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.fierry.build.app.Project;

public class BuildY {
	public Map<String, ProjectY> projects = new HashMap<String, ProjectY>();
	
	public List<Project> getProjects(Path dir) {
		List<Project> projects = new ArrayList<Project>();
		
		for(Entry<String, ProjectY> e : this.projects.entrySet()) {
			projects.add(new Project(e.getKey(), e.getValue(), dir));
		}
		return projects;
	}
	
	public Project getProject(String name, Path dir) {
		assert projects.containsKey(name) : "Project not found: " + name;
		return new Project(name, projects.get(name), dir);
	}
}