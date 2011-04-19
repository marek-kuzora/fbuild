package org.fierry.build.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

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
			InputStream in = Growl.class.getResourceAsStream(name);
			byte[] bytes = new byte[in.available()];
			
			in.read(bytes);
			return new String(bytes);
		}
		catch(IOException e) { throw new RuntimeException(e); }
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
