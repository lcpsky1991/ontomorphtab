package edu.ucsd.ccdb.ontomorph2.view.scene;

import java.net.URL;

import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.util.geom.BufferUtils;

import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Slide;
import edu.ucsd.ccdb.ontomorph2.util.OMTException;
import edu.ucsd.ccdb.ontomorph2.view.View;

/**
 * Defines a slide, which is a plane in space that has the image of a slice of brain on it.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 */
public class QuadSlideView extends Quad {
	
	URL imageURL = null;
	Slide _slide = null;
	private DisplaySystem display = null;
	
	public QuadSlideView(Slide slide) {
		super.setName("Slide View");
		_slide = slide;
		setImageURL(slide.getImageURL());
		init();
	}
	
	public QuadSlideView(Slide slide, DisplaySystem disp) {
		super.setName("Slide View");
		_slide = slide;
		setImageURL(slide.getImageURL());
		setDisplay(disp);
		init();
	}

	private void init() {
		this.initialize(20f,20f);
		
//		get my texturestate
		TextureState ts = null;
		if (this.display == null) {
			ts = View.getInstance().getDisplaySystem().getRenderer().createTextureState();
		} else {
			ts = this.display.getRenderer().createTextureState();
		}
		//get my texture
		
		Texture t= TextureManager.loadTexture(imageURL,
				Texture.MM_LINEAR, Texture.FM_LINEAR);
		//set a wrap for my texture so it repeats
		t.setWrap(Texture.WM_WRAP_S_WRAP_T);
		//set the texture to the texturestate
		ts.setTexture(t);
		//assign the texturestate to the square
		this.setRenderState(ts);
		//scale my square 10x larger
		if (_slide.getAbsoluteScale() != null) {
			//this.setLocalTranslation(0,0,0);
			//this.setLocalScale(_slide.getAbsoluteScale());
		}
			
		if (_slide.getAbsolutePosition() != null) {
			//this.setLocalTranslation(_slide.getAbsolutePosition());
		}
		if (_slide.getAbsoluteRotation() != null) {
			this.setLocalRotation(_slide.getAbsoluteRotation());
		}
		
		this.setModelBound(new BoundingBox());
		this.updateModelBound();
	}
	
	private void setImageURL(URL imageURL) {
		this.imageURL = imageURL;
	}
	
	public void setDisplay(DisplaySystem disp) {
		this.display = disp;
	}
}
