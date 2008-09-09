package edu.ucsd.ccdb.ontomorph2.core.scene;

import java.awt.Color;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Observable;
import java.util.Set;

import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Box;

import edu.ucsd.ccdb.ontomorph2.core.data.CCDBRepository;
import edu.ucsd.ccdb.ontomorph2.core.data.GlobalSemanticRepository;
import edu.ucsd.ccdb.ontomorph2.core.data.SemanticRepository;
import edu.ucsd.ccdb.ontomorph2.core.data.wsclient.CcdbMicroscopyData;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.CCDBSlide;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Curve3D;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.DataMesh;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.MorphMLNeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.NeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Slide;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Surface;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Tangible;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.URISlide;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Volume;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticClass;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticInstance;
import edu.ucsd.ccdb.ontomorph2.core.spatial.DemoCoordinateSystem;
import edu.ucsd.ccdb.ontomorph2.core.spatial.PositionVector;
import edu.ucsd.ccdb.ontomorph2.core.spatial.RotationVector;
import edu.ucsd.ccdb.ontomorph2.util.OMTVector;

/**
 * A scene constructed for a specific demo of functionality
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class DemoScene extends Scene{

	
	URL cell3URL = null;
	URL cell4URL = null;
	URL cell5URL = null;
	URL cell6URL = null;
	URL cell7URL = null;
	public URL cell11URL = null;	//made public for test-case with context menu to create new cells
	URL mitoObjURL = null;
	URL mito2ObjURL = null;
	URL astroObjURL = null;
	URL plasmaObjURL = null;
	URL hippo1URL = null;
	URI hippo2URL = null;
	URI hippo3aURL = null;
	URI hippo3bURL = null;
	URI hippo3cURL = null;
	
	public DemoScene() {
		manager = TangibleManager.getInstance();
				
		try {
			cell3URL = new File(morphMLDir + "cell1zr.morph.xml").toURI().toURL();
			cell4URL = new File(morphMLDir + "cell2zr.morph.xml").toURI().toURL();
			cell5URL = new File(morphMLDir + "cell6zr.morph.xml").toURI().toURL();
			cell6URL = new File(morphMLDir + "pc1c.morph.xml").toURI().toURL();
			cell7URL = new File(morphMLDir + "pc2a.morph.xml").toURI().toURL();
			cell11URL = new File(morphMLDir + "5199202a.morph.xml").toURI().toURL();
			mitoObjURL = new File(mitoDir + "mito_outer.obj").toURI().toURL();
			
			mito2ObjURL = new File(objDir + "mitochondrion.obj").toURI().toURL();
			astroObjURL = new File(objDir + "astrocyte process.obj").toURI().toURL();
			plasmaObjURL = new File(objDir + "plasma membrane.obj").toURI().toURL();
			
			hippo1URL = new File(imgDir + "hippo_slice1.jpg").toURI().toURL();
			hippo2URL = new File(imgDir + "hippo_slice2.jpg").toURI();
			hippo3aURL = new File(imgDir + "hippo_slice3a.jpg").toURI();
			hippo3bURL = new File(imgDir + "hippo_slice3b.jpg").toURI();
			hippo3cURL = new File(imgDir + "hippo_slice3c.jpg").toURI();
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads objects into the scene.  Currently this is done manually mostly from
	 * files on the client.  This is being transitioned to the loadFromCKB method
	 * where we are loading data from the cellular knowledge base
	 * @see #loadFromCKB()
	 *
	 */
	public void load() {
		
		DataMesh mesh = new DataMesh();

		mesh.setObjMeshURL(mitoObjURL);
	
		mesh.setRelativePosition(new PositionVector(289f, -117f, -179.51f));
		mesh.setRelativeRotation(new RotationVector(FastMath.DEG_TO_RAD*90, OMTVector.UNIT_X));
		mesh.setRelativeScale(0.0002f);
		mesh.addSemanticClass(SemanticRepository.getAvailableInstance().getSemanticClass(SemanticClass.MITOCHONDRION_CLASS));
		mesh.getMainSemanticInstance(); //get a SemanticInstance loaded into the local repository
		
		addSceneObject(mesh);
		
		setCameraPosition(Scene.CAMERA_SUBCELLULAR_POSITION);
		
		changed(CHANGED_LOAD);
	}
}
