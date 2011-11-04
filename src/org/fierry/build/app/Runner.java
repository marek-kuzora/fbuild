package org.fierry.build.app;

import java.io.IOException;
import java.util.Date;

import org.fierry.build.filters.FileFiltersRegistry;
import org.fierry.build.utils.Commands;

public class Runner {

	public static void main(String[] rawArgs) throws IOException, InterruptedException {
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
		Build file = Build.load();
		
		file.filter(args.getProjects());
		file.build(FileFiltersRegistry.load());
		file.deploy();
	}
	
	private static void compile(Args args) throws IOException {
		Build file = Build.load();
		
		file.filter(args.getProjects());
		file.compile();
	}
	
	private static void create(Args args) throws IOException {
		assert args.getProjects().size() > 0 : "Cannot create project without a name.";

		for(String project : args.getProjects()) {
			Commands.newProject(project);
		}
	}

}
