package edu.ucsd.ccdb.ontomorph2.observers;

import java.util.Observable;
import java.util.Observer;

import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticInstance;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticProperty;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticRepository;
import edu.ucsd.ccdb.ontomorph2.core.tangible.ContainerTangible;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Tangible;
import edu.ucsd.ccdb.ontomorph2.util.Log;
import edu.ucsd.ccdb.ontomorph2.util.OMTException;

/**
 * Updates the semantic repository when changes occur to semantic things.  For example,
 * when a cell is moved out of the bounds of a brain region into another one, the
 * SemanticObserver updates the relationship between the SemanticInstance of that cell
 * and the SemanticInstance of that brain region.
 * Can create, modify, and delete instances or relationships.
 * Can update class level information.
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class SemanticObserver implements Observer {

	static SemanticObserver instance = null;
	
	public static SemanticObserver getInstance() {
		if (instance == null) {
			instance = new SemanticObserver();
		}
		return instance;
	}
	
	public void update(Observable o, Object arg) 
	{
		try
		{
			if (o instanceof Tangible) {
				Tangible t = (Tangible)o;
				if (Tangible.CHANGED_CONTAINS.equals(arg)) {
					SemanticRepository repo = SemanticRepository.getAvailableInstance();
					SemanticProperty containsProp = repo.getSemanticProperty(SemanticProperty.CONTAINS);
					
					//in order to handle the case where we have removed a containment relationship
					//between tangibles, start out by removing any existing contains property value
					SemanticInstance containedInstance = t.getSemanticInstance().getPropertyValue(containsProp);
					if (containedInstance != null) {
						t.getSemanticInstance().removePropertyValue(containsProp, containedInstance);
					}
					
					//look up the container list from before the last update.
					//start afresh with these guys insofar as instances are concerned
					for (Tangible previousContainer : t.getPreviousContainerTangibles()) {
						previousContainer.getSemanticInstance().removePropertyValue(containsProp, t.getSemanticInstance());
	
					}
					

					for (Tangible containers : t.getContainerTangibles()) {
						containers.getSemanticInstance().setPropertyValue(containsProp, 
								t.getSemanticInstance());
					}
					
					if (t instanceof ContainerTangible) {
						//for those tangibles that are contained in this tangible, make a 
						//containment relationship between this instance and that one
						for (Tangible contained : ((ContainerTangible)t).getContainedTangibles()) {
							t.getSemanticInstance().setPropertyValue(containsProp, 
									contained.getSemanticInstance());
						}
					}
					
					Log.warn("Containment info has changed");
	
				}
			}
		}
		catch (Exception e)
		{
			//throw new OMTException("Could not update() in SemanticObserver!", e);
			Log.warn("ERROR: Could not update() in SemanticObserver");
		}
	}

}
