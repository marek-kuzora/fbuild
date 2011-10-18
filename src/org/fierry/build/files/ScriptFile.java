package org.fierry.build.files;

import java.nio.file.Path;

import org.fierry.build.utils.Extension;
import org.fierry.build.utils.Resource;

public class ScriptFile extends StandardFile {

	private String name;
	
	public ScriptFile(Path path) {
		this.name = Extension.trim(path);
	}
	
	public void deploy(StringBuilder builder) {
		Resource.get("script_module")
				.replace("name", name)
				.replaceLine("content", content)
				.appendTo(builder);
	}

	public String getName() {
		return name;
	}
}
