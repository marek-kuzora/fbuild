package org.fierry.build.io;

import java.io.File;
import java.io.FileFilter;

import org.apache.commons.io.filefilter.DirectoryFileFilter;

public class Filters {

	static private String getExtension(File file) {
		String name = file.getName();
		return name.substring(name.lastIndexOf(".") + 1);
	}
	
	static public FileFilter getDirectories() {
		return DirectoryFileFilter.INSTANCE;
	}
	
	static public FileFilter getJavascriptFiles() {
		return new FileFilter() {
			@Override public boolean accept(File file) {
				return !file.isDirectory() && getExtension(file).equals("js");
			}
		};
	}
}
