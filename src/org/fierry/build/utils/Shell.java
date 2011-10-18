package org.fierry.build.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Shell {

	public static String run(String[] args) {
		return run(args, new byte[0]);
	}
	
	public static String run(String[] args, String string) {
		return run(args, string.getBytes());
	}
	
	public static String run(String[] args, StringBuilder builder) {
		return run(args, builder.toString().getBytes());
	}
	
	public static String run(String[] args, byte[] bytes) {
		try {
			Runtime rtm = Runtime.getRuntime();
			Process pr = rtm.exec(args);
			
			if(bytes != null) {
				OutputStream out = pr.getOutputStream();
				out.write(bytes);
				out.close();
			}
			
			InputStream in = pr.getInputStream();
			StringBuilder builder = new StringBuilder();
			int c;
			
			while ((c = in.read()) != -1) {
				builder.append((char)c);
			}
			return builder.toString();
		}
		catch(IOException e) { throw new RuntimeException(e); }
	}
}
