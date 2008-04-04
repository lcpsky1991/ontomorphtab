package edu.ucsd.ccdb.ontomorph2.core;

import java.net.URL;

public class CellImpl implements ICell {
	
	IMorphology _morph = null;
	
	public CellImpl() {
	}
	
	public void setMorphologyViaURL(URL morphLoc) {
		_morph = new MorphologyImpl(morphLoc, null, null);
	}

	public IMorphology getMorphology() {
		return _morph;
	}

	public void setMorphology(IMorphology morph) {
		_morph = morph;
	}

}
