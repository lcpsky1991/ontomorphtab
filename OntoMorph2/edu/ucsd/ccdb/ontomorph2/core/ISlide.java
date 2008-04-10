package edu.ucsd.ccdb.ontomorph2.core;

import java.net.URL;


/**
 * @$comment Panel in 3D space that displays an image of a brain slice
 */

public interface ISlide {

        public IPosition lnkIPosition = null;
		public URL getImageURL();
		public IPosition getPosition();
		public IRotation getRotation();
}
