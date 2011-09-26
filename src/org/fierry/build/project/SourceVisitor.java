package org.fierry.build.project;

import static com.barbarysoftware.watchservice.StandardWatchEventKind.*;
import static java.nio.file.FileVisitResult.*;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.Map;

import org.fierry.build.app.Project;
import org.fierry.build.filters.FileFiltersRegistry;

import com.barbarysoftware.watchservice.WatchKey;
import com.barbarysoftware.watchservice.WatchService;
import com.barbarysoftware.watchservice.WatchableFile;

public class SourceVisitor implements FileVisitor<Path> {

	private Project project;
	private FileFiltersRegistry filters;
	private WatchService watcher;
	
	private Map<WatchKey, Path> paths;
	private Map<Path, FileTime> files;
	
	public SourceVisitor(Project project, WatchService watcher, FileFiltersRegistry filters, Map<WatchKey, Path> paths, Map<Path, FileTime> files) {
		this.project = project;
		this.watcher = watcher;

		this.paths = paths;
		this.files = files;
		this.filters = filters;
	}
	
	@Override public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		WatchKey key = new WatchableFile(dir.toFile()).register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
		paths.put(key, dir);
		files.put(dir, Files.getLastModifiedTime(dir));

		filters.get(dir, project).fileCreated(dir, project);
		return CONTINUE;
	}

	@Override public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		files.put(file, Files.getLastModifiedTime(file));
		
		filters.get(file, project).fileCreated(file, project);
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
