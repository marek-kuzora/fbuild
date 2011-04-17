package org.fierry.build.files;

import java.util.Collection;
import java.util.Map;

public class PackageBuilder {

	private StringBuilder builder;
	
	public PackageBuilder(StringBuilder builder) {
		this.builder = builder;
	}
	
	public void buildHeading(String name) {
		builder.append("YUI.add( '"+ name + "', function( Env ) {\r\n\r\n");
	}
	
	public void buildNamespaces(Map<String, String> namespaces) {
		for(Map.Entry<String, String> entry : namespaces.entrySet()) {
			builder.append("var ");
			builder.append(entry.getKey());
			builder.append(" = Env.namespace('");
			builder.append(entry.getValue());
			builder.append("');\r\n");
		}
	}
	
	public void buildFiles(Map<String, MemoryFile> files, Collection<String> before, Collection<String> after) {
		for(String file : before) { appendSpecialFile(files, file); }
		
		for(String file : files.keySet()) {
			if(!before.contains(file) && !after.contains(file)) {
				builder.append(files.get(file).getOutput());
				builder.append("\r\n");
			}
		}
		
		for(String file : after) { appendSpecialFile(files, file); }
	}
	
	private void appendSpecialFile(Map<String, MemoryFile> files, String file) {
		if(files.containsKey(file)) {
			builder.append(files.get(file).getOutput());
			builder.append("\r\n");
		}
		else { System.err.println("File not found: " + file); }
	}
	
	public void buildFooter(Collection<String> requires) {
		boolean first = true;
		builder.append("\r\n}, '3.0' ,{requires:[");
		
		for(String require : requires) {
			if(first) { first = false; }
			else { builder.append(", "); }
			
			builder.append("'");
			builder.append(require);
			builder.append("'");
		}
		builder.append("]});\r\n\r\n");
	}
	
	public StringBuilder getResult() {
		return builder;
	}
}
