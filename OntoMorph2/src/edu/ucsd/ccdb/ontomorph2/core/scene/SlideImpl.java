package edu.ucsd.ccdb.ontomorph2.core.scene;

import java.net.URL;

import edu.ucsd.ccdb.ontomorph2.core.spatial.IPosition;
import edu.ucsd.ccdb.ontomorph2.core.spatial.IRotation;

public class SlideImpl extends SceneObjectImpl implements ISlide {
	
	URL _imageURL = null;
	float ratio = 1f;
	
	public SlideImpl(URL imageURL, IPosition position, IRotation rotation) {
		_imageURL = imageURL;
		setPosition(position);
		setRotation(rotation);
	}
	
	
	public SlideImpl(URL filePath, IPosition position, IRotation rotation, float scale, float ratio) {
		_imageURL = filePath;
		setPosition(position);
		setRotation(rotation);
		setScale(scale);
		this.ratio = ratio;
	}

	public URL getImageURL() {
		return _imageURL;
	}


	public float getRatio() {
		return ratio;
	}


	public void select() {
		// TODO Auto-generated method stub
		
	}


	public void unselect() {
		// TODO Auto-generated method stub
		
	}


	public boolean isSelected() {
		// TODO Auto-generated method stub
		return false;
	}

}
