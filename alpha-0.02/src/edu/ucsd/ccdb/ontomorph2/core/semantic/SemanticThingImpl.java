package edu.ucsd.ccdb.ontomorph2.core.semantic;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public abstract class SemanticThingImpl extends Observable implements ISemanticThing {

	String URI = null;
	List<ISemanticsAware> semanticsAware = new ArrayList<ISemanticsAware>();
	private boolean selected;	

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
