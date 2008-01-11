package edu.ucsd.ccdb.ontomorph2.misc;

import java.net.URL;
import java.util.HashMap;

import com.jme.app.AbstractGame;
import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.input.AbsoluteMouse;
import com.jme.input.ChaseCamera;
import com.jme.input.FirstPersonHandler;
import com.jme.input.KeyInput;
import com.jme.input.MouseInput;
import com.jme.input.ThirdPersonHandler;
import com.jme.input.thirdperson.ThirdPersonMouseLook;
import com.jme.intersection.BoundingPickResults;
import com.jme.intersection.PickResults;
import com.jme.math.FastMath;
import com.jme.math.Ray;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.scene.Spatial;
import com.jme.scene.shape.Box;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.util.TextureManager;

public class HelloMousePick extends SimpleGame{
	//this will be my mouse
	AbsoluteMouse am;
	
	Box b;
	
	PickResults pr;
	
	public static void main(String[] args) {
		HelloMousePick app = new HelloMousePick();
		app.setDialogBehaviour(AbstractGame.ALWAYS_SHOW_PROPS_DIALOG);
		app.start();
	}
	
	HashMap handlerProps = null;
	ChaseCamera chaser = null;
	
	
	protected void simpleInitGame() {
		//create a new mouse. restrict its movements to the display screen.
		am = new AbsoluteMouse("The Mouse", display.getWidth(), display.getHeight());
		
		//Get a picture for my mouse.
		TextureState ts = display.getRenderer().createTextureState();
		URL cursorLoc = HelloMousePick.class.getClassLoader().getResource(
				"jmetest/data/cursor/cursor1.png");
		Texture t = TextureManager.loadTexture(cursorLoc, Texture.MM_LINEAR, 
				Texture.FM_LINEAR);
		ts.setTexture(t);
		am.setRenderState(ts);
		
		//Make the mouse's background blend with what's already there
		AlphaState as = display.getRenderer().createAlphaState();
		as.setBlendEnabled(true);
		as.setSrcFunction(AlphaState.SB_SRC_ALPHA);
		as.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_ALPHA);
		as.setTestEnabled(true);
		as.setTestFunction(AlphaState.TF_GREATER);
		am.setRenderState(as);
		
		//Get the mouse input device and assign it to the absolutemouse
		//move the mouse to the middle of the screen to start with
		am.setLocalTranslation(new Vector3f(display.getWidth() / 2, display.getHeight() /2, 0));
		
		//assign the mouse to an input handler
		am.registerWithInputHandler(input);
		
		//create the box in the middle. give it a bounds
		b = new Box("My Box", new Vector3f(-1, -1, -1), new Vector3f(1,1,1));
		b.setModelBound(new BoundingBox());
		b.updateModelBound();
		//attach children
		rootNode.attachChild(b);
		rootNode.attachChild(am);
		//remove all the lightstates so we can see the per-vertex colors
		lightState.detachAll();
		
		b.setLightCombineMode( LightState.OFF );
		pr = new BoundingPickResults();
		((FirstPersonHandler) input ).getMouseLookHandler().setEnabled(false);
		((FirstPersonHandler) input ).getKeyboardLookHandler().setEnabled(false);
		
	}
	
	protected void initView(Spatial target) {
		handlerProps = new HashMap();
		handlerProps.put(ThirdPersonHandler.PROP_ROTATEONLY, "true");
		handlerProps.put(ThirdPersonHandler.PROP_DOGRADUAL, "true");
		handlerProps.put(ThirdPersonHandler.PROP_TURNSPEED, "3.1415");
		handlerProps.put(ThirdPersonHandler.PROP_LOCKBACKWARDS, "true");
		handlerProps.put(ThirdPersonHandler.PROP_STRAFETARGETALIGN, "true");
		handlerProps.put(ThirdPersonHandler.PROP_CAMERAALIGNEDMOVE, "false");

		handlerProps.put(ThirdPersonHandler.PROP_KEY_FORWARD, ""+KeyInput.KEY_W);
		handlerProps.put(ThirdPersonHandler.PROP_KEY_LEFT, ""+KeyInput.KEY_A);
		handlerProps.put(ThirdPersonHandler.PROP_KEY_BACKWARD, ""+KeyInput.KEY_S);
		handlerProps.put(ThirdPersonHandler.PROP_KEY_RIGHT, ""+KeyInput.KEY_D);
		handlerProps.put(ThirdPersonHandler.PROP_KEY_STRAFELEFT, ""+KeyInput.KEY_Q);
		handlerProps.put(ThirdPersonHandler.PROP_KEY_STRAFERIGHT, ""+KeyInput.KEY_E);
		input = new ThirdPersonHandler(target, cam, handlerProps);
		input.setActionSpeed(250.0f);

		HashMap chaserProps = new HashMap();
		chaserProps.put(ChaseCamera.PROP_ENABLESPRING, "true");
		chaserProps.put(ChaseCamera.PROP_DAMPINGK, "55.0");
		chaserProps.put(ChaseCamera.PROP_SPRINGK, "756.25");
		chaserProps.put(ChaseCamera.PROP_MAXDISTANCE, "0.0");
		chaserProps.put(ChaseCamera.PROP_MINDISTANCE, "0.0");
		chaserProps.put(ChaseCamera.PROP_INITIALSPHERECOORDS, new Vector3f(65.0f, 0f, FastMath.DEG_TO_RAD * 12.0f));
		chaserProps.put(ChaseCamera.PROP_STAYBEHINDTARGET, "true");
		chaserProps.put(ChaseCamera.PROP_TARGETOFFSET, new Vector3f(0f, ((BoundingBox) target.getWorldBound()).yExtent * 1.5f, 0f));
		chaserProps.put(ThirdPersonMouseLook.PROP_ENABLED, "true");
		chaserProps.put(ThirdPersonMouseLook.PROP_MAXASCENT, "" + FastMath.DEG_TO_RAD * 85);
		chaserProps.put(ThirdPersonMouseLook.PROP_MINASCENT, "" + FastMath.DEG_TO_RAD * -15);
		chaserProps.put(ThirdPersonMouseLook.PROP_INVERTEDY, "false");
		chaserProps.put(ThirdPersonMouseLook.PROP_ROTATETARGET, "false");
		chaserProps.put(ThirdPersonMouseLook.PROP_MINROLLOUT, "6.2831855");
		chaserProps.put(ThirdPersonMouseLook.PROP_MAXROLLOUT, "240.0");
		chaserProps.put(ThirdPersonMouseLook.PROP_MOUSEXMULT, "2.0");
		chaserProps.put(ThirdPersonMouseLook.PROP_MOUSEYMULT, "30.0");
		chaserProps.put(ThirdPersonMouseLook.PROP_MOUSEROLLMULT, "50.0");
		chaserProps.put(ThirdPersonMouseLook.PROP_LOCKASCENT, "true");
		chaser = new ChaseCamera(cam, target, chaserProps);
		chaser.setActionSpeed(1.0f);
	}
	
	protected void simpleUpdate() {
		//get the mouse input device from the jME mouse
		//Is button - down? Button 0 is left click
		if (MouseInput.get().isButtonDown(0)) {
			Vector2f screenPos = new Vector2f();
			//get the position that the mouse is pointing to
			screenPos.set(am.getHotSpotPosition().x, am.getHotSpotPosition().y);
			//get the world location of that X,Y value
			Vector3f worldCoords = display.getWorldCoordinates(screenPos, 0);
			Vector3f worldCoords2 = display.getWorldCoordinates(screenPos,1);
			System.out.println(worldCoords);
			//create a ray starting from the camera, and going in the 
			//direction of the mouse's location
			Ray mouseRay = new Ray(worldCoords, worldCoords2.subtractLocal(worldCoords).normalizeLocal());
			//does the mouse's ray intersect the box's world bounds?
			pr.clear();
			rootNode.findPick(mouseRay, pr);
			
			for(int i = 0; i < pr.getNumber(); i++) {
				initView(pr.getPickData(i).getTargetMesh().getParentGeom());
				//pr.getPickData(i).getTargetMesh().setRandomColors();
				//pr.getPickData(i).getTargetMesh().reg
			}
		}
	}
}
