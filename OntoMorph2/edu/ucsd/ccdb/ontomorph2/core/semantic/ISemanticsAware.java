package edu.ucsd.ccdb.ontomorph2.core.semantic;

import java.util.List;

import edu.ucsd.ccdb.ontomorph2.core.scene.ISelectable;

/** Any type on the model side that needs to be associated with a list of ISemanticThings
 * and have those ISemanticThings be associated with it.
 */
public interface ISemanticsAware extends ISelectable{

	public List<ISemanticThing> getSemanticThings();
	
	public void addSemanticThing(ISemanticThing thing);
	
	public void removeSemanticThing(ISemanticThing thing);
}
