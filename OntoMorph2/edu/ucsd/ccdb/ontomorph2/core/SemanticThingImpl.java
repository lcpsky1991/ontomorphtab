package edu.ucsd.ccdb.ontomorph2.core;

import java.util.ArrayList;
import java.util.List;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;

public class SemanticThingImpl implements ISemanticThing {

	Cls OWLClass = null;
	Instance owlInstance = null;
	String URI = null;
	List<ISemanticsAware> semanticsAware = new ArrayList<ISemanticsAware>();
	
	public SemanticThingImpl(Cls owlClass, String uri) {
		OWLClass = owlClass;
		this.URI = uri;
	}
	
	public SemanticThingImpl(Instance owlInstance) {
		this.owlInstance = owlInstance;
	}
	
	public String getLabel() {
//		must be done before getLabel() is run!!!
		KnowledgeBase owlModel = SemanticRepository.getInstance().getOWLModel();
		Slot rdfsLabel = owlModel.getSlot("rdfs:label");
		String label = null;
		if (owlModel != null) {

			//Cls root = owlModel.getRootCls();
			//Cls entity = owlModel.getCls("bfo:Entity");
			//System.out.println("The root class is: " + entity.getName());
			//Node rootNode = getTree().addRoot();
			rdfsLabel = owlModel.getSlot("rdfs:label");
			label = (String)this.OWLClass.getDirectOwnSlotValue(rdfsLabel);
			String prefix = null;//owlModel.getPrefixForResourceName(entity.getName());
			if (prefix != null) {
				label =  prefix + ":" + label;
			}
			if (this.URI != null) {
				label = label + "(" + this.URI + ")";
			}
		}
		return label;
	}
	
	public String toString() {
		return getLabel();
	}

	public List<ISemanticsAware> getSemanticsAwareAssociations() {
		return this.semanticsAware;
	}

	public void addSemanticsAwareAssociation(ISemanticsAware obj) {
		this.semanticsAware.add(obj);
	}
	
	public void removeSemanticsAwareAssociation(ISemanticsAware obj) {
		this.semanticsAware.remove(obj);
	}
}
