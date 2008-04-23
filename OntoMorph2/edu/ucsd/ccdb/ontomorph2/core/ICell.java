package edu.ucsd.ccdb.ontomorph2.core;

import java.net.URL;
import java.util.List;

public interface ICell {

		public IStructure2D lnkStructure2D = null;

	public ISemanticThing lnkSemanticThing = null;

	public IPopulation lnkPopulation = null;

	public IMorphology lnkMorphology = null;
	
	public IMorphology getMorphology();
	
	public void setMorphologyViaURL(URL morphLoc);
	
	public void setMorphology(IMorphology morph);
	
	public void addSemanticThing(ISemanticThing thing);

	public List<ISemanticThing> getSemanticThings();
	
	public void removeSemanticThing(ISemanticThing thing);
}