package org.fierry.build.yaml;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.fierry.build.app.Project;
import org.fierry.build.project.Lang;
import org.fierry.build.project.Source;

public class ProjectY {
	
	private Path dir;
	
	public String main;
	public String source;
	
	public String lang = "coffeescript";
	
	public List<String> deploy  = new ArrayList<String>();
	public List<String> libs    = new ArrayList<String>();
	public List<String> exports = new ArrayList<String>();
	public List<Object> dependences = new ArrayList<Object>();
	
	public void setDirectory(Path dir) {
		this.dir = dir;
	}
	
	public Lang getLanguage() {
		return Lang.get(lang);
	}
	
	public Path getMain() {
		return dir.resolve(main).normalize();
	}
	
	public Collection<Path> getLibs() {
		Collection<Path> libs = new ArrayList<Path>();
		
		for(String str : this.libs) {
			libs.add(dir.resolve(str).normalize());
		}
		return libs;
	}
	
	public Collection<Path> getDeploy(Project project) {
		Collection<Path> deploy = new ArrayList<Path>();
		deploy.add(project.getDefaultDeployDir());
		
		for(String str : this.deploy) {
			deploy.add(dir.resolve(str).normalize());
		}
		return deploy;
	}
	
	public Collection<Path> getExterns() {
		Collection<Path> exports = new ArrayList<Path>();
		
		for(String str : this.exports) {
			exports.add(dir.resolve(str).normalize());
		}
		return exports;
	}
	
	public Set<Source> getSources(String name) {
		Set<Source> sources = new HashSet<Source>();
		
		for(Object obj : dependences) {
			sources.addAll(getDependentProject(obj).getSources());
		}

		sources.add(new Source(name, source, dir));
		return sources;
	}

	private Project getDependentProject(Object obj) {
		if(obj instanceof String) {
			return Project.load((String) obj, dir);
		}
		
		if(obj instanceof List) {
			@SuppressWarnings("unchecked")
			List<String> list = (List<String>) obj;
			return Project.load(list.get(0), dir.resolve(list.get(1)));
		}
		
		throw new IllegalArgumentException("Dependency isn't a String nor a List");
	}
	
	@Override public String toString() {
		return String.format("dir %s, source: %s, main: %s, dependeces, %s", dir, source, main, dependences);
	}
}
