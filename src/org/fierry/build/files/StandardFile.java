package org.fierry.build.files;

import org.fierry.build.utils.Lines;

public class StandardFile {

	protected String content;
	
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