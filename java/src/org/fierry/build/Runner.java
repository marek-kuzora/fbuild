package org.fierry.build;

import org.fierry.build.filters.DirectoryFilter;
import org.fierry.build.filters.IgnoreSwpFileFilter;
import org.fierry.build.filters.JavascriptFileFilter;
import org.fierry.build.filters.PackageFileFilter;
import org.fierry.build.filters.UnsupportedFileFilter;
import org.fierry.build.projects.CurrentProject;
import org.fierry.build.utils.FiltersRegistry;

public class Runner {

	private static CurrentProject current;
	
	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		current = new CurrentProject();
		Projects projects = new Projects(current);

		current.loadDependences(projects);
		current.build(getProjectFileFilters());
		current.deploy(true);

		synchronized (current) {
			current.wait();
		}
	}
	
	private static FiltersRegistry getProjectFileFilters() {
		FiltersRegistry filters = new FiltersRegistry();
		
		filters.register(new DirectoryFilter());
		filters.register(new PackageFileFilter());
		filters.register(new JavascriptFileFilter());
		filters.register(new IgnoreSwpFileFilter());
		filters.register(new UnsupportedFileFilter());
		
		return filters;
	}
	
	public static void triggerDeploy() {
		current.deploy(false);
	}

}
