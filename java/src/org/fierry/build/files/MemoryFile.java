package org.fierry.build.files;

public class MemoryFile {

	private String name;
	private String content;
	
	public MemoryFile(String name, String content) {
		this.name = name;
		this.content = content;
	}
	
	public String getName() {
		return name;
	}
	
	public String getOutput() {
		return content;
	}

}
