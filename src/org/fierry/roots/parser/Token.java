package org.fierry.roots.parser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.apache.commons.lang3.StringUtils;

public class Token {

	public static final Token BEGIN = new Token("BEGIN");
	public static final Token END = new Token("END");
	
	public static Token VALUE(String data) {
		return new Token("VALUE", data);
	}
	
	public static Token ACTION(String data) {
		Queue<String> stream = new LinkedList<String>();
		for(String i : data.split(" ")) { stream.add(i); }

		String action = stream.poll().substring(1);
		List<String> tags = new ArrayList<String>();
		
		String tag = null;
		while((tag = stream.peek()) != null && tag.startsWith("-")) {
			tags.add(stream.poll());
		}
		return new Action(action, tags, StringUtils.join(stream, ' '));
	}
	
	private String type;
	private String data;
	
	private Token(String type) {
		this(type, null);
	}
	
	protected Token(String type, String data) {
		this.type = type;
		this.data = data;
	}
	
	public String getData() {
		return data;
	}
	
	public Boolean is(String type) {
		return this.type.equals(type);
	}
	
	@Override public String toString() {
		return type;
	}
	
	
	public static class Action extends Token {
		private String action;
		private List<String> tags;
		
		public Action(String action, List<String> tags, String data) {
			super("ACTION", data);
			
			this.tags = tags;
			this.action = action;
		}
		
		public String getType() {
			return action;
		}
		
		public List<String> getTags() {
			return tags;
		}
		
		@Override public String toString() {
			return "ACTION(" + action + ")";
		}
	}
}
