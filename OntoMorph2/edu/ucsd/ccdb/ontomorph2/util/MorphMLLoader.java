package edu.ucsd.ccdb.ontomorph2.util;

import java.io.File;
import java.net.URL;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.Node;
import com.jme.scene.shape.Cylinder;
import com.jme.scene.state.LightState;
import com.jme.system.DisplaySystem;

import neuroml.generated.NeuroMLLevel2;
import neuroml.generated.Point;
import neuroml.generated.Segment;
import neuroml.generated.Cell.Segments;
import neuroml.generated.NeuroMLLevel2.Cells;
import neuroml.generated.Level2Cell;

public class MorphMLLoader {
	
	public Node loadscene(URL filename) {
		JAXBContext context;
		Node sceneRoot = new Node();
		 /* 
         * Check the LightState. If none has been passed, create a new one and
         * attach it to the scene root
         */ 
		LightState lightState = null;
		lightState = DisplaySystem.getDisplaySystem().getRenderer().createLightState();
        lightState.setEnabled(true);	
        sceneRoot.setRenderState(lightState);
        
		try {
			context = JAXBContext.newInstance("neuroml.generated");
			Unmarshaller unmarshaller = context.createUnmarshaller();
			JAXBElement o = (JAXBElement)unmarshaller.unmarshal(new File(filename.getFile()));
			NeuroMLLevel2 neuroml = (NeuroMLLevel2)o.getValue();
			
			Cells c = neuroml.getCells();
			for (Level2Cell x : c.getCell()){
				List<Segments> segments  = x.getSegments();
				for (Segments s : segments) {
					Point p1 = null;
					Point p2 = null;
					for (Segment seg : s.getSegment()) {

						if (seg.getProximal() != null) {
							p1 = seg.getProximal();
						} else {
							p1 = p2;
						}
						p2 = seg.getDistal();
						
						double xDiff = p2.getX() - p1.getX();
						double yDiff = p2.getY() - p1.getY();
						double zDiff = p2.getZ() - p1.getZ();
						float scale = 20f;
						
						
						float height = (float)Math.sqrt(Math.pow(xDiff, 2) + Math.pow(yDiff,2) + Math.pow(zDiff,2));
						Cylinder cyl = new Cylinder("neuron_cyl", 6, 6, 3f, height);
						cyl.setRadius1(p1.getDiameter().floatValue());
						cyl.setRadius2(p2.getDiameter().floatValue());
						
						cyl.setLocalTranslation((float)(xDiff*scale)/2, (float)(yDiff*scale)/2, (float)(zDiff*scale)/2);
						float rot1 = (float) Math.atan(zDiff/xDiff);
						float rot2 = (float) Math.atan(yDiff/xDiff);
						Quaternion q = new Quaternion().fromAngleAxis(rot1, new Vector3f(0, 0, 1)).fromAngleAxis(rot2, new Vector3f(0, 1, 0));
						cyl.setLocalRotation(q);
						cyl.setRandomColors();
						cyl.setLocalScale(scale);
						sceneRoot.attachChild(cyl);
					}
				}
			}
			
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return sceneRoot;
	}

}
