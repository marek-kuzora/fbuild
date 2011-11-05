package org.fierry.roots.api;

import org.fierry.build.project.Lang;

public interface IDeployable {

	public void deploy(StringBuilder builder, Lang lang);
}
