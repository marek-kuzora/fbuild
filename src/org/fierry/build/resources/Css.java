package org.fierry.build.resources;

import java.nio.file.Path;

import org.fierry.build.utils.Lines;

public class Css extends Resource {
	
	public Css(Path path) {
		super(path);
	}
	
	public void deploy(StringBuilder builder) {
		if(content.length() > 0) {
			builder.append(content);
			builder.append(Lines.separator);
		}
	}
}