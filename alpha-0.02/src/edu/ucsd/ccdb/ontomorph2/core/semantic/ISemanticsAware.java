package edu.ucsd.ccdb.ontomorph2.core.semantic;

import java.util.List;

import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.ISelectable;

/** 
 * Any type that needs to be associated with a list of ISemanticThings and have those 
 * ISemanticThings be associated with it.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 * @see ISemanticThing
 */
public interface ISemanticsAware extends ISelectable{

	/**
	 * Get a list of any semantic classes that have been associated with this item
	 * @return
	 */
	public List<SemanticClass> getSemanticClasses();
	
	/**
	 * Add an association to a semantic class with this item
	 * @param thing
	 */
	public void addSemanticClass(SemanticClass thing);
	
	/**
	 * Remove an association between a semantic class and this item
	 * @param thing
	 */
	public void removeSemanticClass(SemanticClass thing);
	
	/**
	 * Gets semantic classes for this object plus any associated objects
	 * @return
	 */
	public List<SemanticClass> getAllSemanticClasses();
	
	/**
	 * Get the main semantic class that describes this object
	 * @return
	 */
	public SemanticClass getMainSemanticClass();
	
	/**
	 * Get the main semantic instance the describes this object.  If it does not exist, 
	 * instantiate it from the main semantic class
	 * @return
	 */
	public SemanticInstance getMainSemanticInstance();
}
