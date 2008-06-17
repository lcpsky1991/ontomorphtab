package edu.ucsd.ccdb.ontomorph2.core.scene;

import com.jme.math.Vector3f;
import com.jme.scene.BezierMesh;
import com.jme.scene.BezierPatch;

import edu.ucsd.ccdb.ontomorph2.core.spatial.OMTVector;

/**
 * Implementation of ISurface.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 * @see ISurface
 *
 */
public class SurfaceImpl extends BezierMesh implements ISurface{

	public SurfaceImpl(String s, OMTVector[][] array, int levelOfDetail) {
		super(s, new BezierPatch(array, levelOfDetail));
	}
}
