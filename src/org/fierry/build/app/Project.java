package org.fierry.build.app;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.fierry.build.project.FBuilder;
import org.fierry.build.project.FCompiler;
import org.fierry.build.project.FFiles;
import org.fierry.build.project.FLinker;
import org.fierry.build.project.Lang;
import org.fierry.build.project.Source;
import org.fierry.build.resources.Config;
import org.fierry.build.resources.Css;
import org.fierry.build.resources.Resource;
import org.fierry.build.resources.Roots;
import org.fierry.build.resources.Script;
import org.fierry.build.utils.Directory;
import org.fierry.build.visitors.ExternsVisitor;
import org.fierry.build.yaml.BuildY;
import org.fierry.build.yaml.ProjectY;
import org.fierry.build.yaml.Yaml;


public class Project {

	private Path dir;
	private Lang lang;
	private String name;
	
	private FFiles files;
	private FLinker linker;
	private FBuilder builder;
	private FCompiler compiler;
	
	private Set<Source> sources;
	private Map<Path, Resource> resources;
	
	/*
	 * CREATING / LOADING
	 */
	
	public static Project load(String name, Path dir) {
		Path file = dir.resolve(Build.FILE);
		return Yaml.load(BuildY.class, file).getProject(name);
	}

	public Project(String name, ProjectY raw) {
		this.name = name;
		this.dir  = Directory.getBuild();
		
		this.lang    = raw.getLanguage();
		this.sources = raw.getSources(name);
		
		this.files    = new FFiles(this);
		this.linker   = new FLinker(this, raw);
		this.builder  = new FBuilder(this, raw);
		this.compiler = new FCompiler(this, raw);
		
		this.resources = new HashMap<Path, Resource>();
	}
	
	/*
	 * BUILD / DEPLOY / COMPILE
	 */
	
	public void build() {
		builder.build(lang);
	}
	
	public void deploy() throws IOException {
		System.out.println("Deploying... " + name);
		linker.link();
		linker.deploy(lang);
	}
	
	public void compile(ExternsVisitor visitor) throws IOException {
		compiler.deploy(visitor);
	}
	
	/*
	 * RESOURCES
	 */
	
	public <T extends Resource> Collection<T> getResources(Class<T> cls) {
		Set<T> collection = new HashSet<T>();
		
		for(Resource resource : resources.values()) {
			if(cls.isInstance(resource)) {
				boolean accepted = collection.add(cls.cast(resource));
				assert  accepted : "Duplicated resource found: " + resource.getName();
			}
		}
		return collection;
	}
	
	public <T extends Resource> T getResource(Path path, Class<T> cls) {
		if(!resources.containsKey(path)) {
			resources.put(path, createFile(cls, path));
		}
		return cls.cast(resources.get(path));
	}
	
	public boolean containsResource(String name, Class<? extends Resource> cls) {
		for(Resource resource : getResources(cls)) {
			if(name.equals(resource.getName())) { return true; }
		}
		return false;
	}
	
	private Resource createFile(Class<?> cls, Path path) {
		Path relative = toProjectPath(path);
		
		if(cls == Css.class)    { return new Css(relative); }
		if(cls == Roots.class)  { return new Roots(relative, lang); }
		if(cls == Script.class) { return new Script(relative); }
		if(cls == Config.class) { return new Config(relative); }
		
		throw new IllegalArgumentException("Factory for the given class not found: " + cls);
	}
	
	/*
	 * PATHS
	 */

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
	
	/*
	 * READ / WRITE
	 */

	public String read(Path path) {
		return files.read(path);
	}

	public void write(Path path, String file) {
		files.write(path, file);
	}
	
	/*
	 * GETTERS
	 */
	
	public String getName() {
		return name;
	}
	
	public Lang getLanguage() {
		return lang;
	}
	
	public Set<Source> getSources() {
		return sources;
	}

}