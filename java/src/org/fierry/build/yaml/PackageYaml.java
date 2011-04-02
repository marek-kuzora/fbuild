package org.fierry.build.yaml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PackageYaml {

	public String name;
	public List<String> after;
	public List<String> before;
	
	public List<String> require;
	public Map<String, String> namespace;
 	
	protected PackageYaml() {
		after = new ArrayList<String>();
		before = new ArrayList<String>();
		
		require = new ArrayList<String>();
		namespace = new HashMap<String, String>();
	}
}
