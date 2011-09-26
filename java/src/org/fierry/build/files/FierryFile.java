package org.fierry.build.files;

import java.nio.file.Path;

/**
 * Plik odpowiedzialny za trzymanie wewnątrz kodu JS oraz linkowanie konfiguracji.
 */
public class FierryFile extends StandardFile {

	private String name;
	
	public FierryFile(Path path) {
		String name = path.toString();
		Integer idx = name.lastIndexOf('.');
		
		this.name = idx < 0 ? name : name.substring(0, name.lastIndexOf('.'));
	}
	
	public void deploy(StringBuilder builder) {
		builder.append("modules['");
		builder.append(name);
		builder.append("'] = function(require, exports) {\r\n");
		builder.append(content);
		builder.append("\r\n}\r\n");
	}

}
