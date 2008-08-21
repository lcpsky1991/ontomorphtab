package edu.ucsd.ccdb.ontomorph2.core.scene.tangible;

import com.jme.scene.BezierMesh;
import com.jme.scene.BezierPatch;

import edu.ucsd.ccdb.ontomorph2.util.OMTVector;

/**
 * Defines a Bezier Surface.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class Surface extends Tangible{
	BezierMesh bm = null;
	
	public Surface(String s, OMTVector[][] array, int levelOfDetail) {
		this.bm = new BezierMesh(s, new BezierPatch(array, levelOfDetail));
	}
	
	public BezierMesh asBezierMesh() {
		return this.bm;
	}
}
