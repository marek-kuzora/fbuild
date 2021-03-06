package org.fierry.build.linking;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fierry.build.resources.Config;
import org.fierry.build.yaml.ActionY;
import org.fierry.build.yaml.BehaviorY;
import org.fierry.build.yaml.FileY;

/**
 * Issues:
 * 1. There should be no two rules for single type in a single production group.
 */
public class GlobalConfig {

	private Map<String, FileY> files;
	private Map<String, BehaviorY> behaviors;
	private Map<String, List<ActionY>> gactions;
	private Map<String, ActionY> iactions;
	
	public GlobalConfig(Collection<Config> files) {
		this.files     = new HashMap<String, FileY>();
		this.behaviors = new HashMap<String, BehaviorY>();
		this.gactions   = new HashMap<String, List<ActionY>>();
		this.iactions   = new HashMap<String, ActionY>();
		
		for(Config config : files) {
			config.build(this);
		}
	}
	
	public void setFileData(String name, FileY file) {
		assert files.get(name) == null : "File already exists for name: " + name;
		files.put(name, file);
	}
	
	public void setBehavior(String name, BehaviorY behavior) {
		assert behaviors.get(name) == null : "Behavior already exists for name: " + name;
		behaviors.put(name, behavior);
	}
	
	public void setActionProduction(String name, ActionY action) {
		action.setup(this, name);
		
		assert !iactions.containsKey(name) : "Action production already exists, id: " + name;
		iactions.put(name, action);
		

		for(String group : action.from) {
			getGroup(group).add(action);
		}
	}
	
	private Collection<ActionY> getGroup(String group) {
		if(!gactions.containsKey(group)) {
			gactions.put(group, new ArrayList<ActionY>());
		}
		return gactions.get(group);
	}
	
	public ActionY getActionProduction(ActionY parent, String type) {
		for(String group : parent.groups) {
			for(ActionY data : getGroup(group)) {
				if(data.accept(type)) { return data; }
			}
		}
		throw new IllegalArgumentException("No production found for action: " + type + ", from parent: " + parent.types);
	}
	
	public ActionY getActionProduction(String type) {
		for(ActionY data : getGroup("")) {
			if(data.accept(type)) { return data; }
		}
		throw new IllegalArgumentException("No production found for root: " + type);
	}
	
	public ActionY getActionProductionById(String name) {
		assert iactions.containsKey(name) : "Action production not found, id: " + name;
		return iactions.get(name);
	}
	
	public BehaviorY getBehavior(String name) {
		return behaviors.get(name);
	}
	
	public FileY getFileData(String name) {
		if(!files.containsKey(name)) { files.put(name, new FileY()); }
		return files.get(name);
	}
}
