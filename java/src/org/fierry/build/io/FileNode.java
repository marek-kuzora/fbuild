package org.fierry.build.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import org.apache.commons.io.IOUtils;

public class FileNode {

	private File file;
	
	public FileNode(File file) {
		this.file = file.getAbsoluteFile();
	}
	
	public FileNode(URI uri) {
		this.file = new File(uri).getAbsoluteFile();
	}
	
	public FileNode(String path) {
		this.file = new File(path).getAbsoluteFile();
	}
	
	public FileNode parent() {
		return new FileNode(file.getParentFile());
	}
	
	public FileNode get(String path) {
		File tmp = new File(path);
		
		return tmp.isAbsolute() ? 
				new FileNode(tmp) 
			:	new FileNode(file.getAbsolutePath() + "/" + path);
	}
	
	public FileNode[] children() {
		if(!file.isDirectory()) { return new FileNode[]{}; }
		
		File[] files = file.listFiles();
		FileNode[] nodes = new FileNode[files.length];
		
		for(int i = 0; i < files.length; i++) { nodes[i] = new FileNode(files[i]); }
		return nodes;
	}
	
	public FileNode[] children(FileFilter filter) {
		if(!file.isDirectory()) { return new FileNode[]{}; }
		
		File[] files = file.listFiles(filter);
		FileNode[] nodes = new FileNode[files.length];
		
		for(int i = 0; i < files.length; i++) { nodes[i] = new FileNode(files[i]); }
		return nodes;
	}
	
	public Boolean exists() {
		return file.exists();
	}
	
	public Boolean isDirectory() {
		return file.isDirectory();
	}
	
	public Long modifyDate() {
		return file.lastModified();
	}
	
	public String path() {
		String path = file.getAbsolutePath();
		return path.replaceAll("\\\\", "/").replaceAll("[^/]+/\\.\\./", "");
////		path.replaceFirst(arg0, arg1)
//		return file.getAbsolutePath();
	}
	
	public String name() {
		return file.getName();
	}
	
	public File file() {
		return file;
	}
	
	public String read() {
		try {
			String result;
			InputStream inA = new FileInputStream(file);
			InputStream inB = new BufferedInputStream(inA);
			
			try { result = IOUtils.toString(inB); }
			finally { inB.close(); }
			return result;
		}
		catch(IOException e) { throw new RuntimeException(e); }
	}
	
	public void write(String data) {
		try {
			OutputStream outA = new FileOutputStream(file);
			OutputStream outB = new BufferedOutputStream(outA);

			try { IOUtils.write(data, outB); }
			finally { outB.close(); }
		}
		catch(IOException e) { throw new RuntimeException(e); }
	}
	
	public InputStream inputStream() throws FileNotFoundException {
		return new BufferedInputStream(new FileInputStream(file));
	}
	
	public OutputStream outputStream() throws FileNotFoundException {
		return new BufferedOutputStream(new FileOutputStream(file));
	}
	
	@Override public int hashCode() {
		return path().hashCode();
	}
	
	@Override public boolean equals(Object obj) {
		if(!(obj instanceof FileNode)) { return false; }
		
		FileNode node = (FileNode) obj;
		return node != null && path().equals(node.path());
	}
	@Override public String toString() {
		return path();
	}
	
}
