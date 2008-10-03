package edu.ucsd.ccdb.ontomorph2.core.semantic;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import edu.ucsd.ccdb.ontomorph2.core.tangible.ISelectable;

/**
 * Defines an entity that contains semantic knowledge.  Can be associated with a 
 * class implementing ISemanticsAware.  Can be selected.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public abstract class SemanticThing extends Observable implements ISelectable {

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

	/**
	 * Returns a list of spatial semantic properties that hold between this
	 * Semantic thing and the other semantic thing.
	 * 
	 * @return
	 */
	public List<SemanticProperty> findPropertiesThatHold(SemanticThing other) {
		// TODO Auto-generated method stub
		return null;
	}


}
