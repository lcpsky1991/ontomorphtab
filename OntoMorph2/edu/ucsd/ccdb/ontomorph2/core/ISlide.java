package edu.ucsd.ccdb.ontomorph2.core;

import java.net.URL;

public interface ISlide {

		public URL getImageURL();
		public IPosition getPosition();
		public IRotation getRotation();
}
