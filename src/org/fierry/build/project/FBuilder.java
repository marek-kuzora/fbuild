package org.fierry.build.project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.fierry.build.app.Project;
import org.fierry.build.filters.FileFiltersRegistry;
import org.fierry.build.visitors.SourceVisitor;
import org.fierry.build.yaml.ProjectY;

import com.barbarysoftware.watchservice.WatchKey;
import com.barbarysoftware.watchservice.WatchService;

public class FBuilder {

	private Path main;
	private Project project;
	private Set<Source> sources;
	
	public FBuilder(Project project, ProjectY raw) {
		this.project = project;
		this.main    = raw.getMain();
		this.sources = raw.getSources(project.getName());
	}
	
	public void build(Lang lang) {
		FileFiltersRegistry filters = FileFiltersRegistry.load(lang);
		try {
			for(Source source : sources) {
				assert source.exists() : "Missing source entry: " + source;
				buildSource(source.getAbsolutePath(), filters);
			}
			
			assert Files.exists(main) : "Missing main file: " + main;
			buildSource(main, filters);
		}
		catch(IOException e) { throw new RuntimeException(e); }
	}
	
	private void buildSource(Path path, FileFiltersRegistry filters) throws IOException {
		WatchService watcher = WatchService.newWatchService();
		Map<Path, FileTime> files = new HashMap<Path, FileTime>();
		Map<WatchKey, Path> paths = new HashMap<WatchKey, Path>();
		
		Files.walkFileTree(path, new SourceVisitor(project, watcher, filters, paths, files));
		FThread thread = new FThread(project, watcher, filters, paths, files);
		thread.start();
	}
}
