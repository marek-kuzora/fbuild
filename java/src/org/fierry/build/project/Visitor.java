package org.fierry.build.project;

import static java.nio.file.FileVisitResult.*;
import static java.nio.file.StandardWatchEventKind.*;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;

import org.fierry.build.projects.IProject;
import org.fierry.build.utils.FiltersRegistry;

public class Visitor implements FileVisitor<Path> {

	private IProject project;
	private FiltersRegistry filters;
	private WatchService watcher;
	
	private Map<WatchKey, Path> paths;
	
	public Visitor(IProject project, WatchService watcher, FiltersRegistry filters, Map<WatchKey, Path> paths) {
		this.project = project;
		this.watcher = watcher;

		this.paths = paths;
		this.filters = filters;
	}
	
	@Override public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		if(project.isReleaseDirectory(dir)) { return SKIP_SUBTREE; }
		
		WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
		paths.put(key, dir);
		
		Path relative = project.getDirectory().relativize(dir);
		filters.get(relative, project).fileCreated(relative, project);
		
		return CONTINUE;
	}

	@Override public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		Path relative = project.getDirectory().relativize(file);
		filters.get(relative, project).fileCreated(relative, project);
		
		return CONTINUE;
	}

	@Override public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
		return CONTINUE;
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		return CONTINUE;
	}
}
