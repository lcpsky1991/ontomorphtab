package edu.ucsd.ccdb.ontomorph2.view;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fenggui.ComboBox;
import org.fenggui.TextEditor;
import org.fenggui.composites.Window;
import org.fenggui.event.ISelectionChangedListener;
import org.fenggui.event.SelectionChangedEvent;
import org.fenggui.layout.StaticLayout;

import com.jme.app.BaseGame;
import com.jme.app.BaseSimpleGame;
import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.curve.BezierCurve;
import com.jme.image.Texture;
import com.jme.input.AbsoluteMouse;
import com.jme.input.ChaseCamera;
import com.jme.input.FirstPersonHandler;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.input.Mouse;
import com.jme.input.MouseInput;
import com.jme.input.ThirdPersonHandler;
import com.jme.input.controls.binding.KeyboardBinding;
import com.jme.light.PointLight;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Node;
import com.jme.scene.Spatial;
import com.jme.scene.TriMesh;
import com.jme.scene.shape.Box;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.TextureState;
import com.jme.system.DisplaySystem;
import com.jme.util.TextureManager;
import com.jme.util.export.binary.BinaryImporter;
import com.jme.util.geom.BufferUtils;
import com.jme.util.geom.Debugger;
import com.jmex.model.converters.FormatConverter;
import com.jmex.model.converters.MaxToJme;

import edu.ucsd.ccdb.ontomorph2.app.OntoMorph2;
import edu.ucsd.ccdb.ontomorph2.core.CellImpl;
import edu.ucsd.ccdb.ontomorph2.core.ICell;
import edu.ucsd.ccdb.ontomorph2.core.IMorphology;
import edu.ucsd.ccdb.ontomorph2.core.IScene;
import edu.ucsd.ccdb.ontomorph2.core.PositionImpl;
import edu.ucsd.ccdb.ontomorph2.core.RotationImpl;
import edu.ucsd.ccdb.ontomorph2.core.SceneImpl;
import edu.ucsd.ccdb.ontomorph2.misc.HelloMousePick;
import edu.ucsd.ccdb.ontomorph2.observers.SceneObserver;
import edu.ucsd.ccdb.ontomorph2.util.FengJMEInputHandler;
import edu.ucsd.ccdb.ontomorph2.util.X3DLoader;



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

	AbsoluteMouse amouse; 	//the mouse object ref to entire screen

	org.fenggui.Display disp; // FengGUI's display

//	there are two kinds of input, the FPS input and also FENG
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
	
	
	protected void simpleInitGame() {
		//as a hack, calling the main application class to do initialization
		//this is because model loading needs to have the view running in order to work
		OntoMorph2.initialization();
		
		rootNode.attachChild(view3D);
		
		//This sphere is for debugging purposes, need to see something to indicate
		Sphere s=new Sphere("My sphere",10,10,20f);
		// Do bounds for the sphere, but we'll use a BoundingBox this time
		s.setModelBound(new BoundingBox());
		s.updateModelBound();
		// Give the sphere random colors
		s.setRandomColors();
		s.setLocalTranslation(80,0,0);
		//s.setSolidColor(ColorRGBA.blue);
		
		rootNode.attachChild(s);

		
		Vector3f p1 = new Vector3f(-20,0,20);
		Vector3f p2 = new Vector3f(-34,-5,20);
		Vector3f p3 = new Vector3f(-20,-10,20);
		Vector3f[] array = {p1, p2, p3};
		BezierCurve c1 = new BezierCurve("Dentate Gyrus",array);
    	ColorRGBA defaultColor = ColorRGBA.yellow;
    	
    	float[] colorValues2 = {defaultColor.r, defaultColor.g, defaultColor.b, defaultColor.a, 
          		                defaultColor.r, defaultColor.g, defaultColor.b, defaultColor.a,
          		                defaultColor.r, defaultColor.g, defaultColor.b, defaultColor.a};
    	FloatBuffer colorBuffer = BufferUtils.createFloatBuffer(colorValues2);
    	
		c1.setColorBuffer(0,colorBuffer);
		
		rootNode.attachChild(c1);
		

		p1 = new Vector3f(-10,-5,20);

		p2 = new Vector3f(3,-9,20);

		p3 = new Vector3f(7,0,20);

		Vector3f p4 = new Vector3f(-9,20,20);

		Vector3f p5 = new Vector3f(-23,15,20);
		
		Vector3f[] array2 = {p1, p2, p3, p4, p5};
		BezierCurve c2 = new BezierCurve("CA",array2);
    	
    	float[] colorValues3 = {defaultColor.r, defaultColor.g, defaultColor.b, defaultColor.a, 
          		                defaultColor.r, defaultColor.g, defaultColor.b, defaultColor.a,
          		              defaultColor.r, defaultColor.g, defaultColor.b, defaultColor.a,
          		            defaultColor.r, defaultColor.g, defaultColor.b, defaultColor.a,
          		            defaultColor.r, defaultColor.g, defaultColor.b, defaultColor.a};
    	colorBuffer = BufferUtils.createFloatBuffer(colorValues3);
    	
		c2.setColorBuffer(0,colorBuffer);
		
		rootNode.attachChild(c2);
		  
		
		///** Set a black background.*/
		display.getRenderer().setBackgroundColor(ColorRGBA.black);
		
		
		///** Set up how our camera sees. */
		cam.setFrustumPerspective(45.0f, (float) display.getWidth() / (float) display.getHeight(), 1, 1000);
		
		
		//a locaiton on the Z axis a ways away
		Vector3f loc = new Vector3f(0.0f, 0.0f, 200.0f);
		Vector3f left = new Vector3f(-1.0f, 0.0f, 0.0f);
		Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
		Vector3f dir = new Vector3f(0.0f, 0f, -1.0f);

		///** Move our camera to a correct place and orientation. */
		//pX, pY, pZ, orientation
		cam.setFrame(loc, left, up, dir);
		
		///** Signal that we've changed our camera's location/frustum. */
		cam.update();
		
		///** Assign the camera to this renderer.*/
		display.getRenderer().setCamera(cam);
		
		//the code for keybindings was previously here, it's been seperated for code readability
		//Section for setting up the mouse and other input controls	
		configureControls();
		

		//Remove lighting for rootNode so that it will use our basic colors
		rootNode.setLightCombineMode(LightState.OFF);
		
		disp = View2DImpl.getInstance();
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
		
		//assign 'R' to reload the view to inital state
		KeyBindingManager.getKeyBindingManager().set("reinit", KeyInput.KEY_R);
		
		//assignt he camera to up, down, left, right
		KeyBindingManager.getKeyBindingManager().set("cam_forward", KeyInput.KEY_UP);
		KeyBindingManager.getKeyBindingManager().set("cam_back", KeyInput.KEY_DOWN);
		KeyBindingManager.getKeyBindingManager().set("cam_turn_ccw", KeyInput.KEY_LEFT);
		KeyBindingManager.getKeyBindingManager().set("cam_turn_cw", KeyInput.KEY_RIGHT);
	
		KeyBindingManager.getKeyBindingManager().set("info", KeyInput.KEY_I);
		
		//assign the "+" key on the keypad to the command "coordsUp"
		KeyBindingManager.getKeyBindingManager().set("coordsUp", KeyInput.KEY_ADD);
		//adds the "u" key to the command "coordsUp"
		KeyBindingManager.getKeyBindingManager().add("coordsUp", KeyInput.KEY_U);
		//assign the "-" key on the keypad to the command "coordsDown"
		KeyBindingManager.getKeyBindingManager().set("coordsDown", KeyInput.KEY_SUBTRACT);
		
		
		
		// We want a cursor to interact with FengGUI
		MouseInput.get().setCursorVisible(true);
	}
	

	private void handleInput() 
	{
		{
			//exit the program cleanly on ESC
			if (isAction("quit")) finish();
			
			if ( isAction("cam_forward")) 	cam.setLocation( cam.getLocation().add(0,0,1.1f) );			
			
			if ( isAction("cam_back"))		cam.setLocation( cam.getLocation().add(0,0,-1.1f) );
			
			if ( isAction("cam_turn_ccw"))	
			{ //left key
				{
					/* This quaternion stores a 180 degree rolling rotation */ 
					 Quaternion roll = new Quaternion(); 
					 int a = 5;
					 roll.fromAngleAxis( FastMath.PI * a /180 , new Vector3f(0,0,1) ); //rotates a degrees 
					 
					 
					 
					 rootNode.setLocalRotation(roll);;
				}
			}
			
			if ( isAction("cam_turn_cw"))	
			{
				//cam.setDirection( cam.getDirection().add(new Vector3f(-0.2f, 0, 0)));
				Quaternion roll = new Quaternion(); 
				int a = -5;
				roll.fromAngleAxis( FastMath.PI * a /180 , new Vector3f(0,0,1) ); //rotates a degrees 
				 
				 rootNode.setLocalRotation(roll);

			}
			
			if ( isAction("info"))
			{
				logger.log(Level.INFO, "\nLocation: " + cam.getLocation().toString() + 
						"\nDirection: " + cam.getDirection().toString());						
			}
			
			logger.log(Level.FINEST, cam.getDirection().toString() );
			
		}	
	}
	
	
	
	
	
	public DisplaySystem getDisplaySystem(){
		return display;
	}
	
	
	
	protected Node getX3DNeuron(String fileName) {
		Node n = null;
		try {
		X3DLoader converter = new X3DLoader();
		Spatial scene = converter.loadScene(new FileInputStream(fileName), null, null);
		
		n = new Node();
		n.attachChild(scene);
		} catch (Exception e) {
			logger.logp(Level.SEVERE, this.getClass().toString(), "simpleInitGame()", "Exception", e);
			System.exit(0);
		}
		return n;
	}
	
	//called every frame update
	protected void simpleUpdate() {
		//if the coordsDowncommand was activated
		if (KeyBindingManager.getKeyBindingManager().isValidCommand("coordsDown",false)) {
			//scale my texture down
			coordDelta -= .01f;
			//get my square's texture array
			FloatBuffer stBuffer = square.getTextureBuffer(0, 0);
			//change the values of the texture array
			stBuffer.put(2, coordDelta);
			stBuffer.put(5, coordDelta);
			stBuffer.put(6, coordDelta);
			stBuffer.put(7, coordDelta);
			//The texture coordinates are updated
		}
		
		//if the coordsUp Command was activated
		if (KeyBindingManager.getKeyBindingManager().isValidCommand("coordsUp",false)) {
			//scale my texture down
			coordDelta += .01f;
			//get my square's texture array
			FloatBuffer stBuffer = square.getTextureBuffer(0, 0);
			//change the values of the texture array
			stBuffer.put(2, coordDelta);
			stBuffer.put(5, coordDelta);
			stBuffer.put(6, coordDelta);
			stBuffer.put(7, coordDelta);
			//The texture coordinates are updated
		}
		
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
	
	private class CBListener implements ISelectionChangedListener
	{
		public void selectionChanged(SelectionChangedEvent selectionChangedEvent)
		{
			if (!selectionChangedEvent.isSelected()) return;
			String value = selectionChangedEvent.getToggableWidget().getText();
			

			Node x3dNeuron = getX3DNeuron(value+".x3d");
			//x3dNeuron.setLocalScale(0.05f);
			rootNode.attachChild(x3dNeuron);
		}
 
	}

	public IView3D getView3D() {
		return view3D;
	}

	public IView2D getView2D() {
		return View2DImpl.getInstance();
	}
	
}
