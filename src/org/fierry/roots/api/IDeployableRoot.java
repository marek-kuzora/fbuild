package org.fierry.roots.api;

public interface IDeployableRoot extends IRoot, IDeployable, IContainer {

	public String require(String path);
}
