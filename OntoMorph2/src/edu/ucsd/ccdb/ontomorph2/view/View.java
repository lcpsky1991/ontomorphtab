

package edu.ucsd.ccdb.ontomorph2.view;


import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fenggui.event.mouse.MouseButton;

import com.jme.app.AbstractGame;
import com.jme.app.BaseSimpleGame;
import com.jme.image.Texture;
import com.jme.input.FirstPersonHandler;
import com.jme.input.InputHandler;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.input.MouseInput;
import com.jme.input.RelativeMouse;
import com.jme.input.action.InputActionEvent;
import com.jme.input.action.KeyInputAction;
import com.jme.input.action.MouseInputAction;
import com.jme.input.action.MouseLook;
import com.jme.intersection.PickData;
import com.jme.intersection.PickResults;
import com.jme.intersection.TrianglePickResults;
import com.jme.math.Ray;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Geometry;
import com.jme.scene.TriMesh;
import com.jme.scene.batch.GeomBatch;
import com.jme.scene.state.LightState;
import com.jme.system.DisplaySystem;
import com.jme.util.geom.Debugger;

import edu.ucsd.ccdb.ontomorph2.app.OntoMorph2;
import edu.ucsd.ccdb.ontomorph2.core.scene.Scene;
import edu.ucsd.ccdb.ontomorph2.core.scene.TangibleManager;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Tangible;
import edu.ucsd.ccdb.ontomorph2.core.spatial.OMTVector;
import edu.ucsd.ccdb.ontomorph2.util.Log;
import edu.ucsd.ccdb.ontomorph2.view.gui2d.ContextMenu;
import edu.ucsd.ccdb.ontomorph2.view.scene.NeuronMorphologyView;
import edu.ucsd.ccdb.ontomorph2.view.scene.SegmentView;
import edu.ucsd.ccdb.ontomorph2.view.scene.TangibleView;

//=========

/**
 * Defines the <a href="http://openccdb.org/wiki/index.php/Brain_Catalog_Architecture">view 
 * of the system in a model-view-controller architecture</a>. 
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 * @author caprea
 */
public class View extends BaseSimpleGame {

//	==================================
	// DECLARES
	// - used for manipulating the objects, setting the mode says what you're doing with dragging
	//==================================
	public static final int METHOD_NONE = 0;
	public static final int METHOD_PICK = 1;
	public static final int METHOD_MOVE = 2;
	public static final int METHOD_SCALE = 4;
	public static final int METHOD_ROTATEX = 8;
	public static final int METHOD_ROTATEY = 16;
	public static final int METHOD_ROTATEZ = 32;
	public static final int METHOD_LOOKAT = 64;

	private static int manipulation = METHOD_PICK; //use accesor
	
	private static View instance = null;
	
//	The trimesh that i will change
	TriMesh square;
	
	// a scale of my current texture values
	float coordDelta;
	private Scene _scene = null;

	
	//=================================
	// Global Interface-Objects
	//=================================
	public ViewCamera camNode;					//thisobject needed for manipulating the camera in a simple way
	RelativeMouse amouse; 						//the mouse object ref to entire screen, used to hide and show the mouse?
	PickData currentPick;							//made global because it's a conveiniant way to deselect the previous selection since it's stored
	
	//manip not stored locally anymore
		
	FirstPersonHandler fpHandler = null;
	MouseLook looker;	//not used
	private boolean pointerEnabled = false;
	
	float keyPressActionRate = 1.0f; //the rate of rotation by a single key press
	org.fenggui.Display disp; // FengGUI's display
	
	View3D view3D = null;
	
	
	/**
	 * Returns the singleton instance.
	 @return	the singleton instance
	 */
	public static View getInstance() {
		if (instance == null) {
			instance = new View();
		}
		return instance;
	}
	
	public void setManipulation(int m)
	{
		manipulation = m;
		Log.warn("Manipulation method set to: " + m);
		
	}
	
	protected View() 
	{
		//this.setDialogBehaviour(SimpleGame.ALWAYS_SHOW_PROPS_DIALOG);	
		this.setDialogBehaviour(FIRSTRUN_OR_NOCONFIGFILE_SHOW_PROPS_DIALOG);
		view3D = new View3D();
		
	}
	
	/**
	 * Set the Scene object associated with this view
	 * @param scene
	 * @see Scene
	 */
	public void setScene(Scene scene){
		_scene = scene;
	}
	
	/**
	 * Get the Scene object associated with this view.
	 * @return The currently visible scene
	 * @see Scene
	 */
	public Scene getScene() {
		return _scene;
	}

	
	protected void simpleInitGame() {
		//as a hack, calling the main application class to do initialization
		//this is because model loading needs to have the view running in order to work
		
		OntoMorph2.initialization();
		display.getRenderer().setBackgroundColor(ColorRGBA.black); //Set a black background.
		display.setTitle("Whole Brain Catalog");
		
		rootNode.attachChild(view3D);

		this.camNode = new ViewCamera();
		this.cam = camNode.getCamera();
		
		rootNode.attachChild(camNode);
		
		//Section for setting up the mouse and other input controls	
		configureControls();
		
		//Remove lighting for rootNode so that it will use our basic colors
		rootNode.setLightCombineMode(LightState.OFF);
		
		disp = View2D.getInstance();
	}
    	
	private void configureControls()
	{
		
		fpHandler = new FirstPersonHandler(cam, 50, camNode.getRotationRate()); //(cam, moveSpeed, turnSpeed)
		
		//This is where we disable the FPShooter controls that are created by default by JME	
        input = fpHandler;
        
        //Disable both of these because I want to track things with the camera
        
        fpHandler.getKeyboardLookHandler().setEnabled( false );
        fpHandler.getMouseLookHandler().setEnabled( false);
		
        input.clearActions();	//removes all input actions not specifically programmed
        
        amouse = new RelativeMouse("The Mouse");
        amouse.registerWithInputHandler(input);					// Assign the mouse to an input handler
        rootNode.attachChild(amouse);	        
        
		//Bind the Escape key to kill our test app
		KeyBindingManager.getKeyBindingManager().set("quit", KeyInput.KEY_ESCAPE);
		
		//assign 'R' to reload the view to inital state //reinit
		KeyBindingManager.getKeyBindingManager().set("reset", KeyInput.KEY_R);
		
		//assignt he camera to up, down, left, right ;	ADD does not overwrite
		KeyBindingManager.getKeyBindingManager().set("cam_forward", KeyInput.KEY_ADD);
		KeyBindingManager.getKeyBindingManager().add("cam_forward", KeyInput.KEY_EQUALS); //for shift not pressed;
		KeyBindingManager.getKeyBindingManager().set("cam_back", KeyInput.KEY_SUBTRACT);
		KeyBindingManager.getKeyBindingManager().add("cam_back", KeyInput.KEY_MINUS); //for no-shift control
		KeyBindingManager.getKeyBindingManager().set("cam_turn_ccw", KeyInput.KEY_LEFT);
		KeyBindingManager.getKeyBindingManager().set("cam_turn_cw", KeyInput.KEY_RIGHT);
		KeyBindingManager.getKeyBindingManager().set("cam_turn_up", KeyInput.KEY_UP);
		KeyBindingManager.getKeyBindingManager().set("cam_turn_down", KeyInput.KEY_DOWN);
		
		KeyBindingManager.getKeyBindingManager().set("toggleMouse", KeyInput.KEY_M);
		KeyBindingManager.getKeyBindingManager().set("show_selected", KeyInput.KEY_S);
		
		KeyBindingManager.getKeyBindingManager().set("info", KeyInput.KEY_I);
		KeyBindingManager.getKeyBindingManager().add("mem_report", KeyInput.KEY_I);
		
		KeyBindingManager.getKeyBindingManager().set("zoom_in", KeyInput.KEY_RBRACKET);
		KeyBindingManager.getKeyBindingManager().set("zoom_out", KeyInput.KEY_LBRACKET);
		
		// We want a cursor to interact with FengGUI
		MouseInput.get().setCursorVisible(true);
		
		//(InputActionInterface action, java.lang.String deviceName, int button, int axis, boolean allowRepeats) 
        input.addAction( mouseAction, InputHandler.DEVICE_MOUSE, InputHandler.BUTTON_ALL, InputHandler.AXIS_ALL, false );

	}
	
	
	private void handleMouseInput()
	{
		//handle mouse input
		//NOTE:there is a more sophisticated way of dealing with mouse input than this (pickresults has handler)
		try
		{
			
			boolean mouseLook = false;
			int numMouseBut = MouseInput.get().getButtonCount();
			
        	
        	//====================================
        	//	WHEEL
        	//====================================
			
			float dx=MouseInput.get().getWheelDelta() / (keyPressActionRate * 20); //scale it by some factor so it's less jumpy
			if (dx != 0)	
			{
				//zoom camera if Z press
				if ( KeyInput.get().isKeyDown(KeyInput.KEY_Z) )
				{
					camNode.zoomIn(dx);	
				}
				//move camera if Z NOT pressed
				else
				{
					camNode.moveForward(dx);
				}
			}
			
			//====================================
			//	MIDDLE MOUSE
			//====================================
			//mouse look trumps all other actions
        	
        	//middle click manipulates camera OR leftANDright mouse button
			//mouselook is true if there is a middle mouse button and it's pressed down
			if ( numMouseBut >= 2 ) if (MouseInput.get().isButtonDown(2)) mouseLook = true;
			 
			//mouselook is also true if left mouse and right mouse are pressed
			if ( MouseInput.get().isButtonDown(0) && MouseInput.get().isButtonDown(1) ) mouseLook = true;
			
			if (mouseLook)
			{
				//find mouse change
				float mx = MouseInput.get().getXDelta() / 100.0f;
				float my = MouseInput.get().getYDelta() / 100.0f;

				camNode.turnClockwise(mx);
				camNode.turnUp(my);
			}
			else
			{
				//====================================
				//	RIGHT CLICK
				//====================================
				if (MouseInput.get().isButtonDown(1)) //right
				{	
					//MouseInput.get().setCursorVisible(false); //hide mouse cursor
					doPick();
					ContextMenu.getInstance().displayMenuFor(MouseInput.get().getXAbsolute(),
							MouseInput.get().getYAbsolute(),TangibleManager.getInstance().getSelected());
				}
				else
				{
					//MouseInput.get().setCursorVisible(true); //show mouse cursor
				}
				
				//====================================
				//	LEFT CLICK
				//====================================
				if (MouseInput.get().isButtonDown(0)) //left 
				{
					if (MouseInput.get().getXDelta() == 0 && MouseInput.get().getYDelta() == 0) {
						//not dragging, just clicking
						doPick();
					} else {
						//dragging
						manipulateCurrentSelection();
					}
					/*
					if (drag){
						//move, rotate, scale whatever is currently selected.. 
						//don't do new selections until mouse comes back up
					} else if (doubleClick){
						//do new selections
					} else if (singleClick) {
						
					}*/

				} //end if mouse button down
			} //end one-button mouse handling
		} //end try
		catch (Exception e)
		{
			Log.warn("Exception caught in View.handleMouseInput(): " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * Apply manipulations to the tangible that is currently selected
	 * Called during mouse handling
	 */
	private void manipulateCurrentSelection() 
	{		
		
		
		float mx = MouseInput.get().getXDelta();
		float my = MouseInput.get().getYDelta();
		//TODO: replace unity vectors with ones based on camera axis
		switch ( manipulation )
		{
		case METHOD_PICK:
			//do nothing
			break;
		case METHOD_MOVE:
			moveSelected(mx, my);
			break;
		case METHOD_ROTATEX:
			rotateSelected(mx, my, new OMTVector(1,0,0));
			break;
		case METHOD_ROTATEY:
			rotateSelected(mx, my,new OMTVector(0,1,0));
			break;
		case METHOD_ROTATEZ:
			rotateSelected(mx, my,new OMTVector(0,0,1));
			break;
		case METHOD_LOOKAT:
			/* needs to be re-engineered to deal with multiple selections
			if (manip != null)
				//TODO: fixme
				Log.warn("LOOK AT is broken");
			camNode.lookAt(manip.getAbsolutePosition() , new OMTVector(0,1,0)); //make the camera point a thte object in question
			*/
			break;
		case METHOD_SCALE:
			scaleSelected(mx, my);
			break;
		}
			
		
	}
	
	private void moveSelected(float mx, float my) {
		for (Tangible manip : TangibleManager.getInstance().getSelected())
		{
			manip.move(mx, my, new OMTVector(1,1,0));
		}
	}
	
	private void rotateSelected(float mx, float my, OMTVector v) {
		for (Tangible manip : TangibleManager.getInstance().getSelected())
		{
			manip.rotate(mx, my, v);
		}
	}
	
	private void scaleSelected(float mx, float my) {
		for (Tangible manip : TangibleManager.getInstance().getSelected()) {
			manip.scale(mx, my,  new OMTVector(1,1,1));
		}
	}
		
	
	private void doPick() {
		//get stuff we are trying to pick/select
		PickResults results = getPickResults();
		
		if ( results.getNumber() > 0)
		{
			currentPick = results.getPickData(0);	//take the closest pick and set
			
			doSelection(currentPick.getTargetMesh());
		
		} else {
			//if there are no results, unselect everything
			TangibleManager.getInstance().unselectAll();
		}
	}
	
	/**
	 * Give the PickResults object for the object the mouse is trying to select on the screen
	 * Called during mouse handling
	 */
	private PickResults getPickResults() {
//		because dendrites can be densely packed need precision of triangles instead of bounding boxes
		PickResults pr = new TrianglePickResults(); 
		
		
		//Get the position that the mouse is pointing to
		Vector2f mPos = new Vector2f();
		mPos.set(MouseInput.get().getXAbsolute() ,MouseInput.get().getYAbsolute() );
		
		// Get the world location of that X,Y value
		Vector3f farPoint = display.getWorldCoordinates(mPos, 1.0f);
		Vector3f closePoint = display.getWorldCoordinates(mPos, 0.0f);
		
		// Create a ray starting from the camera, and going in the direction
		// of the mouse's location
		Ray mouseRay = new Ray(closePoint, farPoint.subtractLocal(closePoint).normalizeLocal());
		
		// Does the mouse's ray intersect the box's world bounds?
		pr.clear();
		pr.setCheckDistance(true);  //this function is undocumented, orders the items in pickresults
		rootNode.findPick(mouseRay, pr);
		return pr;
	}
	
	
	
	/**
	 * Select the currently chosen object
	 * Called during mouse handling
	 */
	private void doSelection(GeomBatch geo) 
	{
		TangibleView tv = TangibleViewManager.getInstance().getTangibleView(geo.getParentGeom());
	
		if ( KeyInput.get().isShiftDown() )
		{
			//turn on multiselection
			TangibleManager.getInstance().setMultiSelect(true);
		}
		
		//if user selected a segment, assume they wanted to select the parent cell instead if control is down
		if ( tv instanceof SegmentView && KeyInput.get().isControlDown())
		{
			SegmentView pickSeg = (SegmentView) tv;
			
			pickSeg.getCorrespondingSegmentGroup().getParentCell().select();
			
		}
		else if (tv != null) 
		{
			tv.getModel().select();
		}
		
		//turn off multiselection
		TangibleManager.getInstance().setMultiSelect(false);
	}
	
	/**
	 * Handles the execution of code based on activated keys
	 *
	 */
	private void handleKeyInput() 
	{
		//key input handle
		{
			//exit the program cleanly on ESC
			if (isAction("quit")) finish();
			
			
			if (isAction("mem_report"))
			{
				 long totMem = Runtime.getRuntime().totalMemory();
	             long freeMem = Runtime.getRuntime().freeMemory();
	             long maxMem = Runtime.getRuntime().maxMemory();

	             Log.warn("|*|*|  Memory Stats  |*|*|");
	             Log.warn("Total memory: " + (totMem >> 10) + " kb");
	             Log.warn("Free memory: " + (freeMem >> 10) + " kb");
	             Log.warn("Max memory: " + (maxMem >> 10) + " kb");
			}
			
			if ( isAction("toggleMouse"))
			{
				pointerEnabled = !pointerEnabled;
				
				if (pointerEnabled)
				{
					fpHandler.setEnabled(false);
					MouseInput.get().setCursorVisible(true);
				}
				else
				{
					fpHandler.setEnabled(true);
					MouseInput.get().setCursorVisible(false);
				}
				
			}
				
			
			if ( isAction("show_selected"))
			{
				for (Tangible t : TangibleManager.getInstance().getSelected()) {
					System.out.println(t);
				}
				System.out.println("" + TangibleManager.getInstance().countSelected() + " total");
			}
			
			
			if ( isAction("cam_forward") || isAction("cam_forward_ns") ) 
			{
				camNode.moveForward(keyPressActionRate);
			}
			
			if ( isAction("cam_back") || isAction("cam_back_ns"))
			{
				camNode.moveBackward(keyPressActionRate);
			}

			if ( isAction("cam_turn_cw"))	
			{
				camNode.turnClockwise(keyPressActionRate);
			}
			
			if ( isAction("cam_turn_ccw"))	
			{ 
				camNode.turnCounterClockwise(keyPressActionRate);
			}
			
			if ( isAction("cam_turn_down"))	
			{ 
				camNode.turnDown(keyPressActionRate);
			}
			
			if ( isAction("cam_turn_up"))	
			{ 
				camNode.turnUp(keyPressActionRate);
			}
			
			if ( isAction("info"))
			{
				Log.warn( 
						"\nAxes: " + "" +
						"\nLoc Rotation: " + camNode.getLocalRotation() +
						"\nWorld Rot: "+ camNode.getWorldRotation() + 
						"\nDirection: " + camNode.getCamera().getDirection() +
						"\nLoc Trans: "+ camNode.getLocalTranslation() +
						"\nWorld Trans: "+ camNode.getWorldTranslation() +
						"\nZoom: " + camNode.getZoom());
			}
			
			if ( isAction("reset"))
			{
				Log.warn( "\nResetting");
				camNode.reset();			
			}
			
			if ( isAction("zoom_in")) {
				camNode.zoomIn(keyPressActionRate);
			}
			
			if ( isAction("zoom_out")) {
				camNode.zoomOut(keyPressActionRate);
			}
			
		}//end key input
	}
	
	/**
	 * Listener to take care of events
	 */
	MouseInputAction mouseAction = new MouseInputAction() 
	{
	        public void performAction( InputActionEvent evt ) 
	        {
	        	//by putting mouse handler here, the calls are not every frame and do not 'repeat'	        	
	        	handleMouseInput();
	        }	        
	 };	
	 
	 /**
	  * Key input listener to handle events, so that it need not be accounted for every frame
	  * this is allowed to be repeatable, non-repeatables are handled as 'buttons'
	  * @author caprea
	  */
	 KeyInputAction repeatkbAction = new KeyInputAction()
	 {
		 public void performAction(InputActionEvent evt)
		 {
			 //TODO: move keyhandler here
		 }
	 };
	 
	 /**
	  * for NONrepeatale keys or buttons
	  * @author caprea
	  */
	 KeyInputAction norepeatkbAction = new KeyInputAction()
	 {
		 public void performAction(InputActionEvent evt)
		 {
			 //TODO: move keyhandler here
		 }
	 };
	  
	 
	/**
	 * Convenience method for getting the underlying display system
	 * @see DisplaySystem
	 */
	public DisplaySystem getDisplaySystem(){
		return display;
	}

	
	/**
	 * Called every frame update
	 *
	 */
	protected void simpleUpdate() 
	{
		//the coordsDown and coordsUp code used to go here. It's gone now.
		//handle mouse input has been moved to listener so there is no 'repeatables'
		//this is more efficient as well, saves processing time
		handleKeyInput();	//should be mvoed to some other handler
	}
	
	/** 
	 * isAction(String command)
	 * 
	 * Because JME works with the keybinding manager using 'commands'
	 * this function helps to simplify/make pretty the code elsewhere in the program
	 * so that it is more human-readable
	*/
	private boolean isAction(String command)
	{
		if (KeyBindingManager.getKeyBindingManager().isValidCommand(command))
		{
			return true;
		}
		return false;
	}

	
    /**
     * This is called every frame in BaseGame.start(), after update()
     * 
     * @param interpolation
     *            unused in this implementation
     * @see AbstractGame#render(float interpolation)
     */
    protected final void render(float interpolation) {
        super.render(interpolation);
        
        Renderer r = display.getRenderer();
        
        r.clearBuffers();

        /** Draw the rootNode and all its children. */
        r.draw(rootNode);
        
        /** Call simpleRender() in any derived classes. */
        simpleRender();
        

        
        /** Draw the fps node to show the fancy information at the bottom. */
        r.draw(fpsNode);
        
        doDebug(r);
        
		// Then we display the GUI
		disp.display();
    }

	 /**
     * Called every frame to update scene information.
     * 
     * @param interpolation
     *            unused in this implementation
     * @see BaseSimpleGame#update(float interpolation)
     */
    protected final void update(float interpolation) {
        super.update(interpolation);

        if ( !pause ) {
            /** Call simpleUpdate in any derived classes of SimpleGame. */
            simpleUpdate();

            /** Update controllers/render states/transforms/bounds for rootNode. */
            rootNode.updateGeometricState(tpf, true);
        }
    }

    @Override
    protected void doDebug(Renderer r) {
        super.doDebug(r);

        if (showDepth) {
            r.renderQueue();
            Debugger.drawBuffer(Texture.RTT_SOURCE_DEPTH, Debugger.NORTHEAST, r);
        }
    }
	
	public View3D getView3D() {
		return view3D;
	}

	public View2D getView2D() {
		return View2D.getInstance();
	}

	public Renderer getRenderer() {
		return display.getRenderer();
	}
	
	/**
	 * Get the current instance of the ViewCamera for this view
	 * @return
	 */
	public ViewCamera getCamera() {
		return this.camNode;
	}

}


