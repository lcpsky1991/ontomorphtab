package edu.ucsd.ccdb.ontomorph2.core.spatial;

import com.jme.math.Quaternion;
import com.jme.math.TransformMatrix;
import com.jme.math.Vector3f;

/**
 * Defines a coordinate system with an origin, rotation, and scale.  A scene object can be associated with 
 * a coordinate system, and this determines how its relative position, rotation, and scale will be
 * transformed into its absolute position, rotation, and scale.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public abstract class CoordinateSystem extends TransformMatrix implements ICoordinateSystem {

	
	public float MAX_X = 528;
	public float MIN_X = 0;
	public float MAX_Y = 320;
	public float MIN_Y = 0;
	public float MAX_Z = 456;
	public float MIN_Z = -456;
	
	private OMTVector scale = null;
	
	/*
	 *  (non-Javadoc)
	 * @see edu.ucsd.ccdb.ontomorph2.core.spatial.ICoordinateSystem#getMinimumXCoordinate()
	 */
	public float getMinimumXCoordinate() {
		return MIN_X;
	}

	public float getMinimumYCoordinate() {
		return MIN_Y;
	}

	public float getMinimumZCoordinate() {
		return MIN_Z;
	}

	public float getMaximumXCoordinate() {
		return MAX_X;
	}

	public float getMaximumYCoordinate() {
		return MAX_Y;
	}

	public float getMaximumZCoordinate() {
		return MAX_Z;
	}
	
	public OMTVector getXVector() {
		float[] r = this.getXDirection();
		return new OMTVector(r[0], r[1], r[2]);
	}

	public OMTVector getYVector() {
		float[] r = this.getYDirection();
		return new OMTVector(r[0], r[1], r[2]);
	}

	public OMTVector getZVector() {
		float[] r = this.getZDirection();
		return new OMTVector(r[0], r[1], r[2]);
	}

	public PositionVector getOriginVector() {
		return new PositionVector(this.getTranslation(null));
	}
	
	public float[] getXDirection() {
		float[] direction = {OMTVector.UNIT_X.x, 
				OMTVector.UNIT_X.y, OMTVector.UNIT_X.z};
		return direction;
	}
	
	public float[] getYDirection() {
		float[] direction = {OMTVector.UNIT_Y.x, 
				OMTVector.UNIT_Y.y, OMTVector.UNIT_Y.z};
		return direction;
	}
	
	public float[] getZDirection() {
		float[] direction = {OMTVector.UNIT_Z.x, 
				OMTVector.UNIT_Z.y, OMTVector.UNIT_Z.z};
		return direction;
	}
	
	public float[] getOrigin() {
		Vector3f v = this.getTranslation(null);
		float[] f = {v.x, v.y, v.z};
		return f;
	}
	
	public TransformMatrix getTransformMatrix() {
		return this;
	}
	
	public Quaternion getRotationFromAbsolute() {
		return this.getRotation((Quaternion)null);
	}
	
	public void setScale(float x, float y, float z) {
		this.scale = new OMTVector(x,y,z);
	}
	
	public Vector3f getScale(Vector3f in) {
		return this.scale;
	}


}
