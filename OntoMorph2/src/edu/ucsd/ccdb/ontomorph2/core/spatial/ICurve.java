package edu.ucsd.ccdb.ontomorph2.core.spatial;

import java.awt.Color;

import com.jme.curve.Curve;

public interface ICurve {

	public void setColor(Color color);

	public Curve asBezierCurve();
}
