package org.fierry.build.yaml;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.yaml.snakeyaml.constructor.Constructor;

public class Yaml {

	public static<T> T load(Class<T> cls, Path path, Boolean initialize) {
		if(!Files.exists(path) && initialize) {
			return initializeClass(cls);
		}
		org.yaml.snakeyaml.Yaml yaml = new org.yaml.snakeyaml.Yaml(new Constructor(cls));

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
	
}
