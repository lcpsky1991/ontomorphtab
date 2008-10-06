package edu.ucsd.ccdb.ontomorph2.core.semantic;

import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.model.RDFProperty;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFProperty;

/**
 * Represents an OWL Property.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class SemanticProperty{

	public static String CONTAINS = "obo_ro:contains";
	public static String HAS_PART = "obo_ro:has_part";
	
	DefaultRDFProperty property = null;
	
	public SemanticProperty(DefaultRDFProperty aop) {
		this.property = aop; 
	}

	public RDFProperty getOWLProperty() {
		return property;
	}
	
	public String getLabel() {
		KnowledgeBase owlModel = null;
//			must be done before getLabel() is run!!!
		owlModel = SemanticRepository.getAvailableInstance().getOWLModel();
		String label = null;
		
		Slot rdfsLabel = owlModel.getSlot("rdfs:label");
		if (owlModel != null) {
			label = (String)property.getDirectOwnSlotValue(rdfsLabel);
		}
		return label;
	}

	
	public boolean equals(Object o) {
		if (o != null && o instanceof SemanticProperty) {
			SemanticProperty i = (SemanticProperty)o;
			if (property.equals(i.getOWLProperty()))
				return true;
		}
		return false;
	}
	
	public int hashCode() {
		return property.hashCode();
	}


}
