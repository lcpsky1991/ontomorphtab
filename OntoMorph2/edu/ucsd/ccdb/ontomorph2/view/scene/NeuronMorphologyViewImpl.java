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
import com.jme.scene.Geometry;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.batch.GeomBatch;
import com.jme.scene.lod.AreaClodMesh;
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
	
	int targetRecord = 1;
	
	INeuronMorphology currentMorph = null;
	
	public NeuronMorphologyViewImpl(INeuronMorphology morph) {
		segViews = new ArrayList<ISegmentView>();
		currentMorph = morph;
		//if (morph.getRenderOption().equals(INeuronMorphology.RENDER_AS_CYLINDERS)) {
			this.setMorphMLNeuron(this.loadscene(morph), morph.getPosition(), morph.getRotation(), morph.getScale());
		//} else if (morph.getRenderOption().equals(INeuronMorphology.RENDER_AS_LINES)) {
			//InputStream input = XSLTransformManager.getInstance().convertMorphMLToX3D(morph.getMorphMLURL());
			//this.setX3DNeuron(input, morph.getPosition(), morph.getRotation(), morph.getScale());
		//}
			//updateModelBound();
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
		if (numberOfSegsPerGroup == Integer.MAX_VALUE) {
			for (ISegment s : morph.getSegments()) {
				segmentView.add(new SegmentViewImpl(s));
			}
			
		} else {
			for (ISegmentGroup sg: morph.getSegmentGroups()) {
				segmentView.add(new SegmentViewImpl(sg));
			}
		}
		return segmentView;
	}
	
	/**
	 * Provides segments with an arbitrary degradation.
	 * Don't use for values other than 1 or Integer.MAX_VALUE
	 * 
	 * @param morph - Morphology file you want segments from
	 * @param numberOfSegsPerGroup - number of desired segments.  For full resolution, use Integer.MAX_VALUE
	 * @return
	 */
/*	private List<ISegment> getSegments(INeuronMorphology morph, int numberOfSegsPerGroup) {
		assert (numberOfSegsPerGroup == 1 || numberOfSegsPerGroup == Integer.MAX_VALUE);
		if (numberOfSegsPerGroup == Integer.MAX_VALUE) {
			 return morph.getSegments();
		} 
		ArrayList<ISegment> segments = new ArrayList<ISegment>();
		for (edu.ucsd.ccdb.ontomorph2.core.scene.ISegmentGroup sg : morph.getSegmentGroups()) {
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
			} else {
				int cutIncrement = (int)Math.rint(numberOfSegsPerGroup / (sg.getSegments().size()+1));
				for (int i = 0; i < numberOfSegsPerGroup-1; i++) {
					sg.getSegments().get(cutIncrement*i);
				}
			} 
		}
		return segments;
	}*/
	
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
        
        for (ISegmentView seg : this.getSegments(morph, targetRecord)) {
        	
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
           
        	Geometry g = null;
        	
        	if (morph.getRenderOption().equals(INeuronMorphology.RENDER_AS_LINES)) {
        		g = ((SegmentViewImpl)seg).getLine();
        	} else if (morph.getRenderOption().equals(INeuronMorphology.RENDER_AS_CYLINDERS)) {
        		//g = ((SegmentViewImpl)seg).getCylinder();
        		g = ((SegmentViewImpl)seg).getClodMeshCylinder();
        	}
        	
        	this.segViews.add(seg);
        	    		
    		sceneRoot.attachChild(g);	

        }
        
    	//if (morph.getRenderOption().equals(INeuronMorphology.RENDER_AS_CYLINDERS)) {
    		//sceneRoot = getClodNodeFromParent(sceneRoot);
    	//}
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
		
	private Node getClodMeshFromParent(Node meshParent) {
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
