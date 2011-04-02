package org.fierry.build.core;

import java.util.Collection;
import java.util.Map;

import org.fierry.build.io.FileNode;

public class PackageBuilder {

	private StringBuilder builder;
	
	public PackageBuilder() {
		builder = new StringBuilder();
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
	
	public void buildFiles(Map<FileNode, JavaScriptFile> files, Collection<FileNode> before, Collection<FileNode> after) {
		for(FileNode file : before) { appendSpecialFile(files, file); }
		
		for(FileNode file : files.keySet()) {
			if(!before.contains(file) && !after.contains(file)) {
				builder.append(files.get(file).getContent());
				builder.append("\r\n");
			}
		}
		
		for(FileNode file : after) { appendSpecialFile(files, file); }
	}
	
	private void appendSpecialFile(Map<FileNode, JavaScriptFile> files, FileNode file) {
		if(files.containsKey(file)) {
			builder.append(files.get(file).getContent());
			builder.append("\r\n");
		}
		else { System.err.println("File not found: " + file.path()); }
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
