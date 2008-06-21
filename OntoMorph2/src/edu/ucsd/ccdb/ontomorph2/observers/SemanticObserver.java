package edu.ucsd.ccdb.ontomorph2.observers;

import java.util.Observable;
import java.util.Observer;

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
		// TODO Auto-generated method stub

	}

}
