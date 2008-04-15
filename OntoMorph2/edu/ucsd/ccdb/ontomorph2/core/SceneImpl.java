package edu.ucsd.ccdb.ontomorph2.core;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Observable;
import java.util.Set;

import com.jme.math.FastMath;
import com.jme.math.Vector3f;

import edu.ucsd.ccdb.ontomorph2.view.ViewImpl;

public class SceneImpl extends Observable implements IScene {

	ArrayList<ISlide> slides = null;
	Set<ICell> cells = null;
	
	public SceneImpl() {
		slides = new ArrayList<ISlide>();
		cells = new HashSet<ICell>();
	}
	
	public void load() {
		//temporary hack to load a mockup
		URL sliceLoc = SceneImpl.class.getClassLoader().getResource("slice.jpg");
		SlideImpl slide1 = new SlideImpl(sliceLoc, null, null);
		SlideImpl slide2 = new SlideImpl(sliceLoc, new PositionImpl(0,0,2), null);
		slides.add(slide1);
		slides.add(slide2);
		
		
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
		
		/*
		//get semantic thing for a pyramidal cell
		ISemanticThing pyramCell = SemanticRepository.getInstance().getSemanticThing("sao:sao830368389");
		cell2.setSemanticThing(pyramCell);
		*/
		
		cells.add(cell2);
		
		ICell cell3 = new CellImpl(); 
		URL cell3URL = SceneImpl.class.getClassLoader().getResource("etc/morphml/hippocampus/cell1zr.morph.xml");
		cell3.setMorphologyViaURL(cell3URL);
		cell3.getMorphology().setPosition(new PositionImpl(-10,-5,20));
		//cell3.getMorphology().setRotation(new RotationImpl(FastMath.DEG_TO_RAD*-90, new Vector3f(0,1,0)));
		cell3.getMorphology().setScale(0.01f);
		cell3.getMorphology().setRenderOption(IMorphology.RENDER_AS_LINES);
		cells.add(cell3);
		
		ICell cell4 = new CellImpl(); 
		URL cell4URL = SceneImpl.class.getClassLoader().getResource("etc/morphml/hippocampus/cell2zr.morph.xml");
		cell4.setMorphologyViaURL(cell4URL);
		cell4.getMorphology().setPosition(new PositionImpl(0,-5,20));
		cell4.getMorphology().setRotation(new RotationImpl(FastMath.DEG_TO_RAD*45, Vector3f.UNIT_Z));
		cell4.getMorphology().setScale(0.01f);
		cell4.getMorphology().setRenderOption(IMorphology.RENDER_AS_LINES);
		cells.add(cell4);
		
		ICell cell5 = new CellImpl(); 
		URL cell5URL = SceneImpl.class.getClassLoader().getResource("etc/morphml/hippocampus/cell6zr.morph.xml");
		cell5.setMorphologyViaURL(cell5URL);
		cell5.getMorphology().setPosition(new PositionImpl(0,0,20));
		cell5.getMorphology().setRotation(new RotationImpl(FastMath.DEG_TO_RAD*80, Vector3f.UNIT_Z));
		cell5.getMorphology().setScale(0.01f);
		cell5.getMorphology().setRenderOption(IMorphology.RENDER_AS_LINES);
		cells.add(cell5);
		
		
		ICell cell6 = new CellImpl(); 
		URL cell6URL = SceneImpl.class.getClassLoader().getResource("etc/morphml/hippocampus/pc1c.morph.xml");
		cell6.setMorphologyViaURL(cell6URL);
		cell6.getMorphology().setPosition(new PositionImpl(-12,15,20));
		cell6.getMorphology().setRotation(new RotationImpl(FastMath.DEG_TO_RAD*-90, Vector3f.UNIT_Z));
		cell6.getMorphology().setScale(0.02f);
		cell6.getMorphology().setRenderOption(IMorphology.RENDER_AS_CYLINDERS);
		cells.add(cell6);
		
		
		ICell cell7 = new CellImpl(); 
		URL cell7URL = SceneImpl.class.getClassLoader().getResource("etc/morphml/hippocampus/pc2a.morph.xml");
		cell7.setMorphologyViaURL(cell7URL);
		cell7.getMorphology().setPosition(new PositionImpl(-23,15,20));
		cell7.getMorphology().setRotation(new RotationImpl(FastMath.DEG_TO_RAD*-90, Vector3f.UNIT_Z));
		cell7.getMorphology().setScale(0.02f);
		cell7.getMorphology().setRenderOption(IMorphology.RENDER_AS_CYLINDERS);
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
		
		ICell cell11 = new CellImpl(); 
		URL cell11URL = SceneImpl.class.getClassLoader().getResource("etc/morphml/hippocampus/5199202a.morph.xml");
		cell11.setMorphologyViaURL(cell11URL);
		cell11.getMorphology().setPosition(new PositionImpl(-20,0,20));
		//cell11.getMorphology().setRotation(new RotationImpl(FastMath.DEG_TO_RAD*-90,Vector3f.UNIT_Z));
		cell11.getMorphology().setScale(0.01f);
		cell11.getMorphology().setRenderOption(IMorphology.RENDER_AS_CYLINDERS);
		cells.add(cell11);
		
		ICell cell11a = new CellImpl(); 
		cell11a.setMorphologyViaURL(cell11URL);
		cell11a.getMorphology().setPosition(new PositionImpl(-27,-5,20));
		cell11a.getMorphology().setRotation(new RotationImpl(FastMath.DEG_TO_RAD*90,Vector3f.UNIT_Z));
		cell11a.getMorphology().setScale(0.01f);
		cell11a.getMorphology().setRenderOption(IMorphology.RENDER_AS_CYLINDERS);
		cells.add(cell11a);
		
		ICell cell11b = new CellImpl(); 
		cell11b.setMorphologyViaURL(cell11URL);
		cell11b.getMorphology().setPosition(new PositionImpl(-20,-10,20));
		cell11b.getMorphology().setRotation(new RotationImpl(FastMath.DEG_TO_RAD*180,Vector3f.UNIT_Z));
		cell11b.getMorphology().setScale(0.01f);
		cell11b.getMorphology().setRenderOption(IMorphology.RENDER_AS_CYLINDERS);
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
		changed();
	}

	public void save() {
		// TODO Auto-generated method stub

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
	
}
