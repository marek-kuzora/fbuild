package org.fierry.build.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

public class CoffeeScript {
	private static final String separator = String.valueOf(new char[] {0, 0, 0, 0, 0});
	private static final CoffeeScript instance = new CoffeeScript();
	
	public static CoffeeScript get() {
		return instance;
	}
	
	private Runtime runtime;
	private Process process;
	
	private InputStream in;
	private OutputStream out;
	
	private CoffeeScript() {
		start();
	}
	
	private void start() {
		try {
			runtime = Runtime.getRuntime();
			process = runtime.exec(new String[] { getScriptPath() });
			
			in  = process.getInputStream();
			out = process.getOutputStream();
		}
		catch(IOException e) { throw new RuntimeException(e); }
	}
	
	private String getScriptPath() {
		return Directory.getJar() + File.separator + "fierry-coffee";
	}
	
	synchronized public String compile(String code) {
		try {
			Date date = new Date();
			System.out.print("Start... ");
			out.write(code.getBytes());
			out.write(separator.getBytes());
			out.flush();
			
			StringBuilder builder = new StringBuilder();
			int c = -1;
			
			while(continueReading(builder) && (c = in.read()) != -1) {
				builder.append((char) c);
			}
			
			// Handing unexpected compilation errors.
			if(c == -1) {
				start();
				return "";
			}
			System.out.println(new Date().getTime() - date.getTime() + " ms");
			return builder.substring(0, getStreamLength(builder));
		}
		catch(IOException e) { throw new RuntimeException(e); }
	}
	
	private boolean continueReading(StringBuilder sb) {
		return sb.length() < separator.length() || !sb.substring(getStreamLength(sb)).equals(separator);
	}
	
	private int getStreamLength(StringBuilder builder) {
		return builder.length() - separator.length();
	}
}
