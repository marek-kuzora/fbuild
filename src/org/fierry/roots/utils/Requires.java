package org.fierry.roots.utils;

import java.util.Map;
import java.util.Map.Entry;

import org.fierry.build.utils.Template;

public class Requires {
	private static final String VAR = "_require_";

	private Integer i;
	private Map<String, String> requires;
	
	public Requires(Map<String, String> requires) {
		this.i = 0;
		this.requires = requires;
	}
	
	public String require(String path) {
		if(requires.containsValue(path)) {
			for(Entry<String, String> e : requires.entrySet()) {
				if(e.getValue().equals(path)) { return e.getKey(); }
			}
		}
		
		while(requires.containsKey(VAR + i)) { i++; }
		
		requires.put(VAR + i, path);
		return VAR + i;
	}
	
	public String deploy() {
		StringBuilder builder = new StringBuilder();
		
		for(Entry<String, String> e : requires.entrySet()) {

			// Do not require other root modules.
			if(e.getValue().contains(":")) { continue; }
			
			Template.get("nodes/require")
					.replace("name", e.getKey())
					.replace("path", e.getValue())
					.appendTo(builder);
		}
		return builder.toString();
	}
}
