package edu.ucsd.ccdb.ontomorph2.core.spatial;

import com.jme.math.Matrix3f;
import com.jme.math.Vector3f;


public class RotationImpl extends com.jme.math.Quaternion implements IRotation{

	public RotationImpl(float offset, Vector3f start) {
		this.fromAngleAxis(offset, start);
		
	}
	
	public Matrix3f asQuaternion() {
		// TODO Auto-generated method stub
		return this.toRotationMatrix();
	}

	public float getX() {
		// TODO Auto-generated method stub
		return 0;
	}

	public float getY() {
		// TODO Auto-generated method stub
		return 0;
	}

	public float getZ() {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
