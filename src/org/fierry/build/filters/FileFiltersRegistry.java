package org.fierry.build.filters;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

import org.fierry.build.app.Project;
import org.fierry.build.project.Lang;

public class FileFiltersRegistry {

	private Collection<IFileFilter> filters;
	
	public static FileFiltersRegistry load(Lang lang) {
		FileFiltersRegistry filters = new FileFiltersRegistry();
		
		filters.register(new DirectoryFileFilter());
		filters.register(new ConfigFileFilter());
		filters.register(new RootsFileFilter());
		filters.register(new CssFileFilter());
		
		switch (lang) {
			case JavaScript:   filters.register(new JavaScriptFileFilter());   break;
			case CoffeeScript: filters.register(new CoffeeScriptFileFilter()); break;
		}
		
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
