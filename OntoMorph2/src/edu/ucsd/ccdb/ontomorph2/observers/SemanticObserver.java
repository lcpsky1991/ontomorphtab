package edu.ucsd.ccdb.ontomorph2.observers;

import java.util.Observable;
import java.util.Observer;

import edu.ucsd.ccdb.ontomorph2.core.atlas.BrainRegion;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.NeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticInstance;

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

	public void update(Observable o, Object arg) {
		if (o instanceof NeuronMorphology) {
			NeuronMorphology nm = (NeuronMorphology)o;
			BrainRegion br = nm.getEnclosingBrainRegion();
			SemanticInstance brainRegionInstance = br.getSemanticInstance();
			//does this brain region instance have a property saying
			//that it has_part the instance from the neuron morphology??
			//if so, do nothing
			//if not, add it!
		}
	}

}
