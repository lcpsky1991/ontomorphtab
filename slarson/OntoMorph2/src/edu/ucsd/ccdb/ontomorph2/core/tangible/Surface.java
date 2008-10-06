package edu.ucsd.ccdb.ontomorph2.core.tangible;

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
	
	public Surface(String name, OMTVector[][] array, int levelOfDetail) {
		super(name);
		this.bm = new BezierMesh(name, new BezierPatch(array, levelOfDetail));
	}
	
	public BezierMesh asBezierMesh() {
		return this.bm;
	}
	
}
