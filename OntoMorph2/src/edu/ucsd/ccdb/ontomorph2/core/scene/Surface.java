package edu.ucsd.ccdb.ontomorph2.core.scene;

import com.jme.scene.BezierMesh;
import com.jme.scene.BezierPatch;

import edu.ucsd.ccdb.ontomorph2.core.spatial.OMTVector;

/**
 * Defines a Bezier Surface.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class Surface extends BezierMesh{

	public Surface(String s, OMTVector[][] array, int levelOfDetail) {
		super(s, new BezierPatch(array, levelOfDetail));
	}
}