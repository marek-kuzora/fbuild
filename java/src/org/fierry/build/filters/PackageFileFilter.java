package org.fierry.build.filters;

import java.nio.file.Path;

import org.fierry.build.files.PackageY;
import org.fierry.build.projects.IProject;
import org.fierry.build.utils.Resources;

public class PackageFileFilter implements IFileFilter {
	public static final String FILE = ".package.yml";

	@Override
	public Boolean accept(Path path, IProject project) {
		return FILE.equals(path.getFileName().toString());
	}

	@Override
	public void fileCreated(Path path, IProject project) {
		fileUpdated(path, project);
	}

	@Override
	public void fileUpdated(Path path, IProject project) {
		PackageY conf = Resources.loadYaml(PackageY.class, project.getDirectory().resolve(path));
		
		conf.validateRequires(project);
		project.getPackage(path.getParent()).setConfig(conf);
	}
	

	@Override
	public void fileDeleted(Path path, IProject project) {
		project.getPackage(path.getParent()).setConfig(PackageY.EMPTY);
	}

}
