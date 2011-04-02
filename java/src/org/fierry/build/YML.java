package org.fierry.build;

import java.io.IOException;

import org.fierry.build.io.FileNode;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class YML {

	static private Yaml instance;
	
	static {
		DumperOptions options = new DumperOptions();
		options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		
		instance = new Yaml(options);
	}
	
	static public Yaml get() {
		return instance;
	}
	
	static public<T> T decode(Class<T> cls, FileNode node) {
		Yaml yaml = new Yaml(new Constructor(cls));
		
		try { return cls.cast(yaml.load(node.inputStream())); }
		catch(IOException e) { throw new RuntimeException(e); }
	}
	
}
