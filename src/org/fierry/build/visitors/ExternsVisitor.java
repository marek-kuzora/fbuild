package org.fierry.build.visitors;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;

import org.fierry.build.utils.Directory;
import org.fierry.build.utils.Extension;

import com.google.javascript.jscomp.JSSourceFile;

public class ExternsVisitor implements FileVisitor<Path> {

	private Collection<JSSourceFile> externs;
	
	public static ExternsVisitor load() throws IOException {
		ExternsVisitor visitor = new ExternsVisitor();
		Files.walkFileTree(Directory.getJar().resolve("externs"), visitor);
		
		return visitor;
	}
	
	private ExternsVisitor() {
		this.externs = new ArrayList<JSSourceFile>();
	}
	
	public ExternsVisitor(Collection<JSSourceFile> externs) {
		this.externs = externs;
	}
	
	@Override public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
		return FileVisitResult.CONTINUE;
	}

	@Override public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
		if(Extension.get(path).endsWith(".js")) {
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
