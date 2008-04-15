package edu.ucsd.ccdb.ontomorph2.core;

import edu.stanford.smi.protege.model.Cls;

public class SemanticThingImpl implements ISemanticThing {

	Cls OWLClass = null;
	
	public SemanticThingImpl(Cls owlClass) {
		OWLClass = owlClass;
	}
}
