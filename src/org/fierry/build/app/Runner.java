package org.fierry.build.app;

import java.io.IOException;
import java.util.Date;

import org.fierry.build.filters.CoffeeScriptFileFilter;
import org.fierry.build.filters.ConfigurationFileFilter;
import org.fierry.build.filters.CssFileFilter;
import org.fierry.build.filters.DirectoryFileFilter;
import org.fierry.build.filters.FileFiltersRegistry;
import org.fierry.build.filters.IgnoreFileFilter;
import org.fierry.build.filters.JavaScriptFileFilter;
import org.fierry.build.filters.UnsupportedFileFilter;
import org.fierry.build.project.Args;
import org.fierry.build.utils.Uid;

public class Runner {

	public static void main(String[] rawArgs) throws IOException, InterruptedException {
//		for(int i = 0; i < 100; i++ ) {
//			System.out.println(i + " " + Uid.generate(i));
//		}
		Date start = new Date();
		Args args  = new Args(rawArgs);
		
		if(args.isBuild()) { build(args); }
		if(args.isCreate()) { create(args); }
		if(args.isCompile()) { compile(args); }
		
		Date end = new Date();
		System.out.println("Finished operation in: " + (end.getTime() - start.getTime()) + " ms.");
		
		if(args.isContinous()) {
			synchronized (args) {
				args.wait();
			}
		}
		System.exit(0);
	}
	
	private static void build(Args args) throws IOException {
		Build file = Build.create(args.getProjects(), false);
		file.build(getProjectFileFilters());
		file.deploy();
	}
	
	private static void compile(Args args) throws IOException {
		Build file = Build.create(args.getProjects(), false);
		file.compile();
	}
	
	private static void create(Args args) throws IOException {
		Build file = Build.create();
		assert args.getProjects().size() > 0 : "Cannot create project without a name.";

		for(String project : args.getProjects()) {
			file.createProject(project);
		}
	}
	
	private static FileFiltersRegistry getProjectFileFilters() {
		FileFiltersRegistry filters = new FileFiltersRegistry();
		
		filters.register(new CssFileFilter());
		filters.register(new DirectoryFileFilter());
		filters.register(new JavaScriptFileFilter());
		filters.register(new CoffeeScriptFileFilter());
		filters.register(new ConfigurationFileFilter());
		
		filters.register(new IgnoreFileFilter());
		filters.register(new UnsupportedFileFilter());
		
		return filters;
	}

}
