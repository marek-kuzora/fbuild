package org.fierry.build;

import static java.nio.file.StandardWatchEventKind.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Map;

import org.fierry.build.filters.IFileFilter;
import org.fierry.build.projects.IProject;
import org.fierry.build.utils.FiltersRegistry;

public class ProjectThread extends Thread {
	private IProject project;
	private FiltersRegistry filters;
	private WatchService watcher;
	private Map<WatchKey, Path> paths;
	
	public ProjectThread(IProject project, WatchService watcher, FiltersRegistry filters, Map<WatchKey, Path> paths) {
		this.project = project;
		this.watcher = watcher;

		this.paths = paths;
		this.filters = filters;
	}
	
	@Override public void run() {
		while(true) {
			try {
				Path dir = project.getDirectory();
				WatchKey key = watcher.take();
				
				for (WatchEvent<?> event: key.pollEvents()) {
					Path abs = absolutePath(key, event);
					Path file = dir.relativize(abs);

					if(project.isReleaseDirectory(abs)) { continue; }
					IFileFilter filter = filters.get(file, project);
					
					if(event.kind() == ENTRY_CREATE) {
						filter.fileCreated(file, project);
						if(Files.isDirectory(abs)) { registerDirectoryWatcher(abs); }
					}
					
					if(event.kind() == ENTRY_DELETE) { filter.fileDeleted(file, project); }
					if(event.kind() == ENTRY_MODIFY) { filter.fileUpdated(file, project); }
				}
				key.reset();
			}
			catch(InterruptedException e) { return; }
		}
	}
	
	/**
	 * Returns absolute file path for the given key & event.
	 * @param key
	 * @param event
	 */
	private Path absolutePath(WatchKey key, WatchEvent<?> event) {
		return paths.get(key).resolve((Path)event.context());
	}
	
	/**
	 * Register newly created directory into WatchService.
	 * @param dir
	 */
	private void registerDirectoryWatcher(Path dir) {
		try {
			WatchKey ckey = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
			paths.put(ckey, dir);
		} catch(IOException e) { throw new RuntimeException(e); }
	}
}
