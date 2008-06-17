package edu.ucsd.ccdb.ontomorph2.view.scene;

import java.net.URL;

import com.jme.image.Texture;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.scene.TriMesh;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jme.util.geom.BufferUtils;

import edu.ucsd.ccdb.ontomorph2.core.scene.Slide;
import edu.ucsd.ccdb.ontomorph2.view.ViewImpl;

/**
 * Defines a slide, which is a plane in space that has the image of a slice of brain on it.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 */
public class SlideView extends TriMesh {
	
	URL imageURL = null;
	Slide _slide = null;
	
	public SlideView(URL imageURL, Slide slide) {
		_slide = slide;
		setImageURL(imageURL);
		init();
	}

	private void init() {
		
		float ratio = _slide.getRatio();
		float scale = _slide.getAbsoluteScale().x;
		float x = _slide.getAbsolutePosition().x;
		float y = _slide.getAbsolutePosition().y;
		float z = _slide.getAbsolutePosition().z;
		
		y-=113;
		x-=180;
		z-=305;
		
		
		Vector3f v1 = new Vector3f(x,y,z);
		Vector3f v2 = new Vector3f(ratio*scale+x,y,z);
		Vector3f v3 = new Vector3f(x,scale+y,z);
		Vector3f v4 = new Vector3f(ratio*scale+x,scale+y,z);
		
		/*
		Vector3f v1 = new Vector3f(0,0,0);
		Vector3f v2 = new Vector3f(ratio*scale,0,0);
		Vector3f v3 = new Vector3f(0,scale,0);
		Vector3f v4 = new Vector3f(ratio*scale,scale,0);
		*/
		/*
		CoordinateSystem d = _slide.getCoordinateSystem();
		v1 = d.multPoint(v1);
		v2 = d.multPoint(v2);
		v3 = d.multPoint(v3);
		v4 = d.multPoint(v4);
		*/
//		Vertex positions for the mesh
		
		/*
		float xFact = -300;
		v1.x = xFact;
		v2.x = xFact;
		v3.x = xFact;
		v4.x = xFact;
		*/
		
		
		Vector3f[] vertexes={ v1,v2,v3,v4 };
		
		//texture coordinates for each position
		int coordDelta=1;
		Vector2f[] texCoords ={
				new Vector2f(0,0),
				new Vector2f(coordDelta,0),
				new Vector2f(0,coordDelta),
				new Vector2f(coordDelta,coordDelta),
		};
		//The indexes of Vertex/Normal/Color/TexCoord sets.  Every 3
		//makes a triangle.
		int[] indexes={
				0,1,2,1,3,2
		};
		//create the square
		this.setName("my mesh");
		this.setVertexBuffer(0, BufferUtils.createFloatBuffer(vertexes));
		this.setTextureBuffer(0, BufferUtils.createFloatBuffer(texCoords));
		this.setIndexBuffer(0, BufferUtils.createIntBuffer(indexes));
		/*
		square = new TriMesh("my mesh", BufferUtils.createFloatBuffer(vertexes),
				null,null, BufferUtils.createFloatBuffer(texCoords), 
				BufferUtils.createIntBuffer(indexes));
		*/
		//get my texturestate
		TextureState ts = ViewImpl.getInstance().getDisplaySystem().getRenderer().createTextureState();
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
	}
	
	private void setImageURL(URL imageURL) {
		this.imageURL = imageURL;
	}
}
