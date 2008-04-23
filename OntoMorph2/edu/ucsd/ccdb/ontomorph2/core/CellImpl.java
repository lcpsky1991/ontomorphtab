package edu.ucsd.ccdb.ontomorph2.core;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CellImpl implements ICell, ISemanticsAware {
	
	IMorphology _morph = null;
	List<ISemanticThing> semanticThings = new ArrayList<ISemanticThing>();
	
	public CellImpl() {
	}
	
	public void setMorphologyViaURL(URL morphLoc) {
		_morph = new MorphologyImpl(this, morphLoc, null, null);
	}

	public IMorphology getMorphology() {
		return _morph;
	}


	public void setMorphology(IMorphology morph) {
		_morph = morph;
	}


	public List<ISemanticThing> getSemanticThings() {
		return semanticThings;
	}
	
	
	public void addSemanticThing(ISemanticThing thing) {
		this.semanticThings.add(thing);
		thing.addSemanticsAwareAssociation(this);
	}
	
	public void removeSemanticThing(ISemanticThing thing) {
		this.semanticThings.remove(thing);
		thing.removeSemanticsAwareAssociation(this);
	}

}
