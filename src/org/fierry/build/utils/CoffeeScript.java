package org.fierry.build.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CoffeeScript {
	private static final String separator = String.valueOf(new char[] {0, 0, 0, 0, 0});
	private static final CoffeeScript instance = new CoffeeScript();
	
	public static CoffeeScript get() {
		return instance;
	}
	
	private Runtime runtime;
	private Process process;
	
	private InputStream in;
	private InputStream err;
	private OutputStream out;
	
	private CoffeeScript() {
		start();
	}
	
	private void start() {
		try {
			runtime = Runtime.getRuntime();
			process = runtime.exec(new String[] { getScriptPath() });
			
			in  = process.getInputStream();
			err = process.getErrorStream();
			out = process.getOutputStream();
		}
		catch(IOException e) { throw new RuntimeException(e); }
	}
	
	private String getScriptPath() {
		return Directory.getJar() + File.separator + "fierry-coffee";
	}
	
	synchronized public String compile(String code) {
		try {
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
				while ((c = err.read()) != -1) {
					System.out.print((char) c);
				}
				start();
				return "";
			}
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
