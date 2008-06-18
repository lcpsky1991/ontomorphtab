package edu.ucsd.ccdb.ontomorph2.core.semantic;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import edu.stanford.smi.protege.model.Cls;
import edu.stanford.smi.protege.model.Instance;

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
		return SemanticRepository.getInstance().getClassLabel(OWLClass, URI);
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

	public List<SemanticProperty> findPropertiesThatHold(ISemanticThing other) {
		// TODO Auto-generated method stub
		return null;
	}


}
