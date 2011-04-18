package org.fierry.build.files;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.fierry.build.projects.IProject;

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
	
	public void appendTo(StringBuilder builder) {
		PackageBuilder pbuilder = new PackageBuilder(builder);
		
		pbuilder.buildHeading(name);
		pbuilder.buildNamespaces(conf.namespace);
		
		pbuilder.buildFiles(files, conf.before, conf.after);
		pbuilder.buildFooter(conf.require);
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
	
	public Collection<Package> getRequiredPackages(IProject project) {
		Collection<Package> pkgs = new HashSet<Package>();

		for(String name : conf.vrequire) {
			pkgs.add(project.getPackage(name));
		}
		return pkgs;
	}
	
	@Override public int hashCode() {
		return name.hashCode();
	}
	
	@Override public boolean equals(Object obj) {
		if(!(obj instanceof Package)) { return false; }
		return obj != null && hashCode() == obj.hashCode();
	}
}
