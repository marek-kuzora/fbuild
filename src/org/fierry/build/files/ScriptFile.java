package org.fierry.build.files;

import java.nio.file.Path;

import org.fierry.build.utils.Resources;

public class ScriptFile extends StandardFile {

	private String name;
	
	public ScriptFile(Path path) {
		String name = path.toString();
		Integer idx = name.lastIndexOf('.');
		
		this.name = idx < 0 ? name : name.substring(0, name.lastIndexOf('.'));
	}
	
	public void deploy(StringBuilder builder) {
		builder.append(Resources.getTemplate("module")
				.replaceAll("\\$\\{name\\}", name)
				.replaceAll("\\$\\{content\\}", content));
	}

	public String getName() {
		return name;
	}
}
