package org.fierry.build.utils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class Resources {

	public static Path getBuildDirectory() {
		try { return Paths.get(Resources.class.getProtectionDomain().getCodeSource().getLocation().toURI()); }
		catch(URISyntaxException e) { throw new RuntimeException(e); }
	}
	
	public static<T> T loadYaml(Class<T> cls, Path path) {
		Yaml yaml = new Yaml(new Constructor(cls));

		try { return cls.cast(yaml.load(new String(Files.readAllBytes(path)))); }
		catch(IOException e) { throw new RuntimeException(e); }
	}
}
