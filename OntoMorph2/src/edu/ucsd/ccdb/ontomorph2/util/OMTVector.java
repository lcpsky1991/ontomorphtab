package edu.ucsd.ccdb.ontomorph2.util;

import com.jme.math.Vector3f;

import edu.ucsd.ccdb.ontomorph2.core.spatial.CoordinateSystem;
import edu.ucsd.ccdb.ontomorph2.core.spatial.ICoordinateSystem;

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

}
