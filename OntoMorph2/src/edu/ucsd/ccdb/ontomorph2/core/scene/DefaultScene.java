package edu.ucsd.ccdb.ontomorph2.core.scene;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Observable;
import java.util.Set;

import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.scene.shape.Box;

import edu.ucsd.ccdb.ontomorph2.core.data.CCDBRepository;
import edu.ucsd.ccdb.ontomorph2.core.data.ReferenceAtlas;
import edu.ucsd.ccdb.ontomorph2.core.data.wsclient.CcdbMicroscopyData;
import edu.ucsd.ccdb.ontomorph2.core.semantic.GlobalSemanticRepository;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticClass;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticInstance;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticRepository;
import edu.ucsd.ccdb.ontomorph2.core.spatial.DemoCoordinateSystem;
import edu.ucsd.ccdb.ontomorph2.core.spatial.PositionVector;
import edu.ucsd.ccdb.ontomorph2.core.spatial.RotationVector;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Curve3D;
import edu.ucsd.ccdb.ontomorph2.core.tangible.DataMesh;
import edu.ucsd.ccdb.ontomorph2.core.tangible.NeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Slide;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Surface;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Tangible;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Volume;
import edu.ucsd.ccdb.ontomorph2.core.tangible.neuronmorphology.MorphMLNeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.core.tangible.slide.CCDBSlide;
import edu.ucsd.ccdb.ontomorph2.core.tangible.slide.URISlide;
import edu.ucsd.ccdb.ontomorph2.util.Log;
import edu.ucsd.ccdb.ontomorph2.util.OMTOfflineException;
import edu.ucsd.ccdb.ontomorph2.util.OMTVector;

/**
 * The default scene of items
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class DefaultScene extends Scene{
	
	TangibleManager manager = null;
		
	URL mitoObjURL = null;
	URL mito2ObjURL = null;
	URL astroObjURL = null;
	URL plasmaObjURL = null;
	URL hippo1URL = null;
	URI hippo2URL = null;
	URI hippo3aURL = null;
	URI hippo3bURL = null;
	URI hippo3cURL = null;
	
	public DefaultScene() {
		manager = TangibleManager.getInstance();
				
		try {
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

		try 
		{
			
			CcdbMicroscopyData hippoImage = CCDBRepository.getInstance().getCCDBData(35);
			
			
			Slide a = new CCDBSlide(hippoImage, 0.87f);
			a.setRelativePosition(new PositionVector(25,-32,17f));
			a.setCoordinateSystem(d);
			//a.setRelativeRotation(new RotationVector(d.getRotationFromAbsolute()));
			a.setRelativeScale(10);
			addSceneObject(a);
			a.setVisible(false); 
			
		} 
		catch (OMTOfflineException e) {
			Log.warn(e.getMessage() + " Cannot load slide from CCDB Data");
		}
		
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


		//hide the slides for tedd waitt
		//TODO: remove this section
		{
			b.setVisible(false);
			c.setVisible(false);
			ds.setVisible(false);
			e.setVisible(false);
		}
		
		try {
			CcdbMicroscopyData cerebImage = CCDBRepository.getInstance().getCCDBData(53);
			
			RotationVector rot = new RotationVector(
					new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD*-20,OMTVector.UNIT_Z));
			
			Slide f = new CCDBSlide(cerebImage, 1.11f);
			f.setRelativePosition(new PositionVector(440,-118,-250));
			f.setRelativeRotation(rot);
			f.setRelativeScale(4.5F); 
			addSceneObject(f);
			f.setVisible(false);	//added for tedd wait demo
		} catch (OMTOfflineException e2) {
			Log.warn(e2.getMessage());
		}
		
		Volume v1 = new Volume(new Box("my box", new OMTVector(-21,-1,15), 20f, 10f, 20f), d);
		//v1.setVisible(false);
		//addSceneObject(v1);
		
		
		/*
		NeuronMorphology cell1 = new CellImpl();
		URL cell1URL = Scene.class.getClassLoader().getResource("1220882a.morph.xml");
		cell1.setMorphologyViaURL(cell1URL);
		cell1.getMorphology().setPosition(new PositionImpl(6,-30,106));
		cell1.getMorphology().setRotation(new RotationImpl(FastMath.DEG_TO_RAD*-90, new OMTVector(0,1,0)));
		cell1.getMorphology().setScale(0.15f);
		cell1.getMorphology().setRenderOption(NeuronMorphology.RENDER_AS_CYLINDERS);
		cells.add(cell1);
		
		NeuronMorphology cell2 = new CellImpl();
		URL cell2URL = Scene.class.getClassLoader().getResource("1220882a.morph.xml");
		cell2.setMorphologyViaURL(cell1URL);
		cell2.getMorphology().setPosition(new PositionImpl(6,6,106));
		//cell2.getMorphology().setRotation(new RotationImpl(FastMath.DEG_TO_RAD*-90, new OMTVector(0,1,0)));
		cell2.getMorphology().setScale(0.15f);
		cell2.getMorphology().setRenderOption(NeuronMorphology.RENDER_AS_LINES);

		//get semantic thing for a pyramidal cell
		//ISemanticThing pyramCell = SemanticRepository.getInstance().getSemanticThing("sao:sao830368389");
		//cell2.setSemanticThing(pyramCell);
		cells.add(cell2);
		*/
		
		OMTVector p1 = new OMTVector(-5,2,20);
		OMTVector p2 = new OMTVector(-16,3,20);
		OMTVector p2a = new OMTVector(-40, -12,20);
		OMTVector p3 = new OMTVector(-36,-5,20);
		OMTVector p4 = new OMTVector(-30,-3,20);
		OMTVector p5 = new OMTVector(-10,-4,20);
				
		//OMTVector[] array = {p1, p2, p2a, p3, p4, p5};
		OMTVector[] array = {p1, p2, p3, p5};
		Curve3D curve1 = new Curve3D("Dentate Gyrus", array, d);
		curve1.setColor(Color.BLUE);
		curve1.setModelBinormalWithUpVector(OMTVector.UNIT_Y, 0.01f);
		addSceneObject(curve1);

		OMTVector c2v1 = new OMTVector(-5,-3,20);
		OMTVector c2v2 = new OMTVector(18,-4,20);
		OMTVector c2v3 = new OMTVector(12,10,20);
		OMTVector c2v4 = new OMTVector(-9,23,20);
		OMTVector c2v5 = new OMTVector(-23,23,20);
		
		OMTVector[] array2 = {c2v1, c2v2, c2v3, c2v4, c2v5};
		//OMTVector[] array2 = {c2v1, c2v2, c2v3, c2v5};
		Curve3D c2 = new Curve3D("CA",array2, d);
		c2.setColor(Color.BLUE);
		c2.setModelBinormalWithUpVector(OMTVector.UNIT_Y, 0.1f);
		addSceneObject(c2);
		
		
		NeuronMorphology cell3 = new MorphMLNeuronMorphology("cell1zr", c2, 0.03f, 
				NeuronMorphology.RENDER_AS_LOD_2, d);
		cell3.setRelativeScale(0.01f);
		//semantic thing for hippocampal CA3 neuron
		cell3.addSemanticClass(SemanticRepository.getAvailableInstance().getSemanticClass(SemanticClass.CA3_PYRAMIDAL_CELL_CLASS));
		cell3.getMainSemanticInstance();
		addSceneObject(cell3);
		
		NeuronMorphology cell4 = new MorphMLNeuronMorphology("cell2zr", c2, 0.2f, 
				NeuronMorphology.RENDER_AS_LOD, d);
		cell4.setRelativeScale(0.01f);
		cell4.addSemanticClass(SemanticRepository.getAvailableInstance().getSemanticClass(SemanticClass.CA3_PYRAMIDAL_CELL_CLASS));
		cell4.getMainSemanticInstance();
		addSceneObject(cell4);
		

		NeuronMorphology cell5 = new MorphMLNeuronMorphology("cell6zr", c2, 0.35f, 
				NeuronMorphology.RENDER_AS_LOD, d);
		cell5.setRelativeScale(0.01f);
		cell5.addSemanticClass(SemanticRepository.getAvailableInstance().getSemanticClass(SemanticClass.CA3_PYRAMIDAL_CELL_CLASS));
		cell5.getMainSemanticInstance();
		addSceneObject(cell5);
		

		/** These models have their up vectors pointing in an X direction
		 *  Need to implement a way to have a curve properly rotate these guys.
		 *  Curve may need different model vectors for different NeuronMorphologies
		 */

		/*
		NeuronMorphology cell6 = new MorphMLNeuronMorphology(cell6URL, c2, 0.8f, 
				NeuronMorphology.RENDER_AS_LOD, d);
		//NeuronMorphology cell6 = new NeuronMorphology(cell6URL, null, null, NeuronMorphology.RENDER_AS_LOD);
		cell6.setRelativeScale(0.02f);
		//cell6.setUpVector(new OMTVector(1,0,0));
		cell6.setUpVector(new OMTVector(0,0,1));
		cell6.addSemanticThing(SemanticRepository.getInstance().getSemanticClass(SemanticClass.CA1_PYRAMIDAL_CELL_CLASS));
		addSceneObject(cell6);
				
		

		NeuronMorphology cell7 = new MorphMLNeuronMorphology(cell7URL, c2, 0.91f, 
				NeuronMorphology.RENDER_AS_LOD, d);
		cell7.setRelativeScale(0.02f);
		//cell7.setUpVector(new OMTVector(1,0,0));
		cell7.setUpVector(new OMTVector(0,0,1));
		cell7.addSemanticThing(SemanticRepository.getInstance().getSemanticClass(SemanticClass.CA1_PYRAMIDAL_CELL_CLASS));
		addSceneObject(cell7);
		*/
		
		/*
		
		URL cell8URL = Scene.class.getClassLoader().getResource("etc/morphml/hippocampus/cb27e.morph.xml");
		addSceneObject(cell8);
		
		URL cell9URL = Scene.class.getClassLoader().getResource("etc/morphml/hippocampus/cb27g.morph.xml");
		addSceneObject(cell9);
		
		URL cell10URL = Scene.class.getClassLoader().getResource("etc/morphml/hippocampus/cd1152.morph.xml");
		addSceneObject(cell10); */
		


		int numCells = 15;
		for (int i = 1; i < numCells; i++) {
			NeuronMorphology cell11 = new MorphMLNeuronMorphology("5199202a", curve1, ((float)i)/numCells-0.01f, NeuronMorphology.RENDER_AS_LOD, d);

			cell11.setRelativeScale(0.01f);
			
			cell11.addSemanticClass(SemanticRepository.getAvailableInstance().getSemanticClass(SemanticClass.DENTATE_GYRUS_GRANULE_CELL_CLASS));
			cell11.getMainSemanticInstance();
			
			addSceneObject(cell11);
		}
		
		/*
		NeuronMorphology cell12 = new NeuronMorphology(); 
		URL cell12URL = Scene.class.getClassLoader().getResource("etc/morphml/hippocampus/pv08d.morph.xml");
		cell12.setMorphologyViaURL(cell12URL);
		cell12.getMorphology().setPosition(new PositionImpl(-25,0,20));
		//cell3.getMorphology().setRotation(new RotationImpl(FastMath.DEG_TO_RAD*-90, new OMTVector(0,1,0)));
		cell12.getMorphology().setScale(0.01f);
		cell12.getMorphology().setRenderOption(NeuronMorphology.RENDER_AS_LINES);
		addSceneObject(cell12);
		
		NeuronMorphology cell13 = new NeuronMorphology(); 
		URL cell13URL = Scene.class.getClassLoader().getResource("etc/morphml/hippocampus/pv22b.morph.xml");
		cell13.setMorphologyViaURL(cell13URL);
		cell13.getMorphology().setPosition(new PositionImpl(25,0,20));
		//cell3.getMorphology().setRotation(new RotationImpl(FastMath.DEG_TO_RAD*-90, new OMTVector(0,1,0)));
		cell13.getMorphology().setScale(0.01f);
		cell13.getMorphology().setRenderOption(NeuronMorphology.RENDER_AS_LINES);
		addSceneObject(cell13);*/
		
		loadSurfaces();
		
		loadMeshes();
		
		ReferenceAtlas.getInstance().displayBasicAtlas();

		changed(CHANGED_LOAD);
	}
	
	public void loadSurfaces() {

		OMTVector p1 = new OMTVector(-5,2,20);
		OMTVector p2 = new OMTVector(-16,10,20);
		OMTVector p2a = new OMTVector(-40, -12,20);
		OMTVector p3 = new OMTVector(-50,-8,20);
		OMTVector p4 = new OMTVector(-30,-3,20);
		OMTVector p5 = new OMTVector(-10,-4,20);
		
		p1 = new OMTVector(-10,-5,20);
		p2 = new OMTVector(3,-9,20);
		p3 = new OMTVector(14,-10,20);
		p4 = new OMTVector(-9,20,20);
		p5 = new OMTVector(-23,15,20);
				
		OMTVector[][] array3 = {{p1, p3, p4, p5},
				{new OMTVector(p1.x, p1.y, p1.z-20), new OMTVector(p3.x, p3.y, p3.z-20), new OMTVector(p4.x, p4.y, p4.z-20), new OMTVector(p5.x, p5.y, p5.z-20)},   
				{new OMTVector(p1.x, p1.y, p1.z-40), new OMTVector(p3.x, p3.y, p3.z-40), new OMTVector(p4.x, p4.y, p4.z-40), new OMTVector(p5.x, p5.y, p5.z-40)},   
				{new OMTVector(p1.x, p1.y, p1.z-60), new OMTVector(p3.x, p3.y, p3.z-60), new OMTVector(p4.x, p4.y, p4.z-60), new OMTVector(p5.x, p5.y, p5.z-60)}
		};
		
		//Surface surf1 = new Surface("my surf", array3, 16);
		//manager.addSurface(surf1);
		
		p1 = new OMTVector(-20,0,20);
		p2 = new OMTVector(-29,-5,20);
		p3 = new OMTVector(-20,-10,20);
		
		OMTVector[][] array4 = {{p1, p2, p2, p3},
				{new OMTVector(p1.x, p1.y, p1.z-20), new OMTVector(p2.x, p2.y, p2.z-20), new OMTVector(p2.x, p2.y, p2.z-20), new OMTVector(p3.x, p3.y, p3.z-20)},   
				{new OMTVector(p1.x, p1.y, p1.z-40), new OMTVector(p2.x, p2.y, p2.z-40), new OMTVector(p2.x, p2.y, p2.z-40), new OMTVector(p3.x, p3.y, p3.z-40)},   
				{new OMTVector(p1.x, p1.y, p1.z-60), new OMTVector(p2.x, p2.y, p2.z-60), new OMTVector(p2.x, p2.y, p2.z-60), new OMTVector(p3.x, p3.y, p3.z-60)}
		};

		//Surface surf2 = new Surface("my surf", array4, 16);
		//manager.addSurface(surf2);
	}
	
	public void loadMeshes() {

		DemoCoordinateSystem d = new DemoCoordinateSystem();
		
		
		DataMesh mesh, mito2;
		try {
			mesh = new DataMesh(mitoObjURL);
		
		//mesh.loadMaxFile("etc/mito/mito_outer.3ds");
		//mesh.setRelativePosition(new PositionVector(0.49f, -3.5f, 20.01f));
		mesh.setRelativePosition(new PositionVector(0.49f, -3.3f, 20.01f));
		mesh.setRelativeRotation(new RotationVector(FastMath.DEG_TO_RAD*90, OMTVector.UNIT_X));
		mesh.setRelativeScale(0.0002f);
		mesh.setCoordinateSystem(d);
		
		//addSceneObject(mesh);
		
		mito2 = new DataMesh(mito2ObjURL);
		
		mito2.setRelativePosition(new PositionVector(0.49f, -3.3f, 20.03f));
		mito2.setRelativeRotation(new RotationVector(FastMath.DEG_TO_RAD*90, OMTVector.UNIT_X));
		mito2.setRelativeScale(0.0002f);
		mito2.setCoordinateSystem(d);
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		//addSceneObject(mito2);
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
