package org.fierry.build.resources;

import java.nio.file.Path;
import java.util.Map.Entry;

import org.fierry.build.linking.GlobalConfig;
import org.fierry.build.utils.Template;
import org.fierry.build.yaml.FileY;

public class Script extends Resource {

	public Script(Path path) {
		super(path);
	}
	
	public void deploy(StringBuilder builder, GlobalConfig configuration) {
		Template.get("modules/script")
				.replace("name", name)
				.replace("requires", getRequires(configuration))
				.replaceLine("content", content)
				.appendTo(builder);
	}
	
	private String getRequires(GlobalConfig configuration) {
		StringBuilder builder = new StringBuilder();
		FileY data = configuration.getFileData(name);
		
		for(Entry<String, String> e : data.require.entrySet()) {
			Boolean upperCase = Character.isUpperCase(e.getKey().charAt(0));
			Boolean isDot = e.getValue().contains(".");
			String[] value = e.getValue().split("\\.");
			
			Template.get("modules/script_require")
					.replace("name", e.getKey())
					.replace("path", value[0])
					.replace("tail", value.length == 2 && value[1] != "" ? "." + value[1] : "")
					.replace("require", upperCase || isDot ? "F.srequire" : "F.require")
					.appendTo(builder);			
		}
		return builder.toString();
	}
}