package edu.ucsd.ccdb.ontomorph2.view.scene;


import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.renderer.Renderer;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;

import edu.ucsd.ccdb.ontomorph2.core.tangible.slide.Slide;
import edu.ucsd.ccdb.ontomorph2.core.tangible.slide.TiledSlide;
import edu.ucsd.ccdb.ontomorph2.view.View;

/**
 * Defines a slide, which is a plane in space that has the image of a slice of brain on it.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 */
public class SlideView extends TangibleView {
	
	Quad quad;
	
	public SlideView(Slide slide) 
	{
		super(slide);
		quad = new Quad("Slide View");
		
		init();
	}
	private Slide getSlide() {
		return (Slide)getModel();
	}
	
	public void redrawTexture()
	{
		init();
	}
	
	private TextureState getTextureState() {
//		create texture state for graph
		
		TextureState textureState = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
		Texture t = null;
		if (getSlide() instanceof TiledSlide) {
			t = new Texture();
			t.setMipmapState(Texture.MM_LINEAR);
			t.setImage(((TiledSlide)getSlide()).getImage());
		} else {
			t = TextureManager.loadTexture(((Slide)getSlide()).getBufferedImage(), 
						Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR, 1, true);
		}
		
		textureState.setTexture(t);
		textureState.setEnabled(true);
		return textureState;
	}
	

	private void init() {
		quad.initialize(20f*getSlide().getRatio(),20f);
		
		TextureState ts = getTextureState();
		quad.setRenderState(ts);
		
		
		quad.setRenderQueueMode(Renderer.QUEUE_OPAQUE);
		quad.setLightCombineMode(LightState.OFF);
		
		ZBufferState zb = View.getInstance().getRenderer().createZBufferState();
		zb.setWritable(true);
		zb.setFunction(ZBufferState.CF_LEQUAL);
		zb.setEnabled(true);
		
		quad.setRenderState(zb);

		quad.setModelBound(new BoundingBox());
		quad.updateModelBound();
		quad.updateRenderState();
		quad.updateGeometricState(0.5f, false);
		
		this.attachChild(quad);
		
		this.updateRenderState();
		
//		update the geometries registry, this is neccessary to enable picking, which is based on geomtry key maps
		this.registerGeometry(quad);
		
		this.update();
	}

	
	@Override
	public void doHighlight() 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doUnhighlight() 
	{
		// TODO Auto-generated method stub
		
	}
}
