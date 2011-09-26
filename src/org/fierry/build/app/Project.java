package org.fierry.build.app;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.fierry.build.files.StandardFile;
import org.fierry.build.files.ScriptFile;
import org.fierry.build.filters.FileFiltersRegistry;
import org.fierry.build.project.ExternsVisitor;
import org.fierry.build.project.ProjectThread;
import org.fierry.build.project.Source;
import org.fierry.build.project.SourceVisitor;
import org.fierry.build.utils.Resources;
import org.fierry.build.yaml.ProjectY;

import com.barbarysoftware.watchservice.WatchKey;
import com.barbarysoftware.watchservice.WatchService;
import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.JSSourceFile;
import com.yahoo.platform.yui.compressor.CssCompressor;

public class Project {
	private Path dir;
	private String name;
	
	private Path main;
	private Collection<Path> libs;
	private Collection<Path> deploy;
	private Collection<Path> externs;
	private Collection<Source> sources;
	
	private Map<Path, ScriptFile> scripts;
	private Map<Path, StandardFile> styles;
	
	private Boolean scriptsDeploy = true;
	private Boolean stylesDeploy  = true;
	
	public Project(String name, ProjectY raw) {
		this.name = name;
		this.dir  = Resources.getBuildDirectory();
		
		this.main    = raw.getMain();
		this.libs    = raw.getLibs();
		this.deploy  = raw.getDeploy(this);
		this.externs = raw.getExports();
		this.sources = raw.getSources();
		
		this.scripts = new HashMap<Path, ScriptFile>();
		this.styles  = new HashMap<Path, StandardFile>();
	}
	
	public Project(String name) {
		this.name = name;
		this.dir  = Resources.getBuildDirectory();
	}
	
	public void build(FileFiltersRegistry filters) {
		try {
			for(Source source : sources) {
				assert source.exists() : "Missing source entry: " + source;
				buildSource(source.getAbsolutePath(), filters);
			}
			
			assert Files.exists(main) : "Missing main file: " + main;
			buildSource(main, filters);
		}
		catch(IOException e) { throw new RuntimeException(e); }
	}
	
	private void buildSource(Path path, FileFiltersRegistry filters) throws IOException {
		WatchService watcher = WatchService.newWatchService();
		Map<Path, FileTime> files = new HashMap<Path, FileTime>();
		Map<WatchKey, Path> paths = new HashMap<WatchKey, Path>();
		
		Files.walkFileTree(path, new SourceVisitor(this, watcher, filters, paths, files));
		ProjectThread thread = new ProjectThread(this, watcher, filters, paths, files);
		thread.start();
	}
	
	public void deploy() throws IOException {
		byte[] js = createJS();
		byte[] css = createCSS();
		
		for(Path dir : deploy) {
			assert Files.exists(dir) : "Missing deploy entry: " + dir;
			assert Files.isDirectory(dir) : "Deploy entry is not a directory: " + dir;
			
			Path cnt = dir.resolve(name);
			Files.createDirectories(cnt);
			
			if(scriptsDeploy) { Files.write(cnt.resolve("script.js"), js); }
			if(stylesDeploy)  { Files.write(cnt.resolve("style.css"), css); }
			
			if(!Files.exists(dir.resolve(name + ".html"))) {
				Files.write(dir.resolve(name + ".html"), createHTML());
			}
		}
		scriptsDeploy = false;
		stylesDeploy  = false;
	}
	
	private byte[] createJS() throws IOException {
		StringBuilder builder = new StringBuilder();
		
		if(scriptsDeploy) {
			builder.append(Resources.getTemplate("require_fn"));
			
			for(Path library : libs) {
				assert Files.exists(library) : "Missing library file: " + library;
				builder.append(new String(Files.readAllBytes(library)));
				builder.append("\r\n");
			}
			for(ScriptFile file : scripts.values()) {
				file.deploy(builder);
			}
			
			String mainName = getScriptFile(main).getName();
			builder.append("run('/").append(mainName).append("');");
		}
		return builder.toString().getBytes();
	}
	
	private byte[] createCSS() {
		StringBuilder builder = new StringBuilder();

		if(stylesDeploy) {
			for(StandardFile file : styles.values()) {
				file.deploy(builder);
			}
		}
		return builder.toString().getBytes();
	}
	
	private byte[] createHTML() {
		String cnt = Resources.getTemplate("web_page");
		return cnt.replaceAll("\\$\\{name\\}", name).getBytes();
	}
	
	public void compile(ExternsVisitor visitor) throws IOException {
		Path defaultDir = getDefaultDeployDir().resolve(name);
		assert Files.exists(defaultDir) : "Project must be built before compilation: " + name;
		
		byte[] js  = createMinifiedJS(defaultDir, visitor);
		byte[] css = createMinifiedCSS(defaultDir);

		for(Path dir : deploy) {
			Path cnt = dir.resolve(name);
			assert Files.exists(cnt) : "Project is not built: " + name;

			Files.write(cnt.resolve("script-min.js"), js);
			Files.write(cnt.resolve("style-min.css"), css);
			
			if(!Files.exists(dir.resolve(name + "-min.html"))) {
				Files.write(dir.resolve(name + "-min.html"), createMinifiedHTML());
			}
		}
	}
	
	private byte[] createMinifiedJS(Path dir, ExternsVisitor visitor) {
		CompilerOptions options = new CompilerOptions();
	    CompilationLevel.ADVANCED_OPTIMIZATIONS.setOptionsForCompilationLevel(options);
	    
	    File file = dir.resolve("script.js").toFile();
	    JSSourceFile[] input = new JSSourceFile[] { JSSourceFile.fromFile(file) };
	    
	    for(Path path : externs) {
			try { Files.walkFileTree(path, visitor); }
			catch(IOException e) { throw new RuntimeException(e); }
		}
	    
	    Compiler compiler = new Compiler();
	    compiler.compile(visitor.getExterns(), input, options);
	    return compiler.toSource().getBytes();
	}
	
	private byte[] createMinifiedCSS(Path dir) {
		try {
			Reader in = Files.newBufferedReader(dir.resolve("style.css"), Charset.defaultCharset());
			StringWriter out = new StringWriter();
			CssCompressor compressor = new CssCompressor(in);
			
			compressor.compress(out, 0);
			in.close();
			
			return out.toString().getBytes();
		}
		catch(IOException e) { throw new RuntimeException(e); } 
	}
	
	private byte[] createMinifiedHTML() {
		String cnt = Resources.getTemplate("web_page_min");
		return cnt.replaceAll("\\$\\{name\\}", name).getBytes();
	}
	
	public ScriptFile getScriptFile(Path path) {
		if(!scripts.containsKey(path)) {
			scripts.put(path, new ScriptFile(toProjectPath(path)));
		}
		scriptsDeploy = true;
		return scripts.get(path);
	}
	
	public StandardFile getCssFile(Path path) {
		if(!styles.containsKey(path)) {
			styles.put(path, new StandardFile());
		}
		stylesDeploy = true;
		return styles.get(path);
	}
	
	public Path getDefaultDeployDir() {
		return dir.resolve("release");
	}

	public Path toAbsolutePath(Path path) {
		assert !path.isAbsolute(): "Path is already an absolute path: " + path;
		
		for(Source source : sources) {
			if(source.accept(path)) {
				return source.toAbsolutePath(path);
			}
		}
		throw new IllegalArgumentException("Absolute path translation for: " + path + " not found.");
	}
	
	public Path toProjectPath(Path path) {
		assert path.isAbsolute(): "Path is already a project path: " + path;

		for(Source source : sources) {
			if(source.accept(path)) {
				return source.toProjectPath(path);
			}
		}
		throw new IllegalArgumentException("Project path translation for: " + path + " not found.");
	}
	
	public String getName() {
		return name;
	}
}