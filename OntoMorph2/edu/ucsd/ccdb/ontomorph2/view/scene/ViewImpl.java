package edu.ucsd.ccdb.ontomorph2.view.scene;

import java.awt.Color;
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
import com.jme.intersection.BoundingPickResults;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Ray;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.CameraNode;
import com.jme.scene.Line;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.lod.DiscreteLodNode;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.LightState;
import com.jme.system.DisplaySystem;
import com.jme.util.geom.BufferUtils;
import com.jme.util.geom.Debugger;
import java.nio.FloatBuffer;

import edu.ucsd.ccdb.ontomorph2.app.OntoMorph2;
import edu.ucsd.ccdb.ontomorph2.core.scene.MeshImpl;
import edu.ucsd.ccdb.ontomorph2.core.scene.SceneImpl;
import edu.ucsd.ccdb.ontomorph2.core.spatial.CurveImpl;
import edu.ucsd.ccdb.ontomorph2.util.FengJMEInputHandler;



/**
 * Represents a singleton.
 */

public class ViewImpl extends BaseSimpleGame implements IView{

	private static ViewImpl instance = null;
	private static final Logger logger = Logger.getLogger(ViewImpl.class.getName());
	//The trimesh that i will change
	TriMesh square;
	// a scale of my current texture values
	float coordDelta;
	private SceneImpl _scene = null;

	CameraNode camNode;			//thisobject needed for manipulating the camera in a simple way
	AbsoluteMouse amouse; 	//the mouse object ref to entire screen
	PickData prevPick;		//made global because it's a conveiniant way to deselect the previous selection since it's stored
	
	
	//TODO: these variables track the direction of the camera - why can't the cameranode track this info itself?
	float view_angleX=0;
	float view_angleY=0;
	float view_angleZ=0;
	
	org.fenggui.Display disp; // FengGUI's display

	//there are two kinds of input, the FPS input and also FENG
	FengJMEInputHandler menuinput;	
	
	View3DImpl view3D = null;
	
	/**
	 * Returns the singleton instance.
	 @return	the singleton instance
	 */
	public static ViewImpl getInstance() {
		if (instance == null) {
			instance = new ViewImpl();
		}
		return instance;
	}
	
	protected ViewImpl() 
	{
		//this.setDialogBehaviour(SimpleGame.ALWAYS_SHOW_PROPS_DIALOG);	
		this.setDialogBehaviour(FIRSTRUN_OR_NOCONFIGFILE_SHOW_PROPS_DIALOG);
		view3D = new View3DImpl();
	}
	
	public void setScene(SceneImpl scene){
		_scene = scene;
	}
	

	//This function provides a conveiniance to update view_angleX/Y/Z 
	//it also keeps it within bounds of 360
	private float changeAngle(float angle, float angleFactor)
	{
	   /* recalculate rotation for the cylinder */
        if (tpf < 1) //time_per_frame is provided
        {
            angle += (1 * (tpf * angleFactor));
            if (angle > 360)
            {
            	angle = angle - 360;
            }
            else if ( angle < 0 )
            {
            	angle = 360 - angle;
            }
        }

		return angle;
	}
	
	protected void simpleInitGame() {
		//as a hack, calling the main application class to do initialization
		//this is because model loading needs to have the view running in order to work
		OntoMorph2.initialization();
		
		rootNode.attachChild(view3D);
		
		
		
		//This sphere is for debugging purposes, need to see something to indicate cam space/rotation
		Sphere s=new Sphere("DEBUG SPHERE",10,10,10f);
		// Do bounds for the sphere, use a BoundingBox
		s.setModelBound(new BoundingBox());
		s.updateModelBound();
		s.setRandomColors();
		s.setLocalTranslation(0,0,0);
		
		rootNode.attachChild(s);
		
		
		/*
		Vector3f p1 = new Vector3f(-20,0,20);
		Vector3f p2 = new Vector3f(-34,-5,20);
		Vector3f p3 = new Vector3f(-20,-10,20);
		Vector3f[] array = {p1, p2, p3};
		CurveImpl curve1 = new CurveImpl("Dentate Gyrus", array);
		curve1.setColor(Color.BLUE);
		rootNode.attachChild(curve1);
		
		float time = 0.8f;
		Vector3f px = curve1.getPoint(time-0.01f);
		Vector3f py = curve1.getPoint(time+0.01f);
		createLine(px,py);
		
		Vector3f pe = curve1.getPoint(time);
		Vector3f pf = new Vector3f((py.x - px.x)/2+px.x, (py.y - px.y)/2+px.y, (py.z - px.z)/2+px.z);
		createLine(pe,pf);*/
		
		///** Set a black background.*/
		display.getRenderer().setBackgroundColor(ColorRGBA.black);
		
		
		
		//====================================
		// CAMERA SETUP
		//====================================
		
		///** Set up how our camera sees. */
		//cam.setFrustum( 0, 150, -invZoom * aspect, invZoom * aspect, -invZoom, invZoom );
		cam.setFrustumPerspective(45.0f, (float) display.getWidth() / (float) display.getHeight(), 1, 1000);
		
		//a locaiton on the Z axis a ways away
		Vector3f loc = new Vector3f(0, -3f, -400.0f);
		//Vector3f left = new Vector3f(-1.0f, 0.0f, 0.0f);
		//Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
		//Vector3f dir = new Vector3f(0.0f, 0f, -1.0f);

		///** Move our camera to a correct place and orientation. */
		//pX, pY, pZ, orientation
		//cam.setFrame(loc, left, up, dir); //commented out because now using a camNode
		
		///** Signal that we've changed our camera's location/frustum. */
		cam.update();

		///** Assign the camera to this renderer.*/
		display.getRenderer().setCamera(cam);
		
		//camnode is for easy manipulation of the camera
		camNode = new CameraNode("camera node", cam);
		camNode.setLocalTranslation(loc);
		
		
		rootNode.attachChild(camNode);
		
		//camNode.setLocalTranslation(loc);
		System.out.println("Rotation: " + camNode.getLocalRotation() + "\nTranslation: " + camNode.getLocalTranslation());
	
		
		//===================================================
		
		//the code for keybindings was previously here, it's been seperated for code readability
		//Section for setting up the mouse and other input controls	
		configureControls();
		
		//Remove lighting for rootNode so that it will use our basic colors
		rootNode.setLightCombineMode(LightState.OFF);
		
		disp = View2DImpl.getInstance();
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
		
		 Sphere s=new Sphere("My sphere",10,10,3f); //last number is radius
		 s.setModelBound(new BoundingBox());
		 s.updateModelBound();
		 s.setRandomColors();
		 s.setLocalTranslation(x,y,z);
		 
		 rootNode.attachChild(s);
	}
	
	//for debugging
	private void createLine(Vector3f apex, Vector3f base)
	{

    	ColorRGBA defaultColor = ColorRGBA.red;
    	
    	float[] colorValues2 = {defaultColor.r, defaultColor.g, defaultColor.b, defaultColor.a, 
          		defaultColor.r, defaultColor.g, defaultColor.b, defaultColor.a};
    	FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(colorValues2);
    	
    	
    	float[] vertices = {apex.x, apex.y, apex.z, base.x, base.y, base.z};
    		
          
    		Line l = new Line("my Line", BufferUtils.createFloatBuffer(vertices), null, colorBuffer, null);
    		l.updateModelBound();
    		rootNode.attachChild(l);    	
	}
    	
	private void configureControls()
	{
		
		//This is where we disable the FPShooter controls that are created by default by JME	
		FirstPersonHandler fpHandler = new FirstPersonHandler(cam, 50, 5); //(cam, moveSpeed, turnSpeed)
        input = fpHandler;
        
        //Disable both of these because I want to track things with the camera
        fpHandler.getKeyboardLookHandler().setEnabled( false );
        fpHandler.getMouseLookHandler().setEnabled( false);
		
        input.clearActions();	//removes all input actions not specifically programmed
        
                
		
		//Bind the Escape key to kill our test app
		KeyBindingManager.getKeyBindingManager().set("quit", KeyInput.KEY_ESCAPE);
		
		//assign 'R' to reload the view to inital state //reinit
		KeyBindingManager.getKeyBindingManager().set("reset", KeyInput.KEY_R);
		
		//assignt he camera to up, down, left, right
		KeyBindingManager.getKeyBindingManager().set("cam_forward", KeyInput.KEY_ADD);
		//KeyBindingManager.getKeyBindingManager().set("cam_forward", KeyInput.KEY_EQUALS); //for shift not pressed;
		KeyBindingManager.getKeyBindingManager().set("cam_back", KeyInput.KEY_SUBTRACT);
		//KeyBindingManager.getKeyBindingManager().set("cam_back", KeyInput.KEY_MINUS); //for no-shift control
		KeyBindingManager.getKeyBindingManager().set("cam_turn_ccw", KeyInput.KEY_LEFT);
		KeyBindingManager.getKeyBindingManager().set("cam_turn_cw", KeyInput.KEY_RIGHT);
		KeyBindingManager.getKeyBindingManager().set("cam_turn_up", KeyInput.KEY_UP);
		KeyBindingManager.getKeyBindingManager().set("cam_turn_down", KeyInput.KEY_DOWN);

		
		KeyBindingManager.getKeyBindingManager().set("info", KeyInput.KEY_I);
		
		// We want a cursor to interact with FengGUI
		MouseInput.get().setCursorVisible(true);
	}
	

	private void handleInput() 
	{
		//key input handle
		{
			//exit the program cleanly on ESC
			if (isAction("quit")) finish();
			
			if ( isAction("cam_forward")) 
			{
				//find the vector of the direction pointing towards
				Vector3f dir = camNode.getCamera().getDirection().normalize();
				camNode.setLocalTranslation( camNode.getLocalTranslation().add(dir.normalize()));
			}
			
			if ( isAction("cam_back"))
			{
				//find the vector of the direction pointing towards
				Vector3f dir = camNode.getCamera().getDirection().normalize().negate();
				camNode.setLocalTranslation( camNode.getLocalTranslation().add(dir.normalize()));
			}

			
			if ( isAction("cam_turn_cw"))	
			{
				//key right
				 Quaternion roll = new Quaternion(); 
				 view_angleY = changeAngle(view_angleY, -500);					 
				 roll.fromAngleAxis( FastMath.PI * view_angleY / 180 , Vector3f.UNIT_Y ); //rotates a degrees 
				 camNode.setLocalRotation(roll);
			}
			
			
			if ( isAction("cam_turn_ccw"))	
			{ //left key
					 Quaternion roll = new Quaternion(); 
					 view_angleY = changeAngle(view_angleY, 500);					 
					 roll.fromAngleAxis( FastMath.PI * view_angleY / 180 , Vector3f.UNIT_Y ); //rotates a degrees 
					 camNode.setLocalRotation(roll);
			}
			
			if ( isAction("cam_turn_down"))	
			{ //down
				Quaternion roll = new Quaternion(); 
				 view_angleX = changeAngle(view_angleX, -500);					 
				 roll.fromAngleAxis( FastMath.PI * view_angleX / 180 , Vector3f.UNIT_X ); //rotates a degrees 
				 camNode.setLocalRotation(roll);
			}
			
			if ( isAction("cam_turn_up"))	
			{ //up
				Quaternion roll = new Quaternion(); 
				 view_angleX = changeAngle(view_angleX, 500);					 
				 roll.fromAngleAxis( FastMath.PI * view_angleX / 180 , Vector3f.UNIT_X ); //rotates a degrees 
				 camNode.setLocalRotation(roll);
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
				Quaternion q = new Quaternion();
				q.fromAxes(Vector3f.UNIT_X, Vector3f.UNIT_Y,Vector3f.UNIT_Z);
				camNode.setLocalRotation(q);
			}
			
		}//end key input
		
		
		//handle mouse input
		if (MouseInput.get().isButtonDown(0)) 
		{
			
			//because dendrites can be densely packed need precision of triangles instead of bounding boxes
			PickResults pr = new TrianglePickResults(); 
			
			
			//Get the position that the mouse is pointing to
            Vector2f mPos = new Vector2f();
            mPos.set(MouseInput.get().getXAbsolute() ,MouseInput.get().getYAbsolute() );
     
            // Get the world location of that X,Y value
            Vector3f farPoint = display.getWorldCoordinates(mPos, 1.0f);
            Vector3f closePoint = display.getWorldCoordinates(mPos, 0.0f);
            //closePoint = camNode.getWorldTranslation(); //forget the displays location, tell me where camnode is
            
            
            // Create a ray starting from the camera, and going in the direction
            // of the mouse's location
            Ray mouseRay = new Ray(closePoint, farPoint.subtractLocal(closePoint).normalizeLocal());
            
            // Does the mouse's ray intersect the box's world bounds?
            pr.clear();
            pr.setCheckDistance(true);  //this function is undocumented
            rootNode.findPick(mouseRay, pr);
		
			//createLine(mouseRay.origin, mouseRay.direction); //debugging
			//createSphere(closePoint); //for debugging
            
            //set up for deselection
			if ( pr.getNumber() > 0)
			{
				//deselect the previous 
				if ( prevPick != null) prevPick.getTargetMesh().setRandomColors();
				
				if (prevPick != null) {
					/* this should be done in a listener after firing an event here*/
					for (INeuronMorphologyView c : getView3D().getCells()) {
						ISegmentView segView = ((NeuronMorphologyViewImpl)c).getSegmentFromGeomBatch(prevPick.getTargetMesh());
						if (segView != null) {
							if (segView.correspondsToSegment()) {
								c.getMorphology().unselectSegment(segView.getCorrespondingSegment());
							} else if (segView.correspondsToSegmentGroup()) {
								c.getMorphology().unselectSegmentGroup(segView.getCorrespondingSegmentGroup());
							}
						}
					}
				}
								
				//find the one that is closest
				//the 0th element is closest to the origin of the ray with checkdistance
				//This is the distance from the origin of the Ray to the nearest point on the BoundingVolume of the Geometry.
				prevPick = pr.getPickData(0);	//take the closest pick and set
				
				/* this should be done in a listener after firing an event here*/
				for (INeuronMorphologyView c : getView3D().getCells()) { //loop over all IStructure3Ds (the view representation of the morphology)
					/* Try to get a segView (view representation of a segment or segment group) that matches the target mesh 
					 * from the pick results within this INeuronMorphologyView*/
					ISegmentView segView = ((NeuronMorphologyViewImpl)c).getSegmentFromGeomBatch(prevPick.getTargetMesh()); 
					if (segView != null) { //if we found one
						/* tell the INeuronMorphology (the model representation of the morphology)
						 * to note that we have selected a segment or a segment group.  
						 * The SceneObserver will then get updated and change the color on the 
						 * appropriate geometry in the INeuronMorphologyView
						 */
						if (segView.correspondsToSegment()) {
							c.getMorphology().selectSegment(segView.getCorrespondingSegment()); 
						} else if (segView.correspondsToSegmentGroup()) {
							c.getMorphology().selectSegmentGroup(segView.getCorrespondingSegmentGroup());
						}
					}
				}
				
				prevPick.getTargetMesh().setSolidColor(ColorRGBA.yellow);
				//System.out.println("Picked: " + prevPick.getTargetMesh().getName());
			} //end if of pr > 0
		} //end if mouse button down
	}
	
	public DisplaySystem getDisplaySystem(){
		return display;
	}

	
	//called every frame update
	//now it's just a wrapper to handleInput
	protected void simpleUpdate() 
	{
		//the coordsDown and coordsUp code used to go here. It's gone now.
		handleInput();
	}
	
	/* 
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
        
		// Then we display the GUI
		disp.display();
        
        /** Draw the fps node to show the fancy information at the bottom. */
        r.draw(fpsNode);
        
        doDebug(r);
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
	
	public IView3D getView3D() {
		return view3D;
	}

	public IView2D getView2D() {
		return View2DImpl.getInstance();
	}
	
}
