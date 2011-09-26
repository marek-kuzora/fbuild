package org.fierry.build.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import org.fierry.build.app.Build;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class Resources {
	private static final String TEMPLATE_EXT = ".template";
	private static final String TEMPLATE_DIR = "../templates" + File.separator;
	
	public static Path getBuildDirectory() {
		return getBuildDirectory(false);
	}
	
	public static Path getBuildDirectory(Boolean quiet) {
		Path dir = getRunDirectory();
		while(dir != null) {
			Path file = dir.resolve(Build.FILE);
			
			if(Files.exists(file)) { return dir; }
			else { dir = dir.getParent(); }
		}
		
		if(quiet) { return getRunDirectory(); }
		else { throw new IllegalStateException("Build file not found: " + getRunDirectory()); }
	}
	
	public static Path getRunDirectory() {
		return Paths.get("").toAbsolutePath();
	}
	
	public static Path getJarDirectory() {
		try { 
			URI uri = Resources.class.getProtectionDomain().getCodeSource().getLocation().toURI();
			return Paths.get(uri).resolve("../").normalize(); 
		}
		catch(URISyntaxException e) { throw new RuntimeException(e); }
	}
	
	public static<T> T loadYaml(Class<T> cls, Path path, Boolean initialize) {
		if(!Files.exists(path) && initialize) {
			return initializeClass(cls);
		}
		Yaml yaml = new Yaml(new Constructor(cls));

		try { return cls.cast(yaml.load(new String(Files.readAllBytes(path)))); }
		catch(IOException e) { throw new RuntimeException(e); }
	}
	
	private static<T> T initializeClass(Class<T> cls) {
		try {
			java.lang.reflect.Constructor<T> constructor = cls.getDeclaredConstructor();
			constructor.setAccessible(true);
			return constructor.newInstance();
		}
		catch (SecurityException e) { throw new RuntimeException(e); }
		catch (NoSuchMethodException e) { throw new RuntimeException(e); }
		catch (InstantiationException e) { throw new RuntimeException(e); }
		catch (IllegalAccessException e) { throw new RuntimeException(e); }
		catch (InvocationTargetException e) { throw new RuntimeException(e); }
	}
	
	public static String getTemplate(String resource) {
		InputStream in = Resources.class.getResourceAsStream(TEMPLATE_DIR + resource + TEMPLATE_EXT);
		return new Scanner(in).useDelimiter("\\A").next();
	}
}
