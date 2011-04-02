package org.fierry.build;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.Date;

import org.fierry.build.core.StagingArea;
import org.fierry.build.io.Files;

public class App {

	public static void main(String[] args) throws FileNotFoundException, URISyntaxException {
		Date date = new Date();
		System.out.println("Building project: " + Files.getProjectDirectory());
		
		StagingArea area = new StagingArea();
		area.build();
		
		System.out.println("Time elapsed: " + (new Date().getTime() - date.getTime()) + "ms");
	}
	
}
