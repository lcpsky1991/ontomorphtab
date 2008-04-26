package edu.ucsd.ccdb.ontomorph2.core.scene;

import java.net.URL;

import edu.ucsd.ccdb.ontomorph2.core.spatial.IPosition;
import edu.ucsd.ccdb.ontomorph2.core.spatial.IRotation;


/**
 * @$comment Panel in 3D space that displays an image of a brain slice
 */

public interface ISlide extends ISceneObject{

		public URL getImageURL();

		public float getRatio();
}
