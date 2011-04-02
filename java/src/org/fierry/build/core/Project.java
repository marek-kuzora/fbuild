package org.fierry.build.core;

import java.util.Collection;

import org.fierry.build.YML;
import org.fierry.build.io.FileNode;
import org.fierry.build.io.Files;
import org.fierry.build.yaml.ProjectYaml;

public class Project extends Package {

	public Project() {
		super(Files.getProjectFile(), null);
	}
	
	@Override protected void load(FileNode file) {
		raw = YML.decode(ProjectYaml.class, Files.getProjectFile());
	}

	public Collection<String> getRawRequire() {
		return raw.require;
	}
	
	public Collection<FileNode> getDeployLocations() {
		FileNode dir = location.get("../");
		return Files.toFileNodes(dir, ((ProjectYaml) this.raw).deploy);
	}
	
	@Override public Project getProject() {
		return this;
	}
	
	@Override public String getName() {
		return raw.name;
	}
	
}
