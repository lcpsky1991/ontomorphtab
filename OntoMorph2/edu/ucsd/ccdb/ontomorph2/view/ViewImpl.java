package edu.ucsd.ccdb.ontomorph2.view;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.FloatBuffer;
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
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
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

	org.fenggui.Display disp; // FengGUI's display

	FengJMEInputHandler input;
	
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
	
	protected ViewImpl() {
		this.setDialogBehaviour(SimpleGame.ALWAYS_SHOW_PROPS_DIALOG);
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
		Node n2 = new Node();
		Node n3 = new Node();
		Quaternion x90 = new Quaternion();
		x90.fromAngleAxis(FastMath.DEG_TO_RAD*-90, new Vector3f(0,1,0));
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
		rootNode.attachChild(n2);
		rootNode.attachChild(n3);
		
		//assign the "+" key on the keypad to the command "coordsUp"
		KeyBindingManager.getKeyBindingManager().set("coordsUp", 
				KeyInput.KEY_ADD);
		//adds the "u" key to the command "coordsUp"
		KeyBindingManager.getKeyBindingManager().add("coordsUp", KeyInput.KEY_U);
		//assign the "-" key on the keypad to the command "coordsDown"
		KeyBindingManager.getKeyBindingManager().set("coordsDown", KeyInput.KEY_SUBTRACT);
		

		// Create the GUI
		initGUI();
	}
	
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
		//scale my square 10x larger
		square.setLocalScale(50);
		return square;
	}
	
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
	}
	
	/**
	 * Create our GUI.  FengGUI init code goes in here
	 *
	 */
	protected void initGUI()
	{
		// Grab a display using an LWJGL binding
		//	   (obviously, since jME uses LWJGL)
		disp = new org.fenggui.Display(new org.fenggui.render.lwjgl.LWJGLBinding());
 
		input = new FengJMEInputHandler(disp);
 
		//	 Create a dialog and set it to some location on the screen
		Window frame = new Window();
		disp.addWidget(frame);
		frame.setX(20);
		frame.setY(350);
		frame.setSize(200, 100);
		frame.setShrinkable(false);
		//frame.setExpandable(true);
		frame.setTitle("Pick a file...");
		frame.getContentContainer().setLayoutManager(new StaticLayout());
 
		// Create a combobox with some random values in it
		//   we'll change these values to something more useful later on.
		ComboBox<String> list = new ComboBox<String>();
		frame.addWidget(list);
		list.setSize(150, list.getMinHeight());
		list.setShrinkable(false);
		list.setX(25);
		list.setY(25);
		list.addItem("c20466");
		list.addItem("1220882a");
		 
		list.addSelectionChangedListener(new CBListener());
 
		//try to add TextArea here but get OpenGLException
		TextEditor ta = new TextEditor(false);
		disp.addWidget(ta);
		ta.setText("Hallo Text");
		ta.setX(40);
		ta.setY(50);
		//ta.setSize(100, ta.getAppearance().getFont().get)
		ta.setSizeToMinSize();
 
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
