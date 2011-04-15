package org.fierry.build.files;

public class Package implements IFile {

	@Override
	public StringBuilder getOutput() {
		return null;
	}

	public void setConfig() {
		
	}
	
	public void addPackage(Package pkg) {
		
	}
	
	public void deletePackage(String name) {
		
	}
	
	// Package as an IFile too... Do we need an interface? MemoryFile would be enough for all files?!
	// All file logic id done in IFileFilter! :-)
	public void addFile(IFile pkg) {
		
	}
	
	public void deleteFile(String name) {
		
	}
}
