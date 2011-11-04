package org.fierry.build.resources;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.fierry.build.linking.GlobalConfig;
import org.fierry.roots.api.IDeployableRoot;
import org.fierry.roots.api.IRoot;

public class Roots extends Resource {
	
	private Map<String, String> requires;
	
	private Collection<IRoot> roots;
	
	public Roots(Path path) {
		super(path);
		
		this.roots    = new ArrayList<IRoot>();
		this.requires = new HashMap<String, String>();
	}
	
	public void setContent(Collection<IRoot> roots) {
		requires.clear();
		
		for(IRoot root : roots) {
			root.configure();
		}
		
		for(IRoot root : roots) {
			root.install(this);
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
	
	public void deploy(StringBuilder builder, GlobalConfig conf) {
		for(IRoot root : roots) {
			if(root instanceof IDeployableRoot) {
				root.consult(null, null, conf);
				((IDeployableRoot) root).deploy(builder);
			}
		}
	}
}
