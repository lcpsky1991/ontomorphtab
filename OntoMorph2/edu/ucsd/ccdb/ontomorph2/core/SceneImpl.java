package edu.ucsd.ccdb.ontomorph2.core;

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
import com.jme.math.Vector3f;

public class SceneImpl extends Observable implements IScene {

	ArrayList<ISlide> slides = null;
	Set<ICell> cells = null;
	Set<ICurve> curves = null;
	Set<ISurface> surfaces = null;
	
	public SceneImpl() {
		slides = new ArrayList<ISlide>();
		cells = new HashSet<ICell>();
		curves = new HashSet<ICurve>();
		surfaces = new HashSet<ISurface>();
	}
	
	public void load() {
		//temporary hack to load a mockup
		
		URL sliceLoc = SceneImpl.class.getClassLoader().getResource("slice.jpg");
		SlideImpl slide1 = new SlideImpl(sliceLoc, null, null);
		SlideImpl slide2 = new SlideImpl(sliceLoc, new PositionImpl(0,0,2), null);
		slides.add(slide1);
		slides.add(slide2);
		
		
		/*
		ICell cell1 = new CellImpl();
		URL cell1URL = SceneImpl.class.getClassLoader().getResource("1220882a.morph.xml");
		cell1.setMorphologyViaURL(cell1URL);
		cell1.getMorphology().setPosition(new PositionImpl(6,-30,106));
		cell1.getMorphology().setRotation(new RotationImpl(FastMath.DEG_TO_RAD*-90, new Vector3f(0,1,0)));
		cell1.getMorphology().setScale(0.15f);
		cell1.getMorphology().setRenderOption(IMorphology.RENDER_AS_CYLINDERS);
		cells.add(cell1);
		
		ICell cell2 = new CellImpl();
		URL cell2URL = SceneImpl.class.getClassLoader().getResource("1220882a.morph.xml");
		cell2.setMorphologyViaURL(cell1URL);
		cell2.getMorphology().setPosition(new PositionImpl(6,6,106));
		//cell2.getMorphology().setRotation(new RotationImpl(FastMath.DEG_TO_RAD*-90, new Vector3f(0,1,0)));
		cell2.getMorphology().setScale(0.15f);
		cell2.getMorphology().setRenderOption(IMorphology.RENDER_AS_LINES);

		//get semantic thing for a pyramidal cell
		//ISemanticThing pyramCell = SemanticRepository.getInstance().getSemanticThing("sao:sao830368389");
		//cell2.setSemanticThing(pyramCell);
		cells.add(cell2);
		*/
		
		Vector3f p1 = new Vector3f(-20,0,20);
		Vector3f p2 = new Vector3f(-34,-5,20);
		Vector3f p3 = new Vector3f(-20,-10,20);
		Vector3f[] array = {p1, p2, p3};
		ICurve curve1 = new CurveImpl("Dentate Gyrus", array);
		curve1.setColor(Color.BLUE);
		this.curves.add(curve1);
		
		p1 = new Vector3f(-10,-5,20);
		p2 = new Vector3f(3,-9,20);
		p3 = new Vector3f(7,0,20);
		Vector3f p4 = new Vector3f(-9,20,20);
		Vector3f p5 = new Vector3f(-23,15,20);
		
		Vector3f[] array2 = {p1, p2, p3, p4, p5};
		CurveImpl c2 = new CurveImpl("CA",array2);
		c2.setColor(Color.BLUE);
		this.curves.add(c2);
		
		ICell cell3 = new CellImpl(); 
		URL cell3URL = SceneImpl.class.getClassLoader().getResource("etc/morphml/hippocampus/cell1zr.morph.xml");
		cell3.setMorphologyViaURL(cell3URL);
		cell3.getMorphology().setPosition(new PositionImpl(-10,-5,20));
		//cell3.getMorphology().setRotation(new RotationImpl(FastMath.DEG_TO_RAD*-90, new Vector3f(0,1,0)));
		cell3.getMorphology().setScale(0.01f);
		cell3.getMorphology().setRenderOption(IMorphology.RENDER_AS_CYLINDERS);
		//semantic thing for hippocampal CA1 neuron
		cell3.addSemanticThing(SemanticRepository.getInstance().getSemanticThing("nif_cell:nifext_158"));
		cells.add(cell3);
		
		ICell cell4 = new CellImpl(); 
		URL cell4URL = SceneImpl.class.getClassLoader().getResource("etc/morphml/hippocampus/cell2zr.morph.xml");
		cell4.setMorphologyViaURL(cell4URL);
		cell4.getMorphology().setPosition(new PositionImpl(0,-5,20));
		cell4.getMorphology().setRotation(new RotationImpl(FastMath.DEG_TO_RAD*45, Vector3f.UNIT_Z));
		cell4.getMorphology().setScale(0.01f);
		cell4.getMorphology().setRenderOption(IMorphology.RENDER_AS_CYLINDERS);
		cell4.addSemanticThing(SemanticRepository.getInstance().getSemanticThing("nif_cell:nifext_158"));
		cells.add(cell4);
		
		ICell cell5 = new CellImpl(); 
		URL cell5URL = SceneImpl.class.getClassLoader().getResource("etc/morphml/hippocampus/cell6zr.morph.xml");
		cell5.setMorphologyViaURL(cell5URL);
		cell5.getMorphology().setPosition(new PositionImpl(0,0,20));
		cell5.getMorphology().setRotation(new RotationImpl(FastMath.DEG_TO_RAD*80, Vector3f.UNIT_Z));
		cell5.getMorphology().setScale(0.01f);
		cell5.getMorphology().setRenderOption(IMorphology.RENDER_AS_CYLINDERS);
		cell5.addSemanticThing(SemanticRepository.getInstance().getSemanticThing("nif_cell:nifext_158"));
		cells.add(cell5);
		
		
		ICell cell6 = new CellImpl(); 
		URL cell6URL = SceneImpl.class.getClassLoader().getResource("etc/morphml/hippocampus/pc1c.morph.xml");
		cell6.setMorphologyViaURL(cell6URL);
		cell6.getMorphology().setPosition(new PositionImpl(-12,15,20));
		cell6.getMorphology().setRotation(new RotationImpl(FastMath.DEG_TO_RAD*-90, Vector3f.UNIT_Z));
		cell6.getMorphology().setScale(0.02f);
		cell6.getMorphology().setRenderOption(IMorphology.RENDER_AS_CYLINDERS);
		cell6.addSemanticThing(SemanticRepository.getInstance().getSemanticThing("nif_cell:nifext_157"));
		cells.add(cell6);
		
		
		ICell cell7 = new CellImpl(); 
		URL cell7URL = SceneImpl.class.getClassLoader().getResource("etc/morphml/hippocampus/pc2a.morph.xml");
		cell7.setMorphologyViaURL(cell7URL);
		cell7.getMorphology().setPosition(new PositionImpl(-23,15,20));
		cell7.getMorphology().setRotation(new RotationImpl(FastMath.DEG_TO_RAD*-90, Vector3f.UNIT_Z));
		cell7.getMorphology().setScale(0.02f);
		cell7.getMorphology().setRenderOption(IMorphology.RENDER_AS_CYLINDERS);
		cell7.addSemanticThing(SemanticRepository.getInstance().getSemanticThing("nif_cell:nifext_157"));
		cells.add(cell7);
		
		
		/*
		ICell cell8 = new CellImpl(); 
		URL cell8URL = SceneImpl.class.getClassLoader().getResource("etc/morphml/hippocampus/cb27e.morph.xml");
		cell8.setMorphologyViaURL(cell8URL);
		cell8.getMorphology().setPosition(new PositionImpl(-10,0,20));
		//cell3.getMorphology().setRotation(new RotationImpl(FastMath.DEG_TO_RAD*-90, new Vector3f(0,1,0)));
		cell8.getMorphology().setScale(0.01f);
		cell8.getMorphology().setRenderOption(IMorphology.RENDER_AS_LINES);
		cells.add(cell8);
		
		ICell cell9 = new CellImpl(); 
		URL cell9URL = SceneImpl.class.getClassLoader().getResource("etc/morphml/hippocampus/cb27g.morph.xml");
		cell9.setMorphologyViaURL(cell9URL);
		cell9.getMorphology().setPosition(new PositionImpl(-15,0,20));
		//cell3.getMorphology().setRotation(new RotationImpl(FastMath.DEG_TO_RAD*-90, new Vector3f(0,1,0)));
		cell9.getMorphology().setScale(0.01f);
		cell9.getMorphology().setRenderOption(IMorphology.RENDER_AS_LINES);
		cells.add(cell9);
		
		ICell cell10 = new CellImpl(); 
		URL cell10URL = SceneImpl.class.getClassLoader().getResource("etc/morphml/hippocampus/cd1152.morph.xml");
		cell10.setMorphologyViaURL(cell10URL);
		cell10.getMorphology().setPosition(new PositionImpl(20,0,20));
		//cell3.getMorphology().setRotation(new RotationImpl(FastMath.DEG_TO_RAD*-90, new Vector3f(0,1,0)));
		cell10.getMorphology().setScale(0.01f);
		cell10.getMorphology().setRenderOption(IMorphology.RENDER_AS_LINES);
		cells.add(cell10); */
		

		Vector3f point0 = ((CurveImpl)curve1).getPoint(0);
		Vector3f pointhalf = ((CurveImpl)curve1).getPoint(0.5f);
		Vector3f point1 = ((CurveImpl)curve1).getPoint(0.99f);
		
		ICell cell11 = new CellImpl(); 
		URL cell11URL = SceneImpl.class.getClassLoader().getResource("etc/morphml/hippocampus/5199202a.morph.xml");
		cell11.setMorphologyViaURL(cell11URL);
		cell11.getMorphology().setPosition(new PositionImpl(point0.x,point0.y,point0.z));
		//cell11.getMorphology().setRotation(new RotationImpl(FastMath.DEG_TO_RAD*-90,Vector3f.UNIT_Z));
		cell11.getMorphology().setScale(0.01f);
		cell11.getMorphology().setRenderOption(IMorphology.RENDER_AS_CYLINDERS);
		cell11.addSemanticThing(SemanticRepository.getInstance().getSemanticThing("nif_cell:nifext_153"));
		cells.add(cell11);
		
		ICell cell11a = new CellImpl(); 
		cell11a.setMorphologyViaURL(cell11URL);
		cell11a.getMorphology().setPosition(new PositionImpl(pointhalf.x,pointhalf.y,pointhalf.z));
		cell11a.getMorphology().setRotation(new RotationImpl(FastMath.DEG_TO_RAD*90,Vector3f.UNIT_Z));
		cell11a.getMorphology().setScale(0.01f);
		cell11a.getMorphology().setRenderOption(IMorphology.RENDER_AS_CYLINDERS);
		cell11a.addSemanticThing(SemanticRepository.getInstance().getSemanticThing("nif_cell:nifext_153"));
		cells.add(cell11a);
		
		ICell cell11b = new CellImpl(); 
		cell11b.setMorphologyViaURL(cell11URL);
		cell11b.getMorphology().setPosition(new PositionImpl(point1.x,point1.y,point1.z));
		//cell11b.getMorphology().setPosition(new PositionImpl(-20,-10,20));
		cell11b.getMorphology().setRotation(new RotationImpl(FastMath.DEG_TO_RAD*180,Vector3f.UNIT_Z));
		cell11b.getMorphology().setScale(0.01f);
		cell11b.getMorphology().setRenderOption(IMorphology.RENDER_AS_CYLINDERS);
		cell11b.addSemanticThing(SemanticRepository.getInstance().getSemanticThing("nif_cell:nifext_153"));
		cells.add(cell11b);
		
		/*
		ICell cell12 = new CellImpl(); 
		URL cell12URL = SceneImpl.class.getClassLoader().getResource("etc/morphml/hippocampus/pv08d.morph.xml");
		cell12.setMorphologyViaURL(cell12URL);
		cell12.getMorphology().setPosition(new PositionImpl(-25,0,20));
		//cell3.getMorphology().setRotation(new RotationImpl(FastMath.DEG_TO_RAD*-90, new Vector3f(0,1,0)));
		cell12.getMorphology().setScale(0.01f);
		cell12.getMorphology().setRenderOption(IMorphology.RENDER_AS_LINES);
		cells.add(cell12);
		
		ICell cell13 = new CellImpl(); 
		URL cell13URL = SceneImpl.class.getClassLoader().getResource("etc/morphml/hippocampus/pv22b.morph.xml");
		cell13.setMorphologyViaURL(cell13URL);
		cell13.getMorphology().setPosition(new PositionImpl(25,0,20));
		//cell3.getMorphology().setRotation(new RotationImpl(FastMath.DEG_TO_RAD*-90, new Vector3f(0,1,0)));
		cell13.getMorphology().setScale(0.01f);
		cell13.getMorphology().setRenderOption(IMorphology.RENDER_AS_LINES);
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
		
		changed();
	}

	public void save() {
		Instances ins = new Instances();
		Cells thecells = new Cells();
		for (ICell c : this.cells) {
			CellInstance ci = new CellInstance();
			Point loc = new Point();
			loc.setX(c.getMorphology().getPosition().getX());
			loc.setY(c.getMorphology().getPosition().getY());
			loc.setZ(c.getMorphology().getPosition().getZ());
			ci.setLocation(loc);
			//ci.setId(c.getMorphology().get)
			ins.getInstance().add(ci);
			
			thecells.getCell().add(((MorphologyImpl)c).getMorphMLCell());
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
	
	public Set<ICell> getCells() {
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
	
}
