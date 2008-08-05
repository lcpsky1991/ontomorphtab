package wbctest.view;

import java.io.File;
import java.net.MalformedURLException;

import com.jme.app.SimpleGame;

import edu.ucsd.ccdb.ontomorph2.core.scene.Scene;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Slide;
import edu.ucsd.ccdb.ontomorph2.core.spatial.OMTVector;
import edu.ucsd.ccdb.ontomorph2.core.spatial.PositionVector;
import edu.ucsd.ccdb.ontomorph2.core.spatial.RotationVector;
import edu.ucsd.ccdb.ontomorph2.view.scene.QuadSlideView;
import edu.ucsd.ccdb.ontomorph2.view.scene.SlideView;


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
		
		test2();
	}
	
	protected void test1() {
		Slide slide = null;
		try {
			slide = new Slide(new File(Scene.imgDir + "hippo_slice1.jpg").toURI().toURL(), 
					new PositionVector(), new RotationVector(), 170, 0.87f);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		SlideView s = new SlideView(slide, display);
		
		rootNode.attachChild(s);
	}
	
	protected void test2() {
		Slide slide = null;
		try {
			slide = new Slide(new File(Scene.imgDir + "hippo_slice1.jpg").toURI().toURL(), 
					new PositionVector(), new RotationVector(), 170, 0.87f);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		QuadSlideView s = new QuadSlideView(slide, display);
		
		rootNode.attachChild(s);
	}
	
	
}

	
