package org.fierry.build.utils;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

import org.fierry.build.filters.IFileFilter;
import org.fierry.build.projects.IProject;

public class FiltersRegistry {

	private Collection<IFileFilter> filters;
	
	public FiltersRegistry() {
		this.filters = new ArrayList<IFileFilter>();
	}
	
	public void register(IFileFilter filter) {
		filters.add(filter);
	}
	
	public IFileFilter get(Path path, IProject project) {
		for(IFileFilter filter : filters) {
			if(filter.accept(path, project)) { return filter; }
		}
		throw new IllegalStateException("Filter not found for: " + path);
	}
}
