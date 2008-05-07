package edu.ucsd.ccdb.ontomorph2.core.semantic;

import java.util.List;

import edu.stanford.smi.protege.model.Cls;
import edu.ucsd.ccdb.ontomorph2.core.misc.IPopulation;
import edu.ucsd.ccdb.ontomorph2.core.misc.IVariabilityTransformFunction;
import edu.ucsd.ccdb.ontomorph2.core.scene.ISegment;
import edu.ucsd.ccdb.ontomorph2.core.scene.ISegmentGroup;
import edu.ucsd.ccdb.ontomorph2.core.scene.ISelectable;


//import edu.stanford.smi.protege.model.Cls;

/**
 * @$comment Contains the semantic description of a biological object (OWL)
 */

public interface ISemanticThing extends ISelectable{
	
	public List<ISemanticsAware> getSemanticsAwareAssociations();
	
	public void addSemanticsAwareAssociation(ISemanticsAware obj);
	
	public void removeSemanticsAwareAssociation(ISemanticsAware obj);
	
	public String getLabel();
	
	public Cls getCls();
}