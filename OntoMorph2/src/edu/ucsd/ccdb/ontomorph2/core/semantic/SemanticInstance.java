package edu.ucsd.ccdb.ontomorph2.core.semantic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.DefaultRDFProperty;


/**
 * Represents an OWL instance.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class SemanticInstance extends SemanticThing {

	OWLIndividual instance = null;
	SemanticClass sc = null;
	
	public SemanticInstance(OWLIndividual owlInstance) {
		this.instance = owlInstance;
	}
	
	public SemanticClass getSemanticClass() {
		if (sc == null) {
			
			sc = new SemanticClass((OWLNamedClass)instance.getRDFType());
		}
		return this.sc;
	}
	
	public List<SemanticProperty> getProperties() {
		List<SemanticProperty> l = new ArrayList<SemanticProperty>();
		Collection c = instance.getRDFProperties();
		for(Iterator it = c.iterator(); it.hasNext();) {
			DefaultRDFProperty aop = (DefaultRDFProperty)it.next();
			l.add(new SemanticProperty(aop));
		}
		return l;
	}
	
	public String getLabel() {
		KnowledgeBase owlModel = null;
//			must be done before getLabel() is run!!!
		owlModel = SemanticRepository.getAvailableInstance().getOWLModel();
		String label = null;
		
		Slot rdfsLabel = owlModel.getSlot("rdfs:label");
		if (owlModel != null) {
			label = (String)instance.getDirectOwnSlotValue(rdfsLabel);
		}
		return label;
	}

	/**
	 * Adds a relation between this SemanticInstance and i, through SemanticProperty p.
	 * @param p - the property to relate this SemanticInstance to i.
	 * @param i - the target SemanticInstance of property p.
	 */
	public void setPropertyValue(SemanticProperty p, SemanticInstance i) {
		instance.setPropertyValue(p.getOWLProperty(), i.getOWLIndividual());
	}

	protected OWLIndividual getOWLIndividual() {
		return instance;
	}
	

	public String getId() {
		return instance.getName();
	}
	
	/**
	 * Removes this instance from the repository.
	 * Note that doing this will make the other commands unstable
	 *
	 */
	public void removeFromRepository() {
		instance.delete();
	}
	
	/**
	 * Returns the value of property p.  If property p does not return a value of type
	 * SemanticInstance, this returns null.
	 * 
	 * @param p
	 * @return
	 */
	public SemanticInstance getPropertyValue(SemanticProperty p) {
		if (getProperties().contains(p)) {
			Object propValue = instance.getPropertyValue(p.getOWLProperty());
			if (propValue instanceof OWLIndividual) {
				OWLIndividual i = (OWLIndividual)propValue;
				return new SemanticInstance(i);
			} else {
				//Log.warn("Found a property value for " + p.getOWLProperty().getURI() + " that is not an instance of OWLIndividual! : " + propValue);
			}
		}
		return null;
	}
	
	/*
	 * Get rid of property / value pair p and i
	 */
	public void removePropertyValue(SemanticProperty p, SemanticInstance i) {
		instance.removePropertyValue(p.getOWLProperty(), i.getOWLIndividual());
	}
	
	public boolean equals(Object o) {
		if (o != null && o instanceof SemanticInstance) {
			SemanticInstance i = (SemanticInstance)o;
			if (instance.equals(i.getOWLIndividual()))
				return true;
		}
		return false;
	}
	
	public int hashCode() {
		return instance.hashCode();
	}

	

}
