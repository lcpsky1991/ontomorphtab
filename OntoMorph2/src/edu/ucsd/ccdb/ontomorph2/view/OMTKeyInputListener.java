package edu.ucsd.ccdb.ontomorph2.view;

import org.fenggui.Display;

import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.input.KeyInputListener;
import com.jme.input.MouseInput;
import com.jme.math.Vector3f;

import edu.ucsd.ccdb.ontomorph2.app.OntoMorph2;
import edu.ucsd.ccdb.ontomorph2.core.scene.TangibleManager;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Tangible;
import edu.ucsd.ccdb.ontomorph2.util.FengJMEInputHandler;
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
	
	public float keyPressActionRate = 5.0f; //the rate of rotation by a single key press
	boolean pointerEnabled = true;
	
	FengJMEInputHandler guiInput;
	Display disp;
	
	public OMTKeyInputListener(Display disp, FengJMEInputHandler guiInput){
		this.disp = disp;
		this.guiInput = guiInput;
	}
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
		if (guiInput.wasKeyHandled()){
			//Unassigns the keys program from JME
			KeyBindingManager.getKeyBindingManager().remove("toggle_pause");
			KeyBindingManager.getKeyBindingManager().remove("step");
			KeyBindingManager.getKeyBindingManager().remove("toggle_wire");
			KeyBindingManager.getKeyBindingManager().remove("toggle_lights");
			KeyBindingManager.getKeyBindingManager().remove("toggle_bounds");
			KeyBindingManager.getKeyBindingManager().remove("toggle_normals");
			KeyBindingManager.getKeyBindingManager().remove("camera_out");
			KeyBindingManager.getKeyBindingManager().remove("mem_report");
			return;

		}	
		//don't process any released events for now
		//may consider implementing handlers that operate in a loop as long
		//as a button stays pressed later though.  This may need to be done in a
		//separate thread or using JME's controller mechanism though -SL

		/////////////////////////////////////////////
		// inside IF statement handles repeating key actions
		if (!pressed)
		{

			return;
		}
		
		//call to set the JME Action Keys 
		setJMEActionKeys();
		
		switch(keyCode)
		{
		case KeyInput.KEY_ESCAPE:
			//Bind the Escape key to kill our app
			View.getInstance().finish();
			break;
		
        ////////////////////////////////////////////			
        //assign the camera to up, down, left, right
	    ////////////////////////////////////////////
		case KeyInput.KEY_ADD:
		case KeyInput.KEY_EQUALS: //for shift not pressed;
			View.getInstance().getCameraView().moveForward(keyPressActionRate);
			break;
		case KeyInput.KEY_SUBTRACT:
		case KeyInput.KEY_MINUS: //for no-shift control
			View.getInstance().getCameraView().moveBackward(keyPressActionRate);
			break;
		case KeyInput.KEY_LEFT:
			View.getInstance().getCameraView().turnCounterClockwise(keyPressActionRate);
			break;
		case KeyInput.KEY_RIGHT:
			View.getInstance().getCameraView().turnClockwise(keyPressActionRate);
			break;
		case KeyInput.KEY_UP:
			View.getInstance().getCameraView().turnUp(keyPressActionRate);
			break;
		case KeyInput.KEY_DOWN:
			View.getInstance().getCameraView().turnDown(keyPressActionRate);
			break;
		case KeyInput.KEY_1:
			//object_sphere
			//View.getInstance().getCamera().SphereLeftRotation(keyPressActionRate);
			break;
		case KeyInput.KEY_RBRACKET:
			View.getInstance().getCameraView().zoomIn(keyPressActionRate);
			break;		
		case KeyInput.KEY_LBRACKET:
			View.getInstance().getCameraView().zoomOut(keyPressActionRate);
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
				View.getInstance().getCameraView().reset();
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
		case KeyInput.KEY_A:
			View.getInstance().getCameraView().rotateCameraAbout(null, 90f,0);
			break;
		case KeyInput.KEY_BACK:
			{
				//delete
			}
			break;
		case KeyInput.KEY_M:
			//toggle mouse
			if (OntoMorph2.isDebugMode()) {
				pointerEnabled = !pointerEnabled;
				
				if (pointerEnabled)
				{
					View.getInstance().getFPSHandler().setEnabled(false);
					MouseInput.get().setCursorVisible(true);
				}
				else
				{
					View.getInstance().getFPSHandler().setEnabled(true);
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
				
				ViewCamera camNode = View.getInstance().getCameraView();
				
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
	
	public float getKeyPressActionRate() 
	{
		return this.keyPressActionRate;
	}
		
	//sets the JME Action keys after they have been disable if there was a fenggui focus
	public void setJMEActionKeys(){
		 /** Assign key P to action "toggle_pause". */
        KeyBindingManager.getKeyBindingManager().set( "toggle_pause",
                KeyInput.KEY_P );
        /** Assign key ADD to action "step". */
        KeyBindingManager.getKeyBindingManager().set( "step",
                KeyInput.KEY_ADD );
        /** Assign key T to action "toggle_wire". */
        KeyBindingManager.getKeyBindingManager().set( "toggle_wire",
                KeyInput.KEY_T );
        /** Assign key L to action "toggle_lights". */
        KeyBindingManager.getKeyBindingManager().set( "toggle_lights",
                KeyInput.KEY_L );
        /** Assign key B to action "toggle_bounds". */
        KeyBindingManager.getKeyBindingManager().set( "toggle_bounds",
                KeyInput.KEY_B );
        /** Assign key N to action "toggle_normals". */
        KeyBindingManager.getKeyBindingManager().set( "toggle_normals",
                KeyInput.KEY_N );
        /** Assign key C to action "camera_out". */
        KeyBindingManager.getKeyBindingManager().set( "camera_out",
                KeyInput.KEY_C );
        /** Assign key R to action "mem_report". */
        KeyBindingManager.getKeyBindingManager().set("mem_report",
                KeyInput.KEY_R);

	}
}
