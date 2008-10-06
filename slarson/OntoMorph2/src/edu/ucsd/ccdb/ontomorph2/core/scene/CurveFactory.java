package edu.ucsd.ccdb.ontomorph2.core.scene;

import com.jme.math.Vector3f;

import edu.ucsd.ccdb.ontomorph2.core.tangible.Curve3D;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Tangible;
import edu.ucsd.ccdb.ontomorph2.observers.SceneObserver;
import edu.ucsd.ccdb.ontomorph2.util.OMTVector;
import edu.ucsd.ccdb.ontomorph2.view.View;

/**
 * Contains methods for special construction of Curves
 * 
 * @author Stephen D. Larson (slarson@ncmir.ucsd.edu)
 * @author caprea
 */
public class CurveFactory {

	private static CurveFactory instance = null;
	
	public static CurveFactory getInstance() {
		if (instance == null) {
			instance = new CurveFactory();
		}
		return instance;
	}
	
	private CurveFactory() {}
	
	
	/**
	 * This function intends to create a curve in the view based on some originating Tangible.
	 * If the originating Tangible has a CoordinateSystem then the curve is supposed to be created in parallel to that Coords
	 * If the originating Tangible has no CoordinateSystem then the curve will be parallel to the camera's plane and will also adop that plane to be its coordinatesystem
	 * Regarding movement for movement of the newly created curve it will inherit movement based on global, or on the CoordinateSystem
	 * 
	 * @param src
	 */
	public Curve3D createCurve(Tangible src)
	{
		//TODO: rewrite this!
		OMTVector cent = new OMTVector(src.getPosition());
		OMTVector up = null;
		OMTVector left = null;
		OMTVector posa = null;
		OMTVector posb = null;
		OMTVector towardcam = null;
		float offset = 5f;	//the ammount of offset the new points from the originator
		
		//System.out.println("src " + src.getAbsolutePosition() + src.getRelativePosition());
		//cent is the originating point, in some coordinate system
		//create two side points not in that coordinate system
		
		
		//if there is a coordinate system, apply this curve to that coordinate system
		//if no coordinate system make it aligned with the camera
		
		//find the coordinate system of the camera on which to draw the curve parallel to, to do this we need three vectors
		towardcam = new OMTVector(View.getInstance().getCameraView().getCamera().getDirection().normalize().negate().mult(5f)); 
		left = new OMTVector(View.getInstance().getCameraView().getCamera().getLeft()); //too keep units consistent multiply by -1 so positive is 'right' (a droite)
		up = new OMTVector(View.getInstance().getCameraView().getCamera().getUp());
		
		Vector3f combined = towardcam.normalize().add(left.normalize().add(up).normalize());
		//adopt the plane of the camera to be the coordinate system
		//TODO: apply coordinate system
		
		//make two side points that are +/-X and +/-Y
		posa = new OMTVector(left.add(up).mult(offset));
		posb = new OMTVector(left.mult(-offset).add(up.mult(offset)));
		
		//align the side points to be near the center point
		posa.addLocal(cent);	
		posb.addLocal(cent);
		
		//adjust the curve so it appears ever-slightly in front of the background objects
		cent.addLocal(towardcam); 
		posa.addLocal(towardcam);
		posb.addLocal(towardcam);

		//System.out.println("sys " + system);
		//System.out.println("new curve @ " + posa + cent + posb);
		
		OMTVector[] pts = {posb, cent, posa};
		Curve3D cap = new Curve3D("user-created curve", pts);	//FIXME: need to set demo coordinates on new curves
		cap.setColor(java.awt.Color.orange);
		cap.setVisible(true);
		cap.setModelBinormalWithUpVector(towardcam, 0.01f);	
		cap.addObserver(SceneObserver.getInstance());
		cap.changed();
		
		//redraw the scene, but not the whole scene, let observer know the curves have changed
		View.getInstance().getScene().changed(Scene.CHANGED_CURVE);
		
		return cap;
	}
}
