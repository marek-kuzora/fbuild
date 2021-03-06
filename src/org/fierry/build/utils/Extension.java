package org.fierry.build.utils;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Extension {

	public static String get(Path path) {
		String name = path.getFileName().toString();
		Integer idx = name.lastIndexOf('.');
		return idx < 0 ? name : name.substring(name.lastIndexOf('.'));
	}
	
	public static String trim(Path path) {
		String name = path.toString();
		Integer idx = name.lastIndexOf('.');
		
		return idx < 0 ? name : name.substring(0, name.lastIndexOf('.'));
	}
	
	public static String trim(String path) {
		return trim(Paths.get(path));
	}
}
