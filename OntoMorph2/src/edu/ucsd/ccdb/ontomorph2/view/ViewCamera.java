package edu.ucsd.ccdb.ontomorph2.view;

import java.util.logging.Level;

import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.renderer.Camera;

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
		
		Camera cam = View.getInstance().getRenderer().getCamera();
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
	public void moveForward() {
		//find the vector of the direction pointing towards
		Vector3f dir = this.getCamera().getDirection().normalize();
		this.setLocalTranslation( this.getLocalTranslation().add(dir));
	}

	/**
	 * Move the camera one unit backward
	 * @author caprea
	 */
	public void moveBackward() {
//		find the vector of the direction pointing towards
		Vector3f dir = this.getCamera().getDirection().normalize().negate();
		this.setLocalTranslation( this.getLocalTranslation().add(dir));
	}

	/**
	 * Turn the camera one unit clockwise
	 * @author caprea
	 */
	public void turnClockwise() {
//		key right
		Quaternion roll = new Quaternion();
		roll.fromAngleAxis( -camRotationRate*invZoom, Vector3f.UNIT_Y ); //rotates Rate degrees
		roll = this.getLocalRotation().multLocal(roll); // (q, save)
		this.setLocalRotation(roll);
	}

	/**
	 * Turn the camera one unit counter clockwise
	 * @author caprea
	 */
	public void turnCounterClockwise() {
//		left key
		Quaternion roll = new Quaternion();
		roll.fromAngleAxis( camRotationRate*invZoom, Vector3f.UNIT_Y ); //rotates Rate degrees
		roll = this.getLocalRotation().multLocal(roll); // (q, save)
		this.setLocalRotation(roll);
	}

	/**
	 * Turn the camera one unit downwards
	 * @author caprea
	 */
	public void turnDown() {
//		down
		Quaternion roll = new Quaternion();
		roll.fromAngleAxis( camRotationRate*invZoom, Vector3f.UNIT_X );//rotates Rate degrees
		roll = this.getLocalRotation().multLocal(roll); // (q, save)
		this.setLocalRotation(roll);
	}

	/**
	 * Turn the camera one unit upwards
	 * @author caprea
	 */
	public void turnUp() {
//		up
		Quaternion roll = new Quaternion();
		roll.fromAngleAxis( -camRotationRate*invZoom, Vector3f.UNIT_X ); //rotates Rate degrees
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
	public void zoomIn() {
		invZoom -= 0.01f;
		//float aspect = (float) display.getWidth() / (float) display.getHeight();
		this.getCamera().setFrustum(1.0f, 1000.0f, -0.55f * invZoom, 0.55f * invZoom, 0.4125f*invZoom, -0.4125f*invZoom);
		//cam.setFrustum( 0, 150, -invZoom * aspect, invZoom * aspect, -invZoom, invZoom );
		this.getCamera().update();
	}

	/**
	 * Zoom the camera out without changing its position
	 *
	 */
	public void zoomOut() {
		invZoom += 0.01f;
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
		System.out.println("Smoothly zooming to: " + position.toString() + ", rotating to: " 
				+ rotation.toString() + ", changing zoom to: " + zoom);
	}
	
	/**
	 * Smoothly reposition, rotate, and zoom the camera to the atlas
	 * medial view.
	 *
	 */
	public void smoothlyZoomToAtlasMedialView() {
		Vector3f loc = new Vector3f(300f, -118f, -700f);
		Quaternion rotation = new Quaternion().fromAngleAxis(FastMath.DEG_TO_RAD*0, Vector3f.UNIT_Y);
		continuousZoomTo(loc, rotation, invZoom);
	}

}
