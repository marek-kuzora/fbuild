package org.fierry.build.utils;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.fierry.build.app.Build;

public class Directory {
	
	public static Path getBuild() {
		Path dir = getInternalBuild();
		
		assert dir != null : "Build file not found: " + getRun();
		return dir;
	}
	
	public static boolean existsBuild() {
		return getInternalBuild() != null;
	}
	
	private static Path getInternalBuild() {
		Path dir = getRun();
		while(dir != null) {
			if(Files.exists(dir.resolve(Build.FILE))) { return dir; }
			else { dir = dir.getParent(); }
		}
		return null;
	}
	
	public static Path getRun() {
		return Paths.get("").toAbsolutePath();
	}
	
	public static Path getJar() {
		try { 
			URI uri = Directory.class.getProtectionDomain().getCodeSource().getLocation().toURI();
			return Paths.get(uri).resolve("../").normalize(); 
		}
		catch(URISyntaxException e) { throw new RuntimeException(e); }
	}
}
