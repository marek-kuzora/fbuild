package org.fierry.build.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Growl {

	public static void registerApplication() {
		executeScript(getScript("Register.scpt"));
	}
	
	public static void notifyBuildFinished(String project, Path file) {
		String cnt = getScript("Notify.scpt");

		cnt = cnt.replaceAll("\\{\\$project\\}", project);
		cnt = cnt.replaceAll("\\{\\$file\\}", file.toString());
		
		executeScript(cnt);
	}
	
	private static String getScript(String name) {
		try {
			Path path = Paths.get(Growl.class.getResource(name).toURI());
			return new String(Files.readAllBytes(path));
		}
		catch(IOException e) { throw new RuntimeException(e); }
		catch (URISyntaxException e) { throw new RuntimeException(e); }
	}
	
	private static void executeScript(String cnt) {
		Runtime rtm = Runtime.getRuntime();
		String[] args = { "osascript" };
		
		try {
			Process pr = rtm.exec(args);
			OutputStream out = pr.getOutputStream();
			
			out.write(cnt.getBytes());
			out.close();
		}
		catch(IOException e) { throw new RuntimeException(e); }
	}
	
}
