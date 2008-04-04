package edu.ucsd.ccdb.ontomorph2.core;

//import edu.stanford.smi.protege.model.Cls;

/**
 * @$comment Contains the semantic description of a biological object (OWL)
 */

public interface ISemanticThing {

	public edu.ucsd.ccdb.ontomorph2.core.IPopulation lnkIPopulation = null;

	public IVariabilityTransformFunction lnkVariabilityTransformFunction = null;

	public ISegmentGroup lnkSegmentGroup = null;

	public SemanticRepository lnkSemanticRepository = null;

	public ISegment lnkCompartment = null;
}