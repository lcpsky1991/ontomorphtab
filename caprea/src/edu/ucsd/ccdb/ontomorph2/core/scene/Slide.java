package edu.ucsd.ccdb.ontomorph2.core.scene;

import java.net.URL;
import java.util.List;

import edu.ucsd.ccdb.ontomorph2.core.data.reader.CCDBDataCatagory;
import edu.ucsd.ccdb.ontomorph2.core.data.reader.CCDBFile;
import edu.ucsd.ccdb.ontomorph2.core.data.reader.CCDBFileType;
import edu.ucsd.ccdb.ontomorph2.core.data.reader.CCDBModelReader;
import edu.ucsd.ccdb.ontomorph2.core.data.wsclient.CcdbMicroscopyData;
import edu.ucsd.ccdb.ontomorph2.core.spatial.CoordinateSystem;
import edu.ucsd.ccdb.ontomorph2.core.spatial.OMTVector;
import edu.ucsd.ccdb.ontomorph2.core.spatial.PositionVector;
import edu.ucsd.ccdb.ontomorph2.core.spatial.RotationVector;
import edu.ucsd.ccdb.ontomorph2.util.OMTException;

/**
 * A Panel in 3D space that displays an image of a brain slice.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 */
public class Slide extends SceneObject {
	
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
	
	public Slide(CcdbMicroscopyData image, PositionVector pos, RotationVector rot, float scale, float ratio) {
		URL imageURL = null;
		CCDBModelReader reader;
		try {
			reader = new CCDBModelReader(image);
			List file = reader.getFiles();
			
			for(int i=0;i<file.size();i++)
			{
				CCDBFile f = (CCDBFile)file.get(i);
				System.out.println(f.getCCDBCatagory());
				if (f.getCCDBFileType() == CCDBFileType.IMAGE_JPEG) {
						imageURL = f.getURL();
				} 
			}
			
			//if we don't find something within the CCDBFile, throw an exception since this Slide will 
			//be in a bad state.
			if (imageURL == null) {
				throw new OMTException("CcdbMicroscopyData did not contain an image type that was supported!", null);
			}
		} catch (Exception e) {
			throw new OMTException("Unable to load CCDB data!", e);
		}
		_imageURL = imageURL;
		setRelativePosition(pos);
		setRelativeRotation(rot);
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
