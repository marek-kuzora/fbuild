package org.fierry.build.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.fierry.build.YML;
import org.fierry.build.io.FileNode;
import org.fierry.build.io.Files;
import org.fierry.build.visitor.IVisitor;
import org.fierry.build.yaml.PackageYaml;

public class Package {
	
	private Boolean changed;
	
	private Project project;
	private Package parent;
	
	protected FileNode location;
	protected PackageYaml raw;
	
	private List<Package> pkgs;
	private Map<FileNode, JavaScriptFile> files;
	
	public Package(FileNode location, Package parent) {
		this.location = location;
		this.parent = parent;
		if(parent != null) { 
			parent.addPackage(this); 
			project = parent.getProject();
		}

		this.pkgs = new ArrayList<Package>();
		this.files = new HashMap<FileNode, JavaScriptFile>();
		
		load(location);
	}

	protected void load(FileNode file) {
		raw = YML.decode(PackageYaml.class, file);
	}
	
	public void accept(IVisitor visitor) {
		visitor.visit(this);
	}
	
	public StringBuilder getOutput() {
		PackageBuilder builder = new PackageBuilder();
		
		builder.buildHeading(getName());
		builder.buildNamespaces(raw.namespace);
		
		builder.buildFiles(files, Files.toFileNodes(location.get("../"), raw.before), Files.toFileNodes(location.get("../"), raw.after));
		builder.buildFooter(getRequire());
		
		return builder.getResult();
	}
	
	protected void setChanged() {
		changed = true;
	}
	
	public void addFile(JavaScriptFile file) {
		files.put(file.getFile(), file);
		setChanged();
	}
	
	public void addPackage(Package pkg) {
		pkgs.add(pkg);
		setChanged();
	}
	
	public String getName() {
		return parent.getName() + "." + raw.name;
	}
	
	public Set<String> getRequire() {
		Set<String> set = new HashSet<String>(raw.require);
		
		for(Package pkg : pkgs) { set.add(pkg.getName()); }
		if(pkgs.isEmpty()) { set.addAll(getProject().getRawRequire()); }
		
		return set;
	}
	
	protected Project getProject() {
		return project;
	}
	
	public Collection<Package> getPackages() {
		return pkgs;
	}
	
	public Collection<JavaScriptFile> getFiles() {
		return files.values();
	}
}