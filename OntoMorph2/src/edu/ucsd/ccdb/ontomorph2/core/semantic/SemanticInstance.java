package edu.ucsd.ccdb.ontomorph2.core.semantic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;
import edu.stanford.smi.protegex.owl.model.OWLIndividual;
import edu.stanford.smi.protegex.owl.model.OWLNamedClass;
import edu.stanford.smi.protegex.owl.model.impl.AbstractOWLProperty;
import edu.ucsd.ccdb.ontomorph2.core.data.GlobalSemanticRepository;
import edu.ucsd.ccdb.ontomorph2.core.data.LocalSemanticRepository;
import edu.ucsd.ccdb.ontomorph2.util.Log;
import edu.ucsd.ccdb.ontomorph2.util.OMTOfflineException;


/**
 * Represents an OWL instance.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class SemanticInstance extends SemanticThingImpl {

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
			AbstractOWLProperty aop = (AbstractOWLProperty)it.next();
			l.add(new SemanticProperty(aop));
		}
		return l;
	}
	
	public String getLabel() {
		KnowledgeBase owlModel = null;
		try {
//			must be done before getLabel() is run!!!
			owlModel = GlobalSemanticRepository.getInstance().getOWLModel();
			
		} catch (OMTOfflineException e) {
			Log.warn(e.getMessage()+ "using local semantic repository instead");
			owlModel = LocalSemanticRepository.getInstance().getOWLModel();
		}
		String label = null;
		
		Slot rdfsLabel = owlModel.getSlot("rdfs:label");
		if (owlModel != null) {
			rdfsLabel = owlModel.getSlot("rdfs:label");
			label = (String)instance.getDirectOwnSlotValue(rdfsLabel);
		}
		return label;
	}

	/**
	 * Adds a relation between this ISemanticInstance and i, through SemanticProperty p.
	 * @param p
	 * @param i
	 */
	public void addRelationToInstance(SemanticProperty p, SemanticInstance i) {
		// TODO Auto-generated method stub
		
	}

	public String getId() {
		return instance.getName();
	}

}
