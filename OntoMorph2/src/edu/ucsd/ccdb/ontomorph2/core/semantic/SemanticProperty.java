package edu.ucsd.ccdb.ontomorph2.core.semantic;

import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLProperty;

/**
 * Represents an OWL Property.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class SemanticProperty{

	AbstractOWLProperty property = null;
	
	public SemanticProperty(AbstractOWLProperty aop) {
		this.property = aop; 
	}

}
