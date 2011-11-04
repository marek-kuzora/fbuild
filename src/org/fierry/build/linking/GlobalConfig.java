package org.fierry.build.linking;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fierry.build.resources.Config;
import org.fierry.build.yaml.ActionY;
import org.fierry.build.yaml.BehaviorY;

/**
 * Issues:
 * 1. There should be no two rules for single type in a single production group.
 */
public class GlobalConfig {

	private Map<String, BehaviorY> behaviors;
	private Map<String, List<ActionY>> actions;
	
	public GlobalConfig(Collection<Config> files) {
		this.behaviors = new HashMap<String, BehaviorY>();
		this.actions   = new HashMap<String, List<ActionY>>();
		
		for(Config config : files) {
			config.build(this);
		}
	}
	
	public void setBehavior(String name, BehaviorY behavior) {
		assert behaviors.get(name) == null : "Behavior already exists for name: " + name;
		behaviors.put(name, behavior);
	}
	
	public void setActionProduction(String name, ActionY action) {
		action.setup(this, name);

		for(String group : action.from) {
			getGroup(group).add(action);
		}
	}
	
	private Collection<ActionY> getGroup(String group) {
		if(!actions.containsKey(group)) {
			actions.put(group, new ArrayList<ActionY>());
		}
		return actions.get(group);
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
	
	public BehaviorY getBehavior(String name) {
		return behaviors.get(name);
	}
}
