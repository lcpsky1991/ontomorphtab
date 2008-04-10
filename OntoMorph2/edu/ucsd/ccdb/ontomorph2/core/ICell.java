package edu.ucsd.ccdb.ontomorph2.core;

import java.net.URL;

public interface ICell {

		public edu.ucsd.ccdb.ontomorph2.core.IStructure2D lnkStructure2D = null;

	public ISemanticThing lnkSemanticThing = null;

	public IPopulation lnkPopulation = null;

	public IMorphology lnkMorphology = null;
	
	public IMorphology getMorphology();
	
	public void setMorphologyViaURL(URL morphLoc);
	
	public void setMorphology(IMorphology morph);
}