package edu.ucsd.ccdb.ontomorph2.view.scene;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.jme.curve.CurveController;
import com.jme.math.Vector3f;
import com.jme.renderer.Renderer;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.batch.GeomBatch;
import com.jme.scene.lod.AreaClodMesh;
import com.jme.scene.state.LightState;
import com.jme.system.DisplaySystem;
import com.jme.util.AreaUtils;

import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.ISegment;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.ICable;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.NeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.core.spatial.PositionVector;
import edu.ucsd.ccdb.ontomorph2.core.spatial.RotationVector;
import edu.ucsd.ccdb.ontomorph2.util.Log;
import edu.ucsd.ccdb.ontomorph2.util.X3DLoader;

/**
 * Visualizes a neuron morphology.  Describes the 3D structure 
 * of a biological object in a format that can be easily visualized in a 3D viewer (X3D?)
 * 
 * Need to implement selection handlers.  One click should select a segment, while a double-click
 * should select the whole cell
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 * @see NeuronMorphology
 *
 */
public class NeuronMorphologyView extends TangibleView{

	List<SegmentView> segViews;
	
    private static final Logger logger = Logger.getLogger(AreaClodMesh.class
            .getName());

	private float trisPerPixel = 1f;

	private float distTolerance = 1f;

	private float lastDistance = 0f;
	
	int cableResolution = 1;
	
	NeuronMorphology currentMorph = null;
	
	CurveController _cc = null;
	
	public NeuronMorphologyView(NeuronMorphology morph) {
		super(morph);
		super.setName("Neuron Morphology View");
		segViews = new ArrayList<SegmentView>();
		currentMorph = morph;
		this.setMorphMLNeuron(this.loadscene(morph), morph);
	}

	public Node getNode() {
		return this;
	}
	
	/**
	 * Return any SegmentView that is represented by this GeomBatch
	 * 
	 * @param gb
	 * @return a SegmentView, if this is what gb represents.  return null if it does not.
	 */
	public SegmentView getSegmentFromGeomBatch(GeomBatch gb) {
		Geometry g = gb.getParentGeom();
		SegmentView pick = null;
		for (SegmentView sv : this.segViews) {
			
			if (sv.containsCurrentGeometry(g)) {
				pick = sv;
			}
		}
		return pick;
	}
	
	public void setMorphMLNeuron(Node n, NeuronMorphology morph) {
		this.detachAllChildren();
		this.attachChild(n);
		
		if (morph.getCurve() != null) {
			_cc = new CurveController(morph.getCurve().getCurve(), this);
			_cc.setAutoRotation(true);
			_cc.setUpVector(morph.getUpVector());
			_cc.update(morph.getTime());
		}
		if (morph.getAbsolutePosition() != null) {
			this.setLocalTranslation(morph.getAbsolutePosition().asVector3f());
		}
		if (morph.getAbsoluteRotation() != null) {
			this.setLocalRotation(morph.getAbsoluteRotation().asMatrix3f());
		}
		if (morph.getAbsoluteScale() != null) {
			this.setLocalScale(morph.getAbsoluteScale());
		}
		if (morph.getLookAtPosition() != null) {
			this.lookAt(morph.getLookAtPosition().asVector3f(), Vector3f.UNIT_X);
		}
		
		
	}
	
	public void setX3DNeuron(InputStream input, PositionVector _position, RotationVector _rotation, float _scale) {
		try {
			X3DLoader converter = new X3DLoader();
			Spatial scene = converter.loadScene(input, null, null);
			
			this.detachAllChildren();
			this.attachChild(scene);
			
			if (_position != null) 
			{
				this.setLocalTranslation(_position.asVector3f());
			}
			if (_rotation != null) {
				this.setLocalRotation(_rotation.asMatrix3f());
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
	
	/**
	 * Provides segments with an arbitrary degradation.
	 * Don't use for values other than 1 or Integer.MAX_VALUE
	 * 
	 * @param morph - Morphology file you want segments from
	 * @param numberOfSegsPerGroup - number of desired segments.  For full resolution, use Integer.MAX_VALUE
	 * @return
	 */
	private List<SegmentView> getSegments(NeuronMorphology morph, int numberOfSegsPerGroup) {
		assert (numberOfSegsPerGroup == 1 || numberOfSegsPerGroup == Integer.MAX_VALUE);
		List<SegmentView> segmentView = new ArrayList<SegmentView>();
//		if (numberOfSegsPerGroup == Integer.MAX_VALUE) {
//			for (ISegment s : morph.getSegments()) {
//				segmentView.add(new SegmentView(s));
//			}
//			
//		} else {
			for (ICable sg: morph.getSegmentGroups()) {
				segmentView.add(new SegmentView(sg));
			}
//		}
		return segmentView;
	}
	

	
	public Node loadscene(NeuronMorphology morph) {
		long tick = Log.tick();
		Node sceneRoot = new Node("Neuron Morphology Root");
		 /* 
         * Check the LightState. If none has been passed, create a new one and
         * attach it to the scene root
         */ 
		LightState lightState = null;
		lightState = DisplaySystem.getDisplaySystem().getRenderer().createLightState();
        lightState.setEnabled(true);	
        sceneRoot.setRenderState(lightState);
        
        for (SegmentView seg : this.getSegments(morph, cableResolution)) {
        	
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
           
        	Node node = seg.getViewNode(morph.getRenderOption());
           	        	
        	this.segViews.add(seg);
        	    		
    		sceneRoot.attachChild(node);	

        }
        Log.tock("NeuronMorphology.loadScene() took", tick);
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
	  
	  protected void selectLevelOfDetail(Renderer r) {
		  int target = chooseCableResolution(r);
		  if (target == 0) {
			  if (cableResolution == Integer.MAX_VALUE) {
				  reload();
			  }
		  } else if (target == Integer.MAX_VALUE){
			 if (cableResolution == 1) {
				 reload();
			 }
		  }
	  }
	  
	  public void update() 
	  {
		  super.update();
		  updateModelBound();
		  //reload(); //uncomment this and highlighting will work again
	  }
	
	  protected void reload()
	  {
		  super.update();
		  this.detachAllChildren();
		  this.attachChild(loadscene(currentMorph));
		  updateModelBound();
	  }
	  
	  public NeuronMorphology getMorphology() {
		  return currentMorph;
	  }
	  
	  /**
		 * This function is used during rendering to choose the correct target record for the
		 * AreaClodMesh acording to the information in the renderer.  This should not be called
		 * manually.  Instead, allow it to be called automatically during rendering.
		 * @param r The Renderer to use.
		 * @return the target record this AreaClodMesh will use to collapse vertexes.
		 */
		private int chooseCableResolution(Renderer r) {
			if (this.getWorldBound() == null) {
				logger.warning("NeuronMorphologyView found with no Bounds.");
				return 0;
			}

			float newDistance = getWorldBound().distanceTo(
					r.getCamera().getLocation());
			if (Math.abs(newDistance - lastDistance) <= distTolerance)
				return cableResolution; // we haven't moved relative to the model, send the old measurement back.
			if (lastDistance > newDistance && cableResolution == 1)
				return cableResolution; // we're already at the lowest setting and we just got closer to the model, no need to keep trying.
			if (lastDistance < newDistance && cableResolution == Integer.MAX_VALUE)
				return cableResolution; // we're already at the highest setting and we just got further from the model, no need to keep trying.

			lastDistance = newDistance;

			// estimate area of polygon via bounding volume
			float area = AreaUtils.calcScreenArea(getWorldBound(), lastDistance, r
					.getWidth());
			if (area > (640*480/2)) {
				cableResolution = Integer.MAX_VALUE;
			} else {
				cableResolution = 1;
			}
			return cableResolution;
		}

//	needs to be updated by a controller
	public void updateSelectedSegments(Set<ISegment> segments) {
		for (ISegment seg : segments) {
			for (SegmentView sv : this.segViews) {
				if (seg.equals(sv.getCorrespondingSegment())) {
					sv.highlight();
				} else { // or set to the default color of the segment 
					sv.unhighlight();
				}
			}
		}
	}

	public void updateSelectedSegmentGroups(Set<ICable> sgs) {
		for (ICable seg : sgs) {
			for (SegmentView sv : this.segViews) {
				if (seg.equals(sv.getCorrespondingSegmentGroup())) {
					sv.highlight();
				} else {
					sv.unhighlight();
				}
			}
		}
	}


	public void doHighlight() {
		for (SegmentView sv: this.segViews) {
			sv.highlight();
		}
	}

	
	public void doUnhighlight() {
		for (SegmentView sv: this.segViews) {
			sv.unhighlight();
		}
	}

}
