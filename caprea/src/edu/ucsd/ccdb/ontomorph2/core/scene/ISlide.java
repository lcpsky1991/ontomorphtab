package edu.ucsd.ccdb.ontomorph2.core.scene;

import java.net.URL;


/**
 * A Panel in 3D space that displays an image of a brain slice.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 */
public interface ISlide extends ISceneObject{

		public URL getImageURL();

		public float getRatio();
}
