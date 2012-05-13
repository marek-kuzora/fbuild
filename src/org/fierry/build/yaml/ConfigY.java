package org.fierry.build.yaml;

import java.util.HashMap;
import java.util.Map;



public class ConfigY {

	public Map<String, FileY>     files     = new HashMap<String, FileY>();
	public Map<String, ActionY>   actions   = new HashMap<String, ActionY>();
	public Map<String, BehaviorY> behaviors = new HashMap<String, BehaviorY>();

}
