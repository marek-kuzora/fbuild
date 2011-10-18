package org.fierry.build.view.core;

public interface Deployable {
	public static final String SEPARATOR = ":";

	public void deploy(StringBuilder builder);
}
