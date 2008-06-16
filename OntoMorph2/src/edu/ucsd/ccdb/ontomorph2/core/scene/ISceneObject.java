package edu.ucsd.ccdb.ontomorph2.core.scene;

import edu.ucsd.ccdb.ontomorph2.core.spatial.CoordinateSystem;
import edu.ucsd.ccdb.ontomorph2.core.spatial.ICoordinateSystem;
import edu.ucsd.ccdb.ontomorph2.core.spatial.OMTVector;
import edu.ucsd.ccdb.ontomorph2.core.spatial.PositionVector;
import edu.ucsd.ccdb.ontomorph2.core.spatial.RotationVector;

/**
 * Defines an object that can be viewed in the scene.  This object can be made relative
 * to an arbitrary coordinate system.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 * @see ICoordinateSystem
 *
 */
public interface ISceneObject extends ISelectable{

	/**
	 * get the RotationVector that defines this INeuronMorphology's rotation.
	 * This is relative to the coordinate system that this ISceneObject has
	 * associated with it.  By default, the coordinate system is the absolute world
	 * coordinate system.
	 * @return - the RotationVector
	 */
	public RotationVector getRelativeRotation();

	/**
	 * get the PositionVector that defines this INeuronMorphology's position
	 * This is relative to the coordinate system that this ISceneObject has
	 * associated with it.  By default, the coordinate system is the absolute world
	 * coordinate system. 
	 * @return
	 */
	public PositionVector getRelativePosition();

	/** 
	 * set the PositionVector for this INeuronMorphology
	 * This is relative to the coordinate system that this ISceneObject has
	 * associated with it.  By default, the coordinate system is the absolute world
	 * coordinate system.
	 * @param pos - desired position
	 */
	public void setRelativePosition(PositionVector pos);

	/**
	 * set the RotationVector for this INeuronMorphology
	 * This is relative to the coordinate system that this ISceneObject has
	 * associated with it.  By default, the coordinate system is the absolute world
	 * coordinate system.
	 * @param rot - desiredRotation
	 */
	public void setRelativeRotation(RotationVector rot);

	/**
	 * set the scale for this INeuronMorphology
	 * @param v - scale
	 */
	public void setRelativeScale(OMTVector v);
	
	public void setRelativeScale(float f);

	public OMTVector getRelativeScale();
	
	
	/**
	 * get the RotationVector that defines this INeuronMorphology's rotation.
	 * This is always in the absolute world coordinate system.
	 * @return - the RotationVector
	 */
	public RotationVector getAbsoluteRotation();

	/**
	 * get the PositionVector that defines this INeuronMorphology's position
	 * This is always in the absolute world coordinate system.
	 * @return
	 */
	public PositionVector getAbsolutePosition();

	public OMTVector getAbsoluteScale();
	
	public void setCoordinateSystem(CoordinateSystem sys);
	
	public CoordinateSystem getCoordinateSystem();

}
