package org.fierry.build.utils;

import java.nio.file.Path;

public class FileUtils {

	public static String getLineSeparator() {
		return System.getProperty("line.separator");
	}
	
	public static String getExtension(Path path) {
		String name = path.getFileName().toString();
		Integer idx = name.lastIndexOf('.');
		return idx < 0 ? name : name.substring(name.lastIndexOf('.'));
	}
}
