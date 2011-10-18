package org.fierry.build.utils;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import org.fierry.build.app.Build;

public class Directory {
	private static final String TEMPLATE_EXT = ".template";
	private static final String TEMPLATE_DIR = "/org/fierry/build/templates" + File.separator;
	
	public static Path getBuild() {
		return getBuild(false);
	}
	
	public static Path getBuild(Boolean quiet) {
		Path dir = getRun();
		while(dir != null) {
			Path file = dir.resolve(Build.FILE);
			
			if(Files.exists(file)) { return dir; }
			else { dir = dir.getParent(); }
		}
		
		if(quiet) { return getRun(); }
		else { throw new IllegalStateException("Build file not found: " + getRun()); }
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
	
	// TODO remove
	public static String getTempla2te(String resource) {
		InputStream in = Directory.class.getResourceAsStream(TEMPLATE_DIR + resource + TEMPLATE_EXT);
		return new Scanner(in).useDelimiter("\\A").next();
	}
}
