package edu.ucsd.ccdb.ontomorph2.core.semantic;

import java.util.List;

import edu.ucsd.ccdb.ontomorph2.core.misc.IPopulation;
import edu.ucsd.ccdb.ontomorph2.core.misc.IVariabilityTransformFunction;
import edu.ucsd.ccdb.ontomorph2.core.scene.ISegment;
import edu.ucsd.ccdb.ontomorph2.core.scene.ISegmentGroup;


//import edu.stanford.smi.protege.model.Cls;

/**
 * @$comment Contains the semantic description of a biological object (OWL)
 */

public interface ISemanticThing {

	public edu.ucsd.ccdb.ontomorph2.core.misc.IPopulation lnkIPopulation = null;

	public IVariabilityTransformFunction lnkVariabilityTransformFunction = null;

	public ISegmentGroup lnkSegmentGroup = null;

	public SemanticRepository lnkSemanticRepository = null;

	public ISegment lnkCompartment = null;
	
	public List<ISemanticsAware> getSemanticsAwareAssociations();
	
	public void addSemanticsAwareAssociation(ISemanticsAware obj);
	
	public void removeSemanticsAwareAssociation(ISemanticsAware obj);
}