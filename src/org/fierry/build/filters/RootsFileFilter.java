package org.fierry.build.filters;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;

import org.fierry.build.app.Project;
import org.fierry.build.resources.Roots;
import org.fierry.roots.api.IRoot;
import org.fierry.roots.parser.AbstractSyntaxTree;
import org.fierry.roots.parser.Token;
import org.fierry.roots.parser.Tokenizer;

public class RootsFileFilter extends ExtensionFileFilter implements IFileFilter {
	public static final String FILE_EXT = ".v";

	@Override public Boolean accept(Path path, Project project) {
		return accept(path, project, FILE_EXT);
	}

	@Override public Boolean fileCreated(Path path, Project project) {
		return fileUpdated(path, project);
	}

	@Override public Boolean fileUpdated(Path path, Project project) {
		try {
			Collection<Token> tokens = Tokenizer.getTokens(path, project.read(path));
			Collection<IRoot> roots   = AbstractSyntaxTree.getRoots(path, tokens);
			project.getResource(path, Roots.class).setContent(roots);
		} catch(RuntimeException e) {
			e.printStackTrace();
			
			Collection<IRoot> roots = Collections.emptyList();
			project.getResource(path, Roots.class).setContent(roots);
		}
		return true;
	}
	
	@Override public Boolean fileDeleted(Path path, Project project) {
		Collection<IRoot> roots = Collections.emptyList();
		project.getResource(path, Roots.class).setContent(roots);
		return true;
	}

}
