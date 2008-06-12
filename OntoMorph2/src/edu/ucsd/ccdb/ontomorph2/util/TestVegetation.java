package edu.ucsd.ccdb.ontomorph2.util;

import com.jme.app.SimplePassGame;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.input.FirstPersonHandler;
import com.jme.light.DirectionalLight;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.renderer.pass.RenderPass;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Pyramid;
import com.jme.scene.state.CullState;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;
import com.jmex.terrain.TerrainBlock;
import com.jmex.terrain.util.ImageBasedHeightMap;
import com.jmex.terrain.util.ProceduralTextureGenerator;

import javax.swing.*;
import java.util.logging.Level;

/**
 * MrCoder
 */
public class TestVegetation extends SimplePassGame {
	private static final float vegetationSeparation = 8.0f;
	private static final int vegetationCountX = 1000;
	private static final int vegetationCountZ = 1000;
	private static final int terrainSize = 64;
	private TerrainBlock tb;

	public static void main( String[] args ) {
		//LoggingSystem.getLogger().setLevel( Level.SEVERE );

		TestVegetation app = new TestVegetation();
		app.setDialogBehaviour( ALWAYS_SHOW_PROPS_DIALOG );
		app.start();
	}

	protected void simpleInitGame() {
		display.setTitle( "Vegetation challenge" );
		cam.setFrustumPerspective( 45.0f, ( float ) display.getWidth() / ( float ) display.getHeight(), 1f, 10000f );

		DirectionalLight dl = new DirectionalLight();
		dl.setDiffuse( new ColorRGBA( 1.0f, 1.0f, 1.0f, 1.0f ) );
		dl.setDirection( new Vector3f( 1, -1, 1 ) );
		dl.setEnabled( true );
		lightState.detachAll();
		lightState.attach( dl );

		input = new FirstPersonHandler( cam, 200, 1 );

		rootNode.attachChild( createTerrain() );
		rootNode.attachChild( createVegetation() );

		float x = vegetationCountX * vegetationSeparation * 0.5f;
		float z = vegetationCountZ * vegetationSeparation * 0.5f;
		cam.setLocation( new Vector3f( x, tb.getHeight( x, z ) + 20.0f, z ) );
		cam.update();

		RenderPass rootPass = new RenderPass();
		rootPass.add( rootNode );
		pManager.add( rootPass );

		RenderPass fpsPass = new RenderPass();
		fpsPass.add( fpsNode );
		pManager.add( fpsPass );

		CullState cullState = display.getRenderer().createCullState();
		cullState.setCullMode( CullState.CS_BACK );
		cullState.setEnabled( true );
		rootNode.setRenderState( cullState );
		rootNode.setCullMode( Spatial.CULL_NEVER );
		rootNode.setRenderQueueMode( Renderer.QUEUE_OPAQUE );
		fpsNode.setRenderQueueMode( Renderer.QUEUE_OPAQUE );
	}

	private Node createVegetation() {
		//Load the vegetation class of your choice
		AbstractVegetation vegetation = new QuadTreeVegetation( "vegetation", cam, 500.0f );

		vegetation.setCullMode( Spatial.CULL_DYNAMIC );
		vegetation.initialize();

		//Load placeholder models for vegetation
		TriMesh model1 = new Box( "box", new Vector3f( -1, -1, -1 ), new Vector3f( 1, 1, 1 ) );
		model1.setModelBound( new BoundingBox() );
		model1.updateModelBound();
		TriMesh model2 = new Pyramid( "pyramid", 2, 2 );
		model2.setModelBound( new BoundingBox() );
		model2.updateModelBound();

		//Place the darn models
		for ( int i = 0; i < vegetationCountX; i++ ) {
			for ( int j = 0; j < vegetationCountZ; j++ ) {
				float x = i * vegetationSeparation;
				float z = j * vegetationSeparation;

				//find height
				float height = tb.getHeight( x, z );
				if ( Float.isNaN( height ) ) {
					height = 0.0f;
				}
				Vector3f translation = new Vector3f( x, height, z );

				//find scale
				float scaleValue = 1.0f;
				Vector3f scale = new Vector3f( scaleValue, scaleValue, scaleValue );

				//find rotation
				Vector3f normalY = tb.getSurfaceNormal( x, z, null );
				if ( normalY == null ) {
					normalY = Vector3f.UNIT_Y;
				}
				Vector3f normalX = normalY.cross( Vector3f.UNIT_X );
				Vector3f normalZ = normalY.cross( normalX );
				normalX = normalY.cross( normalZ );
				Quaternion rotation = new Quaternion();
				rotation.fromAxes( normalX, normalY, normalZ );

				//just mix the models
				if ( ( i + j ) % 2 == 0 ) {
					vegetation.addVegetationObject( model1, translation, scale, rotation );
				}
				else {
					vegetation.addVegetationObject( model2, translation, scale, rotation );
				}
			}
		}

		vegetation.setup();

		return vegetation;
	}

	private Node createTerrain() {
		Node terrain = new Node( "TerrainNode" );

		float terrainWidth = vegetationCountX * vegetationSeparation / terrainSize;
		float terrainHeight = vegetationCountZ * vegetationSeparation / terrainSize;
		ImageBasedHeightMap heightMap = new ImageBasedHeightMap( new ImageIcon( getClass().getClassLoader().getResource( "jmetest/data/texture/bubble.jpg" ) ).getImage() );
		Vector3f terrainScale = new Vector3f( terrainWidth, 6.0f * vegetationCountX / 1000.0f, terrainHeight );
		tb = new TerrainBlock( "TerrainBlock", heightMap.getSize(), terrainScale,
							   heightMap.getHeightMap(),
							   new Vector3f( -terrainSize * terrainWidth / 2.0f, 0, -terrainSize * terrainHeight / 2.0f ), false );
		tb.setDistanceTolerance( 1.0f );
		tb.setDetailTexture( 1, 16 );
		tb.setModelBound( new BoundingBox() );
		tb.updateModelBound();
		tb.setLocalTranslation( new Vector3f( 0, 0, 0 ) );

		ProceduralTextureGenerator pt = new ProceduralTextureGenerator(
				heightMap );
		pt.addTexture( new ImageIcon( getClass().getClassLoader()
				.getResource( "jmetest/data/texture/grassb.png" ) ),
					   -128, 0, 128 );
		pt.addTexture( new ImageIcon( getClass().getClassLoader()
				.getResource( "jmetest/data/texture/dirt.jpg" ) ),
					   0, 128, 255 );
		pt.addTexture( new ImageIcon( getClass().getClassLoader()
				.getResource( "jmetest/data/texture/highest.jpg" ) ),
					   128, 255,
					   384 );

		pt.createTexture( 64 );

		TextureState ts = display.getRenderer().createTextureState();
		ts.setEnabled( true );
		Texture t1 = TextureManager.loadTexture(
				pt.getImageIcon().getImage(),
				Texture.MM_LINEAR_LINEAR,
				Texture.FM_LINEAR, true );
		ts.setTexture( t1, 0 );

		Texture t2 = TextureManager.loadTexture(
				getClass().getClassLoader().getResource(
						"jmetest/data/texture/Detail.jpg" ),
				Texture.MM_LINEAR_LINEAR,
				Texture.FM_LINEAR );

		ts.setTexture( t2, 1 );
		t2.setWrap( Texture.WM_WRAP_S_WRAP_T );

		t1.setApply( Texture.AM_COMBINE );
		t1.setCombineFuncRGB( Texture.ACF_MODULATE );
		t1.setCombineSrc0RGB( Texture.ACS_TEXTURE );
		t1.setCombineOp0RGB( Texture.ACO_SRC_COLOR );
		t1.setCombineSrc1RGB( Texture.ACS_PRIMARY_COLOR );
		t1.setCombineOp1RGB( Texture.ACO_SRC_COLOR );
		t1.setCombineScaleRGB( 1.0f );

		t2.setApply( Texture.AM_COMBINE );
		t2.setCombineFuncRGB( Texture.ACF_ADD_SIGNED );
		t2.setCombineSrc0RGB( Texture.ACS_TEXTURE );
		t2.setCombineOp0RGB( Texture.ACO_SRC_COLOR );
		t2.setCombineSrc1RGB( Texture.ACS_PREVIOUS );
		t2.setCombineOp1RGB( Texture.ACO_SRC_COLOR );
		t2.setCombineScaleRGB( 1.0f );
		tb.setRenderState( ts );

		terrain.attachChild( tb );

		return terrain;
	}
}