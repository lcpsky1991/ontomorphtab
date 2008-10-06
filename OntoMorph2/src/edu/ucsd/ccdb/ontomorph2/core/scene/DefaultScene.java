package edu.ucsd.ccdb.ontomorph2.core.scene;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;

import org.morphml.metadata.schema.Curve;
import org.morphml.metadata.schema.Point3D;
import org.morphml.networkml.schema.CellInstance;
import org.morphml.networkml.schema.CurveAssociation;
import org.morphml.networkml.schema.Population;
import org.morphml.neuroml.schema.Neuroml;
import org.morphml.neuroml.schema.XWBCSlide;

import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.scene.shape.Box;

import edu.ucsd.ccdb.ontomorph2.core.data.CCDBRepository;
import edu.ucsd.ccdb.ontomorph2.core.data.DataRepository;
import edu.ucsd.ccdb.ontomorph2.core.data.ReferenceAtlas;
import edu.ucsd.ccdb.ontomorph2.core.data.wsclient.CcdbMicroscopyData;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticClass;
import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticRepository;
import edu.ucsd.ccdb.ontomorph2.core.spatial.DemoCoordinateSystem;
import edu.ucsd.ccdb.ontomorph2.core.spatial.PositionVector;
import edu.ucsd.ccdb.ontomorph2.core.spatial.RotationQuat;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Curve3D;
import edu.ucsd.ccdb.ontomorph2.core.tangible.DataMesh;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Volume;
import edu.ucsd.ccdb.ontomorph2.core.tangible.neuronmorphology.NeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.core.tangible.slide.CCDBSlide;
import edu.ucsd.ccdb.ontomorph2.core.tangible.slide.Slide;
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
	URI hippo22URL = null;
	URI striatum1URL = null;
	URI monkey = null;
	
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
			hippo22URL = new File(imgDir + "hippo2.jpg").toURI();
			striatum1URL = new File(imgDir + "striatum1.jpg").toURI();
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

//	public void load() {
//		Log.warn("Loading scene from DB");
//		long tick = Log.tick();
//		Neuroml scene = DataRepository.getInstance().loadScene();
//		
//		//load tangibles for curves
//		for (Iterator it = scene.getCurves().getCurve().iterator(); it.hasNext();) {
//			this.addSceneObject(new Curve3D((Curve)it.next()));
//		}
//		
////		load tangibles for slides
//		for (Iterator it = scene.getSlides().getSlide().iterator(); it.hasNext();) {
//			this.addSceneObject(new Slide((XWBCSlide)it.next()));
//		}
//		
////		load tangibles for cell instances
//		for (Iterator it = scene.getPopulations().getPopulation().iterator(); it.hasNext();) {
//			Population p = (Population) it.next();
//			NeuronMorphology instance = new NeuronMorphology(p.getCellType());
//			
//			for (Iterator it2 = p.getInstances().getInstance().iterator(); it2.hasNext();) {
//				CellInstance ci = (CellInstance)it2.next();
//				CurveAssociation ca = ci.getCurveAssociation();
//				if (ca != null) {
//					for (Curve3D curve : getCurves()) {
//						if (curve.getMorphMLCurve().getId().equals(ca.getCurveId())) {
//							instance.setCurve(curve);
//							instance.positionAlongCurve(curve, (float)ca.getTime());
//						}
//					}
//				} else {
//					instance.setPosition(new PositionVector(ci.getLocation()));
//				}
//			}
//			
//			this.addSceneObject(instance);
//			
//			ReferenceAtlas.getInstance().displayBasicAtlas();
//		}
//		changed(CHANGED_LOAD);
//		Log.tock("Finished loading scene from db! ", tick);
//	}
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
			
			Slide a = new CCDBSlide("CCDBSlide 35", hippoImage, 0.87f);
			a.setPosition(new PositionVector(25,-32,17f));
			//a.setRelativeRotation(new RotationQuat(d.getRotationFromAbsolute()));
			a.setScale(10);
			addSceneObject(a);
			a.setVisible(false); 
			
		} 
		catch (Exception e) {
			Log.warn(e.getMessage() + " Cannot load slide from CCDB Data");
		}
		
		
		
		{

			Slide b = new Slide("hippo2", hippo2URL, 1.34f);
		    b.setName("Waldo");
		    b.setPosition(new PositionVector(-14,0, 18f));
		    //b.setRelativeRotation(new RotationVector(d.getRotationFromAbsolute()));
		    b.setScale(3.2f);
			addSceneObject(b);
		}
	
		
		Slide c = new Slide("hippo3a", hippo3aURL, 1.33f);
		c.setPosition(new PositionVector(-34,-5,19f));
		//c.setRelativeRotation(new RotationQuat(d.getRotationFromAbsolute()));
		c.setScale(0.75f);
		addSceneObject(c);
		
		Slide ds = new Slide("hippo3b", hippo3bURL, 1.31f);
		ds.setPosition(new PositionVector(-15f,-1.5f,19.1f));
		//ds.setRelativeRotation(new RotationQuat(d.getRotationFromAbsolute()));
		ds.setScale(0.75f);
		addSceneObject(ds);
		
		Slide e = new Slide("hippo3c", hippo3cURL, 1.33f);
		e.setPosition(new PositionVector(4,-1f,19.2f));
		//e.setRelativeRotation(new RotationQuat(d.getRotationFromAbsolute()));
		e.setScale(0.75f);
		addSceneObject(e);
		
		Slide f = new Slide("hippo22", hippo22URL, 0.87f);
		f.setName("hippocampus 2");
		f.setPosition(new PositionVector(319.9474f,-153.3174f,-145.52f));
		f.setRotation(new RotationQuat(d.getRotationFromAbsolute()));
		f.setScale(10f);
		addSceneObject(f);
		
		Slide g = new Slide("striatum1", striatum1URL, 1.33f);
		g.setName("striatum 1");
		g.setPosition(new PositionVector(213.1435f, -145.5603f, -146.37f));
		g.setRotation(new RotationQuat(d.getRotationFromAbsolute()));
		//e.setRelativeRotation(new RotationQuat(d.getRotationFromAbsolute()));
		g.setScale(10f);
		addSceneObject(g);


		//RELOAD ALL OF THE SLIDES form DB
		{
			for (Slide s : TangibleManager.getInstance().getSlides()) {
				s.loadFromDB(s.getName());
			}
		}
		
		
		
		//hide the slides for tedd waitt
		//TODO: remove this section
		{
			for (Slide s : TangibleManager.getInstance().getSlides()) {
				s.setVisible(false);
			}
		}
		
		try {
			CcdbMicroscopyData cerebImage = CCDBRepository.getInstance().getCCDBData(53);
			
			RotationQuat rot = new RotationQuat(
					new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD*-20,OMTVector.UNIT_Z));
			
			Slide h = new CCDBSlide("cerebImage", cerebImage, 1.11f);
			h.setPosition(new PositionVector(440,-118,-250));
			h.setRotation(rot);
			h.setScale(4.5F); 
			addSceneObject(h);
			h.setVisible(false);	//added for tedd wait demo
		} catch (OMTOfflineException e2) {
			Log.warn(e2.getMessage());
		}
		
		Volume v1 = new Volume("box", new Box("my box", new OMTVector(-21,-1,15), 20f, 10f, 20f));
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
		Curve3D curve1 = new Curve3D("Dentate Gyrus", array);
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
		Curve3D c2 = new Curve3D("CA",array2);
		c2.setColor(Color.BLUE);
		c2.setModelBinormalWithUpVector(OMTVector.UNIT_Y, 0.1f);
		addSceneObject(c2);
		
		
		NeuronMorphology cell3 = new NeuronMorphology("cell1zr", c2, 0.03f, 
				NeuronMorphology.RENDER_AS_LOD_2);
		cell3.setScale(0.01f);
		//semantic thing for hippocampal CA3 neuron
		cell3.addSemanticClass(SemanticRepository.getAvailableInstance().getSemanticClass(SemanticClass.CA3_PYRAMIDAL_CELL_CLASS));
		cell3.getSemanticInstance();
		addSceneObject(cell3);
		
		NeuronMorphology cell4 = new NeuronMorphology("cell2zr", c2, 0.2f, 
				NeuronMorphology.RENDER_AS_LOD);
		cell4.setScale(0.01f);
		cell4.addSemanticClass(SemanticRepository.getAvailableInstance().getSemanticClass(SemanticClass.CA3_PYRAMIDAL_CELL_CLASS));
		cell4.getSemanticInstance();
		addSceneObject(cell4);
		

		NeuronMorphology cell5 = new NeuronMorphology("cell6zr", c2, 0.35f, 
				NeuronMorphology.RENDER_AS_LOD);
		cell5.setScale(0.01f);
		cell5.addSemanticClass(SemanticRepository.getAvailableInstance().getSemanticClass(SemanticClass.CA3_PYRAMIDAL_CELL_CLASS));
		cell5.getSemanticInstance();
		addSceneObject(cell5);
		

		/** These models have their up vectors pointing in an X direction
		 *  Need to implement a way to have a curve properly rotate these guys.
		 *  Curve may need different model vectors for different NeuronMorphologies
		 */

		/*
		NeuronMorphology cell6 = new NeuronMorphology(cell6URL, c2, 0.8f, 
				NeuronMorphology.RENDER_AS_LOD, d);
		//NeuronMorphology cell6 = new NeuronMorphology(cell6URL, null, null, NeuronMorphology.RENDER_AS_LOD);
		cell6.setRelativeScale(0.02f);
		//cell6.setUpVector(new OMTVector(1,0,0));
		cell6.setUpVector(new OMTVector(0,0,1));
		cell6.addSemanticThing(SemanticRepository.getInstance().getSemanticClass(SemanticClass.CA1_PYRAMIDAL_CELL_CLASS));
		addSceneObject(cell6);
				
		

		NeuronMorphology cell7 = new NeuronMorphology(cell7URL, c2, 0.91f, 
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
			NeuronMorphology cell11 = new NeuronMorphology("5199202a", curve1, ((float)i)/numCells-0.01f, NeuronMorphology.RENDER_AS_LOD);

			cell11.setScale(0.01f);
			
			cell11.addSemanticClass(SemanticRepository.getAvailableInstance().getSemanticClass(SemanticClass.DENTATE_GYRUS_GRANULE_CELL_CLASS));
			cell11.getSemanticInstance();
			
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
		
		DataMesh mesh;
		try {
			mesh = new DataMesh(mitoObjURL);
			
			
			mesh.setPosition(new PositionVector(289f, -117f, -179.51f));
			mesh.setRotation(new RotationQuat(d.getRotationFromAbsolute()));
			mesh.setScale(0.2f);
			mesh.addSemanticClass(SemanticRepository.getAvailableInstance().getSemanticClass(SemanticClass.MITOCHONDRION_CLASS));
			mesh.getSemanticInstance(); //get a SemanticInstance loaded into the local repository
			
			addSceneObject(mesh);
			
		} catch (IOException x) {
			// TODO Auto-generated catch block
			x.printStackTrace();
		}
		
		//loadSurfaces();
		
		//loadMeshes();
		
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
		mesh.setPosition(new PositionVector(0.49f, -3.3f, 20.01f));
		mesh.setRotation(new RotationQuat(FastMath.DEG_TO_RAD*90, OMTVector.UNIT_X));
		mesh.setScale(0.0002f);
		
		addSceneObject(mesh);
		
		mito2 = new DataMesh(mito2ObjURL);
		
		mito2.setPosition(new PositionVector(0.49f, -3.3f, 20.03f));
		mito2.setRotation(new RotationQuat(FastMath.DEG_TO_RAD*90, OMTVector.UNIT_X));
		mito2.setScale(0.0002f);
		
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		//addSceneObject(mito2);
		/*
		DataMesh astro = new DataMesh();
		astro.setObjMeshURL(astroObjURL);
		astro.setRelativePosition(new PositionVector(0.49f, -3.3f, 20.09f));
		astro.setRelativeRotation(new RotationQuat(FastMath.DEG_TO_RAD*90, OMTVector.UNIT_X));
		astro.setRelativeScale(0.0002f);
		astro.setCoordinateSystem(d);
	
		addSceneObject(astro);
		*/

		/*
		DataMesh plasma = new DataMesh();
		plasma.setObjMeshURL(plasmaObjURL);
		plasma.setRelativePosition(new PositionVector(0.49f, -3.3f, 21.05f));
		plasma.setRelativeRotation(new RotationQuat(FastMath.DEG_TO_RAD*90, OMTVector.UNIT_X));
		plasma.setRelativeScale(0.0002f);
		plasma.setCoordinateSystem(d);
	
		addSceneObject(plasma);
		*/
		
	}
	
}
