package edu.ucsd.ccdb.ontomorph2.core;

import java.net.URL;

public class SlideImpl implements ISlide{
	
	URL _imageURL = null;
	IPosition _position = null;
	IRotation _rotation = null;
	
	public SlideImpl(URL imageURL, IPosition position, IRotation rotation) {
		_imageURL = imageURL;
		_position = position;
		_rotation = rotation;
	}

	public URL getImageURL() {
		return _imageURL;
	}

	public IPosition getPosition() {
		return _position;
	}

	public IRotation getRotation() {
		return _rotation;
	}

}
