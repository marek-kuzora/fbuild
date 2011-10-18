package org.fierry.build.view.parser;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.fierry.build.view.core.Node;
import org.fierry.build.view.core.Root;
import org.fierry.build.view.nodes.ExportRoot;
import org.fierry.build.view.nodes.MultilineNode;
import org.fierry.build.view.nodes.RequireRoot;
import org.fierry.build.view.nodes.RunRoot;
import org.fierry.build.view.parser.Token;

/**
 * Każdy token może mieć przekazaną LINIĘ w której się pojawił!
 * Wtedy bardzo łatwo będzie wyświetlać tą linię w przypadku błędu.
 */
public class AbstractSyntaxTree {
	
	private int indent;
	private Node node;
	private Deque<Node> parents;
	
	private Path path;
	private Iterator<Token> tokens;
	private Collection<Root> roots;

	public static Collection<Root> getRoots(Path path, Collection<Token> tokens) {
		AbstractSyntaxTree ast = new AbstractSyntaxTree(path, tokens.iterator());
		ast.parse();
		
		return ast.roots;
	}
	
	private AbstractSyntaxTree(Path path, Iterator<Token> tokens) {
		this.path   = path;
		this.tokens = tokens;
		
		this.indent  = 0;
		this.roots   = new ArrayList<Root>();
		this.parents = new LinkedList<Node>();
	}

	public void parse() {

		// Stream can be empty.
		if(tokens.hasNext()) {
			Token token = tokens.next();
			
			// Expect only ACTION token.
			if(token.is("ACTION")) { actionToken((Token.Action) token); return; }
			error("Illegal token found: /1, expected: ACTION", token);
		}
	}
	
	/*
	 * Dobra, jeśli będę tworzył Action bądź Root to jak to trzymam?? Jakiś wspólny interfejs??
	 */
	private void actionToken(Token.Action token) {
		node = parent() != null ? parent().addNode(token) : createRootNode(token);

		// Stream can be empty if there is no indentation.
		if(tokens.hasNext() || indent != 0) {
			Token next = tokens.next();
			
			if(next.is("ACTION")) { actionToken((Token.Action) next); return; }
			if(next.is("BEGIN"))  { beginToken(next); return; }
			if(next.is("END"))    { endToken(next);   return; }
			
			error("Illegal token found: /1, expected: ACTION, BEGIN, END", next);
		}
	}
	
	private Root createRootNode(Token.Action token) {
		String type = token.getType();
		Map<String, String> args = new HashMap<String, String>();
		
		Root root = null;
		if(type.equals("require")) { root = new RequireRoot(token, args); }
		if(type.equals("export"))  { root = new ExportRoot(token, args);  }
		if(type.equals("run"))     { root = new RunRoot(token, args);     }
		
		if(root != null) { roots.add(root); }
		else { error("Root node not found for type: /1", type); }
		
		return root;
	}
	
	private Node parent() {
		return parents.peekLast();
	}
	
	private void beginToken(Token token) {
		indent++;
		parents.offer(node);
		
		Token next = tokens.next();
		
		if(next.is("ACTION")) { actionToken((Token.Action) next); return; }
		if(next.is("VALUE"))  { valueToken(next, 1); return; }
		
		error("Illegal token found: /1, expected: ACTION, VALUE", next);
	}
	
	private void endToken(Token token) {
		indent--;
		parents.pollLast();
		
		if(indent < 0) {
			error("Negative indentation found: /1", indent);
		}
		
		// Stream can be empty if there is no indentation.
		if(tokens.hasNext() || indent != 0) {
			Token next = tokens.next();
			
			if(next.is("ACTION")) { actionToken((Token.Action) next); return; }
			if(next.is("END"))    { endToken(next); return; }
			
			error("Illegal token found: /1, expected: ACTION, END", next);
		}
	}
	
	private void valueToken(Token token, int padding) {
		assert node instanceof MultilineNode : "Cannot attach additional data into a single-line node";
		((MultilineNode) node).addData(getPadding(padding) + token.getData());
		
		Token next = tokens.next();
		
		if(next.is("VALUE")) { valueToken(next, padding);      return; }
		if(next.is("BEGIN")) { valueBeginToken(next, padding); return; }
		if(next.is("END"))   { valueEndToken(next, padding);   return; }
		
		error("Illegal token found: /1, expected: VALUE, BEGIN, END", next);
	}
	
	private String getPadding(int length) {
		StringBuilder padding = new StringBuilder();
		for(int i = 0; i < length; i++) { padding.append("  "); }

		return padding.toString();
	}
	
	private void valueBeginToken(Token token, int padding) {
		padding++;
		
		Token next = tokens.next();
		
		if(next.is("VALUE")) { valueToken(next, padding);      return; }
		if(next.is("BEGIN")) { valueBeginToken(next, padding); return; }
		
		error("Illegal token found: /1, expected: VALUE, BEGIN", next);
	}
	
	private void valueEndToken(Token token, int padding) {
		padding--;
		if(padding == 0) { endToken(token); return; }
		
		Token next = tokens.next();
		
		if(next.is("VALUE")) { valueToken(next, padding);    return; }
		if(next.is("END"))   { valueEndToken(next, padding); return; }
		
		error("Illegal token found: /1, expected: VALUE, END", next);
	}
	
	private void error(String s, Object... args ) {
		for(int i = 0; i < args.length; i++) {
			s = s.replace("/" + i, args[i].toString());
		}
		throw new IllegalStateException(path + ": " + s);
	}
}
