package edu.ucsd.ccdb.ontomorph2.core.spatial;

import java.awt.Color;

import com.jme.curve.Curve;

import edu.ucsd.ccdb.ontomorph2.core.scene.ISceneObject;

public interface ICurve extends ISceneObject{

	public void setColor(Color color);

	public Curve asBezierCurve();
	
	public PositionVector getPoint(float time);
}
