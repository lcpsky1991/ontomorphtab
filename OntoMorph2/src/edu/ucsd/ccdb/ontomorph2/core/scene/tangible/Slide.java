package edu.ucsd.ccdb.ontomorph2.core.scene.tangible;


/**
 * A Panel in 3D space that displays an image of a brain slice.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 */
public abstract class Slide extends Tangible {
	
	float ratio = 1f;


	public float getRatio() {
		return ratio;
	}
	
	public void setRatio(float ratio) {
		this.ratio = ratio;
	}

}
