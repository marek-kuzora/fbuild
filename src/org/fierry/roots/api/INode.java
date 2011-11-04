package org.fierry.roots.api;

import org.fierry.build.linking.GlobalConfig;
import org.fierry.roots.parser.Token;

public interface INode extends Cloneable {

	public INode addNode(Token.Action token);
	
	public void addArgument(String name, String value);
	
	public void configure();
	
	public void consult(INode parent, IDeployableRoot root, GlobalConfig config);
}
