package org.fierry.build.files;

public class TopPackage extends Package {

	@Override public void setConfig() {
		throw new UnsupportedOperationException("Config not allowed in the TopPackage");
	}
}
