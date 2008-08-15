

package edu.ucsd.ccdb.ontomorph2.view;


import java.math.BigInteger;
import java.util.HashMap;
import java.util.Set;

import sun.security.acl.WorldGroupImpl;

import com.jme.app.AbstractGame;
import com.jme.app.BaseSimpleGame;
import com.jme.bounding.BoundingSphere;
import com.jme.curve.BezierCurve;
import com.jme.image.Texture;
import com.jme.input.FirstPersonHandler;
import com.jme.input.InputHandler;
import com.jme.input.InputHandlerDevice;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.input.MouseInput;
import com.jme.input.MouseInputListener;
import com.jme.input.action.InputActionEvent;
import com.jme.input.action.KeyInputAction;
import com.jme.input.action.MouseInputAction;
import com.jme.intersection.PickData;
import com.jme.intersection.PickResults;
import com.jme.intersection.TrianglePickResults;
import com.jme.math.Ray;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Line;
import com.jme.scene.TriMesh;
import com.jme.scene.batch.GeomBatch;
import com.jme.scene.shape.Cone;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.system.PropertiesIO;
import com.jme.util.geom.Debugger;

import edu.ucsd.ccdb.ontomorph2.app.OntoMorph2;
import edu.ucsd.ccdb.ontomorph2.core.scene.Scene;
import edu.ucsd.ccdb.ontomorph2.core.scene.TangibleManager;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Curve3D;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.ICable;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.INeuronMorphologyPart;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.NeuronMorphology;
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Tangible;
import edu.ucsd.ccdb.ontomorph2.core.spatial.DemoCoordinateSystem;
import edu.ucsd.ccdb.ontomorph2.core.spatial.OMTVector;
import edu.ucsd.ccdb.ontomorph2.util.CatmullRomCurve;
import edu.ucsd.ccdb.ontomorph2.util.Log;
import edu.ucsd.ccdb.ontomorph2.view.gui2d.ContextMenu;
import edu.ucsd.ccdb.ontomorph2.view.scene.CurveAnchorPointView;
import edu.ucsd.ccdb.ontomorph2.view.scene.CurveView;
import edu.ucsd.ccdb.ontomorph2.view.scene.NeuronMorphologyView;
import edu.ucsd.ccdb.ontomorph2.view.scene.SlideView;
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
	private Scene _scene = null;
	
//	The trimesh that i will change
	//TriMesh square;
	
	// a scale of my current texture values
	//float coordDelta;

	//=================================
	// Global Interface-Objects
	//=================================
	public ViewCamera camNode;					//thisobject needed for manipulating the camera in a simple way
	
	//currentPick not stored globally anymore
	//manip not stored locally anymore
		
	FirstPersonHandler fpHandler = null;
	//MouseLook looker;	//not used
	private boolean pointerEnabled = false;
	private boolean debugMode = false;
	Line debugRay = null;	//used for mouse picking debugging mode
	Cone wand = null;
	
	//For dealing with Mouse Events, track previous time and dragging
	boolean dragMode = false;
	long prevPressTime = 0;
	long dblClickDelay = 600;//in milliseconds (1000 = 1 sec)
	
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
		KeyBindingManager.getKeyBindingManager().set("object_sphere", KeyInput.KEY_1);
		KeyBindingManager.getKeyBindingManager().set("update", KeyInput.KEY_U);
		KeyBindingManager.getKeyBindingManager().set("toggleMouse", KeyInput.KEY_M);
		KeyBindingManager.getKeyBindingManager().set("toggleDebug", KeyInput.KEY_D);
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
	
	/**
	 * Listener to take care of events
	 */
	MouseInputAction mouseAction = new MouseInputAction() 
	{
		
	    	public void performAction( InputActionEvent evt ) 
	        {
	        	//by putting mouse handler here, the calls are not every frame and do not 'repeat'
	        	if (evt.getTriggerPressed()) //
	        	{
	        		//+++++ BUTTON PRESSED  ++++++
	        		//=========== MOUSE DOWN ========================
	        		//double-click versus single-click belongs in child method
	        		dragMode = true;	//begin assuming drag (deactive drag in upMouse event)
	        		if (debugMode) Log.warn("mouse press");
	        		
	        		//pafind the index of which button pressed
	        		for (int b = 0; b < MouseInput.get().getButtonCount(); b++)
	        		{
	        			if (MouseInput.get().isButtonDown(b))
	        			{
	        				onMousePress(b);
	        				b = MouseInput.get().getButtonCount()+1; //all done
	        			}
	        		}
	        	}
	        	else
	        	{
	        		//+++++ BUTTON RELEASED (not pressed) ++++++
	        		/*
	        		 (enjoy a drink now and then), 
	        		 will frequently check credit at 
	        		 (moral) bank (hole in the wall), 
	        		 */
	        		boolean pushed = false;
	        		int b= 0;
	        		for (b=0; !pushed && b < MouseInput.get().getButtonCount(); b++)
	        		{
	        			if ( MouseInput.get().isButtonDown(b))
	        			{
	        				pushed = true;
	        			}
	        		}
	        		//============ DRAG =========================
	        		if (pushed && dragMode)
	        		{
	        			if (debugMode) Log.warn("mouse drag");
	        			onMouseDrag();
	        		}
	        		//============ MOUSE UP/RELEASE =============
	        		else if (!pushed && dragMode)	
	        		{
	        			dragMode = false;
	        			if (debugMode) Log.warn("mouse release");
	        			onMouseRelease();
	        		}
	        		//============ MOVE - MOUSE EVENT DEFAULT =======
	        		else	
	        		{
	        			//System.out.println("mouse move/wheel");
	        			onMouseMove();
	        			onMouseWheel();
	        		}
	        	}
	        }	        
	 };	
	 
	/**
	 * Child method from handleMouseInput
	 */ 
	private void onMouseDrag()
	{
		
		boolean mouseLook = false;
		int numMouseBut = MouseInput.get().getButtonCount();
		
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
		else if (MouseInput.get().isButtonDown(0)) //left 
		{
			//dragging
			manipulateCurrentSelection();
		}
	}
	
	/**
	 * Child method from handleMouseInput
	 * still need to check which buttons are pressed
	 */ 
	private void onMousePress(int buttonIndex)
	{
		//	RIGHT CLICK
		if (1 == buttonIndex) //right
		{	
			//MouseInput.get().setCursorVisible(false); //hide mouse cursor
			doPick();
			ContextMenu.getInstance().displayMenuFor(MouseInput.get().getXAbsolute(),
					MouseInput.get().getYAbsolute(),TangibleManager.getInstance().getSelected());
		}
		else if (0 == buttonIndex) //left
		{
			//MouseInput.get().setCursorVisible(true); //show mouse cursor
			doPick();
		}
		
		
		long timenow = System.currentTimeMillis();
		
		//+Double+
		//check double click
		if (timenow < prevPressTime + dblClickDelay) 
		{
			onMouseDouble(buttonIndex);
		}
		prevPressTime = timenow;
	}
	
	private void onMouseRelease()
	{

	}
	
	private void onMouseDouble(int buttonIndex)
	{
		if (0 == buttonIndex) //left 
		{
			
		}
		 
		Log.warn("Double click (" + buttonIndex + ") @ " + System.currentTimeMillis());
	}
	
	/**
	 * Exists only for possible future expansion
	 * 
	 */
	private void onMouseMove()
	{		
		/*
		 * ca: I can't think of anything that would be appripriate here, except a poem
		 * 
			Presently my soul grew stronger; hesitating then no longer,
 			`Sir,' said I, `or Madam, truly your forgiveness I implore;
 			But the fact is I was napping, and so gently you came rapping,
 			And so faintly you came tapping, tapping at my chamber door,
 			That I scarce was sure I heard you' - here I opened wide the door; -
 			Darkness there, and nothing more.
		 */
	}
	
	/**
	 * Child method from handleMouseInput
	 */ 
	private void onMouseWheel()
	{
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
	}

	
	/**
	 * Apply manipulations to the tangible that is currently selected
	 * Called during mouse handling
	 */
	private void manipulateCurrentSelection() 
	{		
	
		//CA: new movement code experiment
		//======================================
//		======================================
		/*
		float mx = 0;
		float my = 0;
		Vector2f mPos = new Vector2f();
		int x = MouseInput.get().getXDelta();
		int y = MouseInput.get().getYAbsolute();
		double angle = 0;
		mPos.set(x ,y );
		
		Vector3f dir = new Vector3f();
		
		dir = cam.getDirection();
		Vector3f dunit = new Vector3f(1,1,1);
		angle = dir.angleBetween(dunit);
		DemoCoordinateSystem d = new DemoCoordinateSystem();
		dunit = d.getOriginVector();
		mx = (float) (x * Math.cos( angle ));
		my = 0;  
		System.out.println("X:" + mx + " angle: " + angle);
		*/
//		======================================
		//======================================
		
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
			//FIXME: /* needs to be re-engineered to deal with multiple selections */
			Log.warn("LOOK AT is broken");
			try
			{
				camNode.lookAt(TangibleManager.getInstance().getSelectedRecent().getAbsolutePosition() , new OMTVector(0,1,0)); //make the camera point a thte object in question	
			}
			catch(Exception e){};
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
		
	
	private void doPick() 
	{
		Tangible picked = null;
		picked = psuedoPick(KeyInput.get().isControlDown()); //get the tangible picked
		
		//enable multiselect if shift is down
		if ( KeyInput.get().isShiftDown() ) TangibleManager.getInstance().setMultiSelect(true);
		
		//decide how to select it, is this multi select, deselect, etc?
		if (picked == null) //nothing was picked so do deselect
		{
			//if there are no results, unselect everything
			TangibleManager.getInstance().unselectAll();
		}
		else 
		{			
			picked.select();
		}
		
		//turn off multiselection
		TangibleManager.getInstance().setMultiSelect(false);
	}
	
	/**
	 * Facilitates mouse picking, but does NOT actually select the object, that must be done elsewhere.
	 * Example: selecting the Tangible that is returned from this function
	 * @return the closest {@link Tangible}, that is of the highest priority of all camera-ray-intersected Tangibles 
	 */
	public Tangible psuedoPick(boolean modifyControl)
	{
		Tangible chosenOne= null;
		PickResults rawresults = getPickResults();
		PickData decision = null;
		if ( rawresults.getNumber() > 0 ) rawresults = reorderPickPriority(rawresults);
		
//		Find out the tangible for the geometry that was decided on
		if ( rawresults.getNumber() > 0) 
		{
			TangibleView tv = null;
			decision = rawresults.getPickData(0);	//find the geomtry
			tv = TangibleViewManager.getInstance().getTangibleView(decision.getTargetMesh().getParentGeom()); //get a tanview instance that is mapped to the selected geomtry
			
			//special case for NeuronMorphologies because they have subcomponents
			if (tv instanceof NeuronMorphologyView && !modifyControl) //if control down proceed to the default case selection, otherwise return the part
			{
				NeuronMorphologyView nmv = (NeuronMorphologyView) tv;
				{
					//otherwise just select the part itself
					BigInteger id = nmv.getCableIdFromGeometry(decision.getTargetMesh().getParentGeom());
					ICable c = ((NeuronMorphology)nmv.getModel()).getCable(id);
					chosenOne = (Tangible) c;
				}
			}
			else if ( tv != null)
			{//CATCH ALL case for all other TangibleViews
				chosenOne = tv.getModel();
			}
		}
		
		
		return chosenOne;
	}
	
	
	private PickResults reorderPickPriority(PickResults results)
	{
		PickData decision = null;
		PickResults reorder = new TrianglePickResults();
		
		//setup
		int cnt = results.getNumber();
		int highP = TangibleView.P_UNKNOWN;
		reorder.clear();
		reorder.setCheckDistance(true);
		
		//===== loop through all the item in results
		//===== find the highest priority item
		for (int i=0; i < cnt; i++)
		{
			GeomBatch obj = results.getPickData(i).getTargetMesh();
			TangibleView tv = TangibleViewManager.getInstance().getTangibleView(obj.getParentGeom());
			if ( tv != null)
			{
				int p = tv.pickPriority;
				if ( p > highP ) highP = p;	
			}
		}
		
		//===== get all instances of THAT item
			//copy all of those that belong to highP to reorder
		for (int i =0; i < cnt; i++)
		{
			GeomBatch obj = results.getPickData(i).getTargetMesh();
			TangibleView tv = TangibleViewManager.getInstance().getTangibleView(obj.getParentGeom());
			if ( tv != null)
			{
				int p = tv.pickPriority;
				if ( highP == p )
				{
					reorder.addPickData(results.getPickData(i));
				}	
			}
		}
		
		return reorder;
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
		
		Vector3f closePoint = new Vector3f();
		Vector3f farPoint = new Vector3f();
		Vector3f dir = new Vector3f();
		
		// Get the world location of that X,Y value
		farPoint = display.getWorldCoordinates(mPos, 1.0f);
		closePoint = display.getWorldCoordinates(mPos, 0.0f);
		dir = farPoint.subtract(closePoint).normalize();
		
		// Create a ray starting from the camera, and going in the direction
		// of the mouse's location
		//Ray mouseRay = new Ray(closePoint, farPoint.subtractLocal(closePoint).normalizeLocal());
		Ray mouseRay = new Ray(closePoint, dir);
		
		createDebugRay(closePoint, farPoint); //draws a picking ray and possibly a picking cone
		
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
		//get a tangible view instance that is mapped to the selected geo
		TangibleView tv = TangibleViewManager.getInstance().getTangibleView(geo.getParentGeom());
	
		if ( KeyInput.get().isShiftDown() )
		{
			//turn on multiselection if shift is pressed
			TangibleManager.getInstance().setMultiSelect(true);
		}
		
		//special case NeuronMorphologyView because it has subcomponents that
		//are not themselves tangibles.
		//should probably bring this piece of code inside NeuronMorphologyView via 
		//some kind of action handler because this is kind of a hack
		if ( tv instanceof NeuronMorphologyView) {
			
			NeuronMorphologyView nmv = (NeuronMorphologyView)tv;

			if (KeyInput.get().isControlDown()) {
				//if user selected a segment, assume they wanted to select the parent cell 
				//instead if control is down
				nmv.getModel().select();
			} else {
				//otherwise just select the part itself
				BigInteger id = nmv.getCableIdFromGeometry(geo.getParentGeom());
				ICable c = ((NeuronMorphology)nmv.getModel()).getCable(id);
				((Tangible)c).select();
	
			}
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
			
			if ( isAction("toggleDebug")) 
			{
				debugMode = !debugMode;
				{
					System.out.println("Debug Mode set to: " + debugMode);
				}
			}
			
			if ( isAction("show_selected"))
			{
				for (Tangible t : TangibleManager.getInstance().getSelected()) {
					System.out.println(t);
				}
				System.out.println("" + TangibleManager.getInstance().countSelected() + " total");
			}
			
			if ( isAction("update"))
			{
				for (Tangible t : TangibleManager.getInstance().getCells())
				{
					t.unselect();
				}
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
				Vector3f unit = new Vector3f(0f,0f,1f);
				
				Log.warn( 
						"\nAngle Between Cam and Origin: " + unit.angleBetween(cam.getDirection()) +
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
		
			if(isAction("object_sphere")){
				camNode.SphereLeftRotation(keyPressActionRate);
			}
			
		}//end key input
	}
	
	 
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
	  
//	this ismostly for debugging
     private void createSphere(Vector3f p1)
     {
             //Cylinder(java.lang.String name, int axisSamples, int radialSamples, float radius, float height, boolean closed)
             
              Sphere s=new Sphere("My sphere",10,10,1f); //last number is radius
              s.setModelBound(new BoundingSphere());
              s.updateModelBound();
              s.setRandomColors();
              s.setLocalTranslation(p1);
              
              rootNode.attachChild(s);
     }

     /**
      * For showing the pickray in debugging mode
      */
     
     private void createDebugRay(Vector3f begin, Vector3f end)
     {
    	if (!debugMode) return;	//do not waste processing time if debug mode is off
    	
    	rootNode.detachChild(debugRay);
    	rootNode.detachChild(wand);
    	Vector3f verts[] = { begin, end};
    	debugRay = new Line("d ray",verts,null,null,null);
    	debugRay.setRandomColors();

    	int len = (int)begin.subtract(end).length();
    	
    	//Cone(java.lang.String name, int axisSamples, int radialSamples, float radius, float height, boolean closed)     	
    	wand = new Cone("wand", 50,len/2, 15, len, true );
    	wand.setLocalTranslation(begin);
    	wand.lookAt(end, begin);	//points the cone in the direction such that base is on object of interest and the apex is at camera
    	wand.setRandomColors();
    	
	    
    	
//    	===== make wand transparent ====
    	// Transparency does not seem to work
		//disable writing to zbuffer
		ZBufferState zb = View.getInstance().getRenderer().createZBufferState();
		zb.setWritable(false);
		zb.setEnabled(true);
		wand.setRenderState(zb);
		
		//enable alpha blending
		AlphaState as = View.getInstance().getRenderer().createAlphaState();
	      as.setBlendEnabled(true);
	      as.setSrcFunction(AlphaState.SB_SRC_ALPHA);      
	      as.setDstFunction(AlphaState.DB_ONE_MINUS_SRC_COLOR);
	      as.setTestEnabled(true);
	      as.setTestFunction(AlphaState.TF_GREATER);
	      as.setEnabled(true);
	    wand.setRenderState(as);
	    wand.setRenderQueueMode(Renderer.QUEUE_TRANSPARENT);
	    wand.updateRenderState();
	    
    	rootNode.attachChild(debugRay);
    	//rootNode.attachChild(wand);
    	
     }
     
     
	/**
	 * Convenience method for getting the underlying display system
	 * @see DisplaySystem
	 */
	public DisplaySystem getDisplaySystem(){
		if (display == null) {
			return DisplaySystem.getDisplaySystem( new PropertiesIO("properties.cfg").getRenderer() );
		}
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
		return getDisplaySystem().getRenderer();
	}
	
	/**
	 * Get the current instance of the ViewCamera for this view
	 * @return
	 */
	public ViewCamera getCamera() {
		return this.camNode;
	}
}


