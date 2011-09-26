package org.fierry.build.files;

import org.fierry.build.utils.FileUtils;

public class StandardFile {

	protected String content;
	
	public void deploy(StringBuilder builder) {
		if(content.length() > 0) {
			builder.append(content);
			builder.append(FileUtils.getLineSeparator());
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