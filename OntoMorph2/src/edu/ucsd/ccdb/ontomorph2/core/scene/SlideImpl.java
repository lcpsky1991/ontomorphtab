package edu.ucsd.ccdb.ontomorph2.core.scene;

import java.net.URL;

import edu.ucsd.ccdb.ontomorph2.core.spatial.CoordinateSystem;
import edu.ucsd.ccdb.ontomorph2.core.spatial.DemoCoordinateSystem;
import edu.ucsd.ccdb.ontomorph2.core.spatial.ICoordinateSystem;
import edu.ucsd.ccdb.ontomorph2.core.spatial.PositionVector;
import edu.ucsd.ccdb.ontomorph2.core.spatial.RotationVector;
import edu.ucsd.ccdb.ontomorph2.util.OMTVector;

public class SlideImpl extends SceneObjectImpl implements ISlide {
	
	URL _imageURL = null;
	float ratio = 1f;
	
	public SlideImpl(URL imageURL, PositionVector position, RotationVector rotation) {
		_imageURL = imageURL;
		setRelativePosition(position);
		setRelativeRotation(rotation);
	}
	
	
	public SlideImpl(URL filePath, PositionVector position, RotationVector rotation, OMTVector scale, float ratio) {
		this(filePath, position, rotation);
		setRelativeScale(scale);
		this.ratio = ratio;
	}
	
	public SlideImpl(URL filePath, PositionVector position, RotationVector rotation, float scale, float ratio) {
		this(filePath, position, rotation);
		setRelativeScale(scale);
		this.ratio = ratio;
	}

	public SlideImpl(URL hippo3cURL, PositionVector impl, RotationVector rotation, OMTVector scale, float ratio, CoordinateSystem d) {
		this(hippo3cURL, impl, rotation, scale, ratio);
		this.setCoordinateSystem(d);
	}
	
	public SlideImpl(URL hippo3cURL, PositionVector impl, RotationVector rotation, float scale, float ratio, CoordinateSystem d) {
		this(hippo3cURL, impl, rotation, scale, ratio);
		this.setCoordinateSystem(d);
	}
	
	
	public SlideImpl(URL hippo3cURL, PositionVector impl, OMTVector scale, float ratio, CoordinateSystem d) {
		this(hippo3cURL, impl, new RotationVector(), scale, ratio);
		this.setCoordinateSystem(d);
	}
	public SlideImpl(URL hippo3cURL, PositionVector impl, float scale, float ratio, CoordinateSystem d) {
		this(hippo3cURL, impl, new RotationVector(), scale, ratio);
		this.setCoordinateSystem(d);
	}
	
	


	public URL getImageURL() {
		return _imageURL;
	}


	public float getRatio() {
		return ratio;
	}

}
