package org.fierry.build.utils;

public class Uid {

	//25
	private static final char[] CHARS = {
		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'w', 'x', 'y', 'z', 
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'W', 'X', 'Y', 'Z',
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '?', '#', '@', '&'
	};

	/*
	 * Podstawa 32. Tablicy brakuje ;P
	 */
	public static String generate(int i) {
		String uid = "";
		for(int j = 0; j < 2; j++) {
			uid = CHARS[i % 64] + uid;
			i = i >> 6;
		}
		return uid;
	}
}
