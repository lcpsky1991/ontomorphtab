package edu.ucsd.ccdb.ontomorph2.core.spatial;


import org.morphml.metadata.schema.XWBCQuat;
import org.morphml.metadata.schema.impl.XWBCQuatImpl;

import com.jme.math.Matrix3f;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;


/**
 * A 3D Quaternion that defines a rotation.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class RotationQuat extends com.jme.math.Quaternion{

	public RotationQuat(float offset, Vector3f start) {
		this.fromAngleAxis(offset, start);
		
	}
	
	public RotationQuat(Quaternion localRotation) {
		super(localRotation);
	}
	
	public RotationQuat(float x, float y, float z, float w) {
		super(x,y,z,w);
	}

	public RotationQuat(XWBCQuat r) {
		super((float)r.getX(), (float)r.getY(), (float)r.getZ(), (float)r.getW());
	}

	public RotationQuat() {
	}

	public Matrix3f asMatrix3f() {
		// TODO Auto-generated method stub
		return this.toRotationMatrix();
	}

	public XWBCQuat toWBCQuat() {
		XWBCQuat q = new XWBCQuatImpl();
		q.setX(this.x);
		q.setY(this.y);
		q.setZ(this.z);
		q.setW(this.w);
		return q;
	}
	
}
