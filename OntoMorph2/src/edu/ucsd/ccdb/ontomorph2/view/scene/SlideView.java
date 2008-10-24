package edu.ucsd.ccdb.ontomorph2.view.scene;


import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.image.Texture;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.*;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jmex.effects.particles.Particle;

import edu.ucsd.ccdb.ontomorph2.core.tangible.slide.Slide;
import edu.ucsd.ccdb.ontomorph2.core.tangible.slide.TiledSlide;
import edu.ucsd.ccdb.ontomorph2.view.TangibleViewManager;
import edu.ucsd.ccdb.ontomorph2.view.View;

/**
 * Defines a slide, which is a plane in space that has the image of a slice of brain on it.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 * @author caprea
 */
public class SlideView extends TangibleView 
{
	
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
		} 
		else 
		{
			t = TextureManager.loadTexture( getSlide().getBufferedImage(), Texture.MM_LINEAR_LINEAR, Texture.FM_LINEAR, 1, true);
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
		/*
		Vector3f cen = (this.getModel().getPosition().asVector3f()); //put the box in the right place
		Vector3f none = new Vector3f(0,0,0);
		Vector3f s = this.getWorldScale();
		Vector3f norm = this.getModel().getWorldNormal();
		Vector3f pos = s.cross(norm);
		
		
		StripBox bound = null;
		
		//=== Set the size
		bound = new StripBox("selectiontool");
		bound.setData(pos.negate(), pos);
        //bound.setModelBound(new BoundingSphere());
        bound.updateModelBound();
        
        //==== Create the color
         * 
         */
        ColorRGBA clrSel = TangibleViewManager.highlightSelectedColor;
        clrSel.a = 0.55f; //adjust the alpha to be less than usual
        //bound.setSolidColor(clrSel);
		//
        
        //=== Create the mnaterial state
        MaterialState ms = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
		//ms.setEmissive(clrSel);
		//ms.setDiffuse(clrSel);
		//ms.setAmbient(clrSel);
		ms.setSpecular(clrSel);
		ms.setEnabled(true);
		//ms.setColorMaterial(MaterialState.;
		//ms.setColorMaterial(MaterialState.CM_EMISSIVE);
		ms.setMaterialFace(MaterialState.MF_FRONT_AND_BACK);
		this.setRenderState(ms);
		this.updateRenderState();
		
        //===== set the alpha state
       
		/*
        AlphaState as = DisplaySystem.getDisplaySystem().getRenderer().createAlphaState();
        as.setEnabled(true);
        as.setBlendEnabled(true);
        as.setSrcFunction(AlphaState.SB_SRC_ALPHA);
        as.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
        this.setRenderState(as);
        this.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
        this.updateRenderState();
        */
        //
		
		//nEffects.attachChild(bound);
	
		
	}

	@Override
	public void doUnhighlight() 
	{
		// TODO Auto-generated method stub
		nEffects.detachAllChildren();
		
		this.setRenderState(null);
		this.updateRenderState();
	}
}
