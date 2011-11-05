package org.fierry.build.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fierry.build.project.Lang;

public class Template {
	private static final String EXT = ".template";
	private static final String DIR = "/org/fierry/build/templates" + File.separator;

	private static Map<String, String> cache = new HashMap<String, String>();
	private String data;
	
	public static Template get(String resource) {
		return new Template(readResource(resource));
	}
	
	public static Template get(String resource, Lang lang) {
		return new Template(readResource(resource + lang.extension));
	}
	
	private static String readResource(String resource) {
		if(!cache.containsKey(resource)) {
			InputStream in = Template.class.getResourceAsStream(DIR + resource + EXT);
			Scanner scanner = new Scanner(in).useDelimiter("\\A");
			
			cache.put(resource, scanner.next());
			scanner.close();
		}
		return cache.get(resource);
	}
	
	private Template(String data) {
		this.data = data;
	}
	
	public Template replace(String name, String value) {
		data = data.replace("${" + name + "}", value);
		return this;
	}
	
	public Template replaceLine(String name, String value) {
		Pattern p = Pattern.compile("(?<=(^|\n)) *\\$\\{" + name + "\\} *(?=(\n|$))");
		Matcher m = p.matcher(data);
		
		while(m.find()) {
			int spaces = m.group().indexOf('$');
			data = data.replace(m.group(), Lines.indent(value, spaces));
			m.reset(data);
		}
		assert data.indexOf("${" + name + "}") == -1 : "Illegal sth";
		return this;
	}
	
	public void appendTo(StringBuilder builder) {
		builder.append(data);
	}
	
	public void appendTo(Path path) throws IOException {
		Files.write(path, data.getBytes(), StandardOpenOption.APPEND);
	}
	
	public String toString() {
		return data;
	}
	
}
