package org.fierry.build.projects;

import java.util.ArrayList;
import java.util.Collection;

public class ProjectY {
	
	public String src;
	public String name;
	public Collection<String> deploy;
	public Collection<String> dependences;
	
	public ProjectY() {
		deploy = new ArrayList<String>();
		dependences = new ArrayList<String>();
	}
}
