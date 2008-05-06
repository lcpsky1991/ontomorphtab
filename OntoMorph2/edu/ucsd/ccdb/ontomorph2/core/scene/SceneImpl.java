package edu.ucsd.ccdb.ontomorph2.core.scene;

import java.awt.Color;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
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
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Box;
import com.sun.org.apache.xml.internal.security.c14n.helper.C14nHelper;

import edu.ucsd.ccdb.ontomorph2.core.semantic.SemanticRepository;
import edu.ucsd.ccdb.ontomorph2.core.spatial.CurveImpl;
import edu.ucsd.ccdb.ontomorph2.core.spatial.ICurve;
import edu.ucsd.ccdb.ontomorph2.core.spatial.IRotation;
import edu.ucsd.ccdb.ontomorph2.core.spatial.ISurface;
import edu.ucsd.ccdb.ontomorph2.core.spatial.PositionImpl;
import edu.ucsd.ccdb.ontomorph2.core.spatial.RotationImpl;
import edu.ucsd.ccdb.ontomorph2.core.spatial.SurfaceImpl;

public class SceneImpl extends Observable implements IScene {

	ArrayList<ISlide> slides = null;
	Set<INeuronMorphology> cells = null;
	Set<ICurve> curves = null;
	Set<ISurface> surfaces = null;
	Set<IMesh> meshes = null;
	Set<IVolume> volumes = null;
	
	public SceneImpl() {
		slides = new ArrayList<ISlide>();
		cells = new HashSet<INeuronMorphology>();
		curves = new HashSet<ICurve>();
		surfaces = new HashSet<ISurface>();
		meshes = new HashSet<IMesh>();
		volumes = new HashSet<IVolume>();
	}
	
	public void load() {
		//temporary hack to load a mockup

		slides.add(new SlideImpl("etc/img/hippo_slice1.jpg", new PositionImpl(-60,-100,22), null, 170, 0.87f));
		slides.add(new SlideImpl("etc/img/hippo_slice2.jpg", new PositionImpl(-55,-30, 22), null, 62, 1.34f));
		slides.add(new SlideImpl("etc/img/hippo_slice3a.jpg", new PositionImpl(-45,-13,21.5f), null, 15, 1.33f));
		slides.add(new SlideImpl("etc/img/hippo_slice3b.jpg", new PositionImpl(-24,-9,21.5f), null, 15,1.31f ));
		slides.add(new SlideImpl("etc/img/hippo_slice3c.jpg", new PositionImpl(-5,-8.5f,21.5f), null, 15,1.33f));
		//slides.add(new SlideImpl("etc/img/hippo_slice3d.jpg", new PositionImpl(-10,-10,22), null, 10,1));
		//slides.add(new SlideImpl("etc/img/hippo_slice3e.jpg", new PositionImpl(-50,-10,22), null, 10,1));
		//slides.add(new SlideImpl("etc/img/hippo_slice3f.jpg", new PositionImpl(10,0,22), null, 10,1));
		//slides.add(new SlideImpl("etc/img/hippo_slice3g.jpg", new PositionImpl(-20,30,22), null, 10,1));
		
		volumes.add(new VolumeImpl(new Box("my box", new Vector3f(-21,-1,15), 20f, 7.5f, 20f)));
		
		/*
		INeuronMorphology cell1 = new CellImpl();
		URL cell1URL = SceneImpl.class.getClassLoader().getResource("1220882a.morph.xml");
		cell1.setMorphologyViaURL(cell1URL);
		cell1.getMorphology().setPosition(new PositionImpl(6,-30,106));
		cell1.getMorphology().setRotation(new RotationImpl(FastMath.DEG_TO_RAD*-90, new Vector3f(0,1,0)));
		cell1.getMorphology().setScale(0.15f);
		cell1.getMorphology().setRenderOption(INeuronMorphology.RENDER_AS_CYLINDERS);
		cells.add(cell1);
		
		INeuronMorphology cell2 = new CellImpl();
		URL cell2URL = SceneImpl.class.getClassLoader().getResource("1220882a.morph.xml");
		cell2.setMorphologyViaURL(cell1URL);
		cell2.getMorphology().setPosition(new PositionImpl(6,6,106));
		//cell2.getMorphology().setRotation(new RotationImpl(FastMath.DEG_TO_RAD*-90, new Vector3f(0,1,0)));
		cell2.getMorphology().setScale(0.15f);
		cell2.getMorphology().setRenderOption(INeuronMorphology.RENDER_AS_LINES);

		//get semantic thing for a pyramidal cell
		//ISemanticThing pyramCell = SemanticRepository.getInstance().getSemanticThing("sao:sao830368389");
		//cell2.setSemanticThing(pyramCell);
		cells.add(cell2);
		*/
		
		Vector3f p1 = new Vector3f(-5,2,20);
		Vector3f p2 = new Vector3f(-16,10,20);
		Vector3f p2a = new Vector3f(-40, -12,20);
		Vector3f p3 = new Vector3f(-50,-8,20);
		Vector3f p4 = new Vector3f(-30,-3,20);
		Vector3f p5 = new Vector3f(-10,-4,20);
				
		Vector3f[] array = {p1, p2, p2a, p3, p4, p5};
		CurveImpl curve1 = new CurveImpl("Dentate Gyrus", array);
		curve1.setColor(Color.BLUE);
		this.curves.add(curve1);

		p1 = new Vector3f(-5,-3,20);
		p2 = new Vector3f(45,-10,20);
		p3 = new Vector3f(12,10,20);
		p4 = new Vector3f(-9,30,20);
		p5 = new Vector3f(-23,25,20);
		
		Vector3f[] array2 = {p1, p2, p3, p4, p5};
		CurveImpl c2 = new CurveImpl("CA",array2);
		c2.setColor(Color.BLUE);
		//this.curves.add(c2);
		
		
		URL cell3URL = SceneImpl.class.getClassLoader().getResource("etc/morphml/hippocampus/cell1zr.morph.xml");
		NeuronMorphologyImpl cell3 = new NeuronMorphologyImpl(cell3URL, null, 
				null, INeuronMorphology.RENDER_AS_LOD_2);
		cell3.positionAlongCurve(c2, 0.03f);
		//cell3.lookAt(new PositionImpl(c2.getPoint(0.5f)));
		RotationImpl r2 = new RotationImpl();
		//r2.lookAt(c2.getTangent(0.8f), Vector3f.UNIT_X);
		//cell3.setRotation(r2);
		cell3.setScale(0.01f);
		//semantic thing for hippocampal CA1 neuron
		cell3.addSemanticThing(SemanticRepository.getInstance().getSemanticThing("nif_cell:nifext_158"));
		cells.add(cell3);
		
		
		NeuronMorphologyImpl cell3a = new NeuronMorphologyImpl(cell3URL, null, null, INeuronMorphology.RENDER_AS_LINES);
		cell3a.setScale(0.01f);
		cell3a.positionAlongCurve(c2, 0.4f);
		//cell3a.setRotation(r2);
		cell3a.lookAt(new PositionImpl(c2.getPoint(0.5f)));
		NeuronMorphologyImpl cell3b = new NeuronMorphologyImpl(cell3URL, null, null, INeuronMorphology.RENDER_AS_LINES);
		cell3b.setScale(0.01f);
		cell3b.positionAlongCurve(c2, 0.6f);
		//cell3b.setRotation(r2);
		cell3b.lookAt(new PositionImpl(c2.getPoint(0.5f)));
		NeuronMorphologyImpl cell3c = new NeuronMorphologyImpl(cell3URL, null, null, INeuronMorphology.RENDER_AS_LINES);
		cell3c.setScale(0.01f);
		cell3c.positionAlongCurve(c2, 0.8f);
		//cell3c.setRotation(r2);
		cell3c.lookAt(new PositionImpl(c2.getPoint(0.5f)));
		/*
		cells.add(cell3a);
		cells.add(cell3b);
		cells.add(cell3c);
		*/
		
		URL cell4URL = SceneImpl.class.getClassLoader().getResource("etc/morphml/hippocampus/cell2zr.morph.xml");
		NeuronMorphologyImpl cell4 = new NeuronMorphologyImpl(cell4URL, null, 
				new RotationImpl(FastMath.DEG_TO_RAD*45, Vector3f.UNIT_Z), INeuronMorphology.RENDER_AS_LOD);
		cell4.positionAlongCurve(c2, 0.2f);
		cell4.setScale(0.01f);
		cell4.addSemanticThing(SemanticRepository.getInstance().getSemanticThing("nif_cell:nifext_158"));
		cells.add(cell4);
		
		
		URL cell5URL = SceneImpl.class.getClassLoader().getResource("etc/morphml/hippocampus/cell6zr.morph.xml");
		NeuronMorphologyImpl cell5 = new NeuronMorphologyImpl(cell5URL, null, 
				new RotationImpl(FastMath.DEG_TO_RAD*80, Vector3f.UNIT_Z), INeuronMorphology.RENDER_AS_LOD);
		cell5.positionAlongCurve(c2, 0.35f);
		cell5.setScale(0.01f);
		cell5.addSemanticThing(SemanticRepository.getInstance().getSemanticThing("nif_cell:nifext_158"));
		cells.add(cell5);
		
		
		/*
		URL cell6URL = SceneImpl.class.getClassLoader().getResource("etc/morphml/hippocampus/pc1c.morph.xml");
		NeuronMorphologyImpl cell6 = new NeuronMorphologyImpl(cell6URL, null,
				new RotationImpl(FastMath.DEG_TO_RAD*-90, Vector3f.UNIT_Z), INeuronMorphology.RENDER_AS_LOD);
		cell6.positionAlongCurve(c2, 0.8f);
		cell6.setScale(0.02f);
		cell6.addSemanticThing(SemanticRepository.getInstance().getSemanticThing("nif_cell:nifext_157"));
		cells.add(cell6);
		
		
		
		URL cell7URL = SceneImpl.class.getClassLoader().getResource("etc/morphml/hippocampus/pc2a.morph.xml");
		NeuronMorphologyImpl cell7 = new NeuronMorphologyImpl(cell7URL, null, 
				new RotationImpl(FastMath.DEG_TO_RAD*-90, Vector3f.UNIT_Z), INeuronMorphology.RENDER_AS_LOD);
		cell7.positionAlongCurve(c2, 0.9f);
		cell7.setScale(0.02f);
		cell7.addSemanticThing(SemanticRepository.getInstance().getSemanticThing("nif_cell:nifext_157"));
		cells.add(cell7);
		*/
		
		/*
		
		URL cell8URL = SceneImpl.class.getClassLoader().getResource("etc/morphml/hippocampus/cb27e.morph.xml");
		cells.add(cell8);
		
		URL cell9URL = SceneImpl.class.getClassLoader().getResource("etc/morphml/hippocampus/cb27g.morph.xml");
		cells.add(cell9);
		
		URL cell10URL = SceneImpl.class.getClassLoader().getResource("etc/morphml/hippocampus/cd1152.morph.xml");
		cells.add(cell10); */
		
		RotationImpl r = new RotationImpl();
		r.lookAt(curve1.getNormal(0.01f), Vector3f.UNIT_Y);
		URL cell11URL = SceneImpl.class.getClassLoader().getResource("etc/morphml/hippocampus/5199202a.morph.xml");
		NeuronMorphologyImpl cell11 = new NeuronMorphologyImpl(cell11URL, null, 
				null, 
				INeuronMorphology.RENDER_AS_LOD);
		cell11.positionAlongCurve(curve1, 0.01f);
		cell11.setScale(0.01f);
		cell11.addSemanticThing(SemanticRepository.getInstance().getSemanticThing("nif_cell:nifext_153"));
		cells.add(cell11);
		
		NeuronMorphologyImpl cell11a = new NeuronMorphologyImpl(cell11URL, null, 
				new RotationImpl(FastMath.DEG_TO_RAD*90,Vector3f.UNIT_Z), INeuronMorphology.RENDER_AS_LOD);
		cell11a.positionAlongCurve(curve1, 0.5f);
		cell11a.setScale(0.01f);
		cell11a.addSemanticThing(SemanticRepository.getInstance().getSemanticThing("nif_cell:nifext_153"));
		cells.add(cell11a);
		
		NeuronMorphologyImpl cell11b = new NeuronMorphologyImpl(cell11URL, null,
				new RotationImpl(FastMath.DEG_TO_RAD*180,Vector3f.UNIT_Z), INeuronMorphology.RENDER_AS_LOD);
		cell11b.positionAlongCurve(curve1, 0.99f);
		cell11b.setScale(0.01f);
		cell11b.addSemanticThing(SemanticRepository.getInstance().getSemanticThing("nif_cell:nifext_153"));
		cells.add(cell11b);
		
		/*
		NeuronMorphologyImpl cell12 = new NeuronMorphologyImpl(); 
		URL cell12URL = SceneImpl.class.getClassLoader().getResource("etc/morphml/hippocampus/pv08d.morph.xml");
		cell12.setMorphologyViaURL(cell12URL);
		cell12.getMorphology().setPosition(new PositionImpl(-25,0,20));
		//cell3.getMorphology().setRotation(new RotationImpl(FastMath.DEG_TO_RAD*-90, new Vector3f(0,1,0)));
		cell12.getMorphology().setScale(0.01f);
		cell12.getMorphology().setRenderOption(INeuronMorphology.RENDER_AS_LINES);
		cells.add(cell12);
		
		NeuronMorphologyImpl cell13 = new NeuronMorphologyImpl(); 
		URL cell13URL = SceneImpl.class.getClassLoader().getResource("etc/morphml/hippocampus/pv22b.morph.xml");
		cell13.setMorphologyViaURL(cell13URL);
		cell13.getMorphology().setPosition(new PositionImpl(25,0,20));
		//cell3.getMorphology().setRotation(new RotationImpl(FastMath.DEG_TO_RAD*-90, new Vector3f(0,1,0)));
		cell13.getMorphology().setScale(0.01f);
		cell13.getMorphology().setRenderOption(INeuronMorphology.RENDER_AS_LINES);
		cells.add(cell13);*/
		
		p1 = new Vector3f(-10,-5,20);
		p2 = new Vector3f(3,-9,20);
		p3 = new Vector3f(14,-10,20);
		p4 = new Vector3f(-9,20,20);
		p5 = new Vector3f(-23,15,20);
				
		Vector3f[][] array3 = {{p1, p3, p4, p5},
				{new Vector3f(p1.x, p1.y, p1.z-20), new Vector3f(p3.x, p3.y, p3.z-20), new Vector3f(p4.x, p4.y, p4.z-20), new Vector3f(p5.x, p5.y, p5.z-20)},   
				{new Vector3f(p1.x, p1.y, p1.z-40), new Vector3f(p3.x, p3.y, p3.z-40), new Vector3f(p4.x, p4.y, p4.z-40), new Vector3f(p5.x, p5.y, p5.z-40)},   
				{new Vector3f(p1.x, p1.y, p1.z-60), new Vector3f(p3.x, p3.y, p3.z-60), new Vector3f(p4.x, p4.y, p4.z-60), new Vector3f(p5.x, p5.y, p5.z-60)}
		};
		
		ISurface surf1 = new SurfaceImpl("my mesh", array3, 16);
		//surfaces.add(surf1);
		
		p1 = new Vector3f(-20,0,20);
		p2 = new Vector3f(-29,-5,20);
		p3 = new Vector3f(-20,-10,20);
		
		Vector3f[][] array4 = {{p1, p2, p2, p3},
				{new Vector3f(p1.x, p1.y, p1.z-20), new Vector3f(p2.x, p2.y, p2.z-20), new Vector3f(p2.x, p2.y, p2.z-20), new Vector3f(p3.x, p3.y, p3.z-20)},   
				{new Vector3f(p1.x, p1.y, p1.z-40), new Vector3f(p2.x, p2.y, p2.z-40), new Vector3f(p2.x, p2.y, p2.z-40), new Vector3f(p3.x, p3.y, p3.z-40)},   
				{new Vector3f(p1.x, p1.y, p1.z-60), new Vector3f(p2.x, p2.y, p2.z-60), new Vector3f(p2.x, p2.y, p2.z-60), new Vector3f(p3.x, p3.y, p3.z-60)}
		};

		ISurface surf2 = new SurfaceImpl("my mesh", array4, 16);
		//surfaces.add(surf2);
		
		
		IMesh mesh = new MeshImpl();
		//mesh.loadMaxFile("etc/mito/mito_outer.3ds");
		mesh.loadObjFile("etc/mito/mito_outer.obj");
		mesh.setPosition(new PositionImpl(-10, -5, 20));
		mesh.setRotation(new RotationImpl(FastMath.DEG_TO_RAD*90, Vector3f.UNIT_X));
		mesh.setScale(0.0005f);
		//meshes.add(mesh);
		
		changed();
	}

	public void save() {
		Instances ins = new Instances();
		Cells thecells = new Cells();
		for (INeuronMorphology c : this.cells) {
			CellInstance ci = new CellInstance();
			Point loc = new Point();
			loc.setX(c.getPosition().getX());
			loc.setY(c.getPosition().getY());
			loc.setZ(c.getPosition().getZ());
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
	
	public ArrayList<ISlide> getSlides() {
		return slides;
	}
	
	public Set<INeuronMorphology> getCells() {
		return cells;
	}

	 public void changed () {       
		  setChanged();                 
		  notifyObservers();            
	 }

	public Set<ICurve> getCurves() {
		return this.curves;
	}

	public Set<ISurface> getSurfaces() {
		return this.surfaces;
	}

	public Set<IMesh> getMeshes() {
		return this.meshes;
	}

	public Set<IVolume> getVolumes() {
		return this.volumes;
	}

	
}
