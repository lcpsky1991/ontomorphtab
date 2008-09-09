package edu.ucsd.ccdb.ontomorph2.util;

import org.morphml.metadata.schema.Point;
import org.morphml.metadata.schema.impl.PointImpl;

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

	public OMTVector(Vector3f v) {
		this(v.x, v.y, v.z);
	}

	public Point asPoint() {
		Point p = new PointImpl();
		p.setX(this.x);
		p.setY(this.y);
		p.setZ(this.z);
		return p;
	}
	
}
