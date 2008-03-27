package edu.ucsd.ccdb.ontomorph2.misc;

import org.fenggui.ComboBox;
import org.fenggui.TextEditor;
import org.fenggui.composites.Window;
import org.fenggui.event.ISelectionChangedListener;
import org.fenggui.event.SelectionChangedEvent;
import org.fenggui.layout.StaticLayout;
 
import com.jme.app.BaseGame;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.input.MouseInput;
import com.jme.light.PointLight;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.scene.Node;
import com.jme.scene.shape.Box;
import com.jme.scene.state.LightState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.system.JmeException;
import com.jme.util.Timer;
import com.jme.util.lwjgl.LWJGLTimer;

import edu.ucsd.ccdb.ontomorph2.util.FengJMEInputHandler;
 
/**
 * FengJME - A test class for integrating FengGUI and jME.
 * 
 * @author Josh (updated by neebie)
 *
 */
public class FengJME extends BaseGame
{
	Camera cam; // Camera for jME
	Node rootNode; // The root node for the jME scene
	PointLight light; // Changeable light
	FengJMEInputHandler input;
	Timer timer;
 
	Box box; // A box
 
	org.fenggui.Display disp; // FengGUI's display
 
 
	/* (non-Javadoc)
	 * @see com.jme.app.BaseGame#cleanup()
	 */
	@Override
	protected void cleanup()
	{
		// Clean up the mouse
		MouseInput.get().removeListeners();
		MouseInput.destroyIfInitalized();
		// Clean up the keyboard
		KeyInput.destroyIfInitalized();
	}
 
 
	/* (non-Javadoc)
	 * @see com.jme.app.BaseGame#initGame()
	 */
	@Override
	protected void initGame()
	{
		// Create our root node
		rootNode = new Node("rootNode");
		// Going to enable z-buffering
		ZBufferState buf = display.getRenderer().createZBufferState();
		buf.setEnabled(true);
		buf.setFunction(ZBufferState.CF_LEQUAL);
		// ... and set the z-buffer on our root node
		rootNode.setRenderState(buf);
 
		// Create a white light and enable it
		light = new PointLight();
		light.setDiffuse(new ColorRGBA(1.0f, 1.0f, 1.0f, 1.0f));
		light.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 1.0f));
		light.setLocation(new Vector3f(100, 100, 100));
		light.setEnabled(true);
		/** Attach the light to a lightState and the lightState to rootNode. */
		LightState lightState = display.getRenderer().createLightState();
		lightState.setEnabled(true);
		lightState.attach(light);
		rootNode.setRenderState(lightState);
 
		// Create our box
		box = new Box("The Box", new Vector3f(-1, -1, -1), new Vector3f(1, 1, 1));
		box.updateRenderState();
		// Rotate the box 25 degrees along the x and y axes.
		Quaternion rot = new Quaternion();
		rot.fromAngles(FastMath.DEG_TO_RAD * 25, FastMath.DEG_TO_RAD * 25, 0.0f);
		box.setLocalRotation(rot);
		// Attach the box to the root node
		rootNode.attachChild(box);
 
		// Update our root node
		rootNode.updateGeometricState(0.0f, true);
		rootNode.updateRenderState();
 
		// Create the GUI
		initGUI();
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
		frame.setTitle("Pick a color");
		frame.getContentContainer().setLayoutManager(new StaticLayout());
 
		// Create a combobox with some random values in it
		//   we'll change these values to something more useful later on.
		ComboBox<String> list = new ComboBox<String>();
		frame.addWidget(list);
		list.setSize(150, list.getMinHeight());
		list.setShrinkable(false);
		list.setX(25);
		list.setY(25);
		list.addItem("White");
		list.addItem("Green");
		list.addItem("Blue");
		list.addItem("Red");
 
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
 
 
	/* (non-Javadoc)
	 * @see com.jme.app.BaseGame#initSystem()
	 */
	@Override
	protected void initSystem()
	{
		try
		{
			// Initialize our jME display system
			display = DisplaySystem.getDisplaySystem(properties.getRenderer());
			display.createWindow(properties.getWidth(), properties.getHeight(), properties.getDepth(), properties
					.getFreq(), properties.getFullscreen());
 
			// Get a camera based on the window settings
			cam = display.getRenderer().createCamera(display.getWidth(), display.getHeight());
		}
		catch (JmeException ex)
		{
			ex.printStackTrace();
			System.exit(1);
		}
 
		/** Set a black background.*/
		display.getRenderer().setBackgroundColor(ColorRGBA.black);
		/** Set up how our camera sees. */
		cam.setFrustumPerspective(45.0f, (float) display.getWidth() / (float) display.getHeight(), 1, 1000);
		Vector3f loc = new Vector3f(0.0f, 0.0f, 15.0f);
		Vector3f left = new Vector3f(-1.0f, 0.0f, 0.0f);
		Vector3f up = new Vector3f(0.0f, 1.0f, 0.0f);
		Vector3f dir = new Vector3f(0.0f, 0f, -1.0f);
		/** Move our camera to a correct place and orientation. */
		cam.setFrame(loc, left, up, dir);
		/** Signal that we've changed our camera's location/frustum. */
		cam.update();
		/** Assign the camera to this renderer.*/
		display.getRenderer().setCamera(cam);
 
		// We want a cursor to interact with FengGUI
		MouseInput.get().setCursorVisible(true);
 
		// Bind the Escape key to kill our test app
		KeyBindingManager.getKeyBindingManager().set("quit", KeyInput.KEY_ESCAPE);
 
		// Create our timer
		timer = new LWJGLTimer();
	}
 
 
	/* (non-Javadoc)
	 * @see com.jme.app.BaseGame#reinit()
	 */
	@Override
	protected void reinit()
	{
		// TODO Auto-generated method stub
 
	}
 
 
	/* (non-Javadoc)
	 * @see com.jme.app.BaseGame#render(float)
	 */
	@Override
	protected void render(float interpolation)
	{
		// First we draw our jME scene.  This must be called before
		//   anything will even show up.
		//   FIXME: This throws a NullPointerException when the app exits.
		//          Must investigate why.
		display.getRenderer().clearBuffers();
		// We could draw the GUI here, but I find it cleaner to just do
		//    all the jME engine calls together.
		display.getRenderer().draw(rootNode);
 
		// Then we display the GUI
		disp.display();
	}
 
 
	/* (non-Javadoc)
	 * @see com.jme.app.BaseGame#update(float)
	 */
	@Override
	protected void update(float interpolation)
	{
		timer.update();
		float tpf = timer.getTimePerFrame();
		input.update(tpf);
		if (!input.wasKeyHandled())
		{
			// Check to see if Escape was pressed
			if (KeyBindingManager.getKeyBindingManager().isValidCommand("quit")) finish();
		}
	}
 
 
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		FengJME app = new FengJME();
		app.setDialogBehaviour(ALWAYS_SHOW_PROPS_DIALOG);
		app.start();
	}
 
	private class CBListener implements ISelectionChangedListener
	{
		public void selectionChanged(SelectionChangedEvent selectionChangedEvent)
		{
			if (!selectionChangedEvent.isSelected()) return;
			String value = selectionChangedEvent.getToggableWidget().getText();
			if ("White".equals(value)) light.setDiffuse(ColorRGBA.white);
			if ("Red".equals(value)) light.setDiffuse(ColorRGBA.red);
			if ("Blue".equals(value)) light.setDiffuse(ColorRGBA.blue);
			if ("Green".equals(value)) light.setDiffuse(ColorRGBA.green);
		}
 
	}
 
}
