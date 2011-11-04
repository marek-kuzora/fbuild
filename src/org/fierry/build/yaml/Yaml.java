package org.fierry.build.yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.yaml.snakeyaml.constructor.Constructor;

public class Yaml {

	public static<T> T load(Class<T> cls, Path path) {
		org.yaml.snakeyaml.Yaml yaml = new org.yaml.snakeyaml.Yaml(new Constructor(cls));

		try { return cls.cast(yaml.load(new String(Files.readAllBytes(path)))); }
		catch(IOException e) { throw new RuntimeException(e); }
	}
	
}
