package org.fierry.roots.api;

import org.fierry.build.yaml.ActionY;

public interface IContainer extends INode {

	public String getUid();
	
	public ActionY getProduction(String type);
}
