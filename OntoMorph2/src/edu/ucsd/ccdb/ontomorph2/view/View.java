

package edu.ucsd.ccdb.ontomorph2.view;


import com.jme.app.AbstractGame;
import com.jme.app.BaseSimpleGame;
import com.jme.bounding.BoundingSphere;
import com.jme.image.Texture;
import com.jme.input.FirstPersonHandler;
import com.jme.input.InputHandler;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.input.MouseInput;
import com.jme.input.action.InputActionEvent;
import com.jme.input.action.KeyInputAction;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Line;
import com.jme.scene.Node;
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
import edu.ucsd.ccdb.ontomorph2.core.scene.tangible.Tangible;
import edu.ucsd.ccdb.ontomorph2.util.FocusManager;
import edu.ucsd.ccdb.ontomorph2.util.Log;

//=========

/**
 * Defines the <a href="http://openccdb.org/wiki/index.php/Brain_Catalog_Architecture">view 
 * of the system in a model-view-controller architecture</a>. 
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 * @author caprea
 */
public class View extends BaseSimpleGame {


	
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
	
	
	float keyPressActionRate = 1.0f; //the rate of rotation by a single key press
	org.fenggui.Display disp; // FengGUI's display
	
	View3D view3D = null;
	private View3DMouseListener view3DMouseListener;
	
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
	
	protected View() 
	{
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
	
	public boolean getDebugMode() {
		return this.debugMode;
	}
	
	public View3DMouseListener getView3DMouseListener() {
		return this.view3DMouseListener;
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
		this.camNode = new ViewCamera();
		this.cam = camNode.getCamera();
		
		rootNode.attachChild(camNode);
		
		//as a hack, calling the main application class to do initialization
		//this is because model loading needs to have the view running in order to work
		  
		OntoMorph2.initialization();
		display.getRenderer().setBackgroundColor(ColorRGBA.black); //Set a black background.
		display.setTitle("Whole Brain Catalog");
				
		rootNode.attachChild(view3D);

		
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
		/*
    	this.view3DMouseHandler = new View3DMouseHandler();
        input.addAction(this.view3DMouseHandler , InputHandler.DEVICE_MOUSE, InputHandler.BUTTON_ALL, InputHandler.AXIS_ALL, false );
        */
		this.view3DMouseListener = new View3DMouseListener();
		MouseInput.get().addListener(this.view3DMouseListener);
	}
			
	
	/**
	 * Handles the execution of code based on activated keys
	 *
	 */
	private void handleKeyInput() 
	{
		
		//if widget NOT focused then...
		if (!FocusManager.get().isWidgetFocused())
		{//key input handle
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
			 System.out.println("keyinputaction");
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
			 System.out.println("key input repetition non repeated");
			 //TODO: move keyhandler here
		 } 
	 };
	  
//	this ismostly for debugging
     public Sphere createSphere(Vector3f p1)
     {
             //Cylinder(java.lang.String name, int axisSamples, int radialSamples, float radius, float height, boolean closed)
             
              Sphere s=new Sphere("My sphere",10,10,2f); //last number is radius
              s.setModelBound(new BoundingSphere());
              s.updateModelBound();
              s.setRandomColors();
              s.setLocalTranslation(p1);
              
              rootNode.attachChild(s);
              return s;
     }

     /**
      * For showing the pickray in debugging mode
      */
     
     public void createDebugRay(Vector3f begin, Vector3f end)
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
    	rootNode.attachChild(wand);
    	
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
    	//System.out.println("render");
        super.render(interpolation);
       
        this.getCamera().getCamera().update();
        
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
        	//System.out.println("update interpolation");
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

	public float getKeyPressActionRate() {
		return this.keyPressActionRate;
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
	
	public Node getMainViewRootNode() {
		return rootNode;
	}
}


