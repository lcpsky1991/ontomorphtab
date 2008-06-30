package edu.ucsd.ccdb.ontomorph2.core.semantic;

import java.util.List;

import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.ISelectable;

/** 
 * Any type that needs to be associated with a list of ISemanticThings and have those ISemanticThings be associated with it.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 * @see ISemanticThing
 */
public interface ISemanticsAware extends ISelectable{

	public List<ISemanticThing> getSemanticThings();
	
	public void addSemanticThing(ISemanticThing thing);
	
	public void removeSemanticThing(ISemanticThing thing);
	
	public List<ISemanticThing> getAllSemanticThings();
}
