package edu.ucsd.ccdb.ontomorph2.core;

import com.jme.math.Matrix3f;
import com.jme.math.Vector3f;

public interface IRotation {
	
		public edu.ucsd.ccdb.ontomorph2.core.ISlide lnkISlide = null;
	public float getX();
	public float getY();
	public float getZ();
	public Vector3f asVector3f();
	public Matrix3f asQuaternion();

}
