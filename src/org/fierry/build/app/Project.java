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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fierry.build.files.RootFile;
import org.fierry.build.files.StandardFile;
import org.fierry.build.files.ScriptFile;
import org.fierry.build.filters.FileFiltersRegistry;
import org.fierry.build.project.ExternsVisitor;
import org.fierry.build.project.ProjectThread;
import org.fierry.build.project.Source;
import org.fierry.build.project.SourceVisitor;
import org.fierry.build.utils.Directory;
import org.fierry.build.utils.Extension;
import org.fierry.build.utils.Resource;
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
	
	private Map<Path, RootFile> roots;
	private Map<Path, ScriptFile> scripts;
	private Map<Path, StandardFile> styles;
	
	private Map<String, String> aliases;

	private Boolean scriptsDeploy = true;
	private Boolean stylesDeploy  = true;
	
	public Project(String name, ProjectY raw) {
		this.name = name;
		this.dir  = Directory.getBuild();
		
		this.main    = raw.getMain();
		this.libs    = raw.getLibs();
		this.deploy  = raw.getDeploy(this);
		this.externs = raw.getExports();
		this.sources = raw.getSources();
		
		this.roots   = new HashMap<Path, RootFile>();
		this.scripts = new HashMap<Path, ScriptFile>();
		this.styles  = new HashMap<Path, StandardFile>();
		
		this.aliases = new HashMap<String, String>();
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
		String js = createJS();
		String css = createCSS();
		
		for(Path dir : deploy) {
			assert Files.exists(dir) : "Missing deploy entry: " + dir;
			assert Files.isDirectory(dir) : "Deploy entry is not a directory: " + dir;
			
			Path cnt = dir.resolve(name);
			Files.createDirectories(cnt);
			
			if(scriptsDeploy) { writeFile(cnt.resolve("script.js"), js); }
			if(scriptsDeploy) { writeFile(cnt.resolve("style.css"), css); }
			
			if(!Files.exists(dir.resolve(name + ".html"))) {
				writeFile(dir.resolve(name + ".html"), createHTML(false));
			}
		}
		scriptsDeploy = false;
		stylesDeploy  = false;
	}
	
	private String createJS() throws IOException {
		StringBuilder builder = new StringBuilder();
		
		if(scriptsDeploy) {
			Resource.get("require_fn").appendTo(builder);
			
			for(Path library : libs) {
				assert Files.exists(library) : "Missing library file: " + library;
				builder.append(new String(Files.readAllBytes(library)));
				builder.append("\r\n");
			}
			for(ScriptFile file : scripts.values()) {
				file.deploy(builder);
			}
			
			for(RootFile file : roots.values()) {
				file.deploy(builder);
			}
			
			builder
				.append("require('/")
				.append(getScriptFile(main).getName())
				.append("');");
		}
		return builder.toString();
	}
	
	private String createCSS() {
		StringBuilder builder = new StringBuilder();

		if(stylesDeploy) {
			for(StandardFile file : styles.values()) {
				file.deploy(builder);
			}
		}
		return builder.toString();
	}

	private String createHTML(Boolean minify) {
		return Resource.get("web_page")
			.replace("name", name)
			.replace("min", minify ? "-min" : "")
			.toString();
	}
	
	public void compile(ExternsVisitor visitor) throws IOException {
		Path defaultDir = getDefaultDeployDir().resolve(name);
		assert Files.exists(defaultDir) : "Project must be built before compilation: " + name;
		
		String js  = createMinifiedJS(defaultDir, visitor);
		String css = createMinifiedCSS(defaultDir);

		for(Path dir : deploy) {
			Path cnt = dir.resolve(name);
			assert Files.exists(cnt) : "Project is not built: " + name;

			writeFile(cnt.resolve("script-min.js"), js);
			writeFile(cnt.resolve("style-min.css"), css);
			
			if(!Files.exists(dir.resolve(name + "-min.html"))) {
				writeFile(dir.resolve(name + "-min.html"), createHTML(true));
			}
		}
	}
	
	private String createMinifiedJS(Path dir, ExternsVisitor visitor) {
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
	    return compiler.toSource();
	}
	
	private String createMinifiedCSS(Path dir) {
		try {
			Reader in = Files.newBufferedReader(dir.resolve("style.css"), Charset.defaultCharset());
			StringWriter out = new StringWriter();
			CssCompressor compressor = new CssCompressor(in);
			
			compressor.compress(out, 0);
			in.close();
			
			return out.toString();
		}
		catch(IOException e) { throw new RuntimeException(e); } 
	}
	
	public RootFile getRootFile(Path path) {
		if(!roots.containsKey(path)) {
			roots.put(path, new RootFile(this, toProjectPath(path)));
		}
		scriptsDeploy = true;
		return roots.get(path);
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
	
	/**
	 * Returns specified file as a String. 
	 * Handles file's tagging - scans the content for @{set:<alias>} and marks found files.
	 * 
	 * @param path
	 * @throws IOException
	 */
	public String readFile(Path path) {
		try {
			String file = new String(Files.readAllBytes(path));
			
			Pattern p = Pattern.compile("@\\{set:(\\w+)\\}");
			Matcher m = p.matcher(file);
			
			while(m.find()) { 
				String name = "/" + Extension.trim(toProjectPath(path));
				aliases.put(m.group(1), name);
			}
			return file;
		} catch(IOException e) { throw new RuntimeException(e); }
	}
	
	/**
	 * Saves the specified file from a String.
	 * Handles file's tagging - replaces all found @{get:<alias>} with proper files paths.
	 * 
	 * @param path
	 * @param file
	 * @throws IOException
	 */
	public void writeFile(Path path, String file) {
		Pattern p = Pattern.compile("@\\{get:(\\w+)\\}");
		Matcher m = p.matcher(file);
		
		while(m.find()) {
			String alias = m.group(1);
			
			assert aliases.containsKey(alias) : "File alias not found: " + alias;
			file = m.replaceFirst(aliases.get(alias));
			m.reset(file);
		}
		try { Files.write(path, file.getBytes()); }
		catch(IOException e) { throw new RuntimeException(e); }
	}
	
	/**
	 * Returns project name.
	 */
	public String getName() {
		return name;
	}
}