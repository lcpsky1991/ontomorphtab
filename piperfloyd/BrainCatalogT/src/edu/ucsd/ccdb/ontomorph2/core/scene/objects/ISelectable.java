package edu.ucsd.ccdb.ontomorph2.core.scene.objects;

/**
 * A general interface for any object that can be selected by a user.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public interface ISelectable {

	public void select();
	
	public void unselect();
	
	public boolean isSelected();
	
}
