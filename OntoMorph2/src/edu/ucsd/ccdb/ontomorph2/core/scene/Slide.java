package edu.ucsd.ccdb.ontomorph2.core.scene;

import java.net.URL;

import edu.ucsd.ccdb.ontomorph2.core.spatial.CoordinateSystem;
import edu.ucsd.ccdb.ontomorph2.core.spatial.OMTVector;
import edu.ucsd.ccdb.ontomorph2.core.spatial.PositionVector;
import edu.ucsd.ccdb.ontomorph2.core.spatial.RotationVector;

/**
 * A Panel in 3D space that displays an image of a brain slice.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 */
public class Slide extends SceneObjectImpl {
	
	URL _imageURL = null;
	float ratio = 1f;
	
	public Slide(URL imageURL, PositionVector position, RotationVector rotation) {
		_imageURL = imageURL;
		setRelativePosition(position);
		setRelativeRotation(rotation);
	}
	
	
	public Slide(URL filePath, PositionVector position, RotationVector rotation, OMTVector scale, float ratio) {
		this(filePath, position, rotation);
		setRelativeScale(scale);
		this.ratio = ratio;
	}
	
	public Slide(URL filePath, PositionVector position, RotationVector rotation, float scale, float ratio) {
		this(filePath, position, rotation);
		setRelativeScale(scale);
		this.ratio = ratio;
	}

	public Slide(URL hippo3cURL, PositionVector impl, RotationVector rotation, OMTVector scale, float ratio, CoordinateSystem d) {
		this(hippo3cURL, impl, rotation, scale, ratio);
		this.setCoordinateSystem(d);
	}
	
	public Slide(URL hippo3cURL, PositionVector impl, RotationVector rotation, float scale, float ratio, CoordinateSystem d) {
		this(hippo3cURL, impl, rotation, scale, ratio);
		this.setCoordinateSystem(d);
	}
	
	
	public Slide(URL hippo3cURL, PositionVector impl, OMTVector scale, float ratio, CoordinateSystem d) {
		this(hippo3cURL, impl, new RotationVector(), scale, ratio);
		this.setCoordinateSystem(d);
	}
	public Slide(URL hippo3cURL, PositionVector impl, float scale, float ratio, CoordinateSystem d) {
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
