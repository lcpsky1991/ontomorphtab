package wbctest.view;

import java.util.ArrayList;
import java.util.List;

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.input.KeyInputListener;
import com.jme.math.FastMath;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.scene.shape.Sphere;

import edu.ucsd.ccdb.ontomorph2.util.FocusManager;
import edu.ucsd.ccdb.ontomorph2.view.ViewCamera;

/**
 * Test case to suss out issues with the camera
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 *
 */
public class TestViewCamera extends SimpleGame{
	List<Sphere> spheres = new ArrayList<Sphere>();
	ViewCamera cam = null;
	/**
	 * Entry point for the test,
	 * @param args
	 */
	public static void main(String[] args) {
		TestViewCamera app = new TestViewCamera();
		app.setDialogBehaviour(NEVER_SHOW_PROPS_DIALOG);
		app.start();
		
	}
	
/**
 * This is aimed at testing the Camera.getDistanceToPosition method
 * Create a ring of spheres and a line of spheres and make sure the distances make sense.
 */
	protected void simpleInitGame() {
		display.setTitle("Test distance from camera");
		KeyBindingManager.getKeyBindingManager().set("info", KeyInput.KEY_I);
		
		for (int i = 0; i < 360; i += 360/8) {
			Sphere s = new Sphere("circle sphere " + i, 10, 10, 0.5f);
			float rads = FastMath.DEG_TO_RAD*i;
			float x = FastMath.sin(rads);
			float z = FastMath.cos(rads);
			float scale = 5;
			s.setLocalTranslation(x*scale, 0f, z*scale);
			s.setModelBound(new BoundingBox());
			s.updateModelBound();
			spheres.add(s);
			rootNode.attachChild(s);
		}
		
		for (int i = -4; i < 4; i++) {
			Sphere s = new Sphere("linear sphere " + i, 10, 10, 0.5f);
			float scale = 5;
			s.setLocalTranslation(i*scale, 0f, 0f);
			s.setModelBound(new BoundingBox());
			s.updateModelBound();
			spheres.add(s);
			rootNode.attachChild(s);
		}
		
		for (int i = 0; i < 10; i++) {
			Sphere s = new Sphere("tight sphere " + i, 10, 10, 0.1f);
			float scale = 5;
			s.setLocalTranslation(0f, (float)(i*scale*0.005+2), 0f);
			s.setModelBound(new BoundingBox());
			s.updateModelBound();
			spheres.add(s);
			rootNode.attachChild(s);
		}
		
		cam = new ViewCamera();
		
		KeyInput.get().addListener(new KeyInputListener() {

			public void onKey(char character, int keyCode, boolean pressed) {
				if (!pressed)  return;
				
				switch(keyCode) {
				case KeyInput.KEY_I:
					doTest();
					break;
				}
			}
			
		});
	}
	
	private void doTest() {
		for (Sphere s : spheres) {
			System.out.println("Distance to " + s.getName() + " at position " + s.getLocalTranslation());
			Vector3f screenCoordinates = cam.getCamera().getScreenCoordinates(s.getLocalTranslation());
			Vector2f screenCoords = new Vector2f(screenCoordinates.x, screenCoordinates.y);
			System.out.println("  Screen coordinates :" + screenCoordinates);
			System.out.println("  Alternative World Coordinates: " 
						+ cam.getCamera().getWorldCoordinates(screenCoords, screenCoordinates.z));
		}
	}
	
}

	
