package org.fierry.build.parsers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fierry.build.resources.Config;
import org.fierry.build.utils.Extension;
import org.fierry.build.utils.Lines;
import org.fierry.build.yaml.ConfigY;
import org.fierry.build.yaml.Yaml;

public class InlineConfigurationParser {
	
	private static final String WHITESPACE = "^( |\r|\n|\r\n)*";
	private static final String FIRST_COMMENT = "^(#.*(\r|\n|\r\n))+";
	
	private static final String EMPTY_HASH = "(?m)^# *(\r|\n|\r\n)";
	private static final String LEADING_HASH = "(?m)^# ";
	
	private static final String SHORTCUT_ENTRY = "(?m)^(@\\w(\\w|\\d|-)*) *$";

	static public Boolean accept(String code) {
		return true;
	}
	
	static public void parse(String code, Config resource) {
		String comment = getConfigComment(code);

		if(comment == null) {
			resource.removeContent();
			return;
		}
			
		String inline = getInlineConfigString(comment);
		if(inline == null) {
			resource.removeContent();
			return;
		}
		
		String name    = Extension.trim(resource.getName());
		ConfigY config = Yaml.load(ConfigY.class, getFinalConfigString(name, inline));
		
		if(config != null) { resource.setContent(config); }
		else { resource.removeContent(); }
	}
	
	static private String getConfigComment(String file) {
		Pattern p = Pattern.compile(FIRST_COMMENT);
		Matcher m = p.matcher(file.replaceAll(WHITESPACE, ""));
		
		if(m.find()) return m.group();
		return null;
	}
	
	static private String getInlineConfigString(String comment) {
		comment = comment.replaceAll(EMPTY_HASH, "")
						 .replaceAll(LEADING_HASH, "");
		
		StringBuffer buffer = new StringBuffer();
		Matcher m = Pattern.compile(SHORTCUT_ENTRY).matcher(comment);
		
		while(m.find()) {
			m.appendReplacement(buffer, m.group(1) + ": true");
		}
		m.appendTail(buffer);
		
		if(buffer.charAt(0) != '@') { return null; }
		return buffer.toString().replace("@", "");
	}
	
	static private String getFinalConfigString(String name, String inline) {
		return "files:" + Lines.separator + "  " + name + ":" + Lines.separator + inline.replaceAll("(?m)^", "    ");
	}
}
