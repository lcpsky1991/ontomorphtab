package edu.ucsd.ccdb.ontomorph2.core.tangible;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;

import javax.imageio.ImageIO;

import com.jme.image.Image;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;

import edu.ucsd.ccdb.ontomorph2.core.data.reader.CCDBDataCatagory;
import edu.ucsd.ccdb.ontomorph2.core.data.reader.CCDBFile;
import edu.ucsd.ccdb.ontomorph2.core.data.reader.CCDBFileType;
import edu.ucsd.ccdb.ontomorph2.core.data.reader.CCDBModelReader;
import edu.ucsd.ccdb.ontomorph2.core.data.wsclient.CcdbMicroscopyData;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticClass;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticRepository;
import edu.ucsd.ccdb.ontomorph2.core.spatial.CoordinateSystem;
import edu.ucsd.ccdb.ontomorph2.core.spatial.PositionVector;
import edu.ucsd.ccdb.ontomorph2.core.spatial.RotationVector;
import edu.ucsd.ccdb.ontomorph2.util.OMTException;
import edu.ucsd.ccdb.ontomorph2.util.OMTUtility;
import edu.ucsd.ccdb.ontomorph2.util.OMTVector;



/**
 * A Panel in 3D space that displays an image of a brain slice.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 */
public abstract class Slide extends Tangible {
	
	float ratio = 1f;

	public Slide() {
		this.addSemanticClass(SemanticRepository.getAvailableInstance().getSemanticClass(
				SemanticClass.IMAGE_CLASS));
		this.getMainSemanticInstance();
	}

	public float getRatio() {
		return ratio;
	}
	
	public void setRatio(float ratio) {
		this.ratio = ratio;
	}

}
