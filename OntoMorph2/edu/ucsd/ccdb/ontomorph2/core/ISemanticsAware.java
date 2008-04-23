package edu.ucsd.ccdb.ontomorph2.core;

import java.util.List;

public interface ISemanticsAware {

	public List<ISemanticThing> getSemanticThings();
	
	public void addSemanticThing(ISemanticThing thing);
	
	public void removeSemanticThing(ISemanticThing thing);
}
