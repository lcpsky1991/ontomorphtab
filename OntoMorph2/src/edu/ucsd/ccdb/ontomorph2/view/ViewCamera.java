package edu.ucsd.ccdb.ontomorph2.view;

import java.util.logging.Level;

import org.fenggui.Display;

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
import com.jme.renderer.ColorRGBA;
import com.jme.scene.CameraNode;
import com.jme.scene.Controller;
import com.jme.scene.Node;
import com.jme.util.geom.BufferUtils;
import com.jme.app.BaseSimpleGame;
import com.jme.bounding.BoundingSphere;

import edu.ucsd.ccdb.ontomorph2.core.spatial.PositionVector;
import edu.ucsd.ccdb.ontomorph2.core.spatial.RotationVector;
import edu.ucsd.ccdb.ontomorph2.util.CatmullRomCurve;
import edu.ucsd.ccdb.ontomorph2.util.CurveOnceController;
import edu.ucsd.ccdb.ontomorph2.util.Log;
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
	
	protected void reduceRotationRate() {
		camRotationRate -= 0.001;
		if (camRotationRate < 0) {
			camRotationRate = 0;
		}
	}
	
	protected void increaseRotationRate() {
		camRotationRate += 0.001;
		if (camRotationRate > 2*FastMath.PI) {
			camRotationRate = 2*FastMath.PI;
		}
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
		Log.warn("Rotation: " + this.getLocalRotation() + "\nTranslation: " + 
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
		roll.fromAngleAxis( -1*amount*getRotationRate(), Vector3f.UNIT_Y ); //rotates Rate degrees
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
		roll.fromAngleAxis( 1*amount*getRotationRate(), Vector3f.UNIT_Y ); //rotates Rate degrees
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
		roll.fromAngleAxis( 1*amount*getRotationRate(), Vector3f.UNIT_X );//rotates Rate degrees
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
		roll.fromAngleAxis( -1*amount*getRotationRate(), Vector3f.UNIT_X ); //rotates Rate degrees
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
		this.reduceRotationRate();
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
		this.increaseRotationRate();
		//float aspect = (float) display.getWidth() / (float) display.getHeight();
		this.getCamera().setFrustum(1.0f, 1000.0f, -0.55f * invZoom, 0.55f * invZoom, 0.4125f*invZoom, -0.4125f*invZoom);
		//cam.setFrustum( 0, 150, -invZoom * aspect, invZoom * aspect, -invZoom, invZoom );
		this.getCamera().update();
	}
	
	/**
	 * Smoothly reposition, rotation, and zoom the camera to the position
	 * rotation and zoom level specified by the parameters.
	 */
	public void continuousZoomTo(Vector3f location, Vector3f objectPosition, float zoom) {		
		
        Vector3f[] points = new Vector3f[2];
        points[0] = cam.getLocation();
        points[1] = location;

        float distance = points[0].distance(points[1]);
        System.out.println("distance " + distance);
        Log.warn("current camera position" + points[0]);
        CatmullRomCurve curve = new CatmullRomCurve("Curve", points);
        /*ColorRGBA[] colors = new ColorRGBA[2];
        colors[0] = new ColorRGBA(0, 1, 0, 1);
        colors[1] = new ColorRGBA(0, 0, 1, 1);
        curve.setColorBuffer(0, BufferUtils.createFloatBuffer(colors));*/

        Vector3f up = new Vector3f(0,1,0);
        CurveOnceController cc = new CurveOnceController(curve, this, objectPosition);
        Log.warn("up" + up);
        cc.setActive(false);
        Log.warn("camNode " + camNode);
        Log.warn(cc + " curve ");
        this.addController(cc);
        cc.setRepeatType(Controller.RT_CLAMP);
        cc.setUpVector(up);
        cc.setSpeed(zoom);
        cc.setDisableAfterClamp(true);
        cc.setAutoRotation(true);
        this.attachChild(curve);
        cc.setActive(true);
        
		//Log.warn("Smoothly zooming to: " + position.toString() + ", rotating to: " 
				//+ rotation.toString() + ", changing zoom to: " + zoom);
		/**CatmullRomCurve approach**/
		/*CameraAnimationController cameraAnimationController;
		//CameraAnimationController cameraAnimationController = new CameraAnimationController();
		Vector3f up = new Vector3f(0, 1, 0);
		
		//create control Points
	    Vector3f[] locations = new Vector3f[2];
	    locations[0] = cam.getLocation();
	    locations[1] = position;
	    
	    Vector3f[] directions = new Vector3f[2];
	    directions[0] = position;
	    directions[1] = position;
	    	
	    Vector3f[] ups = new Vector3f[2];
	    ups[0] = new Vector3f(0,1,0);
	    ups[1] = new Vector3f(0,1,0);
	    
	    Vector3f[] lefts = new Vector3f[2];
	    lefts[0] = new Vector3f(1,0,0);
	    lefts[1] = new Vector3f(1,0,0);


	      Curve locCrc = new CatmullRomCurve("locCurve", locations);
	      locCrc.setSteps(locations.length);
	      Curve dirCrc = new CatmullRomCurve("dirCurve", directions);
	      dirCrc.setSteps(directions.length);
	      Curve leftCrc = new CatmullRomCurve("leftCurve", lefts);
	      leftCrc.setSteps(lefts.length);
	      Curve upCrc = new CatmullRomCurve("upCurve", ups);
	      upCrc.setSteps(ups.length);
	      cameraAnimationController = new CameraAnimationController(locCrc, upCrc, leftCrc, dirCrc, this);
	      cameraAnimationController.setActive(true);
	      this.addController(cameraAnimationController);
	      cameraAnimationController.setSpeed(0.01f);
	      this.updateGeometricState(0.0f, true);*/
        //System.out.println(curve + "curce"); 
	       
	    
		/**BezierCurve approach -- not working, but right path**/
		/*
		BezierCurve bc;
		//get camera current position
		Vector3f currentPosition = this.getCamera().getLocation();
		
		//create the path the camera will take
		Vector3f[] cameraPoints = new Vector3f[2];
		cameraPoints[0] = currentPosition;
		cameraPoints[1] = position;
		
		System.out.println("position " + position + "current " + currentPosition);
		
		//create a path for the camera
		bc = new BezierCurve("camera path", cameraPoints);
		camNode = new CameraNode("camera Node",this.getCamera());		
		//create controller to move cameraNode along the desire path
		CurveController c = new CurveController(bc, camNode);
		c.setRepeatType(Controller.RT_CLAMP);
		c.setSpeed(.25f);
		camNode.addController(c);
		//c.setActive(true);
        //c.setUpVector(new Vector3f(0.0f,1.0f,0.0f));
        //c.setAutoRotation(false);  
		//camNode.addController(c);
		rootNode.attachChild(camNode);
		
	    pos = position;
		rot = rotation;
		System.out.println("Current direction of the camera" + currentPosition);
		//this.getCamera().onFrustumChange();
		//camNode.simpleUpdate(position,rotation);
		//this.getCamera().setDirection(position);
		//this.setLocalRotation(rotation);
		this.update();*/
	
	}
	
	/**
	 * Smoothly reposition, rotate, and zoom the camera to the atlas
	 * medial view.
	 */
	public void smoothlyZoomToAtlasMedialView() {
		Vector3f loc = new Vector3f(300f, -118f, -700f);
		Vector3f position = new Vector3f(300f, -118f, -180f);
		//Quaternion rotation = new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD*0, Vector3f.UNIT_Y);		
		continuousZoomTo(loc, position, .5f);
	}

	/**
	 * Smoothly reposition, rotate, and zoom the camera to the atlas
	 * slide view.
	 */
	public void smoothlyZoomToSlideView() {
		Vector3f loc = new Vector3f(-300f, -118f, -180f);
		Vector3f position = new Vector3f(300f, -118f, -180f);
		//Quaternion rotation = new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD*90, Vector3f.UNIT_Y);
		continuousZoomTo(loc, position, 0.5f);
	}
	

	/**
	 * Smoothly reposition, rotate, and zoom the camera to the atlas
	 * lateral view.
	 */
	public void smoothlyZoomToAtlasLateralView() {
		Vector3f loc = new Vector3f(300f, -118f, 300f);
		Vector3f position = new Vector3f(300f, -118f, -180f);
		//Quaternion rotation = new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD*180, Vector3f.UNIT_Y);

		continuousZoomTo(loc, position, .5f);
	}
	
	public void smoothlyZoomToCellView() {

		Vector3f loc = new Vector3f(190f, -118f, -180f);
		Vector3f position = new Vector3f(250f, -118f, -180f);
		continuousZoomTo(loc,position,.21f);
		//Quaternion rotation = new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD*90, Vector3f.UNIT_Y);


	}
	
	public void smoothlyZoomToSubcellularView() {

		Vector3f loc = new Vector3f(278.8373f, -116.61807f, -179.73985f);
		Vector3f position = new Vector3f(298.8373f, -116.61807f, -179.73985f);
		//Quaternion rotation = new Quaternion(-0.05305708f,0.60644495f, 0.06914531f, 0.7903347f);

		continuousZoomTo(loc, position, 0.21f);
	}

	
	public void smoothlyZoomToSlideCerebellumView() {
		Vector3f loc = new Vector3f(458.9234f, -118.0f, -356.11566f);
		Vector3f position = new Vector3f(458.9234f, -118.0f, -218.11566f);
		continuousZoomTo(loc, position, 0.21f);
	}
	

	public float getZoom() {
		return invZoom;
	}	
}
