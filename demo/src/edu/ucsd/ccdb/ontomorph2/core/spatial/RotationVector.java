package edu.ucsd.ccdb.ontomorph2.core.spatial;

import com.jme.math.Matrix3f;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;


/**
 * A 3D vector that defines a rotation.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class RotationVector extends com.jme.math.Quaternion{

	public RotationVector(float offset, Vector3f start) {
		this.fromAngleAxis(offset, start);
		
	}
	
	public RotationVector() {
		
	}
	
	public RotationVector(Quaternion localRotation) {
		super(localRotation);
	}
	
	public RotationVector(float x, float y, float z, float w) {
		super(x,y,z,w);
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
