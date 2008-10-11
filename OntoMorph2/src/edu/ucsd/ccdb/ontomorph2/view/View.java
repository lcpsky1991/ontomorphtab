

package edu.ucsd.ccdb.ontomorph2.view;


import com.jme.app.AbstractGame;
import com.jme.app.BaseSimpleGame;
import com.jme.bounding.BoundingSphere;
import com.jme.image.Texture;
import com.jme.input.FirstPersonHandler;
import com.jme.input.KeyInput;
import com.jme.input.MouseInput;
import com.jme.light.PointLight;
import com.jme.math.FastMath;
import com.jme.math.Vector3f;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.scene.Line;
import com.jme.scene.Node;
import com.jme.scene.shape.Cone;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.system.PropertiesIO;
import com.jme.util.TextureManager;
import com.jme.util.geom.Debugger;
import com.jmex.effects.particles.ParticleFactory;
import com.jmex.effects.particles.ParticleMesh;
import com.jmex.effects.particles.ParticlePoints;

import edu.ucsd.ccdb.ontomorph2.app.OntoMorph2;
import edu.ucsd.ccdb.ontomorph2.core.scene.Scene;
import edu.ucsd.ccdb.ontomorph2.util.FengJMEInputHandler;

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
	private ViewCamera viewNode;					//thisobject needed for manipulating the camera in a simple way
	
	FirstPersonHandler fpHandler = null;
	Line debugRay = null;	//used for mouse picking debugging mode
	Cone wand = null;
	
	org.fenggui.Display disp; // FengGUI's display

	View3D view3D = null;
	private View3DMouseListener view3DMouseListener;
	private OMTKeyInputListener OMTKeyListener;
	
	FengJMEInputHandler guiInput;
	ParticlePoints pPoints = ParticleFactory.buildPointParticles("particles", 50); 
	private ParticleMesh pMesh;
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
		this.viewNode = new ViewCamera();
		this.cam = viewNode.getCamera();
		
		rootNode.attachChild(viewNode);
		
		//as a hack, calling the main application class to do initialization
		//this is because model loading needs to have the view running in order to work
		  
		OntoMorph2.initialization();
		display.getRenderer().setBackgroundColor(ColorRGBA.black); //Set a black background.
		
		//ugly hack
		if ("demo".equals(OntoMorph2.getWBCProperties().getProperty(OntoMorph2.SCENE))) 
		{
			display.setTitle("Spatial and Semantic Representations");
		}
		else 
		{
			display.setTitle("Whole Brain Catalog");
		}
		
		rootNode.attachChild(view3D);

		//Remove lighting for rootNode so that it will use our basic colors
		lightState.detachAll();
        
		PointLight light2 = new PointLight();
        //light.setDiffuse( new ColorRGBA( 0.75f, 0.75f, 0.75f, 0.75f ) );
        light2.setAmbient( new ColorRGBA( 0.5f, 0.5f, 0.5f, 1.0f ) );
        light2.setLocation( new Vector3f( 387, -57, -39 ) );
        
        ColorRGBA min = new ColorRGBA();
        min.set(0.05f, 0.05f, 0.05f, 1.0f);
        light2.setDiffuse(new ColorRGBA(min));
        light2.setSpecular(new ColorRGBA(min));
        
        ColorRGBA amb = new ColorRGBA();
        amb.set(0.5f, 0.5f, 0.5f, 1.0f);
        light2.setAmbient(amb);
        
        light2.setEnabled( true );
        
        lightState.attach(light2);
        
        lightState.setSeparateSpecular(true);
        
		rootNode.setLightCombineMode(LightState.OFF);
		rootNode.updateRenderState();
		
		disp = View2D.getInstance();
		guiInput = new FengJMEInputHandler(disp);
		//Section for setting up the mouse and other input controls	
		configureControls();
	
	}
	
	public FirstPersonHandler getFPSHandler() {
		return fpHandler;
	}
    	
	private void configureControls()
	{
		
		fpHandler = new FirstPersonHandler(viewNode.getCamera(), 50, viewNode.getRotationRate()); //(cam, moveSpeed, turnSpeed)
		
		//This is where we disable the FPShooter controls that are created by default by JME	
        input = fpHandler;
        
		
        //Disable both of these because I want to track things with the camera
        
        fpHandler.getKeyboardLookHandler().setEnabled( true );
        fpHandler.getMouseLookHandler().setEnabled( true);
		
        
        input.clearActions();	//removes all input actions not specifically programmed
        input.addToAttachedHandlers(guiInput);
		// We want a cursor to interact with FengGUI
		MouseInput.get().setCursorVisible(true);
		
		this.view3DMouseListener = new View3DMouseListener(disp, guiInput);
		MouseInput.get().addListener(this.view3DMouseListener);
		
		this.OMTKeyListener = new OMTKeyInputListener(disp, guiInput);
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
        super.render(interpolation);
       
        this.getCameraView().getCamera().update();
        
        Renderer r = display.getRenderer();

        r.clearBuffers();

        /** Draw the rootNode and all its children. */
        r.draw(rootNode);
        
        /** Call simpleRender() in any derived classes. */
        simpleRender();
        
        /** Draw the fps node to show the fancy information at the bottom. */
        r.draw(fpsNode);
        
        doDebug(r);
        
        //Flush the renderQueue right before rendering the menu so that nothing can get on top of it
        r.renderQueue();
       
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
        pPoints.getParticleController().update(interpolation);

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
	public ViewCamera getCameraView() {
		return this.viewNode;
	}
	
	public Node getMainViewRootNode() {
		return rootNode;
	}
	
	public boolean indicator(Vector3f location){
		boolean state = false;
		
		/*Sphere s = new Sphere("Sphere", location,30,30,2);
		s.setModelBound(new BoundingSphere());
        s.updateModelBound();

		rootNode.attachChild(s);
		
	    pPoints.setPointSize(10);
	    pPoints.setAntialiased(true);
	    pPoints.setEmissionDirection(new Vector3f(0, 1, 0));
	    pPoints.setOriginOffset(location);
	    pPoints.setInitialVelocity(.006f);
	    
	    rootNode.attachChild(pPoints);*/
		AlphaState as1 = display.getRenderer().createAlphaState();
	    as1.setBlendEnabled(true);
	    as1.setSrcFunction(AlphaState.SB_SRC_ALPHA);
	    as1.setDstFunction(AlphaState.DB_ONE);
	    as1.setTestEnabled(true);
	    as1.setTestFunction(AlphaState.TF_GREATER);
	    as1.setEnabled(true);

	    TextureState ts = display.getRenderer().createTextureState();
	    ts.setTexture(
	        TextureManager.loadTexture(
	        jmetest.effects.TestParticleSystem.class.getClassLoader().getResource(
	        "jmetest/data/texture/flaresmall.jpg"),
	        Texture.MM_LINEAR_LINEAR,
	        Texture.FM_LINEAR));
	    ts.setEnabled(true);

	    pMesh = ParticleFactory.buildParticles("particles", 500);
	    pMesh.setOriginOffset(location);
	    pMesh.setEmissionDirection(new Vector3f(0,1,0));
	    pMesh.setInitialVelocity(.006f);
	    pMesh.setStartSize(2.5f);
	    pMesh.setEndSize(2.5f);
	    pMesh.setMinimumLifeTime(1200f);
	    pMesh.setMaximumLifeTime(1400f);
	    pMesh.setStartColor(new ColorRGBA(1, 1, 1, 1));
	    pMesh.setEndColor(new ColorRGBA(0, 1, 1, 0));
	    pMesh.setMaximumAngle(360f * FastMath.DEG_TO_RAD);
	    pMesh.getParticleController().setControlFlow(false);
	    pMesh.warmUp(60);
	    rootNode.setRenderState(ts);
	    rootNode.setRenderState(as1);
	                ZBufferState zstate = display.getRenderer().createZBufferState();
	                zstate.setEnabled(false);
	                pMesh.setRenderState(zstate);
	    pMesh.setModelBound(new BoundingSphere());
	    pMesh.updateModelBound();

	    rootNode.attachChild(pMesh);
	    
	    return state;
	}
	
	
}


