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
		//temporary hack o load a mockup
		DemoCoordinateSystem d = new DemoCoordinateSystem();

		/*
		CcdbMicroscopyData hippoImage = CCDBRepository.getInstance().getCCDBData(35);
		
		Slide a = new CCDBSlide(hippoImage, 0.87f);
		a.setRelativePosition(new PositionVector(25,-32,17f));
		a.setCoordinateSystem(d);
		//a.setRelativeRotation(new RotationVector(d.getRotationFromAbsolute()));
		a.setRelativeScale(10);
		addSceneObject(a);
		
        Slide b = new URISlide(hippo2URL, 1.34f);
        b.setRelativePosition(new PositionVector(-14,0, 18f));
        b.setCoordinateSystem(d);
        //b.setRelativeRotation(new RotationVector(d.getRotationFromAbsolute()));
        b.setRelativeScale(3.2f);
		addSceneObject(b);
		
		Slide c = new URISlide(hippo3aURL, 1.33f);
		c.setRelativePosition(new PositionVector(-34,-5,19f));
		c.setCoordinateSystem(d);
		//c.setRelativeRotation(new RotationVector(d.getRotationFromAbsolute()));
		c.setRelativeScale(0.75f);
		addSceneObject(c);
		
		Slide ds = new URISlide(hippo3bURL, 1.31f);
		ds.setRelativePosition(new PositionVector(-15f,-1.5f,19.1f));
		ds.setCoordinateSystem(d);
		//ds.setRelativeRotation(new RotationVector(d.getRotationFromAbsolute()));
		ds.setRelativeScale(0.75f);
		addSceneObject(ds);
		
		Slide e = new URISlide(hippo3cURL, 1.33f);
		e.setRelativePosition(new PositionVector(4,-1f,19.2f));
		e.setCoordinateSystem(d);
		//e.setRelativeRotation(new RotationVector(d.getRotationFromAbsolute()));
		e.setRelativeScale(0.75f);
		addSceneObject(e);
	
		*/
		
		/*
		NeuronMorphology cell3 = new MorphMLNeuronMorphology("cell1zr", 
				new PositionVector(289f, -118f, -180f), null, 
				NeuronMorphology.RENDER_AS_LOD_2);
		RotationVector v = new RotationVector(new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD*-90, Vector3f.UNIT_Y));
		cell3.setRelativeRotation(v);
		//cell3.setCoordinateSystem(d);
		cell3.setRelativeScale(0.01f);
		//semantic thing for hippocampal CA3 neuron
		cell3.addSemanticThing(GlobalSemanticRepository.getInstance().getSemanticClass(SemanticClass.CA3_PYRAMIDAL_CELL_CLASS));
		addSceneObject(cell3);
		*/
		
		
		setCameraPosition(Scene.CAMERA_CELLS_POSITION);
		
		changed(CHANGED_LOAD);
		changed(CHANGED_LOAD);
	}

	public void save() {
	}
	

	
	public void loadMeshes() {

		DemoCoordinateSystem d = new DemoCoordinateSystem();
		
		
		DataMesh mesh = new DataMesh();
		//mesh.loadMaxFile("etc/mito/mito_outer.3ds");
		mesh.setObjMeshURL(mitoObjURL);
		//mesh.setRelativePosition(new PositionVector(0.49f, -3.5f, 20.01f));
		mesh.setRelativePosition(new PositionVector(0.49f, -3.3f, 20.01f));
		mesh.setRelativeRotation(new RotationVector(FastMath.DEG_TO_RAD*90, OMTVector.UNIT_X));
		mesh.setRelativeScale(0.0002f);
		mesh.setCoordinateSystem(d);
		
		addSceneObject(mesh);
		
		DataMesh mito2 = new DataMesh();
		mito2.setObjMeshURL(mito2ObjURL);
		mito2.setRelativePosition(new PositionVector(0.49f, -3.3f, 20.03f));
		mito2.setRelativeRotation(new RotationVector(FastMath.DEG_TO_RAD*90, OMTVector.UNIT_X));
		mito2.setRelativeScale(0.0002f);
		mito2.setCoordinateSystem(d);
	
		addSceneObject(mito2);
		/*
		DataMesh astro = new DataMesh();
		astro.setObjMeshURL(astroObjURL);
		astro.setRelativePosition(new PositionVector(0.49f, -3.3f, 20.09f));
		astro.setRelativeRotation(new RotationVector(FastMath.DEG_TO_RAD*90, OMTVector.UNIT_X));
		astro.setRelativeScale(0.0002f);
		astro.setCoordinateSystem(d);
	
		addSceneObject(astro);
		*/

		/*
		DataMesh plasma = new DataMesh();
		plasma.setObjMeshURL(plasmaObjURL);
		plasma.setRelativePosition(new PositionVector(0.49f, -3.3f, 21.05f));
		plasma.setRelativeRotation(new RotationVector(FastMath.DEG_TO_RAD*90, OMTVector.UNIT_X));
		plasma.setRelativeScale(0.0002f);
		plasma.setCoordinateSystem(d);
	
		addSceneObject(plasma);
		*/
		
	}
	

	
}
