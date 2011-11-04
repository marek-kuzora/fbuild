package org.fierry.build.filters;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

import org.fierry.build.app.Project;

public class FileFiltersRegistry {

	private Collection<IFileFilter> filters;
	
	/*
	 * Zależnie od typu projektu różne filtry będą ładowane (!)
	 */
	public static FileFiltersRegistry load() {
		FileFiltersRegistry filters = new FileFiltersRegistry();
		
		filters.register(new CssFileFilter());
		filters.register(new DirectoryFileFilter());
		filters.register(new CoffeeScriptFileFilter());
		filters.register(new ConfigFileFilter());
		filters.register(new RootsFileFilter());
		
		filters.register(new IgnoreFileFilter());
		filters.register(new UnsupportedFileFilter());
		
		return filters;
	}
	
	private FileFiltersRegistry() {
		this.filters = new ArrayList<IFileFilter>();
	}
	
	public void register(IFileFilter filter) {
		filters.add(filter);
	}
	
	public IFileFilter get(Path path, Project project) {
		for(IFileFilter filter : filters) {
			if(filter.accept(path, project)) { return filter; }
		}
		throw new IllegalStateException("Filter not found for: " + path);
	}
}
