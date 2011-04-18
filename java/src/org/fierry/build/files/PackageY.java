package org.fierry.build.files;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.fierry.build.projects.IProject;

public class PackageY {
	public static final PackageY EMPTY = new PackageY();
	
	public Collection<String> after;
	public Collection<String> before;
	
	public Collection<String> require;
	public Collection<String> vrequire;
	public Map<String, String> namespace;
	
	public PackageY() {
		after = new ArrayList<String>();
		before = new ArrayList<String>();
		
		require = new ArrayList<String>();
		vrequire = new ArrayList<String>();
		namespace = new HashMap<String, String>();
	}
	
	public void validateRequires(IProject project) {
		for(String name : require) {
			if(project.isProjectReferenced(name)) {
				vrequire.add(name);
			} else {
				System.out.println("Package is missing: " + name);
			}
		}
	}
}
