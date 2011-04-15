package org.fierry.build.filters;

import java.nio.file.Path;

import org.fierry.build.projects.IProject;

/*
 * Zak�adamy tutaj, �e fileCreated jest r�ne od fileVisited?
 * Tzn jak trafi� na rls/ directory to chcia�bym go nie dodawa� i �adnych jego dzieci...
 * A mo�e m�g�bym to obs�u�y� domy�lnie???!! 
 */
public interface IFileFilter {

	public Boolean accept(Path path, IProject project);
	
	public void fileCreated(Path path, IProject project);
	
	public void fileUpdated(Path path, IProject project);
	
	public void fileDeleted(Path path, IProject project);
}
