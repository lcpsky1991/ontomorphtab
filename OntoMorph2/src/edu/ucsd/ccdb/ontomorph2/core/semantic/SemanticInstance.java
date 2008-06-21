package edu.ucsd.ccdb.ontomorph2.core.semantic;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;


/**
 * Represents an OWL instance.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class SemanticInstance extends SemanticThingImpl {

	Instance _ins = null;
	
	public SemanticInstance(Instance i) {
		this._ins = i;
	}
	
	public String getLabel() {

//		must be done before getLabel() is run!!!
		KnowledgeBase owlModel = SemanticRepository.getInstance().getOWLModel();
		
		String label = null;
		
		Slot rdfsLabel = owlModel.getSlot("rdfs:label");
		if (owlModel != null) {

			//Cls root = owlModel.getRootCls();
			//Cls entity = owlModel.getCls("bfo:Entity");
			//System.out.println("The root class is: " + entity.getName());
			//Node rootNode = getTree().addRoot();
			rdfsLabel = owlModel.getSlot("rdfs:label");
			label = (String)_ins.getDirectOwnSlotValue(rdfsLabel);
			
			/*
			String prefix = null;//owlModel.getPrefixForResourceName(entity.getName());
			if (prefix != null) {
				label =  prefix + ":" + label;
			}
			
			if (this.URI != null) {
				label = label + "(" + this.URI + ")";
			}*/
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

}
