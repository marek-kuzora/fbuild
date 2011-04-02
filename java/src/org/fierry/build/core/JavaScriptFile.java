package org.fierry.build.core;

import org.fierry.build.io.FileNode;

public class JavaScriptFile {

	private FileNode file;
	
	public JavaScriptFile(FileNode file) {
		this.file = file;
	}
	
	public String getContent() {
		return file.read();
	}
	
	@Override public String toString() {
		return file.name();
	}
	
	public FileNode getFile() {
		return file;
	}
}
