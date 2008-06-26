package edu.ucsd.ccdb.ontomorph2.view.procedural;

import edu.ucsd.ccdb.ontomorph2.core.scene.objects.SceneObject;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticProperty;

/**
 * Operates on scene objects and creates a visual connection between them that 
 * visually indicates the presence of a particular semantic property.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public abstract class SemanticPropertySchematicRenderer {

	protected void setSemanticProperty(SemanticProperty s) {
		
	}
	
	public void setDomainObject(SceneObject domainObject) {
		//can do checking here with the property to make sure that 
		//the semantic thing associated with the scene object
		//is acceptable in the domain of this object
	}
	
	public void setRangeObject(SceneObject rangeObject) {
		//can do checking here with the property to make sure that
		//the semantic thing associated with the scene object is 
		//acceptable in the range of this object
	}
}
