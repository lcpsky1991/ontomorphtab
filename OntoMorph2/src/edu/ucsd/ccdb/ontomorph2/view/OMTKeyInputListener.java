package edu.ucsd.ccdb.ontomorph2.view;

import com.jme.input.KeyInput;
import com.jme.input.KeyInputListener;
import com.jme.input.MouseInput;
import com.jme.math.Vector3f;

import edu.ucsd.ccdb.ontomorph2.app.OntoMorph2;
import edu.ucsd.ccdb.ontomorph2.core.scene.TangibleManager;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Tangible;
import edu.ucsd.ccdb.ontomorph2.util.FocusManager;
import edu.ucsd.ccdb.ontomorph2.util.Log;

/**
 * Handles key input via JME directly (as opposed to via FengGUI)
 * Avoids use of KeyBindingManager and places execution code inline 
 * with key detection code, which avoids updates in multiple methods 
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 * @author caprea
 */
public class OMTKeyInputListener implements KeyInputListener {
	
	float keyPressActionRate = 20.0f; //the rate of rotation by a single key press
	boolean pointerEnabled = true;
	
	/**
	 * Handles key presses.
	 * 
	 * pressed is true if pressed, false if released
	 * 
	 * Unlike previous code, this will only get called once per key press and once
	 * per key release.. not multiple times while a key is pressed
	 */
	public void onKey(char character, int keyCode, boolean pressed) {
		//don't handle if focus manager has another widget focused
		if (FocusManager.get().isWidgetFocused()) return;
		
		//don't process any released events for now
		//may consider implementing handlers that operate in a loop as long
		//as a button stays pressed later though.  This may need to be done in a
		//separate thread or using JME's controller mechanism though -SL
		if (!pressed) return;
		
		switch(keyCode) {
		case KeyInput.KEY_ESCAPE:
			//Bind the Escape key to kill our app
			View.getInstance().finish();
			break;
		
        ////////////////////////////////////////////			
        //assign the camera to up, down, left, right
	    ////////////////////////////////////////////
		case KeyInput.KEY_ADD:
		case KeyInput.KEY_EQUALS: //for shift not pressed;
			View.getInstance().getCameraNode().moveForward(keyPressActionRate);
			break;
		case KeyInput.KEY_SUBTRACT:
		case KeyInput.KEY_MINUS: //for no-shift control
			View.getInstance().getCameraNode().moveBackward(keyPressActionRate);
			break;
		case KeyInput.KEY_LEFT:
			View.getInstance().getCameraNode().turnCounterClockwise(keyPressActionRate);
			break;
		case KeyInput.KEY_RIGHT:
			View.getInstance().getCameraNode().turnClockwise(keyPressActionRate);
			break;
		case KeyInput.KEY_UP:
			View.getInstance().getCameraNode().turnUp(keyPressActionRate);
			break;
		case KeyInput.KEY_DOWN:
			View.getInstance().getCameraNode().turnDown(keyPressActionRate);
			break;
		case KeyInput.KEY_1:
			//object_sphere
			//View.getInstance().getCamera().SphereLeftRotation(keyPressActionRate);
			break;
		case KeyInput.KEY_RBRACKET:
			View.getInstance().getCameraNode().zoomIn(keyPressActionRate);
			break;		
		case KeyInput.KEY_LBRACKET:
			View.getInstance().getCameraNode().zoomOut(keyPressActionRate);
			break;
			
		//////////////////////////////////////////
	    // Miscellaneous and debug keys
		// (this stuff should probably only run when debug mode is on)	
	    /////////////////////////////////////////
		case KeyInput.KEY_D:
			//toggle debug
			OntoMorph2.setDebugMode(!OntoMorph2.isDebugMode());
			{
				Log.warn("Debug Mode set to: " + OntoMorph2.isDebugMode());
			}
			break;
		case KeyInput.KEY_R: 
			//assign 'R' to reload the view to inital state //reinit
			if (OntoMorph2.isDebugMode()) {
				Log.warn( "\nResetting");
				View.getInstance().getCameraNode().reset();
			}
			break;
		case KeyInput.KEY_U:
            //update
			if (OntoMorph2.isDebugMode()) {
				for (Tangible t : TangibleManager.getInstance().getCells())
				{
					t.unselect();
				}
			}
			break;
		case KeyInput.KEY_M:
			//toggle mouse
			if (OntoMorph2.isDebugMode()) {
				pointerEnabled = !pointerEnabled;
				
				if (pointerEnabled)
				{
					View.getInstance().getFPHandler().setEnabled(false);
					MouseInput.get().setCursorVisible(true);
				}
				else
				{
					View.getInstance().getFPHandler().setEnabled(true);
					MouseInput.get().setCursorVisible(false);
				}
			}
			break;
		case KeyInput.KEY_S:
			//show selected
			if (OntoMorph2.isDebugMode()) {
				for (Tangible t : TangibleManager.getInstance().getSelected()) {
					Log.warn(t.toString());
				}
				Log.warn("" + TangibleManager.getInstance().countSelected() + " total");
			}
			break;
		case KeyInput.KEY_I:
			//info + mem_report
			if (OntoMorph2.isDebugMode()) {
				Vector3f unit = new Vector3f(0f,0f,1f);
				
				ViewCamera camNode = View.getInstance().getCameraNode();
				
				Log.warn( 
						"\nAngle Between Cam and Origin: " + unit.angleBetween(camNode.getCamera().getDirection()) +
						"\nAxes: " + "" +
						"\nLoc Rotation: " + camNode.getLocalRotation() +
						"\nWorld Rot: "+ camNode.getWorldRotation() + 
						"\nDirection: " + camNode.getCamera().getDirection() +
						"\nLoc Trans: "+ camNode.getLocalTranslation() +
						"\nWorld Trans: "+ camNode.getWorldTranslation() +
						"\nZoom: " + camNode.getZoom());
				
				long totMem = Runtime.getRuntime().totalMemory();
				long freeMem = Runtime.getRuntime().freeMemory();
				long maxMem = Runtime.getRuntime().maxMemory();
				
				Log.warn("|*|*|  Memory Stats  |*|*|");
				Log.warn("Total memory: " + (totMem >> 10) + " kb");
				Log.warn("Free memory: " + (freeMem >> 10) + " kb");
				Log.warn("Max memory: " + (maxMem >> 10) + " kb");
			}
			break;
		}
	}
	
	public float getKeyPressActionRate() {
		return this.keyPressActionRate;
	}
}
