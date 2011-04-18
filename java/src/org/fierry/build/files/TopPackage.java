package org.fierry.build.files;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.fierry.build.projects.IProject;

public class TopPackage {
	
	private String name;
	private Map<Path, Package> pkgs;
	
	/**
	 * Constructs  top package.
	 * @param name - package name:
	 *  <project-name> -> if the package is src-package
	 *  <project-name>.<package-dir> -> otherwise
	 * @param project 
	 */
	public TopPackage(String name) {
		this.name = name;
		this.pkgs = new HashMap<Path, Package>();
	}
	
	/**
	 * Builds top package content & deploys into specified locations.
	 * @param dirs
	 */
	public void deploy(IProject project, Set<Path> dirs) {
		Set<Package> set = new HashSet<Package>();
		Queue<Package> queue = new LinkedList<Package>(pkgs.values());
		
		while(!queue.isEmpty()) {
			Package pkg = queue.poll();
			if(!set.contains(pkg)) {
				set.add(pkg);
				queue.addAll(pkg.getRequiredPackages(project));
			}
		}
		
		StringBuilder builder = new StringBuilder();
		for(Package pkg : set) { 
			pkg.appendTo(builder);
		}
		
		for(Path path : dirs) {
			try { 
				Files.createDirectories(path);
				Files.write(path.resolve(name + ".js"), builder.toString().getBytes()); 
			} 
			catch (IOException e) { throw new RuntimeException(e); }
		}
	}
	
	/**
	 * Sets package with the given path.
	 * @param path - package location.
	 */
	public void setPackage(Path path) {
		pkgs.put(path, new Package(getPackageName(path)));
	}
	
	private String getPackageName(Path path) {
		return name + "." + path.toString().replaceAll(File.separator, ".");
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
