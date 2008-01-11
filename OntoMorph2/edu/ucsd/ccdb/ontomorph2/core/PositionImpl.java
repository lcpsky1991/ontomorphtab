package edu.ucsd.ccdb.ontomorph2.core;

import com.jme.math.Vector3f;

public class PositionImpl extends Vector3f implements IPosition{
	
	public PositionImpl(float x, float y, float z) {
		super(x,y,z);
	}

	public Vector3f asVector3f() {
		return this;
	}
}
