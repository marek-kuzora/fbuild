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
import org.fierry.build.project.Args;

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
		assert projects.size() > 0 : "Cannot create project with no given name.";

//		for(String project : projects) {
//			file.createProject(project);
//		}
	}
	
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
