package org.fierry.build.app;

import java.util.ArrayList;
import java.util.Collection;

public class Args {
	private static final String BUILD_SWITCH = "-b";

	private String command;
	private Collection<String> projects;
	
	public Args(String[] args) {
		this.command = args.length > 0 ? args[0] : BUILD_SWITCH;
		
		this.projects = new ArrayList<String>();
		for(int i = 1; i < args.length; i++) { projects.add(args[i]); }
	}
	
	public Boolean isContinous() {
		return isBuildWatch();
	}
	
	public Boolean isBuild() {
		 return isBuildOnce() || isBuildWatch();
	}
	
	private Boolean isBuildWatch() {
		return command.equals("-b") || command.equals("-build");
	}
	
	private Boolean isBuildOnce() {
		return command.equals("-bo") || command.equals("-build-once");
	}
	
	public Boolean isCompile() {
		return command.equals("-c") || command.equals("-compile");
	}
	
	public Boolean isCreate() {
		return command.equals("-n") || command.equals("-new");
	}
	
	public Collection<String> getProjects() {
		return projects;
	}
}