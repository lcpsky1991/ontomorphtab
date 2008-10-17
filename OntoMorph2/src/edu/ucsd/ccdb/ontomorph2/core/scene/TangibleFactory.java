package edu.ucsd.ccdb.ontomorph2.core.scene;

import java.net.URI;
import java.net.URISyntaxException;

import com.jme.math.Quaternion;
import com.jme.math.Vector3f;

import edu.ucsd.ccdb.ontomorph2.core.spatial.PositionVector;
import edu.ucsd.ccdb.ontomorph2.core.spatial.RotationQuat;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Curve3D;
import edu.ucsd.ccdb.ontomorph2.core.tangible.CurveAnchorPoint;
import edu.ucsd.ccdb.ontomorph2.core.tangible.SphereParticles;
import edu.ucsd.ccdb.ontomorph2.core.tangible.Tangible;
import edu.ucsd.ccdb.ontomorph2.core.tangible.slide.Slide;
import edu.ucsd.ccdb.ontomorph2.observers.SceneObserver;
import edu.ucsd.ccdb.ontomorph2.util.Log;
import edu.ucsd.ccdb.ontomorph2.util.OMTVector;
import edu.ucsd.ccdb.ontomorph2.view.View;

/**
 * Responsible for encapuslating the methods for creating Cells, Slides, Curves, Particles, etc
 * @author caprea
 *
 */
public class TangibleFactory 
{
	private static TangibleFactory instance = null;
	
	public static TangibleFactory getInstance() 
	{
		if (instance == null) 
		{
			instance = new TangibleFactory();
		}
		return instance;
	}
	
	public Slide createSlide(String name, URI image)
	{
		Slide sCreated = null;
		String where = null;
		Quaternion dir  = null;
		{
			sCreated = new Slide("user-created slide", image);
			sCreated.setVisible(true);
			sCreated.setPosition(new PositionVector(inFront()));
			sCreated.setScale(1.0f);
			sCreated.setRatio(1.0f);
			dir = View.getInstance().getCameraView().getLocalRotation();
			
			
			sCreated.setRotation(new RotationQuat(dir));
			
		}
		View.getInstance().getScene().changed(Scene.CHANGED_SLIDE);
		return sCreated;
	}
	
	/**
	 * The world position vector that is right in front of the camera
	 * @return
	 */
	public OMTVector inFront()
	{
		Vector3f camat = null;
		Vector3f combined = null;
		Vector3f toward = null;
		
		camat = View.getInstance().getCameraView().getLocalTranslation();
		toward = View.getInstance().getCameraView().getCamera().getDirection().mult(30f);
		combined = camat.add(toward);
		
		return new OMTVector(combined);
	}
	
	
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
	
	/**
	 * Create particles at the given location
	 * 
	 * @param location
	 * @return
	 */
	public SphereParticles createParticles(Vector3f location)
	{
		System.out.println("create Particles");
		SphereParticles particles = new SphereParticles("Go Here Particles", location);
		particles.setColor(java.awt.Color.blue);
		particles.setVisible(true);
		particles.addObserver(SceneObserver.getInstance());
		
		return particles;

	}
	/**
	 * Create a new anchor point at a location relative to this point.
	 *
	 */
	public CurveAnchorPoint createPoint(Curve3D trgCurve, int index)
	{
	
		
		int i = index;				//where the new point will go
		OMTVector place = null;
		OMTVector posPrev = null;
		OMTVector posNext = null;
		
		float delta = 0.15f;
		float t = 0;					//the approximate time of the originating AnchorPoint
		
		if ( index > 0)
		{
			CurveAnchorPoint ptPrev = trgCurve.getAnchorPoints().get(index); //get the previous point
			t = ptPrev.aproxTime();		//get the time of source AnchorPoint
		}
		
		
		//find the tangent of the current index by getting the position of the prev and post
		posPrev = trgCurve.getPoint(t-delta);
		posNext = trgCurve.getPoint(t+delta);
			
		//the new location is the last point, plus the difference between 2 imaginary points around it
		//place = Point(t) + (Next - Prev)
		place = new OMTVector(trgCurve.getPoint(t).add( posNext.subtract(posPrev) ));
		

		CurveAnchorPoint capt =	trgCurve.addControlPoint(i, place);
		trgCurve.reapply(); //TODO: remove this line
		return capt;
	}
	private TangibleFactory()
	{
		
	}
}
