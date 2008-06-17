package edu.ucsd.ccdb.ontomorph2.core.scene;

import java.awt.Color;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Set;

import neuroml.generated.CellInstance;
import neuroml.generated.Instances;
import neuroml.generated.NetworkML;
import neuroml.generated.NeuroMLLevel2;
import neuroml.generated.Point;
import neuroml.generated.Population;
import neuroml.generated.Populations;
import neuroml.generated.NeuroMLLevel2.Cells;

import com.jme.math.FastMath;
import com.jme.scene.shape.Box;

import edu.ucsd.ccdb.ontomorph2.core.semantic.ISemanticClass;
import edu.ucsd.ccdb.ontomorph2.core.spatial.DemoCoordinateSystem;
import edu.ucsd.ccdb.ontomorph2.core.spatial.OMTVector;
import edu.ucsd.ccdb.ontomorph2.core.spatial.PositionVector;
import edu.ucsd.ccdb.ontomorph2.core.spatial.RotationVector;

/**
 * Defines the totality of the objects that can be viewed in the 3D world
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class Scene extends Observable{
	
	SceneObjectManager manager = null;
	
	public static final String baseDir = "." + File.separatorChar + "etc" + File.separatorChar;
	public static final String imgDir = baseDir + "img" + File.separatorChar;
	public static final String morphMLDir = baseDir + "morphml" 
											+ File.separatorChar + "hippocampus" + File.separatorChar;
	public static final String mitoDir = baseDir + File.separatorChar + "mito" + File.separatorChar;
	public static final String allenDir = baseDir + File.separatorChar + "allen" 
									+ File.separatorChar;
	public static final String allenMeshDir = allenDir + "Mesh25" + File.separatorChar;


	
	URL cell3URL = null;
	URL cell4URL = null;
	URL cell5URL = null;
	URL cell6URL = null;
	URL cell7URL = null;
	URL cell11URL = null;
	URL mitoObjURL = null;
	URL hippo1URL = null;
	URL hippo2URL = null;
	URL hippo3aURL = null;
	URL hippo3bURL = null;
	URL hippo3cURL = null;
	
	public Scene() {
		manager = SceneObjectManager.getInstance();
				
		try {
			cell3URL = new File(morphMLDir + "cell1zr.morph.xml").toURI().toURL();
			cell4URL = new File(morphMLDir + "cell2zr.morph.xml").toURI().toURL();
			cell5URL = new File(morphMLDir + "cell6zr.morph.xml").toURI().toURL();
			cell6URL = new File(morphMLDir + "pc1c.morph.xml").toURI().toURL();
			cell7URL = new File(morphMLDir + "pc2a.morph.xml").toURI().toURL();
			cell11URL = new File(morphMLDir + "5199202a.morph.xml").toURI().toURL();
			mitoObjURL = new File(mitoDir + "mito_outer.obj").toURI().toURL();
			
			hippo1URL = new File(imgDir + "hippo_slice1.jpg").toURI().toURL();
			hippo2URL = new File(imgDir + "hippo_slice2.jpg").toURI().toURL();
			hippo3aURL = new File(imgDir + "hippo_slice3a.jpg").toURI().toURL();
			hippo3bURL = new File(imgDir + "hippo_slice3b.jpg").toURI().toURL();
			hippo3cURL = new File(imgDir + "hippo_slice3c.jpg").toURI().toURL();
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	private void addSceneObject(ISceneObject s) {
		if (s instanceof Slide) {
			manager.addSlide(s);
		} else if (s instanceof INeuronMorphology) {
			manager.addCell(s);
		} else if (s instanceof IVolume) {
			manager.addVolume(s);
		} else if (s instanceof DataMesh) {
			manager.addMesh(s);
		}
	}
	
	public void load() {
		//temporary hack o load a mockup
		DemoCoordinateSystem d = new DemoCoordinateSystem();

		/*
		addSceneObject(new Slide(hippo1URL, new PositionVector(-60,-100,22), 170, 0.87f, d));
		addSceneObject(new Slide(hippo2URL, new PositionVector(-55,-30, 22), 62, 1.34f, d));
		addSceneObject(new Slide(hippo3aURL, new PositionVector(-45,-13,21.5f), 15, 1.33f, d));
		addSceneObject(new Slide(hippo3bURL, new PositionVector(-24,-9,21.5f), 15,1.31f, d));
		addSceneObject(new Slide(hippo3cURL, new PositionVector(-5,-8.5f,21.5f), 15,1.33f, d));
		*/
		addSceneObject(new Slide(hippo1URL, new PositionVector(-60,-100,22), 
				new RotationVector(d.getRotationFromAbsolute()), 170, 0.87f));
		addSceneObject(new Slide(hippo2URL, new PositionVector(-55,-30, 22), 
				new RotationVector(d.getRotationFromAbsolute()), 62, 1.34f));
		addSceneObject(new Slide(hippo3aURL, new PositionVector(-45,-13,21.5f), 
				new RotationVector(d.getRotationFromAbsolute()), 15, 1.33f));
		addSceneObject(new Slide(hippo3bURL, new PositionVector(-24,-9,21.5f), 
				new RotationVector(d.getRotationFromAbsolute()), 15,1.31f));
		addSceneObject(new Slide(hippo3cURL, new PositionVector(-5,-8.5f,21.5f), 
				new RotationVector(d.getRotationFromAbsolute()), 15,1.33f));

		//addSceneObject(new Slide("etc/img/hippo_slice3d.jpg", new PositionImpl(-10,-10,22), 10,1));
		//addSceneObject(new Slide("etc/img/hippo_slice3e.jpg", new PositionImpl(-50,-10,22), 10,1));
		//addSceneObject(new Slide("etc/img/hippo_slice3f.jpg", new PositionImpl(10,0,22), 10,1));
		//addSceneObject(new Slide("etc/img/hippo_slice3g.jpg", new PositionImpl(-20,30,22), 10,1));
		
		IVolume v1 = new VolumeImpl(new Box("my box", new OMTVector(-21,-1,15), 20f, 10f, 20f), d);
		v1.setVisible(false);
		addSceneObject(v1);
		
		
		/*
		INeuronMorphology cell1 = new CellImpl();
		URL cell1URL = Scene.class.getClassLoader().getResource("1220882a.morph.xml");
		cell1.setMorphologyViaURL(cell1URL);
		cell1.getMorphology().setPosition(new PositionImpl(6,-30,106));
		cell1.getMorphology().setRotation(new RotationImpl(FastMath.DEG_TO_RAD*-90, new OMTVector(0,1,0)));
		cell1.getMorphology().setScale(0.15f);
		cell1.getMorphology().setRenderOption(INeuronMorphology.RENDER_AS_CYLINDERS);
		cells.add(cell1);
		
		INeuronMorphology cell2 = new CellImpl();
		URL cell2URL = Scene.class.getClassLoader().getResource("1220882a.morph.xml");
		cell2.setMorphologyViaURL(cell1URL);
		cell2.getMorphology().setPosition(new PositionImpl(6,6,106));
		//cell2.getMorphology().setRotation(new RotationImpl(FastMath.DEG_TO_RAD*-90, new OMTVector(0,1,0)));
		cell2.getMorphology().setScale(0.15f);
		cell2.getMorphology().setRenderOption(INeuronMorphology.RENDER_AS_LINES);

		//get semantic thing for a pyramidal cell
		//ISemanticThing pyramCell = SemanticRepository.getInstance().getSemanticThing("sao:sao830368389");
		//cell2.setSemanticThing(pyramCell);
		cells.add(cell2);
		*/
		
		OMTVector p1 = new OMTVector(-5,2,20);
		OMTVector p2 = new OMTVector(-16,10,20);
		OMTVector p2a = new OMTVector(-40, -12,20);
		OMTVector p3 = new OMTVector(-50,-8,20);
		OMTVector p4 = new OMTVector(-30,-3,20);
		OMTVector p5 = new OMTVector(-10,-4,20);
				
		OMTVector[] array = {p1, p2, p2a, p3, p4, p5};
		Curve3D curve1 = new Curve3D("Dentate Gyrus", array, d);
		curve1.setColor(Color.BLUE);
		curve1.setModelBinormalWithUpVector(OMTVector.UNIT_Y, 0.01f);
		manager.addCurve(curve1);

		OMTVector c2v1 = new OMTVector(-5,-3,20);
		OMTVector c2v2 = new OMTVector(45,-10,20);
		OMTVector c2v3 = new OMTVector(12,10,20);
		OMTVector c2v4 = new OMTVector(-9,30,20);
		OMTVector c2v5 = new OMTVector(-23,25,20);
		
		OMTVector[] array2 = {c2v1, c2v2, c2v3, c2v4, c2v5};
		Curve3D c2 = new Curve3D("CA",array2, d);
		c2.setColor(Color.BLUE);
		c2.setModelBinormalWithUpVector(OMTVector.UNIT_Y, 0.1f);
		manager.addCurve(c2);
		

		
		NeuronMorphologyImpl cell3 = new NeuronMorphologyImpl(cell3URL, c2, 0.03f, 
				INeuronMorphology.RENDER_AS_LOD_2, d);
		cell3.setRelativeScale(0.01f);
		//semantic thing for hippocampal CA3 neuron
		cell3.addSemanticClass(ISemanticClass.CA3_PYRAMIDAL_CELL_CLASS);
		addSceneObject(cell3);

		
		
		
		NeuronMorphologyImpl cell4 = new NeuronMorphologyImpl(cell4URL, c2, 0.2f, 
				INeuronMorphology.RENDER_AS_LOD, d);
		cell4.setRelativeScale(0.01f);
		cell4.addSemanticClass(ISemanticClass.CA3_PYRAMIDAL_CELL_CLASS);
		addSceneObject(cell4);
		
		
		NeuronMorphologyImpl cell5 = new NeuronMorphologyImpl(cell5URL, c2, 0.35f, 
				INeuronMorphology.RENDER_AS_LOD, d);
		cell5.setRelativeScale(0.01f);
		cell5.addSemanticClass(ISemanticClass.CA3_PYRAMIDAL_CELL_CLASS);
		addSceneObject(cell5);
		

		/** These models have their up vectors pointing in an X direction
		 *  Need to implement a way to have a curve properly rotate these guys.
		 *  Curve may need different model vectors for different NeuronMorphologies
		 */

		NeuronMorphologyImpl cell6 = new NeuronMorphologyImpl(cell6URL, c2, 0.8f, 
				INeuronMorphology.RENDER_AS_LOD, d);
		//NeuronMorphologyImpl cell6 = new NeuronMorphologyImpl(cell6URL, null, null, INeuronMorphology.RENDER_AS_LOD);
		cell6.setRelativeScale(0.02f);
		//cell6.setUpVector(new OMTVector(1,0,0));
		cell6.setUpVector(new OMTVector(0,0,1));
		cell6.addSemanticClass(ISemanticClass.CA1_PYRAMIDAL_CELL_CLASS);
		//addSceneObject(cell6);
				
		

		NeuronMorphologyImpl cell7 = new NeuronMorphologyImpl(cell7URL, c2, 0.91f, 
				INeuronMorphology.RENDER_AS_LOD, d);
		cell7.setRelativeScale(0.02f);
		//cell7.setUpVector(new OMTVector(1,0,0));
		cell7.setUpVector(new OMTVector(0,0,1));
		cell7.addSemanticClass(ISemanticClass.CA1_PYRAMIDAL_CELL_CLASS);
		//addSceneObject(cell7);
		
		/*
		
		URL cell8URL = Scene.class.getClassLoader().getResource("etc/morphml/hippocampus/cb27e.morph.xml");
		addSceneObject(cell8);
		
		URL cell9URL = Scene.class.getClassLoader().getResource("etc/morphml/hippocampus/cb27g.morph.xml");
		addSceneObject(cell9);
		
		URL cell10URL = Scene.class.getClassLoader().getResource("etc/morphml/hippocampus/cd1152.morph.xml");
		addSceneObject(cell10); */
		

		
		for (int i = 1; i < 20; i++) {
			NeuronMorphologyImpl cell11 = new NeuronMorphologyImpl(cell11URL, curve1, ((float)i)/20f-0.01f, 
					INeuronMorphology.RENDER_AS_LOD, d);
			cell11.setRelativeScale(0.01f);
			cell11.addSemanticClass(ISemanticClass.DENTATE_GYRUS_GRANULE_CELL_CLASS);
			addSceneObject(cell11);
		}
		
		
		/*
		NeuronMorphologyImpl cell12 = new NeuronMorphologyImpl(); 
		URL cell12URL = Scene.class.getClassLoader().getResource("etc/morphml/hippocampus/pv08d.morph.xml");
		cell12.setMorphologyViaURL(cell12URL);
		cell12.getMorphology().setPosition(new PositionImpl(-25,0,20));
		//cell3.getMorphology().setRotation(new RotationImpl(FastMath.DEG_TO_RAD*-90, new OMTVector(0,1,0)));
		cell12.getMorphology().setScale(0.01f);
		cell12.getMorphology().setRenderOption(INeuronMorphology.RENDER_AS_LINES);
		addSceneObject(cell12);
		
		NeuronMorphologyImpl cell13 = new NeuronMorphologyImpl(); 
		URL cell13URL = Scene.class.getClassLoader().getResource("etc/morphml/hippocampus/pv22b.morph.xml");
		cell13.setMorphologyViaURL(cell13URL);
		cell13.getMorphology().setPosition(new PositionImpl(25,0,20));
		//cell3.getMorphology().setRotation(new RotationImpl(FastMath.DEG_TO_RAD*-90, new OMTVector(0,1,0)));
		cell13.getMorphology().setScale(0.01f);
		cell13.getMorphology().setRenderOption(INeuronMorphology.RENDER_AS_LINES);
		addSceneObject(cell13);*/
		
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
		
		Surface surf1 = new Surface("my surf", array3, 16);
		//manager.addSurface(surf1);
		
		p1 = new OMTVector(-20,0,20);
		p2 = new OMTVector(-29,-5,20);
		p3 = new OMTVector(-20,-10,20);
		
		OMTVector[][] array4 = {{p1, p2, p2, p3},
				{new OMTVector(p1.x, p1.y, p1.z-20), new OMTVector(p2.x, p2.y, p2.z-20), new OMTVector(p2.x, p2.y, p2.z-20), new OMTVector(p3.x, p3.y, p3.z-20)},   
				{new OMTVector(p1.x, p1.y, p1.z-40), new OMTVector(p2.x, p2.y, p2.z-40), new OMTVector(p2.x, p2.y, p2.z-40), new OMTVector(p3.x, p3.y, p3.z-40)},   
				{new OMTVector(p1.x, p1.y, p1.z-60), new OMTVector(p2.x, p2.y, p2.z-60), new OMTVector(p2.x, p2.y, p2.z-60), new OMTVector(p3.x, p3.y, p3.z-60)}
		};

		Surface surf2 = new Surface("my surf", array4, 16);
		//manager.addSurface(surf2);
		
		
		DataMesh mesh = new DataMesh();
		//mesh.loadMaxFile("etc/mito/mito_outer.3ds");
		mesh.setObjMeshURL(mitoObjURL);
		//mesh.setRelativePosition(new PositionVector(0.49f, -3.5f, 20.01f));
		mesh.setRelativePosition(new PositionVector(0.49f, -3.3f, 20.01f));
		mesh.setRelativeRotation(new RotationVector(FastMath.DEG_TO_RAD*90, OMTVector.UNIT_X));
		mesh.setRelativeScale(0.0002f);
		mesh.setCoordinateSystem(d);
	
		//addSceneObject(mesh);
		
		changed();
	}

	public void save() {
		Instances ins = new Instances();
		Cells thecells = new Cells();
		for (INeuronMorphology c : manager.getCells()) {
			CellInstance ci = new CellInstance();
			Point loc = new Point();
			loc.setX(c.getRelativePosition().getX());
			loc.setY(c.getRelativePosition().getY());
			loc.setZ(c.getRelativePosition().getZ());
			ci.setLocation(loc);
			//ci.setId(c.getMorphology().get)
			ins.getInstance().add(ci);
			
			thecells.getCell().add(((NeuronMorphologyImpl)c).getMorphMLCell());
		}
		Population pop = new Population();
		
		Populations p = new Populations();
		p.getPopulation().add(pop);
		NetworkML nml = new NetworkML();
		nml.setPopulations(p);
		

		NeuroMLLevel2 nml2 = new NeuroMLLevel2();
		nml2.setCells(thecells);
	}
	
	public ArrayList<Slide> getSlides() {
		return manager.getSlides();
	}
	
	public Set<INeuronMorphology> getCells() {
		return manager.getCells();
	}

	 public void changed () {       
		  setChanged();                 
		  notifyObservers();            
	 }

	public Set<Curve3D> getCurves() {
		return manager.getCurves();
	}

	public Set<Surface> getSurfaces() {
		return manager.getSurfaces();
	}

	public Set<DataMesh> getMeshes() {
		return manager.getMeshes();
	}

	public Set<IVolume> getVolumes() {
		return manager.getVolumes();
	}

	
}
