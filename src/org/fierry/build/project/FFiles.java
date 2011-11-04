package org.fierry.build.project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fierry.build.app.Project;
import org.fierry.build.utils.Extension;

public class FFiles {

	private Pattern set;
	private Pattern get;
	private Project project;
	private Map<String, String> aliases;

	
	public FFiles(Project project) {
		this.project = project;
		this.aliases = new HashMap<String, String>();
		
		this.get = Pattern.compile("@\\{get:(\\w+)\\}");
		this.set = Pattern.compile("@\\{set:(\\w+)\\}");
	}
	
	public String read(Path path) {
		try {
			String file = new String(Files.readAllBytes(path));
			Matcher m   = set.matcher(file);
			
			while(m.find()) {
				String name = "/" + Extension.trim(project.toProjectPath(path));
				aliases.put(m.group(1), name);
			}
			return file;
		} catch(IOException e) { throw new RuntimeException(e); }
	}
	
	public void write(Path path, String file) {
		Matcher m = get.matcher(file);
		
		while(m.find()) {
			String alias = m.group(1);
			
			assert aliases.containsKey(alias) : "File alias not found: " + alias;
			file = m.replaceFirst(aliases.get(alias));
			m.reset(file);
		}
		try { Files.write(path, file.getBytes()); }
		catch(IOException e) { throw new RuntimeException(e); }
	}
}
