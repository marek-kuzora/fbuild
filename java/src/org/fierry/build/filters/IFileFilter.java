package org.fierry.build.filters;

import java.nio.file.Path;

import org.fierry.build.projects.IProject;

/*
 * Zak³adamy tutaj, ¿e fileCreated jest ró¿ne od fileVisited?
 * Tzn jak trafiê na rls/ directory to chcia³bym go nie dodawaæ i ¿adnych jego dzieci...
 * A mo¿e móg³bym to obs³u¿yæ domyœlnie???!! 
 */
public interface IFileFilter {

	public Boolean accept(Path path, IProject project);
	
	public void fileCreated(Path path, IProject project);
	
	public void fileUpdated(Path path, IProject project);
	
	public void fileDeleted(Path path, IProject project);
}
