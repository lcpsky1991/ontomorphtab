package edu.ucsd.ccdb.ontomorph2.view.scene;

import java.awt.Color;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;



import com.jme.light.LightNode;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Cylinder;
import com.jme.scene.state.GLSLShaderObjectsState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;

import edu.ucsd.ccdb.ontomorph2.core.tangible.Curve3D;
import edu.ucsd.ccdb.ontomorph2.core.tangible.CurveAnchorPoint;
import edu.ucsd.ccdb.ontomorph2.util.ColorUtil;
import edu.ucsd.ccdb.ontomorph2.view.View;

/**
 *	The viewgraph for a NeuronMorphology's axon (which is really just a {@link Curve3D}
 * @author caprea
 *
 */


public class AxonView extends CurveView 
{

	Node partsNode = new Node();
	
	
	public AxonView(Curve3D curve) 
	{
		super(curve);
		this.pickPriority = (P_MEDIUM);
	}
	
	/**
	 * In addition to the update a Curve gets, an AxonView also has thickness represented by cylinders
	 */
	public void update() 
	{
		super.update();
	
		
		//+++ detach all the segments
		partsNode.detachAllChildren();
		
		//------------
		//+++ rettach all the segments
		Curve3D model = (Curve3D) this.getModel();
		
		//attach a cylinder for each control point
		
		List points = model.getAnchorPoints();
		
		ArrayList<Geometry> segments = new ArrayList<Geometry>();		//This is a VIEW of the axon as segments(cylinders)
		segments.clear();
		
		for (int i=0; i < points.size() -1 ; i++)	//the loop condition is for all points, minus one point
		{
			CurveAnchorPoint current = (CurveAnchorPoint) points.get(i);
			CurveAnchorPoint next = (CurveAnchorPoint) points.get(i+1);
			Vector3f distBetween = next.getPosition().subtract(current.getPosition());
			Vector3f middle = current.getPosition().add(distBetween.divide(2));
			float height = distBetween.length();
			
			Cylinder part = new Cylinder("segment", 50, 50, 1, height);
			part.setLocalTranslation(middle);
			part.lookAt(next.getPosition(), Vector3f.UNIT_Y);
			segments.add(part);
			
			partsNode.attachChild(part);
		}

		//------------
		//+++ register the geomtries of the segments
		//this.registerGeometries(segments);  
		
		
		//+++ attach the segments to the node
		this.attachChild(partsNode);
		
		//make the segments visually appealing
		partsNode.setLocalScale(1f);
		
		
        //where
		try
		{
			partsNode.clearRenderState(0);
			GLSLShaderObjectsState xray = DisplaySystem.getDisplaySystem().getRenderer().createGLSLShaderObjectsState();
	        			
	        URL frag = this.getClass().getResource("xray.frag");
	        URL vert = this.getClass().getResource("xray.vert");
	        
	        xray.load(vert,  frag);  
	        xray.setEnabled(true);
	        xray.setUniform("edgefalloff", 1f);

	        partsNode.setLightCombineMode(LightState.COMBINE_FIRST);
	        partsNode.setRenderState(xray);
		}
        catch (Exception e) 
        {
        	System.out.println("Xray failed"); 
        	e.printStackTrace();
		}
		//-----------------------------
        
		//+++ update the graphics
        
		this.updateRenderState();
		this.updateGeometricState(5f, true);
		this.updateWorldBound();
		this.updateModelBound();
	}
		
	
	
}
