package org.fierry.build.project;

public enum Lang {
	JavaScript   (".js"),
	CoffeeScript (".cs");
	
	public static Lang get(String name) {
		name = name.toLowerCase();
		
		if(name.equals("javascript") || name.equals("js"))   { return JavaScript; }
		if(name.equals("coffeescript") || name.equals("cs")) { return CoffeeScript; }
		
		throw new IllegalArgumentException("Language not supported: " + name);
	}
	
	public final String extension;
	
	Lang(String extension) {
		this.extension = extension;
	}
}
