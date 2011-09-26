package org.fierry.build.yaml;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.fierry.build.app.Project;
import org.fierry.build.project.Source;
import org.fierry.build.utils.Resources;

public class ProjectY {
	
	private Path dir;
	
	public String main;
	public List<?> source;
	public List<String> deploy;
	public List<String> libs;
	public List<String> exports;
	
	public ProjectY() {
		this.dir  = Resources.getBuildDirectory();
	}
	
	public Path getMain() {
		return dir.resolve(main).normalize();
	}
	
	public Collection<Path> getLibs() {
		Collection<Path> libs = new ArrayList<Path>();
		
		if(this.libs != null) {
			for(String str : this.libs) {
				libs.add(dir.resolve(str).normalize());
			}
		}
		return libs;
	}
	
	public Collection<Path> getDeploy(Project project) {
		Collection<Path> deploy = new ArrayList<Path>();
		deploy.add(project.getDefaultDeployDir());
		
		if(this.deploy != null) {
			for(String str : this.deploy) {
				deploy.add(dir.resolve(str).normalize());
			}
		}
		return deploy;
	}
	
	public Collection<Path> getExports() {
		Collection<Path> exports = new ArrayList<Path>();
		
		if(this.exports != null) {
			for(String str : this.exports) {
				exports.add(dir.resolve(str).normalize());
			}
		}
		return exports;
	}

	public Collection<Source> getSources() {
		Collection<Source> sources = new ArrayList<Source>();
		
		if(this.source != null) {
			for(Object obj : this.source) {
				if(obj instanceof String) {
					String str = (String) obj;
					sources.add(new Source(str, dir));
				}
				
				if(obj instanceof List) {
					@SuppressWarnings("unchecked")
					List<String> list = (List<String>)obj;
					sources.add(new Source(list.get(0), list.get(1), dir));
				}
			}
		}
		return sources;
	}
}
