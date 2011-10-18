package org.fierry.build.files;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.fierry.build.app.Project;
import org.fierry.build.utils.Extension;
import org.fierry.build.view.core.Deployable;
import org.fierry.build.view.core.Root;

public class RootFile {
	
	private String name;
	private Project project;
	private Map<String, String> requires;
	
	private Collection<Root> roots;
	
	public RootFile(Project project, Path path) {
		this.project  = project;
		this.name     = Extension.trim(path);
		this.roots    = new ArrayList<Root>();
		this.requires = new HashMap<String, String>();
	}
	
	public void setContent(Collection<Root> roots) {
		for(Root root : this.roots) {
			root.uninstall(project, this);
		}
		
		requires.clear();
		
		for(Root root : roots) {
			root.install(project, this);
		}
		
		this.roots = roots;
	}
	
	public void addRequire(String namespace, String module) {
		assert !requires.containsKey(namespace) : "Namespace is already defined: " + namespace;
		requires.put(namespace, module);
	}
	
	public Map<String, String> getRequires() {
		return new HashMap<String, String>(requires);
	}
	
	public String getName() {
		return name;
	}
	
	public void deploy(StringBuilder builder) {
		for(Root root : roots) {
			if(root instanceof Deployable) {
				root.consult(null, null);
				((Deployable) root).deploy(builder);
			}
		}
	}
}
