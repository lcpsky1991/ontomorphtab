package edu.ucsd.ccdb.ontomorph2.core.scene;

import java.net.URL;

import edu.ucsd.ccdb.ontomorph2.core.spatial.IPosition;
import edu.ucsd.ccdb.ontomorph2.core.spatial.IRotation;

public class SlideImpl extends SceneObjectImpl implements ISlide {
	
	URL _imageURL = null;
	
	public SlideImpl(URL imageURL, IPosition position, IRotation rotation) {
		_imageURL = imageURL;
		setPosition(position);
		setRotation(rotation);
	}

	public URL getImageURL() {
		return _imageURL;
	}

}
