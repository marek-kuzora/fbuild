package org.fierry.build;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.fierry.build.filters.DirectoryFilter;
import org.fierry.build.filters.IgnoreSwpFileFilter;
import org.fierry.build.filters.JavascriptFileFilter;
import org.fierry.build.filters.PackageFileFilter;
import org.fierry.build.filters.UnsupportedFileFilter;
import org.fierry.build.projects.CurrentProject;
import org.fierry.build.utils.FiltersRegistry;
import org.fierry.build.utils.Growl;

public class Runner {

	private static CurrentProject current;
	
	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		current = new CurrentProject();
		
		Growl.registerApplication();
		Projects projects = new Projects(current);

		current.loadDependences(projects);
		current.build(getProjectFileFilters());
		
		triggerDeploy(Paths.get(""));
        
		synchronized (current) {
			current.wait();
		}
        System.exit(0);
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
	
	public static synchronized void triggerDeploy(Path path) {
		current.deploy();
		Growl.notifyBuildFinished(current.getName(), path);
	}

}
