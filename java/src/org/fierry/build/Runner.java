package org.fierry.build;

import org.fierry.build.filters.DirectoryFilter;
import org.fierry.build.filters.JavascriptFileFilter;
import org.fierry.build.filters.PackageFileFilter;
import org.fierry.build.filters.UnsupportedFileFilter;
import org.fierry.build.projects.CurrentProject;
import org.fierry.build.utils.FiltersRegistry;

public class Runner {

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		CurrentProject current = new CurrentProject();
		Projects projects = new Projects(current);

		current.loadDependences(projects);
		current.build(getProjectFileFilters());
		current.deploy(true);

//		DeployThread thread = new DeployThread(current);
//		thread.start();
//		thread.wait();
		
		synchronized (current) {
			current.wait();
		}
	}
	
	private static FiltersRegistry getProjectFileFilters() {
		FiltersRegistry filters = new FiltersRegistry();
		
		filters.register(new DirectoryFilter());
		filters.register(new PackageFileFilter());
		filters.register(new JavascriptFileFilter());
		filters.register(new UnsupportedFileFilter());
		
		return filters;
	}

}
