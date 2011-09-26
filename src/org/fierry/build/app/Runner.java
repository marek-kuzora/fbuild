package org.fierry.build.app;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;

import org.fierry.build.filters.CoffeeScriptFileFilter;
import org.fierry.build.filters.CssFileFilter;
import org.fierry.build.filters.DirectoryFileFilter;
import org.fierry.build.filters.FileFiltersRegistry;
import org.fierry.build.filters.IgnoreFileFilter;
import org.fierry.build.filters.JavaScriptFileFilter;
import org.fierry.build.filters.UnsupportedFileFilter;
import org.fierry.build.utils.Args;

public class Runner {

	public static void main(String[] rawArgs) throws IOException, InterruptedException {
		Date start = new Date();
		Args args  = new Args(rawArgs);
		Build file = Build.create(args.getProjects());
		
		if(args.isBuild()) { build(file); }
		if(args.isCreate()) { create(file, args.getProjects()); }
		if(args.isCompile()) { compile(file); }
		
		Date end = new Date();
		System.out.println("Finished operation in: " + (end.getTime() - start.getTime()) + " ms.");
		
		if(args.isContinous()) {
			synchronized (file) {
				file.wait();
			}
		}
	}
	
	private static void build(Build file) throws IOException {
		file.build(getProjectFileFilters());
		file.deploy();
	}
	
	private static void compile(Build file) throws IOException {
		file.compile();
	}
	
	private static void create(Build file, Collection<String> projects) {
		if(projects.size() == 0) {
			throw new IllegalArgumentException("Cannot create project with no given name.");
		}
//		for(String project : projects) {
//			file.createProject(project);
//		}
	}
	
	/**
	 * Argumenty linii poleceń:
	 * 1. fierry -build <projects>, fierry -b <projects> 
	 *    - wykonuje standardowy build projektów i obserwuje dalsze zmiany.
	 *    - brak podanych projektów skutkuje odpaleniem builda dla wszystkich
	 *    
	 * 2. fierry -compile <projects>, fierry -c <projects>
	 *    - wykonuje kompilację JS za pomocą Closure Compiler, oraz minifikację CSS.
	 *    - brak podanych projektów skutkuje odpaleniem kompilacji dla wszystkich
	 *    
	 * 3. fierry -new <project>, fierry -n <project>
	 *    - tworzy konfigurację dla nowego projektu.
	 *    - brak podania nazwy projektu, bądź podanie istniejącej jest traktowane jako błąd. 
	 */
	private static FileFiltersRegistry getProjectFileFilters() {
		FileFiltersRegistry filters = new FileFiltersRegistry();
		
		filters.register(new CssFileFilter());
		filters.register(new DirectoryFileFilter());
		filters.register(new JavaScriptFileFilter());
		filters.register(new CoffeeScriptFileFilter());
		
		filters.register(new IgnoreFileFilter());
		filters.register(new UnsupportedFileFilter());
		
		return filters;
	}

}
