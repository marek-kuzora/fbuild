package org.fierry.build.utils;

import org.apache.commons.lang3.StringUtils;

public class Lines {

	public static final String separator = System.getProperty("line.separator");
	
	public static String indent_2(String s, int length) {
		String indent = separator;
		for(int i = 0; i < length; i++) { indent += " "; }
		
		return s.replace(separator, indent);
	}
	
	public static String indent(String s, int length) {
		String indent = "";
		for(int i = 0; i < length; i++) { indent += " "; }
		
		String[] arr = s.split(separator);
		for(int i = 0; i < arr.length; i++) { arr[i] = indent + arr[i]; }
		
		return StringUtils.join(arr, separator);
	}
}
