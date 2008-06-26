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

public class BrainRegionView extends SceneObjectView{

	BrainRegion br = null;
	LightState lightState = null;
	TriMesh mesh = null;
	Node parentNode = null;
	
	public BrainRegionView(BrainRegion br, Node parentNode) {
		this.br = br;
		this.parentNode = parentNode;
		
		TriMesh mesh = br.getTriMesh();
		mesh.setSolidColor(ColorRGBA.blue);
		mesh.setModelBound(new BoundingBox());
		mesh.updateModelBound();
		VBOInfo nfo = new VBOInfo(true);
		mesh.setVBOInfo(nfo);
		mesh.setCullMode(SceneElement.CULL_DYNAMIC);

		
		LightState lightState = null;
		lightState = DisplaySystem.getDisplaySystem().getRenderer().createLightState();
        lightState.setEnabled(true);
        
        
		
		this.parentNode.attachChild(mesh);
		
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
	
	public void update() {
		switch(br.getVisibility()) {
		case BrainRegion.VISIBLE:
			makeVisible();
			makeSolid();
			
			break;
		case BrainRegion.INVISIBLE:
			//make invisible
			this.detachChild(this.mesh);

			this.updateModelBound();
		    this.updateRenderState();
		    this.updateGeometricState(5f, true);
			
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

		this.parentNode.updateModelBound();
	    this.parentNode.updateRenderState();
	    this.parentNode.updateGeometricState(5f, true);
	}
	
	private void makeVisible() {
		this.detachAllChildren();
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
	    this.parentNode.setRenderState(as);
	    this.parentNode.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
	}
	
	private void makeSolid() {
		this.parentNode.setRenderState(lightState);
		this.parentNode.setRenderQueueMode(Renderer.QUEUE_OPAQUE);
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
