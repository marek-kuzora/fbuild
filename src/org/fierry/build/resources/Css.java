package org.fierry.build.resources;

import java.nio.file.Path;

import org.fierry.build.utils.Lines;

public class Css extends Resource {

	protected String content = "";
	
	public Css(Path path) {
		super(path);
	}
	
	public void deploy(StringBuilder builder) {
		if(content.length() > 0) {
			builder.append(content);
			builder.append(Lines.separator);
		}
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
}