package org.fierry.build.project;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;

import org.fierry.build.app.Project;
import org.fierry.build.utils.Template;
import org.fierry.build.visitors.ExternsVisitor;
import org.fierry.build.yaml.ProjectY;

import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.JSSourceFile;
import com.yahoo.platform.yui.compressor.CssCompressor;

public class FCompiler {

	private String name;
	private Project project;
	private Collection<Path> externs;
	private Collection<Path> deploys;
	
	public FCompiler(Project project, ProjectY raw) {
		this.project = project;
		this.name    = project.getName();
		
		this.externs = raw.getExterns();
		this.deploys = raw.getDeploy(project);
	}
	
	public void deploy(ExternsVisitor visitor) {
		Path dir = project.getDefaultDeployDir().resolve(project.getName());
		assert Files.exists(dir) : "Project must be built before compilation: " + name;
		
		String js  = minifyJS(dir, visitor);
		String css = minifyCSS(dir);

		for(Path deploy : deploys) {
			Path cnt = deploy.resolve(name);
			assert Files.exists(cnt) : "Project is not built: " + name;

			project.write(cnt.resolve("script-min.js"), js);
			project.write(cnt.resolve("style-min.css"), css);
			
			if(!Files.exists(deploy.resolve(name + "-min.html"))) {
				project.write(deploy.resolve(name + "-min.html"), createHTML());
			}
		}
	}
	
	private String minifyJS(Path dir, ExternsVisitor visitor) {
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
	
	private String minifyCSS(Path dir) {
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
	
	private String createHTML() {
		return Template.get("commands/new_web")
		.replace("name", name)
		.replace("min", "-min")
		.toString();
	}
}
