package edu.ucsd.ccdb.ontomorph2.core;

import com.jme.math.Matrix3f;
import com.jme.math.Vector3f;

public class RotationImpl extends Vector3f implements IRotation{

	public RotationImpl(float x, float y, float z) {
		super(x,y,z);
	}

	public Vector3f asVector3f() {
		return this;
	}

	public Matrix3f asQuaternion() {
		// TODO Auto-generated method stub
		return null;
	}
}
