package edu.ucsd.ccdb.ontomorph2.core.semantic;

import java.util.List;

import edu.stanford.smi.protege.model.Cls;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.ISelectable;


/**
 * Contains the semantic description of a biological object (OWL).
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 */
public interface ISemanticThing extends ISelectable{
	
	public List<ISemanticsAware> getSemanticsAwareAssociations();
	
	public void addSemanticsAwareAssociation(ISemanticsAware obj);
	
	public void removeSemanticsAwareAssociation(ISemanticsAware obj);
	
	/**
	 * Returns a list of spatial semantic properties that hold between this
	 * Semantic thing and the other semantic thing.
	 * 
	 * @return
	 */
	public List<SemanticProperty> findPropertiesThatHold(ISemanticThing other);
	
}
