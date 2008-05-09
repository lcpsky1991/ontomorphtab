package edu.ucsd.ccdb.ontomorph2.core.semantic;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;
import edu.stanford.smi.protege.model.KnowledgeBase;
import edu.stanford.smi.protege.model.Slot;

public class SemanticThingImpl extends Observable implements ISemanticThing {

	Cls OWLClass = null;
	Instance owlInstance = null;
	String URI = null;
	List<ISemanticsAware> semanticsAware = new ArrayList<ISemanticsAware>();
	private boolean selected;
	
	public SemanticThingImpl() {
		
	}
	
	public SemanticThingImpl(Cls owlClass, String uri) {
		OWLClass = owlClass;
		this.URI = uri;
	}
	
	public SemanticThingImpl(Instance owlInstance) {
		this.owlInstance = owlInstance;
	}
	
	public Cls getCls() {
		return OWLClass;
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
	
	public void unselect() {
		this.selected = false;
		changed();
	}

	public boolean isSelected() {
		return this.selected;
	}

	public void select() {
		this.selected = true;
		changed();
	}
	
	public void changed() {
		notifyObservers();
		setChanged();
	}


}
