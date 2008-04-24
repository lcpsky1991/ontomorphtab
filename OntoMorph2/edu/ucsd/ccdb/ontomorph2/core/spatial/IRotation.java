package edu.ucsd.ccdb.ontomorph2.core.spatial;

import com.jme.math.Matrix3f;

import edu.ucsd.ccdb.ontomorph2.core.scene.ISlide;

public interface IRotation {
	
		public edu.ucsd.ccdb.ontomorph2.core.scene.ISlide lnkISlide = null;
	public float getX();
	public float getY();
	public float getZ();
	public Matrix3f asQuaternion();

}
