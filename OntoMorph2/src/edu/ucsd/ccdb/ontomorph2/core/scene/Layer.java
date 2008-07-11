package edu.ucsd.ccdb.ontomorph2.core.scene;

import java.util.List;

import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticThing;
import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticsAware;

/**
 * Defines a collection of objects in a scene whose visibility is linked. 
 * A list of data layers can be selected to show a particular collection of data.
 * Users are allowed to add particular collections of data to their own layers.
 * Layers are all publicly viewable.  Individual users can elect to turn off a layer, but
 * by default they are all visible.  Layers are associated with a set of instances.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 */
public class Layer implements ISemanticsAware{

	public List<ISemanticThing> getSemanticThings() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addSemanticThing(ISemanticThing thing) {
		// TODO Auto-generated method stub
		
	}

	public void removeSemanticThing(ISemanticThing thing) {
		// TODO Auto-generated method stub
		
	}

	public List<ISemanticThing> getAllSemanticThings() {
		// TODO Auto-generated method stub
		return null;
	}

	public void select() {
		// TODO Auto-generated method stub
		
	}

	public void unselect() {
		// TODO Auto-generated method stub
		
	}

	public boolean isSelected() {
		// TODO Auto-generated method stub
		return false;
	}

}
