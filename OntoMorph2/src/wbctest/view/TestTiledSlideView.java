package wbctest.view;

import java.io.File;
import java.net.URI;
import java.nio.ByteBuffer;

import com.jme.app.SimpleGame;
import com.jme.image.Image;
import com.jme.image.Texture;
import com.jme.scene.shape.Quad;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;

import edu.ucsd.ccdb.ontomorph2.core.scene.Scene;
import edu.ucsd.ccdb.ontomorph2.core.tangible.slide.Slide;
import edu.ucsd.ccdb.ontomorph2.core.tangible.slide.TiledSlide;
import edu.ucsd.ccdb.ontomorph2.view.scene.SlideView;
import edu.ucsd.ccdb.tiff.jviewerBufferedImage;


public class TestTiledSlideView extends SimpleGame{
	
	
	/**
	 * Entry point for the test,
	 * @param args
	 */
	public static void main(String[] args) {
		TestTiledSlideView app = new TestTiledSlideView();
		app.setDialogBehaviour(NEVER_SHOW_PROPS_DIALOG);
		app.start();
	}
	
	/**
	 * @see com.jme.app.SimpleGame#initGame()
	 */
	protected void simpleInitGame() {
		display.setTitle("Test Tiled Slide View");
		
		test4();
	}
	
	protected void test1() {
		Slide slide = null;
		slide = new Slide("hippo_slice1", new File(Scene.imgDir + "hippo_slice1.jpg").toURI(), 0.87f);
		slide.setScale(170);
		
		SlideView s = new SlideView(slide);
		
		rootNode.attachChild(s);
	}
	
	protected void test2() {
		URI img = null;
		img = new File(Scene.imgDir + "hippo_slice1.jpg").toURI();
		
		
		Slide slide1 = new Slide("s1", img, 0.87f);
		Slide slide2 = new Slide("s2", img, 0.87f);
		//slide.setRelativeRotation(new RotationQuat(new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD*90, OMTVector.UNIT_Y)));
		//slide.setRelativeScale(1);
		
		
		SlideView s = new SlideView(slide1);
		SlideView s2 = new SlideView(slide2);
		
		rootNode.attachChild(s);
		rootNode.attachChild(s2);
	}
	
	protected void test3() {
		jviewerBufferedImage test = new jviewerBufferedImage();
		int[] vals = test.getTestImageArray();
		byte[] byteArray = new byte[vals.length];
		for (int i = 0; i < vals.length; i++) {
			byteArray[i] = (byte)vals[i];
		}
		
		ByteBuffer byteBuffer = ByteBuffer.wrap(byteArray);
		Image i = new Image(Image.GUESS_FORMAT, 1405,1080, byteBuffer);
		
		Quad q = new Quad("name", 1405, 1080);

		Texture t = new Texture();
		t.setImage(i);

		TextureState textureState = DisplaySystem.getDisplaySystem().getRenderer().createTextureState();
		textureState.setTexture(t);
		textureState.setEnabled(true);
		
		q.setRenderState(textureState);
		q.updateRenderState();
		
		rootNode.attachChild(q);
	}
	
	protected void test4() {
		TiledSlide ts = new TiledSlide("ts", null, 1f);
		SlideView s = new SlideView(ts);
		
		rootNode.attachChild(s);
	}
	
	
}

	
