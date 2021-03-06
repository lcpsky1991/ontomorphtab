package edu.ucsd.ccdb.ontomorph2.core.spatial;

import org.morphml.metadata.schema.Point3D;

import com.jme.math.Vector3f;

import edu.ucsd.ccdb.ontomorph2.util.OMTVector;


/**
 * A 3D vector that defines the position of something.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class PositionVector extends OMTVector {
	
	public PositionVector(float x, float y, float z) {
		super(x,y,z);
	}
	
	public PositionVector(Vector3f v) {
		super(v.x, v.y, v.z);
	}
	
	public PositionVector(Point3D p) {
		super(p);
	}

	public PositionVector() {
		super(0f, 0f, 0f);
	}

	public Vector3f asVector3f() {
		return this;
	}
	
}
