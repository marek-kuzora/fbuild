package org.fierry.build.project;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Source {

	private Path project;
	private Path absolute;

	public Source(String name, String path, Path dir) {
		assert name.indexOf('/') == -1 && name.indexOf('\\') == -1 && name.indexOf('.') == -1 :
			"Source name cannot contain any '\\', '/' or '.' characters.";
		
		this.project  = Paths.get(name);
		this.absolute = dir.resolve(path).normalize();
	}
	
	public Boolean accept(Path path) {
		return path.isAbsolute() ? path.startsWith(absolute): path.startsWith(project); 
	}
	
	public Path toProjectPath(Path path) {
		return project.resolve(absolute.relativize(path));
	}
	
	public Path toAbsolutePath(Path path) {
		return absolute.resolve(project.relativize(path));
	}
	
	public Boolean exists() {
		return Files.exists(absolute);
	}
	
	public Path getAbsolutePath() {
		return absolute;
	}
	
	@Override public int hashCode() {
		return absolute.hashCode();
	}
	
	@Override public String toString() {
		return project.toString() + ": " + absolute;
	}
	
}