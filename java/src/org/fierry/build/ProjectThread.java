package org.fierry.build;

import static com.barbarysoftware.watchservice.StandardWatchEventKind.ENTRY_CREATE;
import static com.barbarysoftware.watchservice.StandardWatchEventKind.ENTRY_DELETE;
import static com.barbarysoftware.watchservice.StandardWatchEventKind.ENTRY_MODIFY;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.fierry.build.filters.IFileFilter;
import org.fierry.build.projects.IProject;
import org.fierry.build.utils.FiltersRegistry;

import com.barbarysoftware.watchservice.WatchEvent;
import com.barbarysoftware.watchservice.WatchEvent.Kind;
import com.barbarysoftware.watchservice.WatchKey;
import com.barbarysoftware.watchservice.WatchService;
import com.barbarysoftware.watchservice.WatchableFile;

public class ProjectThread extends Thread {
	private IProject project;
	private FiltersRegistry filters;
	private WatchService watcher;
	private Map<WatchKey, Path> paths;
	private Map<Path, FileTime> files;
	
	public ProjectThread(IProject project, WatchService watcher, FiltersRegistry filters, Map<WatchKey, Path> paths, Map<Path, FileTime> files) {
		this.project = project;
		this.watcher = watcher;

		this.paths = paths;
		this.files = files;
		this.filters = filters;
	}
	
	@Override public void run() {
		while(true) {
			try {
				Path dir = project.getDirectory();
				WatchKey key = watcher.take();
				if(key == null) { continue; }

				for (WatchEvent<?> event: key.pollEvents()) {
					Boolean deploy = false;
					Path abs = absolutePath(key, event);
					Path file = dir.relativize(abs);
					
					if(project.isReleaseDirectory(abs) || project.isHiddenDirectory(dir)) { 
						continue;
					}
					
					IFileFilter filter = filters.get(file, project);
					Kind<WatchableFile> kind = getEventKind(abs, files);

					if(kind == null) { continue; }
					if(kind == ENTRY_CREATE) {
						deploy = filter.fileCreated(file, project);
						if(Files.isDirectory(abs)) { registerDirectoryWatcher(abs); }
					}
					
					if(kind == ENTRY_DELETE) { deploy = filter.fileDeleted(file, project); }
					if(kind == ENTRY_MODIFY) { deploy = filter.fileUpdated(file, project); }
					
					if(deploy) {
						Runner.triggerDeploy(file);
					}
					System.out.println("kind: " + kind + ", file: " + ((File)event.context()).getName());
				}
				key.reset();
			}
			catch(IOException e) { throw new RuntimeException(e); }
			catch(InterruptedException e) { return; }
		}
	}
	
	private Kind<WatchableFile> getEventKind(Path abs, Map<Path, FileTime> files) throws IOException, InterruptedException {
		TimeUnit.MILLISECONDS.sleep(15);
		
		if(Files.exists(abs)) {
			FileTime time = Files.getLastModifiedTime(abs);
			if(!files.containsKey(abs)) {
				files.put(abs, time);
				return ENTRY_CREATE;
			}
			if(time.compareTo(files.get(abs)) > 0) {
				files.put(abs, time);
				return ENTRY_MODIFY;
			}
			return null;
		}
		System.out.println(abs + " --- " + Files.exists(abs));
		return ENTRY_DELETE;
	}
	
	/**
	 * Returns absolute file path for the given key & event.
	 * @param key
	 * @param event
	 */
	private Path absolutePath(WatchKey key, WatchEvent<?> event) {
		return paths.get(key).resolve(((File)event.context()).toPath());
	}
	
	/**
	 * Register newly created directory into WatchService.
	 * @param dir
	 */
	private void registerDirectoryWatcher(Path dir) {
		try {
			WatchKey ckey = new WatchableFile(dir.toFile()).register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
//			WatchKey ckey = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
			paths.put(ckey, dir);
		} catch(IOException e) { throw new RuntimeException(e); }
	}
}
