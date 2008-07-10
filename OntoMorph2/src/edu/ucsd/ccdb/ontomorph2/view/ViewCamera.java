package edu.ucsd.ccdb.ontomorph2.view;

import java.util.logging.Level;

import com.jme.app.SimpleGame;
import com.jme.curve.BezierCurve;
import com.jme.curve.Curve;
import com.jme.curve.CurveController;
import com.jme.input.action.MouseLook;
import com.jme.input.MouseInput;
import com.jme.input.RelativeMouse;
import com.jme.math.FastMath;
import com.jme.input.*;
import com.jme.intersection.PickResults;
import com.jme.math.Matrix3f;
import com.jme.math.Quaternion;
import com.jme.math.Ray;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.scene.CameraNode;
import com.jme.scene.Controller;
import com.jme.scene.Node;
import com.jme.app.BaseSimpleGame;

import edu.ucsd.ccdb.ontomorph2.util.CatmullRomCurve;
/**
 * Wraps the camera functionality.
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 * @author caprea
 *
 */
public class ViewCamera extends com.jme.scene.CameraNode {

	float camRotationRate = FastMath.PI * 5 / 180;	//(FastMath.PI * X / 180) corresponds to X degrees per (FPS?) = Rate/UnitOfUpdate 
	float invZoom = 1.0f; //zoom amount

	Camera cam;
	CameraNode camNode;
	Node rootNode = new Node("root Node");
	
	InputHandler input = new InputHandler();
	public ViewCamera() {
		init();	
	}
	
	/**
	 * Get the rate at which the camera rotates when turning it
	 * @return
	 */
	public float getRotationRate() {
		return camRotationRate;
	}
	
	protected void init() {
//		====================================
		// CAMERA SETUP
		//====================================
		
		cam = View.getInstance().getRenderer().getCamera();
		///** Set up how our camera sees. */
		float aspect = (float) View.getInstance().getDisplaySystem().getWidth() / (float) View.getInstance().getDisplaySystem().getHeight();
		//cam.setFrustum( 0, 150, -invZoom * aspect, invZoom * aspect, -invZoom, invZoom );
		cam.setFrustum(1.0f, 1000.0f, -0.55f * invZoom, 0.55f * invZoom, 0.4125f*invZoom, -0.4125f*invZoom);
		cam.update();
		
		///** Signal that we've changed our camera's location/frustum. */
		cam.update();

		///** Assign the camera to this renderer.*/
		//display.getRenderer().setCamera(cam);
		
		//camnode is for easy manipulation of the camera
		this.name = "camera node";
		this.setCamera(cam);

		setToSlideView();
		
		//camNode.setLocalTranslation(loc);
		System.out.println("Rotation: " + this.getLocalRotation() + "\nTranslation: " + 
				this.getLocalTranslation());
	
	}
	
	/**
	 * Set the camera to point towards the initial demo slide of the system. 
	 * (towards the hippocampus)
	 *
	 */
	public void setToSlideView() {
		Vector3f loc = new Vector3f(-300f, -118f, -180f);
		this.setLocalTranslation(loc);
		this.setLocalRotation(new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD*90, Vector3f.UNIT_Y));
	}
	
	/**
	 * Set the camera to point towards the lateral side of the atlas.
	 * Uses a position in space where the lateral side is easily visible.
	 */
	public void setToAtlasLateralView() {
		Vector3f loc = new Vector3f(300f, -118f, 300f);
		this.setLocalTranslation(loc);
		this.setLocalRotation(new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD*180, Vector3f.UNIT_Y));
	}
	
	/**
	 * Set the camera to point towards the medial side of the atlas.
	 * Uses a position in space where the medial side is easily visible.
	 */
	public void setToAtlasMedialView() {
		Vector3f loc = new Vector3f(300f, -118f, -700f);
		this.setLocalTranslation(loc);
		this.setLocalRotation(new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD*0, Vector3f.UNIT_Y));
	}

	/**
	 * Move the camera one unit forward
	 * @author caprea
	 */
	public void moveForward(float amount) {
		//find the vector of the direction pointing towards
		Vector3f dir = this.getCamera().getDirection().normalize().mult(amount);
		this.setLocalTranslation( this.getLocalTranslation().add(dir));
	}

	/**
	 * Move the camera one unit backward
	 * @author caprea
	 */
	public void moveBackward(float amount) {
//		find the vector of the direction pointing towards
		Vector3f dir = this.getCamera().getDirection().normalize().mult(amount).negate();
		this.setLocalTranslation( this.getLocalTranslation().add(dir));
	}

	/**
	 * Turn the camera one unit clockwise
	 * @author caprea
	 */
	public void turnClockwise(float amount) {
//		key right
		Quaternion roll = new Quaternion();
		roll.fromAngleAxis( -1*amount, Vector3f.UNIT_Y ); //rotates Rate degrees
		roll = this.getLocalRotation().multLocal(roll); // (q, save)
		this.setLocalRotation(roll);
	}

	/**
	 * Turn the camera one unit counter clockwise
	 * @author caprea
	 */
	public void turnCounterClockwise(float amount) {
//		left key
		Quaternion roll = new Quaternion();
		roll.fromAngleAxis( 1*amount, Vector3f.UNIT_Y ); //rotates Rate degrees
		roll = this.getLocalRotation().multLocal(roll); // (q, save)
		this.setLocalRotation(roll);
	}

	/**
	 * Turn the camera one unit downwards
	 * @author caprea
	 */
	public void turnDown(float amount) {
//		down
		Quaternion roll = new Quaternion();
		roll.fromAngleAxis( 1*amount, Vector3f.UNIT_X );//rotates Rate degrees
		roll = this.getLocalRotation().multLocal(roll); // (q, save)
		this.setLocalRotation(roll);
	}

	/**
	 * Turn the camera one unit upwards
	 * @author caprea
	 */
	public void turnUp(float amount) {
//		up
		Quaternion roll = new Quaternion();
		roll.fromAngleAxis( -1*amount, Vector3f.UNIT_X ); //rotates Rate degrees
		roll = this.getLocalRotation().multLocal(roll); // (q, save)
		this.setLocalRotation(roll);
	}

	/**
	 * Reset the position and rotation of the camera to a default.
	 * @author caprea
	 */
	public void reset() {
		
		Quaternion q = new Quaternion();
		q.fromAxes(Vector3f.UNIT_X, Vector3f.UNIT_Y,Vector3f.UNIT_Z);
		invZoom = 1.0f;
		this.getCamera().setFrustum(1.0f, 1000.0f, -0.55f * invZoom, 0.55f * invZoom, 0.4125f*invZoom, -0.4125f*invZoom);
		this.getCamera().update();
		this.setLocalRotation(q);
	}

	/**
	 * Zoom the camera in without changing its position
	 *
	 */
	public void zoomIn(float amount) {
		invZoom -= 0.01f * amount;
		//float aspect = (float) display.getWidth() / (float) display.getHeight();
		this.getCamera().setFrustum(1.0f, 1000.0f, -0.55f * invZoom, 0.55f * invZoom, 0.4125f*invZoom, -0.4125f*invZoom);
		//cam.setFrustum( 0, 150, -invZoom * aspect, invZoom * aspect, -invZoom, invZoom );
		this.getCamera().update();
	}

	/**
	 * Zoom the camera out without changing its position
	 *
	 */
	public void zoomOut(float amount) {
		invZoom += 0.01f * amount;
		//float aspect = (float) display.getWidth() / (float) display.getHeight();
		this.getCamera().setFrustum(1.0f, 1000.0f, -0.55f * invZoom, 0.55f * invZoom, 0.4125f*invZoom, -0.4125f*invZoom);
		//cam.setFrustum( 0, 150, -invZoom * aspect, invZoom * aspect, -invZoom, invZoom );
		this.getCamera().update();
	}
	
	/**
	 * Smoothly reposition, rotation, and zoom the camera to the position
	 * rotation and zoom level specified by the parameters.
	 */
	public void continuousZoomTo(Vector3f position, Quaternion rotation, float zoom) {
		
		
		/**CatmullRomCurve approach**/
		
		/*Vector3f up = new Vector3f(0, 1, 0);
		
		//create control Points
	    Vector3f[] locations = new Vector3f[2];
	    locations[0] = this.getCamera().getDirection();
	    locations[1] = position;
	    
	    Vector3f[] directions = new Vector3f[2];
	    directions[0] = position;
	    directions[1] = this.getCamera().getDirection();
	    	
	    Vector3f[] ups = new Vector3f[2];
	    ups[0] = new Vector3f(0,1,0);
	    ups[1] = new Vector3f(0,1,0);
	    
	    Vector3f[] lefts = new Vector3f[2];
	    lefts[0] = new Vector3f(1,0,0);
	    lefts[1] = new Vector3f(1,0,0);
	    
	    camNode = new CameraNode("camera node", cam);
	    
	    //set up four set of catmullromcurves to create path
	    Curve curve = new CatmullRomCurve("Curve", locations);
	    curve.setSteps(locations.length);
	    Curve curve2 = new CatmullRomCurve("Curve2", directions);
	    curve2.setSteps(directions.length);
	    Curve curve3 = new CatmullRomCurve("Curve3", ups);
	    curve3.setSteps(ups.length);
	    Curve curve4 = new CatmullRomCurve("Curve4", lefts);
	    curve4.setSteps(lefts.length);
	    
	    //calls constructor using the curves and the rotation quad
	    CameraAnimationController cameraAnimationController = new CameraAnimationController(curve, curve2, curve3, curve4, camNode, rotation);
	    cameraAnimationController.setActive(true);
	    System.out.println(cameraAnimationController +  "     camcontroller");
	    camNode.addController(cameraAnimationController);
	    cameraAnimationController.setSpeed(0.01f);
	    camNode.updateGeometricState(0.0f, true);
	    this.getCamera().update();
	    /*
	    camNode = new CameraNode("camera node", cam);
        CurveOnceController cc = new CurveOnceController(curve, camNode);
        cc.setActive(false);
        camNode.addController(cc);
        cc.setRepeatType(Controller.RT_CLAMP);
        cc.setUpVector(up);
        cc.setSpeed(zoom);
        cc.setDisableAfterClamp(true);
        cc.setAutoRotation(true);

        rootNode.attachChild(curve);*/      
        //System.out.println(curve + "curce"); */
	    
	    
	    
		/**BezierCurve approach -- not working, but right path**/
		/*
		BezierCurve bc;
		//get camera current position
		Vector3f currentPosition = this.getCamera().getDirection();
		
		//create the path the camera will take
		Vector3f[] cameraPoints = new Vector3f[]{
				cameraPoints[0] = currentPosition;
				cameraPoints[1] = position;
		};
		
		System.out.println("position " + position + "current " + currentPosition);
		
		//create a path for the camera
		bc = new BezierCurve("camera path", cameraPoints);
		
		//create Camera Node to manipulate camera
		camNode = new CameraNode("camera node", cam);
		
		//create controller to move cameraNode along the desire path
		CurveController c = new CurveController(bc, camNode);
		camNode.addController(c);
		rootNode.attachChild(camNode);
		
		System.out.println("Current direction of the camera" + currentPosition);
		this.getCamera().onFrameChange();
		simpleUpdate(position,rotation);
		this.getCamera().update();*/
		System.out.println("Smoothly zooming to: " + position.toString() + ", rotating to: " 
				+ rotation.toString() + ", changing zoom to: " + zoom);
	}
	
	/**
	 * Smoothly reposition, rotate, and zoom the camera to the atlas
	 * medial view.
	 */
	public void smoothlyZoomToAtlasMedialView() {
		Vector3f loc = new Vector3f(300f, -118f, -700f);
		Quaternion rotation = new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD*0, Vector3f.UNIT_Y);
		continuousZoomTo(loc, rotation, invZoom);
	}
	
	
	/**Functioa that will take care of Mouse clicking and Dragging**/
	public void MouseClickDragCamera(RelativeMouse mouse, Camera cam, float speed){
		 //System.out.println("click and drag Camera");
		 //RelativeMouse mouse = new RelativeMouse("Mouse Input");
	     //mouse.registerWithInputHandler( input );
  		 //PickResults pr;   
		 //System.out.println("position: " + position + " cam: " + cam + " speed: " + speed);
		 InputHandler inputHandler = new InputHandler();
	     
		 //System.out.println("mouse " + mouse);
		 MouseClickAndDrag mouseLook = new MouseClickAndDrag(mouse, cam, speed);
		 mouseLook.setLockAxis(new Vector3f(cam.getUp().x, cam.getUp().y,cam.getUp().z));
		 inputHandler.addAction(mouseLook);
		 
		 this.getCamera().update();
		/*if(MouseInput.get().isButtonDown(0)){
            //Vector2f screenPos = new Vector2f();
            //screenPos.set(mouse.getHotSpotPosition().x, mouse.getHotSpotPosition().y);
            System.out.println("position" + screenPos);
            System.out.println(worldCoords);
            // create a ray starting from the camera, and going in the direction
            // of the mouses location
            Ray mouseRay = new Ray(
                    worldCoords,
                    worldCoords2.subtractLocal(worldCoords).normalizeLocal()
            );
            // Does the mouses ray intersect the boxs world bounds?
            //pr.clear();
            rootNode.findPick(mouseRay,pr);*/
		
	}
}
