package edu.ucsd.ccdb.ontomorph2.core;

import com.jme.math.Vector3f;
import com.jme.scene.BezierMesh;
import com.jme.scene.BezierPatch;

public class SurfaceImpl extends BezierMesh implements ISurface{

	public SurfaceImpl(String s, Vector3f[][] array, int levelOfDetail) {
		super(s, new BezierPatch(array, levelOfDetail));
	}
}
