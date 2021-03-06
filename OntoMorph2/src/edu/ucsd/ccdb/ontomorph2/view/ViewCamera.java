package edu.ucsd.ccdb.ontomorph2.view;

import java.util.List;

import com.jme.bounding.BoundingSphere;
import com.jme.input.InputHandler;
import com.jme.input.MouseInput;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Ray;
import com.jme.math.Vector2f;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;
import com.jme.scene.Controller;
import com.jme.scene.Node;
import com.jme.scene.SceneElement;
import com.jme.scene.shape.Sphere;
import com.jme.util.Timer;

import edu.ucsd.ccdb.ontomorph2.core.tangible.Curve3D;
import edu.ucsd.ccdb.ontomorph2.core.tangible.CurveAnchorPoint;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Tangible;
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

	private static ViewCamera instance = null;

	float camRotationRate = FastMath.PI * 5 / 180;	//(FastMath.PI * X / 180) corresponds to X degrees per (FPS?) = Rate/UnitOfUpdate 
	float invZoom = 1.0f; //zoom amount
    protected Timer timer;
    
	float campos = 1.57f; //camera position between 0 and 2*pi
    float distance = 10f;
    float rotationspeed = .1f; 

	Camera cam;
	//CameraNode camNode;
	Node visRepresentation = new Node("camera avatar");
	Node rootNode = new Node("root Node");

	InputHandler input = new InputHandler();
	public ViewCamera() {
		init();	
	}
	
	public static ViewCamera getInstance() {
		if (instance == null) {
			instance = new ViewCamera();
		}
		return instance;
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
		
		//camNode.setLocalTranslation(loc);
		Log.warn("Rotation: " + this.getLocalRotation() + "\nTranslation: " + 
				this.getLocalTranslation());
		
		
		
		//make a sphere that repsents the camera
        Sphere s=new Sphere("camtar",10,10,2f); //last number is radius
        s.setModelBound(new BoundingSphere());
        s.updateModelBound();
        s.setRandomColors();
        s.setLocalTranslation(this.getLocalTranslation());
        visRepresentation.attachChild(s);
        //this.attachChild(visRepresentation);
		
		
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

	public void moveLeft(float amount)
	{
		Vector3f dir = this.getCamera().getLeft().normalize().mult(amount);
		this.setLocalTranslation(this.getLocalTranslation().add(dir));
	}
	
	public void moveRight(float amount)
	{
		Vector3f dir = this.getCamera().getLeft().normalize().negate().mult(amount);
		this.setLocalTranslation(this.getLocalTranslation().add(dir));
	}
	
	
	/**
	 * Rotates the camera (from user-perspective it rotates the world) around a Tangible
	 * @param degreesX
	 * @param degreesY
	 */
	public void rotateCameraAbout(Vector3f focus, float degreesX, float degreesY)
	{
		/**
		 * FIXME: the problem with this function is that it continually expands the radius on which it's rotating
		 * this is tough to fix because of the precision with quaternions rotating vectors and vector.getAngleBetween
		 * can not tell the difference between these tiny movements
		 */
		float factor = (FastMath.PI / 180);
		float theta = 0; //store the change in the rotation of the camera, caused by calling this function
		
		Vector3f posOrig = new Vector3f(this.getLocalTranslation().clone());
		
		//then apply the new rotation to the camera
		Quaternion rotX = new Quaternion();
		Quaternion rotY = new Quaternion();
		Quaternion q = new Quaternion();
		
		//
		rotX.fromAngleAxis(degreesX * factor, Vector3f.UNIT_Y); //up or Y
		rotY.fromAngleAxis(degreesY * factor, cam.getLeft()); //X or left
		
		q = new Quaternion(this.getLocalRotation());
		q = rotX.mult(q);
		q = rotY.mult(q);
		
		//apply the new rotation to the old position
		Vector3f posTo = new Vector3f(posOrig);
		posTo = posTo.add(cam.getLeft().mult(-degreesX * 1));
		posTo = posTo.add(cam.getUp().mult(degreesY));
		
		//apply the new position and rotation
		this.setLocalRotation(q);
		this.setLocalTranslation(posTo);
		
		//refocus the camera on its focus and align it on the radius
		if (focus != null) this.lookAt(focus, cam.getUp());
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
		//this.getCamera().update();
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
		//this.getCamera().update();
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
		//this.getCamera().update();
	}
	
	/**
	 * Smoothly reposition, rotation, and zoom the camera to the position
	 * rotation and zoom level specified by the parameters.
	 */
	public void continuousZoomTo(Vector3f location, Vector3f objectPosition, float zoom) {		
		
        Vector3f[] points = new Vector3f[2];
        points[0] = cam.getLocation();
        points[1] = location;

        CatmullRomCurve curve = new CatmullRomCurve("Curve", points);
        
        Vector3f up = new Vector3f(0,1,0);
        CurveOnceController cc = new CurveOnceController(curve, this, objectPosition);
        cc.setActive(false);
        this.addController(cc);
        cc.setRepeatType(Controller.RT_CLAMP);
        cc.setUpVector(up);
        cc.setSpeed(zoom);
        cc.setDisableAfterClamp(true);
        cc.setAutoRotation(true);
        curve.setCullMode(SceneElement.CULL_ALWAYS);
        curve.updateRenderState();
        this.attachChild(curve);
        cc.setActive(true);        	
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
		//BoundingSphere sphere = new BoundingSphere(20f, position);
	}
	
	public void setToSubcellularView() {
		Vector3f loc = new Vector3f(298.8373f, -116.61807f, -179.73985f);
		this.setLocalTranslation(loc);
		this.setLocalRotation(new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD*90, Vector3f.UNIT_Y));
	}
	
	public void smoothlyZoomToSlideCerebellumView() {
		Vector3f loc = new Vector3f(458.9234f, -118.0f, -356.11566f);
		Vector3f position = new Vector3f(458.9234f, -118.0f, -218.11566f);
		continuousZoomTo(loc, position, 0.21f);
	}
	
	/*
	 * Method called from Basic Search. Location of Query passed as parameter
	 */
	public void searchZoomTo(Vector3f location, Vector3f direction){
  
		Vector3f loc = location;
		Vector3f position = new Vector3f(location.x, location.y, location.z - 20.0f);
		System.out.println("location " + location + " position " + position);
		//cam.setDirection(location);
		continuousZoomTo(loc, location, .21f);
        System.out.println(" camera direction " + direction);
	}

	public float getZoom() {
		return invZoom;
	}
	
	public void TravelAlongCurve(Curve3D curvePath, Vector3f objectPosition){
		CatmullRomCurve curve = (CatmullRomCurve) curvePath.getCurve();
		 Vector3f up = new Vector3f(0,1,0);
	        CurveOnceController cc = new CurveOnceController(curve, this, objectPosition);
	        cc.setActive(false);
	        this.addController(cc);
	        cc.setRepeatType(Controller.RT_CLAMP);
	        cc.setUpVector(up);
	        cc.setSpeed(.21f);
	        cc.setDisableAfterClamp(true);
	        cc.setAutoRotation(true);
	        curve.setCullMode(SceneElement.CULL_ALWAYS);
	        curve.updateRenderState();
	        this.attachChild(curve);
	        cc.setActive(true);      
	}
}
