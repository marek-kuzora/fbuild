package org.fierry.build.files;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.fierry.build.projects.IProject;

public class TopPackage {
	
	private String name;
	private IProject project;
	private Map<Path, Package> pkgs;
	
	/**
	 * Constructs  top package.
	 * @param name - package name:
	 *  <project-name> -> if the package is src-package
	 *  <project-name>.<package-dir> -> otherwise
	 * @param project 
	 */
	public TopPackage(String name, IProject project) {
		this.name = name;
		this.project = project;
		this.pkgs = new HashMap<Path, Package>();
	}
	
	/**
	 * Builds top package content & deploys into specified locations.
	 * @param dirs
	 */
	public void deploy(Set<Path> dirs) {
		
	}
		
	/**
	 * Sets package with the given path.
	 * @param path - package location.
	 */
	public void setPackage(Path path) {
		pkgs.put(path, new Package(getPackageName(path)));
	}
	
	private String getPackageName(Path path) {
		return path.toString().replaceAll(File.pathSeparator, ".");
	}
	
	/**
	 * Deletes package corresponding to the given path.
	 * @param path - package location.
	 */
	public void deletePackage(Path path) {
		pkgs.remove(path);
	}
	
	/**
	 * Returns package corresponding to the given path.
	 * @param path - package location
	 */
	public Package getPackage(Path path) {
		Package pkg = pkgs.get(path);
		
		assert pkg != null : "Package not found in: " + path;
		return pkg;
	}
	
}
