package org.fierry.build.parsers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.fierry.build.utils.Lines;


public class PerformanceParser {
	private static final String NAME = "([^'\"]+)";
	private static final String PERFORMANCE = "(?m)^performance (('|\")" + NAME + "('|\")),?";
	private static final String SPROPERTY = " +(('|\")" + NAME + "('|\")): *";
	private static final String PROPERTY = " +" + NAME + ": *";

	private Integer lineNo;
	private String[] code;

	public PerformanceParser(String code) {
		this.lineNo = 0;
		this.code   = code.split("\r\n|\r|\n");
	}
	
	static public Boolean accept(String code) {
		Pattern p = Pattern.compile(PERFORMANCE);		
		return p.matcher(code).find();
	}
	
	static public String parse(String code) {
		Collection<String> out   = new ArrayList<String>();
		PerformanceParser parser = new PerformanceParser(code);
		
		while(parser.hasNext()) {
			String line = parser.next();

			if(line.matches(PERFORMANCE)) {
				out.add(line);
				out.addAll(parser.parsePerformance());
			} else {
				out.add(line);
			}
		}
//		System.out.println(StringUtils.join(out, Lines.separator));
		return StringUtils.join(out, Lines.separator);
	}
	
	private Collection<String> parsePerformance() {
		Collection<String> p = new ArrayList<String>();
		
		while(hasNext()) {
			String line = next();
			
			// Skip empty lines.
			if(line.matches("[ \t]*$")) {
			
			// No indentation: performance definition has ended.
			} else if(line.matches("[^ \t].*")) {
				prev(); return p; 
			
			// Found a test entry.
			} else if(line.matches("  [^ ].*")) {
				// Group can have before, after, etc!
				p.addAll(parseTest(line));
				
			// Found non-empty line.
			} else {
				throw new IllegalStateException("Unexpected line: " + line);
			}
		}
		return p;
	}
	
	private Collection<String> parseTest(String first) {
		Collection<String> t = new ArrayList<String>();
		
		Pattern p = Pattern.compile(SPROPERTY);		
		Matcher m = p.matcher(first);
		
		if(m.find()) {
			String name = m.group(1);
			String rest = first.substring(m.end());
			
			// Parse extended test definition.
			if(rest.isEmpty()) {
				t.add(first);
				t.addAll(parseHash(name));
				
			// Parse simplified test definition.
			} else {
				t.add("  " + name + ": (i) ->");
				t.add("    while i--");
				t.addAll(parseFunction(rest, true));
				t.add("    return");
			}
		} else {
			t.addAll(parseProperty(first, 2));
		}
		return t;
	}
	
	private Collection<String> parseHash(String name) {
		Collection<String> h = new ArrayList<String>();
		
		Collection<String> run    = new ArrayList<String>();
		Collection<String> rest   = new ArrayList<String>();
		Collection<String> after  = new ArrayList<String>();
		Collection<String> before = new ArrayList<String>();

		while(hasNext()) {
			String line = next();
			
			// Skip empty lines.
			if(line.matches("[ \t]*$")) {
			
			// Less indentation: hash definition has ended.
			} else if(!line.startsWith("    ")) {
				prev(); break;
			
			// Found property entry.
			} else {
				Pattern p = Pattern.compile(PROPERTY);
				Matcher m = p.matcher(line);
				
				if(m.find()) {
					if(m.group(1).equals("run")) {
						run = parseFunction(line.substring	(m.end()), false);
					}
					else if(m.group(1).equals("before_each")) {
						before = parseFunction(line.substring(m.end()), false);
					}
					else if(m.group(1).equals("after_each")) {
						after = parseFunction(line.substring(m.end()), false);
					} else {
						rest.addAll(parseProperty(line, 4));
					}
				}
			}
		}
		
		// Generating run method.
		h.add("    run: (i) ->");
		h.add("      while i--");
		h.addAll(before);
		h.addAll(run);
		h.addAll(after);
		h.add("      return");
		
		// Generating constant method.
		if(before.size() > 0 || after.size() > 0) {
			h.add("    constant: (i) ->");
			h.add("      while i--");
			h.addAll(before);
			h.addAll(after);
			h.add("      return");
		}
		
		h.addAll(rest);
		
		return h;
	}
	
	private Collection<String> parseFunction(String first, Boolean flag) {
		Collection<String> fn = new ArrayList<String>();
		
		Pattern p = Pattern.compile("(\\(i\\) +)*-> *(.+)");		
		Matcher m = p.matcher(first);
		
		if(m.find()) {
			fn.add((flag ? "" : "  ") + "      " + m.group(2));
		}
		
		while(hasNext()) {
			String line = next();
			
			// Skip empty lines.
			if(line.matches("[ \t]*$")) {
			
			// Parse function body.
			} else if((flag && line.matches("    .*")) || (!flag && line.matches("      .*"))) {
				fn.add("  " + line);
				
			// Less indentation: function definition has ended.
			} else {
				prev(); return fn;
			}
				
		}
		return fn;
	}
	
	private Collection<String> parseProperty(String first, Integer indent) {
		Collection<String> p = new ArrayList<String>();
		p.add(first);
		
		while(hasNext()) {
			String line = next();
			
			// Skip empty lines.
			if(line.matches("[ \t]*$")) {
				
			} else if(!line.startsWith("  " + StringUtils.repeat(' ', indent))) {
				prev(); return p;
				
			} else {
				p.add(line);
			}
		}
		return p;
	}
	
	private Boolean hasNext() {
		return lineNo < code.length;
	}
	
	private String next() {
		return code[lineNo++];
	}
	
	private void prev() {
		lineNo--;
	}
}