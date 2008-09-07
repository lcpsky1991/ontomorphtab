

package edu.ucsd.ccdb.ontomorph2.view;


import com.jme.app.AbstractGame;
import com.jme.app.BaseSimpleGame;
import com.jme.bounding.BoundingSphere;
import com.jme.image.Texture;
import com.jme.input.FirstPersonHandler;
import com.jme.input.InputHandler;
import com.jme.input.KeyBindingManager;
import com.jme.input.KeyInput;
import com.jme.input.KeyInputListener;
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

	//=================================
	// Global Interface-Objects
	//=================================
	public ViewCamera camNode;					//thisobject needed for manipulating the camera in a simple way
	
	FirstPersonHandler fpHandler = null;
	Line debugRay = null;	//used for mouse picking debugging mode
	Cone wand = null;
	
	org.fenggui.Display disp; // FengGUI's display
	
	View3D view3D = null;
	private View3DMouseListener view3DMouseListener;
	private OMTKeyInputListener OMTKeyListener;
	
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
	
	public FirstPersonHandler getFPHandler() {
		return fpHandler;
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
		
		// We want a cursor to interact with FengGUI
		MouseInput.get().setCursorVisible(true);
		
		//(InputActionInterface action, java.lang.String deviceName, int button, int axis, boolean allowRepeats)
		/*
    	this.view3DMouseHandler = new View3DMouseHandler();
        input.addAction(this.view3DMouseHandler , InputHandler.DEVICE_MOUSE, InputHandler.BUTTON_ALL, InputHandler.AXIS_ALL, false );
        */
		this.view3DMouseListener = new View3DMouseListener();
		MouseInput.get().addListener(this.view3DMouseListener);
		
		this.OMTKeyListener = new OMTKeyInputListener();
		KeyInput.get().addListener(this.OMTKeyListener);
	}
			
	public OMTKeyInputListener getKeyInputListener() {
		return this.OMTKeyListener;
	}
	  
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
    	if (!OntoMorph2.isDebugMode()) return;	//do not waste processing time if debug mode is off
    	
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
		//handleKeyInput();	//should be mvoed to some other handler
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
       
        this.getCameraNode().getCamera().update();
        
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
	public ViewCamera getCameraNode() {
		return this.camNode;
	}
	
	public Node getMainViewRootNode() {
		return rootNode;
	}
}


