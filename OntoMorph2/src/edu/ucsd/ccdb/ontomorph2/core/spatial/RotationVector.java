package edu.ucsd.ccdb.ontomorph2.core.spatial;

import com.jme.math.Matrix3f;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;


public class RotationVector extends com.jme.math.Quaternion{

	public RotationVector(float offset, Vector3f start) {
		this.fromAngleAxis(offset, start);
		
	}
	
	public RotationVector() {
		
	}
	
	public RotationVector(Quaternion localRotation) {
		super(localRotation);
	}

	public Matrix3f asMatrix3f() {
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
