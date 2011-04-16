package org.fierry.build.files;

import java.util.HashMap;
import java.util.Map;

public class Package {

	private String name;
	private PackageY conf;
	private Map<String, MemoryFile> files;
	
	/**
	 * Constructs the package
	 * @param name - full package name.
	 */
	public Package(String name) {
		this.name = name;
		this.conf = PackageY.EMPTY;
		this.files = new HashMap<String, MemoryFile>();
	}
	
	/**
	 * Sets package's additional information from the configuration object.
	 * @param conf - package configuration.
	 */
	public void setConfig(PackageY conf) {
		this.conf = conf;
	}

	/**
	 * Sets memory file with the given name.
	 * @param name - file name.
	 * @param file
	 */
	public void setFile(MemoryFile file) {
		files.put(file.getName(), file);
	}
	
	/**
	 * Deletes memory file corresponding to the given name.
	 * @param name - file name.
	 */
	public void deleteFile(String name) {
		files.remove(name);
	}
	
}
