package org.fierry.build.resources;

import java.nio.file.Path;

import org.fierry.build.utils.Extension;

public abstract class Resource {

	protected String name;
	protected String content = "";

	public Resource(Path path) {
		this.name = Extension.trim(path);
	}
	
	public void setContent(byte[] content) {
		setContent(new String(content));
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public void removeContent() {
		this.content = "";
	}
	
	public String getName() {
		return name;
	}
	
	@Override public int hashCode() {
		return name.hashCode();
	}
}
