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

import com.jme.app.SimpleGame;
import com.jme.bounding.BoundingBox;
import com.jme.bounding.BoundingSphere;
import com.jme.image.Texture;
import com.jme.input.AbsoluteMouse;
import com.jme.input.FirstPersonHandler;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.input.MouseInput;
import com.jme.input.ThirdPersonHandler;
import com.jme.input.controls.binding.KeyboardBinding;
import com.jme.light.PointLight;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
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
import com.jmex.model.converters.FormatConverter;
import com.jmex.model.converters.MaxToJme;

import edu.ucsd.ccdb.ontomorph2.core.IScene;
import edu.ucsd.ccdb.ontomorph2.core.SceneImpl;
import edu.ucsd.ccdb.ontomorph2.misc.HelloMousePick;
import edu.ucsd.ccdb.ontomorph2.observers.SceneObserver;
import edu.ucsd.ccdb.ontomorph2.util.FengJMEInputHandler;
import edu.ucsd.ccdb.ontomorph2.util.X3DLoader;



/**
 * Represents a singleton.
 */

public class ViewImpl extends SimpleGame implements IView{

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

		//load the current scene
		_scene.load();
		rootNode.attachChild(view3D);
		
		//load neurons
		//Node n2 = new Node();
		//Node n3 = new Node();
		Quaternion x90 = new Quaternion();
		x90.fromAngleAxis(FastMath.DEG_TO_RAD*-90, new Vector3f(0,1,0));
		
		/*
		
		for (int i = 0; i < 3; i++) {
			Node neuron = getNeuron();
			neuron.setLocalTranslation(new Vector3f(12+(i/2),i+10,2));
			neuron.setLocalRotation(x90);
			n2.attachChild(neuron);
		}
		
		for (int i = 0; i < 3; i++) {
			Node neuron = getNeuron();
			neuron.setLocalTranslation(new Vector3f(18,i+10,2));
			n3.attachChild(neuron);
		}
		*/
		//rootNode.attachChild(n2);	//commented out because they are already being attached somehow
		//rootNode.attachChild(n3); 
		
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
		

		// Create the GUI
		initGUI();
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
			{
				{
					//cam.setDirection( cam.getDirection().add(new Vector3f(-0.2f, 0, 0)));
					Quaternion rotQuat = new Quaternion();
					
				    rotQuat.fromAngleAxis(-45, new Vector3f(1, 0, 0));
				    rootNode.setLocalRotation(rotQuat);
					
				}
			}
			
			if ( isAction("cam_turn_cw"))	
			{
				//cam.setDirection( cam.getDirection().add(new Vector3f(-0.2f, 0, 0)));
				Quaternion rotQuat = new Quaternion();
				
			    rotQuat.fromAngleAxis(45, new Vector3f(1, 0, 0));
			    rootNode.setLocalRotation(rotQuat);
				
			}
			
			if ( isAction("info"))
			{
				logger.log(Level.INFO, "\nLocation: " + cam.getLocation().toString() + "\nDirection: " + cam.getDirection().toString());
			}
			
			logger.log(Level.FINEST, cam.getDirection().toString() );
			
		}	
	}
	
	
	/*
	 * 
	 * This function isn't evne used anymore!? - CA
	 *  
	 * 
	protected TriMesh getTriMesh() {
		//Vertex positions for the mesh
		Vector3f[] vertexes={
				new Vector3f(0,0,0),
				new Vector3f(1,0,0),
				new Vector3f(0,1,0),
				new Vector3f(1,1,0)
		};
		
		//texture coordinates for each position
		coordDelta=1;
		Vector2f[] texCoords ={
				new Vector2f(0,0),
				new Vector2f(coordDelta,0),
				new Vector2f(0,coordDelta),
				new Vector2f(coordDelta,coordDelta),
		};
		//The indexes of Vertex/Normal/Color/TexCoord sets.  Every 3
		//makes a triangle.
		int[] indexes={
				0,1,2,1,3,2
		};
		//create the square
		square = new TriMesh("my mesh", BufferUtils.createFloatBuffer(vertexes),
				null,null, BufferUtils.createFloatBuffer(texCoords), 
				BufferUtils.createIntBuffer(indexes));
		
		//Point to slice image
		URL sliceLoc = ViewImpl.class.getClassLoader().getResource("slice.jpg");
		//get my texturestate
		TextureState ts = display.getRenderer().createTextureState();
		//get my texture
		Texture t= TextureManager.loadTexture(sliceLoc,
				Texture.MM_LINEAR, Texture.FM_LINEAR);
		//set a wrap for my texture so it repeats
		t.setWrap(Texture.WM_WRAP_S_WRAP_T);
		//set the texture to the texturestate
		ts.setTexture(t);
		//assign the texturestate to the square
		square.setRenderState(ts);
		//scale my square x larger
		square.setLocalScale(1);
		return square;
	}
	*/
	
	
	public DisplaySystem getDisplaySystem(){
		return display;
	}
	
	protected Node getNeuron() {
//		 point to a URL of my model
		URL model = ViewImpl.class.getClassLoader().getResource("neuron.3ds");
		
		//Create something to convert .obj format to .jme
		FormatConverter converter = new MaxToJme();
		//Point the converter to where it will find the .mtl file from
		//converter.setProperty("mtllib", model);
		
		//This byte array will hold my .jme file
		ByteArrayOutputStream BO = new ByteArrayOutputStream();
		Node neuron = null;
		try {
			//Use the format converter to convert .obj to .jme
			converter.convert(model.openStream(), BO);
			neuron = 
				(Node)BinaryImporter.getInstance().load(new ByteArrayInputStream(BO.toByteArray()));
			neuron.setLocalScale(.1f);
			neuron.setModelBound(new BoundingBox());
			neuron.updateModelBound();
			
		} catch (IOException e) {
			logger.logp(Level.SEVERE, this.getClass().toString(), "simpleInitGame()", "Exception", e);
			System.exit(0);
		}
		return neuron;
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
	 * Create our GUI.  FengGUI init code goes in here
	 *
	 */
	protected void initGUI()
	{
		try
		{
			int test=0;
			test=test+1;
			
			// Grab a display using an LWJGL binding
			//	   (obviously, since jME uses LWJGL)
			disp = new org.fenggui.Display(new org.fenggui.render.lwjgl.LWJGLBinding());
	 
			//try to add TextArea here but get OpenGLException
			TextEditor ta = new TextEditor(false);
			disp.addWidget(ta);
			ta.setText("Hallo Text");
			ta.setX(40);
			ta.setY(50);
			ta.setSizeToMinSize();
			
		}
		catch (Exception e)
		{
			logger.logp(Level.SEVERE, "ViewImpl", "initGUI", e.getMessage());
		}
		
 
		// Update the display with the newly added components
		disp.layout();
	}
	
	private class CBListener implements ISelectionChangedListener
	{
		public void selectionChanged(SelectionChangedEvent selectionChangedEvent)
		{
			if (!selectionChangedEvent.isSelected()) return;
			String value = selectionChangedEvent.getToggableWidget().getText();
			

			Node x3dNeuron = getX3DNeuron(value+".x3d");
			x3dNeuron.setLocalScale(0.05f);
			rootNode.attachChild(x3dNeuron);
		}
 
	}

	public IView3D getView3D() {
		return view3D;
	}

	public IView2D getView2D() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
