package org.fierry.build;

import java.nio.file.Path;

import org.fierry.build.filters.IFileFilter;
import org.fierry.build.projects.CurrentProject;
import org.fierry.build.projects.IProject;
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
//		current.deploy(Console.WARNINGS);

//		DeployThread thread = new DeployThread(current);		// deploing the project with Console.ERRORS logging only!
//		thread.start();
//		thread.wait();
//		synchronized (current) {
//			current.wait();
//		}
		System.exit(0);
	}
	
	private static FiltersRegistry getProjectFileFilters() {
		FiltersRegistry filters = new FiltersRegistry();
		filters.register(new IFileFilter() {
			
			@Override
			public void fileUpdated(Path path, IProject project) {
				System.out.println("UPDATED: " + project.getName() + ", " + path);
			}
			
			@Override
			public void fileDeleted(Path path, IProject project) {
				System.out.println("DELETED: " + project.getName() + ", " + path);
			}
			
			@Override
			public void fileCreated(Path path, IProject project) {
				System.out.println("CREATED: " + project.getName() + ", "+ path + ", " + path.getNameCount());
			}
			
			@Override
			public Boolean accept(Path path, IProject project) {
				return true;
			}
		});
		
		return filters;
	}

}
