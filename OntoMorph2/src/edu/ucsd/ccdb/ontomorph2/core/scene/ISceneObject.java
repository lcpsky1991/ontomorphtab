package edu.ucsd.ccdb.ontomorph2.core.scene;

import edu.ucsd.ccdb.ontomorph2.core.spatial.IPosition;
import edu.ucsd.ccdb.ontomorph2.core.spatial.IRotation;

public interface ISceneObject extends ISelectable{

	/**
	 * get the IRotation that defines this INeuronMorphology's rotation
	 * @return - the IRotation
	 */
	public IRotation getRotation();

	/**
	 * get the IPosition that defines this INeuronMorphology's position 
	 * @return
	 */
	public IPosition getPosition();

	/** 
	 * set the IPosition for this INeuronMorphology
	 * @param pos - desired position
	 */
	public void setPosition(IPosition pos);

	/**
	 * set the IRotation for this INeuronMorphology
	 * @param rot - desiredRotation
	 */
	public void setRotation(IRotation rot);

	/**
	 * set the scale for this INeuronMorphology
	 * @param f - scale
	 */
	public void setScale(float f);

	public float getScale();

}
