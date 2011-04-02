package org.fierry.build.io;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;

public class Files {

	static public final String PROJECT_FILE = ".root.yml";
	static public final String PACKAGE_FILE = ".package.yml";

	static public Collection<FileNode> toFileNodes(FileNode parent, Collection<String> paths) {
		Collection<FileNode> nodes = new ArrayList<FileNode>();
		
		for(String path : paths) { nodes.add(parent.get(path)); }
		return nodes;
	}
	
	static public FileNode getCurrentDirectory() {
		return new FileNode("");
	}
	
	static public FileNode getProjectDirectory() {
		FileNode node = getProjectFile();
		return node != null ? node.parent() : null;
	}
	
	static public FileNode getProjectFile() {
		FileNode dir = getCurrentDirectory();

		while(dir.parent() != null) {
			FileNode file = dir.get(PROJECT_FILE);
			
			if(file.exists()) { return file; }
			else { dir = dir.parent(); }
		}
		return null;
	}
	
	static private FileNode getJarDirectory() {
		try { return new FileNode(Files.class.getProtectionDomain().getCodeSource().getLocation().toURI()); } 
		catch (URISyntaxException e) { throw new RuntimeException(e); }
	}
	static public FileNode getBuildDirectory() {
		return getJarDirectory().parent().get("targets");
	}
	
}
