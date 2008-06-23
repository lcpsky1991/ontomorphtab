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
	
	public BrainRegionView(BrainRegion br) {
		this.br = br;
		
		lightState = DisplaySystem.getDisplaySystem().getRenderer().createLightState();
        lightState.setEnabled(true);
        this.setRenderState(lightState);
        
        mesh = br.getTriMesh();
		mesh.setSolidColor(ColorRGBA.blue);
		mesh.setModelBound(new BoundingBox());
		mesh.updateModelBound();
		VBOInfo nfo = new VBOInfo(true);
		mesh.setVBOInfo(nfo);
		mesh.setCullMode(SceneElement.CULL_DYNAMIC);
		
		this.update();
	}
	
	public BrainRegion getBrainRegion() {
		return this.br;
	}
	
	public void update() {
		AlphaState as = null;
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

	    this.updateRenderState();
	    this.updateGeometricState(5f, true);
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
	    this.setRenderState(as);
	    this.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
	}
	
	private void makeSolid() {
		this.setRenderState(lightState);
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
