package org.fierry.build.filters;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;

import org.fierry.build.app.Project;
import org.fierry.build.view.core.Root;
import org.fierry.build.view.parser.AbstractSyntaxTree;
import org.fierry.build.view.parser.Token;
import org.fierry.build.view.parser.Tokenizer;

public class ConfigurationFileFilter extends ExtensionFileFilter implements IFileFilter {
	public static final String FILE_EXT = ".v";

	@Override public Boolean accept(Path path, Project project) {
		return accept(path, project, FILE_EXT);
	}

	@Override public Boolean fileCreated(Path path, Project project) {
		return fileUpdated(path, project);
	}

	@Override public Boolean fileUpdated(Path path, Project project) {
		Collection<Token> tokens = Tokenizer.getTokens(path, project.readFile(path));
		Collection<Root> roots   = AbstractSyntaxTree.getRoots(path, tokens);
		
		project.getRootFile(path).setContent(roots);
		return true;
	}
	
	@Override public Boolean fileDeleted(Path path, Project project) {
		Collection<Root> roots = Collections.emptyList();
		project.getRootFile(path).setContent(roots);
		return true;
	}

}
