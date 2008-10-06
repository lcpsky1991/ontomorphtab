package edu.ucsd.ccdb.ontomorph2.util;

import org.morphml.metadata.schema.Point3D;
import org.morphml.metadata.schema.impl.Point3DImpl;

import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;


/**
 * Wraps a 3D vector.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class OMTVector extends Vector3f {

	public OMTVector(int i, int j, int k) {
		super(i,j,k);
	}

	public OMTVector(float x, float y, float f) {
		super(x,y,f);
	}

	public OMTVector(double i, double j, double k)
	{
		super((float)i,(float)j,(float)k);
	}
	
	public OMTVector(Vector3f v) {
		this(v.x, v.y, v.z);
	}

	public OMTVector(Point3D p) {
		super((float)p.getX(), (float)p.getY(), (float)p.getZ());
	}
	
	public Point3D toPoint3D() {
		Point3D p = new Point3DImpl();
		p.setX(this.x);
		p.setY(this.y);
		p.setZ(this.z);
		return p;
	}

	/**
	  * Rotates a vector around a specified quaternion
	  * @param v {@link Vector3f} to rotate
	  * @param q {@link Quaternion} about which to rotate v
	  * @return the {@link Vector3f} (v) as rotated around q
	  */
	 public static Vector3f rotateVector(Vector3f v, Quaternion q)
	 {
		 Vector3f r = new Vector3f(v);
		 r = q.mult(r,r);
		 return r;
	 }

	/**
	 * Rotates a vector by some angle around some axis and returns the new vector
	 * @param v the vector to be rotated
	 * @param angle how many degrees to rotate the vector
	 * @param aboutAxis the UNIT vector representing which axis to rotate this vector on
	 * @return a new vector of same length as v, which has been rotated around Axis
	 */
	 public static Vector3f rotateVector(Vector3f v, float angle, Vector3f aboutAxis)
	 {
		Vector3f r = new Vector3f(v);
		Quaternion quat = new Quaternion();
	    quat.fromAngleAxis(FastMath.PI * (angle/180), aboutAxis);
	    quat.mult(r, r);	
		return r;
	 }
	
}
