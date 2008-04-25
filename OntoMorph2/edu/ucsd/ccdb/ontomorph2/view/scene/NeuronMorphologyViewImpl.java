package edu.ucsd.ccdb.ontomorph2.view.scene;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.jme.bounding.BoundingSphere;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.DistanceSwitchModel;
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.batch.GeomBatch;
import com.jme.scene.lod.AreaClodMesh;
import com.jme.scene.lod.DiscreteLodNode;
import com.jme.scene.state.LightState;
import com.jme.scene.state.RenderState;
import com.jme.system.DisplaySystem;
import com.jme.util.AreaUtils;

import edu.ucsd.ccdb.ontomorph2.core.scene.INeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.core.scene.ISegment;
import edu.ucsd.ccdb.ontomorph2.core.scene.ISegmentGroup;
import edu.ucsd.ccdb.ontomorph2.core.spatial.IPosition;
import edu.ucsd.ccdb.ontomorph2.core.spatial.IRotation;
import edu.ucsd.ccdb.ontomorph2.util.ColorUtil;
import edu.ucsd.ccdb.ontomorph2.util.X3DLoader;

public class NeuronMorphologyViewImpl extends Node implements INeuronMorphologyView {

	List<ISegmentView> segViews;
	
    private static final Logger logger = Logger.getLogger(AreaClodMesh.class
            .getName());

	private float trisPerPixel = 1f;

	private float distTolerance = 1f;

	private float lastDistance = 0f;
	
	int cableResolution = 1;
	
	INeuronMorphology currentMorph = null;
	
	public NeuronMorphologyViewImpl(INeuronMorphology morph) {
		segViews = new ArrayList<ISegmentView>();
		currentMorph = morph;
		
		this.setMorphMLNeuron(this.loadscene(morph), morph.getPosition(), morph.getRotation(), morph.getScale());
	}

	public Node getNode() {
		return this;
	}
	
	public boolean containsGeomBatch(GeomBatch gb) {
		return getSegmentFromGeomBatch(gb) != null;
	}
	
	public ISegmentView getSegmentFromGeomBatch(GeomBatch gb) {
		Geometry g = gb.getParentGeom();
		ISegmentView pick = null;
		for (ISegmentView sv : this.segViews) {
			if (((SegmentViewImpl)sv).getCurrentGeometry() == g) {
				pick = sv;
			}
		}
		return pick;
	}
	
	public void setMorphMLNeuron(Node n, IPosition _position, IRotation _rotation, float _scale) {
		this.detachAllChildren();
		this.attachChild(n);
		
		if (_position != null) {
			this.setLocalTranslation(_position.asVector3f());
		}
		if (_rotation != null) {
			this.setLocalRotation(_rotation.asMatrix3f());
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
	private List<ISegmentView> getSegments(INeuronMorphology morph, int numberOfSegsPerGroup) {
		assert (numberOfSegsPerGroup == 1 || numberOfSegsPerGroup == Integer.MAX_VALUE);
		List<ISegmentView> segmentView = new ArrayList<ISegmentView>();
//		if (numberOfSegsPerGroup == Integer.MAX_VALUE) {
//			for (ISegment s : morph.getSegments()) {
//				segmentView.add(new SegmentViewImpl(s));
//			}
//			
//		} else {
			for (ISegmentGroup sg: morph.getSegmentGroups()) {
				segmentView.add(new SegmentViewImpl(sg));
			}
//		}
		return segmentView;
	}
	

	
	public Node loadscene(INeuronMorphology morph) {
		Node sceneRoot = new Node();
		 /* 
         * Check the LightState. If none has been passed, create a new one and
         * attach it to the scene root
         */ 
		LightState lightState = null;
		lightState = DisplaySystem.getDisplaySystem().getRenderer().createLightState();
        lightState.setEnabled(true);	
        sceneRoot.setRenderState(lightState);
        
        for (ISegmentView seg : this.getSegments(morph, cableResolution)) {
        	
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
           
        	Node node = new Node();
        	
        	if (morph.getRenderOption().equals(INeuronMorphology.RENDER_AS_LINES)) {
        		node.attachChild(((SegmentViewImpl)seg).getLine());
        	} else if (morph.getRenderOption().equals(INeuronMorphology.RENDER_AS_CYLINDERS)) {
        		node.attachChild(((SegmentViewImpl)seg).getClodMeshCylinder());
        	} else if (morph.getRenderOption().equals(INeuronMorphology.RENDER_AS_LOD)) {
        		DistanceSwitchModel dsm = new DistanceSwitchModel(2);
            	dsm.setModelDistance(0, 0, 1000);
            	dsm.setModelDistance(1, 1000, 10000);
            	
            	node = new DiscreteLodNode("node", dsm);
            	
            	node.attachChildAt(((SegmentViewImpl)seg).getCylinder(), 0);
            	node.attachChildAt(((SegmentViewImpl)seg).getLine(), 1);
        	} else if (morph.getRenderOption().equals(INeuronMorphology.RENDER_AS_LOD_2)){

            	DistanceSwitchModel dsm = new DistanceSwitchModel(3);
            	dsm.setModelDistance(0, 0, 500);
            	dsm.setModelDistance(1, 500, 1000);
            	dsm.setModelDistance(2, 1000, 10000);
            	
            	node = new DiscreteLodNode("node", dsm);
            	
            	//node.attachChildAt(((SegmentViewImpl)seg).getCurveFromSegGroup(), 0);
            	node.attachChildAt(((SegmentViewImpl)seg).getCylindersFromSegGroup(), 0);
            	node.attachChildAt(((SegmentViewImpl)seg).getCylinder(), 1);
            	node.attachChildAt(((SegmentViewImpl)seg).getLine(), 2);
            	
        	}
           	        	
        	this.segViews.add(seg);
        	    		
    		sceneRoot.attachChild(node);	

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
			  if (cableResolution == Integer.MAX_VALUE) {
				  reload();
			  }
		  } else if (target == Integer.MAX_VALUE){
			 if (cableResolution == 1) {
				 reload();
			 }
		  }
	  }
	  
	  public void reload() {
		  this.detachAllChildren();
		  this.attachChild(loadscene(currentMorph));
		  updateModelBound();
	  }
	
	  public INeuronMorphology getMorphology() {
		  return currentMorph;
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
				logger.warning("NeuronMorphologyViewImpl found with no Bounds.");
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
	//does not unselect yet
	public void updateSelectedSegments(Set<ISegment> segments) {
		for (ISegment seg : segments) {
			for (ISegmentView sv : this.segViews) {
				if (seg.equals(sv.getCorrespondingSegment())) {
					((SegmentViewImpl)sv).getCurrentGeometry().setSolidColor(ColorRGBA.yellow);
				} else { // or set to the default color of the segment 
					((SegmentViewImpl)sv).getCurrentGeometry().setSolidColor(ColorUtil.convertColorToColorRGBA(sv.getCorrespondingSegment().getColor()));
				}
			}
		}
	}

	public void updateSelectedSegmentGroups(Set<ISegmentGroup> sgs) {
		for (ISegmentGroup seg : sgs) {
			for (ISegmentView sv : this.segViews) {
				if (seg.equals(sv.getCorrespondingSegmentGroup())) {
					((SegmentViewImpl)sv).getCurrentGeometry().setSolidColor(ColorRGBA.yellow); //set to selected color yellow
				} else { // or set to the default color of the segment group
					((SegmentViewImpl)sv).getCurrentGeometry().setSolidColor(ColorUtil.convertColorToColorRGBA(sv.getCorrespondingSegmentGroup().getColor()));
				}
			}
		}
	}

	public void updateSelected(boolean selected) {
		if (selected) {
			
		}
	}

}
