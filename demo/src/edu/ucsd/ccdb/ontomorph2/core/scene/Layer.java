package edu.ucsd.ccdb.ontomorph2.core.scene;

import java.util.List;

import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticsAware;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticClass;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticInstance;

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

	public List<SemanticClass> getSemanticClasses() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addSemanticClass(SemanticClass thing) {
		// TODO Auto-generated method stub
		
	}

	public void removeSemanticClass(SemanticClass thing) {
		// TODO Auto-generated method stub
		
	}

	public List<SemanticClass> getAllSemanticClasses() {
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

	public SemanticClass getSemanticClass() {
		// TODO Auto-generated method stub
		return null;
	}

	public SemanticInstance getSemanticInstance() {
		// TODO Auto-generated method stub
		return null;
	}

}
