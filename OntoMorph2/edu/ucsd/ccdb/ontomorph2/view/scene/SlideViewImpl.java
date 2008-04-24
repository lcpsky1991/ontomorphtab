package edu.ucsd.ccdb.ontomorph2.view.scene;

import java.net.URL;

import com.jme.image.Texture;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.scene.TriMesh;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jme.util.geom.BufferUtils;

import edu.ucsd.ccdb.ontomorph2.core.spatial.IPosition;
import edu.ucsd.ccdb.ontomorph2.core.spatial.IRotation;

public class SlideViewImpl extends TriMesh implements ISlideView{
	
	URL imageURL = null;
	IPosition _position = null;
	IRotation _rotation = null;
	
	public SlideViewImpl(URL imageURL, IPosition position, IRotation rotation) {
		setImageURL(imageURL);
		_position = position;
		_rotation = rotation;
		init();
	}
	
	private void init() {
//		Vertex positions for the mesh
		Vector3f[] vertexes={				new Vector3f(0,0,0),
				new Vector3f(1,0,0),
				new Vector3f(0,1,0),
				new Vector3f(1,1,0)
		};
		
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
		this.setLocalScale(50);
		
		if (_position != null) {
			this.setLocalTranslation(_position.asVector3f());
		}
	}
	
	private void setImageURL(URL imageURL) {
		this.imageURL = imageURL;
	}
}
