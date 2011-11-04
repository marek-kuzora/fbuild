package org.fierry.build.yaml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.fierry.build.linking.GlobalConfig;


public class ActionY {

	private GlobalConfig conf;
	
	public String behavior;
	public Boolean root = false;
	
	public List<String> from   = new ArrayList<String>();
	public List<String> types  = new ArrayList<String>();
	public List<String> groups = new ArrayList<String>();
	
	public void setup(GlobalConfig conf, String name) {
		this.conf = conf;
		
		if(root) {
			from.clear();
			from.add("");
		}
		
		if(types.isEmpty()) {
			types.add(name.substring(name.lastIndexOf('/') + 1));
		}
		
		Collections.reverse(groups);
	}
	
	public boolean accept(String type) {
		return types.contains(type);
	}
	
	public String getBehavior() {
		return conf.getBehavior(behavior).location;
	}
	
	public boolean conflicts() {
		return conf.getBehavior(behavior).conflict;
	}
	
	@Override
	public String toString() {
		return types.toString();
	}
}