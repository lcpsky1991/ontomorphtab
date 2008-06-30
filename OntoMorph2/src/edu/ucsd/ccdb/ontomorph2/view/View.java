package edu.ucsd.ccdb.ontomorph2.view;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
//import java.nio.FloatBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme.app.AbstractGame;
import com.jme.app.BaseSimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.image.Texture;
import com.jme.input.AbsoluteMouse;
import com.jme.input.FirstPersonHandler;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.input.MouseInput;
import com.jme.intersection.PickData;
import com.jme.intersection.PickResults;
import com.jme.intersection.TrianglePickResults;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Ray;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.CameraNode;
import com.jme.scene.Line;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.LightState;
import com.jme.system.DisplaySystem;
import com.jme.util.geom.BufferUtils;
import com.jme.util.geom.Debugger;

import edu.ucsd.ccdb.ontomorph2.app.OntoMorph2;
import edu.ucsd.ccdb.ontomorph2.core.scene.Scene;
import edu.ucsd.ccdb.ontomorph2.misc.FengJMEInputHandler;
import edu.ucsd.ccdb.ontomorph2.util.AllenAtlasMeshLoader;
import edu.ucsd.ccdb.ontomorph2.view.scene.NeuronMorphologyView;
import edu.ucsd.ccdb.ontomorph2.view.scene.SegmentView;
import edu.ucsd.ccdb.ontomorph2.core.changes.*;
import edu.ucsd.ccdb.ontomorph2.core.spatial.*;
import edu.ucsd.ccdb.ontomorph2.view.gui2d.View2D;

//===

import com.jme.input.action.MouseLook; 	//drag handler
import com.jme.renderer.Camera;			//drag handler
import com.jme.input.*;					//drag handler
import edu.ucsd.ccdb.ontomorph2.view.MouseClickAndDrag;

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
	private static final Logger logger = Logger.getLogger(View.class.getName());
//	The trimesh that i will change
	TriMesh square;
	
	// a scale of my current texture values
	float coordDelta;
	private Scene _scene = null;

	
	//=================================
	// Global Interface-Objects
	//=================================
	ViewCamera camNode;							//thisobject needed for manipulating the camera in a simple way
	AbsoluteMouse amouse; 						//the mouse object ref to entire screen, used to hide and show the mouse?
	PickData prevPick;							//made global because it's a conveiniant way to deselect the previous selection since it's stored
	
	NeuronMorphologyView manipMorph=null;	//the most recent object to be selected/manipulated as a morphology
	PickData firstClick;
	
	FirstPersonHandler fpHandler = null;
	MouseLook looker;	//not used
	private boolean pointerEnabled = false;
	

	//==================================
	// DECLARES
	// - used for manipulating the objects, setting the mode says what you're doing with dragging
	//==================================
	public static final int METHOD_NONE = 0;
	public static final int METHOD_MOVE = 1;
	public static final int METHOD_MOVE_C = 2;
	public static final int METHOD_SCALE = 4;
	public static final int METHOD_ROTATEX = 8;
	public static final int METHOD_ROTATEY = 16;
	public static final int METHOD_ROTATEZ = 32;
	public static final int METHOD_LOOKAT = 64;

	private static int manipulation = METHOD_NONE; //use accesor
	
	
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
		System.out.println("Manipulation method set to: " + m);
		
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
	
	
	
	//this ismostly for debugging
	//TODO: remove this function
	private void createSphere(Vector3f p1)
	{
		//Cylinder(java.lang.String name, int axisSamples, int radialSamples, float radius, float height, boolean closed)
		
		float x,y,z;
		x = p1.getX();
		y = p1.getY();
		z = p1.getZ();
		
		 Sphere s=new Sphere("My sphere",10,10,1f); //last number is radius
		 s.setModelBound(new BoundingBox());
		 s.updateModelBound();
		 s.setRandomColors();
		 s.setLocalTranslation(x,y,z);
		 
		 rootNode.attachChild(s);
	}
	
	//for debugging
	private void createLine(Vector3f apex, Vector3f base)
	{
		//TODO: remove
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
        
                
		//TODO: wouldn't it be nice to move the camera based on mouse position?
        
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
		
		KeyBindingManager.getKeyBindingManager().set("info", KeyInput.KEY_I);
		KeyBindingManager.getKeyBindingManager().add("mem_report", KeyInput.KEY_I);
		
		KeyBindingManager.getKeyBindingManager().set("zoom_in", KeyInput.KEY_Z);
		KeyBindingManager.getKeyBindingManager().set("zoom_out", KeyInput.KEY_X);
		
		// We want a cursor to interact with FengGUI
		MouseInput.get().setCursorVisible(true);
		
	}
	
	/**
	 * Scales the morophology in the dimensions of constraint
	 * @param morph the item(s) to be rotated
	 * @param constraint the dimensions that the morphology will be scaled in
	 */
	public void scaleMorph(NeuronMorphologyView morph, OMTVector constraint)
	{
		float dx = MouseInput.get().getXDelta(); 
		float dy = MouseInput.get().getYDelta();
		
		float delta = 0.01f * dx;
		
		OMTVector current = morph.getMorphology().getRelativeScale();
		
		OMTVector nscale = new OMTVector(current.add(delta,delta,delta));
		
		
		//do NOT scale if the new scale will 'flip' the object
		if ( !(nscale.getX() < 0 || nscale.getY() < 0 || nscale.getZ() < 0 ) )
		{
			morph.getMorphology().setRelativeScale(nscale);	
		}
	}
	
	
	
	/**
	 * Changes the rotation of a morphology based on changes from the mouse movement 
	 * @param morph the item(s) to be rotated
	 * @param constraint the axis (or axes) on which to rotate the object. For example, if constraint is (1,0,0) the object will rotates about it's own X axis (not the world's X axis)
	 * @author caprea
	 */
	public void rotateMorph(NeuronMorphologyView morph, OMTVector constraint)
	{
		
		float dx = MouseInput.get().getXDelta(); 
		float dy = MouseInput.get().getYDelta();
		
		float delta = dx;
		
		Quaternion more = new Quaternion();
		Quaternion end = new Quaternion();
		
		more.fromAngleAxis(0.1f * delta, constraint); //rotate with horitonzal mouse movement
		
		end = morph.getMorphology().getRelativeRotation().mult(more);
		
		morph.getMorphology().setRelativeRotation( new RotationVector(end) );
		
	}
	
	/**
	 * Changes the local translation of a morpholgy in the scene based on changes from the mouse movement
	 * The dimensions of freedom allow the 2D movement of the mouse to map to the 3D movement intended
	 * @param constraint Specifies what dimensions to allow movement based on mouse input. 
	 * Will typically range from (0,0,0) to (1,1,1). Where (1,1,0) corresponds to 2D movement on the current X,Y plane
	 */
	//TODO: impliment the constraint
	public void moveMorph(NeuronMorphologyView morph, OMTVector constraint)
	{
		//get changes in mouse movement
		float dx = MouseInput.get().getXDelta(); 
		float dy = MouseInput.get().getYDelta();
		float dz = 0;
		
		//TODO: calculate the viewing angle and apply to constraint
		
		dx = dx * constraint.getX();
		dy = dy * constraint.getY();
		dz = dz * constraint.getZ();
		
		//get the position, add the change, store the new position
		PositionVector np = new PositionVector( morph.getMorphology().getRelativePosition().asVector3f().add(dx,dy,dz) );
		
		//apply the movement
		morph.getMorphology().setRelativePosition( np );
	}
	
	private void handleMouseInput()
	{
		//handle mouse input
		//TODO: get more sophisticated way of dealing with mouse input (pickresults has handler)
		try
		{
			//right click to drag
			if (MouseInput.get().isButtonDown(1)) //right
			{	
				MouseInput.get().setCursorVisible(false); //hide mouse cursor
				
				if (prevPick != null && manipMorph != null)
				{
					//what action is being performed?
					
					//TODO: replace unity vectors with ones based on camera axis
					switch ( manipulation )
					{
						case METHOD_NONE:
							//do nothing
							break;
						case METHOD_MOVE:
							moveMorph(manipMorph, new OMTVector(1,1,0));
							break;
						case METHOD_ROTATEX:
							rotateMorph(manipMorph, new OMTVector(1,0,0));
							break;
						case METHOD_ROTATEY:
							rotateMorph(manipMorph, new OMTVector(0,1,0));
							break;
						case METHOD_ROTATEZ:
							rotateMorph(manipMorph, new OMTVector(0,0,1));
							break;
						case METHOD_LOOKAT:
							camNode.lookAt(manipMorph.getLocalTranslation(), new OMTVector(0,1,0)); //make the camera point a thte object in question
							break;
						case METHOD_SCALE:
							scaleMorph(manipMorph, new OMTVector(1,1,1));
							break;
					}
				}
			}
			else
			{
				MouseInput.get().setCursorVisible(true); //show mouse cursor
			}
			
			
			//left mouse click
			if (MouseInput.get().isButtonDown(0)) //left 
			{
				
				//because dendrites can be densely packed need precision of triangles instead of bounding boxes
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
				
				//set up for deselection
				if ( pr.getNumber() > 0)
				{
					//********* DESELECT PREVIOUS ********************* 
					//if ( prevPick != null) prevPick.getTargetMesh().setRandomColors();
					if ( prevPick != null )
					{
						/*
						 * this should be done in a listener after firing an event
						 * here
						 */
						
						for (NeuronMorphologyView c : getView3D().getCells())
						{
							SegmentView segView = ((NeuronMorphologyView) c).getSegmentFromGeomBatch(prevPick.getTargetMesh());
							
							if ( segView != null )
							{
								if ( segView.correspondsToSegment() )
								{
									c.getMorphology().unselectSegment(segView.getCorrespondingSegment());
								}
								else if ( segView.correspondsToSegmentGroup() )
								{
									c.getMorphology().unselectSegmentGroup(segView.getCorrespondingSegmentGroup());	
								}
							}
						}
					}
					//============== END DESLECT ============================
					
					//************** SELECT **********************
					//find the one that is closest
					//the 0th element is closest to the origin of the ray with checkdistance
					//This is the distance from the origin of the Ray to the nearest point on the BoundingVolume of the Geometry.
					prevPick = pr.getPickData(0);	//take the closest pick and set
					
					
					/* this should be done in a listener after firing an event here*/
					for (NeuronMorphologyView c : getView3D().getCells())
					{ // loop over all IStructure3Ds (the view representation of
						// the morphology)
						/*
						 * Try to get a segView (view representation of a segment or
						 * segment group) that matches the target mesh from the pick
						 * results within this INeuronMorphologyView
						 */
						
						SegmentView segView = ((NeuronMorphologyView) c).getSegmentFromGeomBatch(prevPick.getTargetMesh());
						if (segView != null)
						{ // if we found one
							/*
							 * tell the INeuronMorphology (the model representation
							 * of the morphology) to note that we have selected a
							 * segment or a segment group. The SceneObserver will
							 * then get updated and change the color on the
							 * appropriate geometry in the INeuronMorphologyView
							 */
							manipMorph = (NeuronMorphologyView) c; //for manipulating picked items
							
							if (segView.correspondsToSegment())
							{
								c.getMorphology().selectSegment(segView.getCorrespondingSegment());
							}
							else if (segView.correspondsToSegmentGroup())
							{
								c.getMorphology().selectSegmentGroup(segView.getCorrespondingSegmentGroup());
							}
						}
					}
					//===== END DESELCT =========================
					
					//System.out.println("Picked: " + prevPick.getTargetMesh().getName());
				} //end if of pr > 0
			} //end if mouse button down
		} //end try
		catch (Exception e)
		{
			logger.log(Level.SEVERE, "Exception caught in View.handleMouseInput(): " + e.getMessage());
		}
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

	             logger.info("|*|*|  Memory Stats  |*|*|");
	             logger.info("Total memory: " + (totMem >> 10) + " kb");
	             logger.info("Free memory: " + (freeMem >> 10) + " kb");
	             logger.info("Max memory: " + (maxMem >> 10) + " kb");
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
				
				
			if ( isAction("cam_forward") || isAction("cam_forward_ns") ) 
			{
				camNode.moveForward();
			}
			
			if ( isAction("cam_back") || isAction("cam_back_ns"))
			{
				camNode.moveBackward();
			}

			if ( isAction("cam_turn_cw"))	
			{
				camNode.turnClockwise();
			}
			
			
			if ( isAction("cam_turn_ccw"))	
			{ 
				camNode.turnCounterClockwise();
			}
			
			if ( isAction("cam_turn_down"))	
			{ 
				camNode.turnDown();
			}
			
			if ( isAction("cam_turn_up"))	
			{ 
				camNode.turnUp();
			}
			
			if ( isAction("info"))
			{
				logger.log(Level.INFO, 
						"\nAxes: " + "" +
						"\nLoc Rotation: " + camNode.getLocalRotation() +
						"\nWorld Rot: "+ camNode.getWorldRotation() + 
						"\nDirection: " + camNode.getCamera().getDirection() +
						"\nLoc Trans: "+ camNode.getLocalTranslation() +
						"\nWorld Trans: "+ camNode.getWorldTranslation());
			}
			
			if ( isAction("reset"))
			{
				logger.log(Level.INFO, "\nResetting");
				camNode.reset();			
			}
			
			if ( isAction("zoom_in")) {
				camNode.zoomIn();
			}
			
			if ( isAction("zoom_out")) {
				camNode.zoomOut();
			}
			
		}//end key input
	}
	
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
		handleMouseInput();
		handleKeyInput();
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
	 * Get the current instance of the View Camera for this view
	 * @return
	 */
	public ViewCamera getCamera() {
		return this.camNode;
	}
}


