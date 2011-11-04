package org.fierry.roots.utils;

public class Uid {

	private static final char[] CHARS = {
		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'w', 'x', 'y', 'z', 
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'W', 'X', 'Y', 'Z',
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '?', '#', '@', '&'
	};

	private int value = 0;
	
	public String generate(int length) {
		String uid = "";
		int value = this.value;
		
		for(int i = 0; i < length; i++) {
			uid = CHARS[value % 64] + uid;
			value = value >> 6;
		}
		this.value++;
		return uid;
	}
}
