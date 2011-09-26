package org.fierry.build.filters;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

import org.fierry.build.app.Project;

public class FileFiltersRegistry {

	private Collection<IFileFilter> filters;
	
	public FileFiltersRegistry() {
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
