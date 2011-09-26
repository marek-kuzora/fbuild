package org.fierry.build.project;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;

import org.fierry.build.utils.FileUtils;

import com.google.javascript.jscomp.JSSourceFile;

public class ExternsVisitor implements FileVisitor<Path> {

	private Collection<JSSourceFile> externs;
	
	public ExternsVisitor() {
		this.externs = new ArrayList<JSSourceFile>();
	}
	
	public ExternsVisitor(Collection<JSSourceFile> externs) {
		this.externs = externs;
	}
	
	@Override public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		return FileVisitResult.CONTINUE;
	}

	@Override public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
		if(FileUtils.getExtension(path).endsWith(".js")) {
			externs.add(JSSourceFile.fromFile(path.toFile()));
		}
		return FileVisitResult.CONTINUE;
	}

	@Override public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
		return FileVisitResult.CONTINUE;
	}

	@Override public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
		return FileVisitResult.CONTINUE;
	}
	
	public ExternsVisitor clone() {
		return new ExternsVisitor(externs);
	}
	
	public JSSourceFile[] getExterns() {
		return externs.toArray(new JSSourceFile[0]);
	}
}
