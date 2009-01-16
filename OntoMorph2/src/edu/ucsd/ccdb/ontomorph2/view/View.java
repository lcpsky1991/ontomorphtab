

package edu.ucsd.ccdb.ontomorph2.view;


import java.util.Iterator;
import java.util.concurrent.Callable;

import com.jme.app.AbstractGame;
import com.jme.app.BaseSimpleGame;
import com.jme.bounding.BoundingSphere;
import com.jme.image.Texture;
import com.jme.input.FirstPersonHandler;
import com.jme.input.KeyInput;
import com.jme.input.MouseInput;
import com.jme.light.PointLight;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.renderer.ColorRGBA;
import com.jme.renderer.Renderer;
import com.jme.renderer.pass.BasicPassManager;
import com.jme.renderer.pass.RenderPass;
import com.jme.scene.Line;
import com.jme.scene.Node;
import com.jme.scene.SceneElement;
import com.jme.scene.Spatial;
import com.jme.scene.Text;
import com.jme.scene.shape.Cone;
import com.jme.scene.shape.Quad;
import com.jme.scene.shape.Sphere;
import com.jme.scene.state.AlphaState;
import com.jme.scene.state.CullState;
import com.jme.scene.state.LightState;
import com.jme.scene.state.MaterialState;
import com.jme.scene.state.RenderState;
import com.jme.scene.state.TextureState;
import com.jme.scene.state.ZBufferState;
import com.jme.system.DisplaySystem;
import com.jme.system.PropertiesIO;

import com.jme.util.GameTaskQueue;
import com.jme.util.GameTaskQueueManager;
import com.jme.util.geom.Debugger;
import com.jmex.effects.glsl.BloomRenderPass;



import edu.ucsd.ccdb.ontomorph2.app.OntoMorph2;
import edu.ucsd.ccdb.ontomorph2.core.scene.Scene;
import edu.ucsd.ccdb.ontomorph2.core.tangible.SphereParticles;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Tangible;
import edu.ucsd.ccdb.ontomorph2.core.tangible.slide.Slide;
import edu.ucsd.ccdb.ontomorph2.util.FengJMEInputHandler;
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
public class View extends BaseSimpleGame 
{
	
	private static View instance = null;
	private Scene _scene = null;

	//================================= 
	// Global Interface-Objects
	//=================================
	private ViewCamera viewNode;					//thisobject needed for manipulating the camera in a simple way
	
	FirstPersonHandler fpHandler = null;
	Line debugRay = null;	//used for mouse picking debugging mode
	Cone wand = null;
	
	BasicPassManager pManager = new BasicPassManager();
	BloomRenderPass bloomRenderPass;
	private Node debugQuadsNode;
	
	org.fenggui.Display disp; // FengGUI's display

	View3D view3D = null;
	private View3DMouseListener view3DMouseListener;
	private OMTKeyInputListener OMTKeyListener;
	private TangibleView spatial, previousSpatial;
	FengJMEInputHandler guiInput;
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

	
	protected void simpleInitGame() 
	{
		this.viewNode = new ViewCamera();
		this.cam = viewNode.getCamera();
		display.setTitle("Whole Brain Catalog");
		
		rootNode.attachChild(viewNode);
		
		//as a hack, calling the main application class to do initialization
		//this is because model loading needs to have the view running in order to work
		  
		OntoMorph2.initialization();
		display.getRenderer().setBackgroundColor(ColorRGBA.black); //Set a black background.
				
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
        //buildPointLighting(rootNode);
        
        rootNode.updateRenderState();
        rootNode.updateModelBound();
        rootNode.updateWorldBound();
        rootNode.setCullMode(Node.CULL_NEVER);
        
		disp = View2D.getInstance();
		guiInput = new FengJMEInputHandler(disp);
		//Section for setting up the mouse and other input controls	
		configureControls();

		//pManager.add(bloomRenderPass);

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

	
	public void cleanUp()
	{
		super.cleanup();
		display.getRenderer().cleanup();
        if (bloomRenderPass != null){
        	bloomRenderPass.cleanup();
        	pManager.clearAll();
        }	
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
    protected final void render(float interpolation) 
    {
        super.render(interpolation);
       
        this.getCameraView().getCamera().update();
        
        Renderer r = display.getRenderer();

        r.clearStatistics();
        r.clearBuffers();
        //pManager.renderPasses(r);
        /** Draw the rootNode and all its children. */
        r.draw(rootNode);
       
        //pManager.add(bloomRenderPass);
        GameTaskQueueManager.getManager().getQueue(GameTaskQueue.RENDER).execute();
        if(pManager!=null)pManager.renderPasses(display.getRenderer());
	    
        /** Call simpleRender() in any derived classes. */
        simpleRender();
        
        /** Draw the fps node to show the fancy information at the bottom. */
        r.draw(fpsNode);
        
        doDebug(r);
          
        //Flush the renderQueue right before rendering the menu so that nothing can get on top of it
        r.renderQueue();
    
		// Then we display the GUI
		disp.display();

	        
        //r.cleanup();
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
        
       // System.out.println("interpolation " + interpolation);
       if(pManager!=null){pManager.updatePasses(interpolation);}
       
       //System.out.println(MouseInput.get().getXAbsolute() + " " + MouseInput.get().getYAbsolute());

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
	
	public void cleanup() {
		super.cleanup();

         //bloomRenderPass.cleanup();
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
	
	public Camera getCamera(){
		return this.cam;
	}

	//method use for debuggin purposes
	/*private void buildPointLighting(Node node) {
        // Create Light
        PointLight light = new PointLight();
        light.setLocation(new Vector3f(0, 0, 0));
        light.setDiffuse(new ColorRGBA(1f, 0.9f, 0.7f, 1f));
        //light.setAmbient(new ColorRGBA(0.5f, 0.5f, 0.5f, 0.5f));
        light.setShadowCaster(true);
        light.setEnabled(true);

        // Attach LightState
        LightState lightState = DisplaySystem.getDisplaySystem().getRenderer().createLightState();
        lightState.setEnabled(true);
        lightState.attach(light);
        node.setRenderState(lightState);
    }*/

//TODO: Move into different class
public void bloomIndicator(TangibleView rollOverSelected, boolean cameraLocation){
	
	//System.out.println("bloomIndicator");
	spatial = rollOverSelected;	

	this.pManager = new BasicPassManager();
	final RenderPass rootPass = new RenderPass() {

        /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void doRender(Renderer r) {
            DisplaySystem.getDisplaySystem().getRenderer().clearZBuffer();
            super.doRender(r);
        }
    };

	rootPass.add(spatial);
	indicator();
	Callable<?> loadlock = new Callable<Object>() {

        public Object call() throws Exception 
        {
            pManager.add(rootPass);
        	pManager.add(bloomRenderPass);
        	//spatial.setCullMode(SceneElement.CULL_NEVER);
    		//spatial.setRenderQueueMode(Renderer.QUEUE_OPAQUE);
        	//System.out.println("inside the object call method");
        	return null;
        }
    };
    
    GameTaskQueueManager.getManager().getQueue(GameTaskQueue.RENDER).enqueue(loadlock);
	//indicator()
	//pManager.clearAll();
	
	//pManager.clearAll();
	//pManager.add(bloomRenderPass);
	if(cameraLocation == false){
		pManager.cleanUp();
		pManager.clearAll();
		bloomRenderPass.cleanup();
		bloomRenderPass.cleanUp();
		bloomRenderPass.clearPassStates();
		pManager.remove(bloomRenderPass);
		//display.getRenderer().cleanup();
	}
    //pManager.add(bloomRenderPass);	   
    previousSpatial = spatial;
}

//TODO; Move into different class
public void indicator(){
	
	if(spatial instanceof SlideView){
	//System.out.println("slideview");
		//update geomtric states of the rollOverSelected object
        //rollOverSelected.updateRenderState();
		//rollOverSelected.updateGeometricState(0, true);
		
		//final RenderPass rootPass = new RenderPass();
		
		
		//pManager.add(controlPass);
		//System.out.println("pManager " + pManager);
		
		/*ZBufferState zs = display.getRenderer().createZBufferState();
        zs.setWritable(false);
        zs.setEnabled(true);
        spatial.setRenderState(zs);*/

	bloomRenderPass = new BloomRenderPass(this.cam, 4){

		private static final long serialVersionUID = 1L;

			@Override
	         public void doRender(Renderer r) {
				    super.doRender(r);
	         }
				
	     };
	bloomRenderPass.setUseCurrentScene(false);		
	//spatial.setRenderState(arg0)
	//	  System.out.println("is supported");
	//bloomRenderPass.setBlurIntensityMultiplier(bloomRenderPass.getBlurIntensityMultiplier() + 6.1f);
	bloomRenderPass.setBlurSize(bloomRenderPass.getBlurSize() + 0.001f);
	bloomRenderPass.setExposureCutoff(bloomRenderPass.getExposureCutoff() + 0.1f);
	bloomRenderPass.setExposurePow(bloomRenderPass.getExposurePow() - 1.5f);
	bloomRenderPass.add(spatial);	

	}
	
	else{
		//System.out.println("not slideview");
		final MaterialState msc = DisplaySystem.getDisplaySystem().getRenderer().createMaterialState();
	    msc.setAmbient(new ColorRGBA(0f, 0f, 0f, 1f));
	    msc.setEmissive(new ColorRGBA(0f, 0f, 0f, 1f));
	    msc.setDiffuse(new ColorRGBA(0f, 0f, 0f, 1f));
	    msc.setSpecular(new ColorRGBA(0f, 0f, 0f, 1f));
	    msc.setShininess(127);
	    
		bloomRenderPass = new BloomRenderPass(this.cam, 4){

		private static final long serialVersionUID = 1L;

			@Override
	         public void doRender(Renderer r) {
				 spatial.clearRenderState(RenderState.RS_MATERIAL);
				    spatial.setRenderState(msc);
				    spatial.updateRenderState();
				    super.doRender(r);
	         }
				
	     };
		bloomRenderPass.setUseCurrentScene(false);			
		       /*if(!bloomRenderPass.isSupported()) {
		    	   System.out.println(" is not supported");
		           Text t = new Text("Text", "GLSL Not supported on this computer.");
		           t.setRenderQueueMode(Renderer.QUEUE_ORTHO);
		           t.setLightCombineMode(LightState.OFF);
		           t.setLocalTranslation(new Vector3f(0,20,0));
		       } else {*/
		    //	  System.out.println("is supported");
		 bloomRenderPass.setExposurePow(bloomRenderPass.getExposurePow() - 2.5f);
	     bloomRenderPass.add(spatial);	
	     pManager.add(bloomRenderPass);
	     
	     CullState cs = display.getRenderer().createCullState();
	     cs.setCullMode(CullState.CS_BACK);
	     spatial.setRenderState(cs);
	     spatial.updateRenderState(); 
	     spatial.setCullMode(SceneElement.CULL_NEVER);
	}
}
	}



