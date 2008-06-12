package edu.ucsd.ccdb.ontomorph2.core.spatial;

import com.jme.math.Vector3f;
import com.jme.scene.BezierMesh;
import com.jme.scene.BezierPatch;

import edu.ucsd.ccdb.ontomorph2.util.OMTVector;

public class SurfaceImpl extends BezierMesh implements ISurface{

	public SurfaceImpl(String s, OMTVector[][] array, int levelOfDetail) {
		super(s, new BezierPatch(array, levelOfDetail));
	}
}
