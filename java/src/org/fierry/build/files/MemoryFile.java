package org.fierry.build.files;

public class MemoryFile {

	private String name;
	private StringBuilder builder;
	
	public MemoryFile(String name, String content) {
		this.name = name;
		this.builder = new StringBuilder().append(content);
	}
	
	public String getName() {
		return name;
	}
	
	public StringBuilder getOutput() {
		return builder;
	}

}
