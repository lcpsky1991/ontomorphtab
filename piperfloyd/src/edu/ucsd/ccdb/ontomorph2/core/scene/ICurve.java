package edu.ucsd.ccdb.ontomorph2.core.scene;

import java.awt.Color;

import com.jme.curve.Curve;

import edu.ucsd.ccdb.ontomorph2.core.spatial.PositionVector;

/** 
 * Defines a Bezier curve in the framework.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public interface ICurve extends ISceneObject{

	public void setColor(Color color);

	public Curve asBezierCurve();
	
	public PositionVector getPoint(float time);
}
