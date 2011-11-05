package org.fierry.build.project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

import org.fierry.build.app.Project;
import org.fierry.build.linking.GlobalConfig;
import org.fierry.build.resources.Config;
import org.fierry.build.resources.Css;
import org.fierry.build.resources.Roots;
import org.fierry.build.resources.Script;
import org.fierry.build.utils.Template;
import org.fierry.build.yaml.ProjectY;

public class FLinker {

	private String name;
	private Project project;
	
	private Path main;
	private Collection<Path> libs;
	private Collection<Path> deploys;
	
	private GlobalConfig configuration;
	
	public FLinker(Project project, ProjectY raw) {
		this.project = project;
		this.name    = project.getName();
		
		this.main    = raw.getMain();
		this.libs    = raw.getLibs();
		this.deploys = raw.getDeploy(project);
	}
	
	public void link() {
		this.configuration = new GlobalConfig(project.getResources(Config.class));
	}
	
	public void deploy() throws IOException {
		String js  = createJS();
		String css = createCSS();
		
		for(Path dir : deploys) {
			assert Files.exists(dir) : "Missing deploy entry: " + dir;
			assert Files.isDirectory(dir) : "Deploy entry is not a directory: " + dir;
			
			Path cnt = dir.resolve(name);
			Files.createDirectories(cnt);
			
			project.write(cnt.resolve("script.js"), js);
			project.write(cnt.resolve("style.css"), css);
			
			if(!Files.exists(dir.resolve(name + ".html"))) {
				project.write(dir.resolve(name + ".html"), createHTML());
			}
		}
	}
	
	/*
	 * Idea jest, aby dopiero na sam koniec wykonywać translację z CS do JS i tylko wtedy, jeśli jest tak ustalone w projekcie.
	 * Problem gdy dołączę DAO - wtedy potrzebujemy wydrukować require na początku pliku, 
	 * 	a podczas wspólnej kompilacji nie mam jednego pliku a wszystkie razem :/
	 */
	private String createJS() throws IOException {
		StringBuilder builder = new StringBuilder();
		
		Template.get("modules/require").appendTo(builder);
		
		for(Path library : libs) {
			assert Files.exists(library) : "Missing library file: " + library;
			builder.append(new String(Files.readAllBytes(library)));
			builder.append("\r\n");
		}
		
		for(Script file : project.getResources(Script.class)) {
			file.deploy(builder);
		}
		
		for(Roots file : project.getResources(Roots.class)) {
			file.deploy(builder, configuration);
		}
		
		builder
			.append("require('/")
			.append(project.getResource(main, Script.class).getName())
			.append("');");
		
		return builder.toString();
	}
	
	private String createCSS() {
		StringBuilder builder = new StringBuilder();

		for(Css file : project.getResources(Css.class)) {
			file.deploy(builder);
		}
		
		return builder.toString();
	}
	
	private String createHTML() {
		return Template.get("commands/new_web")
			.replace("name", name)
			.replace("min", "")
			.toString();
	}
}
