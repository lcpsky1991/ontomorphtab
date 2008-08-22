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
import com.jme.util.Timer;
import com.jme.util.geom.BufferUtils;
import com.jme.app.BaseSimpleGame;
import com.jme.bounding.BoundingSphere;
import com.jme.bounding.OrientedBoundingBox;

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
    protected Timer timer;
    private Vector3f currentDirection;
    
	float campos = 1.57f; //camera position between 0 and 2*pi
    float distance = 10f;
    float rotationspeed = .1f; 

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

        float distance = points[0].distance(points[1]);
        System.out.println("distance " + distance);
        Log.warn("current camera position" + points[0]);
        CatmullRomCurve curve = new CatmullRomCurve("Curve", points);
        
        Vector3f up = new Vector3f(0.0f,0.1f,0.0f).normalize();
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
        
        this.currentDirection = objectPosition;
	}
	
	/**
	 * Rotates camera left around object being look by the camera
	 */
	public void SphereLeftRotation(float amount){
	    
		/*System.out.println("camera location kleft " + cam.getLocation());
        Vector3f cpos = this.getCamera().getLocation();
        campos = campos + rotationspeed;
 
        if( campos > 6.28f ) campos = 0; //Here we check that the camera rotation is between 0 and 2*pi
        float cx = (float) (distance*Math.cos(campos)); //Calculating X coord
        float cy = (float) (distance*Math.sin(campos));
        cpos.setX(cx + currentDirection.getX()); //we add the spatial x,y components so the camera rotates around it.
        cpos.setY(cy + currentDirection.getY());
        //kc.setCampos(campos); we have to return the camera rotation position so it's shared between actions.
        this.setLocalTranslation(cpos);

        
        this.lookAt(currentDirection, Vector3f.UNIT_Z.clone());
        this.getCamera().update();*/
		BoundingSphere sphere = new BoundingSphere(10f, currentDirection);
        System.out.println("Bounding Sphere " + sphere.getRadius()+ " " + sphere.getCenter());
	}
	
	/**
	 * Right rotation of camera around object
	 */
	/*
	public void SphereRightRotation(float amount){ 
		/*System.out.println("camera location kright " + cam.getLocation());
        Vector3f cpos = this.getCamera().getLocation();
        campos = campos - rotationspeed;

        if( campos < 0f ) campos = 6.28f;
        float cx = (float) (distance*Math.cos(campos));
        float cy = (float) (distance*Math.sin(campos));
        cpos.setX(cx + currentDirection.getX());
        cpos.setY(cy + currentDirection.getY());
        //kc.setCampos(campos);
        this.getCamera().setLocation(cpos);
        
        this.lookAt(currentDirection, Vector3f.UNIT_Z.clone());
        this.getCamera().update();
	}*/
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

	
	public void smoothlyZoomToSlideCerebellumView() {
		Vector3f loc = new Vector3f(458.9234f, -118.0f, -356.11566f);
		Vector3f position = new Vector3f(458.9234f, -118.0f, -218.11566f);
		continuousZoomTo(loc, position, 0.21f);
	}
	

	public float getZoom() {
		return invZoom;
	}	
}
