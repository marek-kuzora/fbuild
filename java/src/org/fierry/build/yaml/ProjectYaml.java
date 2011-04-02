package org.fierry.build.yaml;

import java.util.ArrayList;
import java.util.List;

public class ProjectYaml extends PackageYaml {

	public List<String> deploy;

	protected ProjectYaml() {
		super();
		deploy = new ArrayList<String>();
	}
}
