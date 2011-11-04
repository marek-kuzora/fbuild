package org.fierry.build.resources;

import java.nio.file.Path;

import org.fierry.build.utils.Extension;

public abstract class Resource {

	protected String name;
	
	public Resource(Path path) {
		this.name = Extension.trim(path);
	}
	
	public String getName() {
		return name;
	}
	
	@Override public int hashCode() {
		return name.hashCode();
	}
}
