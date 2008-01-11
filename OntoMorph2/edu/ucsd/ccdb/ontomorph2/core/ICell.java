package edu.ucsd.ccdb.ontomorph2.core;
public interface ICell {

		public edu.ucsd.ccdb.ontomorph2.core.IStructure2D lnkStructure2D = null;

	public ISemanticThing lnkSemanticThing = null;

	public IPopulation lnkPopulation = null;

	public IMorphology lnkMorphology = null;
	
	public IMorphology getMorphology();
}