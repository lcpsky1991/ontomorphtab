package edu.ucsd.ccdb.ontomorph2.view.scene;

import com.jme.bounding.BoundingBox;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.SceneElement;
import com.jme.scene.TriMesh;
import com.jme.scene.VBOInfo;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.LightState;
import com.jme.system.DisplaySystem;

import edu.ucsd.ccdb.ontomorph2.core.atlas.BrainRegion;
import edu.ucsd.ccdb.ontomorph2.view.View;

/**
 * Manages the visual rendering of a BrainRegion.
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class BrainRegionView extends TangibleView{

	BrainRegion br = null;
	LightState lightState = null;
	TriMesh mesh = null;
	Node parentNode = null;
	int defaultRenderQueueMode = 0;
	
	/**
	 * Create a new BrainRegionView based on a BrainRegion model that will be associated with
	 * it and the parentNode that it ought to be contained in
	 * @param br
	 * @param parentNode
	 */
	public BrainRegionView(BrainRegion br, Node parentNode) {
		this.br = br;
		this.parentNode = parentNode;
		
		this.mesh = br.getTriMesh();
		//mesh.setSolidColor(ColorRGBA.blue);
		mesh.setModelBound(new BoundingBox());
		mesh.updateModelBound();
		VBOInfo nfo = new VBOInfo(true);
		mesh.setVBOInfo(nfo);
		mesh.setCullMode(SceneElement.CULL_DYNAMIC);
		
		this.lightState = DisplaySystem.getDisplaySystem().getRenderer().createLightState();
        this.lightState.setEnabled(true);
        
        this.defaultRenderQueueMode = this.getRenderQueueMode();
        
        this.setModelBound(new BoundingBox());
        this.updateModelBound();
           
		this.parentNode.attachChild(this);
		
		this.update();
		/*
        AlphaState as = ViewImpl.getInstance().getRenderer().createAlphaState();
	      as.setBlendEnabled(true);
	      as.setSrcFunction(AlphaState.SB_SRC_ALPHA);
	      as.setDstFunction(AlphaState.DB_ONE);
	      as.setTestEnabled(true);
	      as.setTestFunction(AlphaState.TF_GREATER);
	      as.setEnabled(true);
	    atlasNode.setRenderState(as);
	    atlasNode.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
	    */
		
		
	}
	
	public BrainRegion getBrainRegion() {
		return this.br;
	}
	
	/**
	 * Refresh this view based on the current state of the associated BrainRegion model.
	 *
	 */
	public void update() {
		switch(br.getVisibility()) {
		case BrainRegion.VISIBLE:
			makeVisible();
			makeSolid();
			
			break;
		case BrainRegion.INVISIBLE:
			//make invisible
			this.detachChild(this.mesh);
			
			break;
		case BrainRegion.TRANSPARENT:
			makeVisible();
			makeTransparent();
			break;
		}
		
		if (br.isSelected()) {
			this.highlight();
		} else {
			this.unhighlight();
		}

		this.mesh.updateModelBound();
		this.mesh.updateRenderState();
		this.mesh.updateGeometricState(5f, true);
		
		this.updateModelBound();
	    this.updateRenderState();
	    this.updateGeometricState(5f, true);
	    
		this.parentNode.updateModelBound();
	    this.parentNode.updateRenderState();
	    this.parentNode.updateGeometricState(5f, true);
	}
	
	private void makeVisible() {
		this.detachChild(this.mesh);
		this.attachChild(this.mesh);
	}
	
	private void makeTransparent() {
		AlphaState as = View.getInstance().getRenderer().createAlphaState();
	      as.setBlendEnabled(true);
	      as.setSrcFunction(AlphaState.SB_SRC_ALPHA);
	      as.setDstFunction(AlphaState.DB_ONE);
	      as.setTestEnabled(true);
	      as.setTestFunction(AlphaState.TF_GREATER);
	      as.setEnabled(true);
	    this.setRenderState(as);
	    this.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
	}
	
	private void makeSolid() {
		this.setRenderState(lightState);
		this.setRenderQueueMode(this.defaultRenderQueueMode);
	}

	@Override
	protected void refreshColor() {
		if (isHighlighted()) {
			//
		} else {
			//
		}
	}
}
