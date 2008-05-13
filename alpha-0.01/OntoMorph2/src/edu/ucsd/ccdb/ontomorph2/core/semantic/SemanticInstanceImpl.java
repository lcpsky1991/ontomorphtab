package edu.ucsd.ccdb.ontomorph2.core.semantic;

import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;

public class SemanticInstanceImpl extends SemanticThingImpl implements ISemanticInstance {

	Instance _ins = null;
	
	public SemanticInstanceImpl(Instance i) {
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

}
