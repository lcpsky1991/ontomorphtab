package edu.ucsd.ccdb.ontomorph2.view;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import com.jme.bounding.BoundingSphere;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Geometry;
import com.jme.scene.Line;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.lod.AreaClodMesh;
import com.jme.scene.shape.Cylinder;
import com.jme.scene.state.LightState;
import com.jme.scene.state.RenderState;
import com.jme.system.DisplaySystem;
import com.jme.util.AreaUtils;
import com.jme.util.geom.BufferUtils;

import edu.ucsd.ccdb.ontomorph2.core.IMorphology;
import edu.ucsd.ccdb.ontomorph2.core.IPosition;
import edu.ucsd.ccdb.ontomorph2.core.IRotation;
import edu.ucsd.ccdb.ontomorph2.core.ISegment;
import edu.ucsd.ccdb.ontomorph2.core.SegmentImpl;
import edu.ucsd.ccdb.ontomorph2.util.X3DLoader;
import edu.ucsd.ccdb.ontomorph2.util.XSLTransformManager;

public class Structure3DImpl extends Node implements IStructure3D {
	
	HashMap<ISegment, Geometry> segmentToGeom = new HashMap();
	
    private static final Logger logger = Logger.getLogger(AreaClodMesh.class
            .getName());

	private float trisPerPixel = 1f;

	private float distTolerance = 1f;

	private float lastDistance = 0f;
	
	int targetRecord = 1;
	
	IMorphology currentMorph = null;
	
	public Structure3DImpl(IMorphology morph) {
		currentMorph = morph;
		//if (morph.getRenderOption().equals(IMorphology.RENDER_AS_CYLINDERS)) {
			this.setMorphMLNeuron(this.loadscene(morph), morph.getPosition(), morph.getRotation(), morph.getScale());
		//} else if (morph.getRenderOption().equals(IMorphology.RENDER_AS_LINES)) {
			//InputStream input = XSLTransformManager.getInstance().convertMorphMLToX3D(morph.getMorphMLURL());
			//this.setX3DNeuron(input, morph.getPosition(), morph.getRotation(), morph.getScale());
		//}
			updateModelBound();
	}

	public Node getNode() {
		return this;
	}
	
	public void setMorphMLNeuron(Node n, IPosition _position, IRotation _rotation, float _scale) {
		this.detachAllChildren();
		this.attachChild(n);
		
		if (_position != null) {
			this.setLocalTranslation(_position.asVector3f());
		}
		if (_rotation != null) {
			this.setLocalRotation(_rotation.asQuaternion());
		}
		if (_scale != 1) {
			this.setLocalScale(_scale);
		}
	}
	
	public void setX3DNeuron(InputStream input, IPosition _position, IRotation _rotation, float _scale) {
		try {
			X3DLoader converter = new X3DLoader();
			Spatial scene = converter.loadScene(input, null, null);
			
			this.detachAllChildren();
			this.attachChild(scene);
			
			if (_position != null) {
				this.setLocalTranslation(_position.asVector3f());
			}
			if (_rotation != null) {
				this.setLocalRotation(_rotation.asQuaternion());
			}
			if (_scale != 1) {
				this.setLocalScale(_scale);
			}
		} catch (Exception e) {
			//logger.logp(Level.SEVERE, this.getClass().toString(), "simpleInitGame()", "Exception", e);
			System.exit(0);
		}
	}
	
	public void setX3DNeuron(URL structureLoc) {
		try {
			this.setX3DNeuron(new FileInputStream(structureLoc.getFile()), null, null, 1);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//needs to be updated by a controller
	//does not unselect yet
	public void updateSelectedSegments(List<ISegment> segments) {
		for (ISegment seg : segments) {
			Geometry g = segmentToGeom.get(seg);
			g.setColorBuffer(1, BufferUtils.createFloatBuffer(ColorRGBA.red.getColorArray()));
		}
	}
	
	/**
	 * Provides segments with an arbitrary degradation.
	 * Don't use for values other than 1 or Integer.MAX_VALUE
	 * 
	 * @param morph - Morphology file you want segments from
	 * @param numberOfSegsPerGroup - number of desired segments.  For full resolution, use Integer.MAX_VALUE
	 * @return
	 */
	private List<ISegment> getSegments(IMorphology morph, int numberOfSegsPerGroup) {
		assert (numberOfSegsPerGroup == 1 || numberOfSegsPerGroup == Integer.MAX_VALUE);
		if (numberOfSegsPerGroup == Integer.MAX_VALUE) {
			 return morph.getSegments();
		} 
		ArrayList<ISegment> segments = new ArrayList<ISegment>();
		for (edu.ucsd.ccdb.ontomorph2.core.ISegmentGroup sg : morph.getSegmentGroups()) {
			if (sg.getSegments().size() <= numberOfSegsPerGroup ){
				segments.addAll(sg.getSegments());
			} else if (numberOfSegsPerGroup == 1){
				
				ISegment firstSeg = sg.getSegments().get(0);
				float[] proximalPoint = firstSeg.getProximalPoint();
				float proximalRadius = firstSeg.getProximalRadius();
				ISegment lastSeg = sg.getSegments().get(sg.getSegments().size() - 1);
				float[] distalPoint = lastSeg.getDistalPoint();
				float distalRadius = lastSeg.getDistalRadius();
				
				
				SegmentImpl singleSegment = new SegmentImpl(null, proximalPoint, distalPoint, 
						proximalRadius, distalRadius, null);
				
				segments.add(singleSegment);
			} /*else {
				int cutIncrement = (int)Math.rint(numberOfSegsPerGroup / (sg.getSegments().size()+1));
				for (int i = 0; i < numberOfSegsPerGroup-1; i++) {
					sg.getSegments().get(cutIncrement*i);
				}
			} */
		}
		return segments;
	}
	
	public Node loadscene(IMorphology morph) {
		Node sceneRoot = new Node();
		 /* 
         * Check the LightState. If none has been passed, create a new one and
         * attach it to the scene root
         */ 
		LightState lightState = null;
		lightState = DisplaySystem.getDisplaySystem().getRenderer().createLightState();
        lightState.setEnabled(true);	
        sceneRoot.setRenderState(lightState);
        
        for (ISegment seg : this.getSegments(morph, targetRecord)) {
						        	
        	Vector3f base = new Vector3f();
        	base.x = seg.getProximalPoint()[0];
        	base.y = seg.getProximalPoint()[1];
        	base.z = seg.getProximalPoint()[2];
        	Vector3f apex = new Vector3f();
        	apex.x = seg.getDistalPoint()[0];
        	apex.y = seg.getDistalPoint()[1];
        	apex.z = seg.getDistalPoint()[2];
        	
        	/*
        	 Sphere s1 = new Sphere("my sphere", 10, 10, 0.5f);
        	 s1.setLocalTranslation(base);
        	 s1.setRandomColors();
        	 Sphere s2 = new Sphere("my 2nd sphere", 10, 10, 0.5f);
        	 s2.setLocalTranslation(apex);
        	 s2.setRandomColors();
        	 sceneRoot.attachChild(s1);
        	 sceneRoot.attachChild(s2);
        	 */
            
        	ColorRGBA defaultColor = ColorRGBA.red;
        	
        	float[] colorValues2 = {defaultColor.r, defaultColor.g, defaultColor.b, defaultColor.a, 
              		defaultColor.r, defaultColor.g, defaultColor.b, defaultColor.a};
        	FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(colorValues2);
        	
        	if (morph.getRenderOption().equals(IMorphology.RENDER_AS_LINES)) {
        		
        		float[] vertices = {apex.x, apex.y, apex.z, base.x, base.y, base.z};
        		
              
        		
        		Line l = new Line("my Line", BufferUtils.createFloatBuffer(vertices), null, colorBuffer, null);
        		l.updateModelBound();
        		sceneRoot.attachChild(l);						
        	
        	} else if (morph.getRenderOption().equals(IMorphology.RENDER_AS_CYLINDERS)) {
        		
            	//calculate new center
            	float xCenter = (float)((apex.x - base.x)/2 + base.x);
            	float yCenter = (float)((apex.y - base.y)/2 + base.y);
            	float zCenter = (float)((apex.z - base.z)/2 + base.z);
            	
            	Vector3f center = new Vector3f(xCenter, yCenter, zCenter);
            	
            	Vector3f unit = new Vector3f();
            	unit = apex.subtract(base); // unit = apex - base;
            	float height = unit.length();
            	unit = unit.normalize();
            	
        		Cylinder cyl = new Cylinder("neuron_cyl", 2, 4, 0.5f, height);
        		cyl.setRadius1(seg.getProximalRadius());
        		cyl.setRadius2(seg.getDistalRadius());
        		//cyl.setColorBuffer(2, colorBuffer);
        		cyl.updateModelBound();
        		
        		Quaternion q = new Quaternion();
        		q.lookAt(unit, Vector3f.UNIT_Y);
        		
        		cyl.setLocalRotation(q);
        		
        		cyl.setLocalTranslation(center);
        		cyl.setRandomColors();
        		
        		//cyl.setLocalScale(scale);
        		segmentToGeom.put(seg, cyl);
        		
        		sceneRoot.attachChild(cyl); 
        	}

        }
        
    	if (morph.getRenderOption().equals(IMorphology.RENDER_AS_CYLINDERS)) {
    		sceneRoot = getClodNodeFromParent(sceneRoot);
    	}
    	return sceneRoot;
	}
	
	 /**
	   * Called during rendering.  Should not be called directly.
	   * @param r The renderer to draw this TriMesh with.
	   */
	/*
	  public void draw(Renderer r) {
	    selectLevelOfDetail(r);
	    super.draw(r);
	  }*/
	  
	  public void selectLevelOfDetail(Renderer r) {
		  int target = chooseTargetRecord(r);
		  if (target == 0) {
			  if (targetRecord == Integer.MAX_VALUE) {
				  reload();
			  }
		  } else if (target == Integer.MAX_VALUE){
			 if (targetRecord == 1) {
				 reload();
			 }
		  }
	  }
	  
	  public void reload() {
		  this.detachAllChildren();
		  this.attachChild(loadscene(currentMorph));
		  updateModelBound();
	  }
	
	  /**
		 * This function is used during rendering to choose the correct target record for the
		 * AreaClodMesh acording to the information in the renderer.  This should not be called
		 * manually.  Instead, allow it to be called automatically during rendering.
		 * @param r The Renderer to use.
		 * @return the target record this AreaClodMesh will use to collapse vertexes.
		 */
		public int chooseTargetRecord(Renderer r) {
			if (this.getWorldBound() == null) {
				logger.warning("Structure3DImpl found with no Bounds.");
				return 0;
			}

			float newDistance = getWorldBound().distanceTo(
					r.getCamera().getLocation());
			if (Math.abs(newDistance - lastDistance) <= distTolerance)
				return targetRecord; // we haven't moved relative to the model, send the old measurement back.
			if (lastDistance > newDistance && targetRecord == 1)
				return targetRecord; // we're already at the lowest setting and we just got closer to the model, no need to keep trying.
			if (lastDistance < newDistance && targetRecord == Integer.MAX_VALUE)
				return targetRecord; // we're already at the highest setting and we just got further from the model, no need to keep trying.

			lastDistance = newDistance;

			// estimate area of polygon via bounding volume
			float area = AreaUtils.calcScreenArea(getWorldBound(), lastDistance, r
					.getWidth());
			if (area > (640*480/2)) {
				targetRecord = Integer.MAX_VALUE;
			} else {
				targetRecord = 1;
			}
			return targetRecord;
		}
	  
	private Node getClodNodeFromParent(Node meshParent) {
	    // Create a node to hold my cLOD mesh objects
	    Node clodNode = new Node("Clod node");
	    // For each mesh in maggie
	    for (int i = 0; i < meshParent.getQuantity(); i++) {
	        // Create an AreaClodMesh for that mesh. Let it compute
	        // records automatically
	        AreaClodMesh acm = new AreaClodMesh("part" + i,
	                (TriMesh) meshParent.getChild(i), null);
	        acm.setLocalTranslation(meshParent.getChild(i).getLocalTranslation());
	        acm.setLocalRotation(meshParent.getChild(i).getLocalRotation());
	        acm.setModelBound(new BoundingSphere());
	        acm.updateModelBound();
	        // Allow 1/2 of a triangle in every pixel on the screen in
	        // the bounds.
	        acm.setTrisPerPixel(.5f);
	        // Force a move of 2 units before updating the mesh geometry
	        acm.setDistanceTolerance(2);
	        // Give the clodMe sh node the material state that the
	        // original had.
	        //acm.setRenderState(meshParent.getChild(i).getRenderStateList()[RenderState.RS_MATERIAL]); //Note: Deprecated
	        acm.setRenderState(meshParent.getChild(i).getRenderState(RenderState.RS_MATERIAL));
	        // Attach clod node.
	        clodNode.attachChild(acm);
	    }
	    return clodNode;
	}

}
