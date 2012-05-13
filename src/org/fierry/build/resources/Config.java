package org.fierry.build.resources;

import java.nio.file.Path;
import java.util.Map.Entry;
import java.util.HashSet;
import java.util.Set;

import org.fierry.build.linking.GlobalConfig;
import org.fierry.build.yaml.ActionY;
import org.fierry.build.yaml.BehaviorY;
import org.fierry.build.yaml.ConfigY;
import org.fierry.build.yaml.FileY;

public class Config extends Resource {

	private Set<Entry<String, FileY>> files;
	private Set<Entry<String, ActionY>> actions;
	private Set<Entry<String, BehaviorY>> behaviors;
	
	public Config(Path path) {
		super(path);
		
		this.files     = new HashSet<Entry<String,FileY>>();
		this.actions   = new HashSet<Entry<String,ActionY>>();
		this.behaviors = new HashSet<Entry<String,BehaviorY>>();
	}
	
	public void setContent(ConfigY content) {
		if(content.files != null) {
			this.files = content.files.entrySet();
		}
		if(content.actions != null) {
			this.actions   = content.actions.entrySet();
		}
		if(content.behaviors != null) {
			this.behaviors = content.behaviors.entrySet();
		}
	}
	
	public void removeContent() {
		this.files.clear();
		this.actions.clear();
		this.behaviors.clear();
	}
	
	public void build(GlobalConfig global) {
		for(Entry<String, FileY> e : files) {
			global.setFileData(e.getKey(), e.getValue());
		}
		
		for(Entry<String, BehaviorY> e : behaviors) {
			global.setBehavior(e.getKey(), e.getValue());
		}
		
		for(Entry<String, ActionY> e : actions) {
			global.setActionProduction(e.getKey(), e.getValue());
		}
	}
}