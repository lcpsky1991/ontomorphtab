package edu.ucsd.ccdb.ontomorph2.view.scene;


import java.util.ArrayList;
import java.util.List;

import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.renderer.Renderer;
import com.jme.scene.Geometry;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;

import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Slide;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.TiledSlide;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.URISlide;
import edu.ucsd.ccdb.ontomorph2.view.View;
import edu.ucsd.ccdb.ontomorph2.view.View;

/**
 * Defines a slide, which is a plane in space that has the image of a slice of brain on it.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 */
public class SlideView extends TangibleView {
	
	Quad quad;
	Slide _slide = null;
	private DisplaySystem display = null;
	
	public SlideView(Slide slide) {
		super(slide);
		quad = new Quad("Slide View");
		_slide = slide;
		init();
	}
	
	public SlideView(Slide slide, DisplaySystem disp) {
		super(slide);
		quad = new Quad("Slide View");
		_slide = slide;
		setDisplay(disp);
		init();
	}
	
	private TextureState getTextureState() {
//		create texture state for graph
		TextureState textureState = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
		Texture t = null;
		if (_slide instanceof URISlide) {
			t = TextureManager.loadTexture(((URISlide)_slide).getBufferedImage(), 
						Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR, 1, true);
		} else if (_slide instanceof TiledSlide) {
			t = new Texture();
			t.setMipmapState(Texture.MM_LINEAR);
			t.setImage(((TiledSlide)_slide).getImage());
		}
		
		textureState.setTexture(t);
		textureState.setEnabled(true);
		return textureState;
	}
	

	private void init() {
		quad.initialize(20f*_slide.getRatio(),20f);
		
		TextureState ts = getTextureState();
		quad.setRenderState(ts);
		
		
		quad.setRenderQueueMode(Renderer.QUEUE_OPAQUE);
		quad.setLightCombineMode(LightState.OFF);
		
		ZBufferState zb = View.getInstance().getRenderer().createZBufferState();
		zb.setWritable(true);
		zb.setFunction(ZBufferState.CF_LEQUAL);
		zb.setEnabled(true);
		
		quad.setRenderState(zb);

		quad.setZOrder(9);
		
		quad.setModelBound(new BoundingBox());
		quad.updateModelBound();
		quad.updateRenderState();
		quad.updateGeometricState(0.5f, false);
		
			
		//this.setCullMode(SceneElement.CULL_INHERIT);

		//this.setRenderQueueMode(Renderer.QUEUE_OPAQUE);
		//this.setZOrder(9);
		
		if (_slide.isVisible()) {
			this.attachChild(quad);
		}
		
		this.updateRenderState();
		
//		update the geometries registry, this is neccessary to enable picking, which is based on geomtry key maps
		List<Geometry> ll = new ArrayList<Geometry>();
		ll.add(quad);
		this.registerGeometries(ll);	
		
		
		this.update();
	}
	
	
	public void setDisplay(DisplaySystem disp) {
		this.display = disp;
	}

	@Override
	public void doHighlight() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doUnhighlight() {
		// TODO Auto-generated method stub
		
	}
}
