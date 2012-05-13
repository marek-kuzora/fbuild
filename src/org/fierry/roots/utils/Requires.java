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
			
			Boolean upperCase = Character.isUpperCase(e.getKey().charAt(0));
			String[] value = e.getValue().split("\\.");

			
			Template.get("modules/script_require")
					.replace("name", e.getKey())
					.replace("path", value[0])
					.replace("tail", value.length == 2 && value[1] != "" ? "." + value[1] : "")
					.replace("require", upperCase ? "F.srequire" : "F.require")
					.appendTo(builder);
			
			
		}
		return builder.toString();
	}
}
