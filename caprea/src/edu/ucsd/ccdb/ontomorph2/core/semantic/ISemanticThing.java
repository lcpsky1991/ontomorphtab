package edu.ucsd.ccdb.ontomorph2.core.semantic;

import java.util.List;

import edu.stanford.smi.protege.model.Cls;
import edu.ucsd.ccdb.ontomorph2.core.scene.ISelectable;


/**
 * Contains the semantic description of a biological object (OWL).
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 */
public interface ISemanticThing extends ISelectable{
	
	public List<ISemanticsAware> getSemanticsAwareAssociations();
	
	public void addSemanticsAwareAssociation(ISemanticsAware obj);
	
	public void removeSemanticsAwareAssociation(ISemanticsAware obj);
	
	public String getLabel();
	
	public Cls getCls();
}
